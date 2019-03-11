drop table if exists dq_rule_summary;
 create table dq_rule_summary (
    rule_uuid varchar(50),
    rule_version integer(10),
    rule_name varchar(50),
    datapod_uuid varchar(50),
    datapod_version integer(10),
    datapod_name varchar(50),
    all_check_pass varchar(1),
    total_row_count integer(10),
    total_pass_count integer(10),
    total_fail_count integer(10),
    threshold_type varchar(50),
    threshold_limit integer(3),
    threshold_ind varchar(5),
    score decimal(22,1),
    version integer(10));
