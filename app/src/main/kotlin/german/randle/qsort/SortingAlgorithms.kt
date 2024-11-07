package german.randle.qsort

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun qSortSequential(arr: IntArray, l: Int, r: Int) {
    if (r - l <= 1) {
        return
    }

    var pivot = Random.nextInt(l, r)
    var border = l

    for (i in l..<r) {
        if (arr[i] <= arr[pivot]) {
            arr[border] = arr[i].also { arr[i] = arr[border] }
            if (i == pivot) {
                pivot = border
            }
            border++
        }
    }
    arr[pivot] = arr[border - 1].also { arr[border - 1] = arr[pivot] }

    qSortSequential(arr, l, border - 1)
    qSortSequential(arr, border, r)
}

fun qSortParallel(
    coroutineScope: CoroutineScope,
    arr: IntArray,
    arrForCopy: IntArray,
    arrForScan: IntArray,
    arrForSegTree: IntArray,
    l: Int,
    r: Int,
    blockSize: Int = BLOCK_SIZE,
) {
    suspend fun recursive(l: Int, r: Int) {
        if (r - l <= blockSize) {
            qSortSequential(arr, l, r)
        }

        // 1. Copy the array for the further new positions assignment
        val copyJobs = (0..<(r - l + blockSize - 1) / blockSize).map { chunk ->
            coroutineScope.launch {
                ((chunk * blockSize)..<(chunk * (blockSize + 1))).forEach {
                    arrForCopy[it] = arr[it]
                }
            }
        }

        // 2. Scan analogue, which is used to determine the new positions
        var pivot = Random.nextInt(l, r)

        fun up() {

        }

        fun down() {

        }

        // 3. Assign new positions
        var border = -1
        copyJobs.forEach { it.join() }

        arr[pivot] = arr[border - 1].also { arr[border - 1] = arr[pivot] }

        coroutineScope.launch { recursive(l, border - 1) }
        coroutineScope.launch { recursive(border, r) }
    }

    runBlocking { recursive(l, r) }
}
