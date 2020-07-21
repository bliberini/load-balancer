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

Represents a load balancer. The Loadbalancer class constructor takes two parameters: an `IIterator` object and an integer K. The `IIterator` object determines the strategy with which the load balancer will distribute the load. The integer K fixes the max number of concurrent requests each provider can handle. If the load balancer detects that a request made would put the system above the threshold (that being: K * number of providers), it will throw an error rejecting that request.

The load balancer needs to have a number of providers registered to be invoked, otherwise it will throw an error. Likewise, the number of providers to be registered must be <= 10. This list is a list of Provider objects which are mapped to BalancedProvider objects in order to keep track of their status within the load-balancing scheme.

The load balancer distributes the request load among its registered providers for their methods `get()`. The LoadBalancer class method `get()` checks with the `IIterator` property which is the next provider and hands it the request. It also checks every 2s that all the providers are alive by invoking their `check()` method. It will then decide to exclude them or re-include them in the list of available providers depending on whether they were able to reply to this check in a positive way or not (instead of setting a timer to see if the provider answers on time or not, for simplification purposes). This doesn't interrupt the workflow of the LoadBalancer object, however, as it's executed in a separate thread.

### IIterator

The `IIterator` interface defines a method for deciding which provider should be invoked next by the load balancer. It is implemented by the `RandomIterator` class, which picks a provider randomly with equal chance, and the `RoundRobinIterator` which picks the next provider using the round-robin algorithm. If the `RoundRobinIterator` object detects that the next provider isn't available (for instance, because the list of providers has been replaced) it will restart the iteration from the "0"th provider and move forward looking for the first non-excluded provider.
