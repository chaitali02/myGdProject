  /****/
  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('ShowCardJobMonitoringController', function($stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, dagMetaDataService) {
    $scope.optionsort = [{
        "caption": "Name A-Z",
        name: "caption"
      },
      {
        "caption": "Name Z-A",
        name: "-caption"
      },
      {
        "caption": "Date Asc",
        name: "lastUpdatedOn"
      },
      {
        "caption": "Date Desc",
        name: "-lastUpdatedOn"
      },
    ]
    $scope.optiondata = {
      "caption": "Name A-Z",
      name: "caption"
    };
    $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
      console.log(fromParams)
      $sessionStorage.fromStateName = fromState.name
      $sessionStorage.fromParams = fromParams

    });


    JobMonitoringService.getAllExecStats().then(function(response) {onSuccess(response.data)});
    var onSuccess = function(response) {
      var colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"]
      var metaarray = []
      for (var i = 0; i < response.length; i++) {
        var metajson = {};
        var type = response[i].type.toLowerCase(0);
        metajson.type = response[i].type;
        metajson.count = response[i].count;
        metajson.lastUpdatedBy = response[i].lastUpdatedBy;
        metajson.lastUpdatedOn = new Date(response[i].lastUpdatedOn.split("IST")[0]);
        var randomno = Math.floor((Math.random() * 4) + 0);
        metajson.class = colorclassarray[randomno];
        switch (response[i].type) {
          case "loadExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption //"load";
            metajson.icon = "fa fa-link"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "mapExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-link"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "dqgroupExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-rss"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;

          case "ruleExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-cogs";
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "rulegroupExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-cogs"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;

          case "profileExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-users"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;

          case "profilegroupExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-users"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "dagExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-random"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "vizExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-line-chart"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "dqExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-rss"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
          case "modelExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-flask"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
            case "downloadExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-download"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
            case "uploadExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-upload"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'loadExec'})";
            break;
            case "predictExec":
            metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-flask"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'predictExec'})";
            break;
            case "simulateExec":
          	metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-flask"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'simulateExec'})";
            break;
            case "trainExec":
        	  metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-flask"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'trainExec'})";
            break;
            case "reconExec":
        	  metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-compress"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'trainExec'})";
            break;
            case "recongroupExec":
        	  metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-compress"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'trainExec'})";
            break;
            case "operatorExec":
        	  metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-flask"
            metajson.state = dagMetaDataService.elementDefs[type].listState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'trainExec'})";
            break;
            case "graphExec":
        	  metajson.caption = dagMetaDataService.elementDefs[type].caption;
            metajson.icon = "fa fa-bar"
            metajson.state = dagMetaDataService.elementDefs[type].joblistState + "({type:'" + dagMetaDataService.elementDefs[type].execType + "'})"; //"jobexecutorlist({type:'trainExec'})";
            break;
            
        }
        metaarray[i] = metajson
      }//End For Loop
      $scope.allMetaCount = metaarray;
    }//End getAllExecStats();
  });
