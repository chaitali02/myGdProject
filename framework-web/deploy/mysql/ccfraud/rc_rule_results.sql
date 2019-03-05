drop table if exists rc_rule_results;
create table rc_rule_results(	
	sourceuuid varchar(50) default 0 not null,
	sourceversion varchar(50),
	sourcename varchar(100),
	sourceattributeid varchar(50),
	sourceattributename varchar(100),
	sourcevalue decimal(10,2),
	targetuuid varchar(50) default 0 not null,
	targetversion varchar(50),
	targetname varchar(100),
	targetattributeid varchar(50),
	targetattributename varchar(100),
	targetvalue decimal(10,2),
	status varchar(50),
	version integer(10));
