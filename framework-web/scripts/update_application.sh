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
collection=$2
uuid=$3

if [[ $hostname = "" || $collection = "" || $uuid = "" ]] ; then
        echo Usage: update_application.sh [hostname] [collection] [uuid]
	exit 1
fi

echo Adding application info to $collection collection
if [[ $collection != "datapod" ]] ; then
	mongo --host $hostname framework --eval "printjson(db.${collection}.updateMany( {}, {\$push: {\"appInfo\":{ \"ref\" : { \"type\" : \"application\", \"uuid\" : \"${uuid}\" } } }}, {multi:true}))"
else
	mongo --host $hostname framework --eval "printjson(db.${collection}.updateOne( {name:\"dq_rule_results\"}, {\$push: {\"appInfo\":{ \"ref\" : { \"type\" : \"application\", \"uuid\" : \"${uuid}\" } } }}, {}))"
fi
