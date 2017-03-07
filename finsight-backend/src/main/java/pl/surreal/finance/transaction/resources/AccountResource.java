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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.exception.ConstraintViolationException;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import pl.surreal.finance.transaction.api.AccountApi;
import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.db.AccountDAO;

@Path("/accounts")
@Api(value = "accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource
{
	private AccountDAO accountDAO;

	public AccountResource(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}
	
	private AccountApi mapDomainToApi(Account account) {
		AccountApi accountApi = new AccountApi();
		accountApi.setId(account.getId());
		accountApi.setName(account.getName());
		accountApi.setNumber(account.getNumber());
		return accountApi;
	}
	
	private Account mapApiToDomain(AccountApi accountApi,Account account) {
		if(account==null) {
			account = new Account();
		}
		account.setName(accountApi.getName());
		account.setNumber(accountApi.getNumber());
		return account;
	}	
	
	@GET
	@UnitOfWork
	@Timed
	@ApiOperation(value = "Get all accounts")
	public List<AccountApi> getAccounts() {
		List<AccountApi> accountApis = new ArrayList<>();
		for(Account account : accountDAO.findAll()) {
			AccountApi accountApi = mapDomainToApi(account);
			accountApis.add(accountApi);
		}
		return accountApis;
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	@ApiOperation(value = "Get account by id")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Account not found.") })
	public AccountApi getAccount(@ApiParam(value = "id of the account", required = true) @PathParam("id") LongParam accountId) {
		Account account = accountDAO.findById(accountId.get()).orElseThrow(() -> new NotFoundException("Account not found."));
		AccountApi accountApi = mapDomainToApi(account);
		return accountApi;
	}
	
    @POST
    @UnitOfWork
    @ApiOperation(value = "Create new account")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Contraint violation") })
    public AccountApi createAccount(@ApiParam(value = "new account object", required = true) AccountApi accountApi) {
    	Account accountToCreate = mapApiToDomain(accountApi, null);
    	Account account;
    	try {
    		account = accountDAO.create(accountToCreate);
    	} catch(ConstraintViolationException ex) {
    		throw new BadRequestException("Constraint violation on "+ex.getConstraintName());
    	}
    	accountApi.setId(account.getId());
        return accountApi;
    }
    
    @PUT
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Update account by id")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Account not found.") })
    public AccountApi replace(@ApiParam(value = "id of the account", required = true) @PathParam("id") LongParam accountId,
    					   @ApiParam(value = "updated account object", required = true) AccountApi accountApi) {
    	Account account = accountDAO.findById(accountId.get()).orElseThrow(() -> new NotFoundException("Account not found."));
    	mapApiToDomain(accountApi, account);
    	accountDAO.create(account);
    	return accountApi;
    }
    
    @DELETE
    @Path("/{id}")
    @UnitOfWork
	@ApiOperation(value = "Remove account by id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Account removed successfully."),
							@ApiResponse(code = 404, message = "Account not found.")} )
    public Response delete(@ApiParam(value = "id of the account", required = true) @PathParam("id") LongParam accountId) {
    	try {
    		accountDAO.deleteById(accountId.get());
    	} catch(ObjectNotFoundException ex) {
    		throw new NotFoundException("Account not found.");
    	}
    	return Response.ok().build();
    }
}
