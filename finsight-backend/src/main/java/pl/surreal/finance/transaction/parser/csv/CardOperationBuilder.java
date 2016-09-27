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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.db.IResourceLookup;
import pl.surreal.finance.transaction.parser.ITransactionBuilder;

public class CardOperationBuilder implements ITransactionBuilder 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CardOperationBuilder.class);
	private CardOperation cardOperation;
	
	private IResourceLookup<Card> cardLookup;
	private HashMap<String,Card> cardCache = new HashMap<>();
	
	public CardOperationBuilder() {
		cardOperation = new CardOperation();
	}
	
	private Card getCard(String id) {
		Card card = null;
		if(cardCache.containsKey(id)) {
			card = cardCache.get(id);
		} else {
			if(cardLookup!=null) {
				Card tmpCard = cardLookup.lookup(id);
				cardCache.put(id,tmpCard);
				card = tmpCard;
			}
		}
		return card;
	}
	
	@Override
	public void setDate(String dateString) {
		try {
			cardOperation.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable date '{}'",e.getMessage());
		}
	}

	@Override
	public void setAccountingDate(String dateString) {
		try {
			cardOperation.setAccountingDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
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
    	Pattern detailsPattern = Pattern.compile("(.+),\\s+(.+),\\s+([0-9][0-9\\.\\s]+)\\s+([A-Z\\s]{3,})\\s+[0-9\\.\\s]+\\s+[A-Z\\s]{3,},\\s+[^:]+:\\s+([0-9\\-]+).*");
    	Matcher detailsMatcher = detailsPattern.matcher(details.trim());
    	if(detailsMatcher.matches()) {
    		cardOperation.setTitle(detailsMatcher.group(1).trim());
    		cardOperation.setDestination(detailsMatcher.group(2).trim());
    		cardOperation.setAmount(new BigDecimal(Double.parseDouble(detailsMatcher.group(3).replaceAll("\\s","").replace(',','.'))));
    		cardOperation.setCurrency(detailsMatcher.group(4).replaceAll("\\s",""));
    		Card card = getCard(detailsMatcher.group(5));
    		if(card!=null) {
    			cardOperation.setCard(card.getDetails());
    		}
    	} else {
    		LOGGER.warn("getTransaction : no match on main pattern, transaction will be missing details");
    		LOGGER.debug("getTransaction : mo matcn on main pattern for \""+details+"\"");
    	}
		return cardOperation;
	}
	
	public void setCardLookup(IResourceLookup<Card> cardLookup) {
		this.cardLookup = cardLookup;
	}
	
	public IResourceLookup<Card> getCardLookup() {
		return cardLookup;
	}
}
