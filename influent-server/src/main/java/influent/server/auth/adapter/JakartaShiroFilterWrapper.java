/*
 * Copyright 2013-2016 Uncharted Software Inc.
 *
 *  Property of Uncharted(TM), formerly Oculus Info Inc.
 *  https://uncharted.software/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package influent.server.auth.adapter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.inject.Inject;
import org.apache.shiro.guice.web.GuiceShiroFilter;

/**
 * Wrapper class that implements jakarta.servlet.Filter and delegates to Shiro's GuiceShiroFilter
 * which implements javax.servlet.Filter. This bridges the API incompatibility between jakarta and
 * javax servlet APIs.
 *
 * <p>This wrapper uses dynamic proxies to convert between jakarta and javax servlet objects at
 * runtime.
 */
public class JakartaShiroFilterWrapper implements Filter {
  private final GuiceShiroFilter shiroFilter;

  @Inject
  public JakartaShiroFilterWrapper(GuiceShiroFilter shiroFilter) {
    this.shiroFilter = shiroFilter;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Wrap jakarta FilterConfig as javax FilterConfig
    javax.servlet.FilterConfig javaxConfig =
        ServletApiAdapter.createJavaxFilterConfig(filterConfig);
    try {
      shiroFilter.init(javaxConfig);
    } catch (javax.servlet.ServletException e) {
      throw new ServletException(e.getMessage(), e);
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // Create wrapper chain that delegates back to jakarta
    javax.servlet.FilterChain javaxChain =
        (req, res) -> {
          try {
            // Convert javax objects back to jakarta for the next filter in chain
            ServletRequest jakartaReq = ServletApiAdapter.toJakartaServletRequest(req);
            ServletResponse jakartaRes = ServletApiAdapter.toJakartaServletResponse(res);
            chain.doFilter(jakartaReq, jakartaRes);
          } catch (ServletException e) {
            throw new javax.servlet.ServletException(e.getMessage(), e);
          }
        };

    try {
      // Convert jakarta objects to javax for Shiro
      javax.servlet.ServletRequest javaxRequest =
          ServletApiAdapter.createJavaxServletRequest(request);
      javax.servlet.ServletResponse javaxResponse =
          ServletApiAdapter.createJavaxServletResponse(response);

      shiroFilter.doFilter(javaxRequest, javaxResponse, javaxChain);
    } catch (javax.servlet.ServletException e) {
      throw new ServletException(e.getMessage(), e);
    }
  }

  @Override
  public void destroy() {
    shiroFilter.destroy();
  }
}
