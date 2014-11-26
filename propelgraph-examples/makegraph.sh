#!/bin/sh





add_maven_deps_to_classpath() {
  # Need to generate classpath from maven pom. This is costly so generate it                                                        
  # and cache it. Save the file into our target dir so a mvn clean will get                                                         
  # clean it up and force us create a new one.                                                                                      
  f="target/cached_classpath.txt"
  if [ ! -f "${f}" ]
  then
    mvn dependency:build-classpath -Dmdep.outputFile="${f}" &> /dev/null
  fi
  CLASSPATH=${CLASSPATH}:`cat "${f}"`
}

# Add maven target directory                                                                                                        
add_maven_deps_to_classpath
CLASSPATH=${CLASSPATH}:target/classes
CLASSPATH=${CLASSPATH}:target/test-classes


export CLASSPATH

java org.propelgraph.examples.MakeGraph $1 $2 $3 $4 $5 

