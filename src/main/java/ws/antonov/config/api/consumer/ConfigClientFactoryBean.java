package ws.antonov.config.api.consumer;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.lang.reflect.Proxy;

/**
 * User: aantonov
 * Date: 6/19/11
 * Time: 8:47 PM
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
