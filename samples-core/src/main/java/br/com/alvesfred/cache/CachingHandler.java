package br.com.alvesfred.cache;

import java.io.Serializable;

/**
 * Handler
 * 
 * @author alvesfred
 * 
 */
public interface CachingHandler<K, E> extends Serializable {

	int ADDED   = 1;
	int UPDATED = 2;
	int REMOVED = 3;

	/**
	 * Added 
	 *
	 * @param key
	 * @param value
	 */
	void added(K key, E value);

	/**
	 * Updated
	 *
	 * @param key
	 * @param value
	 */
	void updated(K key, E value);

	/**
	 * Removed
	 *
	 * @param key
	 * @param value
	 */
	void removed(K key, E value);

	/**
	 * Modified for ReplCache
	 */
	void modified();
}
