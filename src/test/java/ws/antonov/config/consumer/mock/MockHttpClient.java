package ws.antonov.config.consumer.mock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;

import java.io.IOException;

/**
 */
public class MockHttpClient extends HttpClient {
  public int statusCode = 200;
  @Override
  public int executeMethod(HttpMethod method) throws IOException, HttpException {
    if (method instanceof MockHttpMethod) {
      return statusCode;
    } else {
      throw new IllegalArgumentException("Please provide an instance of MockHttpMethod as an argument!");
    }
  }
}
