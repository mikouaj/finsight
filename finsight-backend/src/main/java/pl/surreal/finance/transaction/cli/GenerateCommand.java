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

package pl.surreal.finance.transaction.cli;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import pl.surreal.finance.transaction.TransactionConfiguration;
import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.generator.TransactionData;
import pl.surreal.finance.transaction.generator.TransactionGenerator;

public class GenerateCommand extends ConfiguredCommand<TransactionConfiguration> {

	private final static Logger LOGGER = LoggerFactory.getLogger(GenerateCommand.class);
	
	public GenerateCommand() {
		super("generate", "Generates test transaction data");
	}
	
	private Session configureHibernateSession(Bootstrap<TransactionConfiguration> bootstrap, TransactionConfiguration configuration) throws Exception {
		HibernateBundle<TransactionConfiguration> hibernate = new HibernateBundle<TransactionConfiguration>(Transaction.class,Commission.class,CardOperation.class,Transfer.class,Card.class,Account.class,Label.class,LabelRule.class) {
		    @Override
		    public DataSourceFactory getDataSourceFactory(TransactionConfiguration configuration) {
		        return configuration.getDataSourceFactory();
		    }
		};
		configuration.getDataSourceFactory().getProperties().put("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext");
		
		hibernate.run(configuration, new Environment("EnvName", bootstrap.getObjectMapper(),
			            bootstrap.getValidatorFactory().getValidator(),
			            bootstrap.getMetricRegistry(),
			            bootstrap.getClassLoader()));
		
		Session session = hibernate.getSessionFactory().openSession();
		return session;
	}

	@Override
	protected void run(Bootstrap<TransactionConfiguration> bootstrap, Namespace arg1, TransactionConfiguration configuration)
			throws Exception {
		Session session = configureHibernateSession(bootstrap,configuration);
		
		
		Transaction transaction = session.beginTransaction();
		TransactionGenerator generator = new TransactionGenerator();
		LOGGER.info("Transactions generation started");
		session.persist(TransactionData.ownTestAccount);
		session.persist(TransactionData.ownTestCard);
		while(generator.hasNext()) {
			session.persist(generator.next());
		}

		transaction.commit();
		session.close();
		LOGGER.info("Transactions generation finished");
	}
}
