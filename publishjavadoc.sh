#!/bin/sh

# This code is derrived from code located here: https://github.com/jmkristian/bigFloat/blob/master/publish.sh
#
# Push javadoc files to a website hosted by github <http://pages.github.com/>.
#
# Before executing this script, 
#   - cd to the appropriate subproject directory 
#   - invoke   mvn clean install
#      This should insure that the target/apidocs directory is populated with the current javadocs.
#   - invoke ../publishjavadoc.sh while Cd'd in to that appropriate directory
#
git checkout gh-pages || exit $?
# Clear out the old files:
rm -rf javadoc/* 
# Replace them with new files and commit them:
cp -pr target/apidocs/* javadoc/ \
&& git add javadoc \
&& git commit -a -m "generated javadoc"
ERROR=$?
git checkout master || exit $?
[ $ERROR -eq 0 ] || exit $ERROR
git push github master || exit $?
git push github gh-pages || exit $?