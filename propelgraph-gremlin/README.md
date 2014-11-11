propelgraph-gremlin
===========

This is a demo of Gremlin.  It utilizes the LocatableGraphFactory 
support and the CreateGraph class to 
make it easier to support a variety of graph implementations.



Prereqs
=========
- git (optional, `yum install git` )
- unzip (optional, 'yum install unzip' )
- Java JDK 1.6 or higher ( `yum install java-1.7.0-openjdk-devel` )
- Maven (to conveniently compile) ( `yum install maven` )
- Any prereqs for the graph implementations you choose 
 - System-G - Default: (Linux, Intel64)  Mac:(Intel64)
 

Installation
============

First let's download the PropelGraph source code and cd to the gremlin demo directory:

```
wget https://github.com/scalegraph/propelgraph/archive/master.zip ; unzip master.zip
cd propelgraph-master/propelgraph-gremlin
```

Next we're going to build the demo.  
If you'd like to try Neo4j or IBM's NativeStore or NativeMem implementations, 
uncomment the appropriate part of the pom.xml file with your favorite text
editor.  (Warning: Neo4j uses the Affero license.  PLease understand the 
implications of that before using our sample Neo4j interface.) :

```
./makepackage.sh
```
This should create a tar.gz of the project containing everything you need. It will
also create a subdirectory where you can try the demo immediately.


Try it out
==========
```
cd propelgraph-gremlin-2.4.0
bin/gremlin.sh
```

You are now in the Gremlin shell.  This is probably a good opportunity to learn more about Gremlin.  You will
find many good tutorials on the web.  We recommend this [one](http://www.tinkerpop.com/docs/wikidocs/gremlin/2.4.0/Home.html).


One subproject of the PropelGraph project is called propelgraph-util.  It provides a variety of capiabilities.  
One of those [capabilities](http://scalegraph.github.io/propelgraph/propelgraph-util/javadoc/) is a concise 
way to construct some common graph implementations you might have included with you program.  Here we use the 
PropelGraph MemGraph ("propelmem"):

```
g = CreateGraph.openGraph("propelmem","my_awesome_graph")
```

Another [capability]((http://scalegraph.github.io/propelgraph/propelgraph-util/javadoc/)) propelgraph-util 
provides is file loading for CSV or Metis files.  Here we use the CSV loading capability to load some
sample CSV files included in the tutorial:

```
new LoadCSV().populateFromVertexFile(g, "data/movies.movies.v.csv", "movies", 555000111)
new LoadCSV().populateFromEdgeFile(g, "data/movies.appearances.e.csv", "appearances", 555000111)
```

We now have a graph ("g") that is initialized with some movie and actor data from the local
hard disk.  The Gremlin console let's us use that graph in a variety of ways.  Please read a Gremlin
tutorial to learn more about those capabilities.   Here we assign a vertex of interest to a variable
called 'v':


```
v = g.v("Kevin Bacon")
```
This works on graphs implemented by "propelmem" and "tinkerpop", but for other graphs you might 
need to do something like `v = g.v(20)` instead.

One of the nicest features of Gremlin is the piping capability.   Here we use it to do a simple
two hop "collaborative filtering":

```
v.both.both.dedup()
```

Yet another feature of propelgraph-util is the ability to invoke analytics against graphs that adhere
to the TinkerPop Blueprints interface. That support includes generic implementations of various analytics, but 
if propelgraph-util finds that the graph has its own faster implementation of a given anaytic, it will delegate
to that implementation.  Here we do this with an analytic called "collaborative filtering".
```
CollaborativeFilter.simpleCF(v, Direction.BOTH, null)
```

Help
====
- ccjason@us.ibm.com
- https://github.com/scalegraph/propelgraph
- https://github.com/scalegraph/propelgraph/tree/master/propelgraph-gremlin
