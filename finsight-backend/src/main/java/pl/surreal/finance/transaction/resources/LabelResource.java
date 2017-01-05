/* Copyright 2017 Mikolaj Stefaniak
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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import pl.surreal.finance.transaction.api.LabelApi;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.db.LabelDAO;

@Path("/labels")
@Api(value = "labels")
@Produces(MediaType.APPLICATION_JSON)
public class LabelResource
{
//	private static final Logger LOGGER = LoggerFactory.getLogger(LabelResource.class);
	private LabelDAO labelDAO;
	
	public LabelResource(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	private LabelApi mapDomainToApi(Label label) {
		LabelApi labelApi = new LabelApi();
		labelApi.setId(label.getId());
		labelApi.setText(label.getText());
		labelApi.setPath(label.getPath());
		if(label.getParent()!=null) {
			labelApi.setParentId(label.getParent().getId());
		}
		List<Long> childrenIds = new ArrayList<>();
		for(Label childLabel : label.getChildren()) {
			childrenIds.add(childLabel.getId());
		}
		labelApi.setChildrenIds(childrenIds);
		return labelApi;
	}
	
	private Label mapApiToDomain(LabelApi labelApi,Label label) throws NotFoundException {
		if(label==null) {
			label = new Label();
		}
		label.setText(labelApi.getText());
		if(labelApi.getParentId()!=null) {
		  Label parentLabel = labelDAO.findById(labelApi.getParentId()).orElseThrow(() -> new NotFoundException("Parent label not found."));
		  label.setParent(parentLabel);
		} else {
			label.setParent(null);
		}
		List<Label> children = new ArrayList<>();
		for(Long childId : labelApi.getChildrenIds()) {
			Label child = labelDAO.findById(childId).orElseThrow(() -> new NotFoundException("Label "+childId+" not found."));
			children.add(child);
		}
		label.setChildren(children);
		return label;
	}

	@GET
	@UnitOfWork
	@Timed
	@ApiOperation(value = "Get all labels")
	public List<LabelApi> get() {
		List<LabelApi> apiLabels = new ArrayList<>();
		for(Label label : labelDAO.findAll()) {
			LabelApi labelApi = mapDomainToApi(label);
			apiLabels.add(labelApi);
		}
		return apiLabels;
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	@ApiOperation(value = "Get label by id")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Label not found.") })
	public LabelApi getById(@ApiParam(value = "id of the label", required = true) @PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		LabelApi labelApi = mapDomainToApi(label);
		return labelApi;
	}
	
	@PUT
    @Path("/{id}")
    @UnitOfWork
    public LabelApi replace(@PathParam("id") LongParam id, LabelApi labelApi) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		mapApiToDomain(labelApi, label);
		labelDAO.create(label);
		return labelApi;
	}
	
	@POST
	@UnitOfWork
	@ApiOperation(value = "Create new label")
	public LabelApi create(@ApiParam(value = "new label object", required = true) LabelApi labelApi) {
		Label labelToCreate = mapApiToDomain(labelApi,null);
		Label label = labelDAO.create(labelToCreate);
		labelApi.setId(label.getId());
		return labelApi;
	}

	@DELETE
	@Path("/{id}")
	@UnitOfWork
	@ApiOperation(value = "Remove label by id")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Label not found."), @ApiResponse(code = 406, message = "Label has children, delete them first or use force.")  } )
	public Response delete(@ApiParam(value = "id of the label", required = true) @PathParam("id") LongParam id,
			@ApiParam(value = "boolean flag indicating if force removal should take place", required = false) @DefaultValue("false") @QueryParam("force") boolean forceDelete) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		if(label.getChildren().size()>0) {
			if(!forceDelete) throw new NotAcceptableException("Label has children, delete them first or use force");
			label.removeAllChildren();
		}

		if(label.getParent()!=null) {
			label.getParent().removeChild(label);
		}
		labelDAO.delete(label);
		return Response.ok().build();
	}
}
