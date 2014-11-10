propelgraph-gremlin
===========

This is a demo of Gremlin.  It utilizes the LocatableGraphFactory 
support and the CreateGraph class to 
make it easier to support a variety of graph implementations.



Prereqs
=========
- git (optional, `yum install git` )
- Java 1.6 or higher ( `yum install java-1.7.0-openjdk-devel` )
- Maven (to conveniently compile) ( `yum install maven` )
- Any prereqs for the graph implementations you choose 
 - System-G - Default: (Linux, Intel64)  Mac:(Intel64)
 

Installation
============

```
wget https://github.com/scalegraph/propelgraph/archive/master.zip ; unzip master.zip
cd propelgraph-master/propelgraph-gremlin
```

If you'd like to try Neo4j or IBM's NativeStore or NativeMem implementations, 
uncomment the appropriate part of the pom.xml file with your favorite text
editor.  (Warning: Neo4j uses the Affero license.  PLease understand the 
implications of that before using our sample Neo4j interface.)  Then...

```
./makepackage.sh
```
This should create a tar.gz of the project containing everything you need. It will
also create a subdirectory where you can try it out immediately.


Try it out
==========
```
cd propelgraph-gremlin-2.4.0
bin/gremlin.sh
```

You are now in the Gremlin shell.  To learn more about Gremlin, try a tutorial like this [one](http://www.tinkerpop.com/docs/wikidocs/gremlin/2.4.0/Home.html).


One component of propelgraph is propelgraph-util.  One of the capabilities it provides is a concise way to construct some common
graph implementations you might have included with you program.  Another capability it provides is a CSV file populator.  You can
use them here.  Here we use the in-memory
version of TinkerGraph ("tinkermem").  See [propelgraph-util](https://github.com/scalegraph/propelgraph/tree/master/propelgraph-util) 
to learn more about these capabilities and others.

```
g = CreateGraph.openGraph("tinkermem","my_awesome_graph")
new LoadCSV().populateFromVertexFile(g, "data/movies.movies.v.csv", "movies", 5555555)
new LoadCSV().populateFromEdgeFile(g, "data/movies.appearances.e.csv", "appearances", 5555555)
```

You now have a graph ("g") that is initialized with some movie and actor data from the local
hard disk.

We can now use Gremlin to calculate "collaborative filter".  This is just a way to find
vertices that share similar neighbors as the one of interest.
```
v = g.v(20)
v._exid
v.both.both.dedup()._exid
```

One feature of propelgraph is the ability to call various analytics against graphs that adhere
to the TinkerPop Blueprints interface, but also call, when available, analytic implementations
built in to a graph implementation.   Because these implementations of knowledge of the underlying
graph implementations, they generally run faster.
Here we do this with an analytic called "collaborative filtering".
```
CollaborativeFilter.simpleCF(v, Direction.BOTH, null)
```

Help
====
- ccjason@us.ibm.com
- https://github.com/scalegraph/propelgraph
- https://github.com/scalegraph/propelgraph/tree/master/propelgraph-gremlin
