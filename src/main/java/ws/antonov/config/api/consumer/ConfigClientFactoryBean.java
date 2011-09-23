package ws.antonov.config.api.consumer;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.lang.reflect.Proxy;

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
public class ConfigClientFactoryBean<T> extends AbstractFactoryBean<T> {
    private Class<T> interfaceClass;
    private ConfigClient configClient;

    public ConfigClientFactoryBean(Class<T> interfaceClass, ConfigClient configClient) {
        this.interfaceClass = interfaceClass;
        this.configClient = configClient;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    protected T createInstance() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
                new Class[]{interfaceClass}, new ConfigClientInvocationHandler(configClient));
    }
}
