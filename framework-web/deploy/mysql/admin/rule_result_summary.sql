drop table if exists rule_result_summary;
create table rule_result_summary
(
	 rule_uuid varchar(50),
	  rule_version int(10),
	  rule_name varchar(100),
	  entity_type varchar(50),
	  entity_id varchar(50),
    score decimal(6,3),
  version int(10)
);
