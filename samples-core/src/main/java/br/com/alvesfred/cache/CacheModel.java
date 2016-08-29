package br.com.alvesfred.cache;

import java.io.Serializable;

/**
 * Cache Model Constants 
 *
 * @author alvesfred
 *
 */
public interface CacheModel<K, E> extends Serializable {

	String JMX_DOMAIN             = "org.infinispan"; 
	String XML_INFINISPAN		  = "infinispan.xml";
	String XML_JGROUPS			  = "jgroups.xml";
	String XML_JGROUPS_UDP        = "udp.xml";
	String DISTRIBUTED_CACHE_NAME = "samples-dist";
    String CACHE_NAME             = "samples-cache"; // infinispan.xml --> udp.xml
	String NODE_NAME              = "samples-node";
	String RACK_NAME              = "samples-rack";
	// Default Cluster Name Repl "ReplCache-Cluster"
	String CLUSTER_CACHE_NAME     = "samples-cluster-cache";
	String MBEAN_CACHE_MANAGER    = JMX_DOMAIN + ":type=CacheManager,name=\"DefaultCacheManager\",component=CacheManager";

   /**
    * Get 
    *
    * @param key
    * @return
    */
   E get(K key);

   /**
    * Put
    *
    * @param key
    * @param value
    * @return
    */
   void put(K key, E value);

   /**
    * Print Info
    *
    * @param cache
    */
   void print();

	/**
	 * Add Handler
	 *
	 * @return
	 */
	void addHandler(CachingHandler<K, E> handler);

	/**
	 * Destroi cache
	 *
	 */
	void destroy();

	/**
	 * Clear cache
	 */
	void clear();

	/**
	 * Keys - Size
	 *
	 * @return
	 */
	int sizeKeys();

	/**
	 * Values for Keys - Size
	 *
	 * @return
	 */
	int sizeValues();

	/**
	 * Memory Info
	 *
	 * @return
	 */
	String memoryInfo();

}
