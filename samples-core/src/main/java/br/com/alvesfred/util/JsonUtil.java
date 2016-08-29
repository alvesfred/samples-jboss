package br.com.alvesfred.util;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSon Utility Class
 *
 * @author alvesfred
 */
public class JsonUtil {

	/**
	* Object to Json
	*
	* @param object
	* @return
	* @throws IOException
	*/
	public static String objectToJson(Serializable object) throws IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return jsonMapper.writeValueAsString(object);
	}

	/**
	* JSON to Object
    *
	* @param json
	* @param type
	* @return
	* @throws IOException
	*/
	public static Object jsonToObject(String json, Class<?> type) throws IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return jsonMapper.readValue(json, type);
	}

	/**
	* JSON to object
    *
	* @param json
	* @param type
	* @return
	* @throws IOException
	*/
	public static <T> T jsonToGenericObject(String json, Class<T> type) throws IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return (T) jsonMapper.readValue(json, type);
	}
}
