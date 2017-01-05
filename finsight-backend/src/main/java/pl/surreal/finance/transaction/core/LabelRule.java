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

package pl.surreal.finance.transaction.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="labelRule")
@NamedQueries({
    @NamedQuery(
            name = "pl.surreal.finance.transaction.core.LabelRule.findAll",
            query = "SELECT l FROM LabelRule l"
    )
})
public class LabelRule
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NaturalId(mutable=true)
	@Column(name = "regexp", nullable = false)
	@NotEmpty
	private String regexp;
	
	@Column(name = "active")
	private boolean active = false;
	
	@ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Label> labels = new ArrayList<>();
	
	public LabelRule() { }
	
	public LabelRule(String regExp) {
		this.regexp = regExp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		removeAllLabels();
		this.labels = labels;
	}
	
	public void addLabel(Label label) {
		if(!labels.contains(label)) {
			this.labels.add(label);
			label.getRules().add(this);
		}
	}
	
	public void removeLabel(Label label) {
		if(labels.contains(label)) {
			this.labels.remove(label);
			label.getRules().remove(this);
		}
	}
	
	public void removeAllLabels() {
		Iterator<Label> labelI = labels.iterator();
		while(labelI.hasNext()) {
			Label label = labelI.next();
			label.getRules().remove(this);
			labelI.remove();
		}
	}
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        LabelRule rule = (LabelRule) o;
        return Objects.equals( regexp, rule.regexp );
    }

    @Override
    public int hashCode() {
        return Objects.hash( regexp );
    }
}