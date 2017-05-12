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
import pl.surreal.finance.transaction.conf.TokenGeneratorAllowedAudienceConfiguration;
import pl.surreal.finance.transaction.core.security.AuthToken;
import pl.surreal.finance.transaction.core.security.User;

import java.util.*;

public class AuthTokenGenerator implements IAuthTokenGenerator<AuthDetails> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenGenerator.class);
    private Authenticator<BasicCredentials,User> authenticator;

    private String issuer = "transaction-backend";
    private long tokenLifeMilis = 900000L;
    private HashMap<String,TokenGeneratorAllowedAudienceConfiguration> allowedAudiences = new HashMap<>();

    public AuthTokenGenerator(Authenticator<BasicCredentials,User> authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Optional<AuthToken> generateToken(AuthDetails authDetails) {
        AuthToken authToken=null;
        try {
            if(!allowedAudiences.containsKey(authDetails.getAppName())) {
                throw new Exception("Application "+authDetails.getAppName()+" not present on allowed audiences list");
            }
            TokenGeneratorAllowedAudienceConfiguration audienceConfig = allowedAudiences.get(authDetails.getAppName());
            if(audienceConfig.getSignSecret().compareTo(authDetails.getAppSecret())!=0) {
                throw new Exception("Application "+authDetails.getAppName()+" bad secret");
            }

            JWTSignAlgorithm algorithmCode = Enum.valueOf(JWTSignAlgorithm.class,audienceConfig.getSignAlgorithm());
            Algorithm algorithm = Optional.ofNullable(JWTSignAlgorithm.getSecretAlgorithm(algorithmCode,authDetails.getAppSecret())).orElseThrow(()->new Exception("Cant obtain algorithm "+audienceConfig.getSignAlgorithm()+" for audience"));
            com.google.common.base.Optional<User> user = authenticator.authenticate(authDetails);
            if(user.isPresent()) {
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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getTokenLifeMilis() {
        return tokenLifeMilis;
    }

    public void setTokenLifeMilis(long tokenLifeMilis) {
        this.tokenLifeMilis = tokenLifeMilis;
    }

    public List<TokenGeneratorAllowedAudienceConfiguration> getAllowedAudiences() {
        return new ArrayList<>(allowedAudiences.values());
    }

    public void setAllowedAudiences(TokenGeneratorAllowedAudienceConfiguration[] audiences) {
        allowedAudiences.clear();
        for(TokenGeneratorAllowedAudienceConfiguration audience : audiences) {
            allowedAudiences.put(audience.getName(),audience);
        }
    }
}
