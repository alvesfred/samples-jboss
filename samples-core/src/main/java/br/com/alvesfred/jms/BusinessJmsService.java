package br.com.alvesfred.jms;

/**
 * Business JMS Service
 * 
 * @author alvesfred
 */
public interface BusinessJmsService extends JmsService {

	String DESTINATION_NAME = "sample";
	String DESTINATION      = "java:jboss/jms/queue/" + DESTINATION_NAME;
}
