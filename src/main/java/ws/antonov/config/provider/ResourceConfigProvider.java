package ws.antonov.config.provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.util.UriTemplate;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public class ResourceConfigProvider extends AbstractConfigProvider {
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");
    private UriTemplate template;
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public ResourceConfigProvider() {
        this("file://" + System.getProperty("user.dir", "."));
    }
    public ResourceConfigProvider(ExtensionRegistry registry) {
        this("file://" + System.getProperty("user.dir", "."), registry);
    }
    public ResourceConfigProvider(String basePath) {
        this(basePath, "", null);
    }
    public ResourceConfigProvider(String basePath, String pattern) {
        this(basePath, pattern, null);
    }
    public ResourceConfigProvider(String basePath, ExtensionRegistry registry) {
        this(basePath, "", registry);
    }
    public ResourceConfigProvider(String basePath, String pattern, ExtensionRegistry registry) {
        super(registry);
        this.template = new UriTemplate(basePath + pattern);
    }

    public Message.Builder retrieveConfigData(Class<? extends Message> configClass, ConfigParamsBuilder.ConfigParamsMap configParams) throws IOException {
        Resource resource = computeResourceDestinationFromParams(configParams);
        try {
            return convertMessage(configClass, determineContentType(resource), resource.getInputStream());
        } catch (Exception e) {
            throw new IOException("Unable to load requested config from " + resource.getDescription(), e);
        } finally {
            resource.getInputStream().close();
        }
    }

    protected ContentType determineContentType(Resource configFile) throws IOException {
        if (configFile.getURL().getPath().endsWith(".xml"))
            return ContentType.XML;
        else if (configFile.getURL().getPath().endsWith(".js") || configFile.getURL().getPath().endsWith(".json"))
            return ContentType.JSON;
        else if (configFile.getURL().getPath().endsWith(".txt"))
            return ContentType.TEXT;
        else if (configFile.getURL().getPath().endsWith(".properties"))
            return ContentType.PROPS;
        else
            return ContentType.PROTOBUF;
    }

    public Resource computeResourceDestinationFromParams(ConfigParamsBuilder.ConfigParamsMap configParams) {
        String path = template.expand(configParams).toString();
        Resource resource = resourcePatternResolver.getResource(path);
        return resource;
    }

}