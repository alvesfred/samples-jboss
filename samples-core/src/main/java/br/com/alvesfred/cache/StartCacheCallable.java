package br.com.alvesfred.cache;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

/**
 * Start on cluster - infinispan caches
 *
 * @author alvesfred
 *
 * @param <K>
 * @param <E>
 */
public class StartCacheCallable<K, E> implements DistributedCallable<K, E, Void>, Serializable {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 8158177882733203L;

	private final String cacheName;
	private transient Cache<K, E> cache;

	public StartCacheCallable(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public Void call() throws Exception {
		cache.getCacheManager().getCache(cacheName); // start the cache
		return null;
	}

	@Override
	public void setEnvironment(Cache<K, E> cache, Set<K> inputKeys) {
		this.cache = cache;
	}

	/*
	 * DISPATCH
	 */
	/*
	DistributedExecutorService des = new DefaultExecutorService(existingCacheSuchAsDefaultCache);
	List<Future<Void>> list = des.submitEverywhere(new StartCacheCallable<K, V>(cacheName));
	for (Future<Void> future : list) {
	   try {
	      future.get(); // wait for task to complete
	   } catch (InterruptedException e) {
	   } catch (ExecutionException e) {
	   }
	}
	*/
}
