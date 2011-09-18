Configuration abstraction framework using Google Protocol Buffers, Spring and varous formatters
# Concepts
Configuration framework is designed to provide separation between config consumption and aquisition. The `consumer` package provides interfaces that should be coded against by the application wishing to consume the config data.  The data is returned in the form of Google Protocol Buffers `Message` objects which have been populated from various dedicated or augmenting sources (HTTP, Files, DB, etc) stored in various formats (XML, JSON, Properties, Protobuf Binary, etc).  The `provider` package provides interfaces that should be coded against by the config data retrieval providers and formatters.  The responsibility of actually interpreting the `ConfigParam` elements and deciding on where to load the actual config data from and what format conversion to use falls upon the jurisdiction of `ConfigProvider` implementations.

# Usage
## API
### ConfigClient
Configuration framework provides a main entry class into the configuration system - `ConfigClient`. 
The `ConfigClient` interface should be implemented by any class wishing to provide access to _config_.
The default implementation of `ConfigClient` provided by this framework is the `ProviderBasedConfigClient`.

### ConfigProvider
`ConfigProvider` interface is geared to provide the abstraction and a separation from the config consumption and the actual retrieval of config data.

### ConfigParamsBuilder
`ConfigParamsBuilder` is a builder class that is designed to aid in the rapid-chain-creation of the named config parameters map to be supplied to the `ConfigClient` instance to retrieve the desired config.

### ConfigClientInvocationHandler
`ConfigClientInvocationHandler` is an implementation of the Java Dynamic Proxy Invocation Handler to aid in the effort of making config consumption look and feel just like making any other service call.  It utilizes the `ConfigParam` method argument annotation to be placed on the _config interface_ definitions which will later be used to construct the `ConfigParamsMap` and passed to the underlying `ConfigClient` delegate along with the return `Message` type which is derived from the return type specified on the _config interface_.