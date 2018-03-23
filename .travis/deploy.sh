#!/bin/sh

# Get GAV
groupId="$(sed -n 's/^	<groupId>\(.*\)<\/groupId>$/\1/p' pom.xml)"
groupIdForURL="$(echo $groupId | sed -e 's/\./\//g')"
artifactId="$(sed -n 's/^	<artifactId>\(.*\)<\/artifactId>$/\1/p' pom.xml)"
version="$(sed -n 's/^	<version>\(.*\)<\/version>$/\1/p' pom.xml)"

if [ "$TRAVIS_OS_NAME" = "linux" ]
then
	classifier="linux64"
else
	classifier="macosx"
fi

executablePath="target/slim-curve-jar-1.0.0-SNAPSHOT.jar"

# if [ "$TRAVIS_SECURE_ENV_VARS" = true \
# 	-a "$TRAVIS_PULL_REQUEST" = false \
# 	-a "$TRAVIS_BRANCH" = master ]
# then
	mvn deploy:deploy-file \
		-Dfile="$executablePath" \
		-DrepositoryId="slim-curve.snapshots" \
		-Durl="file://C:/Users/gds12/Documents/code/slim-curve/slim-curve-master/deploy" \
		-DgeneratePom="false" \
		-DgroupId="$groupId" -DartifactId="$artifactId" -Dversion="$version" \
		-Dclassifier="$classifier" \
		-Dpackaging="jar"\
		-DuniqueVersion=false
