InferyxApp=angular.module('InferyxApp');
//CF:CONSTANT_FOR
InferyxApp.constant('CF_META_TYPES',{
    activity : "activity",algorithm : "algorithm",application : "application",condition : "condition",
    dag : "dag",dagexec : "dagExec",dashboard : "dashboard",dq : "dq",dqexec : "dqExec",
    dqgroup : "dqgroup",dqgroupexec : "dqgroupExec",datastore : "datastore",datapod : "datapod",
    dataset : "dataset",datasource : "datasource",dimension : "dimension",expression : "expression",
    filter : "filter",formula : "formula",function : "function",group : "group",load : "load",
    loadexec : "loadExec",map : "map",mapexec : "mapExec",measure : "measure",model : "model",
    modelexec : "modelExec",paramlist : "paramlist",paramset : "paramset",privilege : "privilege",
    profile : "profile",profileexec : "profileExec",profilegroup : "profilegroup",
    profilegroupexec : "profilegroupExec",relation : "relation",role : "role",rule : "rule",
    ruleexec : "ruleExec",rulegroup : "rulegroup",rulegroupexec : "rulegroupExec",
    session : "session",user : "user",vizpod : "vizpod",vizexec : "vizExec",usergroup : "usergroup",
    simple : "simple",file : "file",matrixmult : "matrixmult",mapiter : "mapiter",dqview : "dqview",
    ruleview : "ruleview",datasetview : "datasetview",meta : "meta",dashboardview : "dashboardview",
    import : "Import",export : "export",message : "message",log : "log",downloadExec : "downloadExec",
    uploadexec : "uploadExec",predict : "predict",predictexec : "predictExec",simulate : "simulate",
    simulateexec : "simulateExec",train : "train",trainexec : "trainExec",recon : "recon",
    reconexec : "reconExec",recongroup : "recongroup",recongroupexec : "recongroupExec",
    reconview : "reconview",distribution : "distribution",appConfig : "appConfig",
    operatorexec : "operatorExec",operator : "operator",comment : "comment",commentView : "commentView",
	tag : "tag",lov:"lov",graphpod:"graphpod",graphexec:"graphExec"
});


/*InferyxApp.constant('CF_META_TYPES',{
    activity : "ACTIVITY",algorithm : "ALGORITHM",application : "APPLICATION",condition : "CONDITION",
    dag : "DAG",dagExec : "DAG_EXEC",dashboard : "DASHBOARD",dq : "DQ",dqExec : "DQ_EXEC",
    dqgroup : "DQ_GROUP",dqgroupExec : "DQ_GROUP_EXEC",datastore : "DATASTORE",datapod : "DATAPOD",
    dataset : "DATASET",datasource : "DATASOURCE",dimension : "DIMENSION",expression : "EXPRESSION",
    filter : "FILTER",formula : "FORMULA",function : "FUNCTION",group : "GROUP",load : "LOAD",
    loadExec : "LOAD_EXEC",map : "MAP",mapExec : "MAP_EXEC",measure : "MEASURE",model : "MODEL",
    modelExec : "MODEL_EXEC",paramlist : "PARAMLIST",paramset : "PARAMSET",privilege : "PRIVILEGE",
    profile : "PROFILE",profileExec : "PROFILE_EXEC",profilegroup : "PROFILE_GROUP",
    profilegroupExec : "PROFILE_GROUP_EXEC",relation : "RELATION",role : "ROLE",rule : "RULE",
    ruleExec : "RULE_EXEC",rulegroup : "RULE_GROUP",rulegroupExec : "RULE_GROUP_EXEC",
    session : "SESSION",user : "USER",vizpod : "VIZPOD",vizExec : "VIZ_EXEC",usergroup : "USER_GROUP",
    simple : "SIMPLE",file : "FILE",matrixmult : "MATRIXMULT",mapiter : "MAPITER",dqview : "DQVIEW",
    ruleview : "RULEVIEW",datasetview : "DATASETVIEW",meta : "META",dashboardview : "DASHBOARDVIEW",
    Import : "IMPORT",export : "EXPORT",message : "MESSAGE",log : "LOG",downloadExec : "DOWNLOAD_EXEC",
    uploadExec : "UPLOAD_EXEC",predict : "PREDICT",predictExec : "PREDICT_EXEC",simulate : "SIMULATE",
    simulateExec : "SIMULATE_EXEC",train : "TRAIN",trainExec : "TRAIN_EXEC",recon : "RECON",
    reconExec : "RECON_EXEC",recongroup : "RECONGROUP",recongroupExec : "RECONGROUP_EXEC",
    reconview : "RECONVIEW",distribution : "DISTRIBUTION",appConfig : "APPCONFIG",
    operatorExec : "OPERATOR_EXEC",operator : "OPERATOR",comment : "COMMENT",commentView : "COMMENTVIEW",
    tag : "TAG",lov : "LOV",GenerateData : "GENERATE_DATA",Transpose : "TRANSPOSE",CloneData : "CLONE_DATA",
	GenDataAttr : "GEN_DATA_ATTR",GenDataValList: 
});*/

InferyxApp.constant('CF_ACTION_TYPES',{
view:"View",edit:"Edit",add:"Add",delete:"Delete",clone:"Clone",execute:"Execute",export:"Export",publish:"Publish",
unpublish:"Unpublish",restore:"Restore",export:"Export"
});

InferyxApp.constant('CF_LOV_TYPES',{
	tag:"TAG"
});

InferyxApp.constant("CF_FILTER",{
	operator :[{"caption":"EQUAL TO (=)","value":"="},
	{"caption":"LESS THAN","value":"<"},
	{"caption":"GREATER THAN","value":">"},
	{"caption":"LESS OR EQUAL","value":"<="},
	{"caption":"GREATER OR EQUAL (>=) ","value":">="},
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

InferyxApp.constant("CF_GRAPHPOD",{
    nodeIcon:[{"caption":"home","value":"home","class":"fa fa-home"},
    {"caption":"female","value":"female","class":"fa fa-female"},
    {"caption":"male","value":"male","class":"fa fa-male"},
    {"caption":"bank","value":"bank","class":"fa fa-university"},
    {"caption":"office","value":"office","class":"fa fa-building"},
    {"caption":"doller","value":"doller","class":"fa fa-usd"}]

})