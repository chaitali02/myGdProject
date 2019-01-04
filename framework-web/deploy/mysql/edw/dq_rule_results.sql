drop table if exists dq_rule_results;
create table dq_rule_results(	
	rowkey varchar(50),
	datapoduuid varchar(50),
	datapodversion varchar(50),
	datapodname varchar(100),
	attributeid varchar(50),
	attributename varchar(100),
	attributevalue varchar(50),
	nullcheck_pass varchar(50),
	valuecheck_pass varchar(50),
	rangecheck_pass varchar(50),
	datatypecheck_pass varchar(50),
	dataformatcheck_pass varchar(50),
	lengthcheck_pass varchar(50),
	refintegritycheck_pass varchar(50),
	dupcheck_pass varchar(50),
	customcheck_pass varchar(50),
	version integer(10));
