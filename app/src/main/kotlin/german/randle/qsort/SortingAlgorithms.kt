package german.randle.qsort

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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

// Reserve memory in advance not to spoil the time metrics
val arrForCopy = IntArray(ARRAY_SIZE)
val arrForScan = IntArray(ARRAY_SIZE)
val arrForSegTree = IntArray(ARRAY_SIZE / ((BLOCK_SIZE + 1) / 2) * 4)

@OptIn(ExperimentalCoroutinesApi::class)
val coroutineDispatcher = Dispatchers.Default.limitedParallelism(PROCESSES_COUNT)
val scope = CoroutineScope(coroutineDispatcher)

suspend fun qSortParallel(
    arr: IntArray,
    l: Int,
    r: Int,
    blockSize: Int = BLOCK_SIZE,
) {
    if (r - l <= blockSize) {
        qSortSequential(arr, l, r)
    }

    // 1. Copy the array for the further new positions assignment
    val chunksAmount = (r - l + blockSize - 1) / blockSize
    val copyJobs = (0..<chunksAmount).map { chunk ->
        scope.launch {
            val chunkBegin = l + chunk * blockSize
            val chunkEnd = minOf(r, chunkBegin + blockSize)
            (chunkBegin..<chunkEnd).forEach {
                arrForCopy[it] = arr[it]
            }
        }
    }

    // 2. Scan analogue, which is used to determine the new positions
    var pivot = Random.nextInt(l, r)

    suspend fun up(nodeId: Int, l: Int, r: Int) {
        if (r - l <= blockSize) {
            TODO()
            return
        }
        val m = (l + r) / 2
        val leftChild = scope.launch { up(nodeId * 2 + 1, l, m) }
        val rightChild = scope.launch { up(nodeId * 2 + 2, m, r) }
        leftChild.join(); rightChild.join()

    }

    fun down() {

    }

    // 3. Assign new positions
    var border = -1
    copyJobs.forEach { it.join() }

    arr[pivot] = arr[border - 1].also { arr[border - 1] = arr[pivot] }

    scope.launch { qSortParallel(arr, l, border - 1) }
    scope.launch { qSortParallel(arr, border, r) }
}
