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

package pl.surreal.finance.transaction.core;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="cardoperation")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.CardOperation.findAll",
            query = "SELECT op FROM CardOperation op"
    )
})
public class CardOperation extends Transaction
{	
	@Embedded
	@AttributeOverrides({
		  @AttributeOverride(
		    name="number",
		    column = @Column( name = "cardNumber" )
		  ),			
		  @AttributeOverride(
			name="name",
			column = @Column( name = "cardName" )		  
		  )
	})
	private CardDetails card;
	
	@Column(name = "destination", nullable = false)
	private String destination;

	public CardOperation() {
		super();
	}

	public CardDetails getCard() {
		return card;
	}

	public void setCard(CardDetails card) {
		this.card = card;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
