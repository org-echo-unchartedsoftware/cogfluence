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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for adapting between jakarta.servlet.* and javax.servlet.* APIs using dynamic
 * proxies. This allows Shiro (which uses javax.servlet) to work with Guice 7.x (which uses
 * jakarta.servlet).
 */
public class ServletApiAdapter {
  private static final Map<Object, Object> adapterCache = new ConcurrentHashMap<>();

  /**
   * Creates a javax.servlet.FilterConfig proxy that delegates to a jakarta.servlet.FilterConfig.
   */
  public static javax.servlet.FilterConfig createJavaxFilterConfig(
      jakarta.servlet.FilterConfig jakartaConfig) {
    return (javax.servlet.FilterConfig)
        createProxy(
            jakartaConfig, javax.servlet.FilterConfig.class, jakarta.servlet.FilterConfig.class);
  }

  /**
   * Creates a javax.servlet.ServletRequest proxy that delegates to a
   * jakarta.servlet.ServletRequest.
   */
  public static javax.servlet.ServletRequest createJavaxServletRequest(
      jakarta.servlet.ServletRequest jakartaRequest) {
    // Handle HttpServletRequest specifically
    if (jakartaRequest instanceof jakarta.servlet.http.HttpServletRequest) {
      return (javax.servlet.ServletRequest)
          createProxy(
              jakartaRequest,
              javax.servlet.http.HttpServletRequest.class,
              jakarta.servlet.http.HttpServletRequest.class);
    }
    return (javax.servlet.ServletRequest)
        createProxy(
            jakartaRequest,
            javax.servlet.ServletRequest.class,
            jakarta.servlet.ServletRequest.class);
  }

  /**
   * Creates a javax.servlet.ServletResponse proxy that delegates to a
   * jakarta.servlet.ServletResponse.
   */
  public static javax.servlet.ServletResponse createJavaxServletResponse(
      jakarta.servlet.ServletResponse jakartaResponse) {
    // Handle HttpServletResponse specifically
    if (jakartaResponse instanceof jakarta.servlet.http.HttpServletResponse) {
      return (javax.servlet.ServletResponse)
          createProxy(
              jakartaResponse,
              javax.servlet.http.HttpServletResponse.class,
              jakarta.servlet.http.HttpServletResponse.class);
    }
    return (javax.servlet.ServletResponse)
        createProxy(
            jakartaResponse,
            javax.servlet.ServletResponse.class,
            jakarta.servlet.ServletResponse.class);
  }

  /**
   * Converts a javax.servlet.ServletRequest back to a jakarta.servlet.ServletRequest. If the
   * request was created by this adapter, returns the original jakarta object.
   */
  public static jakarta.servlet.ServletRequest toJakartaServletRequest(
      javax.servlet.ServletRequest javaxRequest) {
    // If this is our proxy, extract the original jakarta object
    if (Proxy.isProxyClass(javaxRequest.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(javaxRequest);
      if (handler instanceof ServletApiInvocationHandler) {
        Object target = ((ServletApiInvocationHandler) handler).getTarget();
        if (target instanceof jakarta.servlet.ServletRequest) {
          return (jakarta.servlet.ServletRequest) target;
        }
      }
    }
    // Otherwise, we need to wrap it the other direction
    return (jakarta.servlet.ServletRequest)
        createProxy(
            javaxRequest,
            jakarta.servlet.ServletRequest.class,
            javax.servlet.ServletRequest.class);
  }

  /**
   * Converts a javax.servlet.ServletResponse back to a jakarta.servlet.ServletResponse. If the
   * response was created by this adapter, returns the original jakarta object.
   */
  public static jakarta.servlet.ServletResponse toJakartaServletResponse(
      javax.servlet.ServletResponse javaxResponse) {
    // If this is our proxy, extract the original jakarta object
    if (Proxy.isProxyClass(javaxResponse.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(javaxResponse);
      if (handler instanceof ServletApiInvocationHandler) {
        Object target = ((ServletApiInvocationHandler) handler).getTarget();
        if (target instanceof jakarta.servlet.ServletResponse) {
          return (jakarta.servlet.ServletResponse) target;
        }
      }
    }
    // Otherwise, we need to wrap it the other direction
    return (jakarta.servlet.ServletResponse)
        createProxy(
            javaxResponse,
            jakarta.servlet.ServletResponse.class,
            javax.servlet.ServletResponse.class);
  }

  /**
   * Creates a javax.servlet.ServletContext proxy that delegates to a
   * jakarta.servlet.ServletContext.
   */
  public static javax.servlet.ServletContext createJavaxServletContext(
      jakarta.servlet.ServletContext jakartaContext) {
    return (javax.servlet.ServletContext)
        createProxy(
            jakartaContext,
            javax.servlet.ServletContext.class,
            jakarta.servlet.ServletContext.class);
  }

  /**
   * Generic method to create a proxy that adapts between javax and jakarta APIs. The proxy
   * implements the targetInterface and delegates calls to the source object using reflection.
   */
  private static Object createProxy(Object source, Class<?> targetInterface, Class<?> sourceInterface) {
    if (source == null) {
      return null;
    }

    // Check cache first
    Object cached = adapterCache.get(source);
    if (cached != null && targetInterface.isInstance(cached)) {
      return cached;
    }

    Object proxy =
        Proxy.newProxyInstance(
            targetInterface.getClassLoader(),
            new Class<?>[] {targetInterface},
            new ServletApiInvocationHandler(source, sourceInterface));

    adapterCache.put(source, proxy);
    return proxy;
  }

  /**
   * InvocationHandler that delegates method calls from the proxy to the target object, handling
   * return type conversions when necessary.
   */
  private static class ServletApiInvocationHandler implements InvocationHandler {
    private final Object target;
    private final Class<?> targetClass;

    ServletApiInvocationHandler(Object target, Class<?> targetClass) {
      this.target = target;
      this.targetClass = targetClass;
    }

    Object getTarget() {
      return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
        // Find the corresponding method on the target object
        Method targetMethod = findTargetMethod(method);
        if (targetMethod == null) {
          throw new UnsupportedOperationException(
              "Method not found on target: " + method.getName());
        }

        // Convert arguments if necessary
        Object[] convertedArgs = convertArguments(args, targetMethod.getParameterTypes());

        // Invoke the method on the target
        Object result = targetMethod.invoke(target, convertedArgs);

        // Convert the result if necessary
        return convertResult(result, method.getReturnType());
      } catch (Exception e) {
        throw new RuntimeException("Error invoking method: " + method.getName(), e);
      }
    }

    private Method findTargetMethod(Method proxyMethod) {
      try {
        return targetClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
      } catch (NoSuchMethodException e) {
        // Method signature might be different due to javax/jakarta types
        // Try to find by name only and match parameter count
        for (Method m : targetClass.getMethods()) {
          if (m.getName().equals(proxyMethod.getName())
              && m.getParameterCount() == proxyMethod.getParameterCount()) {
            return m;
          }
        }
        return null;
      }
    }

    private Object[] convertArguments(Object[] args, Class<?>[] parameterTypes) {
      if (args == null) {
        return null;
      }
      Object[] converted = new Object[args.length];
      for (int i = 0; i < args.length; i++) {
        converted[i] = convertArgument(args[i], parameterTypes[i]);
      }
      return converted;
    }

    private Object convertArgument(Object arg, Class<?> targetType) {
      if (arg == null) {
        return null;
      }

      // If the argument is a jakarta type and target expects javax, convert it
      if (arg instanceof jakarta.servlet.ServletContext
          && targetType.getName().equals("javax.servlet.ServletContext")) {
        return createJavaxServletContext((jakarta.servlet.ServletContext) arg);
      }
      // Add more conversions as needed

      return arg;
    }

    private Object convertResult(Object result, Class<?> returnType) {
      if (result == null) {
        return null;
      }

      // Convert javax return types to jakarta if needed
      String resultClassName = result.getClass().getName();
      String returnTypeName = returnType.getName();

      if (resultClassName.startsWith("javax.servlet")
          && returnTypeName.startsWith("javax.servlet")) {
        return result; // Both javax, no conversion needed
      }

      // Add specific conversions as needed
      if (result instanceof javax.servlet.ServletContext
          && returnTypeName.equals("javax.servlet.ServletContext")) {
        return result;
      }

      return result;
    }
  }
}
