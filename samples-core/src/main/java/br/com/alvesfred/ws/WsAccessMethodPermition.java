package br.com.alvesfred.ws;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Access Method annotation
 *
 * {@link SecurityInterceptor} 
 * 
 * @author alvesfred
 *
 */
@Documented
@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface WsAccessMethodPermition {

}
