drop table if exists dp_rule_results;
create table dp_rule_results(	
	datapoduuid varchar(50) default 0 not null,
	datapodversion varchar(50),
	datapodname varchar(100),
	attributeid varchar(50),
	attributename varchar(100),
	numrows varchar(50),
	minval decimal(20,4),
	maxval decimal(20,4),
	avgval decimal(20,4),
	medianval decimal(20,4),
	stddev decimal(20,4),
	numdistinct integer(10),
	perdistinct decimal(20,4),
	numnull integer(10),
	pernull decimal(20,4),
	sixsigma decimal(20,4),
	version integer(10));
