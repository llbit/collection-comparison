/* Copyright (c) 2016, Jesper Öqvist <jesper@llbit.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package se.llbit.util;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Collection equality comparison tests. Includes tests for some special
 * cases that other common approaches to collection equality comparisons fail
 * to handle correctly:
 *
 * <ul>
 *   <li>One collection contains a subset of the elements in the other collection.
 *   <li>One collection has extra duplicates of some items, not present in the other collection.
 *   <li>One collection has the same elements as the other, but in a different order.
 * </ul>
 */
public class CollectionComparisonTest {

  /**
   * Helper class to test equality comparison.  Instances of this class are
   * always equal to each other, never equal to anything else.
   */
  static class AlwaysEqual {
    @Override
    public int hashCode() {
      return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof AlwaysEqual;
    }
  }

  /** Helper to build test subjects. */
  private static <T> List<T> listOf(T... elements) {
    List<T> list = new LinkedList<T>();
    for (T element : elements) {
      list.add(element);
    }
    return list;
  }

  /**
   * Test two collections with no duplicates and elements in the same order.
   */
  @Test
  public void testSimpleEqual() {
    Collection<String> a = listOf("a", "b", "c");
    Collection<String> b = listOf("a", "b", "c");
    assertTrue(CollectionComparison.isEqualCollection(a, b));
    assertTrue(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with no duplicates and one is a permutation of the other.
   */
  @Test
  public void testSimplePermuted() {
    Collection<String> a = listOf("2", "1", "3");
    Collection<String> b = listOf("1", "2", "3");
    assertTrue(CollectionComparison.isEqualCollection(a, b));
    assertTrue(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with no duplicates and one is a subset of the other
   * but not vice versa.
   */
  @Test
  public void testSimpleSubset() {
    Collection<String> a = listOf("x", "y", "z");
    Collection<String> b = listOf("x", "y", "z", "w");
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with no duplicates and one is a subset of the other
   * but not vice versa. The subset collection is permuted with respect to the superset.
   */
  @Test
  public void testSimplePermutedSubset() {
    Collection<String> a = listOf("i", "j", "k");
    Collection<String> b = listOf("l", "i", "k", "j");
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with duplicate elements in the same order.
   */
  @Test
  public void testDuplicates() {
    Collection<String> a = listOf("A", "A", "C");
    Collection<String> b = listOf("A", "A", "C");
    assertTrue(CollectionComparison.isEqualCollection(a, b));
    assertTrue(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with duplicate elements and one is a permutation of
   * the other.
   */
  @Test
  public void testDuplicatesPermuted() {
    Collection<String> a = listOf("&", "&", "?");
    Collection<String> b = listOf("&", "?", "&");
    assertTrue(CollectionComparison.isEqualCollection(a, b));
    assertTrue(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with duplicate elements and one is subset of the
   * other but not vice versa. Both collections contain the same elements
   * when duplicates are removed.
   */
  @Test
  public void testDuplicatesSubset() {
    Collection<String> a = listOf("0", "0", "1");
    Collection<String> b = listOf("0", "1");
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with duplicate elements and one is subset of the
   * other but not vice versa. Both collections contain different elements
   * when duplicates are removed.
   */
  @Test
  public void testDuplicatesSubset2() {
    Collection<String> a = listOf("u", "u", "v");
    Collection<String> b = listOf("u", "u");
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections with the same number of elements but different
   * cardinalities for each unique element.
   */
  @Test
  public void testDuplicatesMismatch() {
    Collection<String> a = listOf("xyz", "xyz", "...");
    Collection<String> b = listOf("xyz", "...", "...");
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test that equality comparsion, not reference equality is used
   * to compare collections.
   */
  @Test
  public void testEqualityComparison() {
    Collection<AlwaysEqual> a = listOf(new AlwaysEqual(), new AlwaysEqual());
    Collection<AlwaysEqual> b = listOf(new AlwaysEqual(), new AlwaysEqual());
    assertTrue(CollectionComparison.isEqualCollection(a, b));
    assertTrue(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test that equality comparsion, not reference equality is used
   * to compare collections.
   */
  @Test
  public void testEqualityComparison2() {
    Collection<AlwaysEqual> a = listOf(new AlwaysEqual(), new AlwaysEqual());
    Collection<AlwaysEqual> b = listOf(new AlwaysEqual());
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }

  /**
   * Test two collections of the same size, but one has duplicates and the
   * other has elements not present in the other.
   */
  @Test
  public void testSameSizeInequal() {
    Collection<Integer> a = listOf(1, 12, 12);
    Collection<Integer> b = listOf(1, 12, 1729);
    assertFalse(CollectionComparison.isEqualCollection(a, b));
    assertFalse(CollectionComparison.isEqualCollection(b, a));
  }
}
