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

package pl.surreal.finance.transaction.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pl.surreal.finance.transaction.resources.LabelResource;

@Entity
@Table(name="label")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.Label.findAll",
            query = "SELECT l FROM Label l"
    )
})
public class Label
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NaturalId
	@Column(name = "text", nullable = false)
	private String text;
	
	@ManyToOne
	@JsonIgnore
	private Label parent;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
//	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
	@JsonIgnore
	private List<Label> children = new ArrayList<>();
	
//	@Transient
//	@JsonProperty
//	@InjectLink(value="label/{id}",style=Style.ABSOLUTE)
//	private URI uri;
	
	@Transient
	@JsonProperty
	@InjectLink(resource=LabelResource.class,method="get",style=Style.ABSOLUTE,bindings = {@Binding(name="id",value="${instance.id}")})
	private URI uri;
	
	public Label() { }
	
	public Label(String text) {
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Label getParent() {
		return parent;
	}

	public void setParent(Label parent) {
		this.parent = parent;
	}
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public List<Label> getChildren() {
		return children;
	}

	public void setChildren(List<Label> children) {
		this.children = children;
	}

	public Label addChild(String text) {
		Label child = new Label(text);
		children.add(child);
		child.setParent(this);
		return child;
	}

	public Label addChild(Label child) {
		children.add(child);
		child.setParent(this);
		return child;
	}
	
	public void removeChild(Label child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public String getPath() {
		if(parent==null) return text;
		return parent.getPath() + "/" +text;
	}
	
    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        Label label = (Label) o;
        return Objects.equals( text, label.text );
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @JsonProperty("parent")
	public URI getParentURI() {
		if(parent!=null) return parent.uri;
		return null;
	}
    
    @JsonProperty("children")
    public List<URI> getChildrenURI() {
    	List<URI> childrenURIs = new ArrayList<>();
    	for(Label child : children) {
    		childrenURIs.add(child.uri);
    	}
    	return childrenURIs;
    }
}