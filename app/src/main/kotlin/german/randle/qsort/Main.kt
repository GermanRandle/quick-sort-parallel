package german.randle.qsort

import kotlin.system.measureTimeMillis

fun main() {
    val (arr1, arr2) = generateRandomArray() to generateRandomArray()

    val (sequentialTime, parallelTime) = measureTimeMillis {
        quickSortSequential(arr1)
    } to measureTimeMillis {
        quickSortParallel(arr2)
    }

    if (!arr1.isSorted()) {
        error("SEQUENTIAL SORTING FAILED")
    }
    if (!arr2.isSorted()) {
        error("PARALLEL SORTING FAILED")
    }

    println("SEQUENTIAL TIME: $sequentialTime")
    println("PARALLEL TIME: $parallelTime")
    println("RATIO: ${sequentialTime / parallelTime}")
}
