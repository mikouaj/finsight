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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
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
import pl.surreal.finance.transaction.api.LabelResultApi;
import pl.surreal.finance.transaction.api.TransactionApi;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.db.LabelDAO;
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
	private LabelDAO labelDAO;
	private IParserFactory parserFactory;
	private ITransactionLabeler transactionLabeler;
	@Context
	private UriInfo uriInfo;
	
	public TransactionResource(TransactionDAO transactionDAO,LabelDAO labelDAO,IParserFactory parserFactory,ITransactionLabeler transactionLabeler) {
		this.transactionDAO = transactionDAO;
		this.parserFactory = parserFactory;
		this.transactionLabeler = transactionLabeler;
		this.labelDAO = labelDAO;
	}
	
	private TransactionApi mapDomainToApi(Transaction transaction) {
		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setId(transaction.getId());
		transactionApi.setDate(transaction.getAccountingDate());
		transactionApi.setAmount(transaction.getAccountingAmount());
		transactionApi.setTitle(transaction.getTitle());
		transactionApi.setType(transaction.getClass().getSimpleName());
		List<Long> labelIds = new ArrayList<>();
		for(Label label : transaction.getLabels()) {
			labelIds.add(label.getId());
		}
		transactionApi.setLabelIds(labelIds);
		
		UriBuilder uriBuilder=uriInfo.getAbsolutePathBuilder();
		if(transaction instanceof Commission) {
			uriBuilder = uriInfo.getBaseUriBuilder().path(CommissionResource.class);
		} else if(transaction instanceof CardOperation) {
			uriBuilder = uriInfo.getBaseUriBuilder().path(CardOperationResource.class);
		} else if(transaction instanceof Transfer) {
			uriBuilder = uriInfo.getBaseUriBuilder().path(TransferResource.class);
		}
		transactionApi.setUrl(uriBuilder.path("/{id}").resolveTemplate("id",transaction.getId()).build());
		return transactionApi;
	}
	
	private Transaction mapApiToDomain(TransactionApi transactionApi,Transaction transaction) throws NotFoundException {
		Objects.requireNonNull(transaction);
		List<Label> labels = new ArrayList<>();
		for(Long labelId : transactionApi.getLabelIds()) {
			Label label = labelDAO.findById(labelId).orElseThrow(() -> new NotFoundException("Label "+labelId+" not found."));
			labels.add(label);
		}
		transaction.setLabels(labels);
		return transaction;
	}
	
	@GET
	@UnitOfWork
	@Timed
	public List<TransactionApi> get(
			@QueryParam("first") @Min(0) Integer first,
			@QueryParam("max") @Min(0) Integer max)
	{
		LOGGER.debug("Query params value '{}' '{}'",first,max);
	
		ArrayList<TransactionApi> apiTransactions = new ArrayList<>();
		for(Transaction transaction : transactionDAO.findAll(first,max)) {
			TransactionApi transactionApi = mapDomainToApi(transaction);
			apiTransactions.add(transactionApi);
		}
		return apiTransactions;
	}
	
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public TransactionApi replace(@PathParam("id") LongParam id, TransactionApi transactionApi) {
    	Transaction transaction = transactionDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Transaction not found."));
    	mapApiToDomain(transactionApi, transaction);
    	transactionDAO.create(transaction);
    	return transactionApi;
    }
    
	@GET
	@Path("/label/{id}")
	@Timed
	@UnitOfWork
	public List<TransactionApi> getByLabel(@PathParam("id") LongParam id,
			@QueryParam("dateFrom") @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}") String dateFromString,
			@QueryParam("dateTo") @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}") String dateToString)
	{
		LOGGER.debug("Query params value '{}' '{}'",dateFromString,dateToString);
		Label label = labelDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Label "+id.get()+" not found."));
		
		Date dateFrom=null;
		Date dateTo=null;
		try {
			dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromString);
			dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToString);
		} catch(NullPointerException e) {
			LOGGER.debug("getByLabel : null pointer on parsing dates");
		} catch(ParseException e) {
			throw new BadRequestException("Unparsable date");
		}
	
		LOGGER.debug("getByLabel dateFrom '{}', dateTo '{}'",dateFrom,dateTo);
		ArrayList<TransactionApi> apiTransactions = new ArrayList<>();
		for(Transaction transaction : transactionDAO.findByLabel(label,dateFrom,dateTo)) {
			LOGGER.debug("getByLabel transaction id '{}' date '{}'",transaction.getId(),transaction.getDate());
			TransactionApi transactionApi = mapDomainToApi(transaction);
			apiTransactions.add(transactionApi);
		}
		return apiTransactions;
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
				ImportType importType = new ImportType(type.getId(),type.getDescription(),Link.fromUri(uri).rel("describedby").type(type.getBaseResourceClass().getSimpleName()).build());
				importTypes.add(importType);
			} catch(Exception e) {
				LOGGER.warn("getImportCapabilities : can't add supported type due to '{}'",e.toString());
				e.printStackTrace();
			}
		}
		return importTypes;
	}
	
	@POST
	@Path("/runAllRules")
	@UnitOfWork
	public LabelResultApi runAllRules() {
		int transactionCount=0;
		int labelsCount=0;
		for(pl.surreal.finance.transaction.core.Transaction t: transactionDAO.findAll()) {
			int appliedCount = transactionLabeler.label(t);
			if(appliedCount>0) {
				transactionCount++;
				labelsCount+=appliedCount;
				transactionDAO.create(t);
			}
		}
		return new LabelResultApi(transactionCount,labelsCount);
	}
	
	@POST
	@Path("/runRule/{id}")
	@UnitOfWork
	public LabelResultApi runRule(@PathParam("id") LongParam id) {
		int transactionCount=0;
		int labelsCount=0;
		for(pl.surreal.finance.transaction.core.Transaction t: transactionDAO.findAll()) {
			try {
				int appliedCount = transactionLabeler.label(t,id.get());
				if(appliedCount>0) {
					transactionCount++;
					labelsCount+=appliedCount;
					transactionDAO.create(t);
				}
			} catch(NoSuchElementException ex) {
				throw new NotFoundException(ex.getMessage());
			}
		}
		return new LabelResultApi(transactionCount,labelsCount);
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