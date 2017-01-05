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

import java.util.ArrayList;
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
import io.swagger.annotations.Api;
import pl.surreal.finance.transaction.api.LabelRuleApi;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.db.LabelDAO;
import pl.surreal.finance.transaction.db.LabelRuleDAO;

@Path("/labelRules")
@Api(value = "labelRules")
@Produces(MediaType.APPLICATION_JSON)
public class LabelRuleResource
{
	private LabelRuleDAO labelRuleDAO;
	private LabelDAO labelDAO;

	public LabelRuleResource(LabelRuleDAO labelRuleDAO,LabelDAO labelDAO) {
		this.labelRuleDAO = labelRuleDAO;
		this.labelDAO = labelDAO;
	}
	
	private LabelRuleApi mapDomainToApi(LabelRule labelRule) {
		LabelRuleApi labelRuleApi = new LabelRuleApi();
		labelRuleApi.setId(labelRule.getId());
		labelRuleApi.setRegexp(labelRule.getRegexp());
		labelRuleApi.setActive(labelRule.isActive());
		List<Long> labelIds = new ArrayList<>();
		for(Label label : labelRule.getLabels()) {
			labelIds.add(label.getId());
		}
		labelRuleApi.setLabelIDs(labelIds);
		return labelRuleApi;
	}
	
	private LabelRule mapApiToDomain(LabelRuleApi labelRuleApi,LabelRule labelRule) throws NotFoundException {
		if(labelRule==null) {
			labelRule = new LabelRule();
		}
		//omitting id overwrite from api level labelRule.setId(labelRuleApi.getId());
		labelRule.setRegexp(labelRuleApi.getRegexp());
		labelRule.setActive(labelRuleApi.isActive());
		List<Label> labels = new ArrayList<>();
		for(Long labelId : labelRuleApi.getLabelIDs()) {
			Label label = labelDAO.findById(labelId).orElseThrow(() -> new NotFoundException("Label "+labelId+" not found."));
			labels.add(label);
		}
		labelRule.setLabels(labels);
		return labelRule;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<LabelRuleApi> get() {
		List<LabelRuleApi> apiLabelRules = new ArrayList<>();
		for(LabelRule labelRule : labelRuleDAO.findAll()) {
			LabelRuleApi labelRuleApi = mapDomainToApi(labelRule);
			apiLabelRules.add(labelRuleApi);
		}
		return apiLabelRules;
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public LabelRuleApi getById(@PathParam("id") LongParam id) {
		LabelRule labelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		LabelRuleApi labelRuleApi = mapDomainToApi(labelRule);
		return labelRuleApi;
	}
	
	@POST
	@UnitOfWork
	public LabelRuleApi create(LabelRuleApi labelRuleApi) {
		LabelRule labelRuleToCreate = mapApiToDomain(labelRuleApi,null);
		LabelRule labelRule = labelRuleDAO.create(labelRuleToCreate);
		labelRuleApi.setId(labelRule.getId());
		return labelRuleApi;
	}
	
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public LabelRuleApi replace(@PathParam("id") LongParam id, LabelRuleApi labelRuleApi) {
    	LabelRule dbLabelRule = labelRuleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
    	mapApiToDomain(labelRuleApi, dbLabelRule);
    	labelRuleDAO.create(dbLabelRule);
    	return labelRuleApi;
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
}
