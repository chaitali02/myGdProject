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
dbuser='inferyx'

if [[ $dbname = "" ]] ; then
        echo Usage: rull_all.sh [dbname]
        exit 1
fi
#rm -r run_all.hql
rm create_all.sql 
cp load_wi.sql load_wi_bck.sql
#cp load_wo.sql load_wo_bck.sql
cp counts.sql counts_bck.sql

for file in *.sql
do
        if [[ $file != "truncate_wi.sql" && $file != "truncate_wo.sql"  && $file != "counts_bck.sql" && $file != "counts.sql" && $file != "load_wo.sql" ]] ; then
        #echo >> create_all.sql
            cat $file >> create_all.sql            
			 
        fi;
done
cp create_all.sql create_all_bck.sql
sed -i 's/DROP TABLE IF EXISTS /DROP TABLE IF EXISTS '$dbname'./g' create_all_bck.sql
sed -i 's/CREATE TABLE /CREATE TABLE '$dbname'./g' create_all_bck.sql
sed -i 's/TRUNCATE TABLE /TRUNCATE TABLE '$dbname'./g' load_wi_bck.sql
sed -i 's/TRUNCATE TABLE /TRUNCATE TABLE '$dbname'./g' load_wo_bck.sql
sed -i 's/FROM /FROM '$dbname'./g' counts_bck.sql
sed -i 's/Copy /Copy '$dbname'./g' load_wi_bck.sql
sed -i 's/Copy /Copy '$dbname'./g' load_wo_bck.sql
		
psql -U $dbuser -d $dbname < create_all_bck.sql
psql -U $dbuser -d $dbname < load_wi_bck.sql
psql -U $dbuser -d $dbname < load_wo_bck.sql
psql -U $dbuser -d $dbname < counts_bck.sql

#rm create_all_bck.sql
#rm load_wi_bck.sql
#rm load_wo_bck.sql
#rm counts_bck.sql

