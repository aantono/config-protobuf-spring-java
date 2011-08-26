package ws.antonov.config.consumer.mock;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;

import java.io.IOException;
import java.io.InputStream;

/**
 */
public class MockHttpMethod extends HttpMethodBase {
  public InputStream data;

  @Override
  public String getName() {
    return "MOCK";
  }

  @Override
  public Header getResponseHeader(String headerName) {
    if ("content-type".equalsIgnoreCase(headerName)) {
      return new Header("Content-Type", "text/plain");
    } else if ("server".equalsIgnoreCase(headerName)) {
      return new Header("Server", "CouchDB/0.10.0 (Erlang OTP/R13B)");
    }
    return null;
  }

  @Override
  public InputStream getResponseBodyAsStream() throws IOException {
    return data;
  }
}
