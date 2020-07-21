package LoadBalancer
import Providers.IProvider

interface ILoadBalancer {
    fun registerProviders(providers : List<IProvider>)
    fun get() : String
    fun include(providerId : String)
    fun exclude(providerId : String)
    fun check()
}