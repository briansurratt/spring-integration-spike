# Spring Integration Spike
My experiments with Spring Integration in Kotlin.  See the [Spring Integration Reference Guide](https://docs.spring.io/spring-integration/docs/current/reference/html/index.html) for details about the library.   Especially [Java DSL](https://docs.spring.io/spring-integration/docs/current/reference/html/dsl.html#java-dsl) and  [Kotlin DSL](https://docs.spring.io/spring-integration/docs/current/reference/html/kotlin-dsl.html#kotlin-dsl).

When possible, I've leveraged the Kotlin's support for multiple declarations in a single file so all the code for a spike is in one place.  This should make the small bits of code easy to review.  

Each example can be triggered using HTTP GET request.  URLs will be included with each 

## Experiments:

### File Batch Aggregation

A custom [aggregator](https://www.enterpriseintegrationpatterns.com/patterns/messaging/Aggregator.html) to collect files in a batch which finalized with a tombstone or terminator message.

Code is located in `d.s.s.i.s.aggregator.AggregatorExample.kt`.   The example can be triggered via the URL http://localhost:8080/example/aggregator.

You can expect to see something like this in the console:
```
messages = 1
messages = 2
messages = 3
messages = 4
messages = 5
messages = 6
Aggregator!
FileMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
FileMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
FileMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
FileMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
FileMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
TombstoneMessage(batchId=8dcb253b-8666-46cf-9e6b-9e14e18f73f2)
```

