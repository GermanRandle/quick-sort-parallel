package german.randle.qsort

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.asCoroutineDispatcher
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.Executors

class SortTest {
    @ParameterizedTest
    @MethodSource("testcases")
    fun testSequentialSort(arr: IntArray, expected: IntArray) {
        qSortSequential(arr, 0, arr.size)
        arr shouldBe expected
    }

    @ParameterizedTest
    @MethodSource("testcases")
    fun testParallelSort(arr: IntArray, expected: IntArray) {
        val coroutineDispatcher = Executors.newFixedThreadPool(PROCESSES_COUNT).asCoroutineDispatcher()
        qSortParallel(coroutineDispatcher, arr, 0, arr.size, blockSize = 1)
        coroutineDispatcher.close()
        arr shouldBe expected
    }

    companion object {
        @JvmStatic
        private fun testcases() = listOf(
            Arguments.of(
                intArrayOf(),
                intArrayOf(),
            ),
            Arguments.of(
                intArrayOf(45),
                intArrayOf(45),
            ),
            Arguments.of(
                intArrayOf(2, 1),
                intArrayOf(1, 2),
            ),
            Arguments.of(
                intArrayOf(1, 2, 3, 4, 5),
                intArrayOf(1, 2, 3, 4, 5),
            ),
            Arguments.of(
                intArrayOf(7, 6, 5, 4, 3, 2, 1),
                intArrayOf(1, 2, 3, 4, 5, 6, 7),
            ),
            Arguments.of(
                intArrayOf(999, 888, 999, 999, 888, 888),
                intArrayOf(888, 888, 888, 999, 999, 999),
            ),
            Arguments.of(
                intArrayOf(90, 50, 20, 70, 80, 10, 60, 30, 40),
                intArrayOf(10, 20, 30, 40, 50, 60, 70, 80, 90),
            ),
        )
    }
}