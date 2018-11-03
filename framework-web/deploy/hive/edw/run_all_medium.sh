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
dbname=$1

if [[ $dbname = "" ]] ; then
        echo Usage: rull_all_medium.sh [dbname]
        exit 1
fi
rm -r run_all.hql
for file in *.hql
do
        if [[ $file != "load_medium.hql" ]] ; then
                cat $file >> run_all_medium.hql
        fi;
done
hive -database $1 -f run_all_medium.hql
hive -database $1 -f load_medium.hql
impala-shell -q "invalidate metadata"
impala-shell -d $1 -f impala_counts.iql
