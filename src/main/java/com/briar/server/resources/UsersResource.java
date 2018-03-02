package com.briar.server.resources;

import com.briar.server.mapper.UserContactMapper;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.model.returnedtobriarclasses.BriarUser;
import com.briar.server.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/users")
@Api
public class UsersResource {

    private UserMapper userMapper;
    private UserContactMapper userContactMapper;

    public UsersResource(UserMapper userMapper, UserContactMapper userContactMapper) {
        this.userMapper = userMapper;
        this.userContactMapper = userContactMapper;
    }

//    @GET
//    @Path("/")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getUsers() {
//        UserContact userContact = new UserContact();
//        userContact.setFirstUserId(7);
//        userContact.setFirstUserContactAcceptance(true);
//        userContact.setSecondUserId(8);
//        userContact.setSecondUserContactAcceptance(true);
//        System.out.println("User contact before insertion: " + userContact);
//        userContactMapper.addNewUserContact(userContact);
//        List<UserContact> userContactList = userContactMapper.findContacts(4);
//        UserContact userContact = userContactList.get(0);
//        userContactMapper.removeSpecificUserContact(userContact);
//        return Response.status(Response.Status.CREATED).entity(userContactList).build();
//    }
//
//    @DELETE
//    @Path("/{userId}")
//    public Response deleteUser(@PathParam("userId") String phoneGeneratedId) {
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//    }
//
//    @PUT
//    @Path("/{userId}")
//    public Response updateUserIP(@PathParam("userId") String phoneGeneratedId, @Context HttpServletRequest requestContext) {
//        String ip = requestContext.getRemoteAddr();
//        return Response.status(Response.Status.CREATED).entity(this.userMapper.findAll()).build();
//    }

    @POST
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userId") String userId, User paramUser) {
        Response response;
        try {
            User user = userMapper.findUser(userId);
            if (user == null) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                boolean isPasswordValid = UserService.authenticate(user, paramUser);
                if (isPasswordValid) {
                    BriarUser returnValue = UserService.convertUserToBriarUser(user);
                    response = Response.status(Response.Status.OK).entity(returnValue).build();
                } else {
                    response = Response.status(Response.Status.UNAUTHORIZED).build();
                }
            }
        } catch (Exception e) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        Response response;
        boolean areParamsValid = UserService.validateUserParams(user);
        if (!areParamsValid) {
            response = Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            try {
                System.out.println("IN THE METHOD, TRYING TO CREATE THE USER");
                userMapper.addNewUser(user);
                System.out.println("IN THE METHOD, USER CREATED");
                BriarUser returnValue = UserService.convertUserToBriarUser(user);
                response = Response.status(Response.Status.CREATED).entity(returnValue).build();
            } catch (Exception e) {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return response;
    }

    @Path("/{userId}/contacts")
    public ContactsResource getContactsResource() {
        return new ContactsResource(userMapper);
    }


}
