propelgraph
===========

Java API for property graphs

The goal of this project is to create a portable Java API for
performant property graph implementations.  It will provide a compatibility
layer for using TinkerPop implementations with this API, but the 
most performant implementations will support this API directly.

## Latency and Throughput
The API will be tuned somewhat to provide low latency and low memory
overhead for in-process property graph implementations, but it will also 
strive to provide an API that can allow implementations to provide
high throughput.

## Portability
The final goal of this API is to be truly portable so that calling
code need write as little code as possible that is specific to the
underlying implementation.  This does mean that occasionally this project
will provide slower API's than the underlying implementation if a
single truly portable performant interface is not possible.  Even in 
that case, a more performant API might also be provided if a number
of property graph implementations might be able to support it.

## Simplification, not a goal

Where possible, this API will build upon TinkerPop Blueprints.  Due
to the goals above, implementations of this API will have more interfaces
than a simple TinkerPop implementation.   This is to reduce impedance
mismatch between calling code and implementation.  As an example,
API's supporting "int" will be provided in addition to API's supporting
"java.lang.Integer". This prevents applications from the burden of
creation and garbage collection of Integer objects if the calling 
application only needs primitive int support.

-----------------

## JavaDoc

propelgraph-impl - [javadoc](http://drewvale.github.io/propelgraph/propelgraph-impl/javadoc/fixthislink)

propelgraph-interfaces - [javadoc](http://drewvale.github.io/propelgraph/propelgraph-interfaces/javadoc/fixthislink)


## Demo

Most of PropelGraph is interfaces and helper functions.  That tends not to demo
easily, but you can still easily experiment with PropelGraph by following the
instructions in the [propelgraph-gremlin section](fixthislink) of this project.
