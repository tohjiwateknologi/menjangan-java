# <img src="src/docs/menjangan-rpcp-library.png" width="80" height="80"> Menjangan-java

This is the home of the Menjangan-java Library: High performance RPCP Java implementation.
Menjangan implement RPCP in some programing language you can find it at GitHub page [Tohjiwa Teknologi](https://github.com/tohjiwateknologi).
For detail RPCP Specification you can find [here](https://github.com/tohjiwateknologi/rpcp).

Menjangan provides everything required you want to add RealTime Procedure to your project.
Menjangan can work with all major Java framework : Spring, Micronaut, Quarkus, Jakarta EE, and Java SE.
In theory menjangan can work everywhere you can have Bidirectional connection, but in most use case menjangan
used in top of websocket.

It was originally written by Eric A. Sanjaya, and has 
been in development since 2022. It is currently developed by a community of developers.

## How add to a project

### Maven
```xml
<dependency>
    <groupId>io.github.tohjiwateknologi.menjangan</groupId>
    <artifactId>menjangan-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```
Work in progress
```

## Documentation

Work in progress

## Demo

You can see how RPCP & Menjangan work before spending time to implement your self for your next project.
we have prepared for you public server with menjangan on top of Spring Framework :
```
host : rpcp.tohjiwateknologi.co.id
vhost : demo.lets-test

method: tester.add, tester.sum
event: event.hello
```

First create WebSocket Connection to host : rpcp.tohjiwateknologi.co.id

And then create RPCP Connection with Send a Connect Request
```
CONNECT demo.lets-test RPCP/1.0
agent: gle-openapi-js/1.0
```

And test by Send a Call Request
```text
CALL tester.add
content-type: application/json
id: 002

{"a": 10, "b": 20}
```
_* faster way connect to demo server you can use [RPCP Tester](https://www.github.com/tohjiwateknologi) tools_

## Tools

### RPCP Tester
Small web based RPCP Client application to test your RPCP server. with these tools, you can connect, call a procedure,
and receive an event from server. you can download source code [here](https://www.github.com/tohjiwateknologi).

## Stay in Touch

--

## Credits
A list of contributors may be found from CREDITS file, which is included in some artifacts (usually source distributions).
but is always available from the source code management (SCM) system project uses.

## License

The Menjangan Library is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).