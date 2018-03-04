/****/
DataQualityModule = angular.module('DataQualityModule');

DataQualityModule.controller('DetailDataQualityController', function($state, $stateParams, $location, $rootScope, $scope, DataqulityService,privilegeSvc) {
  $scope.dataqualitydata = {};
  $scope.mode = "false";
  if($stateParams.mode =='true'){
	$scope.isEdit=false;
	$scope.isversionEnable=false;
	$scope.isAdd=false;
	}
	else if($stateParams.mode =='false'){
	$scope.isEdit=true;
	$scope.isversionEnable=true;
	$scope.isAdd=false;
	}
	else{
	$scope.isAdd=true;
	}
  $scope.dq = {};
  $scope.dq.versions = []
  $scope.dataqualitycompare = null;
  $scope.datatype = ["", "String", "Int", "Float", "Double", "Date"];
  $scope.selectDataType = $scope.datatype[0];
  //$scope.sourceType=["datapod","dataset","relation"]
  $scope.sourceType = ["datapod"]
  $scope.dataqualitysourceType=$scope.sourceType[0];
  $scope.logicalOperator = [" ", "OR", "AND"];
  $scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
  $scope.selectType = true;
  $scope.isDependencyShow = false;
  /* $scope.datefromate=["dd/mm/yy","dd/mm/yyyy","d/m/yy","d/m/yyyy","ddmmyy","ddmmyyy","ddmmmyy","ddmmmyyyy","dd-mmm-yy","dd-mmm-yyyy","dmmmyy","dmmmyyyy","d-mmm-yy","d-mmm-yyyy","d-mmmm-yy","d-mmmm-yyyy","yymmdd","yyyymmdd","yy/mm/dd","yyyy/mm/dd","mmddyy","mmddyyyy","mm/dd/yy","mm/dd/yyyy","mmm-dd-yy","mmm-dd-yyyy","yyyy-mm-dd","dth mmmm yyyy","mmm-yy","yy","yyyy"];
   */
  $scope.isSelectSoureceAttr = false;
  $scope.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
  $scope.showRule = true;
  $scope.showRuleForm = true;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isshowmodel = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['dq'] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['dq'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  $scope.showRulePage = function() {
    $scope.showgraph = false;
    $scope.showRule = true;
    $scope.showRuleForm = true
    $scope.graphDataStatus = false;
    $scope.showgraphdiv = false
  }
  $scope.enableEdit=function (uuid,version) {
    $scope.showRulePage()
    $state.go('createdataquality', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }
  $scope.showview=function (uuid,version) {
    $scope.showRulePage()
    $state.go('createdataquality', {
      id: uuid,
      version: version,
      mode:'true'
    });
  }
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
};

  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('viewdataquality')
    }
  }
  $scope.$watch("isshowmodel", function(newvalue, oldvalue) {
    $scope.isshowmodel = newvalue
    sessionStorage.isshowmodel = newvalue
  })
  $scope.OnselectType = function() {

    if ($scope.selectDataType == "Date") {
      $scope.selectType = false;
    } else {
      $scope.selectType = true;
    }
  }
  $scope.showRuleGraph = function(uuid, version) {

    $scope.showRule = false;
    $scope.showRuleForm = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;
  }




  $scope.countContinue = function() {
    $scope.continueCount = $scope.continueCount + 1;
    if ($scope.continueCount >= 4) {
      $scope.isSubmitShow = true;
    } else {

      $scope.isSubmitShow = false;
    }
  }
  $scope.countBack = function() {

    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }
  DataqulityService.getAllLatestActive("datapod").then(function(response) {
    onSuccess(response.data)
  });
  var onSuccess = function(response) {
    //$scope.allDependsOn=response

    $scope.refIntegrityCheck = response
    $scope.allDependsOn = response
    $scope.selectDependsOn=$scope.allDependsOn[0]
      $scope.dependsOnDataQuality();
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.showactive="true"

    $scope.isSelectSoureceAttr = false
    $scope.isDependencyShow = true;
    DataqulityService.getAllVersionByUuid($stateParams.id, "dq").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var dqversion = {};
        dqversion.version = response[i].version;
        $scope.dq.versions[i] = dqversion;
      }

    }
    DataqulityService.getOneByUuidAndVersionDQView($stateParams.id,$stateParams.version, "dqview").then(function(response) {
      onGetSuccess(response.data)
    });
    var onGetSuccess = function(response) {


      $scope.dataqualitycompare = response.dqdata;
      $scope.dataqualitydata = response.dqdata;
      $scope.tags = response.dqdata.tags;
      var defaultversion = {};
      defaultversion.version = response.dqdata.version;
      defaultversion.uuid = response.dqdata.uuid;
      $scope.uuid = response.uuid;
      $scope.version = response.version;
      $scope.dq.defaultVersion = defaultversion;
      $scope.dataqualitydata.upperBound = response.dqdata.rangeCheck.upperBound;
      $scope.dataqualitydata.lowerBound = response.dqdata.rangeCheck.lowerBound;
      if (response.dqdata.dataTypeCheck != null) {
        $scope.selectDataType = response.dqdata.dataTypeCheck;
      }

      $scope.selectdatefromate = response.dqdata.dateFormatCheck;
      $scope.dataqualitydata.maxLength = response.dqdata.lengthCheck.maxLength
      $scope.dataqualitydata.minLength = response.dqdata.lengthCheck.minLength
      $scope.dataqualitysourceType = response.dqdata.dependsOn.ref.type
      var selectDependsOn = {}
      selectDependsOn.uuid = response.dqdata.dependsOn.ref.uuid;
      selectDependsOn.name = response.dqdata.dependsOn.ref.name
      $scope.selectDependsOn = selectDependsOn
      var selectrefIntegrityCheck = {};
      //alert(response.dqdata.refIntegrityCheck.ref)
      if (response.dqdata.refIntegrityCheck.ref != null) {
        selectrefIntegrityCheck.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
        selectrefIntegrityCheck.name = response.dqdata.refIntegrityCheck.ref.name;
        var refIntegrityCheckoption = {};
        refIntegrityCheckoption.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
        refIntegrityCheckoption.datapodname = response.dqdata.refIntegrityCheck.ref.name;
        refIntegrityCheckoption.name = response.dqdata.refIntegrityCheck.attrName
        refIntegrityCheckoption.attributeId = response.dqdata.refIntegrityCheck.attrId
      }

      if (response.dqdata.attribute != null) {
        $scope.isSelectSoureceAttr = true
        var dataqualityoption = {};
        dataqualityoption.uuid = response.dqdata.attribute.ref.uuid;
        dataqualityoption.datapodname = response.dqdata.attribute.ref.name;
        dataqualityoption.name = response.dqdata.attribute.attrName
        dataqualityoption.attributeId = response.dqdata.attribute.attrId
        $scope.dataqualityoption = dataqualityoption;
      }

      $scope.dataqualityoption = dataqualityoption;
      $scope.refIntegrityCheckoption = refIntegrityCheckoption
      $scope.selectrefIntegrityCheck = selectrefIntegrityCheck
      $scope.filterTableArray = response.filterInfo
      DataqulityService.getAllLatestActive($scope.dataqualitysourceType).then(function(response) {
        onSuccessgetAllLatest(response.data)
      });
      var onSuccessgetAllLatest = function(response) {

        $scope.allDependsOn = response
        DataqulityService.getAllAttributeBySource($scope.selectDependsOn.uuid, $scope.dataqualitysourceType).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          $scope.dataqualityoptions = response;
          $scope.lhsdatapodattributefilter = response;
        }
      }
      if (response.dqdata.refIntegrityCheck.ref != null) {
        DataqulityService.getAttributeByDatapod(response.dqdata.refIntegrityCheck.ref.uuid).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          $scope.refIntegrityCheckoptions = response;
        }
      }
    }
  }
  else{
    $scope.showactive="false"
  }
  $scope.selectVersion = function() {
    $scope.isSelectSoureceAttr = false
    $scope.myform.$dirty = false;
    DataqulityService.getOneByUuidAndVersionDQView($scope.dq.defaultVersion.uuid, $scope.dq.defaultVersion.version, "dqview").then(function(response) {
      onGetSuccess(response.data)
    });
    var onGetSuccess = function(response) {
      $scope.dataqualitycompare = response.dqdata;
      $scope.dataqualitydata = response.dqdata;

      if (response.dqdata.tags.length > 0) {
        $scope.tags = response.dqdata.tags;

      }

      var defaultversion = {};
      defaultversion.version = response.dqdata.version;
      defaultversion.uuid = response.dqdata.uuid;
      $scope.dq.defaultVersion = defaultversion;
      $scope.dataqualitydata.upperBound = response.dqdata.rangeCheck.upperBound;
      $scope.dataqualitydata.lowerBound = response.dqdata.rangeCheck.lowerBound;
      if (response.dqdata.dataTypeCheck != null) {
        $scope.selectDataType = response.dqdata.dataTypeCheck;
      }
      $scope.selectdatefromate = response.dqdata.dateFormatCheck;
      $scope.dataqualitydata.maxLength = response.dqdata.lengthCheck.maxLength
      $scope.dataqualitydata.minLength = response.dqdata.lengthCheck.minLength
      $scope.dataqualitysourceType = response.dqdata.dependsOn.ref.type
      var selectDependsOn = {}
      selectDependsOn.uuid = response.dqdata.dependsOn.ref.uuid;
      selectDependsOn.name = response.dqdata.dependsOn.ref.name
      $scope.selectDependsOn = selectDependsOn
      var selectrefIntegrityCheck = {};
      //alert(response.dqdata.refIntegrityCheck.ref)
      if (response.dqdata.refIntegrityCheck.ref != null) {
        selectrefIntegrityCheck.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
        selectrefIntegrityCheck.name = response.dqdata.refIntegrityCheck.ref.name;
        var refIntegrityCheckoption = {};
        refIntegrityCheckoption.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
        refIntegrityCheckoption.datapodname = response.dqdata.refIntegrityCheck.ref.name;
        refIntegrityCheckoption.name = response.dqdata.refIntegrityCheck.attrName
        refIntegrityCheckoption.attributeId = response.dqdata.refIntegrityCheck.attrId
      }
      DataqulityService.getAllLatestActive($scope.dataqualitysourceType).then(function(response) {
        onSuccessgetAllLatest(response.data)
      });
      var onSuccessgetAllLatest = function(response) {
        $scope.allDependsOn = response
        DataqulityService.getAllAttributeBySource($scope.selectDependsOn.uuid, $scope.dataqualitysourceType).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          //$scope.dataqualityoptions=response;
          $scope.lhsdatapodattributefilter = response;
        }
      }

      if (response.dqdata.attribute != null) {
        $scope.isSelectSoureceAttr = true
        var dataqualityoption = {};
        dataqualityoption.uuid = response.dqdata.attribute.ref.uuid;
        dataqualityoption.datapodname = response.dqdata.attribute.ref.name;
        dataqualityoption.name = response.dqdata.attribute.attrName
        dataqualityoption.attributeId = response.dqdata.attribute.attrId
        $scope.dataqualityoption = dataqualityoption;
        DataqulityService.getAllAttributeBySource(response.dqdata.attribute.ref.uuid, "datapod").then(function(response) {
          onSuccessDataqualityoptions(response.data)
        });
        var onSuccessDataqualityoptions = function(response) {
          $scope.dataqualityoptions = response;
        }

      } else {
        $scope.dataqualityoptions = null;
      }
      $scope.refIntegrityCheckeckoption = refIntegrityCheckoption
      $scope.selectrefIntegrityCheck = selectrefIntegrityCheck
      $scope.filterTableArray = response.filterInfo

      if (response.dqdata.refIntegrityCheck.ref != null) {
        DataqulityService.getAttributeByDatapod(response.dqdata.refIntegrityCheck.ref.uuid).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          $scope.refIntegrityCheckoptions = response;
        }
      }
    }
  }

  /*$scope.OnSourceTypeChange=function(){
	    	$scope.dataqualityoptions=[];
	    	DataqulityService.getAllLatestActive($scope.dataqualitysourceType).then(function(response){onSuccess(response.data)});
		    var onSuccess=function(response){
		    	console.log(response[0].name)
		    	$scope.allDependsOn=response
		    	var selectDependsOn={};
		    	selectDependsOn.uuid=response[0].uuid;
		    	selectDependsOn.name=response[0].name;
		    	selectDependsOn.version=response[0].version;
		    	$scope.selectDependsOcustomFormatCheckn=selectDependsOn
		    	DataqulityService.getAllAttributeBySource($scope.selectDependsOn.uuid,$scope.dataqualitysourceType).then(function(response){onSuccess(response.data)});
			    var onSuccess=function(response){
			    	$scope.dataqualityoptions=response;
			    	$scope.lhsdatapodattributefilter=response;
			    }
		    }
	    }*/
  $scope.dependsOnDataQuality = function() {
    //alert("dfdfdf"+$scope.selectDependsOn.name)
    $scope.dataqualityoptions;
    DataqulityService.getAllAttributeBySource($scope.selectDependsOn.uuid, $scope.dataqualitysourceType).then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {

      $scope.dataqualityoptions = response;
      $scope.lhsdatapodattributefilter = response;
    }
    DataqulityService.getFormulaBytype($scope.selectDependsOn.uuid,$scope.dataqualitysourceType).then(function(response) {
      onSuccessFormula(response.data)
    });
    var onSuccessFormula = function(response) {

      $scope.allFromula = response;
    }
  }

  $scope.onSourceAttributeChagne = function() {

    if ($scope.dataqualityoption != null) {
      $scope.isSelectSoureceAttr = true

      $scope.dataqualitydata.nullCheck = 'Y';

    } else {
      $scope.isSelectSoureceAttr = false

      $scope.dataqualitydata.nullCheck = 'N';
      $scope.dataqualitydata.valueCheck = ""
      $scope.dataqualitydata.lowerBound = "";
      $scope.dataqualitydata.upperBound = "";
      $scope.selectDataType = "";
      $scope.selectdatefromate = "";
      $scope.dataqualitydata.minLength = ""
      $scope.dataqualitydata.maxLength = "";
      $scope.selectrefIntegrityCheck = "";
      $scope.refIntegrityCheckoption = "";
      $scope.dataqualitydata.stdDevCheck = "";
    }

  }

  $scope.checkAllFilterRow = function() {
    angular.forEach($scope.filterTableArray, function(filter) {
      filter.selected = $scope.checkAll;
    });

  }
  $scope.addRowFilter = function() {
    var filterinfo = {};

    if ($scope.filterTableArray == null) {
      $scope.filterTableArray = [];
    }
    filterinfo.logicalOperator = $scope.logicalOperator[0];
    filterinfo.operator = $scope.operator[0];

    filterinfo.lhsFilter = $scope.lhsdatapodattributefilter[0]
    $scope.filterTableArray.splice($scope.filterTableArray.length, 0, filterinfo);


  }
  $scope.removeFilterRow = function() {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.filterTableArray, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    newDataList[0].logicalOperator = "";
    $scope.filterTableArray = newDataList;


  }
  $scope.onRefIntegrityCheck = function() {

    DataqulityService.getAttributeByDatapod($scope.selectrefIntegrityCheck.uuid).then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      $scope.refIntegrityCheckoptions = response;
    }

  }
  $scope.okDQRuleSave = function() {
    $('#dataqualitysave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go('viewdataquality');
      }, 2000);

    }


  }

  $scope.sbumitDataqulity = function() {
    var options = {}
    $scope.dataLoading = true;
    $scope.isshowmodel = true;
    options.execution = $scope.checkboxModelexecution;
    var dataqualityjosn = {}
    if ($scope.dataqualitycompare == null) {
      dataqualityjosn.filterChg = "y";

    }
    dataqualityjosn.uuid = $scope.dataqualitydata.uuid;
    dataqualityjosn.name = $scope.dataqualitydata.name;
    dataqualityjosn.desc = $scope.dataqualitydata.desc;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;

      }
    }
    dataqualityjosn.tags = tagArray;
    dataqualityjosn.active = $scope.dataqualitydata.active
    dataqualityjosn.published = $scope.dataqualitydata.published
    var attributeref = {}
    var attribute = {};
    var ref = {};
    var dependsOn = {};
    ref.type = $scope.dataqualitysourceType;
    ref.uuid = $scope.selectDependsOn.uuid;
    dependsOn.ref = ref;
    dataqualityjosn.dependsOn = dependsOn;

    if (typeof $scope.dataqualityoption != "undefined" && $scope.dataqualityoption != null) {

      attributeref.type = "datapod";
      attributeref.uuid = $scope.dataqualityoption.uuid;
      attribute.ref = attributeref;
      attribute.attrId = $scope.dataqualityoption.attributeId;
      dataqualityjosn.attribute = attribute;
    } else {
      dataqualityjosn.attribute = null;
    }

    dataqualityjosn.duplicateKeyCheck = $scope.dataqualitydata.duplicateKeyCheck
    dataqualityjosn.nullCheck = $scope.dataqualitydata.nullCheck
    var tagArrayvaluecheck = [];
    if ($scope.dataqualitydata.valueCheck != null) {
      for (var counttag = 0; counttag < $scope.dataqualitydata.valueCheck.length; counttag++) {
        tagArrayvaluecheck[counttag] = $scope.dataqualitydata.valueCheck[counttag].text;
      }
    }
    dataqualityjosn.valueCheck = tagArrayvaluecheck
    var rengeCheck = {};
    rengeCheck.lowerBound = $scope.dataqualitydata.lowerBound;
    rengeCheck.upperBound = $scope.dataqualitydata.upperBound;
    dataqualityjosn.rangeCheck = rengeCheck;
    dataqualityjosn.dataTypeCheck = $scope.selectDataType
    dataqualityjosn.dateFormatCheck = $scope.selectdatefromate
    if (typeof $scope.dataqualitydata.CustomFormatCheck != "undefined") {
      dataqualityjosn.customFormatCheck = $scope.dataqualitydata.CustomFormatCheck
    }

    var lengthCheck = {}
    lengthCheck.minLength = $scope.dataqualitydata.minLength;
    lengthCheck.maxLength = $scope.dataqualitydata.maxLength;
    dataqualityjosn.lengthCheck = lengthCheck
    var refIntegrityCheck = {};
    var ref = {};


    if (typeof $scope.refIntegrityCheckoption != "undefined" && $scope.refIntegrityCheckoption != null && $scope.refIntegrityCheckoption != "") {
      ref.type = "datapod";
      ref.uuid = $scope.selectrefIntegrityCheck.uuid;
      refIntegrityCheck.ref = ref;
      refIntegrityCheck.attrId = $scope.refIntegrityCheckoption.attributeId;
      dataqualityjosn.refIntegrityCheck = refIntegrityCheck;
    } else {
      dataqualityjosn.refIntegrityCheck = {};
    }


    //dataqualityjosn.stdDevCheck=$scope.dataqualitydata.stdDevCheck
    var filterInfoArray = [];
    var filter = {}
    if ($scope.dataqualitycompare != null) {
      if ($scope.dataqualitycompare.filter != null) {
        filter.uuid = $scope.dataqualitycompare.filter.uuid;
        filter.name = $scope.dataqualitycompare.filter.name;
        //filter.version=$scope.rulecompare.filter.version;
        filter.createdBy = $scope.dataqualitycompare.filter.createdBy;
        filter.createdOn = $scope.dataqualitycompare.filter.createdOn;
        filter.active = $scope.dataqualitycompare.filter.active;
        filter.tags = $scope.dataqualitycompare.filter.tags;
        filter.desc = $scope.dataqualitycompare.filter.desc;
        filter.dependsOn = $scope.dataqualitycompare.filter.dependsOn;
      }
    }
    if ($scope.filterTableArray != null) {
      if ($scope.filterTableArray.length > 0) {
        for (var i = 0; i < $scope.filterTableArray.length; i++) {

          if ($scope.dataqualitycompare != null && $scope.dataqualitycompare.filter != null && $scope.dataqualitycompare.filter.filterInfo.length == $scope.filterTableArray.length) {
            if ($scope.dataqualitycompare.filter.filterInfo[i].operand[0].attributeId != $scope.filterTableArray[i].lhsFilter.attributeId ||
              $scope.filterTableArray[i].logicalOperator != $scope.dataqualitycompare.filter.filterInfo[i].logicalOperator ||
              $scope.filterTableArray[i].filtervalue != $scope.dataqualitycompare.filter.filterInfo[i].operand[1].value ||
              $scope.filterTableArray[i].operator != $scope.dataqualitycompare.filter.filterInfo[i].operator) {

              dataqualityjosn.filterChg = "y";

            } else {

              dataqualityjosn.filterChg = "n";
            }

          } else {

            dataqualityjosn.filterChg = "y";
          }
          var filterInfo = {};
          var operand = [];
          var operandfirst = {};
          var reffirst = {};
          var operandsecond = {};
          var refsecond = {};
          reffirst.type = "datapod"

          reffirst.uuid = $scope.filterTableArray[i].lhsFilter.uuid
          operandfirst.ref = reffirst;
          operandfirst.attributeId = $scope.filterTableArray[i].lhsFilter.attributeId
          operand[0] = operandfirst;
          refsecond.type = "simple";
          operandsecond.ref = refsecond;
          if (typeof $scope.filterTableArray[i].filtervalue == "undefined") {
            operandsecond.value = "";
          } else {

            operandsecond.value = $scope.filterTableArray[i].filtervalue
          }

          operand[1] = operandsecond;
          if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
            filterInfo.logicalOperator = ""
          } else {
            filterInfo.logicalOperator = $scope.filterTableArray[i].logicalOperator
          }
          filterInfo.operator = $scope.filterTableArray[i].operator
          filterInfo.operand = operand;
          filterInfoArray[i] = filterInfo;
        }
        filter.filterInfo = filterInfoArray;
        dataqualityjosn.filter = filter;
      } else {

        dataqualityjosn.filter = null;
        dataqualityjosn.filterChg = "n";
      }
    } else {
      dataqualityjosn.filter = null;
      dataqualityjosn.filterChg = "n";

    }
    console.log(JSON.stringify(dataqualityjosn))
    DataqulityService.submit(dataqualityjosn, "dqview").then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.changemodelvalue()
      if (options.execution == "YES") {
        DataqulityService.getOneById(response.data, "dq").then(function(response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function(result) {
          DataqulityService.executeDQRule(result.data.uuid, result.data.version).then(function(response) {
            onSuccess(response.data)
          });
          var onSuccess = function(response) {
            $scope.saveMessage = "DQ Rule Saved and Submitted Successfully";
            // if ($scope.isshowmodel == "true") {
            //   $('#dataqualitysave').modal({
            //     backdrop: 'static',
            //     keyboard: false
            //   });
            // } //End Innter If
            notify.type='success',
            notify.title= 'Success',
           notify.content=$scope.saveMessage
           $scope.$emit('notify', notify);
           $scope.okDQRuleSave();
          }
        } /*end onSuccessGetOneById */
      } /*End If*/
      else {
        $scope.saveMessage = "DQ Rule Saved Successfully";
        // if ($scope.isshowmodel == "true") {
        //   $('#dataqualitysave').modal({
        //     backdrop: 'static',
        //     keyboard: false
        //   });
        // } //End Innter If
        notify.type='success',
        notify.title= 'Success',
       notify.content=$scope.saveMessage
       $scope.$emit('notify', notify);
       $scope.okDQRuleSave();
      } //End Else
    } //End Submit Api Function
    var onError = function(response) {
       notify.type='error',
       notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function
  $scope.changemodelvalue = function() {
    $scope.isshowmodel = sessionStorage.isshowmodel
  };
});


DataQualityModule.controller('DetailDataqualityGroupController', function($state, $timeout, $filter,privilegeSvc, $stateParams, $location, $rootScope, $scope, DataqulityService) {
  $scope.select = 'Rule Group';
  //$scope.ruleGroupDetail=null;
  if($stateParams.mode =='true'){
	$scope.isEdit=false;
	$scope.isversionEnable=false;
	$scope.isAdd=false;
	}
	else if($stateParams.mode =='false'){
	$scope.isEdit=true;
	$scope.isversionEnable=true;
	$scope.isAdd=false;
	}
	else{
	$scope.isAdd=true;
	}
  $scope.mode = " ";
  $scope.dqgroup = {};
  $scope.dqgroup.versions = []
  $scope.showRuleGroup = true;
  $scope.showRuleGroupForm = true;
  $scope.isshowmodel = false;
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['dqgroup'] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['dqgroup'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
};
$scope.showRulGroupePage = function() {

  $scope.showRuleGroup = true;
  $scope.showgraphdiv = false;
  $scope.graphDataStatus = false;
  $scope.showRuleGroupForm = true;

}
$scope.enableEdit=function (uuid,version) {
  $scope.showRulGroupePage()
  $state.go('createdataqualitygroup', {
    id: uuid,
    version: version,
    mode:'false'
  });
}
$scope.showview=function (uuid,version) {
  $scope.showRulGroupePage()
  $state.go('createdataqualitygroup', {
    id: uuid,
    version: version,
    mode:'true'
  });
}
  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('viewdataqualitygroup')
    }
  }

  $scope.$watch("isshowmodel", function(newvalue, oldvalue) {
    $scope.isshowmodel = newvalue
    sessionStorage.isshowmodel = newvalue
  })
  DataqulityService.getAllLatest('dq').then(function(response) {
    onSuccess(response.data)
  });
  var onSuccess = function(response) {

    var dqArray = [];
    for (var i = 0; i < response.length; i++) {
      var dqjosn = {};
      dqjosn.uuid = response[i].uuid;
      dqjosn.id = response[i].uuid
      dqjosn.name = response[i].name;
      dqjosn.version = response[i].version;
      dqArray[i] = dqjosn;
    }

    $scope.dqall = dqArray;
  }

  $scope.showRuleGroupGraph = function(uuid, version) {
    $scope.showRuleGroup = false;
    $scope.showgraphdiv = true;
    $scope.graphDataStatus = true;
    $scope.showRuleGroupForm = false;
  }




  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;

    $scope.isDependencyShow = true;
    DataqulityService.getAllVersionByUuid($stateParams.id, "dqgroup").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var dqgroupversion = {};
        dqgroupversion.version = response[i].version;
        $scope.dqgroup.versions[i] = dqgroupversion;
      }

    }
    DataqulityService.getOneByUuidAndVersion1($stateParams.id,$stateParams.version, 'dqgroup').then(function(response) {
      onsuccess(response.data)
    });
    var onsuccess = function(response) {
      //console.log(JSON.stringify(response))
      $scope.dqruleGroupDetail = response;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.dqgroup.defaultVersion = defaultversion;
      $scope.checkboxModelparallel = response.inParallel;

      $scope.uuid = response.uuid;
      $scope.version = response.version;

      $scope.tags = response.tags
      var ruleTagArray = [];
      for (var i = 0; i < response.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.ruleInfo[i].ref.uuid;
        ruletag.name = response.ruleInfo[i].ref.name;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruletag.id = response.ruleInfo[i].ref.uuid

        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
  }

  $scope.selectVersion = function() {
    $scope.myform.$dirty = false;
    DataqulityService.getOneByUuidAndVersion($scope.dqgroup.defaultVersion.uuid, $scope.dqgroup.defaultVersion.version, 'dqgroup').then(function(response) {
      onsuccess(response.data)
    });
    var onsuccess = function(response) {
      //console.log(JSON.stringify(response))
      $scope.dqruleGroupDetail = response.data;
      var defaultversion = {};
      defaultversion.version = response.data.version;
      defaultversion.uuid = response.data.uuid;
      $scope.dqgroup.defaultVersion = defaultversion;
      $scope.tags = response.data.tags
      var ruleTagArray = [];
      for (var i = 0; i < response.data.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.data.ruleInfo[i].ref.uuid;
        ruletag.name = response.data.ruleInfo[i].ref.name;
        ruletag.version = response.data.ruleInfo[i].ref.version;
        ruletag.id = response.data.ruleInfo[i].ref.uuid

        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
  }
  $scope.loadRules = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.dqall, query);
    });
  };
  $scope.clear = function() {

    $scope.ruleTags = null;
  }

  $scope.okDqGroupSave = function() {
    $('#dqrulegroupsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go('viewdataqualitygroup');
      }, 2000);

    }


  }
  $scope.sbumitRuleGroup = function() {
    var dqruleGroupJson = {}
    $scope.dataLoading = true;
    $scope.isshowmodel = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    dqruleGroupJson.uuid = $scope.dqruleGroupDetail.uuid;
    dqruleGroupJson.name = $scope.dqruleGroupDetail.name;
    dqruleGroupJson.desc = $scope.dqruleGroupDetail.desc;
    dqruleGroupJson.active = $scope.dqruleGroupDetail.active;
    dqruleGroupJson.published = $scope.dqruleGroupDetail.published;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    dqruleGroupJson.tags = tagArray;
    var ruleInfoArray = [];
    for (var i = 0; i < $scope.ruleTags.length; i++) {
      var ruleInfo = {}
      var ref = {};
      ref.type = "dq";
      ref.uuid = $scope.ruleTags[i].uuid;
      // ref.version=$scope.ruleTags[i].version;
      ruleInfo.ref = ref;
      ruleInfoArray[i] = ruleInfo;
    }
    dqruleGroupJson.ruleInfo = ruleInfoArray;
    dqruleGroupJson.inParallel = $scope.checkboxModelparallel
    console.log(JSON.stringify(dqruleGroupJson))
    DataqulityService.submit(dqruleGroupJson, "dqgroup").then(function(response) {
      onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.changemodelvalue();

      if (options.execution == "YES") {
        DataqulityService.getOneById(response.data, 'dqgroup').then(function(response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function(response) {
          DataqulityService.executeDQGroup(response.data.uuid, response.data.version).then(function(response) {
            onSuccess(response.data)
          });
          var onSuccess = function(response) {
            $scope.dataLoading = false;
            $scope.saveMessage = "DQ Rule Group Saved and Submitted Successfully"
            // if ($scope.isshowmodel == "true") {
            //   $('#dqrulegroupsave').modal({
            //     backdrop: 'static',
            //     keyboard: false
            //   });
            // } //End Inner If
            notify.type='success',
            notify.title= 'Success',
           notify.content=$scope.saveMessage
           $scope.$emit('notify', notify);
           $scope.okDqGroupSave();
          }
        } //End onSuccessGetOneById
      } //End If
      else {
        $scope.dataLoading = false;
        $scope.saveMessage = "DQ Rule Group Saved Successfully"
        // if ($scope.isshowmodel == "true") {
        //   $('#dqrulegroupsave').modal({
        //     backdrop: 'static',
        //     keyboard: false
        //   });
        // } //End Inner If
        notify.type='success',
        notify.title= 'Success',
       notify.content=$scope.saveMessage
       $scope.$emit('notify', notify);
       $scope.okDqGroupSave();
      } //End Else
    }
    var onError = function(response) {
       notify.type='error',
       notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function

  $scope.changemodelvalue = function() {
    $scope.isshowmodel = sessionStorage.isshowmodel
  };
});


DataQualityModule.controller('ResultDQController', function($http,dagMetaDataService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, NgTableParams, DataqulityService, uuid2,CommonService) {

  $scope.select = $stateParams.type;
  $scope.type = {
    text: $scope.select == 'dqgroupexec' ? 'dqgroup' : 'dq'
  };
  $scope.sortdetail = [];
  $scope.colcount = 0;
  $scope.showprogress = false;
  $scope.isRuleExec = false;
  $scope.isRuleResult = false;
  $scope.zoomSize = 7;
  $scope.isGraphRuleGroupExec = false;
  $scope.isD3RuleEexecGraphShow = false;
  $scope.isD3RGEexecGraphShow = false;
  $scope.currentPage = 1;
  $scope.pageSize = 10;
  $scope.paginationPageSizes = [10, 25, 50, 75, 100],
    $scope.maxSize = 5;
  $scope.bigTotalItems = 175;
  $scope.bigCurrentPage = 1;
  $scope.testgrid = false;
  $scope.filteredRows = [];
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };
  $scope.gridOptions = {
    rowHeight: 40,
    useExternalPagination: true,
    exporterMenuPdf: false,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    useExternalPagination: true,
    enableSorting: true,
    useExternalSorting: true,
    enableFiltering: false,
    enableRowSelection: true,
    enableSelectAll: true,
    enableGridMenu: true,
    fastWatch: true,
    columnDefs: [],
    onRegisterApi: function(gridApi) {
      $scope.gridApiResule = gridApi;
      $scope.filteredRows = $scope.gridApiResule.core.getVisibleRows($scope.gridApiResule.grid);
      $scope.gridApiResule.core.on.sortChanged($scope, function(grid, sortColumns) {
        if (sortColumns.length > 0) {
          $scope.searchRequestId(sortColumns);
        }
      });
    }
  };

  $scope.getGridStyle = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length >0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
    }
    else{
      style['height']="100px";
    }
    return style;
  }
  $scope.refreshData = function() {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
  };

  $scope.selectPage = function(pageNo) {
    $scope.currentPage = pageNo;
  };

  $scope.pageChanged = function() {
    $scope.getResults(null)
    //$log.log('Page changed to: ' + (($scope.currentPage - 1) * $scope.pageSize));
  };

  $scope.onPerPageChange = function() {
    $scope.currentPage = 1;
    $scope.getResults(null)
  }
  $scope.$watch("zoomSize", function(newData, oldData) {
    $scope.$broadcast('zoomChange', newData);
  });

  window.navigateTo = function(url) {
    var state = JSON.parse(url);
    $rootScope.previousState = {
      name: $state.current.name,
      params: $state.params
    };
    var ispresent=false;
    if(ispresent !=true){
      var stateTab={};
      stateTab.route=state.state;
      stateTab.param=state.params;
      stateTab.active=false;
      $rootScope.$broadcast('onAddTab',stateTab);
    }
    $state.go(state.state, state.params);

  }
  $scope.toggleZoom = function() {
    $scope.showZoom = !$scope.showZoom;
  }

  $scope.onClickRuleResult = function() {
    $scope.isRuleExec = true;
    $scope.isRuleResult = false;
    $scope.isD3RuleEexecGraphShow = false;
    if ($scope.type.text == "dqgroup") {
      $scope.isGraphRuleExec = false;
    } else {
      $scope.isRuleTitle = false;
      $scope.isRuleSelect = true;
    }
  }
  $scope.searchRequestId = function(sortColumns) {
    var sortBy = sortColumns[0].name;
    var order = sortColumns[0].sort.direction;
    var result = {};
    result.sortBy = sortBy;
    result.order = order;
    if ($scope.sortdetail.length == 0) {
      var sortobj = {};
      sortobj.uuid = uuid2.newuuid();
      sortobj.colname = sortColumns[0].name;
      sortobj.order = sortColumns[0].sort.direction;
      sortobj.limit = $scope.pageSize;
      $scope.sortdetail[$scope.colcount] = sortobj;
      $scope.colcount = $scope.colcount + 1;
      result.requestId = sortobj.uuid;
      //offset = 0;
    } else {
      var idpresent = "N";
      for (var i = 0; i < $scope.sortdetail.length; i++) {
        if ($scope.sortdetail[i].colname == sortBy && $scope.sortdetail[i].order == order && $scope.sortdetail[i].limit == $scope.pageSize) {
          result.requestId = $scope.sortdetail[i].uuid;
          idpresent = "Y"
          break;
        }
      } //End For
      if (idpresent == "N") {
        var sortobj = {};
        sortobj.uuid = uuid2.newuuid();
        result.requestId = sortobj.uuid;
        sortobj.colname = sortColumns[0].name;
        sortobj.order = sortColumns[0].sort.direction;
        sortobj.limit = $scope.pageSize;
        $scope.sortdetail[$scope.colcount] = sortobj;
        $scope.colcount = $scope.colcount + 1;
        offset = 0;
      } //End Else Innder IF
    } //End IF Inner Else
    console.log(JSON.stringify($scope.sortdetail));
    $scope.showprogress = true;
    $scope.testgrid = false;
    $scope.getResults(result);
  }


  $scope.getResults = function(params) {
    $scope.to = (($scope.currentPage - 1) * $scope.pageSize);
    if ($scope.totalItems < ($scope.pageSize*$scope.currentPage)) {
      $scope.from = $scope.totalItems;
    } else {
      $scope.from = (($scope.currentPage) * $scope.pageSize);
    }
    $scope.gridOptions.columnDefs = [];
    var uuid = $scope.dqexecdetail.uuid
    var version = $scope.dqexecdetail.version;
    var limit = $scope.pageSize;
    var offset;
    var requestId;
    var sortBy;
    var order;
    if (params == null) {
      offset = (($scope.currentPage - 1) * $scope.pageSize) //(($scope.pagination.pageNumber - 1) * $scope.pagination.pageSize);
      requestId = "";
      sortBy = null
      order = null;
    } else {
      offset = 0;
      requestId = params.requestId;
      sortBy = params.sortBy
      order = params.order;

    }
    DataqulityService.getDataQualResults(uuid, version, offset || 0, limit, requestId, sortBy, order).then(function(response) {
      getResult(response.data)
    },function(response){OnError(response.data)});
    var getResult = function(response) {
      $scope.isDataError = false;
      angular.forEach(response.data[0], function(value, key) {
        var attribute = {};
        if (key == "rownum") {
          attribute.visible = false
        } else {
          attribute.visible = true
        }
        attribute.name = key
        attribute.displayName = key
        attribute.width = key.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
        $scope.gridOptions.columnDefs.push(attribute)
      });
      $scope.gridOptions.data = response.data;
      $scope.originalData = response.data;
      $scope.testgrid = true;
      $scope.showprogress = false;
    }
    var OnError=function (response){
      $scope.showprogress = false;
      $scope.isDataError = true;
      $scope.datamessage = "Some Error Occurred"
    }
  }

  window.showResult = function(params) {
    App.scrollTop();
    $scope.testgrid=false;
    $scope.selectGraphRuleExec = params.name
    $scope.isGraphRuleExec = true;
    $scope.isRuleGroupTitle = true;
    $scope.isRuleTitle = false;
    $scope.getDqExec({
      uuid: params.id,
      version: params.version
    });
  }


  $scope.refreshResultFunction = function() {
    $scope.isD3RuleEexecGraphShow = false;
    $scope.testgrid=false;
    $scope.getDqExec($scope.ruleexecdetail)
  }
  $scope.ruleExecshowGraph = function() {
    $scope.isD3RuleEexecGraphShow = true;
  }

  $scope.getDqExec = function(data) {
    $scope.ruleexecdetail = data
    $scope.isRuleResult = true;
    $scope.isRuleExec = false;
    $scope.isRuleSelect = false;
    $scope.isData = false;
    $scope.showprogress = true;
    if ($scope.type.text == 'dqgroup') {
      $scope.isRuleGroupTitle = true;
    } else {
      $scope.isRuleTitle = true;
      $scope.isRuleGroupTitle = false;
    }
    var dqexecjson = {}
    dqexecjson.uuid = data.uuid;
    dqexecjson.version = data.version;
    $scope.ruledata = data.name;
    $scope.dqexecdetail = dqexecjson
    DataqulityService.getNumRowsbyExec(data.uuid, data.version, "dqexec").then(function(response) {
      onSuccessGetNumRowsbyExec(response.data)
    });
    var onSuccessGetNumRowsbyExec = function(response) {
      $scope.totalItems = response.numRows;
      $scope.getResults(null);
    }
  } //End getDqExec Method

  $scope.refreshRuleGroupExecFunction = function() {
    $scope.isD3RGEexecGraphShow = false;
    $scope.dqGroupExec($scope.rulegroupdatail);
  }

  $scope.rGExecshowGraph = function() {
    $scope.isGraphRuleGroupExec = false;
    $scope.isD3RGEexecGraphShow = true;
  }

  $scope.dqGroupExec = function(data) {
    if ($scope.type.text == 'dq') {
      $scope.getDqExec(data);
      return;
    }
    $scope.rulegroupdatail = data
    $scope.rGExecUuid = data.uuid;
    $scope.rGExecVersion = data.version;
    $scope.ruleGrpupName = data.name;
    $scope.isRuleGroupExec = false;
    $scope.isRuleSelect = false;
    $scope.isRuleExec = true;
    $scope.isRuleGroupTitle = true;
    if ($scope.type.text == 'dqgroup') {
      $scope.isGraphRuleGroupExec = true;
    } else {
      $scope.isGraphRuleGroupExec = false;
    }
    var params = {
      "id": data.uuid,
      "name": data.name,
      "elementType": "dqgroup",
      "version": data.version,
      "type": "dataQualGroup",
      "typeLabel": "DataQualGroup",
      "url": "dataqual/getdqExecBydqGroupExec?",
      "ref": {
        "type": "dqgroupExec",
        "uuid": data.uuid,
        "version": data.version,
        "name": data.name
      }
    };
    setTimeout(function() {
      $scope.$broadcast('generateGroupGraph', params);
    }, 500);
  } //End dqGroupExec

  $scope.getExec = $scope.dqGroupExec;
  $scope.getExec({
    uuid: $stateParams.id,
    version: $stateParams.version,
    name: $stateParams.name
  });
  $scope.reGroupExecute=function() {
    $('#reExModal').modal({
      backdrop: 'static',
      keyboard: false
    });

  }
  $scope.okReGroupExecute=function () {
    $('#reExModal').modal('hide');
    $scope.executionmsg = "DQ Group Restarted Successfully"
    notify.type='success',
    notify.title= 'Success',
    notify.content=$scope.executionmsg
    $rootScope.$emit('notify', notify);
    CommonService.restartExec("dqgroupExec",$stateParams.id,$stateParams.version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response) {
      //$scope.refreshRuleGroupExecFunction();
    }
    $scope.refreshRuleGroupExecFunction();
  }
  $scope.downloadFile = function(data) {
    
    var uuid = data.uuid;
    var version=data.version;
    var url=$location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url:url+"dataqual/download?action=view&dataQualExecUUID="+uuid+"&dataQualExecVersion="+version,
      responseType: 'arraybuffer'
    }).success(function(data, status, headers) {
      headers = headers();
    
      var filename = headers['x-filename'];
      var contentType = headers['content-type'];
    
      var linkElement = document.createElement('a');
      try {
      var blob = new Blob([data], {
        type: contentType
      });
      var url = window.URL.createObjectURL(blob);
    
      linkElement.setAttribute('href', url);
      linkElement.setAttribute("download", uuid+".xlsx");
    
      var clickEvent = new MouseEvent("click", {
        "view": window,
        "bubbles": true,
        "cancelable": false
      });
      linkElement.dispatchEvent(clickEvent);
      } catch (ex) {
      console.log(ex);
      }
    }).error(function(data) {
      console.log(data);
    });
    };
}); //End DQRuleResultController
