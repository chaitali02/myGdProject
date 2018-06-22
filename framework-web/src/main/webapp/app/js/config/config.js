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
	{"caption":" NOT IN","value":"NOT IN"}],
	lhsType : [{"text":"string","caption":"string"},
	{ "text": "string", "caption":"integer"},
	{ "text": "datapod","caption":"attribute"},
	{ "text": "formula","caption":"formula"}],
	rhsType : [{ "text": "string", "caption": "string","disabled":false },
	{ "text": "string", "caption": "integer" ,"disabled":false },
	{ "text": "datapod", "caption": "attribute","disabled":false },
	{ "text": "formula", "caption": "formula","disabled":false },
	{ "text": "dataset", "caption": "dataset" ,"disabled":false }]
});


InferyxApp.constant('CONSTANT_FOR_META_Type',{
    activity : "ACTIVITY",algorithm : "ALGORITHM",application : "APPLICATION",condition : "CONDITION",
    dag : "DAG",dagexec : "DAG_EXEC",dashboard : "DASHBOARD",dq : "DQ",dqexec : "DQ_EXEC",
    dqgroup : "DQ_GROUP",dqgroupexec : "DQ_GROUP_EXEC",datastore : "DATASTORE",datapod : "DATAPOD",
    dataset : "DATASET",datasource : "DATASOURCE",dimension : "DIMENSION",expression : "EXPRESSION",
    filter : "FILTER",formula : "FORMULA",function : "FUNCTION",group : "GROUP",load : "LOAD",
    loadexec : "LOAD_EXEC",map : "MAP",mapexec : "MAP_EXEC",measure : "MEASURE",model : "MODEL",
    modelexec : "MODEL_EXEC",paramlist : "PARAMLIST",paramset : "PARAMSET",privilege : "PRIVILEGE",
    profile : "PROFILE",profileexec : "PROFILE_EXEC",profilegroup : "PROFILE_GROUP",
    profilegroupexec : "PROFILE_GROUP_EXEC",relation : "RELATION",role : "ROLE",rule : "RULE",
    ruleexec : "RULE_EXEC",rulegroup : "RULE_GROUP",rulegroupexec : "RULE_GROUP_EXEC",
    session : "SESSION",user : "USER",vizpod : "VIZPOD",vizexec : "VIZ_EXEC",usergroup : "USER_GROUP",
    simple : "SIMPLE",file : "FILE",matrixmult : "MATRIXMULT",mapiter : "MAPITER",dqview : "DQVIEW",
    ruleview : "RULEVIEW",datasetview : "DATASETVIEW",meta : "META",dashboardview : "DASHBOARDVIEW",
    Import : "IMPORT",export : "EXPORT",message : "MESSAGE",log : "LOG",downloadExec : "DOWNLOAD_EXEC",
    uploadexec : "UPLOAD_EXEC",predict : "PREDICT",predictexec : "PREDICT_EXEC",simulate : "SIMULATE",
    simulateexec : "SIMULATE_EXEC",train : "TRAIN",trainexec : "TRAIN_EXEC",recon : "RECON",
    reconexec : "RECON_EXEC",recongroup : "RECONGROUP",recongroupexec : "RECONGROUP_EXEC",
    reconview : "RECONVIEW",distribution : "DISTRIBUTION",appConfig : "APPCONFIG",
    operatorexec : "OPERATOR_EXEC",operator : "OPERATOR",comment : "COMMENT",commentView : "COMMENTVIEW",
	tag : "TAG",lov : "LOV",graphpod:"graphpod",graphexec:"graphExec"
});