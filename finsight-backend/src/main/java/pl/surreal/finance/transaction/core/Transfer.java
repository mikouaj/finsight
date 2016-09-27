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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="transfer")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Transfer.findAll",
            query = "SELECT t FROM Transfer t"
    )
})
public class Transfer extends Transaction
{
	public enum TransferDirection { OUTGOING, INCOMING, UNKNOWN };
	
	@Column(name = "internal", nullable = false)	
	private boolean internal;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "direction", nullable = false)	
	private TransferDirection direction;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Embedded
	@AttributeOverrides({
		  @AttributeOverride(
		    name="number",
		    column = @Column( name = "srcAccountNumber" )
		  ),			
		  @AttributeOverride(
			name="name",
			column = @Column( name = "srcAccountName" )		  
		  )
	})
	private AccountDetails srcAccount;
	
	@Embedded
	@AttributeOverrides({
		  @AttributeOverride(
		    name="number",
		    column = @Column( name = "dstAccountNumber" )
		  ),			
		  @AttributeOverride(
			name="name",
			column = @Column( name = "dstAccountName" )		  
		  )
	})	
	private AccountDetails dstAccount;
	
	public Transfer() {
		super();
		internal = false;
		direction = TransferDirection.UNKNOWN;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public TransferDirection getDirection() {
		return direction;
	}

	public void setDirection(TransferDirection direction) {
		this.direction = direction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountDetails getSrcAccount() {
		return srcAccount;
	}

	public void setSrcAccount(AccountDetails srcAccount) {
		this.srcAccount = srcAccount;
	}

	public AccountDetails getDstAccount() {
		return dstAccount;
	}

	public void setDstAccount(AccountDetails dstAccount) {
		this.dstAccount = dstAccount;
	}
}