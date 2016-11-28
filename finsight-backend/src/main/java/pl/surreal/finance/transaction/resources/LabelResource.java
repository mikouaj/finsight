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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

	@GET
	@UnitOfWork
	@Timed
	@ApiOperation(value = "Get all labels")
	public List<Label> get() {
		return labelDAO.findAll();
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	@ApiOperation(value = "Get label by id")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Label not found.") })
	public Label getById(@ApiParam(value = "id of the label", required = true) @PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		return label;
	}
	
	@POST
	@UnitOfWork
	@ApiOperation(value = "Create new label")
	public Label create(@ApiParam(value = "new label object", required = true) Label label) {
		return labelDAO.create(label);
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

	@GET
	@Path("/{id}/children")
	@UnitOfWork
	@ApiOperation(value = "Get list of children for a given parent label")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Parent label not found.") })
	public List<Label> getChildren(@ApiParam(value = "id of the parent label", required = true) @PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Parent not found."));
		return label.getChildren();
	}
	
	@POST
	@Path("/{id}/children")
	@UnitOfWork
	@ApiOperation(value = "Create new child label for a given parent label")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Parent label not found.") } )
	public Label createChild(@ApiParam(value = "id of the parent label", required = true) @PathParam("id") LongParam id,
			@ApiParam(value = "new label object", required = true) Label childLabel) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Parent not found."));
		label.addChild(childLabel);
		labelDAO.create(label);
		return childLabel;
	}
	
	@PUT
	@Path("/{id}/children/{childId}")
	@UnitOfWork
	@ApiOperation(value = "Assign specified label as a child for a specified parent label")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Child label was assigned successfully"),
			@ApiResponse(code = 404, message = "Parent / child label not found.") } )
	public Response addChild(@ApiParam(value = "id of the parent label") @PathParam("id") LongParam id,
			@ApiParam(value = "id of the child label") @PathParam("childId") LongParam childId) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		Label childLabel = labelDAO.findById(childId.get()).orElseThrow(() -> new NotFoundException("Child label not found."));
		label.addChild(childLabel);
		labelDAO.create(label);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}/children/{childId}")
	@UnitOfWork
	@ApiOperation(value = "Unassign specified label from a child for a specified parent label")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Child label was unassigned successfully"),
			@ApiResponse(code = 404, message = "Parent / child label not found.") } )
	public Response removeChild(@ApiParam(value = "id of the parent label")  @PathParam("id") LongParam id,
			@ApiParam(value = "id of the child label") @PathParam("childId") LongParam childId) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label not found."));
		Label childLabel = labelDAO.findById(childId.get()).orElseThrow(() -> new NotFoundException("Child label not found."));
		label.removeChild(childLabel);
		labelDAO.create(label);
		return Response.ok().build();
	}
}
