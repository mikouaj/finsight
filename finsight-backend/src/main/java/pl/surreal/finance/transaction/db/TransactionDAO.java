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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.Transaction;

public class TransactionDAO extends AbstractDAO<Transaction>
{
	private static final String findAllQuery = "pl.surreal.finance.transaction.core.Transaction.findAll";
	private static final String findByLabelQuery = "pl.surreal.finance.transaction.core.Transaction.findByLabel";
	private static final String findByLabelFromQuery = "pl.surreal.finance.transaction.core.Transaction.findByLabelFrom";
	private static final String findByLabelToQuery = "pl.surreal.finance.transaction.core.Transaction.findByLabelTo";
	private static final String findByLabelFromToQuery = "pl.surreal.finance.transaction.core.Transaction.findByLabelFromTo";
	
	private static final int queryMaxLabels = 10;

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
	
	public List<Transaction> findAll(HashMap<String,Object> params) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT t FROM Transaction t");
		if(params.size()>0) {
			queryBuilder.append(" WHERE 1=1");
			if(params.containsKey("label")) {
				@SuppressWarnings("unchecked")
				List<Label> labels = (List<Label>)params.get("label");
				for(int cnt=0;cnt<labels.size() && cnt<queryMaxLabels;cnt++) {
					queryBuilder.append(" AND :label").append(cnt).append(" member of t.labels");
				}
			}
			if(params.containsKey("dateFrom")) {
				queryBuilder.append(" AND  t.date > :dateFrom");
			}
			if(params.containsKey("dateTo")) {
				queryBuilder.append(" AND  t.date < :dateTo");
			}
		}
		queryBuilder.append(" ORDER BY t.accountingDate DESC");
		Query query = currentSession().createQuery(queryBuilder.toString());
		if(params.size()>0) {
			if(params.containsKey("label")) {
				@SuppressWarnings("unchecked")
				List<Label> labels = (List<Label>)params.get("label");
				for(int cnt=0;cnt<labels.size() && cnt<queryMaxLabels;cnt++) {
					query.setParameter("label"+cnt, labels.get(cnt));
				}
			}
			if(params.containsKey("dateFrom")) {
				query.setParameter("dateFrom", params.get("dateFrom"));
			}
			if(params.containsKey("dateTo")) {
				query.setParameter("dateTo", params.get("dateTo"));
			}
		}
		
		if(params.containsKey("first")) query.setFirstResult((Integer.parseInt((String)params.get("first"))));
		if(params.containsKey("max")) query.setMaxResults((Integer.parseInt((String)params.get("max"))));
		return list(query);
	}
	
	public List<Transaction> findAll() {
		return list(namedQuery(findAllQuery));
	}
	
	public List<Transaction> findByLabel(Label label,Date dateFrom,Date dateTo) {
		Objects.requireNonNull(label);
		Query query;
		if(dateFrom==null && dateTo==null) {
			query = namedQuery(findByLabelQuery);
		} else if(dateFrom!=null && dateTo!=null) {
			query = namedQuery(findByLabelFromToQuery);
			query.setDate("dateFrom",dateFrom);
			query.setDate("dateTo",dateTo);
		} else if(dateFrom!=null && dateTo==null) {
			query = namedQuery(findByLabelFromQuery);
			query.setDate("dateFrom",dateFrom);
		} else {
			query = namedQuery(findByLabelToQuery);
			query.setDate("dateTo",dateTo);
		}
		query.setParameter("label", Objects.requireNonNull(label));
		return list(query);
	}
	
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
}