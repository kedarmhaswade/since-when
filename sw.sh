#!/bin/bash
set -o nounset
set -o errexit

mvn clean install



if [[ $# -lt 2 ]]; then
    echo "Usage: $0 <path-to-src.zip> <some-name-for-class> <since-version>"
    echo "e.g., on a Mac, it could be: $0 /Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/src.zip Array 1.8"
    exit 2
fi

if [[ $# -ne 3 ]]; then
    SINCE=1.8
else
    SINCE=$3
fi
java -jar target/java-source-since-final.jar $1 $2 ${SINCE}