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

package pl.surreal.finance.transaction.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import pl.surreal.finance.transaction.core.security.User;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User>
{
    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<User> findAll() {
        return list(namedQuery("pl.surreal.finance.transaction.core.security.User.findAll"));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Optional<User> findById(String name) {
        Query userQuery = namedQuery("pl.surreal.finance.transaction.core.security.User.findByName");
        userQuery.setString("name",name);
        return Optional.ofNullable(uniqueResult(userQuery));
    }

    public User create(User user) {
        return persist(user);
    }

    public void delete(User user) {
        currentSession().delete(user);
    }
}
