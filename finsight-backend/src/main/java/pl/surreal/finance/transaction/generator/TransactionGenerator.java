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

package pl.surreal.finance.transaction.generator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import pl.surreal.finance.transaction.core.CardDetails;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;

public class TransactionGenerator implements Iterator<Transaction>
{	
	private int transferIndex = 0;
	private int cardOperationIndex = 0;
	private int commissionIndex = 0;
	
	private final int transferLimit;
	private final int cardOperatonLimit;
	private final  int commissionLimit;
	private Date startDate = new Date(new Date().getTime() - 2629743);
	private Date endDate = new Date();
	private String currency = "PLN";
	
	public TransactionGenerator() {
		this.transferLimit = 50;
		this.cardOperatonLimit = 50;
		this.commissionLimit = 10;
	}
	
	public TransactionGenerator(int transferLimit,int cardOperatonLimit,int commissionLimit) {
		this.transferLimit = transferLimit;
		this.cardOperatonLimit = cardOperatonLimit;
		this.commissionLimit = commissionLimit;
	}
	
	private Transaction setBasicTransactionData(Transaction t) {
		t.setDate(TransactionData.getRandomDate(startDate, endDate));
		t.setAccountingDate(t.getDate());
		t.setCurrency(currency);
		t.setAccountingCurrency(currency);
		t.setAmount(new BigDecimal(ThreadLocalRandom.current().nextInt(5,3000)));
		t.setAccountingAmount(t.getAmount());
		t.setBalanceAfter((new BigDecimal(ThreadLocalRandom.current().nextInt(3000,10000))));
		return t;
	}
	
	@Override
	public boolean hasNext() {
		return transferIndex + cardOperationIndex + commissionIndex < transferLimit + cardOperatonLimit + commissionLimit;
	}
	
	@Override
	public Transaction next() {
		if(transferIndex<transferLimit) {
			transferIndex++;
			return generateTransfer();
		}
		if(cardOperationIndex<cardOperatonLimit) {
			cardOperationIndex++;
			return generateCardOperation();
			
		}
		if(commissionIndex<commissionLimit) {
			commissionIndex++;
			return generateComission();
		}
		return null;
	}

	
	public Transfer generateTransfer() {
		Transfer transfer = new Transfer();
		setBasicTransactionData(transfer);
		transfer.setTitle(TransactionData.testTransferTitle);
		transfer.setInternal(false);
		transfer.setDirection(TransactionData.getTransferDirection());
		transfer.setDescription(TransactionData.getTransferDescription());
		return transfer;
	}
	
	public CardOperation generateCardOperation() {
		CardOperation cardOperation = new CardOperation();
		setBasicTransactionData(cardOperation);
		if(ThreadLocalRandom.current().nextInt(0,1)>0) {
			cardOperation.setTitle(TransactionData.testCardOpTitle);
		} else {
			cardOperation.setTitle(TransactionData.testWithdrawalTitle);
		}
		cardOperation.setCard(new CardDetails(TransactionData.ownTestCard.getNumber(),TransactionData.ownTestCard.getName()));
		cardOperation.setDestination(TransactionData.getCardOpDestination());
		return cardOperation;
	}

	public Commission generateComission() {
		Commission commission = new Commission();
		setBasicTransactionData(commission);
		commission.setTitle(TransactionData.testCommissionTitle);
		return commission;
	}

	public int getTransferLimit() {
		return transferLimit;
	}
	
	public int getCardOperatonLimit() {
		return cardOperatonLimit;
	}

	public int getCommissionLimit() {
		return commissionLimit;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}