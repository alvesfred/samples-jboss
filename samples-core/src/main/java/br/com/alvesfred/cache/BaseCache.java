package br.com.alvesfred.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;

import br.com.alvesfred.util.ObjectSizeMemory;

/**
 * Base Cache Implementation for 200k
 *
 * @author alvesfred
 *
 */
public abstract class BaseCache<K, E> extends SampleCache<K, E> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 20351248430097754L;

	@Inject
	@Named("embeddedCacheManager")
	private EmbeddedCacheManager cacheManager;
	
	// Cache Infinispan - 200k (sysctl.conf)
	private Cache<K, E> cache;

	private CachingListener<K, E> listener;

	@Override
	public E get(K key) {
		return cache.get(key);
	}

	@Override
	public void put(K key, E value) {
		cache.put(key, value);
	}

	@Override
	public void print() {
		StringBuffer sb = new StringBuffer("\n CACHE INFINISPAN VALUES: ");
		sb.append("\n Cache no node: "
				+ cache.getAdvancedCache().getRpcManager().getAddress());

		ArrayList<Map.Entry<K, E>> entries = new ArrayList<>(cache.entrySet());
		for (Map.Entry<K, E> e : entries) {
			sb.append("\n KEY: " + e.getKey() + " - VALUE: " + e.getValue());
		}

		sb.append("\n Number of itens in Cache: " + cache.size());
		sb.append("\n");

		LOGGER.fine(sb.toString());
	}

	/**
	 * Add
	 *
	 * @param listener
	 * @throws Exception
	 */
	public void setCacheListener(CachingListener<K, E> listener) throws Exception {
		if (cache != null && 
			!cache.getStatus().equals(ComponentStatus.FAILED) && 
			!cache.getStatus().equals(ComponentStatus.TERMINATED) && 
			this.listener == null) { // permitir apenas 1
			
			this.listener = listener;
			cache.addListener(listener);
		}
	}

	@Override
	public void addHandler(CachingHandler<K, E> handler) {
		if (this.listener == null) {
			throw new RuntimeException("Listener failed!");
		}

		// somente os handlers poderao ser mais de 1
		this.listener.addCacheHandler(handler);
	}

	@Override
	public void destroy() {
		if (cache != null) {
			if (listener != null &&	!listener.getHandlers().isEmpty()) {
				listener.getHandlers().clear();
			}

			listener = null;

			cache.stop();
		}

		LOGGER.info("Destroy cache!");
	}

	@Override
	public void clear() {
		if (cache != null && cache.isEmpty()) {
			cache.clear();
			//cache.clearAsync();
		}
	}

	/**
	 * Create
	 *
	 * @param cacheName
	 * @return
	 */
	protected synchronized Cache<K, E> getCache(String cacheName) throws Exception {
		if (cache == null) {
			cache = cacheManager.getCache(cacheName);

			if (!cache.getStatus().equals(ComponentStatus.RUNNING))
				cache.start();
		}

		return cache;
	}

	@Override
	public int sizeKeys() {
		return this.cache.size();
	}

	@Override
	public int sizeValues() {
		ArrayList<Map.Entry<K, E>> entries = new ArrayList<>(cache.entrySet());

		int count = 0;
		for (Map.Entry<K, E> e : entries) {
			E value = e.getValue();
			if (value != null) {
				if (value instanceof Collection<?>) {
					count = count + ((Collection<?>) value).size();
				} else {
					// precisa contar pois poderao existir chaves sem conteudo -> null
					count++;
				}
			}
		}

		return count; 
	}

	@Override
	public String memoryInfo() {
		return ObjectSizeMemory.measure(cache).toString();
	}
}
