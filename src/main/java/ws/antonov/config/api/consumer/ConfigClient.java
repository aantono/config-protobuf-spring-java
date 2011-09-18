package ws.antonov.config.api.consumer;

import ws.antonov.config.api.provider.ConfigProvider;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public interface ConfigClient {
    public <U extends com.google.protobuf.Message> U getConfig(Class<U> configClass, ConfigParamsBuilder.ConfigParamsMap configParams);

    public ConfigProvider getConfigProvider();
}
