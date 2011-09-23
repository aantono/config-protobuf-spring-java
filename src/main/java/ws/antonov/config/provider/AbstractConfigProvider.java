package ws.antonov.config.provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.CouchDBFormat;
import com.googlecode.protobuf.format.JavaPropsFormat;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.XmlFormat;
import ws.antonov.config.api.provider.ConfigProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

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
public abstract class AbstractConfigProvider implements ConfigProvider {
    private ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
    private static final ConcurrentHashMap<Class, Method> newBuilderMethodCache = new ConcurrentHashMap<Class, Method>();
    protected static boolean isProtobuf23;
    protected static Class extensionRegistryClass;

    static {
        try {
            extensionRegistryClass = Class.forName("com.google.protobuf.ExtensionRegistryLite");
            isProtobuf23 = true;
        } catch (ClassNotFoundException e) {
            extensionRegistryClass = com.google.protobuf.ExtensionRegistry.class;
            isProtobuf23 = false;
        }
    }

    protected AbstractConfigProvider() {
        this(null);
    }

    protected AbstractConfigProvider(ExtensionRegistry extensionRegistry) {
        if (extensionRegistry != null)
            this.extensionRegistry = extensionRegistry;
    }

    /**
     * This method takes a <code>Message</code> class and determines what <code>Message.Builder</code> needs to be instantiated.
     * After that there is a chained call to convertMessage(builder, contentType, data) is done to populate the builder with the data.
     * The returned builder object is not finalized, and can be further appended, if need be to override the values or augment them. 
     * @param returnType
     * @param contentType
     * @param data
     * @return
     * @throws Exception
     * @see ws.antonov.config.provider.AbstractConfigProvider#convertMessage(com.google.protobuf.Message.Builder, ws.antonov.config.provider.AbstractConfigProvider.ContentType, java.io.InputStream)
     */
    protected Message.Builder convertMessage(Class<? extends Message> returnType,
                                             ContentType contentType, InputStream data) throws Exception {
        Message.Builder builder;
        Method m = newBuilderMethodCache.get(returnType);
        if (m == null) {
            m = returnType.getMethod("newBuilder");
            newBuilderMethodCache.put(returnType, m);
        }
        builder = (Message.Builder) m.invoke(returnType);
        return convertMessage(builder, contentType, data);
    }

    protected Message.Builder convertMessage(Message.Builder builder,
                                             ContentType contentType, InputStream data) throws Exception {
        switch (contentType) {
            case JSON:
                return populateJsonMessage(builder, data);
            case COUCHDB:
                return populateCouchDBMessage(builder, data);
            case PROPS:
                return populatePropertiesMessage(builder, data);
            case TEXT:
                return populateTextMessage(builder, data);
            case XML:
                return populateXmlMessage(builder, data);
            case PROTOBUF:
            default:
                return populateBinaryMessage(builder, data);
        }
    }

    protected Message.Builder populateXmlMessage(Message.Builder builder, InputStream data) throws Exception {
        XmlFormat.merge(new InputStreamReader(data), builder);
        return builder;
    }

    protected Message.Builder populateTextMessage(Message.Builder builder, InputStream data) throws Exception {
        TextFormat.merge(new InputStreamReader(data), builder);
        return builder;
    }

    protected Message.Builder populatePropertiesMessage(Message.Builder builder, InputStream data) throws Exception {
        JavaPropsFormat.merge(new InputStreamReader(data), builder);
        return builder;
    }

    protected Message.Builder populateJsonMessage(Message.Builder builder, InputStream data) throws Exception {
        JsonFormat.merge(new InputStreamReader(data), builder);
        return builder;
    }

    protected Message.Builder populateCouchDBMessage(Message.Builder builder, InputStream data) throws Exception {
        CouchDBFormat.merge(new InputStreamReader(data), builder);
        return builder;
    }

    protected Message.Builder populateBinaryMessage(Message.Builder builder, InputStream data) throws Exception {
        if (data != null && data.available() > 0) {
            builder.mergeFrom(data, extensionRegistry);
        }
        return builder;
    }

    public enum ContentType {
        PROTOBUF, JSON, XML, TEXT, PROPS, COUCHDB
    }
}
