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

package pl.surreal.finance.transaction;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import pl.surreal.finance.transaction.auth.AuthTokenGenerator;
import pl.surreal.finance.transaction.auth.UserDBAuthenticator;
import pl.surreal.finance.transaction.cli.GenerateCommand;
import pl.surreal.finance.transaction.cli.MigrateCommand;
import pl.surreal.finance.transaction.conf.TokenGeneratorConfiguration;
import pl.surreal.finance.transaction.core.*;
import pl.surreal.finance.transaction.core.security.AuthToken;
import pl.surreal.finance.transaction.core.security.Role;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.*;
import pl.surreal.finance.transaction.labeler.TransactionLabeler;
import pl.surreal.finance.transaction.parser.ParserFactory;
import pl.surreal.finance.transaction.resources.*;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class TransactionApplication extends Application<TransactionConfiguration>
{
    private final HibernateBundle<TransactionConfiguration> hibernateBundle =
            new HibernateBundle<TransactionConfiguration>(Transaction.class,Commission.class,CardOperation.class,Transfer.class,Card.class,Account.class,Label.class,LabelRule.class,User.class,Role.class,AuthToken.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TransactionConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };
            
    @Override
    public String getName() {
        return "transaction-backend";
    }
    
    @Override
    public void initialize(Bootstrap<TransactionConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addCommand(new GenerateCommand());
        bootstrap.addCommand(new MigrateCommand());
    }
	
	@Override	
	public void run(TransactionConfiguration configuration, Environment environment) throws Exception {
		final TransactionDAO dao = new TransactionDAO(hibernateBundle.getSessionFactory());
		final CardDAO cardDAO = new CardDAO(hibernateBundle.getSessionFactory());
		final AccountDAO accountDAO = new AccountDAO(hibernateBundle.getSessionFactory());
		final LabelDAO labelDAO = new LabelDAO(hibernateBundle.getSessionFactory());
		final LabelRuleDAO labelRuleDAO = new LabelRuleDAO(hibernateBundle.getSessionFactory());
		final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
		final RoleDAO roleDAO = new RoleDAO(hibernateBundle.getSessionFactory());
		
		final ParserFactory parserFactory = new ParserFactory();
		parserFactory.addResourceLookup(Account.class,accountDAO);
		parserFactory.addResourceLookup(Card.class,cardDAO);
		
		final TransactionLabeler transactionLabeler = new TransactionLabeler(labelRuleDAO);

		final UserDBAuthenticator userDBAuthenticator = new UserDBAuthenticator(userDAO);
		final AuthTokenGenerator authTokenGenerator = new AuthTokenGenerator(userDBAuthenticator);
		TokenGeneratorConfiguration tokenGeneratorConfiguration = configuration.getTokenGeneratorConfiguration();
		authTokenGenerator.setIssuer(tokenGeneratorConfiguration.getIssuerName());
		authTokenGenerator.setTokenLifeMilis(tokenGeneratorConfiguration.getTokenLife());
		authTokenGenerator.setAllowedAudiences(tokenGeneratorConfiguration.getAllowedAudiences());

		environment.jersey().register(new TransactionResource(dao,labelDAO,parserFactory,transactionLabeler));
		environment.jersey().register(new CardResource(cardDAO));
		environment.jersey().register(new AccountResource(accountDAO));
		environment.jersey().register(new LabelResource(labelDAO));
		environment.jersey().register(new LabelRuleResource(labelRuleDAO,labelDAO));
		environment.jersey().register(new UserResource(userDAO,roleDAO));
		environment.jersey().register(new RoleResource(roleDAO));
		environment.jersey().register(new AuthTokenResource(authTokenGenerator));

		environment.jersey().register(io.swagger.jaxrs.listing.ApiListingResource.class);
		environment.jersey().register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		configuration.getSwaggerConfiguration().createBeanConfig();
		
		environment.jersey().getResourceConfig().packages(getClass().getPackage().getName()).register(DeclarativeLinkingFeature.class);
		
		FilterRegistration.Dynamic corsfilter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter.class);
		corsfilter.setInitParameter("allowedOrigins", "*");
		corsfilter.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		corsfilter.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
		corsfilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	}
	
    public static void main(String[] args) throws Exception {
        new TransactionApplication().run(args);
    }
}
