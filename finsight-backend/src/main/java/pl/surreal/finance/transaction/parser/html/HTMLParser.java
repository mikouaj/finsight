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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.parser.ILabelProvider;
import pl.surreal.finance.transaction.parser.ITransactionParser;

public class HTMLParser implements ITransactionParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HTMLParser.class);
	private InputStream inputStream;
	private final Iterator<Element> rowsIterator;
	private Card baseCard;
	private ILabelProvider labelProvider;
	
	public HTMLParser(InputStream inputStream) {
		this.inputStream = inputStream;
		rowsIterator = parse(inputStream).orElse(new Elements()).iterator();
	}

	private Optional<Elements> parse(InputStream inputStream) {
		Elements rows = null;
		try {
			Document document = Jsoup.parse(inputStream,null,"");
			Elements docTables = document.select("table");
			Element dataTable = docTables.get(3);
			Elements dataTableRows = dataTable.select("tr");
			if(dataTableRows.size()<1) throw new Exception("Number of rows in main table should be at least one (header)");
			dataTableRows.remove(0); // remove header
			rows = dataTableRows;
		} catch (Exception e) {
			LOGGER.error("parse : error parsing file '{}'",e.getMessage());
			e.printStackTrace();
		}
		return Optional.ofNullable(rows);
	}
	
	private String[] getRowTokens(Elements row) {
		String[] tokens = new String[row.size()];
		for(int i=0;i<row.size();i++) {
			tokens[i] = row.get(i).text().trim().replaceAll("\\p{IsWhite_Space}"," ");
		}
		return tokens;
	}
	
	private String[] parseDates(String token) throws ParseException {
		String[] dates = token.split("\\s");
		if(dates.length!=2) throw new ParseException("parseDates: number date elements is not 2",0);
		return dates;
	}

	private String[] parseAmountCurrency(String token) throws ParseException {
		String[] amountCurrency = new String[2];
		Pattern pattern = Pattern.compile("([-]?[0-9,\\s]+)\\s([A-Z]{3})$");
		Matcher matcher = pattern.matcher(token);
		if(matcher.matches()) {
			amountCurrency[0] = matcher.group(1).replaceAll("\\s","").replace(',','.');
			amountCurrency[1] = matcher.group(2);
		} else {
			throw new ParseException("parseAmountCurrency : error parsing amount and currency, pattern doesnt match",0);
		}
		return amountCurrency;
	}
	
	public Card getBaseCard() {
		return baseCard;
	}

	public void setBaseCard(Card baseCard) {
		this.baseCard = baseCard;
	}
	
	@Override
	public Optional<Transaction> getNext() {
		Element row = rowsIterator.next();
		String[] rowTokens = getRowTokens(row.select("td"));
		Transaction transaction=null;
		
		if(rowTokens.length==4) {
			try {
				String[] dates = parseDates(rowTokens[0]);
				String[] cashDetails = parseAmountCurrency(rowTokens[2]);
				String[] cashAccoutingDetails = parseAmountCurrency(rowTokens[3]);
				
				CreditCardBuilder builder = new CreditCardBuilder();
				builder.setDate(dates[0]);
				builder.setAccountingDate(dates[1]);
				builder.setAmount(cashDetails[0]);
				builder.setCurrency(cashDetails[1]);
				builder.setAccountingAmount(cashAccoutingDetails[0]);
				builder.setAccountingCurrency(cashAccoutingDetails[1]);
				builder.setBalanceAfter("0");
				builder.setBaseCard(baseCard);
				transaction = builder.getTransaction(rowTokens[1]);
			} catch(Exception e) {
				LOGGER.warn("getNext: can't parse row due to exception '{}'",e.getMessage());
				transaction = null;
			}
		}
		return Optional.ofNullable(transaction);
	}

	@Override
	public boolean hasNext() {
		return rowsIterator.hasNext();
	}

	@Override
	public void close() {
		try {
			inputStream.close();
		} catch (IOException e) {
			LOGGER.warn("close: exception occured '{}'",e.getMessage());
		}
	}

	@Override
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}
}
