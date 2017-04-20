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

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import pl.surreal.finance.transaction.api.AuthDetailsApi;
import pl.surreal.finance.transaction.api.AuthTokenApi;
import pl.surreal.finance.transaction.auth.AuthDetails;
import pl.surreal.finance.transaction.auth.IAuthTokenGenerator;
import pl.surreal.finance.transaction.core.security.AuthToken;

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
    private IAuthTokenGenerator<AuthDetails> authTokenGenerator;

    private AuthTokenApi mapDomainToApi(AuthToken token) {
        AuthTokenApi tokenApi = new AuthTokenApi();
        tokenApi.setAuthToken(token.getToken());
        return tokenApi;
    }

    public AuthTokenResource(IAuthTokenGenerator<AuthDetails> authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Create new authentication token")
    public AuthTokenApi login(@ApiParam(value = "Authentication details data", required = true) @Valid AuthDetailsApi authDetailsApi) throws ForbiddenException {
        AuthDetails authDetails = new AuthDetails(authDetailsApi.getUserName(),authDetailsApi.getUserSecret(),authDetailsApi.getAppName(),authDetailsApi.getAppSecret());
        AuthToken token = authTokenGenerator.generateToken(authDetails).orElseThrow(() -> new ForbiddenException("Forbidden"));
        return mapDomainToApi(token);
    }
}
