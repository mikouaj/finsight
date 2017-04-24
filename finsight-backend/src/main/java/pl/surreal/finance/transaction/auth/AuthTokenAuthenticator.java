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
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.surreal.finance.transaction.conf.TokenConfiguration;
import pl.surreal.finance.transaction.conf.TokenVerifierAllowedAudienceConfiguration;
import pl.surreal.finance.transaction.core.security.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AuthTokenAuthenticator implements Authenticator<String, User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenAuthenticator.class);
    private HashMap<String,TokenVerifierAllowedAudienceConfiguration> allowedAudiences = new HashMap<>();

    private Optional<TokenVerifierAllowedAudienceConfiguration> getAudienceConfig(List<String> tokenAudiences) {
        TokenVerifierAllowedAudienceConfiguration audience=null;
        for(String tokenAudience : tokenAudiences) {
            if(allowedAudiences.containsKey(tokenAudience)) {
                audience = allowedAudiences.get(tokenAudience);
                break;
            }
        }
        return Optional.ofNullable(audience);
    }

    private Optional<TokenConfiguration> getTokenConfig(TokenVerifierAllowedAudienceConfiguration audienceConfig, String tokenIssuer) {
        TokenConfiguration tokenConfiguration = null;
        for(TokenConfiguration tmpTokenConfig : audienceConfig.getAuthTokens()) {
            if(tmpTokenConfig.getIssuer().compareTo(tokenIssuer)==0) {
                tokenConfiguration = tmpTokenConfig;
                break;
            }
        }
        return Optional.ofNullable(tokenConfiguration);
    }

    @Override
    public com.google.common.base.Optional<User> authenticate(String token) throws AuthenticationException {
        User user = null;
        try {
            DecodedJWT jwt = JWT.decode(token);
            TokenVerifierAllowedAudienceConfiguration audienceConfig = getAudienceConfig(jwt.getAudience()).orElseThrow(()->new Exception("None of token audiences present on allowed audiences list"));
            TokenConfiguration tokenConfig = getTokenConfig(audienceConfig,jwt.getIssuer()).orElseThrow(()->new Exception("Issuer not present on token issuers list for a given audience"));
        } catch (Exception e){
            LOGGER.warn("authenticate exception due to '{}'",e.getMessage());
            throw new AuthenticationException("Unable to authenticate token");
        }
        return com.google.common.base.Optional.fromNullable(user);
    }

    public List<TokenVerifierAllowedAudienceConfiguration> getAllowedAudiences() {
        return new ArrayList<>(allowedAudiences.values());
    }

    public void setAllowedAudiences(TokenVerifierAllowedAudienceConfiguration[] audiences) {
        allowedAudiences.clear();
        for(TokenVerifierAllowedAudienceConfiguration audience: audiences) {
            allowedAudiences.put(audience.getName(),audience);
        }
    }
}
