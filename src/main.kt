import Iterations.RandomIteration
import Iterations.RoundRobinIteration
import LoadBalancer.LoadBalancer
import Providers.IProvider
import Providers.Provider
import java.util.stream.IntStream.range
import kotlin.concurrent.thread

fun main(args : Array<String>) {
    val K : Int = 2
    var loadBalancer = LoadBalancer(RandomIteration(), K)

    println(">> Check that load balancer can't answer get() if there aren't any providers registered")
    try {
        println(loadBalancer.get())
    } catch (e : Exception) {
        println(e.message)
    }

    println(">> Check that load balancer doesn't allow more than 10 providers")
    try {
        loadBalancer.registerProviders(mutableListOf<IProvider>(
            Provider(90), Provider(90), Provider(80),
            Provider(90), Provider(90), Provider(80),
            Provider(90), Provider(90), Provider(80),
            Provider(90), Provider(90), Provider(80)
        ))
    } catch (e : Exception) {
        println(e.message)
    }

    loadBalancer.registerProviders(mutableListOf<IProvider>(
        Provider(90), Provider(90), Provider(80),
        Provider(90), Provider(90), Provider(80),
        Provider(90), Provider(90), Provider(80)
    ))

    println(">> Sequential random iteration of providers")
    for (i in range(0, 10)) {
        println("Received value = ${loadBalancer.get()}")
    }

    loadBalancer = LoadBalancer(RoundRobinIteration(), K)
    loadBalancer.registerProviders(mutableListOf(
        Provider(50), Provider(50), Provider(50)
    ))

    println(">> Concurrent round robin iteration of providers")
    for (i in range(0, 2)) {
        thread(start=true) {
            while (true) {
                try {
                    loadBalancer.get()
                } catch (e : Exception) {
                    println("${e.message}")
                }
            }
        }
    }
}