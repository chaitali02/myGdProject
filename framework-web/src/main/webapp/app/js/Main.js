


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
    "ReconModule",
    "DataQualityModule",
    "ProfileModule",
    "BatchModule",
    "AdminModule",
    "JobMonitoringModule",
    "SystemMonitoringModule",
    "DataPipelineModule",
    "DatascienceModule",
    "GraphAnalysisModule",
    "VizpodModule",
    "DataIngestionModule",
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
    'ui.bootstrap.datetimepicker',
    'ngclipboard',
    "Utils",
    "ui.multiselect",
    "jsonFormatter",
    "moment-picker",
    "multipleDatePicker"
   
]);


/*Configure ocLazyLoader(refer: https://github.com/ocombe/ocLazyLoad) */
InferyxApp.config(['$httpProvider', '$ocLazyLoadProvider', 'KeepaliveProvider', 'IdleProvider', function ($httpProvider, $ocLazyLoadProvider, KeepaliveProvider, IdleProvider) {
	/* $ocLazyLoadProvider.config({
	        // global configs go here
	    });*/
       
    if (typeof localStorage.userdetail != "undefined") {
        console.log(JSON.parse(localStorage.userdetail).sessionId)
        $httpProvider.defaults.headers.common['sessionId'] = JSON.parse(localStorage.userdetail).sessionId
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

    $httpProvider.interceptors.push(function ($rootScope, $q) {
        return {
            'request': function (config) {
                if (config.timeout) {
                  config.cancel  = $q.defer();
                  config.timeout = config.cancel.promise;            
                }
        
                return config;
              },
        
            'responseError': function (rejection) {
               
                            
                if (rejection.status == 500) {
                    notify.type = 'error',
                    notify.title = 'Some Error Occured',
                    notify.content = "Please check log or contact administrator"//"Dashboard Deleted Successfully"
                    $rootScope.$emit('notify', notify);
                }
                else if (rejection.status != 200 && rejection.status != 500 && rejection.status != 419 && rejection.status != -1 ) {
                    notify.type = 'info';
                    notify.title = 'Info';
                    try {
                        notify.content = rejection.data.message;
                        if(rejection.data instanceof ArrayBuffer){
                            var decodedString = String.fromCharCode.apply(null, new Uint8Array(rejection.data));
                            var obj = JSON.parse(decodedString);
                            var message = obj['message'];
                            notify.content = message;
                        }
                        
                    } catch (e) {
                        notify.content = "Error Code" + rejection.status
                    } finally {
                    }
                    $rootScope.$emit('notify', notify);
                }
                else if (rejection.status == 419) {
                    console.log(rejection.status);       
                    notify.type = 'info';
                    notify.title = 'Info';
                    try {
                        notify.content = rejection.data.message;
                    } catch (e) {
                        notify.content = "Error Code" + rejection.status
                    } finally {
                    }
                    $rootScope.$emit('notify', notify);
                    $rootScope.$emit('CallFromAppRoleControllerLogout', {});
                }else if(rejection.status == -1){
                    
                    notify.type = 'error',
                    notify.title = 'Some Error Occured',
                    notify.content = "Please contact administrator"
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


InferyxApp.run(['Idle', '$sessionStorage', '$rootScope', '$http', '$cookieStore', 'validator', '$timeout', '$filter', 'commentService','defaultErrorMessageResolver','$window','CF_DOWNLOAD', function (Idle, $sessionStorage, $rootScope, $http, $cookieStore, validator, $timeout, $filter, commentService,defaultErrorMessageResolver,$window,CF_DOWNLOAD) {
    Idle.watch();
    validator.setValidElementStyling(false);
    validator.setInvalidElementStyling(true);
    defaultErrorMessageResolver.getErrorMessages().then(function (errorMessages) {
        errorMessages['maxLimitDownload'] = 'Max rows exceeded the limit ('+CF_DOWNLOAD.framework_download_maxrow+')';
        errorMessages['parttenFileName'] = 'invalid charater';
      });
    if (localStorage.userdetail) {
          $rootScope.productDetail = (JSON.parse(localStorage.userdetail).productDetail);
        $rootScope.role = localStorage.role;
        $rootScope.baseUrl = JSON.parse(localStorage.userdetail).baseUrl
    }
    $rootScope.time = new Date();
     $rootScope.tzName = $rootScope.time.toLocaleString('en', {
        timeZoneName: 'short'
    }).split(' ').pop();
    var update = function () {
        $rootScope.time = $filter('date')(new Date(), "MMM dd yyyy - HH:mm:ss");
        $timeout(update, 1000);
    }
    $timeout(update, 1000);

    //console.log("Time until reaching run phase: ", Date.now() - $window.timerStart);
}]);

InferyxApp.factory('commentService', function () {
    var comment = {
        isPanelOpen: false,
    }
    return comment;
});



/* Setup global settings */
InferyxApp.factory('settings', ['$rootScope', function ($rootScope) {
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


InferyxApp.factory('ajaxCallFactory', function ($http, $location) {
    var ajaxCallServiceFactory = {};
    ajaxCallServiceFactory.getCall = function (url, sessionId) {
        var base_url = $location.absUrl().split("app")[0];
        url = base_url + url;
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                ajaxCallServiceFactory.data = this.responseText;
            }
        };
        xhttp.open("GET", url, false);
        xhttp.setRequestHeader("sessionId", sessionId);
        xhttp.send();
        return ajaxCallServiceFactory.data;
    }
    return ajaxCallServiceFactory;
});

InferyxApp.controller('TabController', function ($timeout, $state, $scope, $rootScope, $stateParams,$sessionStorage) {
    $scope.activeTabIndex = 1;
    $rootScope.rootTabs = $scope.tabs = [];
    $scope.showTabs = true;

    $scope.go = function (tab) {
        if (typeof tab != "undefined") {
            $rootScope.rootActiveTab = 'Tab-' + $scope.tabs.indexOf(tab);
            $rootScope.activeState = tab.route.substring(4);
            $state.go(tab.route, tab.param);
        }
    };

    $scope.active = function (route, params) {
        return $state.is(route, params);
    };
    function isTabAvailable(tab) {
        var available = -1;
        angular.forEach($scope.tabs, function (val, key) {
            if (val.route == tab.route && angular.equals(val.param, tab.param))
                available = key;
        });

        return available;
    }
    $scope.$on('onAddTab', function (e, tab) {
        $scope.addTab(tab)
        $scope.activeTabIndex = null;
    });
    $rootScope.$on('onDeInitTabs', function () {
        $scope.tabs = [];
    })
    $scope.addTab = function (tab) {
        var indexOfTab = isTabAvailable(tab);
        if (indexOfTab > -1) {
            $scope.go($scope.tabs[indexOfTab]);
            return
        }
        tab.index = $scope.tabs.length;
        tab.heading = tab.param.type + "-" + tab.param.name || "Tab" + (tab.index + 1);
        $scope.tabs.push(tab);
    }

    $scope.$watch('activeTabIndex', function (oldv, newv) {
        $scope.showTabs = false;
        $timeout(function () {
            $scope.showTabs = true;
        }, 1)
    });

    $scope.removeTab = function (index) {
        if (index == 0) {
            $scope.tabs.splice(index, 1);
            $scope.showTabs = false;
            angular.forEach($scope.tabs, function (val, key) {
                // val.heading = val.param.name || "Tab"+(key+1);
                val.index = key;
                val.active = false;
            });
            $scope.showTabs = true;
            $scope.activeTabIndex = 0;
        }
        else if (!$scope.tabs[index].active) {
            $scope.tabs.splice(index, 1);
        }
        else {
            $scope.tabs.splice(index, 1);
            $scope.showTabs = false;
            angular.forEach($scope.tabs, function (val, key) {
                // val.heading = val.param.name || "Tab"+(key+1);
                val.index = key;
                val.active = false;
            });
            $scope.showTabs = true;
            $scope.go($scope.tabs[index - 1]);
        }
    };

    $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
        $sessionStorage.fromStateName = fromState.name
        $sessionStorage.fromParams = fromParams
        if ($scope.tabs.length == 0) {
            var state = {};
            state.route = toState.name;
            state.index = $scope.tabs.length + 1;
            state.param = toParams;
            state.heading = "Main Tab";
            state.active = false;
            $scope.tabs.splice($scope.tabs.length, 0, state);
        }
        else {
            if ($scope.tabs.length == 1) {
                $scope.tabs[0].route = toState.name;
                $scope.tabs[0].param = toParams;
            }
        }
        $rootScope.isCommentVeiwPrivlage=true;
    });

    $rootScope.$on("$stateChangeSuccess", function () {
        $rootScope.isCommentVeiwPrivlage=true;
        if ($scope.tabs.length > 0) {
            $scope.showTabs = false;
            angular.forEach($scope.tabs, function (val, key) {

                val.active = $scope.active(val.route, val.param);
                if (val.active)
                    $scope.activeTabIndex = key;
                $rootScope.rootActiveTab = "Tab-" + key;
                $rootScope.activeState = val.route.substring(4);
            });
            $scope.showTabs = true;
        }
    });
});



InferyxApp.controller('lhscontroller', function ($scope, $rootScope, $window, $state,$stateParams, LhsService, $timeout) {
    $rootScope.metaStats = {};
    $scope.deInitTabs = function () {
        var param = {};
        param.index = null;
        $rootScope.$broadcast('onDeInitTabs', param);
    }
    
    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
        if (typeof localStorage.userdetail == "undefined") {
            localStorage.test="";
            if($stateParams.redirect == "true"){
              console.log("jit"+$state.current.name);
               var link={}
               link.state=$state.current.name;
               link.params=$stateParams;
               localStorage.link=JSON.stringify(link);
            }
            setTimeout(function(){
                $window.location.href = 'login.html';
            },100)
            
            return false;
       }
     });
  
   

    if (!$rootScope.$$listenerCount['CallFromAppRoleController']) {
        $rootScope.$on("CallFromAppRoleController", function () {
            $scope.updateMetaData();
        });
    }




    $scope.metadatasubmenu;
    $scope.Metadata = {
        "caption": "Data Preparation",
        "name": "metadata",
        "class": "fa fa-link",
        "submenu": []
    };

    $scope.Datadiscovery = {
        "caption": "Data Discovery",
        "name": "datadiscovery",
        "class": "fa fa-binoculars",
        "submenu": []
    };

    $scope.MetaDatanavigator = {
        "caption": "Metadata Navigator",
        "name": "metadatanavigator",
        "class": "fa fa-paper-plane",
        "submenu": []
    };

    $scope.Ruledata = {
        "caption": "Business Rules",
        "name": "rules",
        "class": "fa fa-cogs",
        "submenu": [
            { "name": "viewrule", "type": "rule", "typeCount": "rule", "uuid": "null", "caption": "Rule" },
            { "name": "viewrule2", "type": "rule2", "typeCount": "rule2", "uuid": "null", "caption": "Rule2" },

            { "name": "rulesgroup", "type": "rulegroup", "typeCount": "rulegroup", "uuid": "null", "caption": "Rule Group" },
            { "name": "paramlistrule", "type": "paramlist", "typeCount": "paramlistrule", "uuid": "null", "caption": "Parameter List" },
            { "name": "rulerestult", "type": "ruleexec", "typeCount": "ruleexec", "uuid": "null", "caption": "Rule Results" },
            { "name": "rule2restult", "type": "ruleexec", "typeCount": "ruleexec", "uuid": "null", "caption": "Rule2 Results" }
        
        ]
    }
    $scope.Recondata = {
        "caption": "Data Reconciliation",
        "name": "recone",
        "class": "fa fa-compress",
        "submenu": [
            { "name": "datareconrule", "type": "recon", "uuid": "null", "caption": "Rule" },
            { "name": "datareconrulegroup", "type": "recongroup", "uuid": "null", "caption": "Rule Group" },
            { "name": "datareconrulerestult", "type": "reconexec", "uuid": "null", "caption": "Rule Results" }
        ]
    }

    $scope.Dataqualitydata = {
        "caption": "Data Quality",
        "name": "data quality",
        "class": "fa fa-rss",
        "submenu": [
            { "name": "viewdataquality", "type": "dq", "uuid": "null", "caption": "Rule" },
            { "name": "viewdataqualitygroup", "type": "dqgroup", "uuid": "null", "caption": "Rule Group" },
            { "name": "viewdqresults", "type": "dqexec", "uuid": "null", "caption": "Rule Results" },
            { "name": "viewdqresults2", "type": "dqexec", "uuid": "null", "caption": "Rule Results2" }

        ]
    }

    $scope.Profiledata = {
        "caption": "Data Profiling",
        "name": "profile",
        "class": "fa fa-users",
        "submenu": [
            { "name": "viewprofile", "type": "profile", "uuid": "null", "caption": "Rule" },
            { "name": "viewprofilegroup", "type": "profilegroup", "uuid": "null", "caption": "Rule Group" },
            { "name": "viewprofileresults", "type": "profileexec", "uuid": "null", "caption": "Rule Results" },
        ]
    };
    $scope.Dagworkflow = {
        "caption": "Data Pipeline",
        "name": "workflow",
        "class": "fa fa-random",
        "submenu": [
            { "name": "createwf", "type": "createwf", "uuid": "null", "caption": "Create New" },
            { "name": "listwf", "type": "dag", "uuid": "null", "caption": "List" ,"typeCount": "dag", },
            { "name": "paramlistdag", "type": "paramlist", "typeCount": "paramlistdag", "uuid": "null", "caption": "Parameter List" },
            { "name": "resultwf", "type": "dagexec", "uuid": "null", "caption": "Results","typeCount": "dagexec", }
        ]
    };
    $scope.Batch = {
        "caption": " Batch Scheduler",
        "name": "batch",
        "class": "fa fa-tasks",
        "submenu": [
            { "name": "batchdetail", "type": "createwf", "uuid": "null", "caption": "Create New" },
            { "name": "batchlist", "type": "batch", "uuid": "null", "caption": "List" ,"typeCount": "batch", },
            { "name": "batchexeclist", "type": "batchExec", "uuid": "null", "caption": "Results","typeCount": "batchexec", }
        ]
    };
    $scope.GraphAnalysis = {
        "caption": "Link Analysis ",
        "name": "graphanalysis ",
        "class": "fa fa-share-alt",
        "submenu": [
            { "name": "creaetgraphpod", "type": "graphpod", "typeCount": "","uuid": "null", "caption": "Create New" },
            { "name": "listgraphpod", "type": "graphpod", "typeCount": "graphpod","uuid": "null", "caption": "List" },
            { "name": "graphpodresultlist", "type": "graphexec","typeCount": "graphexec", "uuid": "null", "caption": "Results" }
        ]
    };
    $scope.Datascience = {
        "caption": "Data Science",
        "name": "datascience",
        "class": "fa fa-flask",
        "submenu": [
            { "name": "algorithm", "type": "algorithm", "typeCount": "algorithm", "uuid": "null", "caption": "Algorithm" },
            { "name": "distribution", "type": "distribution", "typeCount": "distribution", "uuid": "null", "caption": "Distribution" },
            { "name": "model", "type": "model", "typeCount": "model", "uuid": "null", "caption": "Model" },
            { "name": "paramlistmodel", "type": "paramlist", "typeCount": "paramlistmodel", "uuid": "null", "caption": "Parameter List" },
            { "name": "paramset", "type": "paramset", "typeCount": "paramset", "uuid": "null", "caption": "Parameter Set" },
            { "name": "operator", "type": "operator", "typeCount": "operator", "uuid": "null", "caption": "Operator" },
            { "name": "train", "type": "train", "typeCount": "train", "uuid": "null", "caption": "Training" },
            { "name": "predict", "type": "predict", "typeCount": "predict", "uuid": "null", "caption": "Prediction" },
            { "name": "simulate", "type": "simulate", "typeCount": "simulate", "uuid": "null", "caption": "Simulation" },
            { "name": "modelDeploy", "type":"deployexec", "typeCount": "deployexec", "uuid": "null", "caption": "Model Deploy" },
            { "name": "vartifAnalysis", "type":"VartifAnalysis", "typeCount": "VartifAnalysis", "uuid": "null", "caption": "What-If Analysis" },
            { "name": "resultmodelmodel", "type": "trainexec", "typeCount": "trainexec", "uuid": "null", "caption": "Results" }
        ]
    };

    $scope.Datavisualization = {
        "caption": "Data Visualization",
        "name": "datavisualization",
        "class": "fa fa-desktop",
        "submenu": [
            { "name": "dashboard", "type": "dashboard", "uuid": "null", "caption": "Dashboard" },
            { "name": "vizpodlist", "type": "vizpod", "uuid": "null", "caption": "Vizpod" },
            { "name": "reportlist", "type": "report", "uuid": "null", "caption": "Report" },
            // { "name": "reportlist2", "type": "report2", "uuid": "null", "caption": "Report2" },
            { "name": "paramlistreport", "type": "paramlistreport", "typeCount": "paramlistreport", "uuid": "null", "caption": "Parameter List" },
            { "name": "reportexeclist", "type": "reportexec", "uuid": "null", "caption": "Results","typeCount": "reportexec", },
            { "name": "reportArchives", "type": "reportexec", "uuid": "null", "caption": "Archives","typeCount": "reportexec",}

        ]  
    }
    $scope.Ingestdata = {
        "caption": "Data Ingestion",
        "name": "ingest",
        "class": "fa fa-random",
        "submenu": [
            // { "name": "ingestrulelist", "type": "ingest","typeCount": "ingest","uuid": "null", "caption": "Rule" },
            { "name": "ingestrulelist2", "type": "ingest","typeCount": "ingest","uuid": "null", "caption": "Rule" },
            { "name": "ingestrulegrouplist", "type": "ingestgroup", "typeCount": "ingestgroup","uuid": "null", "caption": "Rule Group" },
            { "name": "ingestrulerestultlist", "type": "ingestexec",  "typeCount": "ingestexec","uuid": "null", "caption": "Rule Results" }
        ]
    }

    $scope.Admindata = {
        "caption": "Admin",
        "name": "admin",
        "class": "fa fa-wrench",
        "submenu": []
    }

    $scope.Datapoddata = {
        "caption": "Data Pod",
        "name": "datapod",
        "class": "fa fa-list",
        "submenu": []
    },
    $scope.Vizpoddata = {
        "caption": "Viz Pod",
        "name": "vizpod",
        "class": "fa fa-bar-chart",
        "submenu": []
    };

    $scope.JobExecutor = {
            "caption": "Job Monitoring",
            "name": "jobexecutor",
            "class": "fa fa-tasks",
            "submenu": []
    };
    $scope.SystemMonitering = {
        "caption": "System Monitoring",
        "name": "systemmonitering",
        "class": "fa fa-tasks",
        "submenu": []
    };

    $scope.convertUppdercase = function (value) {
        if (!value)
            return value;
        var resultvalue = value.split("_");
        var resultjoint = [];
        for (j = 0; j < resultvalue.length; j++) {
            resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
        }
        return resultjoint.toString().replace(/,/g, " ");
    }

    $scope.updateMetaData = function () {
        LhsService.getMetadata().then(function (response) { onMetadataSuccess(response.data) });
        var onMetadataSuccess = function (metadata) {
            var countMeta = 0;
            var countAdmin = 0;
            for (var i = 0; i < metadata.length; i++) {
                var meta = {};
                if (metadata[i].menu == 'DataPreparation' && metadata[i].active == 'Y') {
                    meta.name = metadata[i].name;
                    meta.uuid = metadata[i].uuid
                    meta.caption = $scope.convertUppdercase(metadata[i].name)
                    $scope.Metadata.submenu[countMeta] = meta
                    countMeta = countMeta + 1;
                }
                else if (metadata[i].menu == 'admin' && metadata[i].active == 'Y') {
                    meta.name = metadata[i].name;
                    meta.uuid = metadata[i].uuid
                    meta.caption = $scope.convertUppdercase(metadata[i].name)
                    $scope.Admindata.submenu[countAdmin] = meta
                    countAdmin = countAdmin + 1;
                }
            }
            if (localStorage.isAppRoleExists) {
                $rootScope.metaStats={};
                LhsService.getMetaStats().then(function (response) { onSuccessGetMetaStats(response.data) });
                var onSuccessGetMetaStats = function (response) {
                    angular.forEach(response, function (val, key) {
                        $rootScope.metaStats[val.type] = val;
                    });
                }
            }
        }


        // }//End ELSE
    }//End updateMetaData Method
});

InferyxApp.controller('AppRoleController', function ($scope,$sessionStorage,$rootScope, $cookieStore, AppRoleService, $cookieStore, $window, $state, privilegeSvc, LhsService, $stateParams) {
   
    $rootScope.reOpen=localStorage.reOpen;
    console.log($scope.selectedApp)
    
    if (localStorage.isAppRoleExists && $rootScope.reOpen ==false) {
        $rootScope.setUserName = JSON.parse(localStorage.userdetail).name
        $rootScope.setUseruuid = JSON.parse(localStorage.userdetail).userUUID
        $scope.username = JSON.parse(localStorage.userdetail).userName;   
        $rootScope.appUuid = localStorage.appUuid;     
        return false;
    }
  
    $scope.selectedApp;
    $scope.selectedRole;
    $scope.selectAppStatus = false;
    $scope.selectRoleStatus = false;
    if(typeof localStorage.userdetail != "undefined"){
        $rootScope.setUserName = JSON.parse(localStorage.userdetail).name
        $rootScope.setUseruuid = JSON.parse(localStorage.userdetail).userUUID
        $scope.username = JSON.parse(localStorage.userdetail).userName;
    }
    
    $rootScope.appUuid = localStorage.appUuid; 
    $rootScope.isSubmit = false;
    AppRoleService.getLatestByUuid($rootScope.setUseruuid, "user").then(function (response) { onSuccessGetLatestByUuid(response.data) });
    var onSuccessGetLatestByUuid = function (response) {
        if(response)   
        $scope.username = response.firstName;
        $rootScope.userdata=response;
    }   
   
    AppRoleService.getAppRole($rootScope.setUserName).then(function (response) { onAppSuccess(response.data) })
    var onAppSuccess = function (response) {
        $scope.AppData = response
            $scope.RoleData = response[0].roleInfo;
            $scope.selectedRole = response[0].roleInfo[0]
            $scope.selectedApp = response[0];
            for(var i=0;i<response.length;i++){
                if(response[i].defaultAppId !=null && typeof localStorage.link == "undefined"){
                    console.log("Y"+response[i].appId.ref.uuid)
                    $scope.selectedApp = response[i];
                    $scope.selectedRole = $scope.selectedApp.roleInfo[0];
                    $scope.RoleData = response[i].roleInfo;
                    break;
                }else{
                    if(typeof localStorage.link != "undefined"){
                        var link=JSON.parse(localStorage.link);
                        if(response[i].appId.ref.uuid == link.params.appId){
                            $scope.selectedApp =response[i];
                            $scope.selectedRole = $scope.selectedApp.roleInfo[0];
                            $scope.RoleData = response[i].roleInfo;
                            console.log(localStorage.link);
                            $scope.ok();
                            break;
                        }
                    }
                }
            }
            
            if(!localStorage.isAppRoleExists){
            $rootScope.appUuid = $scope.selectedApp.appId.ref.uuid;
            localStorage.appName = $scope.selectedApp.appId.ref.name;
            $scope.selectAppStatus = true;
            $scope.selectRoleStatus = true;
            localStorage.role = $scope.selectedRole.ref.name;
            $rootScope.role = localStorage.role;
            $rootScope.appType=$scope.selectedApp.applicationType;
            AppRoleService.getTZ().then(function (responseTz) { onSuccessgetTZ(responseTz.data) });
            var onSuccessgetTZ = function (responseTz) {
                localStorage.serverTz = responseTz;
            }
        }
    };
    
   
    $scope.getselectApp = function () {
        if ($scope.selectedApp != null) {
            localStorage.appName = $scope.selectedApp.appId.ref.name;
            $rootScope.appUuid = $scope.selectedApp.appId.ref.uuid;
            $scope.selectAppStatus = true;
            $scope.selectRoleStatus = true;
            $scope.RoleData = $scope.selectedApp.roleInfo
            $scope.selectedRole = $scope.selectedApp.roleInfo[0]
            localStorage.role = $scope.selectedRole.ref.name
            $rootScope.role = localStorage.role;
            $rootScope.appType=$scope.selectedApp.applicationType;
        }
        else {
            $scope.selectAppStatus = false;
            $scope.selectRoleStatus = false;
        }
    }

    $scope.getselectRole = function () {
        if ($scope.selectedRole != null) {
            $scope.selectRoleStatus = true;
            localStorage.role = $scope.selectedRole.ref.name
            $rootScope.role = localStorage.role;
        }
        else {
            $scope.selectRoleStatus = false;
        }
    }
    $scope.open = function () {
        if (!localStorage.isAppRoleExists && typeof localStorage.userdetail != "undefined" && typeof localStorage.link == "undefined") {
            $('#myModal').modal({
                backdrop: 'static',
                keyboard: false
            });
        }
        else{
            

        }

    };

    $scope.ok = function (event) {
        //event.preventDefault();
        $('#myModal').modal('hide');
        $rootScope.isWelcomenOpen=true;
        if ($scope.selectedApp != null) {
            AppRoleService.setSecurityAppRole($scope.selectedApp.appId.ref.uuid, $scope.selectedRole.ref.uuid).then(function (response) { onSecurityAppRoleSuccess(response) })
            var onSecurityAppRoleSuccess = function (response) {
                localStorage.isAppRoleExists = true;
                localStorage.appUuid=$scope.selectedApp.appId.ref.uuid;
                localStorage.appName = $scope.selectedApp.appId.ref.name;
                localStorage.role = $scope.selectedRole.ref.name
                $rootScope.role = localStorage.role;
                console.log(JSON.stringify(response.data));
                $rootScope.metaStats={};
                LhsService.getMetaStats().then(function (response) { onSuccessGetMetaStats(response.data) });
                var onSuccessGetMetaStats = function (response) {
                    angular.forEach(response, function (val, key) {
                        $rootScope.metaStats[val.type] = val;
                    });
                }
                setTimeout(function(){$rootScope.isWelcomenOpen=false; }, 3000);
                privilegeSvc.getUpdated();
              //  $('#myModal').modal('hide');
                $rootScope.$emit("callsetapp", {});
            };
        }
        else {
            $("#hideshow").removeClass('display-hide')
            $("#hideshow").addClass('display-show')
        }
        $rootScope.reOpen=false;
        localStorage.reOpen=false;
        $state.go('datadiscovery',{}, {reload: true});
        console.log($state.current)
        if($state.current.url !=''){
            $state.transitionTo('datadiscovery',{}, {
                reload: true,
                inherit: false,
                notify: true
            });
        }else{
            if(typeof localStorage.link != "undefined"){
                var link=JSON.parse(localStorage.link);
                $state.go(link.state,link.params);
                localStorage.removeItem('link');
           }
        }
    };

    $scope.cancelLogut = function () {
        if (!localStorage.isAppRoleExists) {
            $rootScope.$emit('CallFromAppRoleControllerLogout', {});
        }
        else{
            $('#myModal').modal('hide');
            $rootScope.reOpen=false;
            localStorage.reOpen=false;
            if ($sessionStorage.fromStateName){
              //  $state.go($sessionStorage.fromStateName,$sessionStorage.fromParams);
            }else{
                $state.go('datadiscovery');
            }
        }
    }
   

});

InferyxApp.controller('UnloadController', function ($rootScope, $scope, $sessionStorage, UnlockService) {

    $scope.isErrorShow = false;
    $scope.initOpen = function () {
        if ($sessionStorage.unlockStatus == true) {
            $('#unlockmodel').modal({
                backdrop: 'static',
                keyboard: false
            });
        }
    }
    $scope.open = function () {
        $sessionStorage.unlockStatus = true;
        $scope.unlockusername = "";
        $scope.unlockpassword = "";
        $('#unlockmodel').modal({
            backdrop: 'static',
            keyboard: false
        });

    }
    $scope.hideErrorMessage = function () {
        $scope.isErrorShow = false
    }
    $scope.close = function () {
        $scope.isErrorShow = false
        $scope.unlockusername = $rootScope.setUserName
        UnlockService.getUnlockApp($scope.unlockusername, $scope.unlockpassword).then(function (response) { onSuccess(response.data) })
        $scope.unlockusername = "";
        $scope.unlockpassword = "";
        var onSuccess = function (response) {
            if (response == true) {
                $sessionStorage.unlockStatus = false;

                $('#unlockmodel').modal('hide');
            }
            else {
                $scope.isErrorShow = true;
                $scope.error = "Invalid Login"
            }
        }
    }
    $scope.notYpalrecha = function () {
        $rootScope.$emit('CallFromAppRoleControllerLogout', {});
    }
});



InferyxApp.controller('LogoutController', function ($scope, $rootScope, $cookieStore, $window, LogoutService) {
    $scope.$on('IdleStart', function () {
        $scope.logout();

    });
    $scope.$on('IdleEnd', function () {

    });

    $scope.$on('IdleTimeout', function () {


    });
    var customeEventListener =$rootScope.$on("CallFromAppRoleControllerLogout", function () {
        $scope.logout();
    });

    $scope.$on('$destroy', function() {
        customeEventListener();
    });

    $scope.logout = function () {
        LogoutService.securitylogoutSession($.cookie("sessionId")).then(function (response) { onSecuritySuccess(response) }, function (response) { onError(response) })
        var onSecuritySuccess = function (response) {
            if (response.data.userInfo.ref.uuid == JSON.parse(localStorage.userdetail).userUUID) {
                localStorage.removeItem('isAppRoleExists');
                localStorage.clear();
                if (typeof localStorage.userdetail == "undefined") {
                    $window.location.href = 'login.html';
                }
            }
        }
        var onError = function (response) {
            localStorage.removeItem('userdetail');
            localStorage.removeItem('isAppRoleExists');
            localStorage.clear();
            if (typeof localStorage.userdetail == "undefined") {
                $window.location.href = 'login.html';
            }
        }

    };
});




/* Setup App Main Controller */
InferyxApp.controller('AppController', ['$scope', '$rootScope', 'commentService', function ($scope, $rootScope, commentService) {
    $rootScope.isCommentDisabled = true;

    $scope.isPanelOpen = commentService.isPanelOpen;
    $rootScope.dirOptions = {};
    $rootScope.onPanelOpen = function () {
        $scope.isPanelOpen = !$scope.isPanelOpen;
        $rootScope.dirOptions.panelToggle($scope.isPanelOpen);
    }
    $rootScope.onPanelClose = function (data) {
        $scope.isPanelOpen = false;
        $rootScope.dirOptions.closePanel();

    }
    $scope.$on('$viewContentLoaded', function () {
        //App.initComponents(); // init core components
        //Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
    });
}]);



/* Setup Layout Part - Header */
InferyxApp.controller('HeaderController', ['$uibModal', '$scope', '$rootScope', '$cookieStore', '$stateParams', '$state', 'dagMetaDataService','$sessionStorage','CF_APP_SETTING', function ($uibModal, $scope, $rootScope, $cookieStore, $stateParams, $state, dagMetaDataService,$sessionStorage,CF_APP_SETTING) {
    $rootScope.dummyArg = 1;
    $scope.appSetting=CF_APP_SETTING;
    $rootScope.caseConverter = function (str) {
        var temp = str.charAt(0).toUpperCase() + str.slice(1);
        return temp.replace(/([A-Z][a-z])/g, " $1");
    }
    $rootScope.genericClose = function (e, type) {
        if ($stateParams.returnBack == "true" && $rootScope.previousState) {
            //revertback
            e.preventDefault();
            $state.go($rootScope.previousState.name, $rootScope.previousState.params);
            $rootScope.previousState = undefined;
        }
        else if (type) {
            //var sref = $(this).attr('data-ui-sref');
            $scope.statedetail = {};
            $scope.statedetail.name = dagMetaDataService.elementDefs[type].listState
            $scope.statedetail.params = { type: type }
            $state.go($scope.statedetail.name, $scope.statedetail.params)
        }
    }
    $rootScope.broadcastRefreshData = function () {

        $rootScope.$broadcast('refreshData');
    }
    $scope.setAppStatus = false;
    $rootScope.$on('callsetapp', function () {
        $scope.getAppName();

    });
    $scope.getAppName = function () {
        if (localStorage.appName) {
            $scope.selectApp = localStorage.appName
            $scope.setAppStatus = true;

        }
    }

    $scope.$on('$includeContentLoaded', function () {
        Layout.initHeader(); // init header
    });

    $scope.uploadAvatar = function () {
        var modalInstance = $uibModal.open({
            templateUrl: 'views/upload-profile-image.html',
            controller: 'ProfileImageCtrl',
            size: 'md',
            windowTopClass: 'avatar-modal',
            scope: $scope
        });
    };

    $scope.OepnWelcomeWindow=function(){
        $rootScope.reOpen=true;
        localStorage.reOpen=true;
        // if ($sessionStorage.fromStateName){
        //     $state.go($sessionStorage.fromStateName,$sessionStorage.fromParams);
        // }
        // else{
        //     $state.go('/home');
        // }
           $('#myModal').modal({
                backdrop: 'static',
                keyboard: false
            });
            
    }
}]);

InferyxApp.controller('FooterController', ['$scope', '$rootScope', '$cookieStore', function ($scope, $rootScope, $cookieStore) {
    if (localStorage.userdetail) {
        $rootScope.productDetail = (JSON.parse(localStorage.userdetail).productDetail);
    }
}]);

InferyxApp.controller('ProfileImageCtrl', function ($timeout, $rootScope, $scope, $uibModalInstance, $location, $http) {

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
        var fd = new FormData();
        fd.append('file', f);
        fd.append('fileName', $rootScope.setUseruuid);

        var baseUrl = $location.absUrl().split("app")[0];
        var url = baseUrl + "metadata/uploadProfileImage";
        $http.post(url, fd,
            {
                transformRequest: angular.identity,
                headers: { 'Content-Type': undefined }
            }).success(function (d) {
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
InferyxApp.controller('SidebarController', ['$state', '$scope', 'cacheService', function ($state, $scope, cacheService) {
    $scope.$on('$includeContentLoaded', function () {
        Layout.initSidebar($state); // init sidebar
    });
    $scope.clearCache = function () {
        cacheService.searchCriteria = {};
    }
}]);



/* Setup Layout Part - Quick Sidebar */
InferyxApp.controller('QuickSidebarController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        setTimeout(function () {
            QuickSidebar.init(); // init quick sidebar
        }, 2000)
    });
}]);


/* Setup Layout Part - Theme Panel */
InferyxApp.controller('ThemePanelController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        Demo.init(); // init theme panel
    });
}]);


/* Setup Layout Part - Footer */
InferyxApp.controller('FooterController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        Layout.initFooter(); // init footer
    });
}]);


/* Setup Rounting For All Pages */
InferyxApp.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise("/");
    $stateProvider.state('/', {
        url: '',
        //templateUrl: 'index.html',
        //controller: function($scope,$rootScope, $stateParams) {}
    })
    $stateProvider.state('/home', {
        url: 'DataDiscovery',
        templateUrl: 'views/data-discovery-card-list.html',
        controller: function ($scope, $rootScope, $stateParams, $window) {
            //$window.location.href = 'index.html';
        },
        data: { pageTitle: 'Data Discovery' }, 
    })

    $stateProvider.state('home', {
        url: 'DataDiscovery',
        templateUrl: 'views/data-discovery-card-list.html',
        controller: function ($scope, $rootScope, $stateParams, $window) {
            //$window.location.href = 'index.html';
        },
        data: { pageTitle: 'Data Discovery' },
    })

        // metadata
        .state('metadata', {
            url: "/dataPreparation?type",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",

        })
        .state('maprestult', {
            url: "/MapResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Preparation' },
            params: { type: 'mapexec', isExec: true }
        })

        .state('maprestultpage', {
            url: "/MapResults?id&version&type&name",
            templateUrl: "views/map-result.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MapResult',
                        files: [
                            'js/controllers/MapResultController.js',
                            'js/services/Mapservice.js'
                        ]
                    }]);
                }]
            }
        })

        .state('datadiscovery', {
            url: "/DataDiscovery",
            templateUrl: "views/data-discovery-card-list.html",
            data: { pageTitle: 'Data Discovery' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Metadata Navigator' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Visualization' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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


        .state('showdashboard', {
            url: "/dashboard?id&returnBack",
            templateUrl: "views/dashboard-graph.html",
            data: { pageTitle: 'Data Visualization' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('vizpodlist', {
            url: "/vizpodList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Visualization' },
            params: { type: 'vizpod' }
        })

        .state('dvvizpod', {
            url: "/vizpod?id&mode&returnBack&version",
            templateUrl: "views/vizpod.html",
            data: { pageTitle: 'Data Visualization' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/VizpodController.js',
                            'js/services/VizpodService.js'
                        ]
                    });
                }]
            }
        })

        .state('reportlist', {
            url: "/reportList",
            templateUrl: "views/report-list.html",
            data: { pageTitle: 'Data Visualization' },
            //controller: "BlankController",
            params: { type: 'report' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ReportController.js',
                             'js/services/ReportService.js'
                        ]
                    });
                }]
            }
        })
       
        // .state('reportlist2', {
        //     url: "/reportList2",
        //     templateUrl: "views/report-list.html",
        //     data: { pageTitle: 'Data Visualization' },
        //     //controller: "BlankController",
        //     params: { type: 'report2' },
        //     resolve: {
        //         deps: ['$ocLazyLoad', function ($ocLazyLoad) {
        //             return $ocLazyLoad.load({
        //                 name: 'InferyxApp',
        //                 insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
        //                 files: [
        //                     'js/controllers/ReportController.js',
        //                      'js/services/ReportService.js'
        //                 ]
        //             });
        //         }]
        //     }
        // })

        .state('reportdetail', {
            url: "/Report?id&mode&returnBack&version",
            templateUrl: "views/report.html",
            data: { pageTitle: 'Data Visualization' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ReportController.js',
                            'js/services/ReportService.js'
                        ]
                    });
                }]
            }
        })
        // .state('reportdetail2', {
        //     url: "/Report2?id&mode&returnBack&version",
        //     templateUrl: "views/report2.html",
        //     data: { pageTitle: 'Data Visualization' },
        //     //controller: "BlankController",
        //     resolve: {
        //         deps: ['$ocLazyLoad', function ($ocLazyLoad) {
        //             return $ocLazyLoad.load({
        //                 name: 'InferyxApp',
        //                 insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
        //                 files: [
        //                     'js/controllers/ReportController.js',
        //                     'js/services/ReportService.js'
        //                 ]
        //             });
        //         }]
        //     }
        // })
        .state('reportexeclist', {
            url: "/Report/ResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: ' Data Visualization' },
            params: { type: 'reportexec', isExec: true }
        })

        
        .state('reportArchives', {
            url: "/ReportArchives",
            templateUrl: "views/report-archives.html",
            data: { pageTitle: 'Data Visualization' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ReportController.js',
                            'js/services/ReportService.js'
                        ]
                    });
                }]
            }
        })
        .state('resultxecresult', {
            url: "/ReportResult?id&mode&returnBack&version&appId&roleId&redirect",
            templateUrl: "views/report-result.html",
            data: { pageTitle: 'Data Visualization' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ReportController.js',
                            'js/services/ReportService.js'
                        ]
                    });
                }]
            }
        })
        // admin
        .state('admin', {
            url: "/Admin?type&returnBack",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Admin' },
        })

        .state('adminListprivilege', {
            url: "/Admin/privilege?id&mode&returnBack&version",
            templateUrl: "views/privilege.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/activity?id&mode&returnBack&version",
            templateUrl: "views/activity.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/role?id&mode&returnBack&version",
            templateUrl: "views/role.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/session?id&mode&returnBack&version",
            templateUrl: "views/session.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/group?id&mode&returnBack&version",
            templateUrl: "views/group.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/dataPreparation/datapod?id&mode&version&returnBack",
            templateUrl: "views/datapod.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Visualization' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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




        .state('metaListcondition', {
            url: "/dataPreparation/condition?id&mode&returnBack",
            templateUrl: "views/condition.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Business Rules' },
            params: { type: 'ruleexec', isExec: true ,isExec2:false}
        })
        .state('rule2restult', {
            url: "/Rule2ResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'ruleexec', isExec: true ,isExec2:true}
        })

        

        .state('rulerestultpage', {
            url: "/RuleResults?id&version&type&name",
            templateUrl: "views/rule-result.html",
            data: { pageTitle: 'Business Rules' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('rule2restultpage', {
            url: "/Rule2Results?id&version&type&name",
            templateUrl: "views/rule2-result.html",
            data: { pageTitle: 'Business Rules' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'RuleResult',
                        files: [
                            'js/controllers/Rule2Controller.js',
                            'js/services/Rule2Service.js'
                        ]
                    }]);
                }]
            }
        })
        
        .state('viewrule', {
            url: "/ListRule",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'rule' }
        })
        .state('viewrule2', {
            url: "/ListRule2",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'rule2' }
        })

        .state('createrules', {
            url: "/CreateRule?id&mode&returnBack&version",
            templateUrl: "views/rule.html",
            data: { pageTitle: 'Business Rules' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'formwizard',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'createRule',
                        files: [
                            'js/controllers/RuleController.js',
                            'js/services/RuleService.js'
                        ]
                    }
                    ]);
                }]
            }
        })

        .state('createrules2', {
            url: "/CreateRule2?id&mode&returnBack&version",
            templateUrl: "views/rule2.html",
            data: { pageTitle: 'Business Rules' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'formwizard',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'createRule2',
                        files: [
                            'js/controllers/Rule2Controller.js',
                            'js/services/Rule2Service.js'
                        ]
                    }
                    ]);
                }]
            }
        })

        //viewrulesgroup
        .state('rulesgroup', {
            url: "/ListRuleGroup",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'rulegroup' }
        })

        .state('createrulesgroup', {
            url: "/CreateRuleGroup?id&mode&returnBack&version",
            templateUrl: "views/rule-group.html",
            data: { pageTitle: 'Business Rules' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'createRuleGroup',
                        files: [
                            'js/controllers/RuleController.js',
                            'js/services/RuleGroupService.js'
                        ]
                    }
                    ]);
                }]
            }
        })


        .state('paramlistrule', {
            url: "/BusinessRules/ParamList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'paramlist', parantType: 'rule' }

        })
        .state('paramlistreport', {
            url: "/DataVisualization/ParamList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Visualization' },
            params: { type: 'paramlist', parantType: 'report' }

        })
        .state('paramlistdag', {
            url: "/Data Pipeline/ParamList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Pipeline' },
            params: { type: 'paramlist', parantType: 'dag' }

        })
        .state('createparamlistdag', {
            url: "/BusinessRules/CreateParamListDag?id&mode&returnBack&version",
            templateUrl: "views/paramlist.html",
            data: { pageTitle: 'Data Pipeline' },
            params: { type: 'paramlist', parantType: 'dag' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('createparamlistrule', {
            url: "/BusinessRules/CreateParamListRule?id&mode&returnBack&version",
            templateUrl: "views/paramlist.html",
            data: { pageTitle: 'Business Rules' },
            params: { type: 'paramlist', parantType: 'rule' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('createparamlistreport', {
            url: "/DataVisualization/CreateParamListRule?id&mode&returnBack&version",
            templateUrl: "views/paramlist.html",
            data: { pageTitle: 'Data Visualization' },
            params: { type: 'paramlist', parantType: 'report' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('rulecompare', {
            url: "/ruleCompare",
            templateUrl: "views/rule-compare.html",
            data: { pageTitle: 'Business Rules' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'rulecompare',
                        files: [
                            'js/controllers/RuleCompareController.js',
                            'js/services/RuleService.js'
                        ]
                    }]);
                }]
            }
        })
        .state('datareconrule', {
            url: "/ListDataReconcilationRule",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Reconciliation' },
            params: { type: 'recon' }
        })
        .state('createreconerule', {
            url: "/CreateDataRecon?id&mode&returnBack&version",
            templateUrl: "views/recon-rule.html",
            data: { pageTitle: 'Data Reconciliation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'formwizard',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'createReconRule',
                        files: [
                            'js/controllers/ReconRuleController.js',
                            'js/services/ReconRuleService.js'
                        ]
                    }
                    ]);
                }]
            }
        })
        .state('datareconrulegroup', {
            url: "/ListDataReconcilationRuleGroup",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Reconciliation' },
            params: { type: 'recongroup' }
        })
        .state('createreconerulegroup', {
            url: "/CreateDataReconGroup?id&mode&returnBack&version",
            templateUrl: "views/recon-rule-group.html",
            data: { pageTitle: 'Data Reconciliation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'formwizard',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'createReconRule',
                        files: [
                            'js/controllers/ReconRuleController.js',
                            'js/services/ReconRuleGroupService.js'
                        ]
                    }
                    ]);
                }]
            }
        })
        .state('datareconrulerestult', {
            url: "/ListDataReconcilationResult",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Reconciliation' },
            params: { type: 'reconexec', isExec: true }
        })

        .state('viewdrresultspage', {
            url: "/DataReconcilationResults?id&version&type&name",
            templateUrl: "views/recon-result.html",
            data: { pageTitle: 'Data Reconciliation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/ReconRuleController.js',
                            'js/services/ReconRuleService.js'

                        ]
                    }]);
                }]
            }
        })

        .state('reconcompare', {
            url: "/ReconCompare",
            templateUrl: "views/recon-compare.html",
            data: { pageTitle: 'Data Reconciliation' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'Reconcompare',
                        files: [
                            'js/controllers/ReconCompareController.js',
                            'js/services/ReconRuleService.js'
                        ]
                    }]);
                }]
            }
        })
        .state('viewdataquality', {
            url: "/ListDataQuality",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Quality' },
            params: { type: 'dq' }
        })

        .state('viewdataqualitygroup', {
            url: "/ListDataQualityGroup",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Quality' },
            params: { type: 'dqgroup' }
        })

        .state('createdataquality', {
            url: "/CreateDataQuality?id&mode&returnBack&version",
            templateUrl: "views/dataquality.html",
            data: { pageTitle: 'Data Quality' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/DataQualityController.js',
                            'js/services/DataQualityService.js'
                        ]
                    }
                    ]);
                }]
            }
        })

        .state('createdataqualitygroup', {
            url: "/CreateDataQualityGroup?id&mode&returnBack&version",
            templateUrl: "views/dataquality-group.html",
            data: { pageTitle: 'Data Quality' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Quality' },
            params: { type: 'dqexec', isExec: true, isExec2:false }
        })
        .state('viewdqresults2', {
            url: "/DataQualityResultList2",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Quality' },
            params: { type: 'dqexec', isExec: true , isExec2:true}
        })

        .state('viewdqresultspage', {
            url: "/DataQualityResults?id&version&type&name",
            templateUrl: "views/dataquality-result.html",
            data: { pageTitle: 'Data Quality' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('viewdqresultspage2', {
            url: "/DataQualityResults2?id&version&type&name",
            templateUrl: "views/dataquality-result2.html",
            data: { pageTitle: 'Data Quality' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Profiling' },
            params: { type: 'profile' }
        })

        .state('viewprofilegroup', {
            url: "/ListProfileGroup",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Profiling' },
            params: { type: 'profilegroup' }

        })


        .state('createprofile', {
            url: "/CreateProfile?id$mode$returnBack&version",
            templateUrl: "views/profile.html",
            data: { pageTitle: 'Data Profiling' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Profiling' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Profiling' },
            params: { type: 'profileexec', isExec: true }
        })

        .state('profilecompare', {
            url: "/profileCompare",
            templateUrl: "views/profile-compare.html",
            data: { pageTitle: 'Data Profiling' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'profilecompare',
                        files: [
                            'js/controllers/ProfileCompareController.js',
                            'js/services/ProfileService.js'
                        ]
                    }]);
                }]
            }
        })

        .state('dataqualitycompare', {
            url: "/DataQualityCompare",
            templateUrl: "views/dataquality-compare.html",
            data: { pageTitle: 'Data Quality' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'DataQualitycompare',
                        files: [
                            'js/controllers/DataQualityCompareController.js',
                            'js/services/DataQualityService.js'
                        ]
                    }]);
                }]
            }
        })

        .state('viewprofileresultspage', {
            url: "/ProfileResults?id&version&type&name",
            templateUrl: "views/profile-result.html",
            data: { pageTitle: 'Data Profiling' },
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/application?id&version&mode&returnBack",
            templateUrl: "views/application.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('createdomain', {
            url: "/Admin/Domain?id&version&mode&returnBack",
            templateUrl: "views/domain.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DomainController.js',
                            'js/services/DomainService.js'
                        ]
                    });
                }]
            }
        })



        .state('metaListgroup', {
            url: "/dataPreparation/group?id&mode&returnBack",
            templateUrl: "views/group.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/MetadataController.js', ,
                            'js/services/MetadataService.js'
                        ]
                    });
                }]
            }
        })

        .state('metaListformula', {
            url: "/dataPreparation/formula?id&mode&returnBack&version",
            templateUrl: "views/formula.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/services/MetadataService.js',
                            'js/controllers/FilterController.js',
                            'js/services/FilterService.js'
                        ]
                    });
                }]
            }
        })



        .state('metaListmeasure', {
            url: "/dataPreparation/measure?id&mode&returnBack",
            templateUrl: "views/measure.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/user?id&mode&returnBack&version",
            templateUrl: "views/user.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/RegisterSource",
            templateUrl: "views/register-source.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('filemanager', {
            url: "/Admin/FileManager",
            templateUrl: "views/file-manager.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Admin',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/FileMangegerController.js',
                            'js/services/FileManagerService.js'
                        ]
                    });
                }]
            }
        })


        .state('migrationassist', {
            url: "/Admin/MigrationAssist?type",
            templateUrl: "views/migration-assist.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/Settings?index",
            templateUrl: "views/settings.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/MigrationAssist/Export?id&mode&returnBack&version",
            templateUrl: "views/export.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/Admin/MigrationAssist/Import?id&mode&returnBack&version",
            templateUrl: "views/import.html",
            data: { pageTitle: 'Admin' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/dataPreparation/function?id&mode&returnBack&version",
            templateUrl: "views/function.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/dataPreparation/dag?id&mode&returnBack",
            templateUrl: "views/dag.html",
            data: { pageTitle: 'Data Preparation' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Meta Data',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DagController.js',
                            'js/services/DagService.js'
                        ]
                    });
                }]
            }
        })


        .state('createwf', {
            url: "/DataPipeline/dagflow?id&mode&action&tab&version",
            templateUrl: "views/data-pipeline.html",
            data: { pageTitle: 'Data Pipeline' },
            controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/services/DagService.js'
                        ]
                    }]);

                }]
            }
        })

        .state('listwf', {
            url: "/DataPipeline/List",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Pipeline' },
            params: { type: "dag" }
        })

        .state('resultwf', {
            url: "/DataPipeline/ResultList?dagid",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Pipeline' },
            params: { type: 'dagexec', isExec: true }
        })

        .state('resultgraphwf', {
            url: "/DataPipeline/Result?id&version&type&mode&dagid&returnBack",
            templateUrl: "views/data-pipeline-result.html",
            data: { pageTitle: 'Data Pipeline' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DataPipelineController.js',
                            'js/services/DagService.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })

        .state('batchlist', {
            url: "/Batch/List",
            templateUrl: "views/common-list.html",
            data: { pageTitle: ' Batch Scheduler' },
            params: { type: "batch" }
        })

        .state('batchexeclist', {
            url: "/Batch/ResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: ' Batch Scheduler' },
            params: { type: 'batchexec', isExec: true }
        })
       
        
      
        .state('batchdetail', {
            url: "/BatchDetail?id&mode&returnBack&version",
            templateUrl: "views/batch.html",
            data: { pageTitle: ' Batch Scheduler' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Batch',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/BatchController.js',
                            'js/services/BatchService.js'
                        ]
                    });
                }]
            }
        })

        
        .state('batchexecresult', {
            url: "/BatchResult?id&mode&returnBack&version&name",
            templateUrl: "views/batch-result.html",
            data: { pageTitle: ' Batch Scheduler' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Batch',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/BatchController.js',
                            'js/services/BatchService.js'
                        ]
                    });
                }]
            }
        })
        .state('algorithm', {
            url: "/Datascience/AlgorithmList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'algorithm' }
        })
        
        .state('createalgorithm', {
            url: "/CreateAlgorithm?id&mode&returnBack&version",
            templateUrl: "views/algorithm.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('distribution', {
            url: "/Datascience/DistributionList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'distribution' }
        })

        .state('createdistribution', {
            url: "/CreateDistribution?id&mode&returnBack&version",
            templateUrl: "views/distribution.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'Distribution',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DistributionController.js',
                            'js/services/DistributionService.js'
                        ]
                    });
                }]
            }
        })
        .state('model', {
            url: "/Datascience/ModelList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'model' }
        })

        .state('train', {
            url: "/Datascience/trainList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'train' }

        })

        .state('createtrain', {
            url: "/CreateTrain?id&mode&returnBack&version",
            templateUrl: "views/train.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/TrainController.js',
                            'js/services/TrainService.js'
                        ]
                    }
                    ]);

                }]
            }
        })

        .state('predict', {
            url: "/Datascience/predictList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'predict' }

        })

        .state('createpredict', {
            url: "/CreatePredict?id&mode&returnBack&version",
            templateUrl: "views/predict.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/PredictController.js',
                            'js/services/PredictService.js'
                        ]
                    }
                    ]);

                }]
            }
        })


        .state('simulate', {
            url: "/Datascience/simulateList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'simulate' }

        })

        .state('createsimulate', {
            url: "/CreateSimulate?id&mode&returnBack&version",
            templateUrl: "views/simulate.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            "assets/global/plugins/jquery-validation/js/jquery.validate.min.js",
                            "assets/global/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js",
                            "assets/pages/scripts/form-wizard.js"
                        ]
                    },
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/SimulateController.js',
                            'js/services/SimulateService.js'
                        ]
                    }
                    ]);

                }]
            }
        })   
        .state('modelDeploy', {
            url: "/ModelDeploy",
            templateUrl:"views/model-deploy.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/ModelDeployController.js',
                            'js/services/ModelDeployService.js'
                        ]
                    }
                    ]);

                }]
            }
        })   
        .state('vartifAnalysis', {
            url: "/WhatIfAnalysis",
            templateUrl:"views/vartif-analysis.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                    {
                        name: 'InferyxApp',
                        files: [
                            'js/controllers/VartifAnalysisController.js',
                            'js/services/VartifAnalysisService.js'
                        ]
                    }
                    ]);

                }]
            }
        })   

        

        .state('createmodel', {
            url: "/CreateModel?id&mode&returnBack&version",
            templateUrl: "views/model.html",
            data: { pageTitle: 'Data Science' },
            //controller: "GraphResourcesController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
                            'js/controllers/ModelController.js',
                            'js/services/ModelService.js'
                        ]
                    }
                    ]);
                }]
            }
        })


        .state('resultmodelmodel', {
            url: "/ModelResultList",
            //templateUrl:"views/model-result.html",
            templateUrl: "views/model-search-result.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            //'js/controllers/ModelController.js',
                            'js/controllers/ModelResultController.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })
        .state('modelrestultpage', {
            url: "/ModelResults?id&version&type&name",
            templateUrl: "views/model-result.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ModelResultController.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })
        .state('trainrestultpage', {
            url: "/trainResults?id&version&type&name",
            templateUrl: "views/train-result.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ModelResultController.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })
        .state('trainrestultpage2', {
            url: "/trainResults2?id&version&type&name",
            templateUrl: "views/train-result2.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'DataPod',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ModelResultController.js',
                            'js/services/ModelService.js'
                        ]
                    });
                }]
            }
        })

        .state('paramlistmodel', {
            url: "/DataScience/ParamList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'paramlist', parantType: 'model' }

        })

        .state('createparamlistmodel', {
            url: "/CreateParamList?id&mode&returnBack&version",
            templateUrl: "views/paramlist.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'paramlist', parantType: 'model' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            url: "/ParamSet",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'paramset' }

        })

        .state('createparamset', {
            url: "/CreateParamSet?id&mode&returnBack&version",
            templateUrl: "views/paramset.html",
            data: { pageTitle: 'Data Science' },
            controller: "",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            params: { isJobExec: true }
        })

        .state('jobmonitoringlistloadexec', {
            url: "/JobMonitoringList/load?id&mode&returnBack",
            templateUrl: "views/load-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('jobexecutorlistdownloadexec', {
            url: "/JobMonitoringList/download?id&mode&returnBack",
            templateUrl: "views/download-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DownloadExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('jobexecutorlistuploadexec', {
            url: "/JobMonitoringList/upload?id&mode&returnBack",
            templateUrl: "views/upload-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/UploadExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('jobexecutorlistdagexec', {
            url: "/JobMonitoringList/pipeline?id&mode&returnBack",
            templateUrl: "views/data-pipeline-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('jobexecutorlisttarinexec', {
            url: "/JobMonitoringList/train?id&mode&returnBack",
            templateUrl: "views/train-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/TrainExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistpredictexec', {
            url: "/JobMonitoringList/predict?id&mode&returnBack",
            templateUrl: "views/predict-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/PredictExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistsimulateexec', {
            url: "/JobMonitoringList/simulate?id&mode&returnBack",
            templateUrl: "views/simulate-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/SimulateExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistprofileexec', {
            url: "/JobMonitoringList/profile?id&mode&returnBack",
            templateUrl: "views/profile-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
        .state('jobexecutorlistreportexec', {
            url: "/JobMonitoringList/report?id&mode&returnBack",
            templateUrl: "views/report-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/ReportExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistdashboardexec', {
            url: "/JobMonitoringList/dashbard?id&mode&returnBack",
            templateUrl: "views/dashboard-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/DashboardExecControlle.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('jobexecutorlistprofilegroupexec', {
            url: "/JobMonitoringList/profilegroup?id&mode&returnBack",
            templateUrl: "views/profile-group-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('jobexecutorlistreconexec', {
            url: "/JobMonitoringList/recon?id&mode&returnBack",
            templateUrl: "views/recon-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                            'js/controllers/ReconExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('jobexecutorlistrecongroupexec', {
            url: "/JobMonitoringList/reconGroup?id&mode&returnBack",
            templateUrl: "views/recon-group-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                            'js/controllers/ReconGroupExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistoperatorexec', {
            url: "/JobMonitoringList/OperatorEXec?id&mode&returnBack",
            templateUrl: "views/operator-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                            'js/controllers/OperatorExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistgraphexec', {
            url: "/JobMonitoringList/GraphEXec?id&mode&returnBack",
            templateUrl: "views/graph-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [

                            'js/controllers/GraphExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistbatchexec', {
            url: "/JobMonitoringList/batch?id&mode&returnBack",
            templateUrl: "views/batch-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/BatchExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })
        .state('jobexecutorlistingestexec', {
            url: "/JobMonitoringList/ingest?id&mode&returnBack",
            templateUrl: "views/ingest-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/IngestExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('jobexecutorlistingestgroupexec', {
            url: "/JobMonitoringList/ingestGroup?id&mode&returnBack",
            templateUrl: "views/ingest-group-exec.html",
            data: { pageTitle: 'Job Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/IngestGroupExecController.js',
                            'js/services/JobMonitoringService.js',
                        ]
                    });
                }]
            }
        })

        .state('commonlistpage', {
            url: "/list?type",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'List Page' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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
            data: { pageTitle: 'System Monitoring' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
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

        .state('createappconfig', {
            url: "/AppConfig?id&mode&returnBack&version",
            templateUrl: "views/appconfig.html",
            data: { pageTitle: 'Admin' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/AppConfigController.js',
                            'js/services/AppConfigService.js',
                        ]
                    });
                }]
            }
        })

        .state('operatortype', {
            url: "/Datascience/OperatorTypeList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'operatortype' }
        })

        .state('createoperatortype', {
            url: "/Datascience/OperatorType?id&mode&returnBack&version",
            templateUrl: "views/operatorType.html",
            data: { pageTitle: 'Data Science' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/OperatorTypeController.js',
                            'js/services/OperatorTypeService.js',
                        ]
                    });
                }]
            }
        })
        .state('operator', {
            url: "/Datascience/OperatorList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Science' },
            params: { type: 'operator' }
        })

        .state('createoperator', {
            url: "/Datascience/Operator?id&mode&returnBack&version",
            templateUrl: "views/operator.html",
            data: { pageTitle: 'Data Science' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/OperatorController.js',
                            'js/services/OperatorService.js',
                        ]
                    });
                }]
            }
        })
        .state('listgraphpod', {
            url: "/GraphAnalysis/GraphpodList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Graph Analysis' },
            params: { type: 'graphpod'}
        })
        .state('graphpodresultlist', {
            url: "/GraphAnalysis/GraphpodResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Graph Analysis' },
            params: { type: 'graphexec', isExec: true }
        })
        
        .state('creaetgraphpod', {
            url: "/GraphAnalysis/Graphpod?id&mode&returnBack&version",
            templateUrl: "views/graphpod.html",
            data: { pageTitle: 'Graph Analysis' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/GraphpodController.js',
                            'js/services/GraphpodService.js',
                        ]
                    });
                }]
            }
        })
           
        .state('graphpodresult', {
            url: "/GraphAnalysis/GraphpodResult?id&version&type&name",
            templateUrl: "views/graphpod-result.html",
            data: { pageTitle: 'Graph Analysis' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/GraphpodController.js',
                            'js/services/GraphpodService.js',
                            'js/directives/FdGraphDirective.js'
                        ]
                    });
                }]
            }
        })
        .state('ingestrulelist', {
            url: "/DataIngestion/IngestList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Ingestion' },
            params: { type: 'ingest'}
        })
        .state('ingestrulelist2', {
            url: "/DataIngestion/IngestList2",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Ingestion' },
            params: { type: 'ingest2'}
        })
        .state('ingestrulegrouplist', {
            url: "/DataIngestion/IngestGroupList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Ingestion' },
            params: { type: 'ingestgroup'}
        })
        .state('ingestrulerestultlist', {
            url: "/DataIngestion/IngestResultList",
            templateUrl: "views/common-list.html",
            data: { pageTitle: 'Data Ingestion' },
            params: { type: 'ingestexec', isExec: true}
        })
        
        .state('ingestruledetail', {
            url: "/DataIngestion/IngestRuleDetail?id&mode&returnBack&version",
            templateUrl: "views/ingest-rule.html",
            data: { pageTitle: 'Data Ingestion' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/IngestController.js',
                            'js/services/IngestRuleService.js',
                        ]
                    });
                }]
            }
        })
        .state('ingestruledetail2', {
            url: "/DataIngestion/IngestRuleDetail2?id&mode&returnBack&version",
            templateUrl: "views/ingest-rule2.html",
            data: { pageTitle: 'Data Ingestion' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/IngestController2.js',
                            'js/services/IngestRuleService.js',
                        ]
                    });
                }]
            }
        })
        .state('ingestrulegroupdetail', {
            url: "/DataIngestion/IngestRuleGroupDetail?id&mode&returnBack&version",
            templateUrl: "views/ingest-rule-group.html",
            data: { pageTitle: 'Data Ingestion' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                             'js/controllers/IngestController.js',
                            'js/services/IngestRuleGroupService.js',
                        ]
                    });
                }]
            }
        })
        .state('ingestexecresult', {
            url: "/DataIngestion/IngestResult?id&version&type&name",
            templateUrl: "views/ingest-result.html",
            data: { pageTitle: 'Data Ingestion' },
            //controller: "BlankController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'InferyxApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/IngestController.js',
                            'js/services/IngestRuleService.js',
                            'js/services/ProfileService.js'
                        ]
                    });
                }]
            }
        })

        .state('organizationdetail',{
            url: "/Admin/OrganizationDetail?id&mode&version&type&name",
            templateUrl:"views/organization.html",
            data:{pageTitle:'Admin'},
            resolve:{
                depe:['$ocLazyLoad',function($ocLazyLoad){
                    return  $ocLazyLoad.load({
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            'js/controllers/OrganizationController.js',
                            'js/services/OrganizationService.js'
                        ]
                    });
            
               }]
           }
        })
        
        
}]);


InferyxApp.factory('privilegeSvc', function ($http, $location, $rootScope) {
    var baseUrl = $location.absUrl().split("app")[0];
    var url = baseUrl + 'security/getRolePriv',
        obj = {};
    $rootScope.privileges = obj.privileges = {};
    covertPrivileges = function (arr) {
        $rootScope.privileges = obj.privileges = {};
        angular.forEach(arr, function (val, key) {
            obj.privileges[val.type] = val.privInfo;
        });
        $rootScope.$broadcast('privilegesUpdated');
    }
    obj.get = function () {
        if (obj.privileges == {}) {
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
    return obj
})

/* Init global settings and run the app */
InferyxApp.run(["$rootScope", "settings", "$state", "privilegeSvc", "$timeout", function ($rootScope, settings, $state, privilegeSvc,  $timeout) {
    $rootScope.$state = $state; // state to be accessed from view
    $rootScope.$settings = settings; // state to be accessed from view
    privilegeSvc.getUpdated();
}]);