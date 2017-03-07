/* Copyright 2017 Mikolaj Stefaniak
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

package pl.surreal.finance.transaction.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferApi
{
	@JsonProperty
	private boolean internal;
	@JsonProperty
	private String direction;
	@JsonProperty
	private String description;
	@JsonProperty
	private AccountApi srcAccount;
	@JsonProperty
	private AccountApi dstAccount;
	
	public TransferApi() { }

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountApi getSrcAccount() {
		return srcAccount;
	}

	public void setSrcAccount(AccountApi srcAccount) {
		this.srcAccount = srcAccount;
	}

	public AccountApi getDstAccount() {
		return dstAccount;
	}

	public void setDstAccount(AccountApi dstAccount) {
		this.dstAccount = dstAccount;
	}
}
