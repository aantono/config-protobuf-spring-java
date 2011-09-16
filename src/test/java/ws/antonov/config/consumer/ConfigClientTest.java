package ws.antonov.config.consumer;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpMethod;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ws.antonov.config.api.consumer.ConfigClient;
import ws.antonov.config.api.consumer.ConfigClientFactoryBean;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;
import ws.antonov.config.consumer.mock.MockHttpClient;
import ws.antonov.config.consumer.mock.MockHttpMethod;
import ws.antonov.config.provider.HttpConfigProvider;
import ws.antonov.config.provider.ResourceConfigProvider;
import ws.antonov.config.test.proto.model.FlatConfigObject;

import java.io.FileOutputStream;
import java.io.IOException;

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
        ConfigClient client = new ProviderBasedConfigClient(new ResourceConfigProvider());
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.pb").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithXml() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(new ResourceConfigProvider());
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.xml").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithJson() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(new ResourceConfigProvider());
        FlatConfigObject config = client.getConfig(FlatConfigObject.class,
                ConfigParamsBuilder.newInstance("file", "build/classes/test/config.json").build());
        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }

    public void testFileConfigConsumptionWithProps() throws Exception {
        ConfigClient client = new ProviderBasedConfigClient(new ResourceConfigProvider());
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
                new HttpConfigProvider("http://domain:port/config/", httpClient) {
                    @Override
                    protected HttpMethod createHttpMethod(String url) {
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
        ConfigClient client = new ProviderBasedConfigClient(new ResourceConfigProvider());
        ConfigClientFactoryBean<FlatConfigService> bean =
                new ConfigClientFactoryBean<FlatConfigService>(FlatConfigService.class, client);
        bean.afterPropertiesSet();
        FlatConfigService service = bean.getObject();

        FlatConfigObject config = service.getConfig("build/classes", "test/config.properties");

        assertEquals(config.getTimeout(), 10);
        assertEquals(config.getValidate(), false);
        assertEquals(config.getSystemCode(), "101");
    }
}
