package Iterators

import Providers.BalancedProvider
import kotlin.random.Random

class RandomIterator : IIterator {
    override fun getNext(providers : List<BalancedProvider>): Int {
        var index : Int = Random.nextInt(0, providers.size - 1)
        while (!providers[index].isIncluded) {
            index = Random.nextInt(0, providers.size - 1)
        }
        return index
    }
}