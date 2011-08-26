package ws.antonov.config.api.consumer;

import java.util.*;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public final class ConfigParamsBuilder {
    private List<ConfigParamEntry> params = new LinkedList<ConfigParamEntry>();

    public ConfigParamsBuilder(){}

    public ConfigParamsBuilder(String key, Object value) {
        params.add(new ConfigParamEntry(key, value));
    }

    public ConfigParamsBuilder addParam(String key, Object value) {
        params.add(new ConfigParamEntry(key, value));
        return this;
    }

    public static ConfigParamsBuilder newInstance() {
        return new ConfigParamsBuilder();
    }

    public static ConfigParamsBuilder newInstance(String key, Object value) {
        return new ConfigParamsBuilder(key, value);
    }

    public List<ConfigParamEntry> build() {
        List<ConfigParamEntry> mapToReturn = Collections.unmodifiableList(params);
        params = new LinkedList<ConfigParamEntry>();
        return mapToReturn;
    }


    public class ConfigParamEntry {
        private String key;
        private Object value;

        private ConfigParamEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }
}
