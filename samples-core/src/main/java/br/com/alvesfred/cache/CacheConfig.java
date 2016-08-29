package br.com.alvesfred.cache;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Cache Config
 *
 * @author alvesfred
 *
 */
@ApplicationScoped
@Named("cacheConfig")
public class CacheConfig implements Serializable {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 50872522019153443L;

	private static final Logger LOGGER =
			Logger.getLogger(CacheConfig.class.getName());

	@SuppressWarnings("rawtypes")
	@Inject
    @Any
    private Instance<BaseCache> caches;

	/**
	 * cache manager
	 *
	 * @return
	 * @throws Exception
	 */
	private GlobalConfiguration createGlobalCacheManagerConfiguration() throws Exception {
		 GlobalConfiguration gcb = GlobalConfigurationBuilder.defaultClusteredBuilder()
			.transport()
			.defaultTransport()
			.clusterName(CacheModel.CLUSTER_CACHE_NAME)
			.addProperty("configurationFile", CacheModel.XML_JGROUPS)
			.machineId(CacheModel.NODE_NAME)
			.rackId(CacheModel.RACK_NAME)
			.globalJmxStatistics().enabled(true).allowDuplicateDomains(true).build();

		 return gcb;
	}

	/**
	 * Create
	 *
	 * @return
	 * @throws Exception
	 */
	private Configuration createDefaultCacheManagerConfiguration() throws Exception {
		return new ConfigurationBuilder().clustering().cacheMode(
				CacheMode.REPL_SYNC).build();
	}

	/**
	 * Create
	 *
	 * @return
	 * @throws Exception
	 */
	private Configuration createCacheManagerClusterConfiguration() throws Exception {
		return new ConfigurationBuilder()
			.clustering()
			//.eviction().strategy(EvictionStrategy.LIRS)
			.cacheMode(CacheMode.DIST_SYNC).sync().hash().numOwners(4)
			.jmxStatistics()
			.enabled(true)
			.build();
	}
	/**
	  * Infinispan
	  *
	  * @return
	  * @throws Exception
	  */
	@Produces
	@ApplicationScoped
	@Named("embeddedCacheManager")
	public EmbeddedCacheManager createCacheManager() throws Exception {
		LOGGER.info("EmbeddedCacheManager...");

		// Validar na mesma instancia/server/jvm se ja existe o registro do cache
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		//Domain already registered org.infinispan when trying to register: type=CacheManager,name="DefaultCacheManager"
		ObjectName obj = new ObjectName(CacheModel.MBEAN_CACHE_MANAGER);
		if (mbs.isRegistered(obj)) {
			LOGGER.warning("EmbeddedCacheManager: " + 
					CacheModel.MBEAN_CACHE_MANAGER + " - MBEAN: " + mbs.getObjectInstance(obj));
		}

		// cria primeiro o default
		// vai executar sempre na mesma jvm devido ao:
		// globalJmxStatistics().enabled(true).allowDuplicateDomains(true)
		EmbeddedCacheManager cacheManager = new DefaultCacheManager(
				createGlobalCacheManagerConfiguration(),
				createDefaultCacheManagerConfiguration());

		LOGGER.info("Global (globalJmxStatistics) EmbeddedCacheManager: " + 
				 cacheManager.getCacheManagerConfiguration().globalJmxStatistics());

		for (String cacheName : cacheManager.getCacheNames()) {
			 LOGGER.warning("Caches EmbeddedCacheManager registrados: " + 
					cacheName);
		}

		cacheManager.defineConfiguration(
				CacheModel.DISTRIBUTED_CACHE_NAME,
				createCacheManagerClusterConfiguration()
		);

		LOGGER.info("Global Confs EmbeddedCacheManager: " + 
				 cacheManager.getCacheManagerConfiguration());

		return cacheManager;
	}

	/**
	 * Destroy
	 *
	 * @param cacheManager
	 * @throws Exception
	 */
	public void destroyCacheManager(@Disposes EmbeddedCacheManager cacheManager) throws Exception {
	 
		if (caches != null) {
			@SuppressWarnings("rawtypes")
			Iterator<BaseCache> ite = caches.iterator();
			while (ite.hasNext()) {
				ite.next().destroy();
			}
		}

		cacheManager.stop();
		 
		LOGGER.warning("EmbeddedCacheManager - stop!");
	}
}
