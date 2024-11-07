package german.randle.qsort

import kotlinx.coroutines.ExecutorCoroutineDispatcher
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
    coroutineDispatcher: ExecutorCoroutineDispatcher,
    arr: IntArray,
    l: Int,
    r: Int,
    blockSize: Int = 1000,
) {
    if (r - l <= blockSize) {
        qSortSequential(arr, l, r)
    }


}
