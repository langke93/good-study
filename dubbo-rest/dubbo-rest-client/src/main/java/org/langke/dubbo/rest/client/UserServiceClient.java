package org.langke.dubbo.rest.client;

import org.apache.dubbo.config.annotation.Service;
import org.langke.dubbo.rest.bean.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * 在dubbo的REST中，我们有两种方式获取客户端IP。
 * 第一种方式，用JAX-RS标准的@Context annotation：
 * public User getUser(@PathParam("id") Long id, @Context HttpServletRequest request) {
 *     System.out.println("Client address is " + request.getRemoteAddr());
 * }
 * 第二种方式，用dubbo中常用的RpcContext：
 * public User getUser(@PathParam("id") Long id) {
 *     System.out.println("Client address is " + RpcContext.getContext().getRemoteAddressString());
 * }
 */
@Service
@Path("/users")
@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML, "text/xml; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
public interface UserServiceClient {
    @POST
    @Path("/register")
    Boolean registerUser(User user, @Context HttpServletRequest request);

    @GET
    @Path("/{id : \\d+}")
    User getUser(@PathParam("id") Long id, @Context HttpServletRequest request);

}
