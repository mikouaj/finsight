/* Copyright 2016 Mikolaj Stefaniak
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package pl.surreal.finance.transaction.resources;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.db.LabelDAO;
import pl.surreal.finance.transaction.db.LabelRuleDAO;

@Path("/labelRules")
@Produces(MediaType.APPLICATION_JSON)
public class LabelRuleResource
{
	private LabelRuleDAO labelRuleDAO;
	private LabelDAO labelDAO;

	public LabelRuleResource(LabelRuleDAO labelRuleDAO,LabelDAO labelDAO) {
		this.labelRuleDAO = labelRuleDAO;
		this.labelDAO = labelDAO;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<LabelRule> get() {
		return labelRuleDAO.findAll();
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public LabelRule getById(@PathParam("id") LongParam id) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return labelRule;
	}
	
	@POST
	@UnitOfWork
	public LabelRule create(LabelRule labelRule) {
		return labelRuleDAO.create(labelRule);
	}
	
	@DELETE
	@Path("/{id}")
	@UnitOfWork
	public Response delete(@PathParam("id") LongParam id,
			@DefaultValue("false") @QueryParam("force") boolean forceDelete) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		if(labelRule.getLabels().size()>0) {
			if(!forceDelete) throw new NotAcceptableException("LabelRule has labels, delete them first or use force");
			labelRule.removeAllLabels();
		}
		labelRuleDAO.delete(labelRule);
		return Response.ok().build();
	}
	
	@GET
	@Path("/{id}/labels")
	@UnitOfWork
	public List<Label> getLabels(@PathParam("id") LongParam id) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return labelRule.getLabels();
	}
	
	@PUT
	@Path("/{id}/labels/{labelId}")
	@UnitOfWork
	public LabelRule addLabel(@PathParam("id") LongParam id,@PathParam("labelId") LongParam labelId) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("LabelRule not found."));
		Label label = labelDAO.findById(labelId.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		labelRule.addLabel(label);
		return labelRuleDAO.create(labelRule);
	}
	
	@DELETE
	@Path("/{id}/labels/{labelId}")
	@UnitOfWork
	public LabelRule removeLabel(@PathParam("id") LongParam id,@PathParam("labelId") LongParam labelId) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("LabelRule not found."));
		Label label = labelDAO.findById(labelId.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		labelRule.removeLabel(label);
		return labelRuleDAO.create(labelRule);
	}
}
