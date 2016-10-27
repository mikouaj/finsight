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
import pl.surreal.finance.transaction.db.LabelDAO;

@Path("/labels")
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
	public List<Label> get() {
		return labelDAO.findAll();
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public Label getById(@PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return label;
	}
	
	@POST
	@UnitOfWork
	public Label create(Label label) {
		return labelDAO.create(label);
	}

	@DELETE
	@Path("/{id}")
	@UnitOfWork
	public Response delete(@PathParam("id") LongParam id,
			@DefaultValue("false") @QueryParam("force") boolean forceDelete) {
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
	public List<Label> getChildren(@PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Parent not found."));
		return label.getChildren();
	}
}
