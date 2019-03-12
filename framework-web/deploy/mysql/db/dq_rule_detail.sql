drop table if exists dq_rule_detail;
 create table dq_rule_detail (
rule_uuid varchar(50),
rule_version int(10) ,
rule_name varchar(50),
datapod_uuid varchar(50),
datapod_version int(10) ,
datapod_name varchar(50),
attribute_id varchar(50),
attribute_name varchar(50),
attribute_value varchar(500),
rowkey_name varchar(500),
rowkey_value varchar(500),
null_check_pass varchar(1) ,
value_check_pass varchar(1) ,
range_check_pass varchar(1) ,
datatype_check_pass varchar(1) ,
format_check_pass varchar(1) ,
length_check_pass varchar(1) ,
ri_check_pass varchar(1) ,
dup_check_pass varchar(1) ,
custom_check_pass varchar(1) ,
domain_check_pass varchar(1) ,
blankspace_check_pass varchar(1) ,
expression_check_pass varchar(1) ,
version int(10));


 
