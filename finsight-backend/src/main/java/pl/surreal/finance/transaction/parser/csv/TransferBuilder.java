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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.AccountDetails;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.core.Transfer.TransferDirection;
import pl.surreal.finance.transaction.parser.ITransactionBuilder;

public class TransferBuilder implements ITransactionBuilder 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferBuilder.class);
	private Transfer transfer;
	private Account baseAccount;
	private AccountDetails baseAccountDetails;
	
	public TransferBuilder() {
		transfer = new Transfer();
	}

	@Override
	public void setDate(String dateString) {
		try {
			transfer.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable date '{}'",e.getMessage());
		}
	}

	@Override
	public void setAccountingDate(String dateString) {
		try {
			transfer.setAccountingDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
		} catch(ParseException e) {
			LOGGER.error("setDate : not parsable accoutingDate '{}'",e.getMessage());
		}
	}
	
	@Override
	public void setCurrency(String currencyString) {
		transfer.setCurrency(currencyString);
	}

	@Override
	public void setAccountingCurrency(String currencyString) {
		transfer.setAccountingCurrency(currencyString);
		transfer.setCurrency(currencyString);
	}
	
	@Override
	public void setAmount(String amountString) {
		transfer.setAmount(new BigDecimal(Double.parseDouble(amountString)));
	}

	@Override
	public void setAccountingAmount(String amountString) {
		transfer.setAccountingAmount(new BigDecimal(Double.parseDouble(amountString)));
		transfer.setAmount(transfer.getAccountingAmount());
	}
	
	@Override
	public void setBalanceAfter(String balanceAfterString) {
		transfer.setBalanceAfter(new BigDecimal(Double.parseDouble(balanceAfterString)));
	}
	
	public void setInternal(boolean isInternal) {
		transfer.setInternal(isInternal);
	}
	
	public void setDirection(TransferDirection direction) {
		transfer.setDirection(direction);
	}
	
	public Account getBaseAccount() {
		return baseAccount;
	}

	public void setBaseAccount(Account baseAccount) {
		this.baseAccount = baseAccount;
		if(baseAccount!=null) {
			this.baseAccountDetails = baseAccount.getDetails();
		}
	}

	private Transfer parseIncomingDetails(String details) {
		String[] detailTokens = details.split(";");
		Pattern detailPattern = Pattern.compile("Nadawca:\\s([0-9][0-9\\s]+[0-9])\\s(.+)T.+\u0107\\s*:(.+)");
		Matcher detailMatcher = detailPattern.matcher(detailTokens[2].trim());
		if(detailMatcher.matches()) {
			String destAccountNo = detailMatcher.group(1).trim().replaceAll("\\s","");
			String destAccountName = detailMatcher.group(2).trim();
			transfer.setSrcAccount(new AccountDetails(destAccountNo,destAccountName));
			transfer.setDstAccount(baseAccountDetails);
			transfer.setDescription(detailMatcher.group(3).trim());
		}
		transfer.setTitle(detailTokens[0].trim());	
		return transfer;
	}
	
	private Transfer parseOutgoingDetails(String details) {
		String[] detailTokens = details.split(";");
		Pattern detailPattern = Pattern.compile("Kwota:.+\\sAdresat:\\s([0-9][0-9\\s]+[0-9])\\s(.+)T.+\u0107\\s*:(.+)");
		Matcher detailMatcher = detailPattern.matcher(detailTokens[2].trim());
		if(detailMatcher.matches()) {
			String destAccountNo = detailMatcher.group(1).trim().replaceAll("\\s","");
			String destAccountName = detailMatcher.group(2).trim();
			transfer.setSrcAccount(baseAccountDetails);
			transfer.setDstAccount(new AccountDetails(destAccountNo,destAccountName));
			transfer.setDescription(detailMatcher.group(3).trim());
		}
		transfer.setTitle(detailTokens[0].trim());	
		return transfer;
	}
	
	private Transfer parseInternalDetails(String details) {
		String[] detailTokens = details.split(";");
		Pattern detailPattern = Pattern.compile("((Adresat)|(Nadawca)):\\s([0-9][0-9\\s]+[0-9])\\s(.+)T.+\u0107\\s*:(.+)");
		Matcher detailMatcher = detailPattern.matcher(detailTokens[2].trim());
		if(detailMatcher.matches()) {
			String destAccountNo = detailMatcher.group(4).trim().replaceAll("\\s","");
			String destAccountName = detailMatcher.group(5).trim();
			if(detailMatcher.group(1).compareTo("Adresat")==0) {
				transfer.setDirection(TransferDirection.OUTGOING);
				transfer.setSrcAccount(baseAccountDetails);
				transfer.setDstAccount(new AccountDetails(destAccountNo,destAccountName));
			} else {
				transfer.setDirection(TransferDirection.INCOMING);
				transfer.setSrcAccount(new AccountDetails(destAccountNo,destAccountName));
				transfer.setDstAccount(baseAccountDetails);
			}
			transfer.setDescription(detailMatcher.group(6).trim());
		} else {
			LOGGER.warn("parseInternalDetails : no match on main pattern, transaction will be missing details");
			LOGGER.debug("parseInternalDetails : mo matcn on main pattern for \""+detailTokens[2]+"\"");
		}
		transfer.setTitle(detailTokens[0].trim());	
		return transfer;
	}

	@Override
	public Transaction getTransaction(String details) {
		switch(transfer.getDirection()) {
			case INCOMING:
				parseIncomingDetails(details);
				break;
			case OUTGOING:
				parseOutgoingDetails(details);
				break;
			case UNKNOWN:
				parseInternalDetails(details);
				break;
			default:
				LOGGER.warn("getTransaction : unknown transfer direction");
				break;
		}
		return transfer;
	}
}
