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

package pl.surreal.finance.transaction.auth.token;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import pl.surreal.finance.transaction.conf.TokenConfiguration;
import pl.surreal.finance.transaction.conf.TokenVerifierAllowedAudienceConfiguration;
import pl.surreal.finance.transaction.conf.TokenVerifierConfiguration;

public class JWTTokenDecoder implements ITokenDecoder<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTTokenDecoder.class);
	private final HashMap<String,TokenVerifierAllowedAudienceConfiguration> allowedAudiences = new HashMap<>();
	
	public JWTTokenDecoder(TokenVerifierConfiguration config) {
        for(TokenVerifierAllowedAudienceConfiguration audienceConfig: config.getAllowedAudiences()) {
            allowedAudiences.put(audienceConfig.getName(),audienceConfig);
        }
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

    private boolean verifyToken(TokenConfiguration tokenConfig,String token) {
    	try {
    		Algorithm algorithm = null;
    		JWTSignAlgorithm algorithmCode = Enum.valueOf(JWTSignAlgorithm.class,tokenConfig.getSignAlgorithm());
    		switch(algorithmCode.getType()) {
    			case SECRET:
    				algorithm = JWTSignAlgorithm.getSecretAlgorithm(algorithmCode,tokenConfig.getSignSecret());
    				break;
    			//case KEY:
    			//	break;
				default:
					throw new IllegalArgumentException("unsupported algorithm type "+algorithmCode.getType().toString());
    		}
     		JWT.require(algorithm).build().verify(token);
     		return true;
    	} catch(IllegalArgumentException | JWTVerificationException | UnsupportedEncodingException e) {
    		LOGGER.info("verifyToken() failed to verify token due to exception '{}'",e.getMessage());
    		return false;
    	}
    }
    
	@Override
	public boolean isValid(String token) {
		try {
			DecodedJWT jwt = JWT.decode(token);
            TokenVerifierAllowedAudienceConfiguration audienceConfig = getAudienceConfig(jwt.getAudience()).orElseThrow(()->new TokenDecoderException("Audience not allowed"));
            TokenConfiguration tokenConfig = getTokenConfig(audienceConfig,jwt.getIssuer()).orElseThrow(()->new TokenDecoderException("Issuer not allowed"));
            if(tokenConfig.getType().compareTo("JWT")!=0) throw new TokenDecoderException("Configured token type is not JWT");
            return verifyToken(tokenConfig,token);
		} catch(JWTDecodeException | TokenDecoderException e) {
			LOGGER.info("isValid() failed to validate token due to exception '{}'",e.getMessage());
			return false;
		}
	}

	@Override
	public String getIssuer(String token) throws TokenDecoderException {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getIssuer();
		} catch(Exception e) {
			throw new TokenDecoderException("Unable to decode token string");
		}
	}

	@Override
	public String getSubject(String token) throws TokenDecoderException {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getSubject();
		} catch(Exception e) {
			throw new TokenDecoderException("Unable to decode token string");
		}
	}
}
