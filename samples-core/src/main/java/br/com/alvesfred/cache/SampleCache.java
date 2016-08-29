package br.com.alvesfred.cache;

import java.util.logging.Logger;

/**
 * Base Class
 * 
 * @author alvesfred
 */
public abstract class SampleCache<K, E> implements CacheModel<K, E> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 58468815349984950L;

	protected static final Logger LOGGER = 
			Logger.getLogger(SampleCache.class.getName());

	/**
	 * Init
	 */
	protected abstract void init() throws Exception;

	/**
	 * Name
	 *
	 * @return
	 */
	protected abstract String getCacheName();

}
