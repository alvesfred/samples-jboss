package br.com.alvesfred.jms.impl;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.UDPBroadcastGroupConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.jgroups.util.UUID;

import br.com.alvesfred.jms.JmsService;
import br.com.alvesfred.util.JsonUtil;

/**
 * JMS support implementation class.
 *
 * @author alvesfred
 *
 */
public abstract class JmsServiceImpl implements JmsService {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 3869851366277033281L;

	/**
	 * Properties for broadcast/multicast jgroups messages on the cluster
	 */
	private static final String GROUP = System.getProperty("jboss.messaging.group.address");
	private static final String PORT  = System.getProperty("jboss.messaging.group.port", "9876");
	private static final String NAME  = System.getProperty("hornetq-discovery-group-name");

	// 
	// Domain.xml configuration
    /*
	<connection-factory name="RemoteConnectionFactory">
		<discovery-group-ref discovery-group-name="dg-sample"/>
	    <entries>
	        <entry name="java:/RemoteJmsXA"/>
	    </entries>
	    <ha>true</ha>
	    <block-on-acknowledge>true</block-on-acknowledge>
	    <retry-interval>1000</retry-interval>
	    <retry-interval-multiplier>1.5</retry-interval-multiplier>
	    <reconnect-attempts>-1</reconnect-attempts>
		
		<producer-window-size>100</producer-window-size>
		<consumer-window-size>100</consumer-window-size>
		<consumer-max-rate>50</consumer-max-rate>
    	<producer-max-rate>50</producer-max-rate>

	    <connection-load-balancing-policy-class-name>
    		org.hornetq.api.core.client.loadbalance.RandomConnectionLoadBalancingPolicy
    	</connection-load-balancing-policy-class-name>
    	
    	(or)
    	
	    <connection-load-balancing-policy-class-name>
    		org.hornetq.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy
    	</connection-load-balancing-policy-class-name>
    </connection-factory>
    */
	@Resource(mappedName = "java:/RemoteJmsXA")
    private ConnectionFactory cf;

	@Override
	public void sendMessage(String message) throws Exception {
		sendText(message);
	}
	
	@Override
	public void dispose() {
		// TODO
	}

	/**
	 * Send Message Text
	 *
	 * @param queue
	 * @param text
	 * @throws JMSException
	 */
	protected void sendText(Destination queue, String text) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();

			doInSession(connection, queue, text);

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException err) {
					// logger...
					err.printStackTrace();
				}
			}
		}
	}

	/**
	 * Send Text
	 *
	 * @param text
	 * @throws JMSException
	 */
	protected void sendText(String text) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();

			Destination destination = getDestination(getDestinationName());
			doInSession(connection, destination, text);

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException err) {
					// logger...
					err.printStackTrace();
				}
			}
		}
	}

	private void doInSession(Connection connection, Destination queue, String text) throws JMSException {
		/*
		  Tunning jms:

			1. Avoid AUTO_ACKNOWLEDGE.
			
			2. setDisableMessageID for MessageProducer 
			
			3. setDisableMessageTimeStamp() for MessageProducer 
			
			4. Avoid ObjectMessage 
		 */

		MessageProducer publisher = null;
		Session session = null; 
		try {
			session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
			//session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			
			publisher = session.createProducer(queue);
			
			publisher.setDisableMessageID(true);
			publisher.setDisableMessageTimestamp(true);

			publisher.setDeliveryMode(DeliveryMode.PERSISTENT);

			connection.start();

			Message msg = session.createTextMessage(text);
			// verifica se ocorreu recebimento duplicado, importantissimo no cluster!!
			msg.setStringProperty(
					org.hornetq.core.message.impl.MessageInternal.HDR_DUPLICATE_DETECTION_ID.toString(),
					UUID.randomUUID().toString());

			publisher.send(msg);

		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException err) {
					err.printStackTrace();
				}
			}

			if (publisher != null) {
				try {
					publisher.close();
				} catch (JMSException err) {
					err.printStackTrace();
				}
			}
		}
	}

	// Broacast discovery groups
	private Connection getConnectionFactoryByBroadcastFirst() throws Exception {

		try {
			UDPBroadcastGroupConfiguration udpConf = new UDPBroadcastGroupConfiguration(GROUP, new Integer(PORT), null, -1);
			DiscoveryGroupConfiguration conf       = new DiscoveryGroupConfiguration(NAME, 1000, 1000, udpConf);
			HornetQConnectionFactory factory       = HornetQJMSClient.createConnectionFactoryWithHA(conf, JMSFactoryType.QUEUE_CF);
			factory.setReconnectAttempts(-1);
			
			defineFactoryPerformance(factory);

			return factory.createConnection();

		} catch (Exception err)	{
			return this.cf.createConnection();
		}
    }

	private void defineFactoryPerformance(HornetQConnectionFactory factory) {
		//factory = HornetQClient.createClientSessionFactory(
		//		new TransportConfiguration(NettyConnectorFactory.class.getName(), params));
		factory.setPreAcknowledge(false);
		factory.setConfirmationWindowSize(-1); // 10 MB
		factory.setProducerWindowSize(-1);
		factory.setConsumerWindowSize(0);
		//factory.setAckBatchSize(1048576);
		
		// domain.xml configuration
		//factory.setBlockOnAcknowledge(false);
		//factory.setBlockOnDurableSend(false);
	}

	private Connection getConnectionFactoryByRemoteBroadcast(
			final String discoveryIp, final Integer discoveryPort, final String discoveryName) throws Exception {

		UDPBroadcastGroupConfiguration udpConf = new UDPBroadcastGroupConfiguration(discoveryIp, discoveryPort, null, -1);
		DiscoveryGroupConfiguration conf = new DiscoveryGroupConfiguration(discoveryName, 1000, 1000, udpConf);
		// XA Connection
		//HornetQConnectionFactory factory = HornetQJMSClient.createConnectionFactoryWithoutHA(conf, JMSFactoryType.QUEUE_XA_CF);
		HornetQConnectionFactory factory = HornetQJMSClient.createConnectionFactoryWithHA(conf, JMSFactoryType.QUEUE_CF);
		factory.setReconnectAttempts(-1);

		// performance
		defineFactoryPerformance(factory);

		return factory.createConnection();
	}

	private Connection getConnection() throws Exception {
		try {
			// inject
			if (!isRemoteConnection()) {
				if (!isBroacastConnection())
					//return getConnectionFactory().createConnection();
					return cf.createConnection();
				else 
					// broacast hornetq/jgroups
					return getConnectionFactoryByBroadcastFirst();
			} else {
				return getConnectionFactoryByRemoteBroadcast(
						getRemoteIpBroadcast(), getRemotePort(), getRemoteName());
			}

		} catch (Exception err)	{
			return this.cf.createConnection();
		}
	}

	// -----------------------------------------------------------------------
	protected String getRemoteIpBroadcast() {
		return GROUP;
	}

	protected Integer getRemotePort() {
		return new Integer(PORT);
	}

	protected String getRemoteName() {
		return NAME;
	}
	// -----------------------------------------------------------------------

	// Queue destination
	protected Destination getDestination(String jndiDestination) {
		Destination queue = (Destination) HornetQJMSClient.createQueue(jndiDestination);
		return queue;
	}

	@Override
	public String getDestinationName() {
		throw new UnsupportedOperationException("Invalid Operation!");
	}

	/**
	 * Envia para a fila especificada do calculo de media
	 *
	 * @param queue
	 * @param msg
	 * @throws Exception
	 */
	protected void send(String queue, Serializable msg) throws Exception {
		String message = JsonUtil.objectToJson(msg);
		this.sendText(getDestination(queue), message);
	}

	/**
	 * broadcast connection support
	 *
	 * @return
	 */
	protected boolean isBroacastConnection() {
		return false;
	}

	/**
	 * Remote connection - cross profiles
	 *
	 * @return
	 */
	protected boolean isRemoteConnection() {
		return false;
	}

}
