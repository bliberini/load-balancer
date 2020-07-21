# Load balancer

## Goal

This project shows the behavior of a load balancing component that distributes the load among its registered providers. Writen in Kotlin

## Design

### Provider class

Represents a service provider. Each provider is uniquely identified through an UUID string. The service it provides is represented through the method `get()`, which simply returns the provider's id after a 1s delay (to represent more exhaustive processing than simply returning a string). The Provider class constructor receives an argument that specifies its "availability percentage", which comes to play in the method `check()`, which mimics a heartbeat endpoint.

For verification purposes, it also exposes a getter for the Provider's id. This is so that identifying each provider doesn't depend on the `get()` method, which includes a 1s delay simply meant to emulate a more complex process than fetching the id. In a real scenario there would be a simple getter to identify the provider and its service methods would be separate from it.

### BalancedProvider

Class created to hold status data of each provider the load balancer requires but wouldn't be part of the Provider class' responsibility. In particular, it holds the state of whether that provider is included in the load balancing or not and methods to toggle these states.

### LoadBalancer

