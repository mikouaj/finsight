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

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer.TransferDirection;
import pl.surreal.finance.transaction.db.IResourceLookup;
import pl.surreal.finance.transaction.parser.ITransactionBuilder;
import pl.surreal.finance.transaction.parser.ITransactionParser;

public class CSVParser implements ITransactionParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVParser.class);
	private final CSVReader csvReader;
	private final Iterator<String[] >csvIterator;
	private Account baseAccount;
	private IResourceLookup<Card> cardLookup;
	
	public CSVParser(CSVReader csvReader) {
		 this.csvReader = csvReader;
		 csvIterator = csvReader.iterator();
	}

	@Override
	public Optional<Transaction> getNext() {
		String[] rowColumns = csvIterator.next();
		//Remove crappy shit generated in great DB csv export
		for(int i=0;i<rowColumns.length;i++) {
			rowColumns[i] = rowColumns[i].trim().replaceAll("\\p{IsWhite_Space}"," ");
		}
		
		OperationTypeParser opParser = new OperationTypeParser();		
		ITransactionBuilder builder = null;
		switch(opParser.getOperationType(rowColumns[2])) {
			case OperationTypeParser.COMMISSION:
				builder = new CommissionBuilder();
				break;
			case OperationTypeParser.CARDOPERATION:
				{
					CardOperationBuilder cBuilder = new CardOperationBuilder();
					cBuilder.setCardLookup(cardLookup);
					builder = cBuilder;
				}
				break;
			case OperationTypeParser.TRANSFER_INCOMING:	
				{ 
					TransferBuilder tBuilder = new TransferBuilder();
					tBuilder.setDirection(TransferDirection.INCOMING);
					tBuilder.setBaseAccount(baseAccount);
					builder = tBuilder;
				}
				break;
			case OperationTypeParser.TRANSFER_OUTGOING:
				{ 
					TransferBuilder tBuilder = new TransferBuilder();
					tBuilder.setDirection(TransferDirection.OUTGOING);
					tBuilder.setBaseAccount(baseAccount);
					builder = tBuilder;
				}
				break;
			case OperationTypeParser.TRANSFER_INTERNAL:
				{ 
					TransferBuilder tBuilder = new TransferBuilder();
					tBuilder.setDirection(TransferDirection.UNKNOWN);
					tBuilder.setInternal(true);
					tBuilder.setBaseAccount(baseAccount);
					builder = tBuilder;
				}
				break;
			default:
				return Optional.empty();
		}
//		try {
//			builder.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rowColumns[0]));
//			builder.setAccountingDate(new SimpleDateFormat("yyyy-MM-dd").parse(rowColumns[1]));
//		} catch(ParseException ex) {
//			LOGGER.error("getNext : not parsable dates '{}'",ex.getMessage());
//			return Optional.ofNullable(null);
//		}
		builder.setDate(rowColumns[0]);
		builder.setAccountingDate(rowColumns[1]);
		builder.setAccountingAmount(rowColumns[3].replaceAll("\\s","").replace(',','.'));
		builder.setAccountingCurrency(rowColumns[4]);
		builder.setBalanceAfter(rowColumns[5].replaceAll("\\s","").replace(',','.'));
		return Optional.of(builder.getTransaction(opParser.getDetails()));
	}

	@Override
	public boolean hasNext() {
		return csvIterator.hasNext();
	}
	
	@Override
	public void close() {
		try {
			csvReader.close();
		} catch (IOException e) {
			LOGGER.warn("close: exception occured '{}'",e.getMessage());
		}
	}

	public Account getBaseAccount() {
		return baseAccount;
	}

	public void setBaseAccount(Account baseAccount) {
		this.baseAccount = baseAccount;
	}

	public IResourceLookup<Card> getCardLookup() {
		return cardLookup;
	}

	public void setCardLookup(IResourceLookup<Card> cardLookup) {
		this.cardLookup = cardLookup;
	}
}