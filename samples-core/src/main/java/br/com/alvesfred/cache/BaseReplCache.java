package br.com.alvesfred.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgroups.blocks.Cache.Value;
import org.jgroups.blocks.ReplCache;

import br.com.alvesfred.util.ObjectSizeMemory;

/**
 * Base ReplCache - jgroups for 5MB.
 *
 * @author alvesfred
 *
 */
public abstract class BaseReplCache<K, E> extends SampleCache<K, E> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 20347512480097754L;

	private ReplCache<K, E> cache;

	// listener
	private ReplCache.ChangeListener listener;

	private Set<CachingHandler<K, E>> handlers;

	/**
	 * Cache
	 *
	 * @param cacheName
	 * @return
	 */
	protected synchronized ReplCache<K, E> getCache() throws Exception {
		if (cache == null) {
			cache = new ReplCache<K, E>(
					XML_JGROUPS_UDP,
					CLUSTER_CACHE_NAME);

			cache.start();
		}

		return cache;
	}

	@Override
	public E get(K key) {
		return cache.get(key);
	}

	@Override
	public void put(K key, E value) {
		//cache.put(key, value);
		cache.put(key, value, new Short("-1"), 0, true);
	}

	@Override
	public void print() {
		StringBuffer sb = new StringBuffer("\n REPL CACHE JGROUPS VALUES: ");
		sb.append("\n ReplCache no node: "
				+ cache.getLocalAddressAsString());
		sb.append("\n ReplCache cluster size: " + cache.getClusterSize()
				+ " - ClusterName: " + cache.getClusterName());
		sb.append("\n ReplCache DefaultReplicationCount: "
				+ cache.getDefaultReplicationCount() + " - CachingTime: "
				+ cache.getCachingTime());
		sb.append("\n ReplCache isMigrateData: " + cache.isMigrateData());

		// L2 - cache
		Set<Map.Entry<K, Value<ReplCache.Value<E>>>> entries = new HashSet<>(
				cache.getL2Cache().entrySet());

		for (Map.Entry<K, Value<ReplCache.Value<E>>> e : entries) {
			sb.append("\n KEY: " + e.getKey() + " - VALUE: "
					+ e.getValue().getValue().getVal() + " - InsertionTime: "
					+ e.getValue().getInsertionTime());
		}

		sb.append("\n Number of Itens - ReplCache L2: "
				+ cache.getL2Cache().getSize());
		sb.append("\n");

		LOGGER.fine(sb.toString());
	}

	@Override
	public void addHandler(CachingHandler<K, E> handler) {
		if (this.listener == null) {
			this.listener = new ReplCache.ChangeListener() {
				@Override
				public void changed() {
					if (handlers != null) {
						for (CachingHandler<K, E> handler : handlers) {
							handler.modified();
						}
					}
				}
			};

			cache.addChangeListener(listener);
		}

		if (handlers == null) {
			handlers = new HashSet<>();
		}
		handlers.add(handler);
	}

	@Override
	public void destroy() {
		if (cache != null) {
			if (handlers != null) {
				handlers.clear();
			}

			listener = null;

			cache.stop();
		}

		LOGGER.info("Destroy cache!");
	}

	@Override
	public void clear() {
		if (cache != null) {
			cache.clear();
		}
	}

	@Override
	public int sizeKeys() {
		return this.cache.getL2Cache().getSize();
	}

	@Override
	public int sizeValues() {
		Set<Map.Entry<K, Value<ReplCache.Value<E>>>> entries = new HashSet<>(
				cache.getL2Cache().entrySet());

		int count = 0;
		for (Map.Entry<K, Value<ReplCache.Value<E>>> e : entries) {
			E value = e.getValue().getValue().getVal();
			if (value != null) {
				if (value instanceof Collection<?>) {
					count = count + ((Collection<?>) value).size();
				} else {
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
