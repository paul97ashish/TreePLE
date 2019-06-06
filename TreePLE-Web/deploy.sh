#!/bin/bash
# This only works on the production server
# Jenkins makes something that works anywhere a pain
# so this just moves the build to the folder of a 
# running service
WWW_LOC=/var/lib/jenkins/http/www/
cd "$(pwd -P)/$(dirname $0)"
npm install
npm run build
cp -r dist/* $WWW_LOC
