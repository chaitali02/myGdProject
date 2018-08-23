BatchModule = angular.module('BatchModule');

BatchModule.controller('DetailBatchController', function($state, $timeout, $filter, $stateParams, $rootScope, $scope, BatchService,privilegeSvc,dagMetaDataService,CommonService,CF_META_TYPES) {
  
  $scope.select = 'batch';
  if($stateParams.mode =='true'){
	  $scope.isEdit=false;
	  $scope.isversionEnable=false;
    $scope.isAdd=false;
    var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if($stateParams.mode =='false'){
	  $scope.isEdit=true;
	  $scope.isversionEnable=true;
    $scope.isAdd=false;
    $scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else{
	$scope.isAdd=true;
	}
  $scope.showForm = true;
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.mode = " ";
  $scope.batch = {};
  $scope.batch.versions = []
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges[CF_META_TYPES.batch] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges[CF_META_TYPES.batch] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag=response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
    $scope.getLovByType();
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs[CF_META_TYPES.batch].listState
      $scope.statedetail.params = {}
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false;
  
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].detailState, {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go(dagMetaDataService.elementDefs['batch'].detailState, {
        id: uuid,
        version: version,
        mode:'true'
      });
   }
  }
 
  $scope.getAllLatest=function(type){
    BatchService.getAllLatest(type).then(function(response) {onSuccess(response.data)});
    var onSuccess = function(response) {
      var metaArray = [];
      for (var i = 0; i < response.data.length; i++) {
        var metajson = {};
        metajson.uuid = response.data[i].uuid;
        metajson.type =type;
        metajson.id = response.data[i].uuid ;
        metajson.name = response.data[i].name;
        metajson.version = response.data[i].version;
        metaArray[i] = metajson;
      }
      $scope.allMeta = metaArray;
    }
  }
 
  $scope.getAllLatest("dag")

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    BatchService.getAllVersionByUuid($stateParams.id, CF_META_TYPES.batch).then(function(response) {onGetAllVersionByUuid(response.data)});
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var BatchVersion = {};
        BatchVersion.version = response[i].version;
        $scope.batch.versions[i] = BatchVersion;
      }
    }
    BatchService.getOneByUuidAndVersion($stateParams.id,$stateParams.version, CF_META_TYPES.batch).then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.batchDetail = response;
      $scope.tags = response.tags
      $scope.checkboxModelparallel = response.inParallel;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagArray = [];
      for (var i = 0; i < response.metaList.length; i++) {
        var metaTags = {};
        metaTags.uuid = response.metaList[i].ref.uuid;
        metaTags.type = response.metaList[i].ref.type;
        metaTags.name = response.metaList[i].ref.name;
        metaTags.id = response.metaList[i].ref.uuid;
        metaTags.version = response.metaList[i].ref.version;
        metaTagArray[i] = metaTags;
      }
      $scope.metaTags = metaTagArray
    }
  }

  $scope.selectVersion = function() {
    $scope.myform.$dirty = false;
    BatchService.getOneByUuidAndVersion($scope.batch.defaultVersion.uuid, $scope.batch.defaultVersion.version, CF_META_TYPES.batch).then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.batchDetail = response;
      $scope.tags = response.tags
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagsArray = [];
      for (var i = 0; i < response.metaList.length; i++) {
        var metaTags = {};
        metaTags.uuid = response.metaList[i].ref.uuid;
        metaTags.type = response.metaList[i].ref.type;
        metaTags.name = response.metaList[i].ref.name;
        metaTags.id = response.metaList[i].ref.uuid;
        metaTags.version = response.metaList[i].ref.version;
        metaTagsArray[i] = metaTags;
      }
      $scope.metaTags = metaTagsArray
    }
  }

  $scope.loadMeta = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allMeta, query);
    });
  };

  $scope.oksave = function() {
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].listState);
      }, 2000);
    }
  }

  $scope.submit = function() {
    var upd_tag="N"
    $scope.isSubmitProgess = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var batchJson = {}
    batchJson.uuid = $scope.batchDetail.uuid;
    batchJson.name = $scope.batchDetail.name;
    batchJson.desc = $scope.batchDetail.desc;
    batchJson.active = $scope.batchDetail.active;
    batchJson.published = $scope.batchDetail.published;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
      var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
    }
    batchJson.tags = tagArray;
    var metaInfoArray = [];
    for (var i = 0; i < $scope.metaTags.length; i++) {
      var metaInfo = {}
      var ref = {};
      ref.type = $scope.metaTags[i].type
      ref.uuid = $scope.metaTags[i].uuid;
      metaInfo.ref = ref;
      metaInfoArray[i] = metaInfo;
    }
    
    batchJson.metaList = metaInfoArray;
    console.log(JSON.stringify(batchJson))
    BatchService.submit(batchJson, CF_META_TYPES.batch,upd_tag).then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
     
      if (options.execution == "YES") {
        BatchService.getOneById(response.data, CF_META_TYPES.batch).then(function(response) {onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          BatchService.execute(result.data.uuid,result.data.version).then(function(response) { onSuccess(response.data)});
          var onSuccess = function(response) {
            console.log(JSON.stringify(response))
            $scope.isSubmitProgess = false;
            $scope.saveMessage = "Batch Saved and Submitted Successfully"
            notify.type='success',
            notify.title= 'Success',
            notify.content=$scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.oksave();
          }
        }
      } //End If
      else {
        $scope.isSubmitProgess = false;
        $scope.saveMessage = "Batch Saved Successfully"
        notify.title= 'Success',
        notify.content=$scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.oksave();
      } //End else
    } //End Submit Api Function
    var onError = function(response) {
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function
});




