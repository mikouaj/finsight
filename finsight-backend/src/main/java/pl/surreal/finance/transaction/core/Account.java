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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="account")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Account.findAll",
            query = "SELECT a FROM Account a"
    ),
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Account.findByNo",
            query = "SELECT a FROM Account a WHERE a.details.number = :number"
    )
})
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
	@Embedded
	@NotNull
	private AccountDetails details;

	public Account() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AccountDetails getDetails() {
		return details;
	}

	public void setDetails(AccountDetails details) {
		this.details = details;
	}
}
