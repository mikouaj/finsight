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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="transaction")
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Transaction.findAll",
            query = "SELECT t FROM Transaction t ORDER BY t.accountingDate DESC"
    )
})
public abstract class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
    @Column(name = "date", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date date;
    
    @Column(name = "accountingDate", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NaturalId
    private Date accountingDate;
    
    @Column(name = "currency", nullable = false)
    private String currency;
    
    @Column(name = "accountingCurrency", nullable = false)
    private String accountingCurrency;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "accountingAmount", nullable = false)
    @NaturalId
    private BigDecimal accountingAmount;
   
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "balanceAfter", nullable = false)
    @NaturalId
    private BigDecimal balanceAfter;
    
    public Transaction() {
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountingCurrency() {
		return accountingCurrency;
	}

	public void setAccountingCurrency(String accountingCurrency) {
		this.accountingCurrency = accountingCurrency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAccountingAmount() {
		return accountingAmount;
	}

	public void setAccountingAmount(BigDecimal accountingAmount) {
		this.accountingAmount = accountingAmount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}
}
