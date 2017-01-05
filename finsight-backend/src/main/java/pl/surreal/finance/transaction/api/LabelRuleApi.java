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

import com.fasterxml.jackson.annotation.JsonProperty;

import pl.surreal.finance.transaction.resources.LabelRuleResource;

public class LabelRuleApi {
	@JsonProperty
	private Long id;
	@JsonProperty
	private String regexp;
	@JsonProperty
	private boolean active;
	@JsonProperty("labels")
	private List<Long> labelIDs = new ArrayList<>();
	@JsonProperty
	@InjectLink(resource=LabelRuleResource.class,method="getById",style=Style.ABSOLUTE,bindings = {@Binding(name="id",value="${instance.id}")})
	private URI uri;
	
	public LabelRuleApi() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Long> getLabelIDs() {
		return labelIDs;
	}

	public void setLabelIDs(List<Long> labelIDs) {
		this.labelIDs = labelIDs;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
}
