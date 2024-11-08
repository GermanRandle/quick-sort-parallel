package german.randle.qsort

import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

const val ARRAY_SIZE = 100_000_000
const val PROCESSES_COUNT = 4
const val LAUNCHES_COUNT = 5

// If the size of array is less than or equal to this number, then we "switch to sequential mode".
const val BLOCK_SIZE = 200_000 // TODO

fun main() = runBlocking {
    val seqToParTimes = List(LAUNCHES_COUNT) {
        val arr1 = generateRandomArray(ARRAY_SIZE)
        val arr2 = arr1.copyOf()

        println("LAUNCH #${it + 1}")

        val (sequentialTime, parallelTime) = measureTimeMillis {
            qSortSequential(arr1, 0, ARRAY_SIZE)
        }.also { println("SEQUENTIAL TIME: $it ms") } to measureTimeMillis {
            qSortParallel(arr2, 0, ARRAY_SIZE, blockSize = BLOCK_SIZE)
        }.also { println("PARALLEL TIME: $it ms") }

        if (!arr1.isSorted()) {
            error("SEQUENTIAL SORTING FAILED")
        }
        if (!arr2.isSorted()) {
            error("PARALLEL SORTING FAILED")
        }

        println("RATIO: ${sequentialTime.toDouble() / parallelTime}")

        sequentialTime to parallelTime
    }

    val avgSeqTime = seqToParTimes.map { it.first }.average()
    val avgParTime = seqToParTimes.map { it.second }.average()

    println()
    println("FINAL RESULTS:")
    println("SEQUENTIAL TIME: $avgSeqTime ms")
    println("PARALLEL TIME: $avgParTime ms")
    println("RATIO: ${avgSeqTime / avgParTime}")
}
