/**
 * Copyright (c) 2013-2014 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oculus.aperture.graph.util.mcl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Sparse vector implementation for MCL clustering algorithm. Replaces the javaml dependency with a
 * native implementation.
 */
public class SparseVector {

  private Map<Integer, Double> data = new HashMap<>();
  private int length = 0;

  public SparseVector() {}

  public SparseVector(int length) {
    this.length = length;
  }

  public void put(int index, double value) {
    if (value != 0.0) {
      data.put(index, value);
      if (index >= length) {
        length = index + 1;
      }
    } else {
      data.remove(index);
    }
  }

  public double get(int index) {
    Double value = data.get(index);
    return value != null ? value : 0.0;
  }

  public void add(int index, double value) {
    double current = get(index);
    put(index, current + value);
  }

  public Set<Integer> keySet() {
    return data.keySet();
  }

  public int size() {
    return data.size();
  }

  public int length() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  /** Returns the maximum value in this vector. */
  public double max() {
    double max = Double.NEGATIVE_INFINITY;
    for (Double value : data.values()) {
      if (value > max) {
        max = value;
      }
    }
    return max == Double.NEGATIVE_INFINITY ? 0.0 : max;
  }

  /** Returns the sum of values raised to the given power. */
  public double sum(double power) {
    double sum = 0.0;
    for (Double value : data.values()) {
      sum += Math.pow(value, power);
    }
    return sum;
  }

  /** Returns the sum of all values. */
  public double sum() {
    double sum = 0.0;
    for (Double value : data.values()) {
      sum += value;
    }
    return sum;
  }

  /** Normalizes this vector so that values sum to the given value. */
  public double normalise(double targetSum) {
    double currentSum = sum();
    if (currentSum == 0.0) {
      return 0.0;
    }
    double factor = targetSum / currentSum;
    for (Integer key : data.keySet()) {
      data.put(key, data.get(key) * factor);
    }
    return currentSum;
  }

  /** Raises all values to the given power. */
  public void hadamardPower(double power) {
    for (Integer key : data.keySet()) {
      data.put(key, Math.pow(data.get(key), power));
    }
  }

  /** Removes values below the threshold. */
  public void prune(double threshold) {
    data.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < threshold);
  }

  /** Copies this vector. */
  public SparseVector copy() {
    SparseVector copy = new SparseVector(length);
    copy.data.putAll(this.data);
    return copy;
  }

  /** Converts to dense array. */
  public double[] toDense() {
    double[] dense = new double[length];
    for (Map.Entry<Integer, Double> entry : data.entrySet()) {
      dense[entry.getKey()] = entry.getValue();
    }
    return dense;
  }
}
