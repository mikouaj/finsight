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
import org.hibernate.SessionFactory;
import pl.surreal.finance.transaction.core.security.Role;

import java.util.List;
import java.util.Optional;

public class RoleDAO extends AbstractDAO<Role>
{
    public RoleDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Role> findAll() {
        return list(namedQuery("pl.surreal.finance.transaction.core.security.Role.findAll"));
    }

    public Optional<Role> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public Role create(Role role) {
        return persist(role);
    }

    public void delete(Role role) {
        currentSession().delete(role);
    }
}
