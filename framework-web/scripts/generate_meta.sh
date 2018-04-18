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
dbname=$2
collection=$3
outfolder=$4

if [[ $hostname = "" || $dbname = "" || $collection = "" || $outfolder = "" ]] ; then
        echo Usage: generate_meta.sh [hostname] [database] [collection] [outfolder]
	exit 1
fi
echo Exporting object info for $collection collection
var=`mongo --quiet --host $hostname $dbname --eval "db.$collection.find({},{"_id":1}).sort({"version":1}).toArray()" | grep "_id" | awk -F":" '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//'`
for obj in $(echo $var)
do
	echo Exporting $collection object $obj
	name=`mongo --quiet --host $hostname $dbname --eval "printjson(db.$collection.find({"_id":$obj},{name:1,uuid:1}).toArray())" | grep "name" | awk -F":" '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//' | tr ' ' '_' | xargs`
	if [[ $name != "" ]] ; then
		mongo --quiet --host $hostname $dbname --eval "db.$collection.find({"_id":$obj}, {_id:0}).forEach(printjson);" > $outfolder/$name.json
		#eval mongoexport --pretty --host $hostname --db $dbname --collection $collection -f | R 'omit ["_id"]' -q '{"_id":$obj}' --out $outfolder/$name.json
	else
		echo "Error: Name not available"
	fi
done
