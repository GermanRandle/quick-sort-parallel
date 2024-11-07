package german.randle.qsort

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.system.measureTimeMillis

const val ARRAY_LENGTH = 100_000_000
const val PROCESSES_COUNT = 4
const val LAUNCHES_COUNT = 5

// If the size of array is less than or equal to this number, then we "switch to sequential mode".
const val BLOCK_SIZE = 1000

// Reserve memory in advance not to spoil the time metrics
val arrForCopy = IntArray(ARRAY_LENGTH)
val arrForScan = IntArray(ARRAY_LENGTH)
val arrForSegTree = IntArray(ARRAY_LENGTH / ((BLOCK_SIZE + 1) / 2) * 4)

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    val seqToParTimes = List(LAUNCHES_COUNT) {
        val arr1 = generateRandomArray()
        val arr2 = arr1.copyOf()

        val (sequentialTime, parallelTime) = measureTimeMillis {
            qSortSequential(arr1, 0, ARRAY_LENGTH)
        } to run {
            val coroutineDispatcher = Dispatchers.Default.limitedParallelism(PROCESSES_COUNT)
            val scope = CoroutineScope(coroutineDispatcher)
            measureTimeMillis {
                qSortParallel(
                    scope,
                    arr2,
                    arrForCopy,
                    arrForScan,
                    arrForSegTree,
                    0,
                    ARRAY_LENGTH,
                )
            }
        }

        if (!arr1.isSorted()) {
            error("SEQUENTIAL SORTING FAILED")
        }
        if (!arr2.isSorted()) {
            error("PARALLEL SORTING FAILED")
        }

        println("LAUNCH #$it")
        println("SEQUENTIAL TIME: $sequentialTime ms")
        println("PARALLEL TIME: $parallelTime ms")
        println("RATIO: ${sequentialTime / parallelTime}")

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
