drop table if exists dq_rule_summary;
 create table dq_rule_summary (
 rule_uuid        varchar(50),
 rule_version     int(10),
 rule_name        varchar(100),
 datapod_uuid     varchar(50),
 datapod_version  int(10),
 datapod_name     varchar(50),
 total_row_count  int(10),
 total_pass_count int(10),
 total_fail_count int(10),
 threshold_type   varchar(50),
 threshold_limit  int(3),
 threshold_ind    varchar(5),
 score            int(3),
 version          int(10));

