#!/bin/bash

if [ -z "$1" ]
    then
        echo "No data path supplied"
else
    tar -xvf $1 -C ./spark/data
    echo "data file was extracted to ./spark/data"
    echo "starting pipeline"
    docker-compose up
fi
