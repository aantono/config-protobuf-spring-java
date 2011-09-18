package ws.antonov.config.consumer;

import com.google.protobuf.Message;
import ws.antonov.config.api.consumer.ConfigClient;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;
import ws.antonov.config.api.provider.ConfigProvider;

import java.util.*;

/**
 * @author aantonov
 * Date: 9/16/11
 * Time: 10:23 AM
 */
public class CachingConfigClientWrapper implements ConfigClient {
    private ConfigClient clientDelegate;
    private Map<CacheKey, Message> objectCache;
    private Set<CacheKey> negativeCache;

    public CachingConfigClientWrapper(ConfigClient clientDelegate) {
        this(clientDelegate, new HashMap<CacheKey, Message>(), new HashSet<CacheKey>());
    }

    public CachingConfigClientWrapper(ConfigClient clientDelegate, Map<CacheKey, Message> objectCache, Set<CacheKey> negativeCache) {
        this.clientDelegate = clientDelegate;
        this.objectCache = objectCache;
        this.negativeCache = negativeCache;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <U extends Message> U getConfig(Class<U> configClass, ConfigParamsBuilder.ConfigParamsMap configParams) {
        CacheKey key = new CacheKey(configClass, configParams);
        Message data = objectCache.get(key);
        if (data == null) {
            if (!negativeCache.contains(key)) {
                data = clientDelegate.getConfig(configClass, configParams);
                if (data != null) {
                    objectCache.put(key, data);
                } else {
                    negativeCache.add(key);
                }
            }
        }
        return (U) data;
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return clientDelegate.getConfigProvider();
    }

    public synchronized void clearCache() {
        this.objectCache.clear();
        this.negativeCache.clear();
    }

    public ConfigClient getClientDelegate() {
        return clientDelegate;
    }

    /**
     * @return an immutable version of the underlying object cache
     */
    public Map<CacheKey, Message> getObjectCache() {
        return Collections.unmodifiableMap(objectCache);
    }

    /**
     * @return an immutable version of the underlying negative/missed request cache
     */
    public Set<CacheKey> getNegativeCache() {
        return Collections.unmodifiableSet(negativeCache);
    }

    public class CacheKey {
        private Class configClass;
        private ConfigParamsBuilder.ConfigParamsMap configParams;

        public CacheKey(Class configClass, ConfigParamsBuilder.ConfigParamsMap configParams) {
            this.configClass = configClass;
            this.configParams = configParams;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (configClass != null ? !configClass.equals(cacheKey.configClass) : cacheKey.configClass != null)
                return false;
            if (configParams != null ? !configParams.equals(cacheKey.configParams) : cacheKey.configParams != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = configClass != null ? configClass.hashCode() : 0;
            result = 31 * result + (configParams != null ? configParams.hashCode() : 0);
            return result;
        }
    }
}
