# Template Architecture, rationale

(document version 0.1)
The solution was based on the combination of good practices and simple tools.

## TL;TR

A simple service built with Koltin subroutines, Ktor, and simple libraries.

Advantages

* Full Kotlin, no Java-Kotlin mismatch
* Explicit behaviour, no annotations that hide implementation, dependencies, logic
* No special support needed from IDE, just normal Koltin language support
* No complex framework to learn, most of the code is plain Kotlin
* No complexity added with ReactiveX libraries for simple asynchronous calls. In case of (real) need to handle 
sequences, Kotlin `flow` construct can be used.
* Low resource consumption, low time to start, high throughput

Disadvantages

* Specials constructs should be used for parallel dispatching
* Code generation for scaffolding should be developed if wanted
* No coordination between running instances is provided
* Heavily depends on Kubernetes
* Requires to follow good Kotlin development practices
* Perhaps fewer plugins, addons, or other libraries available
* Not suitable for big monolithic
* Support for API documentation (OpenApi) should be investigated
* Libraries maturity?
* No fame in using simple tools

# Design goals

The original goals of the service was to provide a backed API for the new version of tha Apalabrados 2 game.

The original Apalabrados 1 has its own api (known as API v1) based in Java, some Spring, Restlet, and Jedis.

The goals for the architecture were:

* Allow to satisfy functional and non functional requirements
    * Performance
    * Observability
* Use Kotlin as development language (Etermax policy)
* Be deployed in Kubernetes (Etermax policy) taking advantage of the services provided by the platform
* Have a relative lowe learning curve

Based on that the main design decisions were:

* Use a Clean architecture to reduce dependencies on technologies
* Use Kotlin libraries and idiomatic
* Use Kubernetes services:
    * Authentication
    * Configuration
    * Service discovery
    * High availability
    * Metrics
    * Centralized logs processing

The particular design decision:

* Use Kotlin coroutines as mechanism for asynchronicity
* Use no framework for Dependency Injection
* User HTTP Rest for inter-service communication
* Use JSON for inter-service serialization
* Use KTor Server as HTTP server
* Use KTor Client as an HTTP Client
* Use kotlinx.json as JSON serialization
* Use Mockk for mocks (try to not use it too much)
* Use Arrow for functional programming
* Use prometheus for metrics
* Log to console for centralized logs processing  
* Use [Hoplite](https://github.com/sksamuel/hoplite) for configuration loading

Additionally, we followed the [Let it crash](https://wiki.c2.com/?LetItCrash) philosophy, lowering the need
of complex error handling, and leveraging the ability of Kubernetes to recovers services in case of failures.
Because of this and additional concern was to hace a very low Start Up time for the services.

# Decision rationale and experience

## Use Kotlin coroutines as mechanism for asynchronicity

Kotlin coroutines were selected because they are native to the language, so no new
framework or library is needed to learn.

For on time asynchronicity languages offer Future, Promises, or in case of ReactiveX, the Single pattern.
These mechanisms require the developer use a different mindset coding.

The coroutine mechanism allows to write code similar to synchronous one, and with simple `suspend`
annotation the language provides support for aysnchronous calls.

## Use no framework for Dependency Injection

A Dependency Injection library (or framework) allow through annotations to automatically create
factories for application objects. The injection can be by field (using reflexion), setter, or constructor.

Field injections breaks object encapsulation, and it is more complicated for testing.

Setter injecter can lead to partial object construction problems.

If constructor dependency injection, or copanion (staric) DI is performed, it can be done by simple object creation
(new). This can be done my simple (Kotlin) "provider" objects that has values for each object to be created.
The DI is performed while constructing the object. To have delayed object creation, the Kotlin `by lazy` idiom can be
used requiring no additional library. Additionally, the object dependency is explicit, no hidden wrapper is introduced
and no special IDE support is required.

## User HTTP Rest for inter-service communication

This is common decision that due to Clean Architecture can be replaced easily.

## Use JSON for inter-service serialization

JSON is simpler that XML alternative and it is a very popular serialization format used for inter process 
communication with HTTP connectivity.

## Use KTor Server as HTTP server

KTor is a Kotlin native, asynchronous HTTP Server library developed by the same team that developed Kotlin.

It leverages Kotlin coroutines for high performance.

Verte.X already has an HTTP Server based on a Reactor pattern. The Ktor HTTP Server directly use NIO support with
a simple desing. No need to master the Event loop to make the application high performance.

Also the need to create Verticles requires creating adapter, message handler, etc. In case the application to build
is single process, this interprocess communication mechanism adds complexity without adding value.

## Use KTor Client as an HTTP Client

Following same idea than HTTP Server, HTTP Client provides the base for implementing HTTP based gateways.

No need to learn different libraries.

Vert.X HTTP Client support had many problems. Other projects needed to replace it with a different library due to the
"mysterious" problems that made some request to be stalled.

## Use kotlinx.json as JSON serialization

Many JSON libraries are Java bases not managing the Kotlin idioms (case classes, non null variables, default values)
like GSon or requires an additional adapted like Jackson. Also this libraries requires object reflexion to do
the transformation.

kotlinx.json is a simple library written in Kotlin, that do not use reflexion. It builds converters at compile time,
and handle the Kotlin nuances.

## Use Mockk for mocks (try to not use it too much)

We'd rather use other Test doubles (Dummy, fakes, or stubs) by in case a mock is needed this library is Koltin native.

Support similar feature that other Java libraries with support for Kotlin and coroutines.

## Use Arrow for functional programming

Finally, not much functional programming were used, buy Array is a very good functional library for Kotlin. The
problem is that true functional library differ from the idioms of "cuasi" functional languages (Kotlin, Scala) and
some impedance mismatch exists.

## Use prometheus for metrics

The company already use prometheus as metrics collector, so we added the application the capability to expose metrics.

Prometheus does not have a native Kotlin library, so the Java version was used. Due to the fact that it is accesed
from specific points, the language mismatch were not a big problem.

We preferred to use the library directly than to use [Micrometer](https://micrometer.io/) beacuse it introduce an
additional layer of complexity without much value.

The main value of Micromenter is to allow to change the metrics store, use case that we supposed is very low probable.

The use of Prometheus client has the same complexity than using Micrometer, so we chosen the direct support.

With two simple adapter we were able to provide metrics for ingoing endpoints and for outgoing ones.

## Log to console for centralized logs processing

The collect logs required no more complexity that writing logs to console.

SLF4J was chosen because it is the new generation of Log4J the most popular logging framework.
  
## Use [Hoplite](https://github.com/sksamuel/hoplite) for configuration loading

Configuration was divided in two level:

    * Environment dependendant configuration
    * Environment independendant configuration

The last one is put into a YAML file and read thank to Hoplite library into an object hierarchy of configuration
object (simple data classes). The use of the library is just one line of code.

The environment dependent configuration should be but in environment variables, and configured through Kubernetes
manifests, or put into a ConfigMap and / or Secret objects.

The strategy for configuration changes, is just stop the application. Kubernetes will restart the container, and
the new configuration will be loaded. No complex logic for properties reloading should be coded.

## Let it crash philosophy

Instead of having complex logic to catch and recover for unexpected errors, the proposal was just to let the application
log the problem and die. The K8S support will restar the app in a known state.

This approach requires the application to start very fast, but this property is also excellent to allow the
application to horizontal scale by quickly adding new instances.

That also why special attention was put to selecting framework or libraries. The use reflexion or heavy framework
adds precious hundreds or milliseconds to the application start up, if not seconds. This goes against the ability
to serve in a microservice environment.

## Testing

For testing the JUnit 5 (Jupiter) test runtime was chosen. No special framework was used, but test follows
the BDD paradigm of Given, WHen, Then by using simple methods to document these elements.

The strategy was to do TDD in the Business code without delivery, and with stubs or mocked gateways and repositories.
In case of repositories usually a In Memory implementation was used instead of mocks.

Additionally, the delivery was tested mocking the actions to verify the conversion to from JSON and the handling of
HTTP parameters and response codes.

The gateway test included same as delivery, just JSON conversions and return code handling.

The DB embedded library was tested against an internal (docker based) Redis to verify the Redis specific logic 
behaviour, and the LUA syntax.

Also the archiecture was check with an Architectural Test. It was performed using the 
[ArchUnit](https://www.archunit.org/) library with a [PantUML](https://plantuml.com/) architecture diagram.

## Resilience

(Noy all implemented yet)

Support for Time out, Retry, circuit breaker, or other mechanism just by explicit decoration in the modules.