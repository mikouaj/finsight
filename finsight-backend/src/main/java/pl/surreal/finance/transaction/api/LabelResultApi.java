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

public class LabelResultApi {
	@JsonProperty
	private int transactionsCount;
	@JsonProperty
	private int labelsCount;
	
	public LabelResultApi() { }
	
	public LabelResultApi(int transactionsCount,int labelsCount) {
		this.transactionsCount = transactionsCount;
		this.labelsCount = labelsCount;
	}

	public int getTransactionsCount() {
		return transactionsCount;
	}

	public void setTransactionsCount(int transactionsCount) {
		this.transactionsCount = transactionsCount;
	}

	public int getLabelsCount() {
		return labelsCount;
	}

	public void setLabelsCount(int labelsCount) {
		this.labelsCount = labelsCount;
	}
}
