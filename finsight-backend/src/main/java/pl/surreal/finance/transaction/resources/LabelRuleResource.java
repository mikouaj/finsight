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

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.db.LabelRuleDAO;

@Path("/labelRule")
@Produces(MediaType.APPLICATION_JSON)
public class LabelRuleResource
{
	private LabelRuleDAO labelRuleDAO;

	public LabelRuleResource(LabelRuleDAO labelRuleDAO) {
		this.labelRuleDAO = labelRuleDAO;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<LabelRule> get() {
		return labelRuleDAO.findAll();
	}
	
	@POST
	@UnitOfWork
	public LabelRule create(LabelRule labelRule) {
		return labelRuleDAO.create(labelRule);
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public LabelRule get(@PathParam("id") LongParam id) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return labelRule;
	}
}
