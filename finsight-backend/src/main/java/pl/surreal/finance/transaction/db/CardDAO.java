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
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.Label;

public class CardDAO extends AbstractDAO<Card> implements IResourceLookup<Card>
{
	public CardDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
    public Optional<Card> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
    
    public List<Card> findAll() {
    	return list(namedQuery("pl.surreal.finance.transaction.core.Card.findAll"));
    }
    
    public Card create(Card card) {
        return persist(card);
    }
    
    public void delete(Card card) {
    	currentSession().delete(card);
    }
    
    public void deleteById(Long id) {
    	Card card = (Card)currentSession().load(Card.class,id);
    	currentSession().delete(card);
    }

	@Override
	public Card lookup(String naturalId) {
		Query cardQuery = namedQuery("pl.surreal.finance.transaction.core.Card.findByNo");
		cardQuery.setString("number",naturalId);
		return uniqueResult(cardQuery);
	}
}
