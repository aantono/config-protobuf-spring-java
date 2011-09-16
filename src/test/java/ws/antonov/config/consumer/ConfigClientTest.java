package ws.antonov.config.consumer;

import com.google.protobuf.Message;
import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpMethod;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ws.antonov.config.api.consumer.ConfigClient;
import ws.antonov.config.api.consumer.ConfigClientFactoryBean;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;
import ws.antonov.config.api.provider.ConfigProvider;
import ws.antonov.config.consumer.mock.MockHttpClient;
import ws.antonov.config.consumer.mock.MockHttpMethod;
import ws.antonov.config.provider.HttpConfigProvider;
import ws.antonov.config.provider.ResourceConfigProvider;
import ws.antonov.config.test.proto.model.FlatConfigObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author aantonov
 * Date: Oct 20, 2010
 */
public class ConfigClientTest extends TestCase {
    public void setUp() throws IOException {
        FlatConfigObject.Builder builder = FlatConfigObject.newBuilder();
        builder.setSystemCode("101");
        builder.setValidate(false);
        builder.setTimeout(10);

        FlatConfigObject msg = builder.build();
        FileOutputStream fos = new FileOutputStream("build/classes/test/config.pb");
        msg.writeTo(fos);
        fos.close();
    }

    public void testFileConfigConsumptionWithBinary() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(
                new ResourceConfigProvider("file://" + System.getProperty("user.dir", "."), "/{file}"));
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.pb").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithXml() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(
                new ResourceConfigProvider("file://" + System.getProperty("user.dir", "."), "/{file}"));
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.xml").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithJson() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(
                new ResourceConfigProvider("file://" + System.getProperty("user.dir", "."), "/{file}"));
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.json").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithProps() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(
                new ResourceConfigProvider("file://" + System.getProperty("user.dir", "."), "/{file}"));
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.properties").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testHttpCouchDbConsumption() throws Exception {
        MockHttpClient httpClient = new MockHttpClient();
        final MockHttpMethod method = new MockHttpMethod();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource json = resolver.getResource("classpath:config.couch");
        method.data = json.getInputStream();

        ConfigClient client = new ProviderBasedConfigClient(
                new HttpConfigProvider("http://domain:port/config/", "{path}", httpClient) {
                    @Override
                    protected HttpMethod createHttpMethod(String url) {
                        assertEquals(url, "http://domain:port/config/bf4919676664810d86479e997c4b86a5");
                        return method;
                    }
                });
      
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("path", "bf4919676664810d86479e997c4b86a5").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testInvocationHandlerConsumption() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(
                new ResourceConfigProvider("file://" + System.getProperty("user.dir", "."), "/{domain}/{path}"));
        ConfigClientFactoryBean<FlatConfigService> bean =
                new ConfigClientFactoryBean<FlatConfigService>(FlatConfigService.class, client);
        bean.afterPropertiesSet();
        FlatConfigService service = bean.getObject();

        FlatConfigObject config = service.getConfig("build/classes", "test/config.properties");

        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    @SuppressWarnings({"unchecked"})
    public void testCachingConfigClientWrapper() throws Exception {
        FileInputStream fis = new FileInputStream("build/classes/test/config.pb");
        final FlatConfigObject msg = FlatConfigObject.parseFrom(fis);
        final AtomicInteger accessCount = new AtomicInteger(0);
        ConfigClient client = new ConfigClient() {
            @Override
            public Message getConfig(Class configClass, ConfigParamsBuilder.ConfigParamMap configParams) {
                accessCount.incrementAndGet();
                if (configParams.size() == 0)
                    return msg;
                else
                    return null;
            }

            @Override
            public ConfigProvider getConfigProvider() {
                return null;
            }
        };
        Map objects = new HashMap();
        Set keys = new HashSet();
        CachingConfigClientWrapper cachingConfig = new CachingConfigClientWrapper(client, objects, keys);

        assertEquals(0, accessCount.get());
        assertEquals(0, cachingConfig.getObjectCache().size());
        assertEquals(0, cachingConfig.getNegativeCache().size());
        
        assertEquals(cachingConfig.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance().build()), msg);
        assertEquals(1, accessCount.get());
        assertEquals(1, cachingConfig.getObjectCache().size());
        assertEquals(0, cachingConfig.getNegativeCache().size());

        assertEquals(cachingConfig.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance().build()), msg);
        assertEquals(1, accessCount.get());
        assertEquals(1, cachingConfig.getObjectCache().size());
        assertEquals(0, cachingConfig.getNegativeCache().size());

        assertNull(cachingConfig.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("foo", "bar").build()));
        assertEquals(2, accessCount.get());
        assertEquals(1, cachingConfig.getObjectCache().size());
        assertEquals(1, cachingConfig.getNegativeCache().size());

        assertNull(cachingConfig.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("foo", "bar").build()));
        assertEquals(2, accessCount.get());
        assertEquals(1, cachingConfig.getObjectCache().size());
        assertEquals(1, cachingConfig.getNegativeCache().size());
    }
}
