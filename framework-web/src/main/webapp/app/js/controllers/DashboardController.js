/****/
MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataDashboardController2', function ($state, $scope, $stateParams, $rootScope, $sessionStorage, $timeout, $filter, privilegeSvc, MetadataDahsboardSerivce,CommonService) {
	$scope.pageNo = 1;
	$scope.nextPage = function () {
		$scope.pageNo++;
	}
	$scope.prevPage = function () {
		$scope.pageNo--;
	}
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.sectionRows = [];
	if ($stateParams.action == 'add' || !$stateParams.id) {
		$scope.sectionRows = [{
			columns: [{ edit: true ,rowNo:1,colNo:1}]
		}]
	}
	var privileges = [];
	$rootScope.isCommentDisabled = true
	if ($stateParams.mode == 'false') {
		privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});

	}
	
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
	
	$scope.onChangeVizpod=function(vizpodInfo,parentIndex,index){
		if(parentIndex ==-1){
			parentIndex=0;
		}
		if(index == -1){
			index =0;
		}
		$scope.sectionRows[parentIndex].columns[index].name=vizpodInfo.name;
	}
	
	$scope.addSectionRow = function (i) {
		debugger
		$scope.sectionRows.splice(i + 1, 0, {
			columns: [{"edit":true,rowNo:$scope.sectionRows.length+1,colNo:1}]
		});
	}

	$scope.addSectionColumn = function (row) {
		var len=row.columns.length-1;
		row.columns.push({ edit: true,rowNo:row.columns[len].rowNo, colNo: row.columns[len].colNo+1});
	}

	$scope.removeSectionColumn = function (columns, i) {
		columns.splice(i, 1);
		if (columns.length == 0 && $scope.sectionRows.length > 1) {
			$scope.sectionRows.splice($scope.sectionRows.indexOf(this.row), 1);
		}

	}

	$scope.convertSectionInfo = function (sectionInfo) {
		if (!sectionInfo[0].rowNo) {
			var row = 0;
			angular.forEach(sectionInfo, function (val, key) {
				if (key > 0 && key % 2 == 0) {
					row++;
				}
				if ($scope.sectionRows[row])
					$scope.sectionRows[row].columns.push(val);
				else
					$scope.sectionRows[row] = { columns: [val] };

			});
		}
		else {
			angular.forEach(sectionInfo, function (val, key) {
				if ($scope.sectionRows[val.rowNo - 1]) {
					$scope.sectionRows[val.rowNo - 1].columns[val.colNo - 1] = val
				}
				else {
					$scope.sectionRows[val.rowNo - 1] = { columns: [val] }
				}
			})
		}
		console.log($scope.sectionRows);
		$scope.reConvertSectionInfo($scope.sectionRows)
	}

	$scope.reConvertSectionInfo = function (rows) {

		var arr = [];
		var rowNo = 0;
		angular.forEach(rows, function (row, rowKey) {
			if (row.columns && row.columns.length > 0) {
				rowNo++;
				angular.forEach(row.columns, function (col, colKey) {
					delete col.edit;
					col.rowNo = rowNo;
					col.colNo = colKey + 1;
					arr.push(col);
				})
			}
		});
		console.log(arr);
		return arr;
	}

	$scope.onDragStart = function (column) {
		column.dragInProgress = true;
		$scope.currentDraggingColumn = column;
	}
	$scope.onDragStop = function (column, row) {
		delete column.dragInProgress;
		$scope.currentDraggingColumn = undefined;
		if (row.columns.length == 0) {
			$scope.sectionRows.splice($scope.sectionRows.indexOf(row), 1);
		}
		if($scope.sectionRows && $scope.sectionRows.length >0){
			for(var i=0;i<$scope.sectionRows.length;i++){
				for(var j=0;j<$scope.sectionRows[i].columns.length;j++){
					$scope.sectionRows[i].columns[j].rowNo=i+1;
					$scope.sectionRows[i].columns[j].colNo=j+1;
				}
			}
		}
		// if($scope.tempDragRowIndex && $scope.tempDragColIndex){
		// 		$scope.sectionRows[$scope.tempDragRowIndex].columns.splice($scope.tempDragColIndex,1);
		// 		$scope.tempDragRowIndex = undefined;
		// 		$scope.tempDragColIndex = undefined;
		// }
	}

	var dragDisableTimeOut = {};
	$scope.dragoverCallback = function (index, external, type, rowIndex, row) {
		var ColWidth = $scope.getColWidth(row);
		if (ColWidth < 4) {
			$('.sectionRow#sectionRowNo_' + rowIndex).css('opacity', '0.3');
			$('.sectionRow#sectionRowNo_' + rowIndex).css('background-color', 'red');
			clearTimeout(dragDisableTimeOut[rowIndex]);
			dragDisableTimeOut[rowIndex] = setTimeout(function () {
				$('.sectionRow#sectionRowNo_' + rowIndex).css('opacity', '1');
				$('.sectionRow#sectionRowNo_' + rowIndex).css('background-color', 'transparent');
			}, 400);
			return false;
		}
		return true
		// if($scope.tempDragRowIndex && $scope.tempDragColIndex){
		// 	if($scope.tempDragRowIndex == rowIndex && $scope.tempDragColIndex != index){
		// 		$scope.sectionRows[$scope.tempDragRowIndex].columns.splice($scope.tempDragColIndex,1)
		// 	}
		// }
		// if($scope.sectionRows[rowIndex].columns[index]!=$scope.currentDraggingColumn) {
		// 	$scope.tempDragRowIndex = rowIndex;
		// 	$scope.tempDragColIndex = index;
		// 	$scope.sectionRows[$scope.tempDragRowIndex].columns.splice($scope.tempDragColIndex,0,$scope.currentDraggingColumn)
		// }


	}
	$scope.dropCallback = function (index, item, external, type, row) {

	}

	

	$scope.getColWidth = function (row) {
		var count = 0;
		angular.forEach(row.columns, function (val, key) {
			if (!val.dragInProgress) {
				count++;
			}
		})
		return count <= 4 ? 12 / (count) : '3'
	}

	$scope.mode = " ";
	$scope.dataLoading = false;
	
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
	}
	else {
		$scope.isAdd = true;
	}
	$scope.isSubmitEnable = true;
	$scope.dashboarddata;
	$scope.showdashboard = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showgraphdiv = false
	$scope.graphDataStatus = false
	$scope.dashboard = {};
	$scope.dashboard.versions = [];
	$scope.isshowmodel = false;
	$scope.dependsOnTypes = ["datapod","dataset","relation"]
	$scope.sectiontable = null
	$scope.logicalOperator = [" ", "OR", "AND"];
	$scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
	$scope.filterTableArray = null;
	$scope.dashboardCompare = null;
	$scope.filterAttributeTags = null;
	$scope.stageName = "dashboard"
	$scope.stageParams = { "type": "dashboard" }
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['dashboard'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['dashboard'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showDashboardPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showdashboard = true;
		$scope.showgraph = false
		$scope.graphDataStatus = false;
		$scope.showgraphdiv = false
	}//End showDashboardPage

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showDashboardPage()
		$state.go('metaListdashboard', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.dashboarddata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showDashboardPage()
		$state.go('metaListdashboard', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showDashboardPage()
		$state.go('metaListdashboard', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.sortableOptions = {
		update: function (e, ui) {
			console.log('Sort Updated');
		},
		stop: function (e, ui) {
			// this callback has the changed model
			console.log('Sort Stopped');
			$scope.myform.$dirty = true;
		}
	};

	$scope.showDashboardGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showdashboard = false;
		$scope.showgraph = false
		$scope.graphDataStatus = true
		$scope.showgraphdiv = true;

	}//End showDashboardGraph



	$scope.getVizpodByType = function () {
		// MetadataDahsboardSerivce.getVizpodByType($scope.alldependsOn.defaultoption.uuid, $scope.selectDependsOnType).then(function (response) { onSuccessGetVizpodByType(response.data) });
		// var onSuccessGetVizpodByType = function (response) {
		// 	$scope.allVizpodByDependsOn = response;
		// 	$scope.icons = {};
		// 	angular.forEach(response, function (val) {
		// 		$scope.icons[val.uuid] = val.type;
		// 	})
		// 	console.log($scope.icons);
		// }
		CommonService.getAllLatest("vizpod").then(function(response){onSuccessGetVizpodByType(response.data)});
		var onSuccessGetVizpodByType = function (response) {
			$scope.allVizpodByDependsOn = response;
			$scope.icons = {};
			angular.forEach(response, function (val) {
				$scope.icons[val.uuid] = val.type;
			})
			console.log($scope.icons);
		}

	}

	$scope.getAllAttributeBySource = function () {
		MetadataDahsboardSerivce.getAllAttributeBySource($scope.alldependsOn.defaultoption.uuid, $scope.selectDependsOnType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
		var onSuccessGetDatapodByRelation = function (response) {
			//console.log(JSON.stringify(response))
			$scope.sourcedatapodattribute = response;
			$scope.lhsdatapodattributefilter = response;
			$scope.allattribute = response;
			$scope.getFormulaByType()//Call Function
		}
	}

    $scope.getFormulaByType = function () {
		MetadataDahsboardSerivce.getFormulaByType($scope.alldependsOn.defaultoption.uuid, $scope.selectDependsOnType).then(function (response) { onSuccessGetFormulaByType(response.data) });
		var onSuccessGetFormulaByType = function (response) {
			for (var i = 0; i < response.length; i++) {
				var formulajson = {};
				formulajson.index = $scope.sourcedatapodattribute.length;
				formulajson.id = response[i].uuid;
				formulajson.uuid = response[i].uuid
				formulajson.dname = "formula"+"."+response[i].name
				formulajson.name = response[i].name
				formulajson.type = "formula"
				$scope.sourcedatapodattribute.push(formulajson)
			}//End For
		}//End onSuccessGetFormulaByType
	}
	$scope.selectType = function () {
		MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) { onSuccessGetAllLatest(response.data) });
		var onSuccessGetAllLatest = function (response) {
			$scope.alldependsOn = response;
			$scope.filterAttributeTags = null;
			$scope.getVizpodByType() //Call Function
			$scope.getAllAttributeBySource()// Call Function
		}

	}//End selectType

	$scope.selectDependsOnOption = function () {
		$scope.filterAttributeTags = null;
		$scope.getVizpodByType() //Call Function
		$scope.getAllAttributeBySource()// Call Function
	}

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	};
	$scope.clear = function () {

		$scope.profileTags = null;
	}

	if (typeof $stateParams.id != "undefined") {
      
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDahsboardSerivce.getAllVersionByUuid($stateParams.id, "dashboard").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var dashboardversion = {};
				dashboardversion.version = response[i].version;
				$scope.dashboard.versions[i] = dashboardversion;
			}
		}//End onGetAllVersionByUuid

		MetadataDahsboardSerivce.getLatestByUuidView($stateParams.id, "dashboard")
			.then(function (response) { onGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.dashboarddata = response.dashboarddata;
			$scope.dashboardCompare = response.dashboarddata;
			var defaultversion = {};
			defaultversion.version = response.dashboarddata.version;
			defaultversion.uuid = response.dashboarddata.uuid;
			$scope.dashboard.defaultVersion = defaultversion;
			$scope.selectDependsOnType = response.dashboarddata.dependsOn.ref.type;
			$scope.sectiontable = response.sectionInfo;
			$scope.convertSectionInfo(response.sectionInfo);
			$scope.filterAttributeTags = response.filterInfo;
			var tags = [];
			if (response.dashboarddata.tags != null) {
				for (var i = 0; i < response.dashboarddata.tags.length; i++) {
					var tag = {};
					tag.text = response.dashboarddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) { onSuccessGetAllLatest(response.data) });
			var onSuccessGetAllLatest = function (response) {
				$scope.alldependsOn = response;
				var defaultoption = {}
				defaultoption.uuid = $scope.dashboarddata.dependsOn.ref.uuid
				defaultoption.name = $scope.dashboarddata.dependsOn.ref.name
				$scope.alldependsOn.defaultoption = defaultoption;
				$scope.getVizpodByType() //Call Function
				$scope.getAllAttributeBySource()// Call Function
			}//End onSuccessGetAllLatest
		}//End
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};

	}//End IF

	else {
	 $scope.dashboarddata={};
	 $scope.dashboarddata.locked="N";

	}//End Else

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.alldependsOn = null;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDahsboardSerivce.getOneByUuidAndVersionView($scope.dashboard.defaultVersion.uuid, $scope.dashboard.defaultVersion.version, 'dashboard')
			.then(function (response) { onSuccessGetOneByUuidAndVersion(response.data) },function (response) { onError(response.data)});
		var onSuccessGetOneByUuidAndVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.dashboarddata = response.dashboarddata;
			$scope.dashboardCompare = response.dashboarddata;
			var defaultversion = {};
			defaultversion.version = response.dashboarddata.version;
			defaultversion.uuid = response.dashboarddata.uuid;
			$scope.dashboard.defaultVersion = defaultversion;
			$scope.selectDependsOnType = response.dashboarddata.dependsOn.ref.type;
			$scope.sectiontable = response.sectionInfo
			$scope.filterAttributeTags = response.filterInfo;
			var tags = [];
			if (response.dashboarddata.tags != null) {
				for (var i = 0; i < response.dashboarddata.tags.length; i++) {
					var tag = {};
					tag.text = response.dashboarddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) { onSuccessGetAllLatest(response.data) });
			var onSuccessGetAllLatest = function (response) {
				$scope.alldependsOn = response;
				var defaultoption = {}
				defaultoption.uuid = $scope.dashboarddata.dependsOn.ref.uuid
				defaultoption.name = $scope.dashboarddata.dependsOn.ref.name
				$scope.alldependsOn.defaultoption = defaultoption;
				$scope.getVizpodByType() //Call Function
				$scope.getAllAttributeBySource()// Call Function
			}//End onSuccessGetAllLatest
		}//End onSuccessGetOneByUuidAndVersion
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End selectVersion

	$scope.addRow = function () {
		if ($scope.sectiontable == null) {
			$scope.sectiontable = [];
		}
		var sectionjson = {}
		var vizpod = {}
		sectionjson.sectionId = $scope.sectiontable.length;
		sectionjson.vizpod = vizpod;
		$scope.sectiontable.splice($scope.sectiontable.length, 0, sectionjson);
	}

	$scope.selectAllRow = function () {

		angular.forEach($scope.sectiontable, function (stage) {
			stage.selected = $scope.selectallsection;
		});
	}

	$scope.removeRow = function () {
		var newDataList = [];
		$scope.selectallsection = false;
		angular.forEach($scope.sectiontable, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.sectiontable = newDataList;
	}

	/*Start SubmitDashboard*/
	$scope.submitDashboard = function () {
        var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		var dashboardjson = {}
	
		dashboardjson.uuid = $scope.dashboarddata.uuid
		dashboardjson.name = $scope.dashboarddata.name
		dashboardjson.desc = $scope.dashboarddata.desc
		dashboardjson.active = $scope.dashboarddata.active;
		dashboardjson.locked = $scope.dashboarddata.locked;
		dashboardjson.published = $scope.dashboarddata.published;
		dashboardjson.publicFlag = $scope.dashboarddata.publicFlag;
		dashboardjson.saveOnRefresh = $scope.dashboarddata.saveOnRefresh;

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

		dashboardjson.tags = tagArray;
		var dependson = {};
		var ref = {};
		ref.uuid = $scope.alldependsOn.defaultoption.uuid;
		ref.type = $scope.selectDependsOnType
		dependson.ref = ref;
		dashboardjson.dependsOn = dependson;

		//SectionInfo
		var sectionArray = [];
		$scope.sectiontable = $scope.reConvertSectionInfo($scope.sectionRows);
		if ($scope.sectiontable.length > 0) {
			for (var i = 0; i < $scope.sectiontable.length; i++) {
				var sectionjson = {};
				var vizpodjson = {};
				var ref = {};
				sectionjson.sectionId = $scope.sectiontable[i].sectionId;
				sectionjson.name = $scope.sectiontable[i].name;
				ref.type = "vizpod";
				ref.uuid = $scope.sectiontable[i].vizpod.uuid;
				vizpodjson.ref = ref;
				sectionjson.vizpodInfo = vizpodjson;
				sectionjson.rowNo = $scope.sectiontable[i].rowNo;
				sectionjson.colNo = $scope.sectiontable[i].colNo;
				sectionArray[i] = sectionjson;
			}
		}
		dashboardjson.sectionInfo = sectionArray;
		var filterInfoArray = [];
		if ($scope.filterAttributeTags != null) {
			for (var i = 0; i < $scope.filterAttributeTags.length; i++) {
				var filterInfo = {}
				var ref = {};
				ref.type = $scope.filterAttributeTags[i].type;
				ref.uuid = $scope.filterAttributeTags[i].uuid;
				filterInfo.ref = ref;
				if($scope.filterAttributeTags[i].type !="formula"){
					filterInfo.attrId = $scope.filterAttributeTags[i].attributeId;
				}
				filterInfoArray[i] = filterInfo;
			}
		}
		dashboardjson.filterInfo = filterInfoArray;
		console.log(JSON.stringify(dashboardjson));
		MetadataDahsboardSerivce.submit(dashboardjson, 'dashboard',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.changemodelvalue();
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Dashboard Saved Successfully'
			$scope.$emit('notify', notify);
			setTimeout(function () {
			$scope.okdashboardsave()},2000);
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End SubmitDashboard

	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};

	$scope.okdashboardsave = function () {
		$scope.stageName = $sessionStorage.fromStateName;
		$scope.stageParams = $sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
		$('#dashboardsave').css("display", "none");
		var hidemode = "yes";
	
		if (hidemode == 'yes') {
			if ($scope.stageName == "metadata") {
				 $state.go('metadata', { 'type': 'dashboard' });
			}
			else if ($scope.stageName != "metadata" && typeof $scope.stageParams.id != "undefined") {

			 	$state.go($scope.stageName, {'id': $scope.stageParams.id }); 
			}
			else {
				 $state.go("dashboard");
			}
		}
	}
	$scope.$on('$destroy', function () {
		privileges = [];
	})

});
