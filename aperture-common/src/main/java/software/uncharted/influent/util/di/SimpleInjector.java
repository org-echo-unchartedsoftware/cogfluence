package software.uncharted.influent.util.di;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple dependency injection container. Replaces Google Guice for basic dependency injection use
 * cases.
 */
public class SimpleInjector {
  private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
  private final Map<Class<?>, Class<?>> bindings = new ConcurrentHashMap<>();
  private final Map<Class<?>, Provider<?>> providers = new ConcurrentHashMap<>();

  /**
   * Bind an interface to an implementation
   *
   * @param interfaceClass the interface class
   * @param implementationClass the implementation class
   * @param <T> the type
   */
  public <T> void bind(Class<T> interfaceClass, Class<? extends T> implementationClass) {
    bindings.put(interfaceClass, implementationClass);
  }

  /**
   * Bind an interface to a singleton instance
   *
   * @param interfaceClass the interface class
   * @param instance the singleton instance
   * @param <T> the type
   */
  public <T> void bindSingleton(Class<T> interfaceClass, T instance) {
    singletons.put(interfaceClass, instance);
  }

  /**
   * Bind an interface to a provider
   *
   * @param interfaceClass the interface class
   * @param provider the provider
   * @param <T> the type
   */
  public <T> void bindProvider(Class<T> interfaceClass, Provider<T> provider) {
    providers.put(interfaceClass, provider);
  }

  /**
   * Get an instance of the specified class
   *
   * @param clazz the class
   * @param <T> the type
   * @return the instance
   */
  @SuppressWarnings("unchecked")
  public <T> T getInstance(Class<T> clazz) {
    // Check if singleton exists
    if (singletons.containsKey(clazz)) {
      return (T) singletons.get(clazz);
    }

    // Check if provider exists
    if (providers.containsKey(clazz)) {
      return (T) providers.get(clazz).get();
    }

    // Check if binding exists
    Class<?> implementationClass = bindings.getOrDefault(clazz, clazz);

    try {
      return (T) createInstance(implementationClass);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
    }
  }

  /**
   * Create an instance using constructor injection
   *
   * @param clazz the class
   * @return the instance
   * @throws Exception if an error occurs
   */
  private Object createInstance(Class<?> clazz) throws Exception {
    // Find the constructor with the most parameters (greedy approach)
    Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length == 0) {
      throw new IllegalArgumentException("No public constructor found for " + clazz.getName());
    }

    Constructor<?> constructor = constructors[0];
    for (Constructor<?> c : constructors) {
      if (c.getParameterCount() > constructor.getParameterCount()) {
        constructor = c;
      }
    }

    // Resolve constructor parameters
    Class<?>[] paramTypes = constructor.getParameterTypes();
    Object[] params = new Object[paramTypes.length];
    for (int i = 0; i < paramTypes.length; i++) {
      params[i] = getInstance(paramTypes[i]);
    }

    return constructor.newInstance(params);
  }

  /**
   * Inject dependencies into an existing object's fields
   *
   * @param obj the object
   */
  public void injectFields(Object obj) {
    Class<?> clazz = obj.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Inject.class)) {
        field.setAccessible(true);
        try {
          Object value = getInstance(field.getType());
          field.set(obj, value);
        } catch (Exception e) {
          throw new RuntimeException("Failed to inject field " + field.getName(), e);
        }
      }
    }
  }

  /** Provider interface */
  public interface Provider<T> {
    T get();
  }

  /** Simple @Inject annotation */
  public @interface Inject {}
}
