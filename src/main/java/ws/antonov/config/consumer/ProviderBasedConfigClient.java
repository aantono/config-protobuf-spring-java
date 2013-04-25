package ws.antonov.config.consumer;

import com.google.protobuf.Message;
import ws.antonov.config.api.consumer.ConfigClient;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;
import ws.antonov.config.api.provider.ConfigProvider;

/**
 * Copyright 2011 Alex Antonov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * @author aantonov
 * @since 0.1
 */
public class ProviderBasedConfigClient implements ConfigClient {
    private ConfigProvider provider;

    public ProviderBasedConfigClient(ConfigProvider provider) {
        this.provider = provider;
    }

    public <U extends com.google.protobuf.Message> U getConfig(Class<U> configClass, ConfigParamsBuilder.ConfigParamsMap configParams) {
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

    public boolean reloadConfig() {
        return getConfigProvider().reloadConfigData();
    }
}
