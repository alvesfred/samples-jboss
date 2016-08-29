package br.com.alvesfred.ws;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.security.smime.EnvelopedOutput;

/**
 * Base Secure Resources
 * 
 * @author alvesfred
 *
 */
public abstract class SecureResource extends ServiceRest {

	public static final String USER_CERTIFICATE = "user-certificate";
	
	private static CertificateFactory factory;

	static {
		try {
			factory = CertificateFactory.getInstance("X.509");
		} catch (Exception e) {

		}
	}

	/**
	 * {@link SecurityInterceptor}
	 * 
	 * @param
	 * @return
	 */
	protected X509Certificate extractCertificate(HttpServletRequest request) {
		try {
			InputStream is = new ByteArrayInputStream((byte[]) request.getAttribute(
					SecureResource.USER_CERTIFICATE));
			return (X509Certificate) factory.generateCertificate(is);
		} catch (CertificateException e) {
			return null;
		}
	}
	
	/**
	 * Encrypt response
	 * 
	 * @param request
	 * @param result
	 * @return
	 */
	protected EnvelopedOutput encryptResponse(HttpServletRequest request, Object result) {
		X509Certificate certificate = extractCertificate(request);
		return encryptResponse(certificate, result);
	}
	
	/**
	 * Encrypt Response
	 * 
	 * @param certificate 
	 * @param result
	 * @return
	 */
	protected EnvelopedOutput encryptResponse(X509Certificate certificate, Object result) {
		// Object user = UserContext.getUser(result);
		EnvelopedOutput output = new EnvelopedOutput(
				new Object()/* your user object */, MediaType.APPLICATION_JSON);
		output.setCertificate(certificate);

		return output;
	}

}
