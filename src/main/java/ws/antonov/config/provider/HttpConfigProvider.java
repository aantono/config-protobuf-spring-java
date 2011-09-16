package ws.antonov.config.provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.web.util.UriTemplate;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;

/**
 */
public class HttpConfigProvider extends AbstractConfigProvider{
  private HttpClient httpClient;
  private UriTemplate template;

  private static final String CONTENT_TYPE = "Content-Type";

    /**
     * The <code>baseUrl</code> will be joined with <code>pattern</code> to produce a full Uri Template for interpretation
     *
     * @param baseUrl - base url of the config repository
     * @param pattern - config pattern in the form of {name} for variable replacement to be used in <class>UriTemplate</class>
     * @param httpClient - underlying Http Client to be used to make the resource request retrievals
     * @see UriTemplate
     */
  public HttpConfigProvider(String baseUrl, String pattern, HttpClient httpClient) {
    this(baseUrl, pattern, httpClient, null);
  }

    /**
     * The <code>baseUrl</code> will be joined with <code>pattern</code> to produce a full Uri Template for interpretation
     *
     * @param baseUrl - base url of the config repository
     * @param pattern - config pattern in the form of {name} for variable replacement to be used in <class>UriTemplate</class>
     * @param httpClient - underlying Http Client to be used to make the resource request retrievals
     * @param registry - extension registry with protobuf extensions
     * @see UriTemplate
     */
  public HttpConfigProvider(String baseUrl, String pattern, HttpClient httpClient, ExtensionRegistry registry) {
    super(registry);
    this.httpClient = httpClient;
    this.template = new UriTemplate(baseUrl + pattern);
  }

  @Override
  public Message.Builder retrieveConfigData(Class<? extends Message> configClass,
                                            ConfigParamsBuilder.ConfigParamMap configParams) throws IOException {
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

  public String computeUrlDestinationFromParams(ConfigParamsBuilder.ConfigParamMap configParams) {
        return template.expand(configParams).toString();
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
