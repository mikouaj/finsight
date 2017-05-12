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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pl.surreal.finance.transaction.auth.AuthTokenAuthenticator;
import pl.surreal.finance.transaction.auth.AuthTokenGenerator;
import pl.surreal.finance.transaction.auth.JWTTokenDecoder;
import pl.surreal.finance.transaction.auth.UserDBAuthenticator;
import pl.surreal.finance.transaction.cli.GenerateCommand;
import pl.surreal.finance.transaction.cli.MigrateCommand;
import pl.surreal.finance.transaction.conf.TokenGeneratorConfiguration;
import pl.surreal.finance.transaction.core.Account;
import pl.surreal.finance.transaction.core.Card;
import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Commission;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.core.security.AuthToken;
import pl.surreal.finance.transaction.core.security.Role;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.AccountDAO;
import pl.surreal.finance.transaction.db.CardDAO;
import pl.surreal.finance.transaction.db.LabelDAO;
import pl.surreal.finance.transaction.db.LabelRuleDAO;
import pl.surreal.finance.transaction.db.RoleDAO;
import pl.surreal.finance.transaction.db.TransactionDAO;
import pl.surreal.finance.transaction.db.UserDAO;
import pl.surreal.finance.transaction.labeler.TransactionLabeler;
import pl.surreal.finance.transaction.parser.ParserFactory;
import pl.surreal.finance.transaction.resources.AccountResource;
import pl.surreal.finance.transaction.resources.AuthTokenResource;
import pl.surreal.finance.transaction.resources.CardResource;
import pl.surreal.finance.transaction.resources.LabelResource;
import pl.surreal.finance.transaction.resources.LabelRuleResource;
import pl.surreal.finance.transaction.resources.RoleResource;
import pl.surreal.finance.transaction.resources.TransactionResource;
import pl.surreal.finance.transaction.resources.UserResource;

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

		final AuthTokenAuthenticator authTokenAuthenticator =
				new UnitOfWorkAwareProxyFactory(hibernateBundle)
						.create(AuthTokenAuthenticator.class,UserDAO.class,userDAO);
		authTokenAuthenticator.addDecoder(new JWTTokenDecoder(configuration.getTokenVerifierConfiguration()));

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

		environment.jersey().register(new AuthDynamicFeature(
				new OAuthCredentialAuthFilter.Builder<User>()
						.setAuthenticator(authTokenAuthenticator)
						.setPrefix("Bearer")
						.buildAuthFilter()));

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
