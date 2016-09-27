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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationTypeParser
{
	public static final int COMMISSION = 0;
	public static final int TRANSFER_INCOMING = 1;
	public static final int TRANSFER_OUTGOING = 2;
	public static final int TRANSFER_INTERNAL = 3;
	public static final int CARDOPERATION = 4;
	public static final int UNKNOWN = 5;
	
	private HashMap<String,Integer> regExps = new HashMap<>();
	private String details;
	
	public OperationTypeParser() {
		details = "N/A";
		regExps.put("PROWIZJA ZA PRZELEW ELIXIR",OperationTypeParser.COMMISSION);
		regExps.put("OP\u0141ATY I PROWIZJE - Obs\u0142uga karty",OperationTypeParser.COMMISSION);
		regExps.put("OP\u0141ATY I PROWIZJE - Prowizja za wyp\u0142at\u0119 got\u00F3wki",OperationTypeParser.COMMISSION);
		regExps.put("OP\u0141ATY I PROWIZJE - Zmiana pakietu",OperationTypeParser.COMMISSION);
		regExps.put("PRZELEW DO INNEGO BANKU KRAJOWEGO",OperationTypeParser.TRANSFER_OUTGOING);
		regExps.put("PRZELEW NA RACHUNEK W DB PBC",OperationTypeParser.TRANSFER_INTERNAL);
		regExps.put("PRZELEW Z INNEGO BANKU",OperationTypeParser.TRANSFER_INCOMING);
		regExps.put("OPERACJA KART\u0104",OperationTypeParser.CARDOPERATION);
    	//supportedTypes.add("PRZELEW PODATKU"); //7	
	}

	public int getOperationType(String input) {
		for(String regExp : regExps.keySet()) {
			Pattern pattern = Pattern.compile("^("+regExp+") (.+)$");
			Matcher matcher = pattern.matcher(input);
			if(matcher.matches()) {
				details = matcher.group(2);
				return regExps.get(regExp);
			}
		}
		return UNKNOWN;
	}
	
	public String getDetails() {
		return details;
	}
}
