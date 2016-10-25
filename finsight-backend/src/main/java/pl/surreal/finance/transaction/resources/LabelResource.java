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

@Path("/label")
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
	
	@POST
	@UnitOfWork
	public Label create(Label label) {
		return labelDAO.create(label);
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public Label get(@PathParam("id") LongParam id) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return label;
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
	
	@POST
	@Path("/{id}/children")
	@UnitOfWork
	public Label addChild(@PathParam("id") LongParam id,Label child) {
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Parent not found."));
		label.addChild(child);
		labelDAO.create(label);
		return child;
	}
	
	@GET
	@UnitOfWork
	@Path("/testCreate")
	public Label testCreate() {
		Label l1 = new Label("MasterLabel");
		l1.addChild("ChildLabel1");
		l1.addChild("ChildLabel2");
		return labelDAO.create(l1);
	}
}
