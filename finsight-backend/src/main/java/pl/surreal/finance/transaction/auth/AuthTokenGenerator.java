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

package pl.surreal.finance.transaction.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.surreal.finance.transaction.core.security.AuthToken;
import pl.surreal.finance.transaction.core.security.User;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class AuthTokenGenerator implements IAuthTokenGenerator<AuthDetails> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenGenerator.class);
    private String issuer = "transaction-backend";
    private long tokenLifeMilis = 900000L;
    private Authenticator<BasicCredentials,User> authenticator;
    private HashMap<String,String> allowedApps = new HashMap<>();

    public AuthTokenGenerator(Authenticator<BasicCredentials,User> authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Optional<AuthToken> generateToken(AuthDetails authDetails) {
        AuthToken authToken=null;
        try {
            if(!allowedApps.containsKey(authDetails.getAppName()) || allowedApps.get(authDetails.getAppName()).compareTo(authDetails.getAppSecret())!=0) {
                throw new Exception("Application verification failed");
            }

            com.google.common.base.Optional<User> user = authenticator.authenticate(authDetails);
            if(user.isPresent()) {
                Algorithm algorithm = Algorithm.HMAC256(authDetails.getAppSecret());
                String tokenString = JWT.create()
                        .withIssuer(issuer)
                        .withAudience(authDetails.getAppName())
                        .withSubject(user.get().getName())
                        .withExpiresAt(new Date(Calendar.getInstance().getTime().getTime()+tokenLifeMilis))
                        .sign(algorithm);

                authToken = new AuthToken(tokenString);
            }
        } catch (Exception e) {
            LOGGER.warn("generateToken authenticator exception due to '{}'",e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(authToken);
    }

    public long getTokenLifeMilis() {
        return tokenLifeMilis;
    }

    public void setTokenLifeMilis(long tokenLifeMilis) {
        this.tokenLifeMilis = tokenLifeMilis;
    }

    public HashMap<String, String> getAllowedApps() {
        return allowedApps;
    }

    public void setAllowedApps(HashMap<String, String> allowedApps) {
        this.allowedApps = allowedApps;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
