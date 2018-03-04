#*******************************************************************************
# Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
#
# This unpublished material is proprietary to GridEdge Consulting LLC.
# The methods and techniques described herein are considered  trade 
# secrets and/or confidential. Reproduction or distribution, in whole or 
# in part, is forbidden.
#
# Written by Yogesh Palrecha <ypalrecha@gridedge.com>
#*******************************************************************************
hostname=$1
collection=$2
uuid=$3

if [[ $hostname = "" ]] ; then
        echo Usage: update_application.sh [hostname]
	exit 1
fi

var=`/home/joy/Documents/mongodb-linux-x86_64-ubuntu1404-3.4.2/bin/mongo --quiet --host localhost framework --eval "db.getCollectionNames().join('\n')"`
for collection in $(echo $var)
do

	echo Updating $collection collection
	/home/joy/Documents/mongodb-linux-x86_64-ubuntu1404-3.4.2/bin/mongo --host $hostname framework --eval "printjson(db.${collection}.updateMany( {}, {\$set: {\"createdOn\":ISODate()}}, {multi:true}))"
done
