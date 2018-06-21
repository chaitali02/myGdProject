InferyxApp=angular.module('InferyxApp');

InferyxApp.constant("CONSTANT_FOR_FILTER",{
	operator :[{"caption":"EQUAL TO","value":"="},
	{"caption":"LESS THEN","value":"<"},
	{"caption":"GREATER THEN","value":">"},
	{"caption":"LESS / EQUAL","value":"<="},
	{"caption":"GREATER / EQUAL","value":">="},
	{"caption":"BETWEEN","value":"BETWEEN"},
	{"caption":"LIKE","value":"LIKE"},
	{"caption":"NOT LIKE","value":"NOT LIKE"},
	{"caption":"RLIKE","value":"RLIKE"},
	{"caption":"EXISTS","value":"EXISTS"},
	{"caption":"NOT EXISTS","value":"NOT EXISTS"},
	{"caption":"IN","value":"IN"},
    {"caption":" NOT IN","value":"NOT IN"}]
});

InferyxApp.constant("META_TYPE",[
	{"datapod":"datapod"},{"dataset":"datapod"}
	]);