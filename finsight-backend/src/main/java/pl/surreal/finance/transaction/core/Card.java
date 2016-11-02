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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="card")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Card.findAll",
            query = "SELECT c FROM Card c"
    ),
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Card.findByNo",
            query = "SELECT c FROM Card c WHERE c.number = :number"
    )
})
public class Card
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
	@NotNull
	@NotEmpty
	private String number;
	
	@NotNull
	@NotEmpty
	private String name;

	public Card() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}