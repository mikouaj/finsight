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
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
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
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
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
		HibernateBundle<TransactionConfiguration> hibernate = new HibernateBundle<TransactionConfiguration>(Label.class,LabelRule.class) {
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
	
	private Label queryLabelByText(Session session,String text) throws NullPointerException,NonUniqueResultException,HibernateException {
		Objects.requireNonNull(session);
		Query labelByTextQuery = (Query)Objects.requireNonNull(session.getNamedQuery("pl.surreal.finance.transaction.core.Label.findByText"));
		labelByTextQuery.setString("text",text);
		Label label = (Label)labelByTextQuery.uniqueResult();
		return label;
	}
	
	private LabelRule queryLabelRuleByRegexp(Session session,String regexp) throws NullPointerException,NonUniqueResultException,HibernateException {
		Objects.requireNonNull(session);
		Query labelByRegexptQuery = (Query)Objects.requireNonNull(session.getNamedQuery("pl.surreal.finance.transaction.core.LabelRule.findByRegexp"));
		labelByRegexptQuery.setString("regexp",regexp);
		LabelRule labelRule = (LabelRule)labelByRegexptQuery.uniqueResult();
		return labelRule;
	}
	
	private void persist(Session session,Object obj) throws Exception {
		Transaction transaction=null;
		try {
			transaction = (Transaction)Objects.requireNonNull(session.beginTransaction());
			session.persist(obj);
			transaction.commit();
		} catch(Exception e) {
			if(transaction!=null && transaction.isActive()) { transaction.rollback(); }
			throw e;
		}
	}
	
	private void persistLabels(Session session,OldConfigMigrator migrator) {
		for(Label label : migrator.getLabels(false)) {
			try {
				Label dbLabel = queryLabelByText(session, label.getText());
				if(dbLabel != null ) {
					LOGGER.warn("Skipping label {} as it already exists in db",label.getText());
					continue;
				}
			} catch(Exception ex) {
				LOGGER.warn("Exception on label {} : can't query label due to exception {}",label.getText(),ex.getMessage());
				continue;
			}
			try {
				persist(session, label);
			} catch(Exception ex) {
				LOGGER.warn("Exception on label {} : can't persist label due to exception {}",label.getText(),ex.getMessage());
				continue;
			}
		}
	}
	
	private void persistRules(Session session,OldConfigMigrator migrator) {
		for(LabelRule rule : migrator.getRules(false)) {
			try {
				LabelRule dbLabelRule = queryLabelRuleByRegexp(session, rule.getRegexp());
				if(dbLabelRule != null ) {
					LOGGER.warn("Skipping rule {} as it already exists in db",rule.getRegexp());
					continue;
				}
			} catch(Exception ex) {
				LOGGER.warn("Exception on rule {} : can't query label due to exception {}",rule.getRegexp(),ex.getMessage());
				continue;
			}
			try {
				persist(session, rule);
			} catch(Exception ex) {
				LOGGER.warn("Exception on label {} : can't persist label due to exception {}",rule.getRegexp(),ex.getMessage());
				continue;
			}
		}
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
		InputStream inputStream = new FileInputStream(oldConfigFile);

		OldConfigReader reader = new OldConfigReader(new ObjectMapper(new YAMLFactory()));
		OldConfig oldConfig = reader.parse(inputStream);
		OldConfigMigrator migrator = new OldConfigMigrator(oldConfig);
		
		Session session = configureHibernateSession(bootstrap,configuration);
		persistLabels(session,migrator);
		persistRules(session,migrator);
		session.close();
	}
}