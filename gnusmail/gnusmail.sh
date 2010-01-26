#!/bin/sh

gnusmail_path=${0%/*}
java -javaagent:${gnusmail_path}/lib/sizeofag.jar -jar -Xmx5G ${gnusmail_path}/gnusmail.jar
