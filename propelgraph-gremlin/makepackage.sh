#!/bin/sh

VER=2.4.0
GDIR=propelgraph-gremlin-${VER}

mvn clean
mvn -U package
rm -Rf ${GDIR}
mkdir ${GDIR}
mvn dependency:copy-dependencies
mv target/dependency ${GDIR}/lib
cp target/propelgraph-gremlin-2.4.0-SNAPSHOT.jar ${GDIR}/lib
cp -r src/main/packaging/* ${GDIR}/
#mkdir ${GDIR}/bin
#cp src/main/bin/* ${GDIR}/bin/
#mkdir ${GDIR}/doc
#cp -r src/main/doc/* ${GDIR}/doc/
tar -cvzf ${GDIR}.tar.gz  ${GDIR}

# the following code creates a Windos compatible gremlin.bat file and then propelgraph.zip
cd  ${GDIR}
TT=$(echo lib/*.jar | sed 's/ /;/g')
echo java -cp $TT com.ibm.research.systemg.gremlin.groovy.console.Console "\1 \2 \3 \4 \5" >bin/gremlin.bat
cd ..
zip -r propelgraph.zip ${GDIR}

#cp ${GDIR}.tar.gz  /var/www/html/xxx/...
#cp propelgraph.zip /var/www/html/xxx/...
