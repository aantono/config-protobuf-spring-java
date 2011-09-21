Configuration abstraction framework using Google Protocol Buffers, Spring and varous formatters
# Concepts
Configuration framework is designed to provide separation between config consumption and aquisition. The `consumer` package provides interfaces that should be coded against by the application wishing to consume the config data.  The data is returned in the form of Google Protocol Buffers `Message` objects which have been populated from various dedicated or augmenting sources (HTTP, Files, DB, etc) stored in various formats (XML, JSON, Properties, Protobuf Binary, etc).  The `provider` package provides interfaces that should be coded against by the config data retrieval providers and formatters.  The responsibility of actually interpreting the `ConfigParam` elements and deciding on where to load the actual config data from and what format conversion to use falls upon the jurisdiction of `ConfigProvider` implementations.

# Formats
The framework uses [Protobuf Java Format](http://code.google.com/p/protobuf-java-format/) library for converting XML, JSON, Text and Properties files into Protocol Buffers Messages.  `AbstractConfigProvider` provides a `convertMessage` function that is handling the conversion from the above-mentioned formats.

# Usage
## API
### ConfigClient
Configuration framework provides a main entry class into the configuration system - `ConfigClient`. 
The `ConfigClient` interface should be implemented by any class wishing to provide access to _config_.
The default implementation of `ConfigClient` provided by this framework is the `ProviderBasedConfigClient`.

### ConfigProvider
`ConfigProvider` interface is geared to provide the abstraction and a separation from the config consumption and the actual retrieval of config data.

#### ResourceConfigProvider
Provides functionality to load config files out of Spring's `[Resource](http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/core/io/Resource.html)` class, which usually represents any file system or a classpath resource.  It uses file type extension to infer content type. i.e. .xml => XML, .json => JSON, etc.

#### HttpConfigProvider
Uses Apache HttpClient to retrieve the config resources from any HTTP/S endpoint.

### ConfigParamsBuilder
`ConfigParamsBuilder` is a builder class that is designed to aid in the rapid-chain-creation of the named config parameters map to be supplied to the `ConfigClient` instance to retrieve the desired config.

### ConfigClientInvocationHandler
`ConfigClientInvocationHandler` is an implementation of the Java Dynamic Proxy Invocation Handler to aid in the effort of making config consumption look and feel just like making any other service call.  It utilizes the `ConfigParam` method argument annotation to be placed on the _config interface_ definitions which will later be used to construct the `ConfigParamsMap` and passed to the underlying `ConfigClient` delegate along with the return `Message` type which is derived from the return type specified on the _config interface_.

### ConfigClientFactoryBean
For the Spring users (which you all should be) a `ConfigClientFactoryBean` is provided to ease the creation of the dynamic proxy for the supplied _config interface_ and `ConfigClient`.

## Example
Let's imagine that we have the following Protocol Buffers definition for our config data model:

`config.proto`

```proto
package test.config;

option java_package = "ws.antonov.config.test.proto.model";
option java_outer_classname = "TestProtos";
option java_multiple_files = true;

option optimize_for = SPEED;

message FlatConfigObject {
    optional int32 timeout = 1;
    optional bool validate = 2;
    optional string system_code = 3;
}
```
as well as the following _config interface_ defined:

`FlatConfigService.java`

```java
package ws.antonov.config.consumer;

import ws.antonov.config.api.consumer.ConfigParam;
import ws.antonov.config.test.proto.model.FlatConfigObject;

/**
 */
public interface FlatConfigService {
    public FlatConfigObject getConfig(@ConfigParam("domain") String domain,
                                      @ConfigParam("path") String path);
}
```

The definition of your Spring _ApplicationContext.xml_ would be:

`ApplicationContext.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer"/>

    <bean id="configProvider" class="ws.antonov.config.provider.ResourceConfigProvider">
        <constructor-arg value="file://${user.dir}"/> <!-- Base Dir -->
        <constructor-arg value="/{domain}/{path}"/> <!-- ConfigParam name matching pattern -->
    </bean>

    <bean id="configClient" class="ws.antonov.config.consumer.ProviderBasedConfigClient">
        <constructor-arg ref="configProvider"/>
    </bean>

    <bean id="configService" class="ws.antonov.config.api.consumer.ConfigClientFactoryBean">
        <constructor-arg value="ws.antonov.config.consumer.FlatConfigService"/>
        <constructor-arg ref="configClient"/>
    </bean>
</beans>
```

The resulting _configService_ bean can be used to wire in the `FlatConfigService` object as a dependency into any other class you have in your application context.

Let's say that our configuration is stored in a properties file:

`config.properties`

```properties
timeout=10
validate=false
system_code="101"
```

The application code would consume it the following way:

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
FlatConfigService service = context.getBean(FlatConfigService.class);
FlatConfigObject config = service.getConfig("build/classes", "test/config.properties");
```

Of course, in your real application you would not couple the underlying file storage/location with the business logic, so the parameters you would pass in into the config service would be something along the lines of a business domain ('search', 'purchase', etc), or let's say a Locale ('en_US') as a separator.  The rest of the path would be hard-coded in your Spring file: `/config_files/{locale}/{domain}.properties`.  The pattern resolution is handled by Spring's `[UriTemplate](http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/util/UriTemplate.html)`.

One other ability is to provide your own facade around the `ConfigProvider` which would add some implicit _config param_ values to the map, things like application id (if one exists) or a machine IP, or anything that can be passed in as a -D start-up property.