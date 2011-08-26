package ws.antonov.config.provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;
import java.util.List;

/**
 */
public class HttpConfigProvider extends AbstractConfigProvider{
  private String baseUrl;
  private HttpClient httpClient;

  private static final String CONTENT_TYPE = "Content-Type";

  public HttpConfigProvider(String baseUrl, HttpClient httpClient) {
    super(null);
    this.baseUrl = baseUrl;
    this.httpClient = httpClient;
  }

  public HttpConfigProvider(String baseUrl, HttpClient httpClient, ExtensionRegistry registry) {
    super(registry);
    this.baseUrl = baseUrl;
    this.httpClient = httpClient;
  }

  @Override
  public Message.Builder retrieveConfigData(Class<? extends Message> configClass,
                                            List<ConfigParamsBuilder.ConfigParamEntry> configParams) throws IOException {
    try {
      HttpMethod get = createHttpMethod(computeUrlDestinationFromParams(configParams));
      int result = httpClient.executeMethod(get);
      if (result == 200) {
        ContentType contentType = determineContentType(get);
        return convertMessage(configClass, contentType, get.getResponseBodyAsStream());
      }
      throw new RuntimeException("Did not receive 200-OK response from config provider at " + get.getURI() + ", but got " + result + " instead!");
    } catch (Exception e) {
        throw new IOException("Unable to load requested config", e);
    }
  }

  public String computeUrlDestinationFromParams(List<ConfigParamsBuilder.ConfigParamEntry> configParams) {
        StringBuilder builder = new StringBuilder(this.baseUrl);
        for (ConfigParamsBuilder.ConfigParamEntry o : configParams) {
            builder.append("/").append(o.getValue());
        }
        return builder.toString();
  }

  protected ContentType determineContentType(HttpMethod getMethod) throws IOException {
        Header configContentType = getMethod.getResponseHeader(CONTENT_TYPE);
        Header configServerKind = getMethod.getResponseHeader("Server");
        if (configContentType.getValue().endsWith("xml"))
            return ContentType.XML;
        else if (configServerKind.getValue().contains("CouchDB"))
            return ContentType.COUCHDB;
        else if (configContentType.getValue().endsWith("js") || configContentType.getValue().endsWith("json"))
            return ContentType.JSON;
        else if (configContentType.getValue().endsWith("text/plain"))
            return ContentType.TEXT;
        else if (configContentType.getValue().endsWith("properties"))
            return ContentType.PROPS;
        else
            return ContentType.PROTOBUF;
  }

  protected HttpMethod createHttpMethod(String url) {
      return new GetMethod(url);
  }
}
