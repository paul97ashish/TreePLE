#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Web archive to copy must be specified."
    exit 1
elif [ $# -eq 1 ]; then
    if [ -n "$TOMCAT_DIR" ]; then
        echo "Tomcat installed at $TOMCAT_DIR"
    else
        echo "TOMCAT_DIR must be set or specified as the second argument."
        exit 1
    fi
elif [ $# -eq 2 ]; then
    TOMCAT_DIR=$2
else
    echo "USAGE: deploy.sh [war location] [TOMCAT_DIR (optional if set)]"
    exit 1
fi

rm -rf $TOMCAT_DIR/webapps/ROOT $TOMCAT_DIR/webapps/ROOT.war
cp $1 $TOMCAT_DIR/webapps/ROOT.war
