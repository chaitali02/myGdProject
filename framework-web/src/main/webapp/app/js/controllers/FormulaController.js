
MetadataModule = angular.module('MetadataModule');
MetadataModule.controller('MetadataFormulaController', function ($state,$timeout,$filter,$scope,$stateParams, $rootScope, CommonService, MetadataSerivce, MetadataFormulaSerivce, $sessionStorage, privilegeSvc) {
	$scope.isDependonDisabled = false;
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
	$scope.formulaTypes=['simple','aggr','custom']
	$scope.mode = "false"
	$scope.formuladata;
	$scope.showFrom = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.isSourceAtributeDatapod = true;
	$scope.formula = {};
	$scope.formula.versions = [];
	$scope.depandsOnTypes = ["datapod", "relation", 'dataset','rule', 'paramlist'];
	$scope.isDependencyShow = false;

	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['formula'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['formula'] || [];
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
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
	}
	
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListformula', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.formuladata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListformula', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('metaListformula', {
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

	$scope.formulafuction = [
		{ "type": "simple", "value": "+", "class": "frormula-btn formula_button btn btn-icon-only" },
		{ "type": "simple", "value": "-", "class": "formula_button btn btn-icon-only frormula-btn" },
		{ "type": "simple", "value": "*", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "/", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "(", "class": "formula_button btn btn-icon-only" },
		{ "type": "simple", "value": ")", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "%", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "=", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "<", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": ">", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "<=", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": ">=", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": ",", "class": "formula_button btn btn-icon-only " },
		{ "type": "simple", "value": "AND", "class": "formula_function btn " },
		{ "type": "simple", "value": "OR", "class": "formula_function btn " },
		{ "type": "simple", "value": "SUM", "class": "formula_function btn " },
		{ "type": "simple", "value": "MIN", "class": "formula_function btn " },
		{ "type": "simple", "value": "MAX", "class": "formula_function btn ", },
		{ "type": "simple", "value": "COUNT", "class": "formula_function btn " },
		{ "type": "simple", "value": "AVG", "class": "formula_function btn " },
		{ "type": "simple", "value": "CASE", "class": "formula_function btn " },
		{ "type": "simple", "value": "WHEN", "class": "formula_function btn " },
		{ "type": "simple", "value": "ELSE", "class": "formula_function btn " },
		{ "type": "simple", "value": "END", "class": "formula_function btn " },
		{ "type": "simple", "value": "THEN", "class": "formula_function btn " },
		// { "type": "simple", "value": "OVER", "class": "formula_button btn " },
		{ "type": "simple", "value": "IN", "class": "formula_button btn " },
		{ "type": "simple", "value": "ORDER BY", "class": "formula_button btn " },
		{ "type": "simple", "value": "PARTITION BY", "class": "formula_button btn " },
		{ "type": "simple", "value": "BETWEEN", "class": "formula_button btn " },
		{ "type": "simple", "value": " != ", "class": "formula_button btn " },
		
		{ "type": "simple", "value": "ASC", "class": "formula_button btn " },
		{ "type": "simple", "value": "DESC", "class": "formula_button btn  " },
			
	]
	$scope.fornulaHasChanged = true;
	$scope.isshowmodel = false;
	$scope.attributeTypes =
		[{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "expression", "caption": "expression" },
		{ "text": "formula", "caption": "formula" },
		{ "text": "function", "caption": "function" },
	    { "text": "paramlist", "caption": "paramlist" }]
	$scope.attributeType = $scope.attributeTypes[1]
	
	$scope.formulaFormChange = function () {
		if ($scope.mode == "true") {
			$scope.formulaHasChanged = true;
		}
		else {
			$scope.formulaHasChanged = false;
		}

	}
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		//console.log(fromParams)
		if (fromState.name != "matadata") {
			$sessionStorage.fromParams = fromParams
		}

	});
	
	$scope.onMouseEnterTitle=function(type,index){
		if(['function','simple'].indexOf(type) == -1)
        $scope.formulainfoarray[index].title="dbclick to edit"
	}
	$scope.onDbclcikEdit=function(type,index){
		if(!$scope.isEdit && !$scope.isAdd ){
			return false;
		}
		//console.log($scope.formulainfoarray[index]);
	 	if(["datapod",'dataset','rule','paramlist'].indexOf(type) != -1){
			$scope.attributeinfo={};
			var type={};
	 	    type.text="datapod";
		    $scope.attributeType= type;
			var attributeInfo={}
			 attributeInfo.uuid=$scope.formulainfoarray[index].uuid
			 attributeInfo.type=$scope.formulainfoarray[index].type

			attributeInfo.attributeId=$scope.formulainfoarray[index].attrId;
			attributeInfo.dname= $scope.formulainfoarray[index].value;
			setTimeout(function(){ $scope.attributeinfo=attributeInfo; },10);
			$scope.DblClcikEditDetail={};
			$scope.DblClcikEditDetail.isEdit=true;
			$scope.DblClcikEditDetail.index=index; 
		}
		else if (type == "string" || type == "simple"){
			var type={};
	 	    type.text="string"//$scope.formulainfoarray[index].type;
		    $scope.attributeType= type;
			$scope.sourcesimple=$scope.formulainfoarray[index].value;
			$scope.DblClcikEditDetail={};
			$scope.DblClcikEditDetail.isEdit=true;
			$scope.DblClcikEditDetail.index=index; 
		}else{
			var type={};
	 	    type.text=$scope.formulainfoarray[index].type;
			$scope.attributeType= type;
			$scope.sourcefunction={}
			$scope.sourcefunction.uuid=$scope.formulainfoarray[index].uuid;
			$scope.sourcefunction.name=$scope.formulainfoarray[index].name;
			$scope.DblClcikEditDetail={};
			$scope.DblClcikEditDetail.isEdit=true;
			$scope.DblClcikEditDetail.index=index; 
		}
		$scope.onChangeAttribute(type.text);
		
		 
	};

	$scope.onChangeName = function (data) {
		$scope.statename
		$scope.formulaName = data;
		$scope.formuladata.displayName=data;
	}

	$scope.formulainfoarray = [];
	
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.showGraphDiv = true;

	}

	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}
	
	
    $scope.getAllLatestParamListByTemplate=function(){
		CommonService.getAllLatestParamListByTemplate('Y', "paramlist","").then(function (response) {
			onSuccessGetAllLatestParamListByTemplate(response.data)
		});
		var onSuccessGetAllLatestParamListByTemplate = function (response) {
			$scope.allformuladepands={};
			$scope.allformuladepands.options = response;
			$scope.allformuladepands.defaultoption=response[0];
			if(typeof $stateParams.id != "undefined"){
				var defaultoption = {};
				defaultoption.uuid = $scope.formuladata.dependsOn.ref.uuid;
				defaultoption.name = $scope.formuladata.dependsOn.ref.name;
				$scope.allformuladepands.defaultoption = defaultoption;
			}
			MetadataFormulaSerivce.getAllAttributeBySource($scope.allformuladepands.defaultoption.uuid, $scope.selectedDependsOnType).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (resposne) {
				$scope.allAttribute = resposne;
			}
		}
	}

	$scope.onChangedepandsOnTypes = function () {
		$scope.formulainfoarray=[];
		if($scope.selectedDependsOnType =="paramlist"){
			$scope.getAllLatestParamListByTemplate();
			
		}
		else{
			MetadataFormulaSerivce.getAllLatest($scope.selectedDependsOnType).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {
				$scope.allformuladepands = response
				MetadataFormulaSerivce.getAllAttributeBySource($scope.allformuladepands.defaultoption.uuid, $scope.selectedDependsOnType).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
				var onSuccessGetAllAttributeBySource = function (resposne) {
					$scope.allAttribute = resposne;
				}
			}
	    }
	}
	
	$scope.getParamByApp=function(){
		CommonService.getParamByApp($rootScope.appUuidd || "", "application").
		then(function (response) { onSuccessGetParamByApp(response.data)});
		var onSuccessGetParamByApp=function(response){
		  $scope.lodeParamlist=[];
		  if(response.length >0){
			var paramsArray = [];
			for(var i=0;i<response.length;i++){
			  var paramjson={}
			  var paramsjson = {};
			  paramsjson.uuid = response[i].ref.uuid;
			  paramsjson.name = response[i].ref.name + "." + response[i].paramName;
			  paramsjson.dname = response[i].ref.name + "." + response[i].paramName;
			  paramsjson.attributeId = response[i].paramId;
			  paramsjson.attrType = response[i].paramType;
			  paramsjson.paramName = response[i].paramName;
			  paramsjson.caption = "app."+paramsjson.paramName;
			  paramsArray[i] = paramsjson
			}
			$scope.lodeParamlist=paramsArray;
		  }
		}
	  }
	$scope.getParamByApp();
	$scope.selectDependson = function () {
		$scope.formulainfoarray=[];
		MetadataFormulaSerivce.getAllAttributeBySource($scope.allformuladepands.defaultoption.uuid, $scope.selectedDependsOnType).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
		var onSuccessGetAllAttributeBySource = function (resposne) {
			$scope.allAttribute = resposne;
		}
	}


	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataFormulaSerivce.getAllVersionByUuid($stateParams.id, "formula")
			.then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var formulaversion = {};
				formulaversion.version = response[i].version;
				$scope.formula.versions[i] = formulaversion;
			}
		}
		MetadataFormulaSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'formula')
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			$scope.formuladata = response.formuladata
			$scope.formulainfoarray = response.formulainfoarray
			$scope.formulaName = $scope.convertUppdercase($scope.formuladata.name)
			defaultversion.version = response.formuladata.version;
			defaultversion.uuid = response.formuladata.uuid;
			$scope.formula.defaultVersion = defaultversion;
			$scope.selectedDependsOnType = response.formuladata.dependsOn.ref.type;
			
			if($scope.selectedDependsOnType =="paramlist"){
				$scope.getAllLatestParamListByTemplate();
				
			}else{
				MetadataFormulaSerivce.getAllLatest(response.formuladata.dependsOn.ref.type).then(function (response) { onSuccessGetAllLatest(response.data) });
				var onSuccessGetAllLatest = function (response) {
					$scope.allformuladepands = response
					var defaultoption = {};
					defaultoption.uuid = $scope.formuladata.dependsOn.ref.uuid;
					defaultoption.name = $scope.formuladata.dependsOn.ref.name;
					$scope.allformuladepands.defaultoption = defaultoption;
				}
		    }
			MetadataFormulaSerivce.getAllAttributeBySource($scope.formuladata.dependsOn.ref.uuid, $scope.formuladata.dependsOn.ref.type).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (resposne) {
				$scope.allAttribute = resposne;
			}
			var tags = [];
			if (response.formuladata.tags != null) {
				for (var i = 0; i < response.formuladata.tags.length; i++) {
					var tag = {};
					tag.text = response.formuladata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}

		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	} //End if
	else {
		if ($sessionStorage.fromStateName  && typeof $sessionStorage.fromStateName != "undefined" && $sessionStorage.fromStateName != "metadata" && $sessionStorage.fromStateName != "metaListformula") {
			$scope.showactive = "false"
			$scope.selectedDependsOnType = $sessionStorage.dependon.type
			$scope.isDependonDisabled = true;
			MetadataFormulaSerivce.getAllLatest($scope.selectedDependsOnType).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {
				$scope.allformuladepands = response
				var defaultoption = {};
				defaultoption.uuid = $sessionStorage.dependon.uuid;
				defaultoption.name = "";
				$scope.allformuladepands.defaultoption = defaultoption;
				MetadataFormulaSerivce.getAllAttributeBySource($sessionStorage.dependon.uuid, $sessionStorage.dependon.type).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
				var onSuccessGetAllAttributeBySource = function (resposne) {
					$scope.allAttribute = resposne;

				}
			}
		}//End Inner If
		else{
			$scope.formuladata={};
			$scope.formuladata.locked="N";
		}
	}//End Else

	$scope.clear = function () {
		$scope.formulainfoarray = [];
		$scope.myform.$dirty=true ;
	}

	$scope.undo = function () {
		$scope.formulainfoarray.splice($scope.formulainfoarray.length - 1, 1);
		$scope.myform.$dirty=true ;
	}

	$scope.addData = function (data) {
		if(!$scope.isEdit && !$scope.isAdd ){
			return false;
		}
		$scope.myform.$dirty=true ;
		var aggrfun = ["SUM", "MIN", "MAX", "COUNT", "AVG","OVER"]
		var len = $scope.formulainfoarray.length;
		if (aggrfun.indexOf(data.value) > -1) {
			MetadataFormulaSerivce.getFunctionByFunctionInfo(data.value.toLowerCase()).then(function (response) { onSuccessGetFunctionByFunctionInfo(response.data) });
			var onSuccessGetFunctionByFunctionInfo = function (response) {
				console.log(JSON.stringify(response))
				var fundata = {};
				fundata.type = "function"
				fundata.value = response[0].functionInfo[0].name.toUpperCase();
				fundata.uuid = response[0].uuid;
				fundata.category = response[0].category
				$scope.formulainfoarray[len] = fundata;
			}
		}
		else {
			$scope.formulainfoarray[len] = data;

		}

	}

	$scope.addAttribute = function () {
		var len = $scope.formulainfoarray.length;
		if($scope.DblClcikEditDetail !=null){
			len =$scope.DblClcikEditDetail.index;
		}
		var data = {};
		if ($scope.attributeType.text == "datapod") {
			if ($scope.attributeinfo != null) {
				data.type =$scope.attributeinfo.type
				data.value = $scope.attributeinfo.dname
				data.uuid = $scope.attributeinfo.uuid;
				data.attrId = $scope.attributeinfo.attributeId;
				$scope.attributeinfo = null;
			}
		}
		else if ($scope.attributeType.text == "string") {
			data.type = "string"
			data.value = $scope.sourcesimple
			$scope.sourcesimple = null
		}

		else if ($scope.attributeType.text == "formula") {
			data.type = $scope.attributeType.text
			data.value = $scope.sourceformula.name
			data.uuid = $scope.sourceformula.uuid;
			data.formulatype = $scope.sourceformula.formulaType;
			$scope.sourceformula = null;
		}
		else if ($scope.attributeType.text == "expression") {
			data.type = $scope.attributeType.text
			data.value = $scope.sourceexpression.name
			data.uuid = $scope.sourceexpression.uuid;
			$scope.sourceexpression = null;
		}
		else if ($scope.attributeType.text == "function") {
			CommonService.getOneByUuidAndVersion($scope.sourcefunction.uuid, $scope.sourcefunction.version, 'function').then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				//console.log(JSON.stringify($scope.sourcefunction))
				data.type = $scope.attributeType.text
				data.value = response.functionInfo[0].name.toUpperCase();
				data.uuid = $scope.sourcefunction.uuid;
				data.category = response.category
				$scope.sourcefunction = null;
			}
		}
		else if($scope.attributeType.text == "paramlist"){
			
			data.type = $scope.attributeType.text
			data.value = $scope.sourceparamlist.dname
			data.uuid = $scope.sourceparamlist.uuid;
			data.attrId = $scope.sourceparamlist.attributeId;
			$scope.sourceparamlist = null;
		}
		$scope.formulainfoarray[len] = data;
		$scope.DblClcikEditDetail=null;
	}

	$scope.onChangeAttribute = function (type) {
		if (type == "string") {
			$scope.isSourceAtributeSimple = true;
			$scope.isSourceAtributeDatapod = false;
			$scope.isSourceAtributeFormula = false;
			$scope.isSourceAtributeExpression = false;
			$scope.isSourceAtributeFunction = false;
			$scope.isSourceAtributeParamlist = false;
		}
		else if (type == "datapod") {
			$scope.isSourceAtributeSimple = false;
			$scope.isSourceAtributeDatapod = true;
			$scope.isSourceAtributeFormula = false;
			$scope.isSourceAtributeExpression = false;
			$scope.isSourceAtributeFunction = false;
			$scope.isSourceAtributeParamlist = false;
		}
		else if (type == "formula") {
			$scope.isSourceAtributeSimple = false;
			$scope.isSourceAtributeDatapod = false;
			$scope.isSourceAtributeFormula = true;
			$scope.isSourceAtributeExpression = false;
			$scope.isSourceAtributeFunction = false;
			$scope.isSourceAtributeParamlist = false;
			MetadataFormulaSerivce.getFormulaByType($scope.allformuladepands.defaultoption.uuid, $scope.selectedDependsOnType).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				var temp;
				temp=response.data
				if($scope.formuladata){
					temp = response.data.filter(function(el) {
						return el.uuid !== $scope.formuladata.uuid;
					});
			    }
				$scope.formulaLodeFormula = temp
			}
		}
		else if (type == "expression") {
			$scope.isSourceAtributeSimple = false;
			$scope.isSourceAtributeDatapod = false;
			$scope.isSourceAtributeFormula = false;
			$scope.isSourceAtributeExpression = true;
			$scope.isSourceAtributeFunction = false;
			$scope.isSourceAtributeParamlist = false;
			MetadataFormulaSerivce.getExpressionByType($scope.allformuladepands.defaultoption.uuid, $scope.selectedDependsOnType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.formulaLodeExpression = response
			}
		}
		else if (type == "function") {
			$scope.isSourceAtributeSimple = false;
			$scope.isSourceAtributeDatapod = false;
			$scope.isSourceAtributeFormula = false;
			$scope.isSourceAtributeExpression = false;
			$scope.isSourceAtributeFunction = true;
			$scope.isSourceAtributeParamlist = false;
			MetadataFormulaSerivce.getAllLatestFunction("function", 'Y').then(function (response) { onSuccessFunction(response.data) });
			var onSuccessFunction = function (response) {
				$scope.ruleLodeFunction = response
			}
		}
		else if (type == "paramlist") {
			$scope.isSourceAtributeSimple = false;
			$scope.isSourceAtributeDatapod = false;
			$scope.isSourceAtributeFormula = false;
			$scope.isSourceAtributeExpression = false;
			$scope.isSourceAtributeFunction = false;
			$scope.isSourceAtributeParamlist = true;
			MetadataFormulaSerivce.getParamByParamList($scope.allformuladepands.defaultoption.uuid,"paramlist").then(function (response) { onSuccessParamlist(response.data) });
			var onSuccessParamlist = function (response) {
				if($scope.lodeParamlist && $scope.lodeParamlist.length ==0){
					$scope.lodeParamlist = response;
				}else if($scope.lodeParamlist.length >0  && $scope.lodeParamlist[0].uuid != response[0].uuid){
					for(var i=0;i<response.length;i++){
						var paramjson={}
						var paramsjson = {};
						paramsjson.uuid = response[i].uuid;
						paramsjson.name = response[i].name 
						paramsjson.dname = response[i].datapodname+"."+response[i].dname;
						paramsjson.attributeId = response[i].attributeId;
						paramsjson.attrType = response[i].paramType;
						paramsjson.paramName = response[i].paramName;
						paramsjson.caption = "formula."+paramsjson.paramName;
						$scope.lodeParamlist.push(paramsjson);
					}
				}
			}
		}
	}

	$scope.getFunctionInfo = function (index, data) {
		if (data.type == "function") {
			MetadataFormulaSerivce.getLatestByUuidForFunction(data.uuid, 'function').then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.formulainfoarray[index].value = response.functionInfo[0].name.toUpperCase();
				$scope.formulainfoarray[index].category=response.category;
			}
		}
	}

	$scope.selectVersion = function () {
		$scope.allformuladepands = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataFormulaSerivce.getOneByUuidAndVersion($scope.formula.defaultVersion.uuid, $scope.formula.defaultVersion.version, 'formula')
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			$scope.formuladata = response.formuladata
			$scope.formulainfoarray = response.formulainfoarray
			$scope.formulaName = $scope.convertUppdercase($scope.formuladata.name)
			defaultversion.version = response.formuladata.version;
			defaultversion.uuid = response.formuladata.uuid;
			$scope.formula.defaultVersion = defaultversion;
			$scope.selectedDependsOnType = response.formuladata.dependsOn.ref.type
			MetadataFormulaSerivce.getAllLatest(response.formuladata.dependsOn.ref.type)
				.then(function (response) { onSuccessGetAllLatest(response.data) });
			var onSuccessGetAllLatest = function (response) {
				$scope.allformuladepands = response
				var defaultoption = {};
				defaultoption.uuid = $scope.formuladata.dependsOn.ref.uuid;
				defaultoption.name = $scope.formuladata.dependsOn.ref.name;
				$scope.allformuladepands.defaultoption = defaultoption;
			}
			MetadataFormulaSerivce.getAllAttributeBySource($scope.formuladata.dependsOn.ref.uuid, $scope.formuladata.dependsOn.ref.type)
				.then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (resposne) {
				$scope.allAttribute = resposne;
			}
			var tags = [];
			if (response.formuladata.tags != null) {
				for (var i = 0; i < response.formuladata.tags.length; i++) {
					var tag = {};
					tag.text = response.formuladata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End selectVersion



	$scope.submitFormula = function () {
		var aggrfun = ["sum", "min", "max", "count", "avg"]
		var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.formulaHasChanged = true;
		$scope.myform.$dirty = false;
		var formulaJson = {};
		formulaJson.formulaType = "simple"
		formulaJson.uuid = $scope.formuladata.uuid;
		formulaJson.name = $scope.formuladata.name;
		formulaJson.displayName = $scope.formuladata.displayName;
		formulaJson.publicFlag = $scope.formuladata.publicFlag;

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
		formulaJson.tags = tagArray
		formulaJson.desc = $scope.formuladata.desc
		var dependsOn = {};
		var ref = {}
		ref.type = $scope.selectedDependsOnType
		ref.uuid = $scope.allformuladepands.defaultoption.uuid
		dependsOn.ref = ref;
		formulaJson.dependsOn = dependsOn;
		formulaJson.active = $scope.formuladata.active;
		formulaJson.locked = $scope.formuladata.locked;
		formulaJson.published = $scope.formuladata.published
		var formulaArray = [];
		if ($scope.formulainfoarray.length > 0) {
			for (var i = 0; i < $scope.formulainfoarray.length; i++) {
				var formulainfo = {}
				var ref = {};
				if ($scope.formulainfoarray[i].type == "simple") {
					if (aggrfun.indexOf($scope.formulainfoarray[i].value.toLowerCase()) > -1) {
						formulaJson.formulaType = "aggr"
					}
					ref.type = $scope.formulainfoarray[i].type;
					formulainfo.ref = ref;
					formulainfo.value = $scope.formulainfoarray[i].value;
				}
				else if ($scope.formulainfoarray[i].type == "string") {
					ref.type = "simple";
					formulainfo.ref = ref;
					formulainfo.value = $scope.formulainfoarray[i].value;
				}
			
				else if ($scope.formulainfoarray[i].type == "datapod" || $scope.formulainfoarray[i].type == "dataset" || $scope.formulainfoarray[i].type == "rule") {
					if ($scope.selectedDependsOnType == "dataset") {
						ref.type = "dataset";
					}
					else if($scope.selectedDependsOnType == "rule"){
						ref.type = "rule";
					}
					else {
						ref.type = $scope.formulainfoarray[i].type;
					}
					ref.uuid = $scope.formulainfoarray[i].uuid;
					formulainfo.ref = ref;
					formulainfo.attributeId = $scope.formulainfoarray[i].attrId;
				}
				else if ($scope.formulainfoarray[i].type == "paramlist") {
						
					ref.type = $scope.formulainfoarray[i].type;
					ref.uuid = $scope.formulainfoarray[i].uuid;
					formulainfo.ref = ref;
					formulainfo.attributeId = $scope.formulainfoarray[i].attrId;
					formulaJson.formulaType = "custom"
				}
				else {
					if ($scope.formulainfoarray[i].type == "formula") {
						if (formulaJson.formulaType != "aggr")
							formulaJson.formulaType = $scope.formulainfoarray[i].formulatype;
					}
					if ($scope.formulainfoarray[i].type == "function") {
						if ($scope.formulainfoarray[i].category == "AGGREGATE") {
							formulaJson.formulaType = "aggr"
						}
					}
					ref.type = $scope.formulainfoarray[i].type;
					ref.uuid = $scope.formulainfoarray[i].uuid;
					formulainfo.ref = ref;
				}
				formulaArray[i] = formulainfo;
			}
			formulaJson.formulaInfo = formulaArray;
		}


		console.log(JSON.stringify(formulaJson))
		MetadataFormulaSerivce.submit(formulaJson,'formula',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			var dependon = {}
			dependon.type = "formula";
			dependon.id = response;
			$sessionStorage.dependon = dependon;
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.formulaHasChanged = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Formula Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okdatasetsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End Submit

	

	$scope.okdatasetsave = function () {
		$scope.stageName = $sessionStorage.fromStateName;
		$scope.stageParams = $sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
		$('#formulasave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			if ($scope.stageName == "metaListformula") {
				setTimeout(function () { $state.go('metadata', { 'type': 'formula' }); }, 2000);
			}
			else if ($scope.stageName != "metadata" && typeof $scope.stageParams.id != "undefined") {
				setTimeout(function () { $state.go($scope.stageName, { 'id': $scope.stageParams.id, 'version': $scope.stageParams.version, 'mode': $scope.stageParams.mode }); }, 2000);
			}
			else {
				setTimeout(function () { $state.go('metadata', { 'type': 'formula' }); }, 2000);
			}
		}
	}
})//end of formula
