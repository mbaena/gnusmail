#!/bin/sh

gnusmail_path=${0%/*}
if [[ $gnusmail_path == $0 ]]
then
gnusmail_path="."
fi

echo $gnusmail_path
java -javaagent:${gnusmail_path}/lib/sizeofag.jar -jar -Xmx5G ${gnusmail_path}/gnusmail.jar
