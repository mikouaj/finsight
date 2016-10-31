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

package pl.surreal.finance.transaction.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.db.IResourceLookup;
import pl.surreal.finance.transaction.parser.csv.CSVParser;
import pl.surreal.finance.transaction.parser.html.HTMLParser;

public class ParserFactory implements IParserFactory
{
	private final static Logger LOGGER = LoggerFactory.getLogger(ParserFactory.class);
	private final String dbStatementEncoding = "Windows-1250";
	private final HashMap<Class<?>,IResourceLookup<?>> resourceLookups = new HashMap<>();
	private ILabelProvider labelProvider;
	
	private ITransactionParser getDBStatementParser(InputStream inputStream,String baseAccountId) {
		InputStreamReader reader=null;
		try {
			reader = new InputStreamReader(inputStream,dbStatementEncoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("getDBStatementParser : not supported encoding '{}', '{}'",dbStatementEncoding,e.getMessage());
			return null;
		}
		CSVReader csvReader = new CSVReader(reader,';');
		CSVParser csvParser = new CSVParser(csvReader);
		
		IResourceLookup<?> lookup = resourceLookups.get(Account.class);
		if(lookup!=null) {
			@SuppressWarnings("unchecked")
			IResourceLookup<Account> accountLookup = (IResourceLookup<Account>)lookup;
			Account account = accountLookup.lookup(baseAccountId);
			csvParser.setBaseAccount(account);
		}
		
		lookup = resourceLookups.get(Card.class);
		if(lookup!=null) {
			@SuppressWarnings("unchecked")
			IResourceLookup<Card> cardLookup = (IResourceLookup<Card>)lookup;
			csvParser.setCardLookup(cardLookup);
		}
		return csvParser;
	}
	
	private ITransactionParser getDBHTMLStatementParser(InputStream inputStream,String baseCardId) {
		HTMLParser htmlParser = new HTMLParser(inputStream);
		
		IResourceLookup<?> lookup = resourceLookups.get(Card.class);
		if(lookup!=null) {
			@SuppressWarnings("unchecked")
			IResourceLookup<Card> accountLookup = (IResourceLookup<Card>)lookup;
			Card card = accountLookup.lookup(baseCardId);
			htmlParser.setBaseCard(card);
		}
		return htmlParser;
	}
	
	@Override
	public ParserSupportedType[] getSupportedTypes() {
		ParserSupportedType[] supportedTypes = {
				new ParserSupportedType("dbCSVStatement","DB Account Statement CSV",Account.class),
				new ParserSupportedType("dbHTMLStatement","DB Credit Card Printout HTML",Card.class),
				};
		return supportedTypes;
	}
	
	@Override
	public Optional<ITransactionParser> getParser(InputStream inputStream,String type,String baseResourceId) {
		ITransactionParser parser=null;
		switch(type) {
			case "dbCSVStatement":
				parser = getDBStatementParser(inputStream,baseResourceId);
				break;
			case "dbHTMLStatement":
				parser = getDBHTMLStatementParser(inputStream,baseResourceId);
				break;
		}
		if(parser!=null && labelProvider!=null) {
			parser.setLabelProvider(labelProvider);
		}
		return Optional.ofNullable(parser);
	}

	@Override
	public void addResourceLookup(Class<?> resourceClass, IResourceLookup<?> lookup) {
		resourceLookups.put(resourceClass,lookup);
	}

	@Override
	public void removeResourceLookup(Class<?> resourceClass) {
		resourceLookups.remove(resourceClass);
	}

	@Override
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}
	
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}
}
