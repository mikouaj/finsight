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
	
	@GET
	@UnitOfWork
	@Timed
	public List<Card> getCards() {
		return cardDAO.findAll();
	}
	
	@GET
	@Path("/{cardId}")
	@UnitOfWork
	public Card getCardOperation(@PathParam("cardId") LongParam cardId) {
		Card card = cardDAO.findById(cardId.get()).orElseThrow(() -> new NotFoundException("Not found."));
		return card;
	}
	
    @POST
    @UnitOfWork
    public Card createCard(Card card) {
    	Card dbcard;
    	try {
    		dbcard = cardDAO.create(card);
    	} catch(ConstraintViolationException ex) {
    		throw new BadRequestException("Constraint violation on "+ex.getConstraintName());
    	}
        return dbcard;
    }
    
    @PUT
    @Path("/{cardId}")
    @UnitOfWork
    public Card replace(@PathParam("cardId") LongParam cardId, Card card) {
    	Card dbCard = cardDAO.findById(cardId.get()).orElseThrow(() -> new NotFoundException("Not found."));
    	dbCard.setName(card.getName());
    	dbCard.setNumber(card.getNumber());
    	return cardDAO.create(dbCard);
    }
    
    @DELETE
    @Path("/{cardId}")
    @UnitOfWork
    public Response delete(@PathParam("cardId") LongParam cardId) {
    	try {
    		cardDAO.deleteById(cardId.get());
    	} catch(ObjectNotFoundException ex) {
    		throw new NotFoundException("Not found.");
    	}
    	return Response.ok().build();
    }
}
