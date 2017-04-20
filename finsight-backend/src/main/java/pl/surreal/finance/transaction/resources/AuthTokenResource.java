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

package pl.surreal.finance.transaction.resources;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.surreal.finance.transaction.api.AuthDetailsApi;
import pl.surreal.finance.transaction.api.AuthTokenApi;
import pl.surreal.finance.transaction.auth.UserDBAuthenticator;
import pl.surreal.finance.transaction.core.security.User;

import javax.validation.Valid;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/authtoken")
@Api(value = "authtoken")
@Produces(MediaType.APPLICATION_JSON)
public class AuthTokenResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenResource.class);
    private UserDBAuthenticator userDBAuthenticator;

    public AuthTokenResource(UserDBAuthenticator userDBAuthenticator) {
        this.userDBAuthenticator = userDBAuthenticator;
    }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Create new authentication token")
    public AuthTokenApi login(@ApiParam(value = "Authentication details data", required = true) @Valid AuthDetailsApi authDetailsApi) throws ForbiddenException {
        BasicCredentials basicCredentials = new BasicCredentials(authDetailsApi.getUserName(),authDetailsApi.getUserSecret());
        try {
            Optional<User> user = userDBAuthenticator.authenticate(basicCredentials);
            if(user.isPresent()) {
                AuthTokenApi authTokenApi = new AuthTokenApi();
                authTokenApi.setAuthToken("aaaaaabbbbbbccccc");
                return authTokenApi;
            }
        } catch (AuthenticationException e) {
            LOGGER.warn("login AuthenticationException due to {}",e.getMessage());
        }
        throw new ForbiddenException("Forbidden");
    }
}
