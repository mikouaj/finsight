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

package pl.surreal.finance.transaction.parser.csv;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.parser.ITransactionBuilder;

public class CommissionBuilder implements ITransactionBuilder 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommissionBuilder.class);
	private Commission commission;
	
	public CommissionBuilder() {
		commission = new Commission();
	}

	@Override
	public void setDate(String dateString) {
		try {
			commission.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable date '{}'",e.getMessage());
		}
	}

	@Override
	public void setAccountingDate(String dateString) {
		try {
			commission.setAccountingDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable accountingDate '{}'",e.getMessage());
		}
	}
	
	@Override
	public void setCurrency(String currencyString) {
		commission.setCurrency(currencyString);
	}

	@Override
	public void setAccountingCurrency(String currencyString) {
		commission.setAccountingCurrency(currencyString);
		commission.setCurrency(currencyString);
	}
	
	@Override
	public void setAmount(String amountString) {
		commission.setAmount(new BigDecimal(Double.parseDouble(amountString)));	
	}

	@Override
	public void setAccountingAmount(String amountString) {
		commission.setAccountingAmount(new BigDecimal(Double.parseDouble(amountString)));
		commission.setAmount(commission.getAccountingAmount());
	}
	
	@Override
	public void setBalanceAfter(String balanceAfterString) {
		commission.setBalanceAfter(new BigDecimal(Double.parseDouble(balanceAfterString)));
	}

	@Override
	public Transaction getTransaction(String details) {
		commission.setTitle(details);
		return commission;
	}
}
