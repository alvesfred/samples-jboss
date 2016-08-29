package br.com.alvesfred.jms.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import br.com.alvesfred.jms.BusinessJmsService;

/**
 * Business JMS Service Sample
 *
 * @author alvesfred
 *
 */
@ApplicationScoped
@Named("businessJmsServiceImpl")
public class BusinessJmsServiceImpl	extends JmsServiceImpl implements BusinessJmsService {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 3412582541557318520L;

	@Override
	public String getDestinationName() {
		return DESTINATION_NAME;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
