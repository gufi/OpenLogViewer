#!/bin/bash

mvn versions:set -DnewVersion=`git describe --dirty=-DEV`

