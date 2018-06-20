InferyxApp=angular.module('InferyxApp');

InferyxApp.constant("fILTER_OPERATOR",{
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