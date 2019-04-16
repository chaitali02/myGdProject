
angular.module('InferyxApp')
  .controller('GraphResourcesController', ['$rootScope', 'privilegeSvc', '$scope', 'MetadataDagSerivce', '$stateParams', '$state', 'graphService', 'dagValidationSvc', 'dagMetaDataService', '$http', '$filter', 'uibButtonConfig', 'CommonService', '$timeout',
    function ($rootScope, privilegeSvc, $scope, MetadataDagSerivce, $stateParams, $state, graphService, dagValidationSvc, dagMetaDataService, $http, $filter, buttonConfig, CommonService, $timeout) {

      $scope.isTemplate = true;
      $scope.isUseTemplate = false;
      $scope.editMode = false;
      $scope.addMode = false;
      $scope.editMode = $stateParams.mode == 'true' ? false : true;
      $scope.createMode = $stateParams.action == 'add';
      $scope.selectedDagTemplate = null;
      $scope.isDependencyShow = false;
      $scope.isDestoryState = false; 
      var notify = {
        type: 'success',
        title: 'Success',
        content: 'Dashboard deleted Successfully',
        timeout: 3000 //time in ms
      };
      $scope.toggleZoom = function () {
        $scope.showZoom = !$scope.showZoom;
      }

      $scope.zoomSize = 7;
      $scope.$watch('zoomSize', function () {
        try {
          $scope.$broadcast('zoomChange', $scope.zoomSize);
        } catch (e) {

        } finally {
        }
      });

      $scope.$on('refreshData', function (e, data) {
        if ($scope.showdag) {
          $scope.$broadcast('createGraph', $scope.dagdata);
        }
      });

      $scope.isversionEnable = true;
      $scope.showdag = true;
      $scope.showgraphdiv = false
      $scope.dag = {};
      $scope.dagtable = null;
      $scope.dag.versions = [];
      $scope.mode = ""
      $scope.dagHasChanged = true;
      $scope.isshowmodel = false;
      $scope.isSubmitEnable = true;
      $scope.userDetail = {}
      $scope.userDetail.uuid = $rootScope.setUseruuid;
      $scope.userDetail.name = $rootScope.setUserName;
      if ($stateParams.mode == 'true') {
        $scope.isEdit = false;
        $scope.isversionEnable = false;
        $scope.isAdd = false;
        var privileges = privilegeSvc.privileges['comment'] || [];
        $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
        $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
        $scope.$on('privilegesUpdated', function (e, data) {
          var privileges = privilegeSvc.privileges['comment'] || [];
          $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
          $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

        });
      }
      else if ($stateParams.mode == 'false') {
        $scope.isEdit = true;
        $scope.isversionEnable = true;
        $scope.isAdd = false;
        $scope.isPanelActiveOpen = true;
        var privileges = privilegeSvc.privileges['comment'] || [];
        $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
        $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
        $scope.$on('privilegesUpdated', function (e, data) {
          var privileges = privilegeSvc.privileges['comment'] || [];
          $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
          $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

        });
      }
      else {
        $scope.isAdd = true;
      }
      $scope.privileges = [];
      $scope.privileges = privilegeSvc.privileges['dag'] || [];
      $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
      $scope.$on('privilegesUpdated', function (e, data) {
        $scope.privileges = privilegeSvc.privileges['dag'] || [];
        $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
      });
      $scope.getLovByType = function () {
        CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
        var onSuccessGetLovByType = function (response) {
          $scope.lobTag = response[0].value
        }
      }

      $scope.loadTag = function (query) {
        return $timeout(function () {
          return $filter('filter')($scope.lobTag, query);
        });
      };

      $scope.getLovByType();
        
      $scope.onChangeName = function (data) {
        $scope.dagdata.displayName=data;
      }

      $scope.$on('$destroy', function () {
        $scope.isDestoryState = true;
      }); 
      
      $scope.checkIsInrogess=function(){
        if($scope.isEditInprogess || $scope.isEditVeiwError){
        return false;
        }
      }

      $scope.showDagPage = function () {
        if($scope.checkIsInrogess () ==false){
          return false;
        }
        $scope.showdag = true;
        $scope.showgraphdiv = false;
        $scope.showdagflow = false;
      }/*End showDatapodPage*/
      
      $scope.showHome=function(uuid, version,mode){
        if($scope.checkIsInrogess () ==false){
          return false;
        }
        $scope.showDagPage();
        $state.go('createwf', {
          id: uuid,
          version: version,
          mode: mode
        });
      }
      $scope.enableEdit = function (uuid, version) {
        if($scope.isPrivlage || $scope.dagdata.locked =="Y"){
          return false;
        } 
        if($scope.checkIsInrogess () ==false){
          return false;
        } 
        $scope.showDagPage()
        $state.go('createwf', {
          id: uuid,
          version: version,
          mode: 'false'
        });
      }

      $scope.showview = function (uuid, version) {
        if($scope.checkIsInrogess () ==false){
          return false;
        }
        if (!$scope.isEdit) {
          $scope.showDagPage()
          $state.go('createwf', {
            id: uuid,
            version: version,
            mode: 'true'
          });
        }
      }

      $scope.$watch("isshowmodel", function (newvalue, oldvalue) {
        $scope.isshowmodel = newvalue
        $scope.operatorTypes = [
          { "text": "map", "caption": "map" },
          { "text": "dq", "caption": "dq" },
          { "text": "dqgroup", "caption": "dqgroup" },
          { "text": "load", "caption": "load" }
        ];
        sessionStorage.isshowmodel = newvalue;
      });

      $scope.dagFormChange = function () {
        if ($scope.mode == "true") {
          $scope.dagHasChanged = true;
        }
        else {
          $scope.dagHasChanged = false;
        }
      }

      $scope.showDagGraph = function (uuid, version) {
        if($scope.checkIsInrogess () ==false){
          return false;
        }
        $scope.showdag = false;
        $scope.showgraphdiv = true;
        $scope.showdagflow = false;
        $scope.graphDataStatus = true;
        $scope.showgraph = false;
      }/*End ShowDatapodGraph*/

      $scope.showDagWorkFlow = function () {
        $scope.showdag = true;
        $scope.showgraphdiv = false;
        $scope.showdagflow = true;
      }
      
      $scope.getAllLatestParamListByTemplate=function(){
        CommonService.getAllLatestParamListByTemplate('Y', "paramlist","dag").then(function (response){onSuccessGetAllLatestParamListByTemplate(response.data)});
        var onSuccessGetAllLatestParamListByTemplate = function (response) {
          $scope.allparamlist={};
          $scope.allparamlist.options = response
          if ($scope.dagdata.paramList != null) {
            var defaultoption = {};
            defaultoption.uuid = $scope.dagdata.paramList.ref.uuid;
            defaultoption.name = $scope.dagdata.paramList.ref.name;
            $scope.allparamlist.defaultoption = defaultoption;
          } else {
            $scope.allparamlist.defaultoption = null;
          }
        }
      }
      if (typeof $stateParams.id != "undefined") {
        $scope.mode = $stateParams.mode;
        $scope.isDependencyShow = true;
        $scope.isEditInprogess=true;
        $scope.isEditVeiwError=false;
        MetadataDagSerivce.getAllVersionByUuid($stateParams.id, 'dag').then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
        var onSuccessGetAllVersionByUuid = function (response) {
          for (var i = 0; i < response.length; i++) {
            var dagversion = {};
            dagversion.version = response[i].version;
            $scope.dag.versions[i] = dagversion;
          }
        }//End getAllVersionByUuid

        MetadataDagSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "dag")
          .then(function (response) { onSuccessGetLatestByUuid(response.data)},function(response) {onError(response.data)});
        var onSuccessGetLatestByUuid = function (response) {
          $scope.isEditInprogess=false;
          var defaultversion = {};
          $scope.dagdata = response.dagdata;
          $scope.pipelineName = response.dagdata.name;
          defaultversion.version = response.dagdata.version;
          defaultversion.uuid = response.dagdata.uuid;
          $scope.uuid = response.dagdata.uuid;
          $scope.version = response.dagdata.version;
          $scope.dag.defaultVersion = defaultversion;
          $scope.tags = response.dagdata.tags;
          $scope.dagtable = response.stage;
          $scope.allparamlist=null;
          $scope.getAllLatestParamListByTemplate();

          if (response.dagdata.templateInfo != null) {

            $scope.allDagTemplate = [];
            $scope.isUseTemplate = true;
            MetadataDagSerivce.getDagTemplates('dag').then(function (response) { onSuccessGetDagTemplates(response.data) });
            var onSuccessGetDagTemplates = function (response) {
              console.log(response)
              for (var i = 0; i < response.length; i++) {
                var dagtemplate = {};
                dagtemplate.version = response[i].version;
                dagtemplate.uuid = response[i].uuid;
                dagtemplate.name = response[i].name;
                $scope.allDagTemplate[i] = dagtemplate
              }
            }//End getDagTemplates
            $scope.selectedDagTemplate = {};
            $scope.selectedDagTemplate.uuid = response.dagdata.templateInfo.ref.uuid;
            $scope.selectedDagTemplate.version = response.dagdata.templateInfo.ref.version;
            $scope.selectedDagTemplate.name = response.dagdata.templateInfo.ref.name;
          }
          setTimeout(function () {
            if ($stateParams.tab) {
              $scope.continueCount = $stateParams.tab - 1;
              $('.button-next').click();
            }
          }, 500);
        } //End getLatestByUuid
        var onError =function(){
          $scope.isEditInprogess=false;
          $scope.isEditVeiwError=true;
        } 
      }//End If
      else {
        //$scope.mode="false";
        $scope.dagdata={};
        $scope.dagdata.locked="N";
        $scope.addMode = true;
        $scope.allDagTemplate = [];
        $scope.allparamlist=null;
        $scope.getAllLatestParamListByTemplate();
        MetadataDagSerivce.getDagTemplates('dag').then(function (response) { onSuccessGetDagTemplates(response.data) });
        var onSuccessGetDagTemplates = function (response) {
          console.log(response)
          for (var i = 0; i < response.length; i++) {
            var dagtemplate = {};
            dagtemplate.version = response[i].version;
            dagtemplate.uuid = response[i].uuid;
            dagtemplate.name = response[i].name;
            $scope.allDagTemplate[i] = dagtemplate
          }
        }//End getDagTemplates
      }


      /* Start selectVersion*/
      $scope.selectVersion = function (uuid, version) {
        $scope.isEditInprogess=true;
        $scope.isEditVeiwError=false;
        MetadataDagSerivce.getOneByUuidAndVersion(uuid, version, "dag")
          .then(function (response) { onSuccessGetOneByUuidAndVersion(response.data)},function(response) {onError(response.data)});
        var onSuccessGetOneByUuidAndVersion = function (response) {
          $scope.isEditInprogess=false;
          $scope.isGraphRenderEdit = false;
          var defaultversion = {};
          $scope.dagdata = response.dagdata;
          $scope.pipelineName = response.dagdata.name;
          defaultversion.version = response.dagdata.version;
          defaultversion.uuid = response.dagdata.uuid;
          $scope.uuid = response.dagdata.uuid;
          $scope.version = response.dagdata.version;
          $scope.dag.defaultVersion = defaultversion;
          $scope.tags = response.dagdata.tags;
          $scope.allparamlist=null;
          $scope.getAllLatestParamListByTemplate();
        } //End getLatestByUuid
        var onError =function(){
          $scope.isEditInprogess=false;
          $scope.isEditVeiwError=true;
        } 
      }//End SelectVersin


      $scope.okdagsave = function () {
        $('#dagsave').css("dispaly", "none");
        var hidemode = "yes";
        if (hidemode == 'yes') {
          setTimeout(function () { $state.go('metadata', { 'type': 'dag' }); }, 2000);
        }
      }

     

      $scope.saveDagJsonData = function () {
        var upd_tag = "N"
        $scope.isshowmodel = true;
        $scope.dataLoading = true;
        $scope.iSSubmitEnable = false;
        $scope.dagHasChanged = true;
        var options = {}
        options.execution = $scope.checkboxModelexecution;
        var cells = window.getAllCell();
        var inArrayFormat = graphService.convertGraphToDag(cells,true);
        var dagJson = {};
        dagJson.uuid = $scope.dagdata.uuid;
        dagJson.name = $scope.pipelineName;
        dagJson.displayName = $scope.dagdata.displayName;

        var tagArray = [];
        if ($scope.tags != null) {
          for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
            tagArray[counttag] = $scope.tags[counttag].text;
          }
          var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
          if (result == false) {
            upd_tag = "Y"
          }
        }
        dagJson.tags = tagArray
        dagJson.active = $scope.dagdata.active;
        dagJson.locked = $scope.dagdata.locked;
        dagJson.published = $scope.dagdata.published;
        dagJson.publicFlag = $scope.dagdata.publicFlag;

        dagJson.desc = $scope.dagdata.desc;
        dagJson.stages = inArrayFormat[0].stages;
        dagJson.xPos = inArrayFormat[0].xPos;
        dagJson.yPos = inArrayFormat[0].yPos;
        dagJson.templateFlg = $scope.dagdata.templateFlg
        if ($scope.isUseTemplate == true && $scope.selectedDagTemplate != null) {

          var templateInfo = {};
          var ref = {}
          ref.type = "dag";
          ref.uuid = $scope.selectedDagTemplate.uuid;
          templateInfo.ref = ref;
          dagJson.templateInfo = templateInfo;
        }
        else {
          dagJson.templateInfo = null;
        }
        if ($scope.allparamlist && $scope.allparamlist.defaultoption != null) {
          var paramlist = {};
          var ref = {};
          ref.type = "paramlist";
          ref.uuid = $scope.allparamlist.defaultoption.uuid;
          paramlist.ref = ref;
          dagJson.paramList = paramlist;
        } else {
          dagJson.paramList = null;
        }
        console.log("JSON", dagJson);
        MetadataDagSerivce.submit(dagJson, "dag", upd_tag).then(function (response) { onSuccessSubmit(response.data) }, function (response) { onError(response.data) });
        var onSuccessSubmit = function (response) {
          $scope.dataLoading = false;
          if (options.execution == "YES") {
            MetadataDagSerivce.getOneById(response, "dag").then(function (response) { onSuccessGetOneById(response.data) });
            var onSuccessGetOneById = function (result) {
              MetadataDagSerivce.excutionDag(result.uuid, result.version).then(function (response) { onSuccess(response.data) });
              var onSuccess = function (response) {
                console.log(JSON.stringify(response))
                $scope.saveMessage = "Pipeline Saved and Submitted Successfully"
                notify.type = 'success',
                notify.title = 'Success',
                notify.content = $scope.saveMessage
                $scope.$emit('notify', notify);
                $scope.okWorkflowsave();
              }
            }
          }//End If
          else {
            $scope.saveMessage = "Pipeline Saved Successfully"
            notify.type = 'success',
            notify.title = 'Success',
            notify.content = $scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okWorkflowsave();
          }//End else
        }//EndSubmit Api
        var onError = function (response) {
          notify.type = 'error',
          notify.title = 'Error',
          notify.content = "Some Error Occurred"
          $scope.$emit('notify', notify);
        }
      }

      //jitu new code
      $scope.okWorkflowsave = function () {
        $('.modal-backdrop').hide();
        if ($scope.isDestoryState==false) {
          setTimeout(function () { $state.go('listwf'); }, 2000);
        }
      }

      $scope.showRule = true;
      $scope.showdagForm = true;
      $scope.continueCount = 1;
      $scope.backCount;
      $scope.showRuleGraph = function (uuid, version) {
        $scope.showRule = false;
        $scope.showRuleForm = false
        $scope.graphDataStatus = true
        $scope.showgraphdiv = true;
      }

      $scope.showRulePage = function () {
        $scope.showgraph = false;
        $scope.showRule = true;
        $scope.showRuleForm = true
        $scope.graphDataStatus = false;
        $scope.showgraphdiv = false
      }



      $scope.countContinue = function () {
        $scope.continueCount = $scope.continueCount + 1;
        if ($scope.continueCount == 2 && $scope.isGraphRenderEdit != true) {
          setTimeout(function () {
            $scope.isGraphRenderEdit = true;
            $scope.dagdata.name = $scope.pipelineName;
            $scope.$broadcast('createGraph', $scope.dagdata);
          }, 0);
        }
        if ($scope.isGraphRenderEdit == true) {
        //  console.log($scope.graph.getCells());
          //$scope.graph.addCells($scope.graph.getCells())
        }
        if ($scope.continueCount >= 3) {
          $scope.isSubmitShow = true;
        } else {
          $scope.isSubmitShow = false;
        }

      }

      $scope.countBack = function () {
        if ($scope.graph) {
          var cells = $scope.graph.getCells();
          var inArrayFormat = graphService.convertGraphToDag(cells,false);
          $scope.dagdata.stages = inArrayFormat[0].stages;
          $scope.dagdata.xPos = inArrayFormat[0].xPos;
          $scope.dagdata.yPos = inArrayFormat[0].yPos;
          $scope.graphReady = true;
        } else {
          setTimeout(function () {
            console.log('scope dagResp', $scope.dagdata);
            $scope.$broadcast('createGraph', $scope.dagdata);
          }, 0);
        }
        $scope.continueCount = $scope.continueCount - 1;
        $scope.isSubmitShow = false;
      }

      $scope.selectTemplateDag = function () {
        console.log($scope.selectedDagTemplate)
        if ($scope.selectedDagTemplate != null) {
          $scope.isUseTemplate = true;
          MetadataDagSerivce.getOneByUuidAndVersion($scope.selectedDagTemplate.uuid, $scope.selectedDagTemplate.version, 'dag').then(function (response) { onSuccess(response.data) });
          var onSuccess = function (response) {
            $scope.dagdata = response.dagdata;
            $scope.dagdata.uuid = "";
            if (typeof $scope.pipelineName == "undefined") {
              $scope.pipelineName = response.dagdata.name;
            }
            $scope.isGraphRenderEdit = false;
            $scope.dagdata.name = "";
            $scope.dagdata.desc = '';
            $scope.dagdata.version = '';
            $scope.dagdata.tag = [];
            $scope.dagdata.templateFlg = 'N';
            $scope.dagdata.action = 'Y';
          }
        } else {
          $scope.isUseTemplate = false;
          $scope.dagdata.stages = null;
        }

      }
      //  buttonConfig.toggleEvent=$scope.OnChangeTemplate();
      // $scope.sbumitDag = function () {
      //   $scope.isshowmodel = true;
      //   $scope.dataLoading = true;
      //   $scope.iSSubmitEnable = false;
      //   $scope.dagHasChanged = true;
      //   var dagJson = {};
      //   dagJson.uuid = $scope.dagdata.uuid
      //   dagJson.name = $scope.dagdata.name
      //   var tagArray = [];
      //   if ($scope.tags != null) {
      //     for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
      //       tagArray[counttag] = $scope.tags[counttag].text;
      //     }
      //   }
      //   dagJson.tags = tagArray
      //   dagJson.active = $scope.dagdata.active;
      //   console.log(JSON.stringify(dagJson));
      // }

      $scope.fullscreen = false;
      $scope.requestFullscreen = function () {
        if ($scope.fullscreen) {
          $("#form_wizard_1").removeAttr('style');
        }
        else {
          $("#form_wizard_1").css(
            {
              "z-index": "999",
              "width": "100%",
              "height": "100%",
              "position": "fixed",
              "overflow": "scroll",
              "top": "0",
              "left": "0",
            }
          );
        }
        $scope.fullscreen = !$scope.fullscreen;
      };

    }//End Controller
  ]);
