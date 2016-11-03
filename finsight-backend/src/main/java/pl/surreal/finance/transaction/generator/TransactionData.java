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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.Transfer.TransferDirection;

public class TransactionData
{
	public final static Account ownTestAccount = new Account("69696969696969696969696969","MyAccount");
	public final static Card ownTestCard = new Card("1234-69696","MyCard");
	public final static String testTransferTitle = "System db easyNET Nr. dok.: TEST/69";
	public final static String testCardOpTitle = "Treść: Zakup TESTOWY";
	public final static String testWithdrawalTitle = "Treść: Wypłata gotówki TESTOWY";
	public final static String testCommissionTitle = "Dotyczy dok.: TEST/69";
	
	public final static String[] cardopDestinations = {
     "RESTAURACJA MANDARY WARSZAWA", "ADRIAN Adam Goral 4 WARSZAWA POL", "AIOLI-CANTINE Warszawa POL", "WINIARNIA KOTLOWNIA WARSZAWA POL",
     "JMP S.A. BIEDRONKA WARSZAWA", "MARKS & SPENCER BLU WARSZAWA POL", "SPP WARSZAWA WARSZAWA POL", "PETERS ZABRODZIE POL", 
     "Apteka za Grosze J1 Warszawa POL", "DELIKATESY MIESNE NIEGOW POL", "Automatspec Warsaw POL", "Ziko Apteka Warszawa POL",
     "GREEN COFFEE SP Z O WARSZAWA POL", "CARREFOUR HIPERMARK WARSZAWA POL", "ZABKA Z3313 K.1 WARSZAWA POL", "SPP WARSZAWA 2 WARSZAWA POL"
	};

	public final static Account[] transferAccounts = {
		new Account("12345678912345678912345001","Wspólnota Mieszkaniowa Test Warszawa"),
		new Account("12345678912345678912345002","Energetyka S.A. ul. Testowa 11/5 69-669 Warszawa"),
		new Account("12345678912345678912345003","Ubezpieczalnia T.U. S.A. ul. Testowa 11/5 69-669 Warszawa"),
		new Account("12345678912345678912345004","OperatorMobile Sp. z o. o. ul. Testowa 11/5 69-669 Warszawa"),
		new Account("12345678912345678912345005","InternetShop Sp. z o. o. ul. Testowa 11/5 69-669 Warszawa"),
		new Account("12345678912345678912345006","TicketProShop Sp. z o. o. ul. Testowa 11/5 69-669 Warszawa"),
	};
	
	public final static String[] transferDescriptions = {
		"FV VAT 123456", "Zamówienie 123456", "WYNAGRODZENIE 1/1234", "PAYU XX123456789", "zasilenie"
	};
	
	public static String getCardOpDestination() {
		return cardopDestinations[ThreadLocalRandom.current().nextInt(0,cardopDestinations.length)];
	}
	
	public static Account getTransferAccount() {
		return transferAccounts[ThreadLocalRandom.current().nextInt(0,transferAccounts.length)];
	}
	
	public static String getTransferDescription() {
		return transferDescriptions[ThreadLocalRandom.current().nextInt(0,transferDescriptions.length)];
	}
	
	public static TransferDirection getTransferDirection() {
		TransferDirection[] directions = TransferDirection.values();
		return directions[ThreadLocalRandom.current().nextInt(0,directions.length)];
	}
	
	public static Date getRandomDate(Date minDate,Date maxDate) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(minDate);
		int minDay = gc.get(GregorianCalendar.DAY_OF_YEAR);
		gc.setTime(maxDate);
		int maxDay = gc.get(GregorianCalendar.DAY_OF_YEAR);
		int rndDay = ThreadLocalRandom.current().nextInt(minDay,maxDay);
		gc.set(GregorianCalendar.DAY_OF_YEAR, rndDay);
		return gc.getTime();
	}
}
	