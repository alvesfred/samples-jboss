package br.com.alvesfred.jms;

import java.io.Serializable;

/**
 * JMS Service
 * 
 * @author alvesfred
 *
 */
public interface JmsService extends Serializable {
	/**
	 * JNDI
	 */
	String JNDI       = "java:jboss/exported/jms/queue/";
	String JNDI_LOCAL = "java:jboss/jms/queue/";

	/**
	 * Send message
	 *
	 * @param message
	 * @throws Exception
	 */
	void sendMessage(String message) throws Exception;

	/**
	 * Destination
	 *
	 * @return
	 */
	String getDestinationName();

	/**
	 * Dispose block...if needed
	 */
	void dispose();
}
