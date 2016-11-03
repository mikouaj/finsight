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
import java.util.concurrent.ThreadLocalRandom;

import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.db.AccountDAO;
import pl.surreal.finance.transaction.db.CardDAO;
import pl.surreal.finance.transaction.db.CardOperationDAO;
import pl.surreal.finance.transaction.db.CommissionDAO;
import pl.surreal.finance.transaction.db.TransferDAO;

public class TransactionGenerator
{
	private final AccountDAO accountDAO;
	private final CardDAO cardDAO;
	private final TransferDAO transferDAO;
	private final CardOperationDAO cardOperationDAO;
	private final CommissionDAO commissionDAO;
	
	private int transferLimit = 50;
	private int cardOperatonLimit = 50;
	private int commissionLimit = 10;
	private Date startDate = new Date(new Date().getTime() - 2629743);
	private Date endDate = new Date();
	private String currency = "PLN";
	
	public TransactionGenerator(AccountDAO accountDAO,CardDAO cardDAO,TransferDAO transferDAO,CardOperationDAO cardOperationDAO,CommissionDAO commissionDAO) {
		this.accountDAO = accountDAO;
		this.cardDAO = cardDAO;
		this.transferDAO = transferDAO;
		this.cardOperationDAO = cardOperationDAO;
		this.commissionDAO = commissionDAO;
	}
	
	public void generate() {
		for(int i=0;i<transferLimit;i++) {
			Transfer transfer = new Transfer();
			setBasicTransactionData(transfer);
			transfer.setTitle(TransactionData.testTransferTitle);
			transfer.setInternal(false);
			transfer.setDirection(TransactionData.getTransferDirection());
			transfer.setDescription(TransactionData.getTransferDescription());
			transferDAO.create(transfer);
		}
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

	public int getTransferLimit() {
		return transferLimit;
	}

	public void setTransferLimit(int transferLimit) {
		this.transferLimit = transferLimit;
	}

	public int getCardOperatonLimit() {
		return cardOperatonLimit;
	}

	public void setCardOperatonLimit(int cardOperatonLimit) {
		this.cardOperatonLimit = cardOperatonLimit;
	}

	public int getCommissionLimit() {
		return commissionLimit;
	}

	public void setCommissionLimit(int commissionLimit) {
		this.commissionLimit = commissionLimit;
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