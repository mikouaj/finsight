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

package pl.surreal.finance.transaction.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportResult
{
	@JsonProperty
	private String fileName;
	@JsonProperty
	private int processed;
	@JsonProperty
	private int imported;
	@JsonProperty
	private int contraintViolations;
	@JsonProperty
	private int nulls;

	public ImportResult() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}
	
	public void incProcessed() {
		this.processed++;
	}

	public int getImported() {
		return imported;
	}

	public void setImported(int imported) {
		this.imported = imported;
	}
	
	public void incImported() {
		this.imported++;
	}

	public int getContraintViolations() {
		return contraintViolations;
	}

	public void setContraintViolations(int contraintViolations) {
		this.contraintViolations = contraintViolations;
	}
	
	public void incContraintViolations() {
		this.contraintViolations++;
	}

	public int getNulls() {
		return nulls;
	}

	public void setNulls(int nulls) {
		this.nulls = nulls;
	}
	
	public void incNulls() {
		this.nulls++;
	}
}
