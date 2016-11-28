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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.swagger.annotations.Api;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.db.CommissionDAO;

@Path("/commissions")
@Api(value = "commissions")
@Produces(MediaType.APPLICATION_JSON)
public class CommissionResource
{
	public CommissionDAO commissionDAO;
	
	public CommissionResource(CommissionDAO commissionDAO) {
		this.commissionDAO = commissionDAO;
	}
	
	@GET
	@UnitOfWork
	@Timed
	public List<Commission> getCommissions() {
		return commissionDAO.findAll();
	}
	
	@GET
	@Path("/{commissionId}")
	@UnitOfWork
	public Commission getCommission(@PathParam("commissionId") LongParam commissionId) {
		Commission commission = commissionDAO.findById(commissionId.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return commission;
	}
}
