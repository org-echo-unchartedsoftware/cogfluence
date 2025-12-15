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
 * Sparse matrix implementation for MCL clustering algorithm. Replaces the javaml dependency with a
 * native implementation.
 */
public class SparseMatrix {

  private Map<Integer, SparseVector> rows = new HashMap<>();
  private int numRows = 0;
  private int numCols = 0;

  public SparseMatrix() {}

  public SparseMatrix(int rows, int cols) {
    this.numRows = rows;
    this.numCols = cols;
  }

  public void add(int row, int col, double value) {
    SparseVector rowVector = rows.get(row);
    if (rowVector == null) {
      rowVector = new SparseVector();
      rows.put(row, rowVector);
    }
    rowVector.add(col, value);

    if (row >= numRows) {
      numRows = row + 1;
    }
    if (col >= numCols) {
      numCols = col + 1;
    }
  }

  public void set(int row, int col, double value) {
    SparseVector rowVector = rows.get(row);
    if (rowVector == null) {
      rowVector = new SparseVector();
      rows.put(row, rowVector);
    }
    rowVector.put(col, value);

    if (row >= numRows) {
      numRows = row + 1;
    }
    if (col >= numCols) {
      numCols = col + 1;
    }
  }

  public double get(int row, int col) {
    SparseVector rowVector = rows.get(row);
    if (rowVector == null) {
      return 0.0;
    }
    return rowVector.get(col);
  }

  public SparseVector get(int row) {
    return rows.get(row);
  }

  public int size() {
    return numRows;
  }

  public int numRows() {
    return numRows;
  }

  public int numCols() {
    return numCols;
  }

  public Set<Integer> keySet() {
    return rows.keySet();
  }

  /** Normalizes each row to sum to 1. */
  public void normaliseRows() {
    for (SparseVector row : rows.values()) {
      row.normalise(1.0);
    }
  }

  /** Normalizes each row to sum to the given value and returns row sums. */
  public SparseVector normalise(double targetSum) {
    SparseVector rowSums = new SparseVector(numRows);
    for (Map.Entry<Integer, SparseVector> entry : rows.entrySet()) {
      double sum = entry.getValue().normalise(targetSum);
      rowSums.put(entry.getKey(), sum);
    }
    return rowSums;
  }

  /** Raises all values to the given power. */
  public void hadamardPower(double power) {
    for (SparseVector row : rows.values()) {
      row.hadamardPower(power);
    }
  }

  /** Removes values below the threshold. */
  public void prune(double threshold) {
    for (SparseVector row : rows.values()) {
      row.prune(threshold);
    }
  }

  /** Matrix multiplication: this * other */
  public SparseMatrix times(SparseMatrix other) {
    SparseMatrix result = new SparseMatrix();

    for (Map.Entry<Integer, SparseVector> rowEntry : rows.entrySet()) {
      int i = rowEntry.getKey();
      SparseVector row = rowEntry.getValue();

      for (Integer k : row.keySet()) {
        double aik = row.get(k);
        SparseVector otherRow = other.get(k);
        if (otherRow != null) {
          for (Integer j : otherRow.keySet()) {
            double bkj = otherRow.get(j);
            result.add(i, j, aik * bkj);
          }
        }
      }
    }

    return result;
  }

  /** Returns the transpose of this matrix. */
  public SparseMatrix transpose() {
    SparseMatrix result = new SparseMatrix(numCols, numRows);

    for (Map.Entry<Integer, SparseVector> rowEntry : rows.entrySet()) {
      int i = rowEntry.getKey();
      SparseVector row = rowEntry.getValue();

      for (Integer j : row.keySet()) {
        result.set(j, i, row.get(j));
      }
    }

    return result;
  }

  /** Converts to dense 2D array. */
  public double[][] getDense() {
    double[][] dense = new double[numRows][numCols];

    for (Map.Entry<Integer, SparseVector> rowEntry : rows.entrySet()) {
      int i = rowEntry.getKey();
      SparseVector row = rowEntry.getValue();

      for (Integer j : row.keySet()) {
        dense[i][j] = row.get(j);
      }
    }

    return dense;
  }
}
