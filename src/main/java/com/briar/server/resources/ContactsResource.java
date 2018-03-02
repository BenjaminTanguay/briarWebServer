package com.briar.server.resources;

import com.briar.server.mapper.UserMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ContactsResource {

    private UserMapper userMapper;

    public ContactsResource(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

//    @DELETE
//    @Path("/{contactId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response deleteUser(@PathParam("contactId") String targetContact, @PathParam("userId") String targetUser) {
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//
//    }
//
//    @GET
//    public Response getAllContacts(@PathParam("userId") String targetUser) {
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//
//    }
//
//    @POST
//    @Path("/{contactId}")
//    public Response createContact(@PathParam("contactId") String targetContact, @PathParam("userId") String targetUser) {
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//
//    }

}
