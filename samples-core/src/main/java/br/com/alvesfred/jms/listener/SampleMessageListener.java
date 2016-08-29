package br.com.alvesfred.jms.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import br.com.alvesfred.jms.BusinessJmsService;

/**
 * MDB Listener
 *
 * @author alvesfred
 */
@MessageDriven
(
	activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = BusinessJmsService.DESTINATION),
		//@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"), 
		//@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Client-acknowledge"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Dups_ok_acknowledge"),
		@ActivationConfigProperty(propertyName = "minSession",      propertyValue = "2"),
		@ActivationConfigProperty(propertyName = "maxSession",      propertyValue = "5"),
		@ActivationConfigProperty(propertyName="useDLQ", propertyValue="false"),
		//************************* RECONNECT PARAMERTERS  *****************************
		@ActivationConfigProperty(propertyName="reconnectAttempts", propertyValue="60"),
		@ActivationConfigProperty(propertyName="reconnectInterval", propertyValue="30")
	}, 
	messageListenerInterface = MessageListener.class
)
@TransactionManagement(value = TransactionManagementType.CONTAINER)
@TransactionAttribute(value  = TransactionAttributeType.SUPPORTS)
public class SampleMessageListener implements MessageListener {

	private static final Logger logger = 
			Logger.getLogger(SampleMessageListener.class.getName());

	@Inject
	private BusinessJmsService service;

	public void onMessage(Message message) {
		try {
			logger.info("Begin...");

			String msg = "";
			if (message instanceof TextMessage) {
				TextMessage tm = (TextMessage) message;
				msg = tm.getText();
			} else {
				throw new Exception("Invalid Message ==> " + message);
			}

			logger.info(service.toString() + " Message: " + msg);
			//service.<method>()

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Sample MDB Error!", e);
		} finally {
			logger.info("End!");
		}
	}
}