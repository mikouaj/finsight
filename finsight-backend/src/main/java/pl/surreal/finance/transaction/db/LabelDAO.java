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

package pl.surreal.finance.transaction.db;

import java.util.List;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import pl.surreal.finance.transaction.core.Label;

public class LabelDAO  extends AbstractDAO<Label>
{
	public LabelDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
    public List<Label> findAll() {
    	return list(namedQuery("pl.surreal.finance.transaction.core.Label.findAll"));
    }
    
    public Label create(Label label) {
        return persist(label);
    }
}
