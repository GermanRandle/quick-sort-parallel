package german.randle.qsort

import kotlin.random.Random

val random = Random(18)

fun generateRandomArray(size: Int): IntArray {
    return IntArray(size) { random.nextInt() }
}

fun IntArray.isSorted(): Boolean {
    for (i in 1..<size) {
        if (this[i - 1] > this[i]) {
            return false
        }
    }
    return true
}
