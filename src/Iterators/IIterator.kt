package Iterators

import Providers.BalancedProvider

interface IIterator {
    fun getNext(providers : List<BalancedProvider>) : Int
}