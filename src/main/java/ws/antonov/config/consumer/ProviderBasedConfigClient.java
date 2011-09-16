package ws.antonov.config.consumer;

import com.google.protobuf.Message;
import ws.antonov.config.api.consumer.ConfigClient;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;
import ws.antonov.config.api.provider.ConfigProvider;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public class ProviderBasedConfigClient implements ConfigClient {
    private ConfigProvider provider;

    public ProviderBasedConfigClient(ConfigProvider provider) {
        this.provider = provider;
    }

    public <U extends com.google.protobuf.Message> U getConfig(Class<U> configClass, ConfigParamsBuilder.ConfigParamMap configParams) {
        try {
            Message.Builder builder = getConfigProvider().retrieveConfigData(configClass, configParams);
            return (U) builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrive config", e);
        }
    }

    public ConfigProvider getConfigProvider() {
        return provider;
    }
}
