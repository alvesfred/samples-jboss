package br.com.alvesfred.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base WS Service 
 *
 * @author <a href="mailto:fredericocerqueiraalves@gmail.com">Frederico Alves</a>
 *
 */
public abstract class ServiceRest {
	// Logger
	protected static final Logger LOGGER =
			LoggerFactory.getLogger(ServiceRest.class);

	/**
	 * Handle exceptions
	 *
	 * @param msg
	 * @param t
	 * @throws ServiceAPIException
	 */
	protected void handleException(String msg, Throwable t) throws ServiceAPIException {
		LOGGER.error(msg, t);
		throw new ServiceAPIException(msg, t);
	}
}
