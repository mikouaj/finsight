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

package pl.surreal.finance.transaction.parser;

public class ParserSupportedType
{
	private String id;
	private String description;
	private Class<?> baseResourceClass;
	
	public ParserSupportedType(String id, String description, Class<?> baseResourceClass) {
		this.id = id;
		this.description = description;
		this.baseResourceClass = baseResourceClass;
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
	public Class<?> getBaseResourceClass() {
		return baseResourceClass;
	}
	public void setBaseResourceClass(Class<?> baseResourceClass) {
		this.baseResourceClass = baseResourceClass;
	}
}
