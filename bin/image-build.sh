#!/usr/bin/env bash

set -e  # exit immediately for any error

if [ -f "/usr/local/bin/docker-machine" ]; then
    eval "$(docker-machine env default)"
fi

export img=log-access-service
# tag is the git sha
#export tag=`git log -1 | head -1 | cut -f 2 -d ' '`
# just use tag as 1 since no git in interview project    
export tag=1

mvn clean package

docker build -t $img:$tag .
docker tag $img:$tag $img:latest

# list images to visually confirm success
docker images $img
