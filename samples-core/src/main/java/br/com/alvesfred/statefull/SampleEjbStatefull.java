package br.com.alvesfred.statefull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

/**
 * Sample for EJB Stateful
 *
 * @author fred
 *
 */
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class SampleEjbStatefull {

	// User transaction for EJB Bean Managed Transaction (BMT)
	@Resource
	private UserTransaction userTransaction;

	//@Resource
	//private EJBContext context;
	//UserTransaction utx = context.getUserTransaction();
	/**
	 * Using EntityManager / JPA or Hibernate

		EntityTransaction tx = null;
		try {
		    tx = em.getTransaction();
		    tx.begin();
	
		    // do some work
		    ...

		    tx.commit();
		}
		catch (RuntimeException e) {
		    if ( tx != null && tx.isActive() ) tx.rollback();
		    throw e; // or display error message
		}
		finally {
		    em.close();
		}
	 */

	/**
	 * Store messages
	 */
	private List<String> messages;

	public SampleEjbStatefull() {
	}

	@PostConstruct
	public void init() {
		System.out.println(" Post construct class...");
		messages = new ArrayList<>();
		System.out.println(" Messages created!");
	}

	@PreDestroy
	public void destroy() {
		System.out.println(" Destroy Bean!");
	}

	@Remove
	public void remove() {
		System.out.println(" Remove Bean!");
	}

	@AfterBegin
	private void afterBegin() {
		System.out.println(" After begin...");
	}

	@BeforeCompletion
	private void beforeCompletion() {
		System.out.println(" Before completion...");
	}

	@AfterCompletion
	private void afterCompletion(boolean committed) {
		System.out.println(" After completion...");
	}

	public void addMessage(String message) {
		getMessages().add(message);
		System.out.println(message + " message added");
	}

	public void removeMessage(String message) {
		getMessages().remove(message);
		System.out.println(message + " message removed!");
	}

	public List<String> getMessages() {
		return messages;
	}

}
