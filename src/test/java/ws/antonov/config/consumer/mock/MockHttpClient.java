package ws.antonov.config.consumer.mock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;

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
