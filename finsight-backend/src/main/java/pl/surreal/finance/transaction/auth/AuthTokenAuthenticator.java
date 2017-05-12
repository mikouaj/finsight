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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.UserDAO;

public class AuthTokenAuthenticator implements Authenticator<String, User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenAuthenticator.class);
    private final UserDAO userDAO;
    private final List<ITokenDecoder<String>> decoders;

    public AuthTokenAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.decoders = new ArrayList<>();
    }

    @Override
    @UnitOfWork
    public com.google.common.base.Optional<User> authenticate(String token) throws AuthenticationException {
    	ITokenDecoder<String> decoder = null;
    	for(int i=0;i<decoders.size();i++) {
    		if(decoders.get(i).isValid(token)) {
    			decoder = decoders.get(i);
    			break;
    		}
    	}
    	if(decoder==null) {
    		LOGGER.info("authenticate() no suitable decoders found for a token"); 
    	} else {
    		try {
				Optional<User> userOpt = userDAO.findById(decoder.getSubject(token));
				if(userOpt.isPresent()) {
					return com.google.common.base.Optional.of(userOpt.get());
				}
			} catch (TokenDecoderException e) {
				LOGGER.debug("authenticate() got TokenDecoderException due to '{}'",e.getMessage()); 
			}
    	}
    	return com.google.common.base.Optional.absent();
    }
    
    public void addDecoder(ITokenDecoder<String> decoder) {
    	decoders.add(decoder);
    }

	public List<ITokenDecoder<String>> getDecoders() {
		return decoders;
	}
}
