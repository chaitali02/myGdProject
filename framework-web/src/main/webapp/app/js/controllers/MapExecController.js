  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailMapExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {

    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showmapexec = true;
    $scope.state = dagMetaDataService.elementDefs['mapexec'].listState + "({type:'" + dagMetaDataService.elementDefs['mapexec'].execType + "'})"
    $rootScope.isCommentVeiwPrivlage=true;
    var privileges = privilegeSvc.privileges['comment'] || [];
	  $rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
	  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
	  $scope.$on('privilegesUpdated', function (e, data) {
		  var privileges = privilegeSvc.privileges['comment'] || [];
		  $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;	
    });
    $scope.userDetail={}
	  $scope.userDetail.uuid= $rootScope.setUseruuid;
	  $scope.userDetail.name= $rootScope.setUserName;
    $scope.close = function() {
      if ($stateParams.returnBack == "true" && $rootScope.previousState) {
        //revertback
        $state.go($rootScope.previousState.name, $rootScope.previousState.params);
      } else {
        $scope.statedetail = {};
        $scope.statedetail.name = dagMetaDataService.elementDefs['mapexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['mapexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }

    JobMonitoringService.getLatestByUuid($scope.uuid, "mapexec").then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      $scope.mapexecdata = response;
      var statusList = [];
      for (i = 0; i < response.statusList.length; i++) {
        d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
        d = d.toString().replace("+0530", "IST");
        statusList[i] = response.statusList[i].stage + "-" + d;
      }

      $scope.statusList = statusList
      var refkeylist = [];
      if (response.refKeyList.length > 0) {
        for (j = 0; j < response.refKeyList.length; j++) {
          refkeylist[j] = response.refKeyList[j].type + "-" + response.refKeyList[j].name;

        }
        $scope.refkeylist = refkeylist
        $scope.refkeylistclass = "cross"
      }
    }


    $scope.showLoadGraph = function(uuid, version) {
      $scope.showmapexec = false;
      $scope.showgraph = false
      $scope.graphDatastatusList = true
      $scope.showgraphdiv = true;
      var newUuid = uuid + "_" + version;
      // MapExecService.getGraphData(newUuid, version, "1")
      //   .then(function(result) {
      //     $scope.graphDatastatusList = false;
      //     $scope.showgraph = true;
      //     console.log(JSON.stringify(result.data))
      //     $scope.data = result.data;
      //     $scope.graphdata = result.data;
      //
      //   });


    }
    $scope.showMapExecPage = function() {
      $scope.showmapexec = true
      $scope.showgraph = false
      $scope.graphDatastatusList = false
      $scope.showgraphdiv = false;
    }

    // $scope.searchObjet = function(JSONObject, key, value) {
    //   for (var i = 0; i < JSONObject.length; i++) {
    //     if (JSONObject[i][key] == value) {
    //       return true;
    //     }
    //   }
    //   return false;
    // }
    //
    // $scope.searcLinkObjet = function(JSONObject, valuedst, valuesrc) {
    //   for (var i = 0; i < JSONObject.length; i++) {
    //     if (JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc) {
    //       return true;
    //     }
    //   }
    //   return false;
    // }
    //
    // /* Start getNodeData*/
    // $scope.getNodeData = function(data) {
    //   $scope.graphDatastatusList = true;
    //   $scope.showgraph = false;
    //   MapExecService.getGraphData(data.uuid, data.version, "1").then(function(response) {
    //     onSuccess(response.data)
    //   });
    //   var onSuccess = function(response) {
    //     $scope.graphDatastatusList = false;
    //     $scope.showgraph = true;
    //     console.log(JSON.stringify(response))
    //     var nodelen = $scope.graphdata.nodes.length;
    //     var linklen = $scope.graphdata.links.length;
    //     for (var j = 0; j < $scope.graphdata.nodes.length; j++) {
    //       delete $scope.graphdata.nodes[j].weight
    //       delete $scope.graphdata.nodes[j].index
    //       delete $scope.graphdata.nodes[j].x
    //       delete $scope.graphdata.nodes[j].y
    //       delete $scope.graphdata.nodes[j].px
    //       delete $scope.graphdata.nodes[j].py
    //     }
    //     var countnode = 0;
    //     var countlink = 0;
    //     for (var i = 0; i < response.nodes.length; i++) {
    //       if ($scope.searchObjet($scope.graphdata.nodes, "id", response.nodes[i].id) != true) {
    //         $scope.graphdata.nodes[nodelen + countnode] = response.nodes[i];
    //         countnode = countnode + 1;
    //       }
    //     }
    //     for (var j = 0; j < response.links.length; j++) {
    //       if ($scope.searchObjet($scope.graphdata.links, response.links[j].dst, response.links[j].src) != true) {
    //         $scope.graphdata.links[linklen + countlink] = response.links[j];
    //         countlink = countlink + 1;
    //       }
    //     }
    //     console.log(JSON.stringify($scope.graphdata))
    //     $scope.data = $scope.graphdata
    //     /* $scope.$apply(function(){}); */
    //   }
    // } /* End getNodeData*/


  });
