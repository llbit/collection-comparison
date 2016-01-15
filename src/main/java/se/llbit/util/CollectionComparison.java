/* Copyright (c) 2014-2016, Jesper Öqvist <jesper@llbit.se>
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
import java.util.HashMap;
import java.util.Map;

/**
 * Collection comparison helper.
 * See http://llbit.se/?p=2009 for more info.
 *
 * @author Jesper Öqvist
 */
public class CollectionComparison {
  /**
   * Utility method for checking if two collections have the same elements,
   * disregarding the element order but respecting duplicates, i.e. the elements
   * must have the same cardinality in both collections. Elements are considered
   * equal if {@code x.equals(y)} returns true for two elements x and y.
   *
   * @param a the first collection
   * @param b the other collection
   * @return {@code true} if the collections a and b are identical without regard
   * to element order.
   */
  public static boolean isEqualCollection(Collection<?> a, Collection<?> b) {
    if (a.size() != b.size()) {
      return false;
    }
    Map<Object, Integer> map = new HashMap<Object, Integer>();
    for (Object o : a) {
      Integer val = map.get(o);
      int count;
      if (val != null) {
        count = val.intValue();
      } else {
        count = 0;
      }
      map.put(o, Integer.valueOf(count + 1));
    }
    for (Object o : b) {
      Integer val = map.get(o);
      int count;
      if (val != null) {
        count = val.intValue();
        if (count == 0) {
          return false;
        }
      } else {
        return false;
      }
      map.put(o, Integer.valueOf(count - 1));
    }
    for (Integer count: map.values()) {
      if (count.intValue() != 0) {
        return false;
      }
    }
    return true;
  }
}
