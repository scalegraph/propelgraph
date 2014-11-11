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

#try to turn off debug logging
#CLASSPATH=/usr/share/java/slf4j/nop.jar:~/.m2/repository/org/slf4j/slf4j-nop/1.6.6/slf4j-nop-1.6.6.jar:${CLASSPATH}:target/classes/

#CLASSPATH=/usr/share/java/junit4.jar:${CLASSPATH}:
CLASSPATH=~/.m2/repository/junit/junit/4.11/junit-4.11.jar:${CLASSPATH}:
export CLASSPATH
echo "classpath is "$CLASSPATH

mkdir -p target/test-data
java   org.junit.runner.JUnitCore org.propelgraph.memgraph.test.TinkerGraphTest


