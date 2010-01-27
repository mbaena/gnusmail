#!/bin/bash

gnusmail_path=${0%/*}

if [[ $gnusmail_path == $0 ]]
then
gnusmail_path="."
fi

java -javaagent:${gnusmail_path}/lib/sizeofag.jar -jar -Xmx5G ${gnusmail_path}/gnusmail.jar $*
