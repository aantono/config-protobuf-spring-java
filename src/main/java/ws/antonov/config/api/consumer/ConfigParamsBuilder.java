package ws.antonov.config.api.consumer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public final class ConfigParamsBuilder {
    private ConfigParamsMap params = new ConfigParamsMap();

    public ConfigParamsBuilder(){}

    public ConfigParamsBuilder(String key, Object value) {
        params.put(key, value);
    }

    public ConfigParamsBuilder addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public static ConfigParamsBuilder newInstance() {
        return new ConfigParamsBuilder();
    }

    public static ConfigParamsBuilder newInstance(String key, Object value) {
        return new ConfigParamsBuilder(key, value);
    }

    public ConfigParamsMap build() {
        ConfigParamsMap mapToReturn = params;
        mapToReturn.sealMap();
        params = new ConfigParamsMap();
        return mapToReturn;
    }

    public class ConfigParamsMap extends LinkedHashMap<String, Object> {
        private boolean sealed = false;

        void sealMap() {
            sealed = true;
        }

        @Override
        public Object put(String s, Object o) {
            if (!sealed) {
                return super.put(s, o);
            } else {
                throw createException();
            }
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> map) {
            if (!sealed) {
                super.putAll(map);
            } else {
                throw createException();
            }
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Object> stringObjectEntry) {
            if (!sealed) {
                return super.removeEldestEntry(stringObjectEntry);
            } else {
                throw createException();
            }
        }

        @Override
        public Object remove(Object o) {
            if (!sealed) {
                return super.remove(o);
            } else {
                throw createException();
            }
        }

        @Override
        public void clear() {
            if (!sealed) {
                super.clear();
            } else {
                throw createException();
            }
        }

        private UnsupportedOperationException createException() {
            return new UnsupportedOperationException("The ConfigParamsMap is sealed and can not be modified");
        }
    }
}
