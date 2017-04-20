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
package pl.surreal.finance.transaction.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.UserDAO;

public class UserDBAuthenticator implements Authenticator<BasicCredentials,User> {
    private UserDAO userDAO;

    public UserDBAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        java.util.Optional<User> user = userDAO.findById(basicCredentials.getUsername());
        if(user.isPresent()) {
            if(user.get().getSecret().compareTo(basicCredentials.getPassword())==0){
                return Optional.of(user.get());
            }
        }
        return Optional.absent();
    }
}