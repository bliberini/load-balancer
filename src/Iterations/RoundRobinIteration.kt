package Iterations

import Providers.BalancedProvider

class RoundRobinIteration : IIteration {
    private var posCurrent : Int = 0

    override fun getNext(providers : List<BalancedProvider>): Int {
        var next = -1
        if (providers.isNotEmpty()) {
            if (providers.size <= this.posCurrent) {
                this.posCurrent = 0
            }
            while (!providers[this.posCurrent].isIncluded) {
                this.posCurrent++
            }
            next = this.posCurrent
            if (providers.size != 1) {
                this.posCurrent = (this.posCurrent + 1) % providers.size
            }
        } else {
            this.posCurrent = 0
        }
        return next
    }
}