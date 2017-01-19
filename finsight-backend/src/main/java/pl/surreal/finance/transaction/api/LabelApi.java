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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pl.surreal.finance.transaction.resources.LabelResource;

public class LabelApi {
	@JsonProperty
	private Long id;
	@JsonProperty
	private String text;
	@JsonProperty
	private String path;
	@JsonProperty("parent")
	private Long parentId;
	@JsonProperty("children")
	private List<Long> childrenIds = new ArrayList<>();
	//@JsonProperty("rules")
	@JsonIgnore
	private List<Long> ruleIds = new ArrayList<>();
	@JsonProperty("transactions")
	private List<URI> transactionURIs = new ArrayList<>();
	@JsonProperty
	@InjectLink(resource=LabelResource.class,method="getById",style=Style.ABSOLUTE,bindings = {@Binding(name="id",value="${instance.id}")})
	private URI uri;
	
	public LabelApi() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public List<Long> getChildrenIds() {
		return childrenIds;
	}
	
	public void setChildrenIds(List<Long> childrenIds) {
		this.childrenIds = childrenIds;
	}
	
	public List<Long> getRuleIds() {
		return ruleIds;
	}
	
	public void setRuleIds(List<Long> ruleIds) {
		this.ruleIds = ruleIds;
	}
	

	public List<URI> getTransactionURIs() {
		return transactionURIs;
	}

	public void setTransactionURIs(List<URI> transactionURIs) {
		this.transactionURIs = transactionURIs;
	}

	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
	}
}