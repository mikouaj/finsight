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
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.surreal.finance.transaction.conf.TokenConfiguration;
import pl.surreal.finance.transaction.conf.TokenVerifierAllowedAudienceConfiguration;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.UserDAO;

import java.util.*;

public class AuthTokenAuthenticator implements Authenticator<String, User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenAuthenticator.class);
    private UserDAO userDAO;
    private HashMap<String,TokenVerifierAllowedAudienceConfiguration> allowedAudiences = new HashMap<>();

    public AuthTokenAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

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

    private boolean verifyRSAToken(String token, String algorithmCode, String[] pemKeys) {
        for(String pemKey : pemKeys) {
            try {
                Algorithm algorithm = SignAlgorithm.getAlgorithm(algorithmCode,pemKeys).orElseThrow(()->new Exception("can't obtain algorithm"));
                JWT.require(algorithm).build().verify(token);
                return true;
            } catch(Exception e) {
                LOGGER.debug("verifyRSAToken unable to verify token due to '{}'",e.getMessage());
                continue;
            }
        }
        return false;
    }

    private boolean verifySecretToken(String token, String algorithmCode, String secret) {
        try {
            Algorithm algorithm = SignAlgorithm.getAlgorithm(algorithmCode, secret).orElseThrow(() -> new Exception("can't obtain algorithm"));
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch(Exception e) {
            LOGGER.debug("verifySecretToken unable to verify token due to '{}'",e.getMessage());
            return false;
        }
    }

    @Override
    @UnitOfWork
    public com.google.common.base.Optional<User> authenticate(String token) throws AuthenticationException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            TokenVerifierAllowedAudienceConfiguration audienceConfig = getAudienceConfig(jwt.getAudience()).orElseThrow(()->new AuthTokenAuthenticatorException("None of token audiences present on allowed audiences list"));
            TokenConfiguration tokenConfig = getTokenConfig(audienceConfig,jwt.getIssuer()).orElseThrow(()->new AuthTokenAuthenticatorException("Issuer not present on token issuers list for a given audience"));

            SignAlgorithm signAlgorithm = Enum.valueOf(SignAlgorithm.class,tokenConfig.getSignAlgorithm());
            boolean tokenVerified = false;
            if(signAlgorithm.getType() == SignAlgorithm.SignAlgorithmType.KEY) {
                tokenVerified = verifyRSAToken(token,tokenConfig.getSignAlgorithm(),tokenConfig.getSignPemKeys());
            }
            if(signAlgorithm.getType() == SignAlgorithm.SignAlgorithmType.SECRET) {
                tokenVerified = verifySecretToken(token,tokenConfig.getSignAlgorithm(),tokenConfig.getSignSecret());
            }
            if(!tokenVerified) throw new AuthTokenAuthenticatorException("token verification failed");

            Optional<User> userOpt = userDAO.findById(jwt.getSubject());
            if(userOpt.isPresent()) {
                return com.google.common.base.Optional.of(userOpt.get());
            }
        } catch (AuthTokenAuthenticatorException e){
            LOGGER.warn("authenticate AuthTokenAuthenticatorException due to '{}'",e.getMessage());
        } catch(Exception e) {
            LOGGER.warn("authenticate Exception due to '{}'",e.getMessage());
            e.printStackTrace();
            throw new AuthenticationException("Unable to authenticate due to internal error");
        }
        return com.google.common.base.Optional.absent();
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
