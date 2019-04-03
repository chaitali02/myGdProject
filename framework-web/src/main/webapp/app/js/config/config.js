InferyxApp=angular.module('InferyxApp');
//CF:CONSTANT_FLAG
InferyxApp.constant("CF_APP_SETTING",{
    "companyName":"INFERYX",
    "companyLog":"assets/layouts/layout/img/logo.png"

});


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
    tag : "tag",lov:"lov",graphpod:"graphpod",graphexec:"graphExec",report:"report",reportexec:"reportExec",
    reportview:"reportview",batch:"batch",batchexec:"batchExec",ingest:"ingest",ingestexec:"ingestExec",rule2:"rule2"
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


InferyxApp.constant('CF_SUCCESS_MSG',{
 ruleSave:"Rule Saved Successfully",
 ruleGroupSave:"Rule Groups Saved Successfully",
 ruleSaveExecute:"Rule Saved and Submitted Successfully",
 ruleGroupSaveExecute:"Rule Groups Saved and Submitted Successfully",
 dqSave:"DQ Rule Saved Successfully",
 dqGroupSave:"DQ Rule Groups Saved Successfully",
 dqSaveExecute:"DQ Rule Saved and Submitted Successfully",
 dqGroupSaveExecute:"DQ Rule Groups Saved and Submitted Successfully",
 profileSave:"Profile Rule Saved Successfully",
 profileGroupSave:"Profile Rule Groups Saved Successfully",
 profileSaveExecute:"Profile Rule Saved and Submitted Successfully",
 profileGroupSaveExecute:"Profile Rule Groups Saved and Submitted Successfully",
 rcSave:"RC Rule Saved Successfully",
 rcGroupSave:"RC Rule Groups Saved Successfully",
 rcSaveExecute:"RC Rule Saved and Submitted Successfully",
 rcGroupSaveExecute:"RC Rule Groups Saved and Submitted Successfully",
 modelDeployIsExist:"Model already deployed.if you continue old one wiil be undeployed and seleced will be deployed."
});
InferyxApp.constant('CF_ACTION_TYPES',{
view:"View",edit:"Edit",add:"Add",delete:"Delete",clone:"Clone",execute:"Execute",export:"Export",publish:"Publish",
unpublish:"Unpublish",restore:"Restore",export:"Export"
});

InferyxApp.constant('CF_LOV_TYPES',{
	tag:"TAG"
});

InferyxApp.constant('COLORPALETTE',{
    Palette_1:["#4a4e4d", "#0e9aa7","#3da4ab", "#f6cd61" ,"#fe8a71"],
    Palette_2:["#008744","#0057e7","#d62d20","#ffa700","#29a8ab"],
    Palette_3:["#edc951","#eb6841","#cc2a36","#4f372d","#00a0b0"],
    Random_4:["#73c6b6", "#f8c471", "#d98880", "#7dcea0", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#82e0aa", "#f7dc6f", "#f0b27a", "#e59866"] //Don't change key value
 });

InferyxApp.constant('CF_SAMPLE',{
    framework_sample_maxrows:1000,
    framework_sample_minrows:100,
    limit_to:6,
});

InferyxApp.constant('CF_DOWNLOAD',{
    framework_download_maxrow:10000,
    framework_download_minrows:100,
    limit_to:6,
    formate:["EXCEL","PDF"],
    layout:["LANDSCAPE","PORTRAIT"],
});

InferyxApp.constant('CF_GRID',{
    framework_autopopulate_grid:200,
   
});

InferyxApp.constant('CF_FUNCTION',{
    retrunType:["STRING", "BOOLEAN", "INTEGER", "DATE", "DOUBLE"]
})

InferyxApp.constant('CF_ENCODINGTYPE',{
    encodingType:["ORDINAL", "ONEHOT", "BINARY", "BASEN","HASHING"]
});

InferyxApp.constant('CF_THRESHOLDTYPE',{
    thresholdType:["NUMERIC", "PERCENTAGE", "STDDEV"]
});


InferyxApp.constant("CF_ORGANIZATION",{
    contactTitle:["Owner","Partner","Manager","Director","President","Vice-President","Associate","Other"],
    phoneType:["home","business","mobile","fax","other","number"],
    emailType:["personal","business","other"],
    addressType:["home","business","other"]
});

InferyxApp.constant("CF_FILTER",{
	operator :[{"caption":"EQUAL (=)","value":"="},{"caption":" NOT EQUAL (!=)","value":"!="},
	{"caption":"LESS THAN (<)","value":"<"},
	{"caption":"GREATER THAN (>) ","value":">"},
	{"caption":"LESS OR EQUAL (<=) ","value":"<="},
	{"caption":"GREATER OR EQUAL (>=)  ","value":">="},
	{"caption":"BETWEEN","value":"BETWEEN"},
	{"caption":"LIKE","value":"LIKE"},
	{"caption":"NOT LIKE","value":"NOT LIKE"},
	{"caption":"RLIKE","value":"RLIKE"},
	{"caption":"EXISTS","value":"EXISTS"},
	{"caption":"NOT EXISTS","value":"NOT EXISTS"},
	{"caption":"IN","value":"IN"},
    {"caption":"NOT IN","value":"NOT IN"},
    {"caption":"IS","value":"IS"}],
    
	lhsType : [{"text":"string","caption":"string"},
	{ "text": "string", "caption":"integer"},
	{ "text": "datapod","caption":"attribute"},
    { "text": "formula","caption":"formula"}],
    
	rhsType : [{ "text": "string", "caption": "string","disabled":false },
	{ "text": "string", "caption": "integer" ,"disabled":false },
	{ "text": "datapod", "caption": "attribute","disabled":false },
	{ "text": "formula", "caption": "formula","disabled":false },
    { "text": "dataset", "caption": "dataset" ,"disabled":false },
    { "text":  "paramlist", "caption": "paramlist" ,"disabled":false },
    { "text": "function", "caption": "function" ,"disabled":false }]
});

InferyxApp.constant("CF_GRAPHPOD",{
    allType:["datapod","dataset"],
    nodeIcon:[{"caption":"Home","value":"home","class":"fa fa-home"},
    {"caption":"User","value":"user","class":"fa fa-user"},
    {"caption":"Bank","value":"bank","class":"fa fa-university"},
    {"caption":"Office","value":"office","class":"fa fa-building"},
    {"caption":"Dollar","value":"doller","class":"fa fa-usd"}], 
    nodeIconMap:{"home":{"caption":"Home","value":"home","class":"fa fa-home","code":"\uf015","color":"#5C9BD1"},
    "user":{"caption":"User","value":"user","class":"fa fa-user","code":"\uf007","color":"#8877a9"},
    "bank":{"caption":"Bank","value":"bank","class":"fa fa-university","code":"\uf19c","color":"#2ab4c0"},
    "office":{"caption":"Office","value":"office","class":"fa fa-building","code":"\uf1ad","color":"#f36a5a"},
    "dollar":{"caption":"Dollar","value":"dollar","class":"fa fa-usd","code":"\f155","color":"#0db7ed"}},
    nodeHighlightType:[
        {"caption":"Category","value":"category"},
        {"caption":"Numerical","value":"numerical"}],
    nodeHighlightColor:["#B71C1C","#004D40","#FF9800","#BF360C","#0D47A1","#263238","#000000"],
    nodeBackGroundColor:["#Af9A9A","#B39DDB","#80DEEA","#BCAAA4","#B0BEC5"],
    edgeHighlightColor:["#CCD1D1","#A9DFBF","#F5B7B1"]//["#b71c1c","#004D40","#FF9800","#BF360C","#0D47A1","#263238","#000000"],
  
});

