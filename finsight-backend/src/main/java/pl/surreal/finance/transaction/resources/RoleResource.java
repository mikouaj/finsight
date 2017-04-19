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
import io.swagger.annotations.*;
import pl.surreal.finance.transaction.api.RoleApi;
import pl.surreal.finance.transaction.core.security.Role;
import pl.surreal.finance.transaction.core.security.User;
import pl.surreal.finance.transaction.db.RoleDAO;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/roles")
@Api(value = "roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {
    private RoleDAO roleDAO;

    public RoleResource(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    private RoleApi mapDomainToApi(Role role) {
        RoleApi roleApi = new RoleApi();
        roleApi.setId(role.getId());
        roleApi.setName(role.getName());
        roleApi.setDescription(role.getDescription());
        List<Long> userIds = new ArrayList<>();
        for(User user : role.getUsers()) {
            userIds.add(user.getId());
        }
        roleApi.setUserIds(userIds);
        return roleApi;
    }

    public Role mapApiToDomain(RoleApi roleApi,Role role) {
        if(role==null) {
            role = new Role();
        }
        role.setName(roleApi.getName());
        role.setDescription(roleApi.getDescription());
        return role;
    }

    @GET
    @UnitOfWork
    @Timed
    @ApiOperation(value = "Get all roles")
    public List<RoleApi> get() {
        List<RoleApi> apiRoles = new ArrayList<>();
        for(Role role: roleDAO.findAll()) {
            RoleApi roleApi = mapDomainToApi(role);
            apiRoles.add(roleApi);
        }
        return apiRoles;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Get role by id")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Role not found.") })
    public RoleApi getById(@ApiParam(value = "id of a role", required = true) @PathParam("id") LongParam id) {
        Role role = roleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Role not found."));
        RoleApi roleApi = mapDomainToApi(role);
        return roleApi;
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Replace role data")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Role not found.") })
    public RoleApi replace(@ApiParam(value = "if of a role to be replaced", required = true) @PathParam("id") LongParam id,
                           @ApiParam(value = "target role data", required = true) @Valid RoleApi roleApi) {
        Role role = roleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Role not found."));
        mapApiToDomain(roleApi, role);
        roleDAO.create(role);
        return roleApi;
    }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Create new role")
    public RoleApi create(@ApiParam(value = "new role data", required = true) @Valid RoleApi roleApi) {
        Role roleToCreate = mapApiToDomain(roleApi,null);
        Role role = roleDAO.create(roleToCreate);
        roleApi.setId(role.getId());
        return roleApi;
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Remove role by id")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Role not found.") } )
    public Response delete(@ApiParam(value = "id of a role", required = true) @PathParam("id") LongParam id) {
        Role role = roleDAO.findById(id.get()).orElseThrow(() -> new NotFoundException("Role not found."));
        roleDAO.delete(role);
        return Response.ok().build();
    }
}
