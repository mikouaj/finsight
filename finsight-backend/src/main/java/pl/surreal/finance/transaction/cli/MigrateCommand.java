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

package pl.surreal.finance.transaction.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import pl.surreal.finance.transaction.TransactionConfiguration;
import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.migration.OldConfig;
import pl.surreal.finance.transaction.migration.OldConfigMigrator;
import pl.surreal.finance.transaction.migration.OldConfigReader;

public class MigrateCommand extends ConfiguredCommand<TransactionConfiguration>
{
	private final static Logger LOGGER = LoggerFactory.getLogger(MigrateCommand.class);
	
	public MigrateCommand() {
		super("migrate", "Migrates old configuration data");
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
	public void configure(Subparser subparser) {
		super.configure(subparser);
		subparser.addArgument("-c", "--config")
			.dest("oldconfig")
	        .type(String.class)
	        .required(true)
	        .help("Path to old yml config file");
	}

	@Override
	protected void run(Bootstrap<TransactionConfiguration> bootstrap, Namespace namespace, TransactionConfiguration configuration)
			throws Exception {
		File oldConfigFile = new File(namespace.getString("oldconfig"));
		if(!oldConfigFile.isFile() || !oldConfigFile.canRead()) {
			LOGGER.error("Can't read old config file '{}'",oldConfigFile.getAbsolutePath());
			return;
		}
		LOGGER.debug("File is "+oldConfigFile.getAbsolutePath());
		InputStream inputStream = new FileInputStream(oldConfigFile);

		OldConfigReader reader = new OldConfigReader(new ObjectMapper(new YAMLFactory()));
		OldConfig oldConfig = reader.parse(inputStream);
		OldConfigMigrator migrator = new OldConfigMigrator(oldConfig);
//		List<Label> labels = migrator.getLabels();
//        LOGGER.info("Size is {}",labels.size());
//        for(Label label : labels) {
//        	if(label.getParent()!=null) {
//        		LOGGER.info("Label : {}, parent : {}",label.getText(),label.getParent().getText());
//        	} else {
//        		LOGGER.info("Label : {}",label.getText());
//        	}
//        }
//		Session session = configureHibernateSession(bootstrap,configuration);
//		
//		for(Label label : migrator.getLabels()) {
//			Transaction transaction = session.beginTransaction();
//			try {
//				session.persist(label);
//				transaction.commit();
//			} catch(HibernateException ex) {
//				LOGGER.warn("Couldnt persist label {} due to hibernate exception {}",label.getText(),ex.getMessage());
//				continue;
//			} finally {
//				if(transaction.isActive()) {
//				  transaction.rollback();
//				}
//			}
//		}
//		session.close();
	}
}
