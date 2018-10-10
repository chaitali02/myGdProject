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
#rm -r run_all.hql
#rm -r create_all.sql 
cp load.sql load_bck.sql
cp counts.sql counts_bck.sql
for file in *.hql
do
        if [[ $file != "load_small.sql" && $file != "load_medium.hql" && $file != "create_db_user.sql" && $file != "counts.sql" && $file != "load.sql" ]] ; then
			echo >> create_all.sql
            cat $file >> create_all.sql
			#sed -i 's/DROP TABLE IF EXISTS /DROP TABLE IF EXISTS '$1'./g' create_all_new.sql 
			#sed -i 's/CREATE TABLE /CREATE TABLE '$1'./g' create_all_new.sql
			
			#sed -i 's/Copy /Copy '$1'./g' load.sql
        fi;
done
sed -i 's/TRUNCATE TABLE /TRUNCATE TABLE '$1'./g' load_bck.sql
sed -i 's/from /from '$1'./g' counts_bck.sql
			
#psql -U postgres -c \ $1 
#psql -U postgres -d $1 < create_all_new.sql
#psql -U postgres -d $1 < load_bck.sql
#psql -U postgres -d $1 < counts_bck.sql
rm -r load_bck.sql
rm -r counts_bck.sql

