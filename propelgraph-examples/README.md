#Examples


## Purpose
This subdirectory of the PropelGraph project simply demonstrates how to create a project 
that creates a Java program that uses PropelGraph.  You can copy and modify this
subdirectory/project as a starting point for your own Java graph project.

## Building
The project here is a standard Maven project, respecting Mavan conventions. Your steps are:

1. Install Java JDK 6.x or higher [link](http://openjdk.java.net/) - normally 
   this can be done with
   a `yum` or `apt-get` command as root on Linux.  One can also install it 
   via the instructions [here](http://openjdk.java.net/).  When correctly
   installed you should be able to invoke the `javac` command on the command line.

2. Install Maven - This also can usually be installed via `yum` or `apt-get`.  If that
   is not possible, one can find 
   instructions [here](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

3. Download [PropelGraph](https://github.com/scalegraph/propelgraph) - typically you'd do that 
   with `git`, but you can also simply download it and unpack it from 
   [github](https://github.com/scalegraph/propelgraph/archive/master.zip).

4. Go to the propelgraph-examples subdirectory
  ```
  cd propelgraph/propelgraph-examples
  ```

5. Invoke Maven on the project

  ```
  mvn package
  ```

6. Run the program
   For the MakeGraph program, try:
  ```
  ./makegraph.sh
  ```



## MakeGraph
This example program simply creates a graph and populates it.  The current version of this program
choses to open a PropelGraph MemGraph (aka "propelmem").  If it were to chose a
persistent graph, graph files would be left in the appropriate graph subdirectory.  
