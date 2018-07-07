MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataFilterController', function ($rootScope,$state, $scope, $stateParams, MetadataFilterSerivce, privilegeSvc,CommonService,$timeout,$filter,CF_FILTER) {
	$scope.mode = "false";
	$scope.dataLoading = false;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else {
		$scope.isAdd = true;
	}
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
	$scope.filterHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.filterdata;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.logicalOperator = ["OR", "AND"];
	$scope.relation = ["relation", "dataset", "datapod"];
	//$scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN","LIKE","Not LIKE","RLIKE","EXISTS","NOT EXISTS"];
	$scope.spacialOperator=['<','>','<=','>=','=','LIKE','NOT LIKE','RLIKE'];
	$scope.operator =CF_FILTER.operator;
	$scope.lshType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer"},
		{ "text": "datapod", "caption": "attribute"},
		{ "text": "formula", "caption": "formula"}];
	$scope.rhsType = [
		{ "text": "string", "caption": "string","disabled":false },
		{ "text": "string", "caption": "integer" ,"disabled":false },
		{ "text": "datapod", "caption": "attribute","disabled":false },
		{ "text": "formula", "caption": "formula","disabled":false },
		{ "text": "dataset", "caption": "dataset" ,"disabled":false }]
	$scope.filter = {};
	$scope.filter.versions = [];
	$scope.filterTableArray = null;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['filter'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['filter'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
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

	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('metaListfilter', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('metaListfilter', {
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
	
	$scope.SearchAttribute=function(index){
		$scope.selectDatasetAttr=$scope.filterTableArray[index].rhsdataset
		MetadataFilterSerivce.getAllLatest("dataset").then(function (response) { onSuccessRelation(response.data) });
		$scope.searchAttrIndex=index;
		var onSuccessRelation = function (response) {
			$scope.allDataset = response;
			var temp;
			if($scope.selectRelation == "dataset"){
				temp = response.options.filter(function(el) {
					return el.uuid !== $scope.filterRelation.defaultoption.uuid;
				});
				$scope.allDataset.options=temp;
				$scope.allDataset.defaultoption=temp[0]
			}
            
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			  });
			MetadataFilterSerivce.getAllAttributeBySource($scope.allDataset.defaultoption.uuid,'dataset').then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.allDatasetAttr = response;
				debugger
				if (typeof $stateParams.id != "undefined" && $scope.selectDatasetAtt) {
					var defaultoption={};
					defaultoption.uuid=$scope.selectDatasetAttr.uuid;
					defaultoption.name="";
					$scope.allDataset.defaultoption=defaultoption;
				}else{
					$scope.selectDatasetAtt=$scope.allDatasetAttr[0]
				}

			}
		}
		
	}
    $scope.onChangeDataset=function(){
		MetadataFilterSerivce.getAllAttributeBySource($scope.allDataset.defaultoption.uuid,'dataset').then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allDatasetAttr = response;
		}
	}

	$scope.SubmitSearchAttr=function(){
		console.log($scope.selectDatasetAttr);
		$scope.filterTableArray[$scope.searchAttrIndex].rhsdataset=$scope.selectDatasetAttr;
		$('#searchAttr').modal('hide')
	}

	// $scope.disableRhsType=function(arrayStr){
	// 	for(var i=0;i<$scope.rhsType.length;i++){
	// 		$scope.rhsType[i].disabled=false;
	// 		if(arrayStr.length >0){
	// 			var index=arrayStr.indexOf($scope.rhsType[i].caption);
	// 			if(index !=-1){
	// 				$scope.rhsType[i].disabled=true;
	// 			}
	// 	    }
	// 	}
	// }

    $scope.onChangeOperator=function(index){	
		if($scope.filterTableArray[index].operator =='BETWEEN'){
			$scope.filterTableArray[index].rhstype=$scope.rhsType[1];
		//	$scope.disableRhsType(['string','attribute','formula','dataset'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}else if(['EXISTS','NOT EXISTS','IN','NOT IN'].indexOf($scope.filterTableArray[index].operator) !=-1){
			// if(['IN'].indexOf($scope.filterTableArray[index].operator) !=-1){
			// 	$scope.disableRhsType([]);
			// }else{
			// 	$scope.disableRhsType(['string','integer','attribute','formula']);
	 	    // }
			$scope.filterTableArray[index].rhstype=$scope.rhsType[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}else if(['<','>',"<=",'>='].indexOf($scope.filterTableArray[index].operator) !=-1){
           // $scope.disableRhsType(['string','dataset']);
			$scope.filterTableArray[index].rhstype=$scope.rhsType[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}
		else{
			//$scope.disableRhsType(['attribute','formula','dataset']);
			$scope.filterTableArray[index].rhstype=$scope.rhsType[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}
	}
	
	$scope.filterFormChange = function () {
		if ($scope.mode == "true") {
			$scope.filterHasChanged = true;
		}
		else {
			$scope.filterHasChanged = false;
		}
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}

	$scope.dependsOndd = function () {

		MetadataFilterSerivce.getAllLatest($scope.selectRelation).then(function (response) { onSuccessRelation(response.data) });
		var onSuccessRelation = function (response) {
			$scope.filterRelation = response
			MetadataFilterSerivce.getAllAttributeBySource($scope.filterRelation.defaultoption.uuid, $scope.selectRelation).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.filterDatapod = response;
				$scope.lhsdatapodattributefilter = response;
				$scope.filterTableArray=[];
				$scope.addRow();
			}
		}
	};

	$scope.changeRelation = function () {
		MetadataFilterSerivce.getAllAttributeBySource($scope.filterRelation.defaultoption.uuid, $scope.selectRelation).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.filterDatapod = response
			$scope.lhsdatapodattributefilter = response;
		}
	}



	$scope.checkAllFilterRow = function () {
		if (!$scope.selectedAllFitlerRow) {
			$scope.selectedAllFitlerRow = true;
		}
		else {
			$scope.selectedAllFitlerRow = false;
		}
		angular.forEach($scope.filterTableArray, function (filter) {
			filter.selected = $scope.selectedAllFitlerRow;
		});
	}
	$scope.addRow = function () {
		if ($scope.filterTableArray == null) {
			$scope.filterTableArray = [];
		}
		var filertable = {};
		filertable.id=$scope.filterTableArray.length;
		filertable.islhsDatapod = false;
		filertable.islhsFormula = false;
		filertable.islhsSimple = true;
		filertable.isrhsDatapod = false;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = true;
		if(filertable.id !=0)
		filertable.logicalOperator=$scope.logicalOperator[1];
		filertable.lhsFilter = $scope.lhsdatapodattributefilter[0]
		filertable.operator = $scope.operator[0].value;
		filertable.lhstype = $scope.lshType[0]
		filertable.rhstype = $scope.lshType[0]
		filertable.rhsvalue;
		filertable.rhsTypeDisables=['attribute','formula','dataset'];
		filertable.lhsvalue;
		$scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);
	}


	$scope.removeRow = function () {
		var newDataList = [];
		$scope.selectedAllFitlerRow = false;
		angular.forEach($scope.filterTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.filterTableArray = newDataList;
	}

	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}
	
	$scope.selectlhsType = function (type, index) {
		if (type == "string") {
			$scope.filterTableArray[index].islhsSimple = true;
			$scope.filterTableArray[index].islhsDatapod = false;
			$scope.filterTableArray[index].lhsvalue;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "datapod") {

			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = true;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.filterTableArray[index].islhsFormula = true;
			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = false;
			MetadataFilterSerivce.getFormulaByType($scope.filterRelation.defaultoption.uuid, $scope.selectRelation).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
		 }
		 


	}
	$scope.selectrhsType = function (type, index) {

		if (type == "string") {
			$scope.filterTableArray[index].isrhsSimple = true;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].rhsvalue
			$scope.filterTableArray[index].isrhsDataset = false;
		}
		else if (type == "datapod") {

			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = true;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsDataset = false;
		}
		else if (type == "formula") {

			$scope.filterTableArray[index].isrhsFormula = true;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			MetadataFilterSerivce.getFormulaByType($scope.filterRelation.defaultoption.uuid, $scope.selectRelation).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
		}
		else if (type == "dataset") {
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = true;
			CommonService.getAllLatest("dataset").then(function (response) { onSuccressGetAllLatestDataset(response.data) });
			var onSuccressGetAllLatestDataset = function (response) {
				$scope.allDataset = response;
			}
		}

	}


	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode;

		$scope.isDependencyShow = true;
		MetadataFilterSerivce.getAllVersionByUuid($stateParams.id, "filter").then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var filterversion = {};
				filterversion.version = response[i].version;
				$scope.filter.versions[i] = filterversion;
			}
		}//End  getAllVersionByUuid

		MetadataFilterSerivce.getOneByUuidandVersion($stateParams.id, $stateParams.version, 'filter').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			var defaultoption = {};
			defaultversion.version = response.filter.version;
			defaultversion.uuid = response.filter.uuid;
			$scope.filter.defaultVersion = defaultversion;
			$scope.filterdata = response.filter
			defaultoption.uuid = response.filter.dependsOn.ref.uuid;
			defaultoption.name = response.filter.dependsOn.ref.name;
			$scope.selectRelation = response.filter.dependsOn.ref.type
			$scope.filterTableArray = response.filterInfo;
			MetadataFilterSerivce.getAllLatest(response.filter.dependsOn.ref.type).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {
				$scope.filterRelation = response
				$scope.filterRelation.defaultoption = defaultoption
				MetadataFilterSerivce.getAllAttributeBySource($scope.filterdata.dependsOn.ref.uuid, $scope.filterdata.dependsOn.ref.type).then(function (response) { onSuccessAttributeBySource(response.data) });
				var onSuccessAttributeBySource = function (response) {
					$scope.filterDatapod = response
					$scope.lhsdatapodattributefilter = response;
				}
			}
			MetadataFilterSerivce.getFormulaByType($scope.filterdata.dependsOn.ref.uuid, $scope.filterdata.dependsOn.ref.type).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
			CommonService.getAllLatest("dataset").then(function (response) { onSuccressGetAllLatestDataset(response.data) });
			var onSuccressGetAllLatestDataset = function (response) {
				$scope.allDataset = response;
			}
			$scope.selectRelation = response.filter.dependsOn.ref.type
			for (var j = 0; j < $scope.filterdata.filterInfo.length; j++) {
				var lhsoperand = {};
				lhsoperand.uuid = $scope.filterdata.filterInfo[j].operand[0].ref.uuid
				lhsoperand.datapodname = $scope.filterdata.filterInfo[j].operand[0].ref.name;
				lhsoperand.name = $scope.filterdata.filterInfo[j].operand[0].attributeName;
				lhsoperand.attributeId = $scope.filterdata.filterInfo[j].operand[0].attributeId;
				$scope.filterdata.filterInfo[j].lhsoperand = lhsoperand;
			}
			$scope.filterName = $scope.convertUppdercase($scope.filterdata.name)
			var tags = [];
			for (var i = 0; i < response.filter.tags.length; i++) {
				var tag = {};
				tag.text = response.filter.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}

		}

	}//End IF
	else {
		$scope.showactive = "false"
	}



	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		MetadataFilterSerivce.getOneByUuidandVersion($scope.filter.defaultVersion.uuid, $scope.filter.defaultVersion.version, 'filter').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			var defaultoption = {};
			defaultversion.version = response.filter.version;
			defaultversion.uuid = response.filter.uuid;
			$scope.filter.defaultVersion = defaultversion;
			$scope.filterdata = response.filter
			defaultoption.uuid = response.filter.dependsOn.ref.uuid;
			defaultoption.name = response.filter.dependsOn.ref.name;
			$scope.selectRelation = response.filter.dependsOn.ref.type
			$scope.filterTableArray = response.filterInfo;
			MetadataFilterSerivce.getAllLatest(response.filter.dependsOn.ref.type).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {
				$scope.filterRelation = response
				$scope.filterRelation.defaultoption = defaultoption
				MetadataFilterSerivce.getAllAttributeBySource($scope.filterdata.dependsOn.ref.uuid, $scope.filterdata.dependsOn.ref.type).then(function (response) { onSuccessAttributeBySource(response.data) });
				var onSuccessAttributeBySource = function (response) {
					$scope.filterDatapod = response
					$scope.lhsdatapodattributefilter = response;
				}
			}
			MetadataFilterSerivce.getFormulaByType($scope.filterdata.dependsOn.ref.uuid, $scope.filterdata.dependsOn.ref.type).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
			CommonService.getAllLatest("dataset").then(function (response) { onSuccressGetAllLatestDataset(response.data) });
			var onSuccressGetAllLatestDataset = function (response) {
				$scope.allDataset = response;
			}
			$scope.selectRelation = response.filter.dependsOn.ref.type
			for (var j = 0; j < $scope.filterdata.filterInfo.length; j++) {
				var lhsoperand = {};
				lhsoperand.uuid = $scope.filterdata.filterInfo[j].operand[0].ref.uuid
				lhsoperand.datapodname = $scope.filterdata.filterInfo[j].operand[0].ref.name;
				lhsoperand.name = $scope.filterdata.filterInfo[j].operand[0].attributeName;
				lhsoperand.attributeId = $scope.filterdata.filterInfo[j].operand[0].attributeId;
				$scope.filterdata.filterInfo[j].lhsoperand = lhsoperand;
			}
			$scope.filterName = $scope.convertUppdercase($scope.filterdata.name)
			var tags = [];
			for (var i = 0; i < response.filter.tags.length; i++) {
				var tag = {};
				tag.text = response.filter.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
	}//End selectVersion



	/*Start  code of SubmitFilter*/
	$scope.submitFilter = function () {
		var upd_tag="N";
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.filterHasChanged = true;
		$scope.myform.$dirty = false;
		var filterJson = {};
		filterJson.uuid = $scope.filterdata.uuid
		filterJson.name = $scope.filterdata.name
		var tagArray = [];
		if($scope.tags){
			if ($scope.tags.length != 0) {
				for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
					tagArray[counttag] = $scope.tags[counttag].text;
				}
				var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
				if(result ==false){
					upd_tag="Y"	
				}
			}
	    }
		filterJson.tags = tagArray
		filterJson.desc = $scope.filterdata.desc
		var dependsOn = {};
		var ref = {}
		ref.type = $scope.selectRelation
		ref.uuid = $scope.filterRelation.defaultoption.uuid
		dependsOn.ref = ref;
		filterJson.dependsOn = dependsOn;
		filterJson.active = $scope.filterdata.active
		filterJson.published = $scope.filterdata.published
		
		
		var filterInfoArray = [];
		if ($scope.filterTableArray.length > 0) {
			for (var i = 0; i < $scope.filterTableArray.length; i++) {
				var  filterInfo  = {};
				var operand = []
				var lhsoperand = {}
				var lhsref = {}
				var rhsoperand = {}
				var rhsref = {};
				if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
					filterInfo.logicalOperator=""
				}
				else{
					filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
				}
				//filterInfo .logicalOperator = $scope.filterTableArray[i].logicalOperator;
				filterInfo .operator = $scope.filterTableArray[i].operator;
				if ($scope.filterTableArray[i].lhstype.text == "string") {

					lhsref.type = "simple";
					lhsoperand.ref = lhsref;
					lhsoperand.value = $scope.filterTableArray[i].lhsvalue//"'"+$scope.filterTableArray[i].lhsvalue+"'";
				}
				else if ($scope.filterTableArray[i].lhstype.text == "datapod") {
					if ($scope.selectRelation == "dataset") {
						lhsref.type = "dataset";
					}
					else {
						lhsref.type = "datapod";
					}
					lhsref.uuid = $scope.filterTableArray[i].lhsdatapodAttribute.uuid;
					lhsoperand.ref = lhsref;
					lhsoperand.attributeId = $scope.filterTableArray[i].lhsdatapodAttribute.attributeId;
				}
				else if ($scope.filterTableArray[i].lhstype.text == "formula") {
					lhsref.type = "formula";
					lhsref.uuid = $scope.filterTableArray[i].lhsformula.uuid;
					lhsoperand.ref = lhsref;
				}
				
				operand[0] = lhsoperand;
				if ($scope.filterTableArray[i].rhstype.text == "string") {
					rhsref.type = "simple";
					rhsoperand.ref = rhsref;
					if( $scope.filterTableArray[i].operator =='BETWEEN'){
						rhsoperand.value = $scope.filterTableArray[i].rhsvalue1+"and"+$scope.filterTableArray[i].rhsvalue2;
					}
					else if($scope.filterTableArray[i].rhstype.caption =='integer'){
						rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
					}
					else if($scope.filterTableArray[i].rhstype.caption="string"){
						rhsoperand.value =$scope.filterTableArray[i].rhsvalue//"'"++"'";
					}
				}
				else if ($scope.filterTableArray[i].rhstype.text == "datapod") {
					if ($scope.selectRelation == "dataset") {
						rhsref.type = "dataset";

					}
					else {
						rhsref.type = "datapod";
					}
					rhsref.uuid = $scope.filterTableArray[i].rhsdatapodAttribute.uuid;

					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.filterTableArray[i].rhsdatapodAttribute.attributeId;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "formula") {

					rhsref.type = "formula";
					rhsref.uuid = $scope.filterTableArray[i].rhsformula.uuid;
					rhsoperand.ref = rhsref;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "dataset") {

					rhsref.type = "dataset";
					rhsref.uuid = $scope.filterTableArray[i].rhsdataset.uuid;
					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.filterTableArray[i].rhsdataset.attributeId;
				}
				operand[1] = rhsoperand;
				 filterInfo .operand = operand;
				 filterInfoArray[i] =  filterInfo 
			}

		}
		
		filterJson.filterInfo = filterInfoArray;
		console.log(JSON.stringify(filterJson))
		MetadataFilterSerivce.submit(filterJson,'filter',upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
		
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Filter Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okfiltersave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitFilter*/

	
	$scope.okfiltersave = function () {
		$('#filtersave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'filter' }); }, 2000);
		}
	}
});
