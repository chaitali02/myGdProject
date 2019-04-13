/****/
ProfileModule= angular.module('ProfileModule');
ProfileModule.controller('ProfileComapreController',function($state,$stateParams,$rootScope,$scope,ProfileService,$filter) {
    
    $scope.searchForm={}
    $scope.searchForm.datapod={};
    $scope.searchForm.attribute={};
    $scope.allTargetProfile;
    $scope.isSPPProgress=false;
    $scope.chartdata={};
    $scope.chartdata.datapoints=[];
    $scope.tz=localStorage.serverTz;
    var matches = $scope.tz.match(/\b(\w)/g);
    $scope.timezone=matches.join('')
    $scope.isDatapodChange=true;
    $scope.chartcolor=["#f1948a","#c39bd3","#73c6b6","#d98880","#82e0aa","#f7dc6f","#bb8fce","#f8c471","#7fb3d5","#76d7c4","#7dcea0","#f0b27a","#e59866",,"#85c1e9"];
    $scope.profileAttributes=[
                        { name:"All",caption:"All"},
                        { name:"minVal",caption:"minVal" },
                        { name:"maxVal",caption:"maxVal" },
                        { name:"avgVal",caption:"avgVal" },
                        { name:"medianVal",caption:"medianVal" },
                        { name:"stdDev",caption:"stdDev" },
                        { name:"numDistinct",caption:"numDistinct" },
                        { name:"perDistinct",caption:"perDistinct" },
                        { name:"numNull",caption:"numNull" },
                        { name:"perNull",caption:"perNull" },
                        { name:"sixSigma",caption:"sixSigma" },
                        ]
    $scope.profilePeriods=[
                        { name:"7",caption:" 1 Week" },
                        { name:"30",caption:"1 Month"},
                        { name:"90",caption:"3 Month"},
                        { name:"365",caption:"1 Year"},
                    ]

    $scope.getLatest=function(){
        ProfileService.getAllLatest("datapod").then(function(response){onSuccessGetAllDatapod(response.data)});
        var onSuccessGetAllDatapod=function(response){
            $scope.allDatapod=response   
        }
    }

    $scope.getLatest();
    
    $scope.resultSearchCriteriaRefresh=function(){
        $scope.searchForm={};
        $scope.allDatapod=[];
        $scope.getLatest();
        $scope.resultCompareResultRefresh(false);
    }
    $scope.resultCompareResultRefresh=function(defaultValue){
        $scope.sourceProfileResult=[];
        $scope.sourceOrignalData=[]
        $scope.allSourceProfile=[];
        $scopeisSPPProgress=false;
        $scope.isSPPError=false;
        $scope.targetProfileResult=[]
        $scope.allTargetProfile=[];
        $scope.targetOrignalData=[]
        $scopeisTPPProgress=false;
        $scope.isTPPError=false;
        if(defaultValue)
            $scope.getProfileExecByDatapod();
    }

    $scope.onChangeDatapod=function(){
        $scope.isDatapodChange=false;
        // $scope.isSTShow=false
        // $scope.sourceColumns=[];
        // $scope.sourceProfileResult=[];
        // $scopeisSPPProgress=false;
        // $scope.isSPPError=false;
        // $scope.targetColumns=[]
        // $scope.targetProfileResult=[]
        // $scope.allTargetProfile=[];
        // $scopeisTPPProgress=false;
        // $scope.isTPPError=false;
        if($scope.searchForm.datapod !=null){
           // $scope.getProfileExecByDatapod();
            ProfileService.getAttributeByDatapod($scope.searchForm.datapod.uuid,"datapod").then(function(response){onSuccessGetAttributeByDatapod(response.data)});
            var onSuccessGetAttributeByDatapod=function(response){
                $scope.allAttribute=response 
                $scope.searchForm.attribute=response[0];
                $scope.isSTShow=true;
                $scope.resultCompareResultRefresh(false);
            }
        }
        else{
            $scope.allAttribute=[];
            $scope.isSTShow=false;
        }

    }
    
    $scope.onChangeDate=function(){
        if($scope.searchForm.datapod !=null){
            $scope.isDatapodChange=false;
        }else{
            $scope.isDatapodChange=true;
        }
        
    }

    $scope.onChangeAttribute=function(){
        $scope.sourceProfileResult=[];
        $scopeisSPPProgress=false;
        $scope.isSPPError=false;
        $scope.targetProfileResult=[]
       // $scope.allTargetProfile=[];
        $scopeisTPPProgress=false;
        $scope.isTPPError=false;
        $scope.sourceProfileResult=$scope.getdata($scope.sourceOrignalData,$scope.sourceColumns);
        $scope.targetProfileResult=$scope.getdata($scope.targetOrignalData,$scope.targetColumns);
        
    }
    
    $scope.getProfileExecByDatapod=function(){
        $scope.isRSCProgress=true;
        var startdate="";
        if($scope.searchForm.startdate != null) {
            startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
            enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            enddate = enddate + " UTC";
        }
        console.log(startdate);
        console.log(enddate);
        ProfileService.getProfileExecByDatapod($scope.searchForm.datapod.uuid,startdate,enddate,"profileexec").then(function(response){onSuccessGetProfileExecByDatapod(response.data)});
        var onSuccessGetProfileExecByDatapod=function(response){
            $scope.allSourceProfile=response;
            $scope.isRSCProgress=false; 
        }
    }
    
    $scope.onChangeSourceProfile=function(){
      
        if($scope.selectSoureProfile){
            $scope.allTargetProfile = $scope.allSourceProfile.filter(function(el) {
                return el.uuid !== $scope.selectSoureProfile.uuid;
            });
            $scope.isSPPProgress=false;
            $scope.isSPPError=false;
            $scope.isTPPProgress=true;
            $scope.targetProfileResult=[];
            $scope.getProfileResult($scope.selectSoureProfile.uuid,$scope.selectSoureProfile.version,"soucre");
        }
    }

    $scope.onChangeTargetProfile=function(){
        $scope.isTPPProgress=false;
        $scope.isTPPError=false;
        $scope.isSPPProgress=false;
        if($scope.selectTargetProfile)
        $scope.getProfileResult($scope.selectTargetProfile.uuid,$scope.selectTargetProfile.version,"target");
    }

    $scope.getProfileResult=function(uuid,version,type){
    
        if(type=="soucre"){
            $scope.isSPPProgress=true;
            $scope.isTPPProgress=false;
            
        }
        if(type=="target"){
            $scope.isSPPProgress=false;
            $scope.isTPPProgress=true; 
        }
        ProfileService.getNumRowsbyExec(uuid,version,"profileexec").then(function(response){onSuccessGetNumRowsbyExec(response.data)});
        var onSuccessGetNumRowsbyExec=function(response){
            ProfileService.getResults(uuid,version,response.runMode).then(function(response){onSuccessGetResults(response.data)},function(response){onErrorGetProfileResults(response.data)});
            var onSuccessGetResults=function(response){
                console.log(response[0])
                
                if(type=="soucre"){
                    $scope.isSPPProgress=false;
                    $scope.sourceOrignalData=response;
                    $scope.sourceColumns=$scope.getColumns(response);
                    $scope.sourceProfileResult=$scope.getdata(response,$scope.sourceColumns);
                }
                if(type=="target"){
                    $scope.isTPPProgress=false;
                    $scope.targetColumns=$scope.getColumns(response);
                    $scope.targetOrignalData=response;
                    $scope.targetProfileResult=$scope.getdata(response,$scope.targetColumns);
                }
            }
            var onErrorGetProfileResults=function(response){
                if(type=="soucre"){
                    $scope.isSPPProgress=false;
                    $scope.isSPPError=true;
                    $scope.sourceColumns=[]
                    $scope.sourceProfileResult=[]
                }
                if(type=="target"){
                    $scope.isTPPProgress=false;
                    $scope.isTPPError=true;
                    $scope.targetColumns=[]
                    $scope.targetProfileResult=[]
                }
    
            }   
        }
       
    }

    $scope.getColumns=function(response){
        var hideKey=["rownum","datapod_uuid","datapodVersion","attribute_id","datapod_name","attribute_name"];
        var columns = [];
        angular.forEach(response[0],function (val,key) {
            if(hideKey.indexOf(key) ==-1)
            columns.push(key);
        });
        return columns;
    };

    $scope.getdata=function(response,columns){
        var profileresult=[]
        if(response){
            for(var i=0;i<response.length;i++){
                if($scope.searchForm.attribute.attributeId == response[i]["attribute_id"]){
                    var profileresultdata={}
                    for(var j=0;j<columns.length;j++){
                        profileresultdata[columns[j]]=response[i][columns[j]];
                        profileresult[j]=profileresultdata;
                    }
                    break;
                }    
            }
        }
        return profileresult;
    }
    
    $scope.onChangeProfileAttributes=function(){
       $scope.getChartColumn();
    }
    $scope.getTrenddingData=function(){
        $scope.isChartShow=false;
        $scope.isChartProgrss=true;
       // $scope.chartdata.datacolumns=[{"id":$scope.selectProfileAttr,"type":"bar","name":$scope.selectProfileAttr,"color":$scope.chartcolor[randomno]}];
        $scope.chartdata.datax={"id":"createdOn"};
        ProfileService.getProfileResults($scope.searchForm.datapod.uuid,$scope.searchForm.datapod.version,$scope.searchForm.attribute.attributeId,$scope.selectProfilePriode,$scope.selectProfileAttr)
        .then(function(response){onSuccessGetProfileResults(response.data)});
        var onSuccessGetProfileResults=function(response){
            console.log(response);
            $scope.chartdata.datapoints=response;
            $scope.isChartShow=true;
            $scope.isChartProgrss=false;
            $scope.getChartColumn();
        }      
    }
    $scope.getChartColumn=function(){
        if($scope.chartdata.datapoints  && $scope.chartdata.datapoints.length >0){
            var randomno = Math.floor((Math.random() *15) + 0);
            $scope.isChartShow=false;
            $scope.isChartProgrss=true;
            if($scope.selectProfileAttr !="All"){
            $scope.chartdata.datacolumns=[{"id":$scope.selectProfileAttr,"type":"bar","name":$scope.selectProfileAttr,"color":$scope.chartcolor[randomno]}];}
            else{
                $scope.chartdata.datacolumns=[];
                for(var i=0;i<$scope.profileAttributes.length;i++){
                    if($scope.profileAttributes[i].name !="All"){
                        var columnsjosn={};
                        var randomno = Math.floor((Math.random() *15) + 0);
                        columnsjosn.id=$scope.profileAttributes[i].name;
                        columnsjosn.name=$scope.profileAttributes[i].name;
                        columnsjosn.type="bar";
                        columnsjosn.color=$scope.chartcolor[i];
                        $scope.chartdata.datacolumns[i]=columnsjosn 
                    } 
                }
            }
            setTimeout(function(){ 
                $scope.isChartShow=true;
                $scope.isChartProgrss=false; 
            }, 3000);
        } 
    }

    $scope.resultSearchCriteria=function(){
        $scope.allSourceProfile=[];
        $scope.allTargetProfile=[];
        $scope.sourceColumns=[];
        $scope.sourceProfileResult=[];
        $scopeisSPPProgress=false;
        $scope.isSPPError=false;
        $scope.targetColumns=[]
        $scope.targetProfileResult=[]
        $scope.allTargetProfile=[];
        $scopeisTPPProgress=false;
        $scope.isTPPError=false;
        $scope.isDatapodChange=true;
        $scope.getProfileExecByDatapod();
    }
});


ProfileModule.controller('ProfileComapreAttributeController',function($state,$stateParams,$rootScope,$scope,ProfileService) {
    $scope.attrSourceSearchForm={}
    $scope.attrTargetSearchForm={}
    $scope.attrSourceSearchForm.sourceDatapod={};
    $scope.attrTargetSearchForm.targetDatapod={};
    $scope.chartdata={};
    $scope.chartdata.datapoints=[];
    
    $scope.tz=localStorage.serverTz;
    var matches = $scope.tz.match(/\b(\w)/g);
    $scope.timezone=matches.join('')
    $scope.chartcolor=["#f1948a","#c39bd3","#73c6b6","#d98880","#82e0aa","#f7dc6f","#bb8fce","#f8c471","#7fb3d5","#76d7c4","#7dcea0","#f0b27a","#e59866",,"#85c1e9"];
    $scope.profileAttrAttributes=[
                        //{ name:"All",caption:"All"},
                        { name:"min_val",caption:"Min Value"},
                        { name:"max_val",caption:"Max Value"},
                        { name:"avg_val",caption:"Avg Val" },
                        { name:"median_val",caption:"Median Val"},
                        { name:"std_dev",caption:"Std Dev"},
                        { name:"num_distinct",caption:"Num Distinct" },
                        { name:"perc_distinct",caption:"Perc Distinct" },
                        { name:"num_null",caption:"Num Null" },
                        { name:"perc_null",caption:"Perc Null" },
                        { name:"min_length",caption:"Min Length" },
                        { name:"max_length",caption:"Max Length" },
                        { name:"avg_length",caption:"Avg Length" },
                        { name:"num_duplicates",caption:"Num Duplicates" },
                        ]
    $scope.attrSourceSearchForm.selectSourceProfileAttr=$scope.profileAttrAttributes[0].name
    $scope.attrTargetSearchForm.selectTargetProfileAttr=$scope.profileAttrAttributes[0].name
    $scope.profileAttrPeriods=[
                        { name:"7",caption:" 1 Week" },
                        { name:"30",caption:"1 Month"},
                        { name:"90",caption:"3 Month"},
                        { name:"365",caption:"1 Year"},
                        
                    ]
    $scope.attrTargetSearchForm.selectTargetProfilePriode=$scope.profileAttrPeriods[0].name
    $scope.attrSourceSearchForm.selectSourceProfilePriode=$scope.profileAttrPeriods[0].name
    ProfileService.getAllLatest("datapod").then(function(response){onSuccessGetAllDatapod(response.data)});
    var onSuccessGetAllDatapod=function(response){
        $scope.allAttrSourceDatapod=response;
        $scope.allAttrTargetDatapod=response;   
    };
     
    $scope.onChangeAttrSourceDatapod=function(){
        console.log($scope.searchForm);
        if($scope.attrSourceSearchForm.sourceDatapod !=null){
            ProfileService.getAttributeByDatapod($scope.attrSourceSearchForm.sourceDatapod.uuid,"datapod").then(function(response){onSuccessGetAttributeByDatapod(response.data)});
            var onSuccessGetAttributeByDatapod=function(response){
                $scope.allAttrSourceAttribute=response 
                $scope.attrSourceSearchForm.sourceAttribute=response[0];
            }
        }
        else{
            $scope.allAttrSourceAttribute=[];
        }

    }


    $scope.onChangeAttrTargetDatapod=function(){
        if($scope.attrTargetSearchForm.targetDatapod !=null){
            ProfileService.getAttributeByDatapod($scope.attrTargetSearchForm.targetDatapod.uuid,"datapod").then(function(response){onSuccessGetAttributeByDatapod(response.data)});
            var onSuccessGetAttributeByDatapod=function(response){
                $scope.allAttrTargetAttribute=response 
                $scope.attrTargetSearchForm.targetAttribute=response[0]
            }
        }
        else{
            $scope.allAttrTargetAttribute=[];
        }
    }

    /*$scope.onChangeSourceProfileAttributes=function(index){
      
        $scope.getChartColumn($scope.chartdata[index].datapoints,index,$scope.attrSourceSearchForm.selectSourceProfileAttr)
    }
    $scope.onChangeTargetProfileAttributes=function(index){
      
        $scope.getChartColumn($scope.chartdata[index].datapoints,index,$scope.attrTargetSearchForm.selectTargetProfileAttr)
    }*/

    $scope.getChartColumn=function(datapoint,index,columns){
    
        if(datapoint  && datapoint.length >0){
            var randomno = Math.floor((Math.random() *15) + 0);
            $scope.chartdata[index].isProgress=true;
            if($scope.selectProfileAttr !="All"){
            $scope.chartdata[index].datacolumns=[{"id":columns,"type":"bar","name":columns,"color":$scope.chartcolor[randomno]}];}
            else{
                $scope.chartdata[index].datacolumns=[];
                for(var i=0;i<$scope.profileAttributes.length;i++){
                    if($scope.profileAttributes[i].name !="All"){
                        var columnsjosn={};
                        var randomno = Math.floor((Math.random() *15) + 0);
                        columnsjosn.id=$scope.profileAttributes[i].name;
                        columnsjosn.name=$scope.profileAttributes[i].name;
                        columnsjosn.type="bsar";
                        columnsjosn.color=$scope.chartcolor[i];
                        $scope.chartdata[index].datacolumns[i]=columnsjosn 
                    } 
                }
            }
            setTimeout(function(){ 
                $scope.chartdata[index].isProgress=false; 
            }, 3000);
        } 
    }

    $scope.attrSearchCriteria=function(){
        $scope.chartdata=[];
        var chartdata={};
        chartdata.isProgress=true;
        chartdata.title="Source";
        $scope.chartdata[0]=chartdata;
        var chartdata1={};
        chartdata1.isProgress=true;
        chartdata1.title="Target";
        $scope.chartdata[1]=chartdata1;
        
        ProfileService.getAttrProfileResults($scope.attrSourceSearchForm,$scope.attrTargetSearchForm).then(function(response){onSuccessGetAttributeByDatapod(response.data)});
        var onSuccessGetAttributeByDatapod=function(response){
            console.log(response)
            for(var i=0;i<response.length;i++){
                var randomno = Math.floor((Math.random() *15) + 0);
                var chartdata={}
                chartdata.id="chart"+i
                if(i==0){
                chartdata.title="Source";
                //chartdata.datacolumns=[{"id":$scope.attrSourceSearchForm.selectSourceProfileAttr,"type":"bar","name":$scope.attrSourceSearchForm.selectSourceProfileAttr,"color":$scope.chartcolor[randomno]}];
                chartdata.datacolumns=[{"id":"profile_attribute","type":"bar","name":"profile_attribute","color":$scope.chartcolor[randomno]}];

                }else{
                    chartdata.title="Target";
                    debugger
                    //chartdata.datacolumns=[{"id":$scope.attrSourceSearchForm.selectSourceProfileAttr,"type":"bar","name":$scope.attrSourceSearchForm.selectSourceProfileAttr,"color":$scope.chartcolor[randomno]}];
                    chartdata.datacolumns=[{"id":"profile_attribute","type":"bar","name":"profile_attribute","color":$scope.chartcolor[randomno]}];

                }
                chartdata.isProgress=false;
                chartdata.datapoints=response[i].data;
                //chartdata.datax={"id":"createdOn"};
                chartdata.datax={"id":"profile_exec_time"};

                //chartdata.datacolumns=[];
                //$scope.getChartColumn(response[i].data,i,$scope.attrSourceSearchForm.selectSourceProfileAttr)
               // chartdata.datacolumns=[{"id":$scope.attrSourceSearchForm.selectSourceProfileAttr,"type":"bar","name":$scope.attrSourceSearchForm.selectSourceProfileAttr,"color":$scope.chartcolor[randomno]}];
                $scope.chartdata[i]=chartdata;
            }
            
        }
    }
});