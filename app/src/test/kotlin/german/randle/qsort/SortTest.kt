package german.randle.qsort

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.Executors

class SortTest {
    @ParameterizedTest
    @MethodSource("testcases")
    fun testSequentialSort(arr: IntArray) {
        val expected = arr.sorted()
        qSortSequential(arr, 0, arr.size)
        arr shouldBe expected
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("testcases")
    fun testParallelSort(arr: IntArray) = runBlocking {
        val expected = arr.sorted()
        qSortParallel(arr, 0, arr.size, blockSize = 1)
        arr shouldBe expected
    }

    companion object {
        @JvmStatic
        private fun testcases() = listOf(
            Arguments.of(intArrayOf()),
            Arguments.of(intArrayOf(45)),
            Arguments.of(intArrayOf(2, 1)),
            Arguments.of(intArrayOf(1, 2, 3, 4, 5)),
            Arguments.of(intArrayOf(7, 6, 5, 4, 3, 2, 1)),
            Arguments.of(intArrayOf(999, 888, 999, 999, 888, 888)),
            Arguments.of(intArrayOf(90, 50, 20, 70, 80, 10, 60, 30, 40)),
            Arguments.of(generateRandomArray(50)),
            Arguments.of(generateRandomArray(2000)),
        )
    }
}
