/* Copyright 2017 Mikolaj Stefaniak
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

package pl.surreal.finance.transaction.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionApi
{
	@JsonProperty
	private Long id;
	@JsonProperty
	private String type;
	@JsonProperty
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private Date date;
	@JsonProperty
	private BigDecimal amount;
	@JsonProperty
	private BigDecimal accountingAmount;
	@JsonProperty
	private String currency;
	@JsonProperty
	private String title;
	@JsonProperty("labels")
	private List<Long> labelIds;
	@JsonProperty
	private ITransactionDetailApi details;
	
	public TransactionApi() { }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
	
	public List<Long> getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(List<Long> labelIds) {
		this.labelIds = labelIds;
	}

	public ITransactionDetailApi getDetails() {
		return details;
	}

	public void setDetails(ITransactionDetailApi details) {
		this.details = details;
	}
}
