package br.com.alvesfred.ws;

/**
 * Exception
 *
 * @author <a href="mailto:fredericocerqueiraalves@gmail.com">Frederico Alves</a>
 *
 */
public class ServiceAPIException extends Exception {

	private static final long serialVersionUID = -8273095191160246461L;

	/**
	 * Construtor
	 *
	 * @param message
	 */
	public ServiceAPIException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 *
	 * @param e
	 */
	public ServiceAPIException(Exception e) {
		super(e);
	}

	/**
	 * Constructor
	 *
	 * @param msg
	 * @param t
	 */
	public ServiceAPIException(String msg, Throwable t) {
		super(msg, t);
	}
}