# quick-sort-parallel
Comparing sequential and parallel implementations of quick sort.

The source code for both algorithms is [here](https://github.com/GermanRandle/quick-sort-parallel/blob/main/app/src/main/kotlin/german/randle/qsort/SortingAlgorithms.kt). Tests are [here](https://github.com/GermanRandle/quick-sort-parallel/tree/main/app/src/test/kotlin/german/randle/qsort/SortTest.kt).

### How to run

1) Install Gradle: https://gradle.org/install/

2) Execute in terminal:
```
quick-sort-parallel % ./gradlew run
```

### Results on my local machine

```declarative
LAUNCH #1
SEQUENTIAL TIME: 8180 ms
PARALLEL TIME: 3253 ms
RATIO: 2.514601905932985
LAUNCH #2
SEQUENTIAL TIME: 8226 ms
PARALLEL TIME: 3253 ms
RATIO: 2.5287426990470334
LAUNCH #3
SEQUENTIAL TIME: 8213 ms
PARALLEL TIME: 3316 ms
RATIO: 2.476779252110977
LAUNCH #4
SEQUENTIAL TIME: 8333 ms
PARALLEL TIME: 3112 ms
RATIO: 2.6776992287917736
LAUNCH #5
SEQUENTIAL TIME: 8234 ms
PARALLEL TIME: 3367 ms
RATIO: 2.4455004455004454

FINAL RESULTS:
SEQUENTIAL TIME: 8237.2 ms
PARALLEL TIME: 3260.2 ms
RATIO: 2.5265934605238947
```
