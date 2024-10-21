package german.randle.qsort

import kotlin.random.Random

fun generateRandomArray(): IntArray {
    return IntArray(ARRAY_LENGTH) { Random.nextInt() }
}

fun IntArray.isSorted(): Boolean {
    for (i in 1..<size) {
        if (this[i - 1] > this[i]) {
            return false
        }
    }
    return true
}
