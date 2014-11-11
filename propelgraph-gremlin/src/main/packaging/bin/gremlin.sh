#!/bin/sh
#
# This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
#
# This file is licensed to You under the Eclipse Public License (EPL);
# You may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.opensource.org/licenses/eclipse-1.0.php
#
# (C) Copyright ScaleGraph Team 2014.
#

CLPATH=
CLPATH=${CLPATH}:$( echo `dirname $0`/../lib/*.jar . | sed 's/ /:/g')
CLASSPATH=${CLPATH}
export CLASSPATH

mkdir -p tinkergraphstore
mkdir -p neo4jstores
# store directory for IBM System-G NativeStore
mkdir -p nativestore

java org.propelgraph.gremlin.Console $1 $2 $3 $4 $5 $6 $7

