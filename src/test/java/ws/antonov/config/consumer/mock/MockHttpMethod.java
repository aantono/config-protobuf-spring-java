package ws.antonov.config.consumer.mock;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;

import java.io.IOException;
import java.io.InputStream;

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
