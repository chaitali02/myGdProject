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
        echo Usage: rull_all.sh [dbname]
        exit 1
fi
rm -r create_all.sql
for file in *.sql
do
        if [[ $file != "load_small.sql" && $file != "load_medium.hql" && $file != "create_db_user.sql" && $file != "counts.sql" && $file != "load.sql" ]] ; then
		echo >> create_all.sql
                cat $file >> create_all.sql
        fi;
done
mysql -u inferyx -p -v $1 < create_all.sql
mysql -u inferyx -p -v $1 --local-infile < load.sql
mysql -u inferyx -p -v $1 < counts.sql
