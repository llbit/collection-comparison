Efficient Collection Equality Comparison
========================================

[![Build Status](https://travis-ci.org/llbit/collection-comparison.svg?branch=master)](https://travis-ci.org/llbit/collection-comparison)
[![Test Coverage](https://codecov.io/github/llbit/collection-comparison/coverage.svg?branch=master)](https://codecov.io/github/llbit/collection-comparison?branch=master)


This is a tiny Java library with a single method for testing if two Java
collections contain the same elements:

    se.llbit.util.CollectionComparison.isEqualCollection(a, b)


The method returns `true` if, and only if, the two input collections `A` and
`B` contain the same elements, compared using `x.equals(y)`, with the same
number of occurrences of each element in both collections.  The algorithm is
efficient and well tested. A sketch of a correctness proof is available below.


##Background

I have seen many implementations of collection comparison algorithms in various
projects. Many of those implementations were incorrect in some way. Comparing
collections is more difficult than it first seems, and getting everything right
is not easy.

Common **incorrect** Java solutions are:

* `a.containsAll(b)` - completely wrong, only checks that `a` is a subset of
  `b` if neither collection has duplicates.
* `a.containsAll(b) && b.containsAll(a)` - works only when `a` and `b` have no
  duplicates, i.e. they are sets.
* `a.size() == b.size() && a.containsAll(b) && b.containsAll(a)` - still only
  works when `a` and `b` have no duplicates.

A correct implementation of collection equality comparison is available in
Apache Commons Collections 4.0, [`CollectionUtils.isEqualCollection`][1].
However, when I last looked at the Apache Commons algorithm I found that some
performance improvements could be made to that algorithm. I documented an
improved algorithm [in this blog post][2]. Since then I have further improved
the algorithm. The final version is:

      boolean isEqualCollection(Collection<?> a, Collection<?> b) {
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
        return true;
      }


My original blog post describes what I improved in the Apache Commons
algorithm, but the blog post did not provide tests for my algorithm.  After I
posted that blog post I have copied the code in several of my projects, and I
figured it would be useful to create a public Git repository with tests and
give it an Open Source license. The code and tests in this repository are
licensed under the Modified BSD License. The tests check some special cases
that incorrect collection comparison algorithms fail to handle, and the tests
cover all statements in the implementation.

##Correctness Proof Sketch

I have made a sketch of a proof for correctness here. I'm not used to writing
proofs, so I hope it's not too poorly structured. If I made an error please
report a bug on the issue tracker of this repository!

Definition: Two collections `A` and `B` are equal if and only if each element
`x` in `A` occurs the same number of times in `B`, and each element `y` in `B`
occurs the same number of times in `A`.  Two elements `x` and `y` are
considered equal if `x.equals(y)` returns `true` and `x.equals(y)` is equal to
`y.equals(x)`.

A description of how the algorithm works:

1. An initial test checks that the number of elements in `A`, denoted
   `size(A)`, is equal to the number of elements in `B`. If `size(A) !=
   size(B)` then the algorithm is done and returns `false`.
2. A map containing occurrence counts of elements in `A` is built. If `|x|`
   denotes the number of occurrences of an element `x` in collection `A`, then
   `map.get(x) == |x|` for each `x` in `A` after the first loop.
3. The second loop updates the occurrence map by iterating over all elements in
   `B`. For each element `o` in `B`, the following happens:
    1. *(1)* If there was no record for element `o` in the occurrence map, then
       the algorithm exits with return value `false` because `o` occurred in
       `B` and not `A`.
    2. Otherwise, the algorithm checks the occurrence count stored in the map
       for element `o` and there is a choice:
        1. *(2)* If the occurrence count was zero, then `o` occurs more often
           in `B` than `A` and the method exits with return value `false`.
        2. Otherwise, the record for element `o` in the occurrence map is
           updated by decreasing the count by one.
4. If the execution passes the second loop without returning, then the
   collections are equal so `true` is returned.

*Lemma 1:* The second loop ensures that every element in `B` occurs in `A` at least
as many times as in `B`.

*Proof:* Suppose that some element `x` occurs in `B` but not in `A`, then there
is no record of `x` in the occurrence map before the second loop, and if
execution reaches the first occurrence of `x` in `B` then case *(1)* returns
`false`. Otherwise, if there are `n` occurrences of `x` in `A`, and `n+k`
occurrences of `x` in `B`, and `k>0`, then when the `n+1`th occurrence of `x`
in `B` is encountered case *(2)* returns false.

*Lemma*: The algorithm returns `true` if and only if `A` and `B` are equal.

*Proof:* There are three possibilities:

1. Either an element `x` occurs more times in `B` than in `A`.
2. Or, an element `x` occurs more times in `A` than in `B`.
3. Otherwise `A` and `B` are equal.

Consider each case in turn:

* If an element `x` occurs more often in `B` than `A` then:
    * If `size(A) != size(B)` the initial size check returns `false`.
    * Otherwise the second loop returns `false` due to either case *(1)* or
      *(2)* in the second loop at the latest when `o == x`.
* Otherwise, if an element `x` occurs more often in `A` than `B` then:
    * If `size(A) != size(B)` the initial size check returns `false`.
    * Otherwise there must be some other element `y != x` that occurs more
      often in `B` than `A` and either case *(1)* or *(2)* in the second loop
      returns `false`.
* Otherwise, `A` and `B` are equal. The execution reaches the last statement
  and returns `true`.


##Using the library

The library is [available from The Central Repository][3]!

To build using the tiny collection comparison library, just declare an extra
dependency in your build script. For example as in this Gradle build:

    apply plugin: 'maven'
    apply plugin: 'java'
    repositories {
        mavenCentral()
    }
    dependencies {
        compile 'se.llbit:collcompare:1.0.0'
    }


You can then use the collection equality method in your code:

    import static se.llbit.util.CollectionComparison.isEqualCollection;

    ...
    isEqualCollection(a, b);


##Version History

* **1.0.0** Initial version.
* **1.0.1** Improved algorithm.
    * Removed a redundant loop that checked the occurrence map after the second loop.
    * Added correctness proof in README.
    * Added an additional test case to reach 100% test coverage.
    * Made the `CollectionComparison` class `abstract`.


[1]: https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/CollectionUtils.html#isEqualCollection(java.util.Collection,%20java.util.Collection)
[2]: http://llbit.se/?p=2009
[3]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22collcompare%22
