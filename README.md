# RHub - Reactive Event Hub

## [JavaDocs](http://apptik.github.io/RHub/)

[![Build Status](https://travis-ci.org/apptik/RHub.svg?branch=master)](https://travis-ci.org/apptik/RHub)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-rxHub-green.svg?style=true)](https://android-arsenal.com/details/1/4260)

Reactive Hub is a collection of multi-receiver and multi-producer Proxies connecting Publishers 
and Subscribers so that Subscribers can receive events without knowledge of which Publishers, if 
any, there are, while maintaining easily identifiable connection between them.

It is ideal for centralizing cross-cutting activities like 
UI updates, logging, alerting, monitoring, security, etc.

## Motivation

*   Simplified combination of Pub/Sub (EventBus) pattern and Reactive Programming
*   Most 'Rx-EventBuses' implementations support only non Rx input/output of events
*   In Standard EventBus the link between producers and event consumers is not that evident thus 
making the code more difficult to follow, reason and debug 

## Example

```java
	RxHub rxHub = new DefaultRxJava1Hub();
	rxHub.getPub("src1").subscribe(System.out::println);
	rxHub.addUpstream("src1", Observable.just(1));
	rxHub.addUpstream("src1", Observable.just(5));
	rxHub.getPub("src1").subscribe(System.err::println);
```


## More Examples
* [java examples]
* [Android examples(with jack)]

## Overview

![RHub](https://raw.githubusercontent.com/apptik/rhub/master/img/RHub.png)

RHub allows Publish/Subscribe pattern implementation and thus helps
decouple components. This can be compared to an EventBus where:

*   publishing events may happen via single post using 'emit()' or by adding a Publisher
*   RHub Proxies take care of connecting publishers and consumers to the Hub
*   The subscriptions and the flow of events is handled by the Reactive Framework (RxJava, Reactor, etc.)

In RHub different set of events are separated into Proxies identified by a Tag(topic).

*   a Hub contains Proxies which can merge inputs from 0-n Publishers and post to 0-n Subscribers
*   a Proxy is identified by a Tag
*   a single event from non-rx code can be emitted on a Proxy 

Check also the [Gherkin Features][features]

## Modules
* [core][core] - core module containing the very basic interfaces of RHub
[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/core.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/core)
[![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:core/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:core/0.1.0)
* [rxjava-v1][rxjava-v1] - RxJava 1.x implementation of RHub
[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/rxjava-v1.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/rxjava-v1)
[![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:rxjava-v1/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:rxjava-v1/0.1.0)
* [rxjava-v2][rxjava-v2] - RxJava 2.x implementation of RHub
[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/rxjava-v2.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/rxjava-v2)
[![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:rxjava-v2/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:rxjava-v2/0.1.0)
* [reactor][reactor] - Reactor Core implementation of RHub
[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/reactor.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/reactor)
[![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:reactor/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:reactor/0.1.0)
* [rs-core][rs-core] - Reactive Streams generic implementation of RHub
[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/rs-core.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/rs-core)
[![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:rs-core/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:rs-core/0.1.0)
* Roxy (Reactive Proxy) - single fan-in/fan-out unit in RHub
	* [roxy-core][roxy-core] - core Roxy module containing the very basic interfaces
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/roxy-core.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/roxy-core)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:roxy-core/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:roxy-core/0.1.0)
    * [roxy-rs][roxy-rs] - Reactive Strems generic implementation of Roxy
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/roxy-rs.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/roxy-rs)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:roxy-rs/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:roxy-rs/0.1.0)
    * [roxy-rxjava1][roxy-rxjava1] - RxJava 1.x implementation of Roxy
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/roxy-rxjava1.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/roxy-rxjava1)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:roxy-rxjava1/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:roxy-rxjava1/0.1.0)
    * [roxy-rxjava2][roxy-rxjava2] - RxJava 2.x implementation of Roxy
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/roxy-rxjava2.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/roxy-rxjava2)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:roxy-rxjava2/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:roxy-rxjava2/0.1.0)
    * [roxy-reactor][roxy-reactor] - Reactor Core implementation of Roxy
    [![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/roxy-reactor.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/roxy-reactor)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:roxy-reactor/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:roxy-reactor/0.1.0)
* Shield - Type-safe handy layer on top of RHub
	* [shield-annotations][shield-annotations] - Common Shield Annotations
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-annotations.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-annotations)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-annotations/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-annotations/0.1.0)
	* [shield-base-maker][shield-base-maker] - base module for Shield Maker responsible to create instances for a given Shield interface
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-base-maker.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-base-maker)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-base-maker/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-base-maker/0.1.0)
    * [shield-maker-rs][shield-maker-rs] - Shield Maker module for Reactive Streams Hubs
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-maker-rs.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-maker-rs)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rs/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rs/0.1.0)
    * [shield-maker-rxjava1][shield-maker-rxjava1] - Shield Maker module for RxJava 1.x Hubs
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-maker-rxjava1.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-maker-rxjava1)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rxjava1/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rxjava1/0.1.0)
    * [shield-maker-rxjava2][shield-maker-rxjava2] - Shield Maker module for RxJava 2.x Hubs
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-maker-rxjava2.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-maker-rxjava2)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rxjava2/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-maker-rxjava2/0.1.0)
    * [shield-base-processor][shield-base-processor] - base Annotation Processor module
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-base-processor.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-base-processor)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-base-processor/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-base-processor/0.1.0)
    * [shield-processor-rxjava1][shield-processor-rxjava1] - Annotation Processor module for RxJava1
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-processor-rxjava1.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-processor-rxjava1)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava1/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava1/0.1.0)
    * [shield-processor-rxjava2-subj][shield-processor-rxjava2-subj] - Annotation Processor module for RxJava2 Subject/Observable types
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-processor-rxjava2-subj.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-processor-rxjava2-subj)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava2-subj/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava2-subj/0.1.0)
    * [shield-processor-rxjava2-proc][shield-processor-rxjava2-proc] - Annotation Processor module for RxJava2 Processor/Flowable types
	[![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-processor-rxjava2-proc.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-processor-rxjava2-proc)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava2-proc/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-rxjava2-proc/0.1.0)
    * [shield-processor-reactor][shield-processor-reactor] - Annotation Processor module for Reactor Core
    [![Maven Central](https://img.shields.io/maven-central/v/io.apptik.rhub/shield-processor-reactor.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.apptik.rhub/shield-processor-reactor)
    [![VersionEye](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-reactor/0.1.0/badge.svg)](https://www.versioneye.com/java/io.apptik.rhub:shield-processor-reactor/0.1.0)


## Download

Find [the latest JARs][mvn] or grab via Maven:
```xml
<dependency>
  <groupId>io.apptik.rhub</groupId>
  <artifactId>XXX</artifactId>
  <version>0.1.0</version>
</dependency>
```
or Gradle:
```groovy
compile 'io.apptik.rhub:XXX:0.1.0'
```

Downloads of the released versions are available in [Sonatype's `releases` repository][release].

Snapshots of the development versions are available in [Sonatype's `snapshots` repository][snap].


## Questions

[StackOverflow with tag 'rhub' or 'apptik'](http://stackoverflow.com/questions/ask)

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


[mvn]: http://search.maven.org/#search|ga|1|io.apptik.rhub.core
 [release]: https://oss.sonatype.org/content/repositories/releases/io/apptik/rhub/core
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/io/apptik/rhub/core
 [features]: https://github.com/apptik/RHub/tree/master/rxjava-v1/src/test/resources/features
 [core]: https://github.com/apptik/RHub/tree/master/core/
 [rxjava-v1]: https://github.com/apptik/RHub/tree/master/rxjava-v1/
 [rxjava-v2]: https://github.com/apptik/RHub/tree/master/rxjava-v2/
 [reactor]: https://github.com/apptik/RHub/tree/master/reactor/
 [rs-core]: https://github.com/apptik/RHub/tree/master/rs-core/
 [roxy-core]: https://github.com/apptik/RHub/tree/master/roxy/roxy-core/
 [roxy-rs]: https://github.com/apptik/RHub/tree/master/roxy/roxy-rs/
 [roxy-rxjava1]: https://github.com/apptik/RHub/tree/master/roxy/roxy-rxjava1/
 [roxy-rxjava2]: https://github.com/apptik/RHub/tree/master/roxy/roxy-rxjava2/
 [roxy-reactor]: https://github.com/apptik/RHub/tree/master/roxy/roxy-reactor/
 [shield-annotations]: https://github.com/apptik/RHub/tree/master/shield/shield-annotations/
 [shield-base-maker]: https://github.com/apptik/RHub/tree/master/shield/shield-base-maker/
 [shield-maker-rs]: https://github.com/apptik/RHub/tree/master/shield/shield-maker-rs/
 [shield-maker-rxjava1]: https://github.com/apptik/RHub/tree/master/shield/shield-maker-rxjava1/
 [shield-maker-rxjava2]: https://github.com/apptik/RHub/tree/master/shield/shield-maker-rxjava2/
 [shield-base-processor]: https://github.com/apptik/RHub/tree/master/shield/shield-base-processor/
 [shield-processor-rxjava1]: https://github.com/apptik/RHub/tree/master/shield/shield-processor-rxjava1/
 [shield-processor-rxjava2-subj]: https://github.com/apptik/RHub/tree/master/shield/shield-processor-rxjava2-subj/
 [shield-processor-rxjava2-proc]: https://github.com/apptik/RHub/tree/master/shield/shield-processor-rxjava2-proc/
 [shield-processor-reactor]: https://github.com/apptik/RHub/tree/master/shield/shield-processor-reactor/
 [java examples]: https://github.com/apptik/RHub/tree/master/example-java/
 [Android examples(with jack)]: https://github.com/apptik/RHub/tree/master/example-app/