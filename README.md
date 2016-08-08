# RxHub

[![Build Status](https://travis-ci.org/apptik/rxhub.svg?branch=master)](https://travis-ci.org/apptik/rxhub)

RxJava based Observables Hub connecting Observables and Observers so that 
Observers can subscribe to an Event Source without knowledge of which 
Observables, if any, there are, while maintaining clear connection between them.

It is ideal for centralizing cross-cutting activities like 
UI updates, logging, alerting, monitoring, security, etc.

-- Disclaimer --

This is in experimental stage. The API may change at any time.

## Download

Find [the latest JARs][mvn] or grab via Maven:
```xml
<dependency>
  <groupId>io.apptik.rxhub</groupId>
  <artifactId>core</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
or Gradle:
```groovy
compile 'io.apptik.rxhub:core:0.0.1-SNAPSHOT'
```

Downloads of the released versions are available in [Sonatype's `releases` repository][release].

Snapshots of the development versions are available in [Sonatype's `snapshots` repository][snap].


## Motivation

*   Simplified combination of Pub/Sub (EventBus) pattern and Reactive Programming
*   Most EventBus implementations support only non Rx input/output of events
*   EventBus connections with producers and event consumers are not that evident thus making the code more difficult to read, reason and debug 



## Example

```java
	RxHub rxHub = new DefaultRxHub();
	rxHub.getNode("src1").subscribe(System.out::println);
	rxHub.addProvider("src1", Observable.just(1));
	rxHub.addProvider("src1", Observable.just(5));
	rxHub.getNode("src1").subscribe(System.err::println);
```

## Overview

![RxHub](https://raw.githubusercontent.com/apptik/rxhub/master/img/RxHub.png)

RxHub allows Publish/Subscribe pattern implementation and thus helps
decouple components. This might be compared to an EventBus where:

*   publishing events may happen via single post using 'emit()' 
or by adding an Observable
*   RxHub takes care of connecting publishers and consumers to the Hub
*   while subscribing to and passing events is handled by RxJava

In RxHub different set of events are separated into Nodes identified by a tag.

Main Concepts and Features:
(Hub, Node, Tag, Producer, Consumer)

*   a Hub contains Nodes which are rx.Observables
*   a Node represents a stream of events
*   a Node is identified by a Tag
*   a single event from non-rx code can be emitted on a Node 
*   one or more Producers can be added to a Node
*   Node can be returned by the Hub and then multiple Consumers can subscribe to it

Check also the [Gherkin Features][features]

## Considerations

*   RxHub Nodes by default are not type-safe. Custom implementations or wrappers of
 RxHub (prefferably extending AbstractRxHub or DefaultRxHub), might fix this 
 by exposing type-safe getters.
*   Using emit() interfere with original streams and might break those, thus its usage is not encouraged. 
Using RxHub in fully reactive code emit should be disabled or removed.
*   ObservableRef node type is not really unsubscribed form the original source,
because it is the original source. This might cause confusion.
*   Backpressure handling is not done in the nodes where probably it makes the most sense to do that.
Backpressure strategy interface could be used and applied to nodes in the hub

## Licence

    Copyright (C) 2016 AppTik Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[mvn]: http://search.maven.org/#search|ga|1|io.apptik.rxhub.core
 [release]: https://oss.sonatype.org/content/repositories/releases/io/apptik/rxhub/core
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/io/apptik/rxhub/core
 [features]: https://github.com/apptik/rxHub/tree/master/lib/src/test/resources/features
 