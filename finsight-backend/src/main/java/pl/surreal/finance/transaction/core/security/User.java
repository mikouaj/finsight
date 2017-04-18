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

package pl.surreal.finance.transaction.core.security;

import javax.persistence.*;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@NamedQueries({
        @NamedQuery(
                name = "pl.surreal.finance.transaction.core.security.User.findAll",
                query = "SELECT u FROM User u"
        )
})
public class User implements Principal {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "secret", nullable = false)
    private String secret;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthToken> tokens = new HashSet<>();

    public User(String name, String secret) {
        this.name = name;
        this.secret = secret;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<AuthToken> getTokens() {
        return tokens;
    }

    public void addRole(Role role) {
        if(!roles.contains(role)) {
            roles.add(role);
            role.getUsers().add(this);
        }
    }

    public void removeRole(Role role) {
        if(roles.contains(role)) {
            roles.remove(role);
            role.getUsers().remove(this);
        }
    }

    public void addToken(AuthToken token) {
        if(!tokens.contains(token)) {
            tokens.add(token);
            token.setUser(this);
        }
    }

    public void removeToken(AuthToken token) {
        if(tokens.contains(token)) {
            tokens.remove(token);
            token.setUser(null);
        }
    }
}