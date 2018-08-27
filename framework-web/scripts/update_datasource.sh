###############################################################################
# Copyright (C) Inferyx Inc, 2018 All rights reserved. 
#
# This unpublished material is proprietary to Inferyx Inc.
# The methods and techniques described herein are considered  trade 
# secrets and/or confidential. Reproduction or distribution, in whole or 
# in part, is forbidden.
#
# Written by Yogesh Palrecha <ypalrecha@inferyx.com>
###############################################################################
hostname=$1
collection=datapod
uuid=$2

if [[ $hostname = "" || $collection = "" || $uuid = "" ]] ; then
        echo Usage: update_datasource.sh [hostname] [uuid]
	exit 1
fi

echo Updating datasource in datapod collection
mongo --host $hostname framework --eval "printjson(db.${collection}.updateMany( {}, {\$set: {\"datasource\":{ \"ref\" : { \"type\" : \"datasource\" , \"uuid\" : \"${uuid}\" } } }}, {multi:true}))"
