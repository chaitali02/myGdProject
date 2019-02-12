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

if [[ $hostname = "" ]] ; then
        echo Usage: create_indexes.sh [hostname]
	exit 1
fi

echo Index Creation Started
mongo --host $hostname framework -eval "db.datapod.ensureIndex({'name': 'text', 'desc': 'text', 'attributes.name': 'text', 'attributes.desc': 'text'})"
echo Index Creation Completed
