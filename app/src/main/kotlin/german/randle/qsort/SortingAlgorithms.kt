package german.randle.qsort

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
val coroutineDispatcher = Dispatchers.Default.limitedParallelism(PROCESSES_COUNT)
val scope = CoroutineScope(coroutineDispatcher)

suspend fun qSortParallel(arr: IntArray, l: Int, r: Int, blockSize: Int) {
    if (r - l <= blockSize) {
        qSortSequential(arr, l, r)
        return
    }

    // 1. Copy the array for the further positions reassignment
    val chunksAmount = (r - l + blockSize - 1) / blockSize
    val copyJobs = (0..<chunksAmount).map { chunk ->
        scope.launch {
            val chunkBegin = l + chunk * blockSize
            val chunkEnd = minOf(r, chunkBegin + blockSize)
            arr.copyInto(arrForCopy, chunkBegin, chunkBegin, chunkEnd)
        }
    }

    // 2. Scan analogue, which is used to determine the new positions
    val pivot = random.nextInt(l, r)
    val arrForSegTree1 = IntArray((r - l) / ((blockSize + 1) / 2) * 4)
    val arrForSegTree2 = IntArray((r - l) / ((blockSize + 1) / 2) * 4)

    suspend fun up(nodeId: Int, l: Int, r: Int) {
        if (r - l <= blockSize) {
            val moreThanPivot = (l..<r).count { arr[it] > arr[pivot] }
            arrForSegTree1[nodeId] = r - l - moreThanPivot
            arrForSegTree2[nodeId] = moreThanPivot
            return
        }
        val m = (l + r) / 2
        val leftChild = scope.launch { up(nodeId * 2 + 1, l, m) }
        val rightChild = scope.launch { up(nodeId * 2 + 2, m, r) }
        leftChild.join()
        rightChild.join()
        arrForSegTree1[nodeId] = arrForSegTree1[nodeId * 2 + 1] + arrForSegTree1[nodeId * 2 + 2]
        arrForSegTree2[nodeId] = arrForSegTree2[nodeId * 2 + 1] + arrForSegTree2[nodeId * 2 + 2]
    }

    suspend fun down(nodeId: Int, l: Int, r: Int, acc1: Int, acc2: Int) {
        if (r - l <= blockSize) {
            var (localAcc1, localAcc2) = 1 to 1
            (l..<r).forEach {
                arrForScan[it] = if (arr[it] <= arr[pivot]) {
                    (acc1 + localAcc1).also { localAcc1++ }
                } else {
                    -(acc2 + localAcc2).also { localAcc2++ }
                }
            }
            return
        }
        val m = (l + r) / 2
        val leftChild = scope.launch { down(nodeId * 2 + 1, l, m, acc1, acc2) }
        val rightChild = scope.launch { down(nodeId * 2 + 2, m, r, acc1 + arrForSegTree1[nodeId * 2 + 1], acc2 + arrForSegTree2[nodeId * 2 + 1]) }
        leftChild.join()
        rightChild.join()
    }

    scope.launch { up(0, l, r) }.join()
    scope.launch { down(0, l, r, 0, 0) }.join()

    // 3. Assign new positions
    copyJobs.forEach { it.join() }

    var pivotNewPos: Int = -1
    var lessThanOrEqualCount = 0
    val assignJobs = (0..<chunksAmount).map { chunk ->
        scope.launch {
            val chunkBegin = l + chunk * blockSize
            val chunkEnd = minOf(r, chunkBegin + blockSize)
            (chunkBegin..<chunkEnd).forEach {
                val newPos = if (arrForScan[it] > 0) {
                    lessThanOrEqualCount++
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
