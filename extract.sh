#!/usr/bin/env bash

###########################################
#
# Easy Start Script for Grade Distiller
#
# Created: Jan 1, 2018
# License: MIT
#
###########################################

# Change the values of the environment variables below for your installation and Bb Learn URL
export BB_URL="https://my-site.blackboard.com"
export BB_KEY="68ecd4c9-3e8b-4e27-9527-5d646285ac30"
export BB_SECRET="hpeWLBEX32Bzcyr8USEJ"

# This may need to be changed depending on how the bundle is distributed
PATH_TO_JAR="build/libs/grade-distiller-1.0.jar"

# Start the Distiller
java -jar ${PATH_TO_JAR}
