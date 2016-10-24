package pl.surreal.finance.transaction.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.db.LabelDAO;

@Path("/label")
@Produces(MediaType.APPLICATION_JSON)
public class LabelResource
{
	private LabelDAO labelDAO;
	
	public LabelResource(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<Label> getLabels() {
		return labelDAO.findAll();
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
