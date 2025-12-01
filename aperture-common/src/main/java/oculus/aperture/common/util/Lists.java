package oculus.aperture.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Static utility methods pertaining to List instances. Replacement for Guava Lists. */
public final class Lists {

  private Lists() {
    // Utility class
  }

  /** Creates a mutable, empty ArrayList instance. */
  public static <E> ArrayList<E> newArrayList() {
    return new ArrayList<>();
  }

  /** Creates a mutable ArrayList instance containing the given elements. */
  @SafeVarargs
  public static <E> ArrayList<E> newArrayList(E... elements) {
    if (elements == null) {
      throw new NullPointerException();
    }
    final ArrayList<E> list = new ArrayList<>(elements.length);
    Collections.addAll(list, elements);
    return list;
  }

  /** Creates a mutable ArrayList instance containing the given elements. */
  public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
    if (elements == null) {
      throw new NullPointerException();
    }
    return (elements instanceof java.util.Collection)
        ? new ArrayList<>((java.util.Collection<? extends E>) elements)
        : newArrayList(elements.iterator());
  }

  /** Creates a mutable ArrayList instance containing the given elements. */
  public static <E> ArrayList<E> newArrayList(java.util.Iterator<? extends E> elements) {
    final ArrayList<E> list = newArrayList();
    while (elements.hasNext()) {
      list.add(elements.next());
    }
    return list;
  }

  /** Creates an ArrayList instance with the specified initial capacity. */
  public static <E> ArrayList<E> newArrayListWithCapacity(int initialCapacity) {
    return new ArrayList<>(initialCapacity);
  }

  /** Creates an ArrayList instance sized appropriately to hold an estimated number of elements. */
  public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
    return new ArrayList<>(computeArrayListCapacity(estimatedSize));
  }

  /** Computes the capacity for an ArrayList to hold the expected size. */
  private static int computeArrayListCapacity(int arraySize) {
    if (arraySize < 0) {
      throw new IllegalArgumentException("arraySize must be non-negative");
    }
    return (int) Math.min(5L + arraySize + (arraySize / 10), Integer.MAX_VALUE);
  }

  /** Creates an immutable list containing the given elements. */
  public static <E> List<E> asList(E first, E[] rest) {
    final List<E> result = new ArrayList<>(1 + rest.length);
    result.add(first);
    Collections.addAll(result, rest);
    return Collections.unmodifiableList(result);
  }

  /** Creates an immutable list containing the given elements. */
  public static <E> List<E> asList(E first, E second, E[] rest) {
    final List<E> result = new ArrayList<>(2 + rest.length);
    result.add(first);
    result.add(second);
    Collections.addAll(result, rest);
    return Collections.unmodifiableList(result);
  }

  /** Returns a list that applies a function to each element of a from list. */
  public static <F, T> List<T> transform(
      List<F> fromList, Function<? super F, ? extends T> function) {
    final List<T> result = new ArrayList<>(fromList.size());
    for (F element : fromList) {
      result.add(function.apply(element));
    }
    return result;
  }

  /**
   * Returns every possible list that can be formed by choosing one element from each of the given
   * lists.
   */
  @SafeVarargs
  public static <B> List<List<B>> cartesianProduct(List<? extends B>... lists) {
    return cartesianProduct(Arrays.asList(lists));
  }

  /**
   * Returns every possible list that can be formed by choosing one element from each of the given
   * lists.
   */
  public static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> lists) {
    if (lists.isEmpty()) {
      final List<List<B>> result = new ArrayList<>(1);
      result.add(new ArrayList<>());
      return result;
    }

    final List<List<B>> result = new ArrayList<>();
    cartesianProductHelper(lists, 0, new ArrayList<>(), result);
    return result;
  }

  /** Helper method for cartesian product. */
  private static <B> void cartesianProductHelper(
      List<? extends List<? extends B>> lists, int index, List<B> current, List<List<B>> result) {
    if (index == lists.size()) {
      result.add(new ArrayList<>(current));
      return;
    }

    for (B element : lists.get(index)) {
      current.add(element);
      cartesianProductHelper(lists, index + 1, current, result);
      current.remove(current.size() - 1);
    }
  }

  /** Returns a reversed view of the specified list. */
  public static <T> List<T> reverse(List<T> list) {
    final List<T> reversed = new ArrayList<>(list);
    Collections.reverse(reversed);
    return reversed;
  }

  /** Partitions a list into sublists of the specified size. */
  public static <T> List<List<T>> partition(List<T> list, int size) {
    if (list == null) {
      throw new NullPointerException();
    }
    if (size <= 0) {
      throw new IllegalArgumentException("size must be positive");
    }

    final List<List<T>> result = new ArrayList<>((list.size() + size - 1) / size);
    for (int i = 0; i < list.size(); i += size) {
      result.add(list.subList(i, Math.min(i + size, list.size())));
    }
    return result;
  }

  /** Simple function interface for transformations. */
  @FunctionalInterface
  public interface Function<F, T> {
    T apply(F input);
  }
}
