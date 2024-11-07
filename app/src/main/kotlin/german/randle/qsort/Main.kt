package german.randle.qsort

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

const val ARRAY_LENGTH = 100_000_000
const val PROCESSES_COUNT = 4
const val LAUNCHES_COUNT = 5

fun main() {
    val seqToParTimes = List(LAUNCHES_COUNT) {
        val arr1 = generateRandomArray()
        val arr2 = arr1.copyOf()

        val (sequentialTime, parallelTime) = measureTimeMillis {
            qSortSequential(arr1, 0, ARRAY_LENGTH)
        } to run {
            val coroutineDispatcher = Executors.newFixedThreadPool(PROCESSES_COUNT).asCoroutineDispatcher()
            measureTimeMillis {
                qSortParallel(coroutineDispatcher, arr2, 0, ARRAY_LENGTH)
            }.also { coroutineDispatcher.close() }
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

    println("SEQUENTIAL TIME: $avgSeqTime ms")
    println("PARALLEL TIME: $avgParTime ms")
    println("RATIO: ${avgSeqTime / avgParTime}")
}
