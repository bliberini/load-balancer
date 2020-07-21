package LoadBalancer

import Iterations.IIteration
import Providers.BalancedProvider
import Providers.IProvider
import Providers.Provider
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class LoadBalancer(private val iterator : IIteration, private val maxRequestsPerProvider : Int) : ILoadBalancer {
    private var balancedProviders : List<BalancedProvider> = mutableListOf<BalancedProvider>()
    private val currentRequestsSharedLock = ReentrantLock()
    private val currentProviderSharedLock = ReentrantLock()
    private var currentRequests : Int = 0

    override fun registerProviders(providers: List<IProvider>) {
        require(providers.size <= 10) { "Max number of providers is 10" }
        this.balancedProviders = providers.map { provider -> BalancedProvider(provider) }
        this.check()
    }

    override fun get(): String {
        require(this.balancedProviders.isNotEmpty()) { "Load balancer does not have registered providers" }
        this.increaseCurrentRequests()
        val nextProvider = this.getNextProvider()
        println("Assigned provider $nextProvider")
        val value = this.balancedProviders[nextProvider].provider.get()
        this.decreaseCurrentRequests()
        return value
    }

    override fun include(providerId: String) {
        var provider = this.balancedProviders.find { bp -> bp.provider.id() == providerId }
        provider?.include()
    }

    override fun exclude(providerId: String) {
        var provider = this.balancedProviders.find { bp -> bp.provider.id() == providerId }
        provider?.exclude()
    }

    override fun check() {
        thread(start = true) {
            var positiveChecksById = mutableMapOf<String, Int>()
            while(true) {
                for (bp in this.balancedProviders) {
                    if (!bp.provider.check()) {
                        println("Excluding provider ${bp.provider.id()}")
                        this.exclude(bp.provider.id())
                        positiveChecksById[bp.provider.id()] = 0
                    } else if (!bp.isIncluded) {
                        positiveChecksById[bp.provider.id()] = positiveChecksById.getOrDefault(bp.provider.id(), 0) + 1
                    }

                    if (positiveChecksById[bp.provider.id()] == 2) {
                        println("Including provider ${bp.provider.id()}")
                        this.include(bp.provider.id())
                        positiveChecksById[bp.provider.id()] = 0
                    }
                }
                Thread.sleep(2000)
            }
        }
    }

    private fun increaseCurrentRequests() {
        try {
           this.currentRequestsSharedLock.lock()
            if (this.currentRequests == this.maxRequestsPerProvider * this.balancedProviders.size) {
               throw Exception("Too many concurrent requests")
           }
           this.currentRequests++
        } finally {
            this.currentRequestsSharedLock.unlock()
        }
    }

    private fun decreaseCurrentRequests() {
        try {
            this.currentRequestsSharedLock.lock()
            this.currentRequests--
        } finally {
            this.currentRequestsSharedLock.unlock()
        }
    }

    private fun getNextProvider() : Int{
        try {
            this.currentProviderSharedLock.lock()
            return this.iterator.getNext(this.balancedProviders)
        } finally {
            this.currentProviderSharedLock.unlock()
        }
    }
}