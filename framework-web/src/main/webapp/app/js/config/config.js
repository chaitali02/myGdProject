InferyxApp=angular.module('InferyxApp');

InferyxApp.constant("CONSTANT_FOR_FILTER",{
	operator :[{"caption":"Equal to","value":"="},
	{"caption":"Less than","value":"<"},
	{"caption":"Greater than","value":">"},
	{"caption":"Less than or equal to","value":"<="},
	{"caption":"Greater than or equal to","value":">="},
	{"caption":"BETWEEN","value":"BETWEEN"},
	{"caption":"LIKE","value":"LIKE"},
	{"caption":"NOT LIKE","value":"NOT LIKE"},
	{"caption":"RLIKE","value":"RLIKE"},
	{"caption":"EXISTS","value":"EXISTS"},
	{"caption":"NOT EXISTS","value":"NOT EXISTS"},
	{"caption":"IN","value":"IN"}]
});

InferyxApp.constant("META_TYPE",[
	{"datapod":"datapod"},{"dataset":"datapod"}
	]);