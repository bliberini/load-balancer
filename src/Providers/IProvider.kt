package Providers

interface IProvider {
    fun id() : String
    fun get() : String
    fun check() : Boolean
}