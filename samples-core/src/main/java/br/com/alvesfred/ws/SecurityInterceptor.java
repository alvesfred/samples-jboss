package br.com.alvesfred.ws;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.Base64;

/**
 * Interceptor
 *
 * @author alvesfred
 *
 */
@Provider
@ServerInterceptor
public class SecurityInterceptor implements PreProcessInterceptor, AcceptedByMethod {

    public static final String           ACESSO_WEBSERVICE      = "ACESSO_WEBSERVICE";
    private static final String          AUTHORIZATION_PROPERTY = "Authorization";
    private static final String          AUTHENTICATION_SCHEME  = "Basic";
    private static final ServerResponse  ACCESS_DENIED          = new ServerResponse("Access Denied!",     401, new Headers<Object>());
    private static final ServerResponse  ACCESS_FORBIDDEN       = new ServerResponse("Access Forbidden!",  403, new Headers<Object>());
    private static final ServerResponse  SERVER_ERROR           = new ServerResponse("Internal Error!",    500, new Headers<Object>());

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod methodInvoked) throws Failure, WebApplicationException {
    	Method method = methodInvoked.getMethod();

        if (method.isAnnotationPresent(WsAccessMethodPermition.class) || 
        		method.isAnnotationPresent(PermitAll.class)) {
            return null;
        }

        // Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            return ACCESS_FORBIDDEN;
        }

        // Get request headers
        final HttpHeaders headers = request.getHttpHeaders();

        // Fetch authorization header
        final List<String> authorization = headers.getRequestHeader(AUTHORIZATION_PROPERTY);

        // If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            return ACCESS_DENIED;
        }

        // Get encoded username and password
        final String encodedUserPassword = authorization.get(0).replaceFirst(
        		AUTHENTICATION_SCHEME.concat(" "), "");

        // Decode username and password
        String usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.decode(encodedUserPassword));
        } catch (IOException e) {
            return SERVER_ERROR;
        }

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        // Verify user access
        if (!isUserAllowed(request, username, password)) {
            return ACCESS_DENIED;
        }

        return null;
    }

    private boolean isUserAllowed(final HttpRequest request, final String username, final String password) {
        //UserWS user = new UserWS(username, password);

//        if (user != null) {
//            if (!user.getRoles().isEmpty() && !user.hasPermission()) {
//
//            	byte[] certificate = user.getCertificate();
//            	request.setAttribute(SecureResource.USER_CERTIFICATE, certificate);
//            	return true;
//            }
//        }
        return false;
    }

	@Override
    public boolean accept(@SuppressWarnings("rawtypes") Class declaring, Method method) {

        if (method.isAnnotationPresent(WsAccessMethodPermition.class) || 
        		method.isAnnotationPresent(PermitAll.class)) {
            return true;
        }

        // Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            return false;
        }

        return false;
    }
}