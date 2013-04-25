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

    public boolean reloadConfigData() {
        return true;
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