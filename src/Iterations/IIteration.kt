package Iterations

import Providers.BalancedProvider

interface IIteration {
    fun getNext(providers : List<BalancedProvider>) : Int
}