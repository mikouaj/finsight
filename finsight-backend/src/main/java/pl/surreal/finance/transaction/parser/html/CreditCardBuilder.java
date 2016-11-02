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

package pl.surreal.finance.transaction.parser.html;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.CardDetails;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.parser.ITransactionBuilder;

public class CreditCardBuilder implements ITransactionBuilder
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardBuilder.class);
	private CardOperation cardOperation;
	private Card baseCard;
	
	public CreditCardBuilder() {
		cardOperation = new CardOperation();
	}

	public Card getBaseCard() {
		return baseCard;
	}

	public void setBaseCard(Card baseCard) {
		this.baseCard = baseCard;
	}

	@Override
	public void setDate(String dateString) {
		try {
			cardOperation.setDate(new SimpleDateFormat("dd.MM.yyyy").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable date '{}'",e.getMessage());
		}
	}

	@Override
	public void setAccountingDate(String dateString) {
		try {
			cardOperation.setAccountingDate(new SimpleDateFormat("dd.MM.yyyy").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable accountingDate '{}'",e.getMessage());
		}
	}

	@Override
	public void setCurrency(String currencyString) {
		cardOperation.setCurrency(currencyString);
	}

	@Override
	public void setAccountingCurrency(String currencyString) {
		cardOperation.setAccountingCurrency(currencyString);
	}

	@Override
	public void setAmount(String amountString) {
		cardOperation.setAmount(new BigDecimal(Double.parseDouble(amountString)));
		
	}
	
	@Override
	public void setAccountingAmount(String amountString) {
		cardOperation.setAccountingAmount(new BigDecimal(Double.parseDouble(amountString)));	
	}

	@Override
	public void setBalanceAfter(String balanceAfterString) {
		cardOperation.setBalanceAfter(new BigDecimal(Double.parseDouble(balanceAfterString)));
	}

	@Override
	public Transaction getTransaction(String details) {
		Pattern detailsPattern = Pattern.compile("^([^\\s]+)\\s(.+)$");
        Matcher detailsMatcher = detailsPattern.matcher(details);
        if(detailsMatcher.matches()) {
        	cardOperation.setTitle(detailsMatcher.group(1).trim());
        	cardOperation.setDestination(detailsMatcher.group(2).trim());
        	if(baseCard!=null) {
        		cardOperation.setCard(new CardDetails(baseCard.getNumber(),baseCard.getName()));
        	}
        } else {
        	LOGGER.warn("getTransaction : no match on main pattern, transaction will be missing details");
        }
		return cardOperation;
	}
}
