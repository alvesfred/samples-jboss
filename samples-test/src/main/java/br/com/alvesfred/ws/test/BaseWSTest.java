package br.com.alvesfred.ws.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.cache.CacheFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alvesfred.ws.ServiceAPIException;

/**
 * Base Abstract Class for testing client resources
 *
 * @author alvesfred
 *
 */
public abstract class BaseWSTest implements Serializable {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 1112872841386409683L;

	// WS - URL Full
	protected final static String RESOURCE_WS_URL = "http://localhost:8080/rest";
	protected final static String INFO_URL        = "/info";
	
	protected final static Integer OK = 200;

	// Suppported Types
	protected final static HttpMethod[] SUPPORTED_METHODS_TYPE = 
		  { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE };

	// Logger
	private final static Logger LOGGER = Logger.getLogger(BaseWSTest.class.getName());

	/**
	 * Logger
	 *
	 * @return
	 */
	protected final static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Info log
	 *
	 * @param msg
	 */
	protected void logInfo(String msg) {
		LOGGER.info(msg);
	}

	/**
	 * Error log
	 *
	 * @param msg
	 * @param t
	 */
	protected void logError(String msg, Throwable t) {
		LOGGER.log(Level.SEVERE, msg, t);
	}

	/**
	 * Base URL
	 *
	 * @return
	 */
	protected abstract String getWSUrlBase();

	/**
	 * RestTemplate
	 *
	 * @return
	 */
	protected RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setPrettyPrint(false);
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
		messageConverter.setObjectMapper(jsonMapper);

		restTemplate.getMessageConverters().add(messageConverter);
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		restTemplate.getMessageConverters().add(new SourceHttpMessageConverter<>());
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        return restTemplate;
	}

	/**
	 * Response Status
	 *
	 * @param response
	 * @return
	 */
	protected boolean isResponseOk(ResponseEntity<?> response) {
		return response.getStatusCode() == HttpStatus.OK || 
			   response.getStatusCode() == HttpStatus.ACCEPTED;
	}

	/**
	 * Response Validation
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected boolean isResponseValid(ResponseEntity<?> response) throws Exception {
		if (response.getStatusCode() != HttpStatus.OK && 
				response.getStatusCode() != HttpStatus.ACCEPTED) {
			// return false;
			throw new ServiceAPIException("Response invalid ?: " + response.getStatusCode());
		}

		return true;
	}

	/**
	 * Info
	 *
	 * @param msg
	 * @throws Exception
	 */
	protected void info(String msg) throws Exception {
		final String infoUrl = RESOURCE_WS_URL + getWSUrlBase() + INFO_URL;

		logInfo((msg != null ? msg : "Info..."));

		ResponseEntity<String> response = getRestTemplate().getForEntity(
				infoUrl, String.class);
		isResponseValid(response);
		
		logInfo((msg != null ? msg : "") + " => Info: " + response.getBody());
	}

	/**
	 * Header Json
	 *
	 * @return
	 */
	protected <H> HttpEntity<H> createHttpEntity() throws Exception {
		final List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);

		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(mediaTypes);

		return new HttpEntity<H>(null, headers);
	}

	/**
	 * Send Object by GET
	 *
	 * @param url
	 * @param klassO
	 * @return
	 * @throws Exception
	 */
	protected <O> ResponseEntity<O> sendObjectByGet(final String url, final Class<O> klass) throws Exception {
		return getRestTemplate().exchange(
				url, HttpMethod.GET, createHttpEntity(), klass);
	}

	/**
	 * Post Object
	 *
	 * @param url
	 * @param param
	 * @param klass
	 * @param headerInfo
	 * @return
	 * @throws Exception
	 */
	protected <E, O> O sendObjectByPost(String url, E param, Class<O> klass, Map<String, String> headerInfo) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (headerInfo != null && !headerInfo.isEmpty()) {
			for (String key : headerInfo.keySet()) {
				headers.add(key, headerInfo.get(key));
			}
		}

		final HttpEntity<E> request = new HttpEntity<>(param, headers);

		return getRestTemplate().postForObject(url, request, klass);
	}

	/**
	 * Basic  Auth. method
	 * 
	 * @param user
	 * @param pwd
	 * @return
	 */
	protected final static Map<String, String> getAuthenticationBasicHeader(String user, String pwd) {
		String plainCreds       = user + ":" + pwd;
		byte[] plainCredsBytes  = plainCreds.getBytes();
		String base64Creds      = Base64.encodeBytes(plainCredsBytes);

		Map<String, String> map = new HashMap<>();
		map.put("Authorization", "Basic " + base64Creds);

		return map;
	}

	/**
	 * Fake User
	 *
	 * @return
	 */
	protected final static Map<String, String> criarCertificadoTeste() {
		return getAuthenticationBasicHeader("testWS", null);
	}

	/**
	 * Post Object
	 *
	 * @param url
	 * @param param
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	protected <E, O> O sendObjectByPostExchange(String url, E param, Class<O> klass) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final HttpEntity<E> request = new HttpEntity<>(param, headers);

		// send request and parse result
		ResponseEntity<O> response = getRestTemplate().exchange(url, HttpMethod.POST, request, klass);
		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		}

		throw new ServiceAPIException("Invalid post!");
	}

	/**
	 * Resteasy Client
	 *
	 * @param url
	 * @param header
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	protected <T, O> O sendObjectPostHttpClient(String url, T body, Map<String, String> header, Class<O> klass) throws Exception {
		// Cache on client side
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		org.jboss.resteasy.client.cache.BrowserCache cache = new org.jboss.resteasy.client.cache.LightweightBrowserCache();
		ClientRequest request = new ClientRequest(url);
		CacheFactory.makeCacheable(request, cache);

		// On server side
		//CacheControl cache = new CacheControl();
		//cache.setMustRevalidate(true);
		//return Response.ok(<obj-class>).cacheControl(cache).build();
		
		javax.ws.rs.core.MediaType mediaType = javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
		request.accept(mediaType);

		if (header != null && !header.isEmpty()) {
			for (final String key : header.keySet()) {
				request.getHeaders().add(key, header.get(key));
			}
		}

		request.body(mediaType, body);

		ClientResponse<O> response = request.post(klass);
		if (response.getStatus() != OK) {
			throw new ServiceAPIException(
					"Request Processing Failed - HTTP status: "
							+ response.getStatus());
		}

		return response.getEntity(klass);
	}

	/**
	 * Resteasy get method
	 *
	 * @param url
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	protected <T, O> O sendObjectGetHttpClient(String url, Class<O> klass) throws Exception {
		// Cache on client side
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		org.jboss.resteasy.client.cache.BrowserCache cache = new org.jboss.resteasy.client.cache.LightweightBrowserCache();
		ClientRequest request = new ClientRequest(url);
		CacheFactory.makeCacheable(request, cache);

		javax.ws.rs.core.MediaType mediaType = javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
		request.accept(mediaType);

		ClientResponse<O> response = request.get(klass);
		if (response.getStatus() != OK) {
			throw new ServiceAPIException(
					"Request Processing Failed - HTTP status: "
							+ response.getStatus());
		}

		return response.getEntity(klass);
	}

	/**
	 * Client WS
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected String sendObjectGetHttpClientJSON(String url) throws Exception {
		ClientRequest request = new ClientRequest(url);
		javax.ws.rs.core.MediaType mediaType = javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
		request.accept(mediaType);

		ClientResponse<String> response = request.get(String.class);
		if (response.getStatus() != OK) {
			throw new ServiceAPIException(
					"Request Processing Failed - HTTP status: "
							+ response.getStatus());
		}

		return response.getEntity();
	}
}
