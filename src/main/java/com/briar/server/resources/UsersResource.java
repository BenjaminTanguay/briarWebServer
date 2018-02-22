package com.briar.server.resources;

import com.briar.server.mapper.UserMapper;
import com.briar.server.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.Thread.yield;

@Component
@Path("/users")
@Api
public class UsersResource {

    private UserMapper userMapper;

    public UsersResource(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get List of Users", notes = "List can be filtered by Name", response = User.class, responseContainer = "List")
    public Response getUsers() {
        //this.userMapper.findAll()
        yield();
        long id = Thread.currentThread().getId();
        return Response.status(Response.Status.CREATED).entity(id).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") String phoneGeneratedId) {
        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
    }

    @PUT
    @Path("/{userId}")
    public Response updateUserIP(@PathParam("userId") String phoneGeneratedId, @Context HttpServletRequest requestContext) {
        String ip = requestContext.getRemoteAddr();
        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createUser(User user) {
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//    }

    @Path("/{userId}/contacts")
    public ContactsResource getContactsResource() {
        return new ContactsResource(userMapper);
    }


}
