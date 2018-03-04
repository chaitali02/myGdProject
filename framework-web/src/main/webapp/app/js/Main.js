
/* inferyx App*/

var InferyxApp = angular.module("InferyxApp", [

	"CommonModule",
	"MetadataModule",
	"DashboardModule",
	"DatadiscoveryModule",
	"MetadataNavigatorModule",
	"DatavisualizationModule",
	"DatapodModule",
	"RuleModule",
	"DataQualityModule",
	"ProfileModule",
	"AdminModule",
	"JobMonitoringModule",
	"SystemMonitoringModule",
	"DataPipelineModule",
	"DatascienceModule",
	"VizpodModule",
	'dataGrid',
	'pagination',
	"ngCookies",
	"ui.router",
  "ui.bootstrap",
  "oc.lazyLoad",
  "ngSanitize",
  'ui.select',
  "toggle-switch",
  "ngTagsInput",
  "ngStorage",
  "ngIdle",
  "ngLoadingSpinner",
  "ngResource",
  "ngFileSaver",
  "ngDragDrop",
  "angularUUID2",
  "ngTable",
  "gridshore.c3js.chart",
	"rzModule",
	"ngRightClick",
  'ngConfirm',
	'ui.grid',
	'ui.grid.resizeColumns',
	'ui.grid.pagination',
	'ui.grid.pinning',
	'ui.grid.autoResize',
	'ui.grid.exporter',
	'ui.grid.edit',
	'ui.grid.selection',
	'angularNotify',
	'dndLists',
	'jcs-autoValidate',
	'angular-screenshot',
	'ui.bootstrap.datetimepicker'
]);


 /*Configure ocLazyLoader(refer: https://github.com/ocombe/ocLazyLoad) */
  InferyxApp.config(['$httpProvider','$ocLazyLoadProvider','KeepaliveProvider', 'IdleProvider',function($httpProvider,$ocLazyLoadProvider,KeepaliveProvider,IdleProvider){
	/* $ocLazyLoadProvider.config({
	        // global configs go here
	    });*/
  //console.log("SessionId:"+$.cookie("sessionId"));
	if(typeof localStorage.userdetail !="undefined"){
	console.log(JSON.parse(localStorage.userdetail).sessionId)
	$httpProvider.defaults.headers.common['sessionId'] =JSON.parse(localStorage.userdetail).sessionId//$.cookie("sessionId");
  }
	//$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	//$httpProvider.defaults.headers.common['Accept'] ='application/json, text/javascript, */*; q=0.01';
	//$httpProvider.defaults.withCredentials = true;
	var notify = {
      type: 'success',
      title: 'Success',
      timeout: 3000 //time in ms
  };

	$httpProvider.interceptors.push(function($rootScope,$q){
  return {
		'responseError': function(rejection) {
			console.log(JSON.stringify(rejection))
			if(rejection.status == 500){
				notify.type='error',
				notify.title= 'Some Error Occured',
				notify.content="Please try to reload or contact administrator"//"Dashboard Deleted Successfully"
				$rootScope.$emit('notify', notify);
			}
			else if(rejection.status !=200 && rejection.status !=500 && rejection.status!=419){
					notify.type='info';
					notify.title= 'Info';
					try {
						notify.content=rejection.data.message;
					} catch (e) {
						notify.content= "Error Code" + rejection.status
					} finally {
					}

					$rootScope.$emit('notify', notify);

			}
			else if(rejection.status==419){
				notify.type='info';
				notify.title= 'Info';
				try {
					notify.content=rejection.data.message;
				} catch (e) {
					notify.content= "Error Code" + rejection.status
				} finally {
				}

				$rootScope.$emit('notify', notify);
            $rootScope.$emit('CallFromAppRoleControllerLogout', {});
			}
      return $q.reject(rejection);
    }
  };
});

	IdleProvider.idle(600);
	/*IdleProvider.timeout(5);
	 KeepaliveProvider.interval(10);*/
    $ocLazyLoadProvider.config({
        // global configs go here
    });

    /*var $cookies;
    angular.injector(['ngCookies']).invoke(['$cookieStore', function(_$cookies_) {
      $cookies = _$cookies_;
    }]);*/
}]);


InferyxApp.run(['Idle','$sessionStorage','$rootScope','$http','$cookieStore','validator','$timeout','$filter', function(Idle,$sessionStorage,$rootScope,$http,$cookieStore,validator,$timeout,$filter) {
	  Idle.watch();
		validator.setValidElementStyling(false);
	  validator.setInvalidElementStyling(true);
	  //if($cookieStore.get('userdetail')){
		  if(localStorage.userdetail){
			//$rootScope.baseUrl=$cookieStore.get('userdetail').baseUrl
			$rootScope.role=localStorage.role;
			$rootScope.baseUrl=JSON.parse(localStorage.userdetail).baseUrl
		}
		$rootScope.time= new Date();
		$rootScope.tzName = $rootScope.time.toLocaleString('en', {
			timeZoneName: 'short'
		}).split(' ').pop();
		var update = function() {

    //$rootScope.time = new Date();
		$rootScope.time=$filter('date')(new Date(),"MMM dd yyyy - HH:mm:ss");
    $timeout(update, 1000);
  }
  $timeout(update, 1000);

	}]);




/* Setup global settings */
InferyxApp.factory('settings', ['$rootScope', function($rootScope) {
    // supported languages
    var settings = {
        layout: {
            pageSidebarClosed: false, // sidebar menu state
            pageContentWhite: true, // set page content layout
            pageBodySolid: false, // solid body color state
            pageAutoScrollOnLoad: 1000 // auto scroll to top on page load
        },
        assetsPath: 'assets',
        globalPath: 'assets/global',
        layoutPath: 'assets/layouts/layout',
    };
    $rootScope.settings = settings;
    return settings;
}]);

-




InferyxApp.factory('ajaxCallFactory',function($http,$location){
	var ajaxCallServiceFactory={};
    ajaxCallServiceFactory.getCall=function(url,sessionId){
		 var base_url=$location.absUrl().split("app")[0];
		 url = base_url + url;
    	 var xhttp = new XMLHttpRequest();
    	  xhttp.onreadystatechange = function() {
    	    if (this.readyState == 4 && this.status == 200) {
    	       ajaxCallServiceFactory.data = this.responseText;
    	    }
    	  };
    	  xhttp.open("GET", url, false);
    	  xhttp.setRequestHeader("sessionId",sessionId);
    	  xhttp.send();
    	  return ajaxCallServiceFactory.data;
    }
   return ajaxCallServiceFactory;
});

InferyxApp.controller('TabController',function($timeout,$state,$scope,$rootScope,$stateParams){
    $scope.activeTabIndex=1;
    $rootScope.rootTabs = $scope.tabs =[];
    $scope.showTabs = true;

    $scope.go=function(tab){
        if(typeof tab != "undefined"){
            $rootScope.rootActiveTab = 'Tab-'+$scope.tabs.indexOf(tab);
            $rootScope.activeState = tab.route.substring(4);
            $state.go(tab.route,tab.param);
        }
    };

  $scope.active = function(route, params){
      return $state.is(route,params);
  };
    function isTabAvailable(tab) {
        var available = -1;
        angular.forEach($scope.tabs,function (val,key) {
            if(val.route == tab.route && angular.equals(val.param,tab.param))
                available = key;
        });

        return available;
    }
  $scope.$on('onAddTab',function (e,tab) {
    $scope.addTab(tab)
        $scope.activeTabIndex=null;
  });
    $rootScope.$on('onDeInitTabs',function () {
   $scope.tabs=[];
    })
  $scope.addTab = function(tab) {
        var indexOfTab = isTabAvailable(tab);
        if(indexOfTab > -1){
            $scope.go($scope.tabs[indexOfTab]);
            return
        }
        tab.index = $scope.tabs.length;
        tab.heading = tab.param.type+"-"+tab.param.name || "Tab"+(tab.index+1);
        $scope.tabs.push(tab);
    }
    $scope.$watch('activeTabIndex',function (oldv,newv) {
        $scope.showTabs = false;
        $timeout(function () {
            $scope.showTabs = true;
        },1)
 });

  $scope.removeTab=function(index) {
        if(index==0){
            $scope.tabs.splice(index,1);
            $scope.showTabs = false;
            angular.forEach($scope.tabs,function (val,key) {
                // val.heading = val.param.name || "Tab"+(key+1);
                val.index = key;
                val.active = false;
            });
            $scope.showTabs = true;
            $scope.activeTabIndex=0;
        }
        else if(!$scope.tabs[index].active){
            $scope.tabs.splice(index,1);
        }
        else{
            $scope.tabs.splice(index,1);
            $scope.showTabs = false;
            angular.forEach($scope.tabs,function (val,key) {
                // val.heading = val.param.name || "Tab"+(key+1);
                val.index = key;
                val.active = false;
            });
            $scope.showTabs = true;
            $scope.go($scope.tabs[index-1]);
        }
    };

  $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
        if($scope.tabs.length ==0){
            var state={};
            state.route=toState.name;
            state.index=$scope.tabs.length+1;
            state.param=toParams;
            // state.param.name = "Main Tab";
            // state.heading=state.param.name;
            state.heading="Main Tab";
            state.active=false;
            $scope.tabs.splice($scope.tabs.length,0,state);
        }
        else{
            if($scope.tabs.length == 1){
                $scope.tabs[0].route=toState.name;
                $scope.tabs[0].param=toParams;
                // $scope.tabs[0].param.name= "Main Tab";
          }
      }
  });

    $rootScope.$on("$stateChangeSuccess", function() {
        if($scope.tabs.length >0){
            $scope.showTabs = false;
            angular.forEach($scope.tabs,function (val,key) {

                val.active = $scope.active(val.route, val.param);
                if(val.active)
                    $scope.activeTabIndex = key;
                    $rootScope.rootActiveTab = "Tab-"+key;
                    $rootScope.activeState = val.route.substring(4);
                });
            $scope.showTabs = true;
        }
    });
});



 InferyxApp.controller('lhscontroller',function($scope,$rootScope,SharedProperties,$state,$window,$cookieStore,LhsService){
	  $rootScope.metaStats = {};
		$scope.deInitTabs=function () {
			var param={};
			param.index=null;
		 $rootScope.$broadcast('onDeInitTabs',param);
		}
    if(typeof localStorage.userdetail =="undefined"){
		  $window.location.href = 'login.html';
		  return false;
	  }
    $rootScope.$on("CallFromAppRoleController", function() {
	    $scope.updateMetaData()
	  });

    $scope.metadatasubmenu;
    $scope.Metadata= {
		                "caption":"Data Preparation",
                    "name":"metadata",
                    "class":"fa fa-link",
                    "submenu":[]
                    };
    $scope.Datadiscovery= {
    				        "caption":"Data Discovery",
    				        "name":"datadiscovery",
    				        "class":"fa fa-binoculars",
    				        "submenu":[]
    				       };
    $scope.MetaDatanavigator= {
			              "caption":"Metadata Navigator",
			              "name":"metadatanavigator",
			              "class":"fa fa-paper-plane",
										"submenu":[]
			            };
    $scope.Ruledata={
	              "caption":"Business Rules",
	              "name":"rules",
	              "class":"fa fa-cogs",
	              "submenu":[
	            	  {"name":"viewrule","type":"rule","uuid":"null","caption":"Rule"},
	    	          {"name":"rulesgroup","type":"rulegroup","uuid":"null","caption":"Rule Group"},
	                {"name":"rulerestult","type":"ruleexec","uuid":"null","caption":"Rule Results"}
	    	        ]
							}
    $scope.Dataqualitydata={
                "caption":"Data Quality",
                "name":"data quality",
                "class":"fa fa-rss",
                "submenu":[
         	        {"name":"viewdataquality","type":"dq","uuid":"null","caption":"Rule"},
         	        {"name":"viewdataqualitygroup","type":"dqgroup","uuid":"null","caption":"Rule Group"},
         	        {"name":"viewdqresults","type":"dqexec","uuid":"null","caption":"Rule Results"}
								]
							}
    $scope.Profiledata={
                "caption":"Data Profiling",
                "name":"profile",
                "class":"fa fa-users",
                "submenu":[
         	        {"name":"viewprofile","type":"profile","uuid":"null","caption":"Rule"},
         	        {"name":"viewprofilegroup","type":"profilegroup","uuid":"null","caption":"Rule Group"},
         	        {"name":"viewprofileresults","type":"profileexec","uuid":"null","caption":"Rule Results"}
								]
							}
    $scope.Dagworkflow={
              "caption":"Data Pipeline",
              "name":"workflow",
              "class":"fa fa-random",
              "submenu":[
                {"name":"createwf","type":"createwf","uuid":"null","caption":"Create New"},
                {"name":"listwf","type":"dag","uuid":"null","caption":"List"},
                {"name":"resultwf","type":"dagexec","uuid":"null","caption":"Results"}
         	      ]
							}
  $scope.Datascience={
		        "caption":"Data Science",
           "name":"datascience",
           "class":"fa fa-flask",
            "submenu":[
              {"name":"algorithm","type":"algorithm","uuid":"null","caption":"Algorithm"},
              {"name":"model","type":"model","uuid":"null","caption":"Model"},
              {"name":"paramlist","type":"paramlist","uuid":"null","caption":"Parameter List"},
              {"name":"paramset","type":"paramset","uuid":"null","caption":"Parameter Set"},
              {"name":"resultmodelmodel","type":"modelexec","uuid":"null","caption":"Results"}
        	    ]
            }
  $scope.Datavisualization={
		        "caption":"Data Visualization",
           "name":"datavisualization",
           "class":"fa fa-desktop",
            "submenu":[
              {"name":"dashboard","type":"dashboard","uuid":"null","caption":"Dashboard"},
              // {"name":"dashboard2","type":"dashboard","uuid":"null","caption":"Dashboard 2"},
              {"name":"vizpodlist","type":"vizpod","uuid":"null","caption":"Vizpod"},
        	    ]
            }
  $scope.Admindata={
		        "caption":"Admin",
  	        "name":"admin",
  	        "class":"fa fa-wrench",
  	        "submenu":[]
            }

  $scope.Datapoddata={
	          "caption":"Data Pod",
	          "name":"datapod",
	          "class":"fa fa-list",
	          "submenu":[]
	          },
  $scope.Vizpoddata= {
		        "caption":"Viz Pod",
		        "name":"vizpod",
		        "class":"fa fa-bar-chart",
		        "submenu":[]
		      }
  $scope.JobExecutor= {
		        "caption":"Job Monitoring",
            "name":"jobexecutor",
            "class":"fa fa-tasks",
            "submenu":[]
          };
					$scope.SystemMonitering= {
										"caption":"System Monitoring",
										"name":"systemmonitering",
										"class":"fa fa-tasks",
										"submenu":[]
									};

  $scope.convertUppdercase=function(value){
    if(!value)
		  return value;
	  var resultvalue =value.split("_");
    var resultjoint = [];
    for (j = 0; j < resultvalue.length; j++) {
      resultjoint[j]=resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
    }
    return  resultjoint.toString().replace(/,/g, " ");
  }

 	$scope.updateMetaData=function(){
		if(!localStorage.isAppRoleExists){//!$cookieStore.get('isAppRoleSubmit')){
	    return false
		}
  	else{
   		LhsService.getMetadata().then(function(response){onMetadataSuccess(response.data)});
    	var onMetadataSuccess=function(metadata){
   			var countMeta=0;
   			var countAdmin=0;
	 			for(var i=0;i<metadata.length;i++){
					var meta={};
					if(metadata[i].menu == 'DataPreparation' && metadata[i].active == 'Y'){
						meta.name=metadata[i].name;
			      meta.uuid=metadata[i].uuid
			      meta.caption=$scope.convertUppdercase(metadata[i].name)
			      $scope.Metadata.submenu[countMeta]=meta
			      countMeta=countMeta+1;
		      }
	        else if(metadata[i].menu == 'admin' && metadata[i].active == 'Y'){
		     	  meta.name=metadata[i].name;
		        meta.uuid=metadata[i].uuid
		 	      meta.caption=$scope.convertUppdercase(metadata[i].name)
			      $scope.Admindata.submenu[countAdmin]=meta
			      countAdmin=countAdmin+1;
		      }
	      }
      LhsService.getMetaStats().then(function(response){onSuccessGetMetaStats(response.data)});
		}
      var onSuccessGetMetaStats=function(response){
	      angular.forEach(response,function (val,key) {
		      $rootScope.metaStats[val.type] = val;
	      });
	    }
  // var onDatapoddataSuccess=function(datapoddata){
	//   for(var j=0;j<datapoddata.length;j++){
	//
	// 		 var datapodsubmenu={};
	// 		 datapodsubmenu.name=datapoddata[j].name;
	// 		 datapodsubmenu.uuid=datapoddata[j].uuid
	// 		 datapodsubmenu.caption=$scope.convertUppdercase(datapoddata[j].name)
	// 		 $scope.Datapoddata.submenu[j]=datapodsubmenu
	// 	 }
	//
  //  }
    }//End ELSE
  }//End updateMetaData Method
});

InferyxApp.controller('AppRoleController',function($scope,$rootScope,$cookieStore,AppRoleService,$cookieStore,$window,$state,privilegeSvc){
 if(localStorage.isAppRoleExists){
	 $rootScope.setUserName=JSON.parse(localStorage.userdetail).name//$cookieStore.get('name')
   $rootScope.setUseruuid=JSON.parse(localStorage.userdetail).userUUID//$cookieStore.get('userdetail').userUUID
   $scope.username=JSON.parse(localStorage.userdetail).userName
	return false;
  }
	// if(!$cookieStore.get('sessionId')){
	// 		//$window.location.href = 'login.html';
	// 		 return false;
  //   }
	//localStorage.roleappdetail={};
	$scope.selectedApp;
	$scope.selectedRole;
	$scope.selectAppStatus=false;
	$scope.selectRoleStatus=false;
  $rootScope.setUserName=JSON.parse(localStorage.userdetail).name//$cookieStore.get('name')
  $rootScope.setUseruuid=JSON.parse(localStorage.userdetail).userUUID//$cookieStore.get('userdetail').userUUID
  $scope.username=JSON.parse(localStorage.userdetail).userName
  $rootScope.isSubmit=false;
  //AppRoleService.getLatestByUuid($cookieStore.get('userdetail').userUUID,"user").then(function(response){onSuccessGetLatestByUuid(response.data)});
	AppRoleService.getLatestByUuid(JSON.parse(localStorage.userdetail).userUUID,"user").then(function(response){onSuccessGetLatestByUuid(response.data)});
  var onSuccessGetLatestByUuid=function(response){
    	$scope.username=response.firstName
  }
	AppRoleService.getAppRole($rootScope.setUserName).then(function(response){onAppSuccess(response.data)})
	var onAppSuccess = function (response) {
		  $scope.AppData=response
		  $scope.RoleData=response[0].roleInfo
		  $scope.selectedRole=response[0].roleInfo[0]
		  $scope.selectedApp=response[0]
		  $rootScope.appUuid=$scope.selectedApp.appId.ref.uuid;
			//$scope.selectedRole=response[0]
			//alert(JSON.stringify($scope.AppData))
		  localStorage.appName=$scope.selectedApp.appId.ref.name;
		  $scope.selectAppStatus=true;
		  $scope.selectRoleStatus=true;
		 localStorage.role=$scope.selectedRole.ref.name;
		 $rootScope.role=localStorage.role;
		 AppRoleService.getTZ().then(function(responseTz){onSuccessgetTZ(responseTz.data)});
		 var onSuccessgetTZ = function(responseTz){
			localStorage.serverTz=responseTz;
			//alert(localStorage.serverTz)
		 }
	};

	// AppRoleService.getUserApp($rootScope.setUserName).then(function(response){onAppSuccess(response.data)})
	// var onAppSuccess = function (response) {
	//   $scope.AppData=response
	//   $scope.selectedApp=$scope.AppData.defaultoption
	// 	localStorage.appName=$scope.AppData.defaultoption.name;
	// 	$scope.selectAppStatus=true;
	// };
	//
	// AppRoleService.getUserRole($rootScope.setUserName).then(function(response){onRoleSuccess(response.data)})
	// var onRoleSuccess = function (response) {
	// 	$scope.RoleData=response
	// 	$scope.selectedRole=  $scope.RoleData.defaultoption
	// 	$scope.selectRoleStatus=true;
	// 	localStorage.role=$scope.selectedRole.name
	// 	$rootScope.role=localStorage.role;
  // };

	$scope.getselectApp=function(){
		if($scope.selectedApp !=null){
      localStorage.appName=$scope.selectedApp.appId.ref.name
      $rootScope.appUuid=$scope.selectedApp.appId.ref.uuid
			$scope.selectAppStatus=true;
			$scope.selectRoleStatus=true;
			 $scope.RoleData=$scope.selectedApp.roleInfo
			 $scope.selectedRole=$scope.selectedApp.roleInfo[0]
			localStorage.role=$scope.selectedRole.ref.name
			$rootScope.role=localStorage.role;
		}
		else{
			$scope.selectAppStatus=false;
			$scope.selectRoleStatus=false;
		}
	}

	 $scope.getselectRole=function(){
	 	if($scope.selectedRole !=null){
	 		$scope.selectRoleStatus=true;
	 		localStorage.role=$scope.selectedRole.ref.name
	 		$rootScope.role=localStorage.role;
	 	}
	 	else {
	 		$scope.selectRoleStatus=false;
	 	}
	 }

	$scope.open = function() {
		if(!localStorage.isAppRoleExists){//!$cookieStore.get('isAppRoleSubmit')){
	 		$('#myModal').modal({
	 		  backdrop: 'static',
	 		  keyboard: false
	 	  });
	 	}
  };

  $scope.ok = function(event) {
    event.preventDefault();
    if($scope.selectedApp !=null){
      AppRoleService.setSecurityAppRole($scope.selectedApp.appId.ref.uuid,$scope.selectedRole.ref.uuid).then(function(response){onSecurityAppRoleSuccess(response)})
	     var onSecurityAppRoleSuccess = function (response) {
    	  //$cookieStore.put('isAppRoleSubmit','true');
				localStorage.isAppRoleExists=true
	      console.log(JSON.stringify(response.data));
		    $rootScope.$emit("CallFromAppRoleController",{});
			  privilegeSvc.getUpdated();
		    $('#myModal').modal('hide');
	     $rootScope.$emit("callsetapp", {});
		  };
    }
    else{
    	$("#hideshow").removeClass('display-hide')
    	$("#hideshow").addClass('display-show')
    }
    $state.go('datadiscovery');
  };

  $scope.cancelLogut=function(){
	  $rootScope.$emit('CallFromAppRoleControllerLogout', {});
  }
});

InferyxApp.controller('UnloadController',function($rootScope,$scope,$sessionStorage,UnlockService){


	$scope.isErrorShow=false;
	$scope.initOpen=function(){
		if($sessionStorage.unlockStatus == true){
			$('#unlockmodel').modal({
			      backdrop: 'static',
			      keyboard: false
		   });
		 }
	}
   $scope.open=function(){
      $sessionStorage.unlockStatus =true;
      $scope.unlockusername="";
      $scope.unlockpassword="";
	   $('#unlockmodel').modal({
		      backdrop: 'static',
		      keyboard: false
		});

	}
   $scope.hideErrorMessage=function(){
	   $scope.isErrorShow=false
   }
   $scope.close=function(){
	   $scope.isErrorShow=false
	   $scope.unlockusername=$rootScope.setUserName
	   UnlockService.getUnlockApp( $scope.unlockusername,$scope.unlockpassword).then(function(response){ onSuccess(response.data)})
	   $scope.unlockusername="";
       $scope.unlockpassword="";
	   var onSuccess=function(response){
		   if(response == true){
			   $sessionStorage.unlockStatus =false;

			   $('#unlockmodel').modal('hide');
		   }
		  else{
			  $scope.isErrorShow=true;
			  $scope.error="Invalid Login"
		  }
	   }
	  }
		$scope.notYpalrecha=function(){
			$rootScope.$emit('CallFromAppRoleControllerLogout', {});
		}
});



InferyxApp.controller('LogoutController',function($scope,$rootScope,$cookieStore,$window,LogoutService){
	 $scope.$on('IdleStart', function() {
		 $scope.logout();

	      });
	   $scope.$on('IdleEnd', function() {

	      });

	 $scope.$on('IdleTimeout', function() {


	      });
	 $rootScope.$on("CallFromAppRoleControllerLogout", function() {

		 $scope.logout();
		});
     $scope.logout = function() {

    	 LogoutService.securitylogoutSession($.cookie("sessionId")).then(function(response){onSecuritySuccess(response)},function(response){onError(response)})
    	 var onSecuritySuccess =function(response){

    		 console.log(JSON.stringify(response.data))
    		 //alert(response.data.userInfo.ref.uuid)
    		 if(response.data.userInfo.ref.uuid == JSON.parse(localStorage.userdetail).userUUID){//$cookieStore.get('userdetail').userUUID){
    			// $cookieStore.remove('sessionId');
					localStorage.removeItem('userdetail');
    			 //$cookieStore.remove('userdetail');
    			// $cookieStore.remove('selectAppStatus')
    			// $cookieStore.remove('selectRoleStatus')
    			 //$cookieStore.remove('setAppName');
    			// $cookieStore.remove('name');
    			 //$cookieStore.remove('isAppRoleSubmit');
					 localStorage.removeItem('isAppRoleExists');
					 localStorage.clear();
    		     if(typeof localStorage.userdetail =="undefined"){
    		 		    $window.location.href = 'login.html';
    		 	    }
           	 }
    	 }
    	 var onError=function(response){
    		 //$cookieStore.remove('sessionId');
    		// $cookieStore.remove('userdetail');
    		// $cookieStore.remove('selectAppStatus')
    		// $cookieStore.remove('selectRoleStatus')
    		 //$cookieStore.remove('setAppName');
    		 //$cookieStore.remove('name');
    		 //$cookieStore.remove('isAppRoleSubmit');
				 localStorage.removeItem('userdetail');
				  localStorage.removeItem('isAppRoleExists');
					localStorage.clear();
    	     if(typeof localStorage.userdetail =="undefined"){
    	 		    $window.location.href = 'login.html';
    	 	    }
    	 }

     };
});




/* Setup App Main Controller */
InferyxApp.controller('AppController', ['$scope', '$rootScope', function($scope, $rootScope) {
    $scope.$on('$viewContentLoaded', function() {
        //App.initComponents(); // init core components
        //Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
    });
}]);



/* Setup Layout Part - Header */
InferyxApp.controller('HeaderController', ['$uibModal','$scope','$rootScope','$cookieStore','$stateParams','$state','dagMetaDataService', function($uibModal,$scope,$rootScope,$cookieStore, $stateParams, $state, dagMetaDataService) {
	$rootScope.dummyArg = 1;
	$rootScope.caseConverter=function(str) {
		var temp= str.charAt(0).toUpperCase() + str.slice(1);
		return temp.replace(/([A-Z][a-z])/g, " $1");
	}
	$rootScope.genericClose = function (e,type) {
		 if($stateParams.returnBack == "true" && $rootScope.previousState){
					//revertback
					e.preventDefault();
					$state.go($rootScope.previousState.name,$rootScope.previousState.params);
					$rootScope.previousState = undefined;
		 }
		 else if(type) {
			 //var sref = $(this).attr('data-ui-sref');
            $scope.statedetail={};
            $scope.statedetail.name=dagMetaDataService.elementDefs[type].listState
            $scope.statedetail.params={type:type}
            $state.go($scope.statedetail.name,$scope.statedetail.params)
		 }
 }
 	$rootScope.broadcastRefreshData = function () {

		$rootScope.$broadcast('refreshData');
 	}
	$scope.setAppStatus=false;
	$rootScope.$on('callsetapp',function() {
	    $scope.getAppName();

    });
   $scope.getAppName=function(){

	        if(localStorage.appName){//$cookieStore.get('setAppName')){
					$scope.selectApp=localStorage.appName//$cookieStore.get('setAppName');
		    	$scope.setAppStatus=true;

	       }
   }

    $scope.$on('$includeContentLoaded', function() {
        Layout.initHeader(); // init header
    });

		$scope.uploadAvatar = function () {
			var modalInstance = $uibModal.open({
	      templateUrl: 'views/upload-profile-image.html',
	      controller: 'ProfileImageCtrl',
	      size: 'md',
				windowTopClass : 'avatar-modal',
				scope: $scope
	    });
		};

}]);

InferyxApp.controller('ProfileImageCtrl',function ($timeout,$rootScope,$scope,$uibModalInstance,$location,$http) {

	window.readURL = function (input) {
      if (input.files && input.files[0]) {
					$scope.$apply();
          var reader = new FileReader();

          reader.onload = function (e) {
              $('#avatar-preview')
                  .attr('src', e.target.result)
									.show();
          };

          reader.readAsDataURL(input.files[0]);
      }
  }
	$scope.upload = function (file) {
		var f = document.getElementById('avatar').files[0];
		console.log(f);
		var type = f.type.split('/')[1];
		console.log(type);
		var fd=new FormData();
        fd.append('file',f);
        fd.append('fileName',$rootScope.setUseruuid);

		var baseUrl = $location.absUrl().split("app")[0];
		var url = baseUrl+"metadata/uploadProfileImage";
		$http.post(url,fd,
    {
	    transformRequest: angular.identity,
	    headers: {'Content-Type': undefined}
    }).success(function(d){
    	console.log(d);
			$scope.$parent.dummyArg++;
			$scope.cancel()
    })


	}
	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	}
})

/* Setup Layout Part - Sidebar */
InferyxApp.controller('SidebarController', ['$state', '$scope', 'cacheService', function($state, $scope, cacheService) {
    $scope.$on('$includeContentLoaded', function() {
        Layout.initSidebar($state); // init sidebar
    });
		$scope.clearCache = function () {
			cacheService.searchCriteria = {};
		}
}]);



/* Setup Layout Part - Quick Sidebar */
InferyxApp.controller('QuickSidebarController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
       setTimeout(function(){
            QuickSidebar.init(); // init quick sidebar
        }, 2000)
    });
}]);


/* Setup Layout Part - Theme Panel */
InferyxApp.controller('ThemePanelController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
        Demo.init(); // init theme panel
    });
}]);


/* Setup Layout Part - Footer */
InferyxApp.controller('FooterController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
        Layout.initFooter(); // init footer
    });
}]);


/* Setup Rounting For All Pages */
InferyxApp.config(['$stateProvider', '$urlRouterProvider',function($stateProvider,$urlRouterProvider){
    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('/', {
       url: '',
    //    // templateUrl: 'index.html',
        //controller: function($scope,$rootScope, $stateParams) {


      //}
     })

     $stateProvider.state('home', {
        url: 'DataDiscovery',
        templateUrl: 'views/data-discovery-card-list.html',
         controller: function($scope,$rootScope, $stateParams,$window) {
        	//$window.location.href = 'index.html';
        },
				data: {pageTitle: 'Data Discovery'},
    })
        // metadata
       .state('metadata', {
            url: "/dataPreparation?type",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Preparation'},
            controller: "",

        })


.state('datadiscovery', {
         url: "/DataDiscovery",
         templateUrl: "views/data-discovery-card-list.html",
          data: {pageTitle: 'Data Discovery'},
          //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [
                             'js/controllers/DataDiscoveryController.js',
                             'js/services/DataDiscoveryService.js'


                       ]
                   });
               }]
           }
       })
       .state('metadatanavigator', {
           url: "/MetadataNavigator",
         templateUrl: "views/metadata-navigator-card-list.html",
           data: {pageTitle: 'Metadata Navigator'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                             'js/controllers/MatadataNavigatorController.js',
                             'js/services/MetadataNavigatorService.js'


                       ]
                   });
               }]
           }
       })

.state('dashboard', {
           url: "/dashboardList",
         templateUrl: "views/dashboard-card-list.html",
           data: {pageTitle: 'Data Visualization'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                             'js/controllers/DashboardGraphcontroller.js',
                             'js/services/DashboardGraphServices.js'


                       ]
                   });
               }]
           }
       })
// .state('dashboard2', {
//            url: "/dashboardList2",
//          templateUrl: "views/dashboard-card-list2.html",
//            data: {pageTitle: 'Data Visualization'},
//            //controller: "BlankController",
//            resolve: {
//                deps: ['$ocLazyLoad', function($ocLazyLoad) {
//                    return $ocLazyLoad.load({
//                        name: 'InferyxApp',
//                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
//                        files: [
//                              'js/controllers/DashboardGraphcontroller2.js',
//                              'js/services/DashboardGraphServices.js'
//
//
//                        ]
//                    });
//                }]
//            }
//        })

       .state('showdashboard', {
           url: "/dashboard?id&returnBack",
           templateUrl: "views/dashboard-graph.html",
           data: {pageTitle: 'Data Visualization'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                             'js/controllers/DashboardGraphcontroller.js',
                             'js/services/DashboardGraphServices.js',
                             'js/directives/DashboardGraphDirective.js',


                       ]
                   });
               }]
           }
       })
      //  .state('showdashboard2', {
      //      url: "/dashboard2?id&returnBack",
      //      templateUrl: "views/dashboard-graph2.html",
      //      data: {pageTitle: 'Data Visualization'},
      //      //controller: "BlankController",
      //      resolve: {
      //          deps: ['$ocLazyLoad', function($ocLazyLoad) {
      //              return $ocLazyLoad.load({
      //                  name: 'InferyxApp',
      //                  insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
      //                  files: [
			 //
      //                        'js/controllers/DashboardGraphcontroller2.js',
      //                        'js/services/DashboardGraphServices.js',
      //                        'js/directives/DashboardGraphDirective.js',
			 //
			 //
      //                  ]
      //              });
      //          }]
      //      }
      //  })

       .state('vizpodlist', {
           url: "/vizpodList",
           templateUrl: "views/common-list.html",
             data: {pageTitle: 'Data Visualization'},
             params:{type:'vizpod'}
         })

       .state('dvvizpod', {
       url: "/vizpod?id&mode&returnBack&version",
       templateUrl: "views/vizpod.html",
       data: {pageTitle: 'Data Visualization'},
       controller: "",
       resolve: {
           deps: ['$ocLazyLoad', function($ocLazyLoad) {
               return $ocLazyLoad.load({
                   name: 'InferyxApp',
                   insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                   files: [
                   	 '   js/controllers/VizpodController.js',
                        'js/services/VizpodService.js'
                   ]
               });
           }]
       }
   })

         // admin
       .state('admin', {
            url: "/Admin?type&returnBack",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Admin'},

        })

         .state('adminListprivilege', {
            url:"/Admin/privilege?id&mode&returnBack&version",
            templateUrl: "views/privilege.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                            'js/controllers/PrivilegeController.js',
                            'js/services/PrivilegeService.js'
                        ]
                    });
                }]
            }
        })

          .state('adminListactivity', {
            url:"/Admin/activity?id&mode&returnBack&version",
            templateUrl: "views/activity.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ActivityController.js',
                             'js/services/ActivityService.js'

                        ]
                    });
                }]
            }
        })



          .state('adminListrole', {
            url:"/Admin/role?id&mode&returnBack&version",
            templateUrl: "views/role.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/RoleController.js',
                            'js/services/RoleService.js'

                        ]
                    });
                }]
            }
        })



        .state('adminListsession', {
            url:"/Admin/session?id&mode&returnBack&version",
            templateUrl: "views/session.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/SessionController.js',
                            'js/services/SessionService.js'
                        ]
                    });
                }]
            }
        })

         .state('adminListgroup', {
            url:"/Admin/group?id&mode&returnBack&version",
            templateUrl: "views/group.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/GroupController.js',
                            'js/services/GroupService.js'


                        ]
                    });
                }]
            }
        })






       .state('metaListdatapod', {
            url: "/dataPreparation/datapod?id&mode&returnBack&version",
            templateUrl: "views/datapod.html",
            data: {pageTitle: 'Data Preparation'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'datapoddetails',
                        files: [
                        	'assets/jquery-filestyle/css/jquery-filestyle.css',
                        	'js/controllers/MetadataController.js',
                          'js/services/DatapodService.js',
                        ]
                    }]);
                }]
            }
        })


        .state('metaListdashboard', {
            url: "/dataVisualization/dashboard?id&mode&returnBack",
            templateUrl: "views/dashboard.html",
            data: {pageTitle: 'Data Visualization'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	 'js/controllers/DashboardController.js',
                            'js/services/DashboardService.js',

                        ]
                    });
                }]
            }
        })
        // .state('metaListdashboard2', {
        //     url: "/dataVisualization/dashboard2?id&mode&returnBack",
        //     templateUrl: "views/dashboard2.html",
        //     data: {pageTitle: 'Data Visualization'},
        //     controller: "",
        //     resolve: {
        //         deps: ['$ocLazyLoad', function($ocLazyLoad) {
        //             return $ocLazyLoad.load({
        //                 name: 'InferyxApp',
        //                 insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
        //                 files: [
        //                 	 'js/controllers/DashboardController2.js',
        //                     'js/services/DashboardService.js',
				//
        //                 ]
        //             });
        //         }]
        //     }
        // })


        //  .state('datapod', {
        //     url: "/datapodDetail?id",
        //     templateUrl: "views/datapod-detaihtml",
        //     data: {pageTitle: 'DatapodDetail'},
        //     controller: "DatapodController",
        //     resolve: {
        //         deps: ['$ocLazyLoad', function($ocLazyLoad) {
        //             return $ocLazyLoad.load([{
        //                 name: 'datapoddetails',
        //                 files: [
        //                 	'assets/jquery-filestyle/css/jquery-filestyle.css',
        //                     'js/controllers/DatapodController.js',
        //                     'js/services/DatapodDetailService.js',
        //                 ]
        //             }]);
        //         }]
        //     }
        // })





         .state('metaListcondition', {
            url: "/dataPreparation/condition?id&mode&returnBack",
            templateUrl: "views/condition.html",
            data: {pageTitle: 'Data Preparation'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	 'js/controllers/ConditionController.js',
                            'js/services/MetadataService.js'
                        ]
                    });
                }]
            }
        })



         .state('adminListdatastore', {
            url: "/Admin/datastore?id&mode&returnBack&version",
            templateUrl: "views/datastore.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	 'js/controllers/DataStoreController.js',
                             'js/services/DataStoreService.js'
                        ]
                    });
                }]
            }
        })



         .state('metaListexpression', {
            url: "/dataPreparation/expression?id&mode&returnBack&version",
            templateUrl: "views/expression.html",
            data: {pageTitle: 'Data Preparation'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                        	 'js/controllers/ExpressionController.js',
                            'js/services/ExpressionService.js'
                        ]
                    });
                }]
            }
        })

         .state('metaListdataset', {
            url: "/dataPreparation/dataset?id&mode&returnBack&version",
            templateUrl: "views/dataset.html",
            data: {pageTitle: 'Data Preparation'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'dataset',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	 'js/controllers/DatasetController.js',
                            'js/services/MetadataService.js',
                            'js/services/DatasetService.js'
                        ]
                    });
                }]
            }
        })



				.state('rulerestult', {
					 url: "/RuleResultList",
					 templateUrl: "views/common-list.html",
					 data: {pageTitle: 'Business Rules'},
					 params: {type:'ruleexec',isExec:true}
			 })
        .state('rulerestultpage', {
            url: "/RuleResults?id&version&type&name",
            templateUrl: "views/rule-result.html",
            data: {pageTitle: 'Business Rules'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'RuleResult',
                        files: [
                        	'js/controllers/RuleController.js',
                          'js/services/RuleResultService.js'
                        ]
                    }]);
                }]
            }
        })

        .state('viewrule', {
            url: "/ListRule",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Business Rules'},
            params: {type:'rule'}
        })

        .state('createrules', {
            url: "/CreateRule?id&mode&returnBack&version",
            templateUrl: "views/rule.html",
            data: {pageTitle: 'Business Rules'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([
                    	{
                            name: 'formwizard',
                            files: [
                            	 "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                                 //"assets/global/plugins/jquery-validation/js/additional-methods.min.js",
                                 "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            	 "assets/pages/scripts/form-wizard.js"
                            ]
                        },{
                        name: 'createRule',
                        files: [
                        	'js/controllers/RuleController.js',
                            'js/services/RuleService.js'
                        ]
                    }]);
                }]
            }
        })

        //viewrulesgroup
        .state('rulesgroup', {
            url: "/ListRuleGroup",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Business Rules'},
            params:{type:'rulegroup'}
        })
				.state('createrulesgroup', {
            url: "/CreateRuleGroup?id&mode&returnBack&version",
            templateUrl: "views/rule-group.html",
            data: {pageTitle: 'Business Rules'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                        	 "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            // "assets/global/plugins/jquery-validation/js/additional-methods.min.js",
                             "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                        	 "assets/pages/scripts/form-wizard.js"
                        ]
                    }, {
                        name: 'createRuleGroup',
                        files: [
                        	'js/controllers/RuleController.js',
                            'js/services/RuleGroupService.js'
                        ]
                    }]);
                }]
            }
        })

         .state('viewdataquality', {
            url: "/ListDataQuality",
						templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Quality'},
            params:{type:'dq'}
        })
        .state('viewdataqualitygroup', {
            url: "/ListDataQualityGroup",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Quality'},
            params:{type:'dqgroup'}
        })
         .state('createdataquality', {
            url: "/CreateDataQuality?id&mode&returnBack&version",
            templateUrl: "views/dataquality.html",
            data: {pageTitle: 'Data Quality'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                       	    "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                       	    "assets/pages/scripts/form-wizard.js"
                       ]
                    }, {
                        name: 'InferyxApp',
                        files: [
                        	'js/controllers/DataQualityController.js',
                        	'js/services/DataQualityService.js'
                        ]
                    }]);
                }]
            }
        })
        .state('createdataqualitygroup', {
            url: "/CreateDataQualityGroup?id&mode&returnBack&version",
            templateUrl: "views/dataquality-group.html",
            data: {pageTitle: 'Data Quality'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'InferyxApp',
                        files: [
                        	'js/controllers/DataQualityController.js',
                        	'js/services/DataQualityService.js'

                        ]
                    }]);
                }]
            }
        })

				.state('viewdqresults', {
					 url: "/DataQualityResultList",
					 templateUrl: "views/common-list.html",
					 data: {pageTitle: 'Data Quality'},
					 params: {type:'dqexec',isExec:true}
			 })
        .state('viewdqresultspage', {
            url: "/DataQualityResults?id&version&type&name",
            templateUrl: "views/dataquality-result.html",
            data: {pageTitle: 'Data Quality'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'InferyxApp',
                        files: [
                        	'js/controllers/DataQualityController.js',
                        	'js/services/DataQualityService.js'

                        ]
                    }]);
                }]
            }
        })

       .state('viewprofile', {
            url: "/ListProfile",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Profiling'},
            params:{
							type:'profile'
						}
        })

         .state('viewprofilegroup', {
            url: "/ListProfileGroup",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Profiling'},
						params:{
							type:'profilegroup'
						}

        })


        .state('createprofile', {
            url: "/CreateProfile?id$mode$returnBack&version",
            templateUrl: "views/profile.html",
            data: {pageTitle: 'Data Profiling'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'Data Profiling',
                        files: [

                        	'js/controllers/ProfileController.js',
                        	'js/services/ProfileService.js'

                        ]
                    }]);
                }]
            }
        })

        .state('createprofilegroup', {
            url: "/CreateProfileGroup?id$mode$returnBack&version",
            templateUrl: "views/profile-group.html",
            data: {pageTitle: 'Data Profiling'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'profilegroup',
                        files: [

                        	'js/controllers/ProfileController.js',
                        	'js/services/ProfileService.js'


                        ]
                    }]);
                }]
            }
        })

         .state('viewprofileresults', {
            url: "/ProfileResultList",
            templateUrl: "views/common-list.html",
            data: {pageTitle: 'Data Profiling'},
						params: {type:'profileexec',isExec:true}
        })
         .state('viewprofileresultspage', {
            url: "/ProfileResults?id&version&type&name",
            templateUrl: "views/profile-result.html",
            data: {pageTitle: 'Data Profiling'},
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'profilegroup',
                        files: [
                        	'js/controllers/ProfileController.js',
                        	'js/services/DataQualityService.js',
                        	'js/services/ProfileService.js'

                        ]
                    }]);
                }]
            }
        })

        .state('adminListapplication', {
           url: "/Admin/application?id&mode&returnBack",
           templateUrl: "views/application.html",
           data: {pageTitle: 'Admin'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [
                            'js/controllers/ApplicationController.js',
                            'js/services/ApplicationService.js'
                       ]
                   });
               }]
           }
       })



        .state('metaListgroup', {
           url: "/dataPreparation/group?id&mode&returnBack",
           templateUrl: "views/group.html",
           data: {pageTitle: 'Data Preparation'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                       	    'js/controllers/MetadataController.js',,
                            'js/services/MetadataService.js'
                       ]
                   });
               }]
           }
       })




        .state('metaListformula', {
           url: "/dataPreparation/formula?id&mode&returnBack&version",
           templateUrl: "views/formula.html",
           data: {pageTitle: 'Data Preparation'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                       	    'js/controllers/FormulaController.js',
                            'js/services/MetadataService.js',
                            'js/services/FormulaService.js'
                       ]
                   });
               }]
           }
       })


        .state('metaListdimension', {
           url: "/dataPreparation/dimension?id&mode&returnBack",
           templateUrl: "views/dimension.html",
           data: {pageTitle: 'Data Preparation'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                       	    'js/controllers/MetadataController.js',
                            'js/services/MetadataService.js'
                       ]
                   });
               }]
           }
       })



        .state('metaListfilter', {
           url: "/dataPreparation/filter?id&mode&returnBack&version",
           templateUrl: "views/filter.html",
           data: {pageTitle: 'Data Preparation'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                      	    'js/controllers/FilterController.js',
                            'js/services/MetadataService.js',
                            'js/services/FilterService.js',


                       ]
                   });
               }]
           }
       })


        .state('metaListmeasure', {
           url: "/dataPreparation/measure?id&mode&returnBack",
           templateUrl: "views/measure.html",
           data: {pageTitle: 'Data Preparation'},
           controller: "",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                       	    'js/controllers/MeasureController.js',
                            'js/services/MetadataService.js'
                       ]
                   });
               }]
           }
       })


       .state('adminListdatasource', {
          url: "/Admin/datasource?id&mode&returnBack&version",
          templateUrl: "views/datasource.html",
          data: {pageTitle: 'Admin'},
          controller: "",
          resolve: {
              deps: ['$ocLazyLoad', function($ocLazyLoad) {
                  return $ocLazyLoad.load({
                      name: 'InferyxApp',
                      insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                      files: [
                           'js/controllers/DataSourceController.js',
                           'js/services/DataSourceService.js',
                      ]
                  });
              }]
          }
      })


      .state('metaListrelation', {
         url: "/dataPreparation/relation?id&mode&returnBack&version",
         templateUrl: "views/relation.html",
         data: {pageTitle: 'Data Preparation'},
         controller: "",
         resolve: {
             deps: ['$ocLazyLoad', function($ocLazyLoad) {
                 return $ocLazyLoad.load({
                     name: 'InferyxApp',
                     insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                     files: [
                        'js/services/MetadataService.js',
                     	  'js/controllers/RelationController.js',
                        'js/services/RelationService.js'
                     ]
                 });
             }]
         }
     })

    .state('metaListmap', {
       url: "/dataPreparation/map?id&mode&returnBack&version",
       templateUrl: "views/map.html",
       data: {pageTitle: 'Data Preparation'},
       controller: "",
       resolve: {
           deps: ['$ocLazyLoad', function($ocLazyLoad) {
               return $ocLazyLoad.load({
                   name: 'metadatamap',
                   insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                   files: [

                   	    'js/controllers/MapController.js',
                        'js/services/MetadataService.js',
                        'js/services/Mapservice.js'
                   ]
               });
           }]
       }
   })

   .state('metaListload', {
       url: "/dataPreparation/load?id&mode&returnBack&version",
       templateUrl: "views/load.html",
       data: {pageTitle: 'Data Preparation'},
       controller: "",
       resolve: {
           deps: ['$ocLazyLoad', function($ocLazyLoad) {
               return $ocLazyLoad.load({
                   name: 'InferyxApp',
                   insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                   files: [
                   	 '   js/controllers/LoadController.js',
                        'js/services/LoadService.js'
                   ]
               });
           }]
       }
   })





   .state('adminListuser', {
            url:"/Admin/user?id&mode&returnBack&version",
            templateUrl: "views/user.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                              'js/controllers/UserController.js',
                              'js/services/UserService.js'

                        ]
                    });
                }]
            }
        })

        .state('registersource', {
            url:"/Admin/RegisterSource",
            templateUrl: "views/register-source.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
												files: [
													 'js/controllers/RegisterSourceController.js',
                        	 'js/services/RegisterSourceServie.js'
                        ]
                    });
                }]
            }
        })

				// .state('migrationassist', {
        //     url:"/Admin/MigrationAssist?type",
        //     templateUrl: "views/migration-assist.html",
        //     data: {pageTitle: 'Admin'},
        //     controller: "",
        //     resolve: {
        //         deps: ['$ocLazyLoad', function($ocLazyLoad) {
        //             return $ocLazyLoad.load({
        //                 name: 'Admin',
        //                 insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
				// 								files: [
				// 									 'js/controllers/MigrationAssistController.js',
        //                 	 'js/services/MigrationAssistServices.js'
        //                 ]
        //             });
        //         }]
        //     }
        // })
				.state('migrationassist', {
            url:"/Admin/MigrationAssist?type",
            templateUrl: "views/migration-assist.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
												files: [
													 'js/controllers/MigrationAssistController.js',
                        	 'js/services/MigrationAssistServices.js'
                        ]
                    });
                }]
            }
        })
				.state('settings', {
            url:"/Admin/Settings",
            templateUrl: "views/settings.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
												files: [
													 'js/controllers/settingsController.js',
                        	 'js/services/settingsService.js'
                        ]
                    });
                }]
            }
        })

				.state('detaitexport', {
            url:"/Admin/MigrationAssist/Export?id&mode&returnBack&version",
            templateUrl: "views/export.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
												files: [
													'js/controllers/MigrationAssistController.js',
													'js/services/MigrationAssistServices.js'
                        ]
                    });
                }]
            }
        })
				.state('detaitimport', {
            url:"/Admin/MigrationAssist/Import?id&mode&returnBack&version",
            templateUrl: "views/import.html",
            data: {pageTitle: 'Admin'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
												files: [
													'js/controllers/MigrationAssistController.js',
													'js/services/MigrationAssistServices.js'
                        ]
                    });
                }]
            }
        })
        .state('metaListfunction', {
          url:"/dataPreparation/function?id&mode&returnBack&version",
          templateUrl: "views/function.html",
          data: {pageTitle: 'Data Preparation'},
          controller: "",
          resolve: {
              deps: ['$ocLazyLoad', function($ocLazyLoad) {
                  return $ocLazyLoad.load({
                      name: 'Meta Data',
                      insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                      files: [
                           'js/controllers/FunctionController.js',
                           'js/services/FunctionService.js'

                      ]
                  });
              }]
          }
      })
       .state('metaListdag', {
          url:"/dataPreparation/dag?id&mode&returnBack",
          templateUrl: "views/dag.html",
          data: {pageTitle: 'Data Preparation'},
          controller: "",
          resolve: {
              deps: ['$ocLazyLoad', function($ocLazyLoad) {
                  return $ocLazyLoad.load({
                      name: 'Meta Data',
                      insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                      files:[
                    	  'js/controllers/DagController.js',
                        'js/services/DagService.js'
                      ]
                  });
              }]
          }
      })


       .state('createwf', {
          url:"/DataPipeline/dagflow?id&mode&action&tab&version",
          templateUrl:"views/data-pipeline.html",
          data: {pageTitle:'Data Pipeline'},
          controller: "GraphResourcesController",
          resolve: {
              deps: ['$ocLazyLoad', function($ocLazyLoad) {
            	  return $ocLazyLoad.load([{
                      name: 'angularFileUpload',
                      files: [
                     	   "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                        //   "assets/global/plugins/jquery-validation/js/additional-methods.min.js",
                           "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                     	   "assets/pages/scripts/form-wizard.js"
                     ]
                  }, {
                      name: 'InferyxApp',
                      files: [
                          'js/services/DagService.js'
                      ]
                  }]);

              }]
          }
      })

   .state('listwf', {
            url:"/DataPipeline/List",
            templateUrl:"views/common-list.html",
            data: {pageTitle: 'Data Pipeline'},
            params:{
							type:"dag"
						}
        })
        .state('resultwf', {
            url:"/DataPipeline/ResultList?dagid",
            templateUrl:"views/common-list.html",
            data: {pageTitle: 'Data Pipeline'},
						params: {type:'dagexec',isExec:true}
        })
			.state('resultgraphwf', {
            url:"/DataPipeline/Result?id&version&type&mode&dagid",
            templateUrl:"views/data-pipeline-result.html",
            data: {pageTitle: 'Data Pipeline'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DataPipelineController.js',
                            'js/services/DagService.js',
                        ]
                    });
                }]
            }
        })
         .state('algorithm', {
            url:"/Datascience/AlgorithmList",
            templateUrl:"views/common-list.html",
            data: {pageTitle: 'Data Science'},
            params:{type:'algorithm'}
        })
        .state('createalgorithm', {
            url:"/CreateAlgorithm?id&mode&returnBack&version",
            templateUrl:"views/algorithm.html",
            data: {pageTitle: 'Data Science'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/AlgorithmController.js',
                            'js/services/AlgorithmService.js'


                        ]
                    });
                }]
            }
        })
        .state('model', {
            url:"/Datascience/ModelList",
						templateUrl:"views/common-list.html",
						data: {pageTitle: 'Data Science'},
						params:{type:'model'}

        })

        .state('createmodel', {
         url:"/CreateModel?id&mode&returnBack&version",
         templateUrl:"views/model.html",
         data: {pageTitle: 'Data Science'},
         //controller: "GraphResourcesController",
         resolve: {
              deps: ['$ocLazyLoad', function($ocLazyLoad) {
            	  return $ocLazyLoad.load([{
                      name: 'angularFileUpload',
                      files: [
                     	   "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                          // "assets/global/plugins/jquery-validation/js/additional-methods.min.js",
                           "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                     	   "assets/pages/scripts/form-wizard.js"
                     ]
                  }, {
                      name: 'InferyxApp',
                      files: [
                    	  'js/controllers/ModelController.js',
                          'js/services/ModelService.js'
                      ]
                  }]);

              }]
          }
      })

        .state('resultmodelmodel', {
            url:"/ModelResult",
            templateUrl:"views/model-result.html",
            data: {pageTitle: 'Data Science'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ModelController.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })

         .state('paramlist', {
            url:"/ParamList",
						templateUrl:"views/common-list.html",
						data: {pageTitle: 'Data Science'},
						params:{type:'paramlist'}

        })
         .state('createparamlist', {
            url:"/CreateParamList?id&mode&returnBack&version",
            templateUrl:"views/paramlist.html",
            data: {pageTitle: 'Data Science'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	'js/controllers/ParamlistController.js',
                            'js/services/ParamlistService.js'
                        ]
                    });
                }]
            }
        })
          .state('paramset', {
            url:"/ParamSet",
						templateUrl:"views/common-list.html",
						data: {pageTitle: 'Data Science'},
						params:{type:'paramset'}

        })

          .state('createparamset', {
            url:"/CreateParamSet?id&mode&returnBack&version",
            templateUrl:"views/paramset.html",
            data: {pageTitle: 'Data Science'},
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                        	'js/controllers/ParamsetController.js',
                            'js/services/ParamsetService.js',
                        ]
                    });
                }]
            }
        })

 .state('jobexecutor', {
           url: "/JobMonitoring",
         templateUrl: "views/job-monitoring-card-list.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                             'js/controllers/JobMonitoringControllers.js',
                             'js/services/JobMonitoringService.js'
                       ]
                   });
               }]
           }
       })


 .state('jobmonitoringlist', {
           url: "/JobMonitoringList?type",
         templateUrl: "views/common-list.html",
           data: {pageTitle: 'Job Monitoring'},
					 params:{isJobExec:true}
       })


  .state('jobmonitoringlistloadexec', {
           url: "/JobMonitoringList/load?id&mode&returnBack",
         templateUrl: "views/load-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                             'js/controllers/LoadExecController.js',
                             'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
 .state('jobexecutorlistdagexec', {
           url: "/JobMonitoringList/pipeline?id&mode&returnBack",
         templateUrl: "views/data-pipeline-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [
                          'js/controllers/DataPipelineExecController.js',
                          'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
  .state('jobexecutorlistruleexec', {
           url: "/JobMonitoringList/rule?id&mode&returnBack",
         templateUrl: "views/rule-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                           'js/controllers/RuleExecController.js',
                            'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
  .state('jobexecutorlistrulegroupexec', {
           url: "/JobMonitoringList/rulegroup?id&mode&returnBack",
         templateUrl: "views/rule-group-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                           'js/controllers/RuleGroupExecController.js',
                            'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
   .state('jobexecutorlistmapexec', {
           url: "/JobMonitoringList/map?id&mode&returnBack",
         templateUrl: "views/map-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                           'js/controllers/MapExecController.js',
                            'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
   .state('jobexecutorlistmodelexec', {
           url: "/JobMonitoringList/model?id&mode&returnBack",
         templateUrl: "views/model-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [
                          'js/controllers/ModelExecController.js',
                          'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
   .state('jobexecutorlistprofileexec', {
           url: "/JobMonitoringList/profile?id&mode&returnBack",
         templateUrl: "views/profile-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [
                          'js/controllers/profileExecController.js',
                          'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
   .state('jobexecutorlistprofilegroupexec', {
           url: "/JobMonitoringList/profilegroup?id&mode&returnBack",
         templateUrl: "views/profile-group-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                          'js/controllers/ProfileGroupExecController.js',
                          'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
   .state('jobexecutorlistdqexec', {
           url: "/JobMonitoringList/dq?id&mode&returnBack",
         templateUrl: "views/dataquality-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                           'js/controllers/DataQualityExecController.js',
                            'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
      .state('jobexecutorlistdqgroupexec', {
           url: "/JobMonitoringList/dqgroup?id&mode&returnBack",
         templateUrl: "views/dataquality-group-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                           'js/controllers/DataQualityGroupExecController.js',
                            'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })
        .state('jobexecutorlistvizpodexec', {
           url: "/JobMonitoringList/vizpod?id&mode&returnBack",
         templateUrl: "views/vizpod-exec.html",
           data: {pageTitle: 'Job Monitoring'},
           //controller: "BlankController",
           resolve: {
               deps: ['$ocLazyLoad', function($ocLazyLoad) {
                   return $ocLazyLoad.load({
                       name: 'InferyxApp',
                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                       files: [

                          'js/controllers/VizpodExecController.js',
                          'js/services/JobMonitoringService.js',
                       ]
                   });
               }]
           }
       })

			 .state('commonlistpage', {
		           url: "/list?type",
		         templateUrl: "views/common-list.html",
		           data: {pageTitle: 'List Page'},
		           //controller: "BlankController",
		           resolve: {
		               deps: ['$ocLazyLoad', function($ocLazyLoad) {
		                   return $ocLazyLoad.load({
		                       name: 'InferyxApp',
		                       insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
		                       files: [

		                           'js/controllers/commonlistcontroller.js',

		                       ]
		                   });
		               }]
		           }
		       })
					 .state('systemmonitering', {
							url: "/SystemMonitoring?type",
						templateUrl: "views/systemmonitering.html",
							data: {pageTitle: 'System Monitoring'},
							//controller: "BlankController",
							resolve: {
									deps: ['$ocLazyLoad', function($ocLazyLoad) {
											return $ocLazyLoad.load({
													name: 'InferyxApp',
													insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
													files: [

														 'js/controllers/SystemMoniteringController.js',
														 'js/services/SystemMoniteringService.js',
														 'js/directives/ChartDirective.js',
													]
											});
									}]
							}
					})

 }]);


InferyxApp.factory('privilegeSvc',function ($http, $location, $rootScope) {
	var baseUrl = $location.absUrl().split("app")[0];
	var url = baseUrl + 'security/getRolePriv',
	obj = {};
	$rootScope.privileges = obj.privileges = {};
	covertPrivileges = function (arr) {
		angular.forEach(arr,function (val,key) {
			obj.privileges[val.type] = val.privInfo;
		});
		$rootScope.$broadcast('privilegesUpdated');
	}
	obj.get = function () {
		if(obj.privileges == {}){
			$http.get(url).then(function (response) {
				covertPrivileges(response.data);
			});
			return obj.privileges;
		}
		else {
			return obj.privileges;
		}
	}
	obj.getUpdated = function () {
		$http.get(url).then(function (response) {
			covertPrivileges(response.data);
		});
		return obj.privileges;
	}
	// obj.getUpdated();

	return obj
})

/* Init global settings and run the app */
InferyxApp.run(["$rootScope","settings", "$state","privilegeSvc", function($rootScope,settings, $state, privilegeSvc) {
		$rootScope.$state = $state; // state to be accessed from view
    $rootScope.$settings = settings; // state to be accessed from view
		privilegeSvc.getUpdated();
}]);
