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

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.swagger.annotations.Api;
import pl.surreal.finance.transaction.api.ImportResult;
import pl.surreal.finance.transaction.api.ImportType;
import pl.surreal.finance.transaction.api.Transaction;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.db.TransactionDAO;
import pl.surreal.finance.transaction.labeler.ITransactionLabeler;
import pl.surreal.finance.transaction.parser.IParserFactory;
import pl.surreal.finance.transaction.parser.ITransactionParser;
import pl.surreal.finance.transaction.parser.ParserSupportedType;

@Path("/transactions")
@Api(value = "transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionResource.class);
	private TransactionDAO transactionDAO;
	private IParserFactory parserFactory;
	private ITransactionLabeler transactionLabeler;
	@Context
	private UriInfo uriInfo;
	
	public TransactionResource(TransactionDAO transactionDAO,IParserFactory parserFactory,ITransactionLabeler transactionLabeler) {
		this.transactionDAO = transactionDAO;
		this.parserFactory = parserFactory;
		this.transactionLabeler = transactionLabeler;
	}
	
	@GET
	@UnitOfWork
	@Timed
	public List<Transaction> getTransactions(
			@QueryParam("first") @Min(0) Integer first,
			@QueryParam("max") @Min(0) Integer max)
	{
		LOGGER.debug("Query params value '{}' '{}'",first,max);
	
		ArrayList<Transaction> transactions = new ArrayList<>();
	
		for(pl.surreal.finance.transaction.core.Transaction coreTransaction : transactionDAO.findAll(first,max)) {
			Transaction transaction = new Transaction();
			transaction.setDate(coreTransaction.getAccountingDate());
			transaction.setAmount(coreTransaction.getAccountingAmount());
			transaction.setTitle(coreTransaction.getTitle());
			transaction.setType(coreTransaction.getClass().getSimpleName());
			transaction.setLabels(coreTransaction.getLabels());
			UriBuilder uriBuilder=uriInfo.getAbsolutePathBuilder();
			if(coreTransaction instanceof Commission) {
				uriBuilder = uriInfo.getBaseUriBuilder().path(CommissionResource.class);
			} else if(coreTransaction instanceof CardOperation) {
				uriBuilder = uriInfo.getBaseUriBuilder().path(CardOperationResource.class);
			} else if(coreTransaction instanceof Transfer) {
				uriBuilder = uriInfo.getBaseUriBuilder().path(TransferResource.class);
			}
			transaction.setUrl(uriBuilder.path("/{id}").resolveTemplate("id",coreTransaction.getId()).build());
			transactions.add(transaction);
		}
		return transactions;
	}
	
	@POST
	@Path("/import")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@UnitOfWork(transactional=false)
	@Timed
	public Response importTransactions(
			@NotNull @FormDataParam("file") InputStream uploadedInputStream,
			@NotNull @FormDataParam("file") FormDataContentDisposition fileDetail,
			@NotNull @FormDataParam("type") String fileType,
			@NotNull @FormDataParam("baseResourceId") String baseResourceId)
	{
		ITransactionParser parser = parserFactory.getParser(uploadedInputStream,fileType,baseResourceId).orElseThrow(()->new InternalServerErrorException("Can't configure parser for a given file type"));
		ImportResult result = importTransactions(parser);
		result.setFileName(fileDetail.getFileName());
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("/import")
	public List<ImportType> getImportTypes() {
		ArrayList<ImportType> importTypes = new ArrayList<>();
		for(ParserSupportedType type : parserFactory.getSupportedTypes()) {
			try {
				Class<?> resourceClass = Class.forName("pl.surreal.finance.transaction.resources."+type.getBaseResourceClass().getSimpleName()+"Resource");
				URI uri = uriInfo.getBaseUriBuilder().path(resourceClass).build();
				importTypes.add(new ImportType(type.getId(),type.getDescription(),uri));
			} catch(Exception e) {
				LOGGER.warn("getImportCapabilities : can't add supported type due to '{}'",e.toString());
				e.printStackTrace();
			}
		}
		return importTypes;
	}
	
	@POST
	@Path("/doLabelAll")
	@UnitOfWork
	public Response labelAll() {
		for(pl.surreal.finance.transaction.core.Transaction t: transactionDAO.findAll()) {
			transactionLabeler.label(t);
			transactionDAO.create(t);
		}
		return Response.status(200).build();
	}
	
	@POST
	@Path("/doLabelAll/{ruleId}")
	@UnitOfWork
	public Response labelAll(@PathParam("ruleId") LongParam ruleId) {
		for(pl.surreal.finance.transaction.core.Transaction t: transactionDAO.findAll()) {
			try {
				transactionLabeler.label(t,ruleId.get());
				transactionDAO.create(t);
			} catch(NoSuchElementException ex) {
				throw new NotFoundException(ex.getMessage());
			}
		}
		return Response.status(200).build();
	}
	
	private ImportResult importTransactions(ITransactionParser parser) {
		ImportResult result = new ImportResult();
		while(parser.hasNext()) {
			Optional<pl.surreal.finance.transaction.core.Transaction> optional = parser.getNext();
			result.incProcessed();
			if(optional.isPresent()) {
				try {
					transactionDAO.create(optional.get());
					result.incImported();
				} catch(ConstraintViolationException ex) {
					LOGGER.warn("importCSV: constraint violation '{}'",ex.getMessage());
					result.incContraintViolations();
					continue;
				}
			} else {
				result.incNulls();
			}
		}
		parser.close();
		return result;
	}
}