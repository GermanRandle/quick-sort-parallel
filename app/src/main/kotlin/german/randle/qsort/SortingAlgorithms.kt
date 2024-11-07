package german.randle.qsort

import kotlin.random.Random

fun quickSortSequential(arr: IntArray) {
    fun qSortRecursive(l: Int, r: Int) {
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

        qSortRecursive(l, border - 1)
        qSortRecursive(border, r)
    }

    qSortRecursive(0, arr.size)
}

fun quickSortParallel(arr: IntArray) {

}
