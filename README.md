Efficient Collection Equality Comparison
========================================

Comparing collections in Java is something that I have seen many different
implementations of, and many of the ones I have seen have been wrong. The
problem is more difficult than it initially appears, so most coders think they
can implement it correclty and then they don't test it enough to notice the
flaws. Incorrect implmentations of collection equality comparisons may go
unnoticed for a long time, but can lead to serious problems later on.

Common anti patterns for comparing collections include:

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
improved algorithm [in this blog post][2]. Here is the code for my improved
version:

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
        for (Integer count: map.values()) {
          if (count.intValue() != 0) {
            return false;
          }
        }
        return true;
      }


My original blog post describes how the above algorithm is better than the
Apache Commons algorithm, but I did not provide tests for my algorithm.
Since I posted that blog post I have copied the code in several of my
projects, and I figured it would be useful to create a public Git repository with
tests and give it an Open Source license. The code and tests in this repository are
licensed under the Modified BSD License. The tests check some special cases
that incorrect collection comparison algorithms fail to handle:

* One collection contains a subset of the elements in the other collection.
* One collection has extra duplicates of some items, not present in the other collection.
* One collection has the same elements as the other, but in a different order.

##Using the library

The algorithm described above is available as a tiny collection library which
is [available on The Central Repository][3]!

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



[1]: https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/CollectionUtils.html#isEqualCollection(java.util.Collection,%20java.util.Collection)
[2]: http://llbit.se/?p=2009
[3]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22collcompare%22
