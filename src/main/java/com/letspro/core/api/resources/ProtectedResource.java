package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.letspro.core.api.auth.SimplePrincipal;

@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {

    @PermitAll
    @GET
    public String showSecret(@Auth SimplePrincipal principal) {
        return String.format("Hey there, %s. You know the secret!", principal.getName());
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("admin")
    public String showAdminSecret(@Auth SimplePrincipal principal) {
        return String.format("Hey there, %s. It looks like you are an admin", principal.getName());
    }
}
