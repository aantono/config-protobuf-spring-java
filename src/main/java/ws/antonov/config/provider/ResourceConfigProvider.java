package ws.antonov.config.provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public class ResourceConfigProvider extends AbstractConfigProvider {
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");
    private String basePath;
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public ResourceConfigProvider() {
        this("file://" + System.getProperty("user.dir", "."));
    }
    public ResourceConfigProvider(ExtensionRegistry registry) {
        this("file://" + System.getProperty("user.dir", "."), registry);
    }
    public ResourceConfigProvider(String basePath) {
        this(basePath, null);
    }
    public ResourceConfigProvider(String basePath, ExtensionRegistry registry) {
        super(registry);
        this.basePath = basePath;
    }

    public Message.Builder retrieveConfigData(Class<? extends Message> configClass, ConfigParamsBuilder.ConfigParamMap configParams) throws IOException {
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

    public Resource computeResourceDestinationFromParams(ConfigParamsBuilder.ConfigParamMap configParams) {
        StringBuilder builder = new StringBuilder(this.basePath);
        for (Map.Entry<String, Object> o : configParams.entrySet()) {
            builder.append(PATH_SEPARATOR).append(o.getValue());
        }
        Resource resource = resourcePatternResolver.getResource(builder.toString());
        return resource;
    }

}