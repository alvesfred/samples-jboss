package br.com.alvesfred.cache;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachelistener.event.TopologyChangedEvent;

/**
 * Infinispan listener
 *
 * @author alvesfred
 * 
 */
public interface CachingListener<K, E> extends Serializable {

	/**
	 * Added 
	 *
	 * @param event
	 */
	void observeAdd(CacheEntryCreatedEvent<K, E> event);

	/**
	 * Updated
	 *
	 * @param event
	 */
	void observeUpdate(CacheEntryModifiedEvent<K, E> event);

	/**
	 * Removed
	 *
	 * @param event
	 */
	void observeRemove(CacheEntryRemovedEvent<K, E> event);

	/**
	 * Topology
	 *
	 * @param event
	 */
	void observeTopologyChange(TopologyChangedEvent<K, E> event);

	/**
	 * Cache Handler add
	 */
	void addCacheHandler(CachingHandler<K, E> handler);

	/**
	 * Handlers
	 */
	Set<CachingHandler<K, E>> getHandlers();
}
