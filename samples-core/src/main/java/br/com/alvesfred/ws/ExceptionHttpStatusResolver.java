package br.com.alvesfred.ws;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Resolver
 *
 * @author <a href="mailto:fredericocerqueiraalves@gmail.com">Frederico Alves</a>
 *
 */ 
@Provider
public class ExceptionHttpStatusResolver implements ExceptionMapper<ServiceAPIException> {
 
    @Override
    public Response toResponse(ServiceAPIException exception) {
        Response.Status httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
 
        if (exception instanceof ServiceAPIException)
            httpStatus = Response.Status.BAD_REQUEST;
 
        return Response.status(httpStatus).entity(exception.getMessage()).build();
    }
}