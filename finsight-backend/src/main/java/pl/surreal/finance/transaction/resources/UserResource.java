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

package pl.surreal.finance.transaction.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.validation.Validated;
import io.swagger.annotations.*;
import pl.surreal.finance.transaction.api.UserApi;
import pl.surreal.finance.transaction.api.validation.UserPostChecks;
import pl.surreal.finance.transaction.core.security.Role;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.RoleDAO;
import pl.surreal.finance.transaction.db.UserDAO;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/users")
@Api(value = "users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource
{
    private UserDAO userDAO;
    private RoleDAO roleDAO;

    public UserResource(UserDAO userDAO,RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

    private UserApi mapDomainToApi(User user) {
        UserApi userApi = new UserApi();
        userApi.setId(user.getId());
        userApi.setName(user.getName());
        userApi.setFirstName(user.getFirstName());
        userApi.setLastName(user.getLastName());
        userApi.setActive(user.isActive());
        List<Long> roleIds = new ArrayList<>();
        for(Role role : user.getRoles()) {
            roleIds.add(role.getId());
        }
        userApi.setRoleIds(roleIds);
        return userApi;
    }

    public User mapApiToDomain(UserApi userApi,User user) throws NotFoundException {
        if(user==null) {
            user = new User();
        }
        user.setName(userApi.getName());
        if(userApi.getSecret()!=null) {
            user.setSecret(userApi.getSecret());
        }
        user.setActive(userApi.isActive());
        user.setFirstName(userApi.getFirstName());
        user.setLastName(userApi.getLastName());
        List<Role> roles = new ArrayList<>();
        for(Long roleId : userApi.getRoleIds()) {
            Role role = roleDAO.findById(roleId).orElseThrow(() -> new NotFoundException("Role "+roleId+" not found."));
            roles.add(role);
        }
        user.setRoles(roles);
        return user;
    }

    @GET
    @UnitOfWork
    @Timed
    @ApiOperation(value = "Get all users")
    public List<UserApi> get() {
        List<UserApi> apiUsers = new ArrayList<>();
        for(User user: userDAO.findAll()) {
            UserApi userApi = mapDomainToApi(user);
            apiUsers.add(userApi);
        }
        return apiUsers;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Get user by id")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "User not found.") })
    public UserApi getById(@ApiParam(value = "id of an user", required = true) @PathParam("id") LongParam id) {
        User user = userDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("User not found."));
        UserApi userApi = mapDomainToApi(user);
        return userApi;
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Replace user data")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "User not found.") })
    public UserApi replace(@ApiParam(value = "if of a user to be replaced", required = true) @PathParam("id") LongParam id,
                           @ApiParam(value = "target user data", required = true) @Valid UserApi userApi) {
        User user = userDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("User not found."));
        mapApiToDomain(userApi, user);
        userDAO.create(user);
        return userApi;
    }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Create new user")
    public UserApi create(@ApiParam(value = "new user data", required = true) @Validated(UserPostChecks.class) UserApi userApi) {
        User userToCreate = mapApiToDomain(userApi,null);
        User user = userDAO.create(userToCreate);
        userApi.setId(user.getId());
        return userApi;
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Remove user by id")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "User not found.") } )
    public Response delete(@ApiParam(value = "id of an user", required = true) @PathParam("id") LongParam id) {
        User user = userDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("User not found."));
        userDAO.delete(user);
        return Response.ok().build();
    }
}
