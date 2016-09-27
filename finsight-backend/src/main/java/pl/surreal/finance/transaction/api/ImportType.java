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

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportType 
{
	@JsonProperty
	private String id;
	@JsonProperty
	private String description; 
	@JsonProperty
	private URI baseResourceURI;

	public ImportType() {
	}
	
	public ImportType(String id,String description,URI baseResourceURI) {
		this.id = id;
		this.description = description;
		this.baseResourceURI = baseResourceURI;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URI getBaseResourceURI() {
		return baseResourceURI;
	}

	public void setBaseResourceURI(URI baseResourceURI) {
		this.baseResourceURI = baseResourceURI;
	}
}
