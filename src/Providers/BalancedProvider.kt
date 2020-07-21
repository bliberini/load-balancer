package Providers

class BalancedProvider(val provider : IProvider) {
    private var included : Boolean = true

    val isIncluded : Boolean
        get() = this.included

    fun exclude() {
        this.included = false
    }

    fun include() {
        this.included = true
    }
}