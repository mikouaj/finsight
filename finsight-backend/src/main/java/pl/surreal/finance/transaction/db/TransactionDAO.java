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
import pl.surreal.finance.transaction.core.Transaction;

public class TransactionDAO extends AbstractDAO<Transaction>
{
	private static final String findAllQuery = "pl.surreal.finance.transaction.core.Transaction.findAll";
	
	public TransactionDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Transaction create(Transaction transaction) {
		return persist(transaction);
	}
	
	public List<Transaction> findAll(Integer first,Integer max) {
		Query query = namedQuery(findAllQuery);
		if(first!=null) query.setFirstResult(first);
		if(max!=null) query.setMaxResults(max);
		return list(query);
	}
	
	public List<Transaction> findAll() {
		return list(namedQuery(findAllQuery));
	}
	
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
}