package org.langke.dubbo.rest.service;

import org.apache.dubbo.config.annotation.Service;
import org.langke.dubbo.rest.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Service
@Path("/users")
@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
public interface UserService {
    @POST
    @Path("/register")
    Boolean registerUser(User user);

    @GET
    @Path("/{id : \\d+}")
    User getUser(@PathParam("id") Long id);

}
