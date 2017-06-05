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

import java.util.Optional;

import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.UserDAO;

public class BackendUserVerifier implements IUserVerifier<User> {
	private final UserDAO userDAO;
	
	public BackendUserVerifier(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public Optional<User> verify(String userString) {
		return userDAO.findById(userString);
	}
}