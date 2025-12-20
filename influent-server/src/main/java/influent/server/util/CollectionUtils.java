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

package influent.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for working with collections, providing replacements for common Guava collection
 * operations using Java standard library.
 */
public class CollectionUtils {

  /**
   * Converts an Iterable to a mutable ArrayList. This is a replacement for Guava's
   * Lists.newArrayList(Iterable).
   *
   * <p>This method is optimized for Collection inputs by using the Collection constructor. For
   * non-Collection Iterables, it iterates and adds each element.
   *
   * @param <T> the type of elements in the iterable
   * @param iterable the iterable to convert to a list
   * @return a new mutable ArrayList containing all elements from the iterable
   */
  public static <T> List<T> toList(Iterable<T> iterable) {
    if (iterable == null) {
      return new ArrayList<>();
    }

    // Optimize for Collection inputs
    if (iterable instanceof Collection) {
      return new ArrayList<>((Collection<T>) iterable);
    }

    // For non-Collection Iterables, iterate and add
    List<T> list = new ArrayList<>();
    iterable.forEach(list::add);
    return list;
  }

  /**
   * Creates a mutable ArrayList from varargs elements. This is a replacement for Guava's
   * Lists.newArrayList(E...).
   *
   * @param <T> the type of elements
   * @param elements the elements to add to the list
   * @return a new mutable ArrayList containing all provided elements
   */
  @SafeVarargs
  public static <T> List<T> toList(T... elements) {
    if (elements == null || elements.length == 0) {
      return new ArrayList<>();
    }
    List<T> list = new ArrayList<>(elements.length);
    Collections.addAll(list, elements);
    return list;
  }

  /** Private constructor to prevent instantiation of utility class. */
  private CollectionUtils() {
    throw new AssertionError("Utility class should not be instantiated");
  }
}
