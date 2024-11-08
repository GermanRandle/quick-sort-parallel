package german.randle.qsort

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun qSortSequential(arr: IntArray, l: Int, r: Int) {
    if (r - l <= 1) {
        return
    }

    var pivot = random.nextInt(l, r)
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

@OptIn(ExperimentalCoroutinesApi::class)
val scope = CoroutineScope(Dispatchers.Default.limitedParallelism(PROCESSES_COUNT))

suspend fun qSortParallel(arr: IntArray, l: Int, r: Int, blockSize: Int) {
    if (r - l <= blockSize) {
        qSortSequential(arr, l, r)
        return
    }

    // 1. Copy the array for the further positions reassignment
    val chunksAmount = (r - l + blockSize - 1) / blockSize
    val chunkSize = (r - l + chunksAmount - 1) / chunksAmount
    val copyJobs = (0..<chunksAmount).map { chunk ->
        scope.launch {
            val chunkBegin = l + chunk * chunkSize
            val chunkEnd = minOf(r, chunkBegin + chunkSize)
            arr.copyInto(arrForCopy, chunkBegin, chunkBegin, chunkEnd)
        }
    }

    // 2. Scan analogue, which is used to determine the new positions
    val pivot = random.nextInt(l, r)
    val scanTree = IntArray((r - l) / ((blockSize + 1) / 2) * 4)

    suspend fun up(nodeId: Int, l: Int, r: Int) {
        if (r - l <= blockSize) {
            scanTree[nodeId] = (l..<r).count { arr[it] <= arr[pivot] }
            return
        }

        val m = (l + r) / 2
        val leftChild = scope.launch { up(nodeId * 2 + 1, l, m) }
        val rightChild = scope.launch { up(nodeId * 2 + 2, m, r) }
        leftChild.join()
        scanTree[nodeId] += scanTree[nodeId * 2 + 1]
        rightChild.join()
        scanTree[nodeId] += scanTree[nodeId * 2 + 2]
    }

    suspend fun down(nodeId: Int, l: Int, r: Int, acc1: Int, acc2: Int): Int {
        if (r - l <= blockSize) {
            var (localAcc1, localAcc2) = 1 to 1
            (l..<r).forEach {
                arrForScan[it] = if (arr[it] <= arr[pivot]) {
                    (acc1 + localAcc1).also { localAcc1++ }
                } else {
                    -(acc2 + localAcc2).also { localAcc2++ }
                }
            }
            return localAcc1 - 1
        }

        val m = (l + r) / 2
        val leftChild = scope.async { down(nodeId * 2 + 1, l, m, acc1, acc2) }
        val rightChild = scope.async { down(nodeId * 2 + 2, m, r, acc1 + scanTree[nodeId * 2 + 1], acc2 + m - l - scanTree[nodeId * 2 + 1]) }
        return leftChild.await() + rightChild.await()
    }

    scope.launch { up(0, l, r) }.join()
    val lessThanOrEqualCount = scope.async { down(0, l, r, 0, 0) }.await()
    copyJobs.forEach { it.join() }

    // 3. Assign new positions
    var pivotNewPos = -1
    val assignJobs = (0..<chunksAmount).map { chunk ->
        scope.launch {
            val chunkBegin = l + chunk * chunkSize
            val chunkEnd = minOf(r, chunkBegin + chunkSize)
            (chunkBegin..<chunkEnd).forEach {
                val newPos = if (arrForScan[it] > 0) {
                    l + arrForScan[it] - 1
                } else {
                    r + arrForScan[it]
                }
                arr[newPos] = arrForCopy[it]
                if (it == pivot) {
                    pivotNewPos = newPos
                }
            }
        }
    }
    assignJobs.forEach { it.join() }

    arr[pivotNewPos] = arr[l + lessThanOrEqualCount - 1]
        .also { arr[l + lessThanOrEqualCount - 1] = arr[pivotNewPos] }

    val leftChild = scope.launch { qSortParallel(arr, l, l + lessThanOrEqualCount - 1, blockSize) }
    val rightChild = scope.launch { qSortParallel(arr, l + lessThanOrEqualCount, r, blockSize) }
    leftChild.join()
    rightChild.join()
}
