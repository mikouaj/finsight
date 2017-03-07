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
import pl.surreal.finance.transaction.api.CardApi;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.db.CardDAO;

@Path("/cards")
@Api(value = "cards")
@Produces(MediaType.APPLICATION_JSON)
public class CardResource
{
	private CardDAO cardDAO;

	public CardResource(CardDAO cardDAO) {
		this.cardDAO = cardDAO;
	}
	
	private CardApi mapDomainToApi(Card card) {
		CardApi cardApi = new CardApi();
		cardApi.setId(card.getId());
		cardApi.setName(card.getName());
		cardApi.setNumber(card.getNumber());
		return cardApi;
	}
	
	private Card mapApiToDomain(CardApi cardApi, Card card) throws NotFoundException {
		if(card==null) {
			card = new Card();
		}
		card.setName(cardApi.getName());
		card.setNumber(cardApi.getNumber());
		return card;
	}
	
	@GET
	@UnitOfWork
	@Timed
	public List<CardApi> getCards() {
		List<CardApi> cardApis = new ArrayList<>();
		for(Card card : cardDAO.findAll()) {
			CardApi cardApi = mapDomainToApi(card);
			cardApis.add(cardApi);
		}
		return cardApis;
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public CardApi getCardOperation(@PathParam("id") LongParam cardId) {
		Card card = cardDAO.findById(cardId.get()).orElseThrow(() -> new NotFoundException("Not found."));
		CardApi cardApi = mapDomainToApi(card);
		return cardApi;
	}
	
    @POST
    @UnitOfWork
    public CardApi createCard(CardApi cardApi) {
    	Card cardToCreate = mapApiToDomain(cardApi,null);
    	Card card;
    	try {
    		card = cardDAO.create(cardToCreate);
    	} catch(ConstraintViolationException ex) {
    		throw new BadRequestException("Constraint violation on "+ex.getConstraintName());
    	}
    	cardApi.setId(card.getId());
        return cardApi;
    }
    
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public CardApi replace(@PathParam("id") LongParam cardId, CardApi cardApi) {
    	Card card = cardDAO.findById(cardId.get()).orElseThrow(() -> new NotFoundException("Card not found."));
    	mapApiToDomain(cardApi, card);
    	cardDAO.create(card);;
    	return cardApi;
    }
    
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") LongParam cardId) {
    	try {
    		cardDAO.deleteById(cardId.get());
    	} catch(ObjectNotFoundException ex) {
    		throw new NotFoundException("Not found.");
    	}
    	return Response.ok().build();
    }
}
