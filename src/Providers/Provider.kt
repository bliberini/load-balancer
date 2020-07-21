package Providers

import java.util.*

open class Provider(private val heartBeatOKChance : Int) : IProvider {
    private val id: String = UUID.randomUUID().toString()

    override fun id() : String {
        return this.id
    }

    override fun get(): String {
        println("Provider $id processing...")
        Thread.sleep(1000)
        return id
    }

    override fun check() : Boolean {
        println("Provider $id heart beat checking...")
        return Math.random() <= this.heartBeatOKChance
    }
}