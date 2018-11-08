
MetadataModule = angular.module('MetadataModule');
MetadataModule.controller('MetadataRelationController', function ($state, $rootScope, $scope, $stateParams, $cookieStore, MetadataRelationSerivce, privilegeSvc, CommonService, $timeout, $filter) {
	$scope.mode = "false";
	$scope.relationdata;
	$scope.showFrom = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.joinType = ["EQUI JOIN", "LEFT OUTER", 'RIGHT OUTER', 'FULL OUTER', 'LEFT SEMI', 'CROSS JOIN'];
	$scope.operator = ["=", "<", ">", "<=", ">=", "IN", "BETWEEN"];
	$scope.lshType = ["string", "datapod", 'formula'];
	$scope.logicalOperator = ["AND","OR"];
	$scope.relationOperators = ["=", "IN", "NOT IN"]
	$scope.relation = {};
	$scope.relation.versions = [];
	$scope.SourceTypes = ["datapod","dataset"]
	$scope.rhsAllAttribute = [];
	$scope.selectSourceType = $scope.SourceTypes[0];
	$scope.relationTableArray = null;
	$scope.isshowmodel = false;
	$scope.isSubmitEnable = true;
	$scope.relationHasChanged = true;
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
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['relation'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['relation'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag = response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
	$scope.getLovByType();
	$scope.showPage = function () {
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}

	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('metaListrelation', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListrelation', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};

	$scope.relationFormChange = function () {
		if ($scope.mode == "true") {
			$scope.relationHasChanged = true;
		}
		else {

			$scope.relationHasChanged = false;
		}
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}

	$scope.onChangeName = function (data) {
		$scope.relationName = data;
	}

	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		MetadataRelationSerivce.getAllVersionByUuid($stateParams.id, "relation").then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var relationversion = {};
				relationversion.version = response[i].version;
				$scope.relation.versions[i] = relationversion;

			}
		}

		MetadataRelationSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'relation').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			var defaultoption = {};
			defaultversion.version = response.relationdata.version;
			defaultversion.uuid = response.relationdata.uuid;
			$scope.relation.defaultVersion = defaultversion;
			$scope.relationdata = response.relationdata
			$scope.relationTableArray = response.relationInfo;
			$scope.relationName = $scope.relationdata.name
			$scope.selectSourceType=$scope.relationdata.dependsOn.ref.type;
			var tags = [];
			for (var i = 0; i < response.relationdata.tags.length; i++) {
				var tag = {};
				tag.text = response.relationdata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}

			$scope.rhsAllAttribute = [];
			MetadataRelationSerivce.getAllAttributeBySource($scope.relationdata.uuid, "relation", $scope.relationdata.version).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.rhsAllAttribute = [];
				$scope.lhsAllAttribute = response.allattributes;
			    var count = 0;
			  	for (var i = 0; i < response.attributes.length; i++) {
			 	//	if (i != 0) {
			 			$scope.rhsAllAttribute[count] = response.attributes[i];
			 			count = count + 1;
			 	//	}
				}
			}
            
			MetadataRelationSerivce.getAllLatest($scope.selectSourceType).then(function (response1) { onSuccessrelationWithSourceType(response1.data) });
			var onSuccessrelationWithSourceType = function (response1) {
				$scope.alldatapod = response1
				$scope.alljoindatapod = response1;
				defaultoption.uuid = $scope.relationdata.dependsOn.ref.uuid;
				defaultoption.name = $scope.relationdata.dependsOn.ref.name;
				$scope.alldatapod.defaultoption = defaultoption;
				$scope.allJoinDatapod();
			}
			// MetadataRelationSerivce.getAllLatest($scope.selectSourceType=="datapod"?"dataset":"datapod").then(function (response) { onSuccessrelationWoSourceType(response.data) });
			// var onSuccessrelationWoSourceType = function (response) {
			// 	if($scope.selectSourceType =="datapod"){
			// 		$scope.alljoindataset = response.options;
			// 	}else{
			// 		$scope.alljoindatapod=response.options;
			// 	}
				
				
			// }
		
		} //End Onsuccess()

	}//End IF
	else {
		$scope.showactive = "false";
		$scope.relationdata={};
		$scope.relationdata.locked="N";
		MetadataRelationSerivce.getAllLatest("datapod").then(function (response) { onSuccessrelation(response.data) });
		var onSuccessrelation = function (response) {
			$scope.alldatapod = response
			$scope.allJoinDatapod();
			MetadataRelationSerivce.getAllAttributeBySource($scope.alldatapod.defaultoption.uuid, "datapod").then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.lhsAllAttribute = response;
				$scope.rhsAllAttribute[0]=response;
			}
		}

	}
	
	$scope.onChangeType=function(){
		$scope.relationTableArray=[];
		$scope.showactive = "false"
		MetadataRelationSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccessrelation(response.data) });
		var onSuccessrelation = function (response) {
			$scope.alldatapod = response
			$scope.allJoinDatapod();
			MetadataRelationSerivce.getAllAttributeBySource($scope.alldatapod.defaultoption.uuid,$scope.selectSourceType).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.lhsAllAttribute = response;
				$scope.rhsAllAttribute[0]=response;
			}
		}
	}
	 
	$scope.onChangeJoinMetaType=function(type,index){
	
		MetadataRelationSerivce.getAllLatest(type).then(function (response) { onSuccessrelation(response.data) });
		var onSuccessrelation = function (response) {
			console.log(response)
			type =="datapod"?$scope.alljoindatapod = response.options:$scope.alljoindataset=response.options;
		}
	}

	$scope.allJoinDatapod = function () {
		
		var newDataList = [];
		angular.forEach($scope.alldatapod.options, function (selected) {
			if (selected.uuid != $scope.alldatapod.defaultoption.uuid) {
				newDataList.push(selected);
			}
		});

	  	$scope.alljoindatapod = newDataList;
		// if($scope.selectSourceType =="datapod"){
		// 	$scope.alljoindataset = newDataList;
		// }else{
		// 	$scope.alljoindatapod=newDataList
		// }

	}
	$scope.selectOption = function () {
		MetadataRelationSerivce.getAllAttributeBySource($scope.alldatapod.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
		var onSuccessGetAttributesByDatapod = function (response) {
			$scope.lhsAllAttribute = response;
			$scope.rhsAllAttribute[0]=response;
			$scope.allJoinDatapod();
			$scope.joinRHS();
			$scope.relationTableArray=[];
		}

	}

	$scope.joinRHS = function () {
		$scope.lhsAllAttribute=[];
		for (var i = 0; i < $scope.rhsAllAttribute.length; i++) {
			for (var obj in $scope.rhsAllAttribute[i]) {
				$scope.lhsAllAttribute.push($scope.rhsAllAttribute[i][obj])
			}
		}
	
	}

	$scope.joinChange = function (data, index,type) {
		//var temp=$scope.rhsAllAttribute.splice(index+1,1);
		//$scope.lhsAllAttribute = _.without($scope.lhsAllAttribute, _.findWhere($scope.lhsAllAttribute, {uuid:temp.uuid}));
		if (data !=null && typeof data != "undefined") {
			MetadataRelationSerivce.getAllAttributeBySource(data.uuid,type).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
			    $scope.rhsAllAttribute.splice([index+1],0,response);
				$scope.joinRHS();
			}	
		}
	}

	$scope.onChangeJoinType = function (joinType, index) {
		if (joinType == 'CROSS JOIN') {
			$scope.relationTableArray[index].isjoinDisable = false;
			$scope.relationTableArray[index].joinKey = [];
		} else {
			$scope.relationTableArray[index].isjoinDisable = false;
			if($scope.relationTableArray[index].joinKey.length == 0)
			$scope.addJoinSubRow(index);
		}
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.relationTableArray, function (relation) {
			relation.selected = $scope.selectalljoin;
		});
	}

	$scope.addrow = function () {
		if ($scope.relationTableArray == null) {
			$scope.relationTableArray = [];
		}
		$scope.expanded = true;
		var relationtable = {};

		var joinKey = [];
		
		relationtable.relationJoinType = $scope.joinType[0];
		var joinkey = {};
		joinkey.relationOperator = $scope.relationOperators[0];
		joinKey.push(joinkey)
		relationtable.joinKey = joinKey;
		relationtable.joinMetaType=$scope.selectSourceType;
	
		$scope.relationTableArray.splice($scope.relationTableArray.length, 0, relationtable);

	}

	$scope.removeRow = function (index) {
		var newDataList = [];
		var index = null;
		angular.forEach($scope.relationTableArray, function (selected, key) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			else {
				index = key;
			}
		});
		$scope.relationTableArray = newDataList;
		if (index != null) {
			$scope.rhsAllAttribute.splice(index, 1);
			$scope.selectOption();
		}
		if ($scope.selectalljoin == true) {
			$scope.lhsAllAttribute = null;
			$scope.rhsAllAttribute = [];
		}
		$scope.selectalljoin = false;

	}

	$scope.selectAllSubRow = function (index) {
		angular.forEach($scope.relationTableArray[index].joinKey, function (joinkey) {
			joinkey.selected = $scope.relationTableArray[index].selectalljoinkey;
		});
	}

	$scope.removeJoinSubRow = function (index) {
		$scope.relationTableArray[index].selectalljoinkey = false
		var newDataList = [];
		$scope.relationTableArray[index].checkalljoinKeyrow = false;
		angular.forEach($scope.relationTableArray[index].joinKey, function (selected) {

			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.relationTableArray[index].joinKey = newDataList;
	}
	
	$scope.addJoinSubRow = function (index) {
		var joinKey = {}
		joinKey.logicalOperator = $scope.relationTableArray[index].joinKey.length >0 ? $scope.logicalOperator[0]:"";
		joinKey.relationOperator = $scope.relationOperators[0];
       
		$scope.relationTableArray[index].joinKey.splice($scope.relationTableArray[index].joinKey.length, 0, joinKey);
	}

	$scope.selectVersion = function () {
		$scope.alldatapod = null;
		$scope.myform.$dirty = false;
		MetadataRelationSerivce.getOneByUuidAndVersion($scope.relation.defaultVersion.uuid, $scope.relation.defaultVersion.version, 'relation').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			var defaultoption = {};
			defaultversion.version = response.relationdata.version;
			defaultversion.uuid = response.relationdata.uuid;
			$scope.relation.defaultVersion = defaultversion;
			$scope.relationdata = response.relationdata
			$scope.relationTableArray = response.relationInfo;
			$scope.relationName = $scope.relationdata.name;
			var tags = [];
			for (var i = 0; i < response.relationdata.tags.length; i++) {
				var tag = {};
				tag.text = response.relationdata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
			$scope.rhsAllAttribute = [];
			MetadataRelationSerivce.getAllAttributeBySource($scope.relationdata.uuid, "relation", $scope.relationdata.version).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.rhsAllAttribute = [];
				$scope.lhsAllAttribute = response.allattributes;
				var count = 0;
				for (var i = 0; i < response.attributes.length; i++) {
					if (i != 0) {

						$scope.rhsAllAttribute[count] = response.attributes[i];
						count = count + 1;
					}

				}



			}
			MetadataRelationSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccessrelation(response.data) });
			var onSuccessrelation = function (response) {
				$scope.alldatapod = response
				$scope.alljoindatapod = response
				defaultoption.uuid = $scope.relationdata.dependsOn.ref.uuid;
				defaultoption.name = $scope.relationdata.dependsOn.ref.name;
				$scope.alldatapod.defaultoption = defaultoption;
				$scope.allJoinDatapod();
			}
		}

	}

	$scope.submitRelation = function () {
		var upd_tag = "N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.relationHasChanged = true;
		$scope.myform.$dirty = false;
		var relationjson = {}
		relationjson.uuid = $scope.relationdata.uuid;
		relationjson.name = $scope.relationdata.name;
		relationjson.active = $scope.relationdata.active;
		relationjson.locked = $scope.relationdata.locked;
		relationjson.desc = $scope.relationdata.desc;
		relationjson.published = $scope.relationdata.published;
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
		relationjson.tags = tagArray;
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectSourceType;
		ref.uuid = $scope.alldatapod.defaultoption.uuid;
		dependsOn.ref = ref;
		relationjson.dependsOn = dependsOn

		var relationInfoArray = [];

		if ($scope.relationTableArray != null) {
			if ($scope.relationTableArray.length > 0) {
				for (j = 0; j < $scope.relationTableArray.length; j++) {

					var relationInfo = {};
					var attributeList = {};
					var join = {}
					var joinKey = [];

					var joinref = {}
					var operand = []
					var firstoperad = {}
					var scecondoperad = {}
					var firstoperandref = {}
					var scecondoperandref = {}
					if ($scope.relationTableArray[j].relationJoinType == "EQUI JOIN") {
						relationInfo.joinType = ""
					}
					else if ($scope.relationTableArray[j].relationJoinType == "CROSS JOIN"){
						relationInfo.joinType = "CROSS";
					}
					else {

						relationInfo.joinType = $scope.relationTableArray[j].relationJoinType;
					}
					joinref.type = $scope.relationTableArray[j].joinMetaType;
					joinref.uuid = $scope.relationTableArray[j].join.uuid;
					join.ref = joinref;
					relationInfo.join = join;
					if ($scope.relationTableArray[j].joinKey && $scope.relationTableArray[j].joinKey.length > 0) {
						for (var i = 0; i < $scope.relationTableArray[j].joinKey.length; i++) {
							var operand = []
							var JoinKeyDetail = {};
							var firstoperad = {}
							var scecondoperad = {}
							var firstoperandref = {}
							var scecondoperandref = {}
							if (typeof $scope.relationTableArray[j].joinKey[i].logicalOperator == "undefined") {
								JoinKeyDetail.logicalOperator = ""
							}
							else {
								JoinKeyDetail.logicalOperator = $scope.relationTableArray[j].joinKey[i].logicalOperator
							}

							JoinKeyDetail.operator = $scope.relationTableArray[j].joinKey[i].relationOperator
							firstoperandref.type =$scope.relationTableArray[j].joinKey[i].lhsoperand.type;
							firstoperandref.uuid = $scope.relationTableArray[j].joinKey[i].lhsoperand.uuid
							firstoperad.ref = firstoperandref;
							firstoperad.attributeId = $scope.relationTableArray[j].joinKey[i].lhsoperand.attributeId;
							firstoperad.attributeType = $scope.relationTableArray[j].joinKey[i].lhsoperand.attrType
							scecondoperandref.type =  $scope.relationTableArray[j].joinKey[i].rhsoperand.type;
							scecondoperandref.uuid = $scope.relationTableArray[j].joinKey[i].rhsoperand.uuid;
							scecondoperad.attributeId = $scope.relationTableArray[j].joinKey[i].rhsoperand.attributeId;
							scecondoperad.attributeType = $scope.relationTableArray[j].joinKey[i].rhsoperand.attrType;
							scecondoperad.ref = scecondoperandref;
							operand[0] = firstoperad;

							operand[1] = scecondoperad;
							JoinKeyDetail.operand = operand
							joinKey[i] = JoinKeyDetail;
							relationInfo.joinKey = joinKey;
						}
					}
					else {
						relationInfo.joinKey = [];
					}
					relationInfoArray[j] = relationInfo




				}

			}
		}
		relationjson.relationInfo = relationInfoArray
		console.log(JSON.stringify(relationjson))
		MetadataRelationSerivce.submit(relationjson, 'relation', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Relation Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okRelationSave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}


	$scope.okRelationSave = function () {
		$('#relationsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'relation' }); }, 2000);

		}
	}


	$scope.expandAll = function (expanded) {
		// $scope is required here, hence the injection above, even though we're using "controller as" syntax
		$scope.$broadcast('onExpandAll', { expanded: expanded });
	};
});




