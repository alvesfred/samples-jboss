package br.com.alvesfred.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.TopologyChanged;
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
@Listener(sync = true)
public abstract class CachingAbstractListener<K, E> implements CachingListener<K, E> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 12295928828469379L;
	
	protected static final Logger LOGGER =
			Logger.getLogger(CachingAbstractListener.class.getName());

	private Set<CachingHandler<K, E>> handlers;

	@Override
	@CacheEntryCreated
	public void observeAdd(CacheEntryCreatedEvent<K, E> event) {
		if (event.isPre())
			return;

		notifyHandler(CachingHandler.ADDED, 
				event.getKey(), event.getCache().get(event.getKey()));

		LOGGER.fine(Thread.currentThread().getName() + " - Key: " + 
    		  event.getKey() + " - ADDED on cache: " + event.getCache() + 
    		  " - VALUE: " + event.getCache().get(event.getKey()));
	}

	@Override
	@CacheEntryModified
	public void observeUpdate(CacheEntryModifiedEvent<K, E> event) {
		if (event.isPre())
			return;

		notifyHandler(CachingHandler.UPDATED, 
				event.getKey(), event.getCache().get(event.getKey()));

		LOGGER.fine(Thread.currentThread().getName() + " - Chave: " + 
	    		  event.getKey() + " - UPDATED on cache: " + event.getCache() + 
	    		  " - VALUE: " + event.getCache().get(event.getKey()));
	}

	@Override
	@CacheEntryRemoved
	public void observeRemove(CacheEntryRemovedEvent<K, E> event) {
		if (event.isPre())
			return;

		notifyHandler(CachingHandler.REMOVED, 
				event.getKey(), event.getCache().get(event.getKey()));

		LOGGER.fine(Thread.currentThread().getName() + " - Chave: " + 
	    		  event.getKey() + " - REMOVED from cache: " + event.getCache() + 
	    		  " - VALUE: " + event.getCache().get(event.getKey()));
	}

	/**
	 * Notify
	 *
	 * @param type
	 * @param key
	 * @param e
	 */
	private void notifyHandler(final int type, K key, E value) {
		
		if (handlers != null) {
			for (CachingHandler<K , E> handler : handlers) {
				switch (type) {
					case CachingHandler.ADDED: 
						handler.added(key, value);
						break;
	
					case CachingHandler.UPDATED:
						handler.updated(key, value);
						break;
	
					case CachingHandler.REMOVED:
						handler.removed(key, value);
						break;
	
					default: 
						handler.modified();
						break;
				}
			}
		}
	}

	@Override
	@TopologyChanged
	public void observeTopologyChange(TopologyChangedEvent<K, E> event) {
		if (event.isPre())
			return;

		LOGGER.fine(Thread.currentThread().getName() + " - Cache Name " + 
    		  event.getCache().getName() + " ObserveTopologyChange - Members: " + 
    		  event.getConsistentHashAtEnd().getMembers());
	}

	@Override
	public void addCacheHandler(CachingHandler<K, E> handler) {
		if (this.handlers == null) {
			this.handlers = new HashSet<>();
		}
		this.handlers.add(handler);
	}

	@Override
	public Set<CachingHandler<K, E>> getHandlers() {
		if (this.handlers == null) {
			this.handlers = new HashSet<>();
		}

		return handlers;
	}
}
