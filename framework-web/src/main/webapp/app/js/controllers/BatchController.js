BatchModule = angular.module('BatchModule');

BatchModule.controller('DetailBatchController', function($state, $timeout, $filter, $stateParams, $rootScope, $scope, BatchService,privilegeSvc,dagMetaDataService,CommonService,CF_META_TYPES) {
  $scope.moment = moment();
 // console.log( moment.tz.names() )
  $scope.moment.locale('fr-FR');
  $scope.select = 'batch';
  $scope.myArrayOfDates = [];
  $scope.myArrayOfquarters=[];
  $scope.myArrayOfHours=[13];
  $scope.tz = localStorage.serverTz;
  var matches = $scope.tz.match(/\b(\w)/g);
  $scope.timezone = matches.join('')
  $scope.WeekArray = [1,2];
  $scope.isDestoryState = false; 
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
  
  var batchScope=$scope;
  $scope.frequencyTypes=[{"text":"ONCE","caption":"Once"},{"text":"DAILY","caption":"Daily"},{"text":"HOURLY","caption":"Hourly"},{"text":"WEEKLY","caption":"Weekly"},{"text":"BIWEEKLY","caption":"Bi-Weekly"},{"text":"MONTHLY","caption":"Monthly"},{"text":"QUARTERLY","caption":"Quarterly"},{"text":"YEARLY","caption":"Yearly"}];
  $scope.weekNumToDays={"0":"SUN","1":"MON","2":"TUE","3":"WED","4":"THU","5":"FRI","6":"SAT"};
  $scope.weekDaysToNum={"SUN":"0","MON":"1","TUE":"2","WED":"3","THU":"4","FRI":"5","SAT":"6"};
  $scope.numToQuarterly={"0":"Q1","1":"Q2","2":"Q3","3":"Q4"};
  $scope.quarterlyToNum={"Q1":"0","Q2":"1","Q3":"2","Q4":"3"};
  $scope.showForm = true;
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.mode = " ";
  $scope.batch = {};
  $scope.batch.versions = []
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.scheduleTableArray;
  $scope.popoverIsOpen=false;
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
			$scope.lobTag=response[0].value
		}
  }
  
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
  };
  
  $scope.getLovByType();

  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });

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
  $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
  }
  $scope.showPage = function() {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showForm = true;
    $scope.showGraphDiv = false;
  
  }
  $scope.showHome=function(uuid, version,mode){
    if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].detailState, {
			id: uuid,
			version: version,
			mode: mode
		});
	}
  $scope.showGraph = function(uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit=function (uuid,version) {
    if($scope.isPrivlage || $scope.batchDetail.locked =="Y"){
      return false;
    }
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showPage()
    $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].detailState, {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go(dagMetaDataService.elementDefs['batch'].detailState, {
        id: uuid,
        version: version,
        mode:'true'
      });
   }
  }

  $scope.clear=function(){
    $scope.metaTags=null;
  }

  $scope.isSelectable=function(date,type,index){
    var result=false;
    if($scope.mode !="true"){
      result=true;
    }
   // console.log(date)
    return result;
  }

  $scope.closeFrequencyDetailDOW=function(index){
    $scope.WeekArray=[];
    $scope.scheduleTableArray[index].domPopoverIsOpen=false;
  }

  $scope.doneFrequencyDetailDOW=function(index){
    if($scope.scheduleTableArray[index].domPopoverIsOpen ==true){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
   //   console.log($scope.WeekArray)
      for(var i=0;i<$scope.WeekArray.length;i++){
        $scope.scheduleTableArray[index].frequencyDetail[i]=$scope.weekNumToDays[$scope.WeekArray[i]];
      }
      $scope.scheduleTableArray[index].scheduleChg="Y";
    //  console.log($scope.scheduleTableArray[index].frequencyDetail)
      
    }else{
      $scope.WeekArray=[];
    //  console.log($scope.scheduleTableArray[index].frequencyDetail)
      for(var i=0;i<$scope.scheduleTableArray[index].frequencyDetail.length;i++){
       $scope.WeekArray[i]= Number($scope.weekDaysToNum[$scope.scheduleTableArray[index].frequencyDetail[i]]);
      }
    }

    for(var k=0;k<$scope.scheduleTableArray.length;k++){
      $scope.scheduleTableArray[k].domPopoverIsOpen=false;
      $scope.scheduleTableArray[index].domPopoverIsOpen=true;
    }
    $scope.scheduleTableArray[index].domPopoverIsOpen=!$scope.scheduleTableArray[index].domPopoverIsOpen;
  }
  $scope.closeFrequencyDetailQuarterly=function(index){
    $scope.WeekArray=[];
    $scope.scheduleTableArray[index].quarterlyPopoverIsOpen=false;
  }

  $scope.doneFrequencyDetailQuarterly=function(index){
    if($scope.scheduleTableArray[index].quarterlyPopoverIsOpen ==true){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
      for(var i=0;i<$scope.myArrayOfquarters.length;i++){
        $scope.scheduleTableArray[index].frequencyDetail[i]=$scope.numToQuarterly[$scope.myArrayOfquarters[i]]
      }
      $scope.scheduleTableArray[index].scheduleChg="Y";
    //  console.log($scope.scheduleTableArray[index].frequencyDetail)
      
    }else{
      $scope.myArrayOfquarters=[];
      console.log($scope.scheduleTableArray[index].frequencyDetail)
      for(var i=0;i<$scope.scheduleTableArray[index].frequencyDetail.length;i++){
       $scope.myArrayOfquarters[i]= Number($scope.quarterlyToNum[$scope.scheduleTableArray[index].frequencyDetail[i]]);
      }
    }

    for(var k=0;k<$scope.scheduleTableArray.length;k++){
      $scope.scheduleTableArray[k].quarterlyPopoverIsOpen=false;
      $scope.scheduleTableArray[index].quarterlyPopoverIsOpen=true;
    }
    $scope.scheduleTableArray[index].quarterlyPopoverIsOpen=!$scope.scheduleTableArray[index].quarterlyPopoverIsOpen;
  }
  
  $scope.closeFrequencyDetailHourly=function(index){
    $scope.WeekArray=[];
    $scope.scheduleTableArray[index].hourlyPopoverIsOpen=false;
  }

  $scope.doneFrequencyDetailHourly=function(index){
    if($scope.scheduleTableArray[index].hourlyPopoverIsOpen ==true){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
      $scope.myArrayOfHours=$scope.myArrayOfHours.sort(function(a, b){return a-b});
      for(var i=0;i<$scope.myArrayOfHours.length;i++){
        $scope.scheduleTableArray[index].frequencyDetail[i]=$scope.myArrayOfHours[i];
      }
      $scope.scheduleTableArray[index].scheduleChg="Y";
    //  console.log($scope.scheduleTableArray[index].frequencyDetail)
      
    }else{
      $scope.myArrayOfHours=[];
      console.log($scope.scheduleTableArray[index].frequencyDetail)
      for(var i=0;i<$scope.scheduleTableArray[index].frequencyDetail.length;i++){
       $scope.myArrayOfHours[i]=Number($scope.scheduleTableArray[index].frequencyDetail[i]);
      }
    }

    for(var k=0;k<$scope.scheduleTableArray.length;k++){
      $scope.scheduleTableArray[k].hourlyPopoverIsOpen=false;
      $scope.scheduleTableArray[index].hourlyPopoverIsOpen=true;
    }
    $scope.scheduleTableArray[index].hourlyPopoverIsOpen=!$scope.scheduleTableArray[index].hourlyPopoverIsOpen;
  }
  $scope.closeFrequencyDetail=function(index){
    $scope.myArrayOfDates=[];
    $scope.scheduleTableArray[index].popoverIsOpen=false;
  }

  $scope.doneFrequencyDetail=function(index){
   // $scope.scheduleTableArray[index].frequencyDetail=[];
    if($scope.scheduleTableArray[index].popoverIsOpen ==true){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
      for(var i=0;i<$scope.myArrayOfDates.length;i++){
        console.log($scope.myArrayOfDates[i]._d)
        var date=moment($scope.myArrayOfDates[i])._d
        console.log($filter('date')(date, "EEE MMM d y h:mm:ss "))
        $scope.scheduleTableArray[index].frequencyDetail.push($filter('date')(date, "dd"));
        $scope.scheduleTableArray[index].frequencyDetail.sort();
        $scope.scheduleTableArray[index].scheduleChg="Y";
      }
      
    }
    else{
      $scope.myArrayOfDates=[];
      if($scope.scheduleTableArray[index].frequencyDetail){
        for(var i=0;i<$scope.scheduleTableArray[index].frequencyDetail.length;i++){
        //  var dd=$filter('date')(new Date($scope.scheduleTableArray[index].frequencyDetail[i]), "dd");
        //  var mm=$filter('date')(new Date($scope.scheduleTableArray[index].frequencyDetail[i]), "MM");
        //  var yyyy=$filter('date')(new Date($scope.scheduleTableArray[index].frequencyDetail[i]), "yyyy");
          // moment().year(yyyy).month(mm-1).date(dd)
          $scope.myArrayOfDates.push(moment().date($scope.scheduleTableArray[index].frequencyDetail[i]));
        }
        var sd=$filter('date')(new Date($scope.scheduleTableArray[index].startDate), "dd");
        var smm=$filter('date')(new Date($scope.scheduleTableArray[index].startDate), "MM");
        var syyyy=$filter('date')(new Date($scope.scheduleTableArray[index].startDate), "yyyy");
        $scope.scheduleTableArray[index].disable_days_before=moment().date(sd);//moment().year(syyyy).month(smm-1).date(sd);
        var ed=$filter('date')(new Date($scope.scheduleTableArray[index].endDate), "dd");
        var emm=$filter('date')(new Date($scope.scheduleTableArray[index].endDate), "MM");
        var eyyyy=$filter('date')(new Date($scope.scheduleTableArray[index].endDate), "yyyy");
        $scope.scheduleTableArray[index].disable_days_after=moment().date(ed);//moment().year(eyyyy).month(emm-1).date(ed);
      }
    }
    for(var k=0;k<$scope.scheduleTableArray.length;k++){
      $scope.scheduleTableArray[k].popoverIsOpen=false;
      $scope.scheduleTableArray[index].popoverIsOpen=true;
    }
    $scope.scheduleTableArray[index].popoverIsOpen=!$scope.scheduleTableArray[index].popoverIsOpen;
  }

  $scope.onChangeScheduleName=function(index){
    $scope.scheduleTableArray[index].scheduleChg="Y";
  }

  $scope.onChangeFrequencyType=function(index){
    $scope.scheduleTableArray[index].frequencyDetail=[];
    $scope.scheduleTableArray[index].scheduleChg="Y";
  }
  
  $scope.onChangeStartDate=function(newDate,index,isStartDateChange){
    var d=$filter('date')(newDate, "dd");
    var mm=$filter('date')(newDate, "MM");
    var yyyy=$filter('date')(newDate, "yyyy");
    $scope.scheduleTableArray[index].disable_days_before=moment().date(d);//moment().year(yyyy).month(mm-1).date(d);
    if(isStartDateChange=="Y"){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
      $scope.scheduleTableArray[index].scheduleChg="Y";
    }
    $scope.scheduleTableArray[index].isStartDateChange="Y"
  }

  $scope.onChangeEndDate=function(newDate,index,isEndDateChange){
    var d=$filter('date')(newDate, "dd");
    var mm=$filter('date')(newDate, "MM");
    var yyyy=$filter('date')(newDate, "yyyy");
    $scope.scheduleTableArray[index].disable_days_after=moment().date(d);//moment().year(yyyy).month(mm-1).date(d);
    if(isEndDateChange=="Y"){
      $scope.scheduleTableArray[index].frequencyDetail=[];
      $scope.myform.$dirty=true;
      $scope.scheduleTableArray[index].scheduleChg="Y"
    }
    $scope.scheduleTableArray[index].isEndDateChange="Y"
  }
  
 
  $scope.disable6MonthsFromNow = function(event, month){
    if(month.isBefore(moment().subtract(6, 'month'), 'month') || month.isAfter(moment().add(6, 'month'), 'month')){
        event.preventDefault();
    }
  };

  $scope.$watch('myArrayOfDates', function(newValue, oldValue){
    if(newValue){
        console.log('my array changed, new size : ' +  $filter('date')(newValue, "dd/MM/yyyy"));
    }
   }, true);
  
   $scope.selectedAllRow = function () {
		angular.forEach($scope.scheduleTableArray, function (attribute) {
			attribute.selected = $scope.selectAllRow;
		});
	}

  $scope.addRow=function(){
    if($scope.scheduleTableArray ==null){
      $scope.scheduleTableArray=[]; 
    }
    var len=$scope.scheduleTableArray.length;
    var scheduleInfo={};
    scheduleInfo.frequencyType=$scope.frequencyTypes[0].text;
    scheduleInfo.startDate;
    scheduleInfo.scheduleChg="Y";
    scheduleInfo.minDate=$scope.minDate=moment().subtract(new Date(), 'day');
    $scope.scheduleTableArray.splice(len,0,scheduleInfo);
  }

  $scope.removeRow = function () {
		var newDataList = [];
		$scope.selectAllRow = false
		angular.forEach($scope.scheduleTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.scheduleTableArray = newDataList;
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
        metajson.tempName = response.data[i].name;
        metajson.version = response.data[i].version;
        metajson.index = i;
        metaArray[i] = metajson;
      }
      $scope.allMeta = metaArray;
    }
  }
 
  $scope.getAllLatest("dag")

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
    BatchService.getAllVersionByUuid($stateParams.id, CF_META_TYPES.batch).then(function(response) {onGetAllVersionByUuid(response.data)});
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var BatchVersion = {};
        BatchVersion.version = response[i].version;
        $scope.batch.versions[i] = BatchVersion;
      }
    }
    BatchService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"batchview")
      .then(function(response) {onsuccess(response.data)},function (response) { onError(response.data)});
    var onsuccess = function(response) {
      $scope.isEditInprogess=false;
      $scope.batchDetail = response.batch;
      if( $scope.batchDetail.tags)
        $scope.tags =  $scope.batchDetail.tags
      $scope.checkboxModelparallel =  $scope.batchDetail.inParallel;
      var defaultversion = {};
      defaultversion.version =  $scope.batchDetail.version;
      defaultversion.uuid =  $scope.batchDetail.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagArray = [];
      for (var i = 0; i <  $scope.batchDetail.pipelineInfo.length; i++) {
        var metaTags = {};
        metaTags.uuid =  $scope.batchDetail.pipelineInfo[i].ref.uuid;
        metaTags.type =  $scope.batchDetail.pipelineInfo[i].ref.type;
        metaTags.name =i+1+" - "+response.batch.pipelineInfo[i].ref.name;
        metaTags.tempName = $scope.batchDetail.pipelineInfo[i].ref.name;
        metaTags.id =  $scope.batchDetail.pipelineInfo[i].ref.uuid;
        metaTags.index =  i;
        metaTags.version =  $scope.batchDetail.pipelineInfo[i].ref.version;
        metaTagArray[i] = metaTags;
      }
      $scope.metaTags = metaTagArray
      $scope.scheduleTableArray=response.scheduleInfoArray;
    };
    var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
  }else{
    $scope.batchDetail={};
    $scope.batchDetail.locked="N";
  }

  $scope.selectVersion = function() {
    $scope.myform.$dirty = false;
    $scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
    BatchService.getOneByUuidAndVersion($scope.batch.defaultVersion.uuid, $scope.batch.defaultVersion.version, "batchview")
      .then(function(response) {onsuccess(response.data)},function (response) { onError(response.data)});
    var onsuccess = function(response) {
      $scope.isEditInprogess=false;
      $scope.batchDetail = response.batch;
      if(response.batch.tags)
        $scope.tags = response.batch.tags
      $scope.checkboxModelparallel = response.batch.inParallel;
      var defaultversion = {};
      defaultversion.version = response.batch.version;
      defaultversion.uuid = response.batch.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagArray = [];
      for (var i = 0; i < response.batch.pipelineInfo.length; i++) {
        var metaTags = {};
        metaTags.uuid = response.batch.pipelineInfo[i].ref.uuid;
        metaTags.type = response.batch.pipelineInfo[i].ref.type;
        metaTags.name = i+1+" - "+response.batch.pipelineInfo[i].ref.name;
        metaTags.version = response.batch.pipelineInfo[i].ref.version;
        metaTags.tempName = $scope.batchDetail.pipelineInfo[i].ref.name;
        metaTags.id =  $scope.batchDetail.pipelineInfo[i].ref.uuid;
        metaTags.index =  i;
        metaTagArray[i] = metaTags;
      }
      $scope.metaTags = metaTagArray
      $scope.scheduleTableArray=response.batch.scheduleInfoArray;
    };
    var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
  }

  $scope.loadMeta = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allMeta, query);
    });
  };

  $scope.oksave = function() {
    var hidemode = "yes";
    if (hidemode == 'yes' && $scope.isDestoryState==false){
      setTimeout(function() {
        $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].listState);
      }, 2000);
    }
  }

  $scope.onChangeDesc=function(){
    $scope.batchDetail.batchChg="Y";
  }

  $scope.onChangeRunInParallel=function(){
    $scope.batchDetail.batchChg="Y";
  }
  
  $scope.onTagRemoved=function(){
    $scope.batchDetail.batchChg="Y";
  }
  $scope.onTagAdd=function(){
    $scope.batchDetail.batchChg="Y";
  }
  
  $scope.onPipelineInfoTagAdding=function(tag){
    var len;
    if($scope.metaTags){
      len=$scope.metaTags.length+1;
    }else{
      len =1;
    } 
    tag.name=len+" - "+tag.name 
    //console.log(tag)
    $scope.batchDetail.batchChg="Y";
  }
  $scope.onPipelineInfoTagRemoved=function(tag){
    for(var i=0;i<$scope.metaTags.length;i++){
      $scope.metaTags[i].name = i+1+" - "+$scope.metaTags[i].tempName
    }
    $scope.batchDetail.batchChg="Y";
  }

  $scope.submit = function(isTrue) {
   
    var upd_tag="N"
    $scope.isSubmitProgess = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var batchJson = {}
    batchJson.uuid = $scope.batchDetail.uuid;
    batchJson.id = $scope.batchDetail.id;
    batchJson.name = $scope.batchDetail.name;
    batchJson.desc = $scope.batchDetail.desc;
    batchJson.active = $scope.batchDetail.active;
    batchJson.locked = $scope.batchDetail.locked;
    batchJson.published = $scope.batchDetail.published;
    batchJson.publicFlag = $scope.batchDetail.publicFlag;
    batchJson.inParallel= $scope.batchDetail.inParallel;
    if($scope.isAdd ==true){
      batchJson.batchChg="Y";
    }else{
      batchJson.batchChg=$scope.batchDetail.batchChg;
      if($scope.batchDetail.batchChg ==null){
        batchJson.batchChg="N";
      }
    }
   
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
    
    batchJson.pipelineInfo = metaInfoArray;
    var scheduleTableArray=[];
    if($scope.scheduleTableArray && $scope.scheduleTableArray.length >0){
    for(var i=0;i<$scope.scheduleTableArray.length;i++){
      var scheduleInfo={};
      if($scope.scheduleTableArray[i].scheduleChg =="Y"){
        scheduleInfo.scheduleChg="Y";
      }else{
        scheduleInfo.scheduleChg="N";
      }
      if($scope.isAdd ==false){
        scheduleInfo.uuid=$scope.scheduleTableArray[i].uuid;
      }
      scheduleInfo.name=$scope.scheduleTableArray[i].name;
      scheduleInfo.startDate=$filter('date')(new Date($scope.scheduleTableArray[i].startDate), "EEE MMM dd HH:mm:ss Z yyyy");//new Date($scope.scheduleTableArray[i].startDate);
      scheduleInfo.endDate=$filter('date')(new Date($scope.scheduleTableArray[i].endDate), "EEE MMM dd HH:mm:ss Z yyyy");//new Date($scope.scheduleTableArray[i].endDate);
      scheduleInfo.frequencyType=$scope.scheduleTableArray[i].frequencyType;
      scheduleInfo.frequencyDetail=[];
      
      if($scope.scheduleTableArray[i].frequencyDetail){
        for(var j=0;j<$scope.scheduleTableArray[i].frequencyDetail.length;j++){
          if($scope.scheduleTableArray[i].frequencyType !="MONTHLY" && $scope.scheduleTableArray[i].frequencyType !="QUARTERLY" && $scope.scheduleTableArray[i].frequencyType !="HOURLY"){
            scheduleInfo.frequencyDetail[j]=$scope.weekDaysToNum[$scope.scheduleTableArray[i].frequencyDetail[j]];
          }
          else if($scope.scheduleTableArray[i].frequencyType =="QUARTERLY"){
            scheduleInfo.frequencyDetail[j]=$scope.quarterlyToNum[$scope.scheduleTableArray[i].frequencyDetail[j]];
          }
        else{
          scheduleInfo.frequencyDetail[j]=$scope.scheduleTableArray[i].frequencyDetail[j];
        }
      }
    }
    //  scheduleInfo.recurring=$scope.scheduleTableArray[i].recurring==true ?'Y':'N';
      scheduleTableArray[i]=scheduleInfo;
    } 
    } 

    if(isTrue){
      batchJson.batchChg="Y";
    }
    batchJson.scheduleInfo=scheduleTableArray; 
    console.log(JSON.stringify(batchJson))
    BatchService.submit(batchJson,"batchview",upd_tag).then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
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


BatchModule.controller('ResultBatchController', function( $location,$http,uiGridConstants,$state,$timeout, $filter, $stateParams, $rootScope, $scope, BatchService,privilegeSvc,dagMetaDataService,CommonService,CF_META_TYPES,FileSaver,Blob) {
  
  $scope.autoRefreshCounter=05;
  $scope.autoRefreshResult=false;
  $scope.path = dagMetaDataService.statusDefs
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
 
  $scope.getGridStyle = function () {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
    } else {
      style['height'] = "100px"
    }
    return style;
  }
  $scope.batchexecDetail={};
  $scope.batchexecDetail.name=$stateParams.name
  $scope.gridOptions ={
    // paginationPageSizes: [10, 25, 50, 75],
    // paginationPageSize: 10,
    // enableFiltering: true,
    enableGridMenu: true,
    rowHeight: 40,
    exporterSuppressCtiolumns: [ 'action' ],
    exporterMenuPdf: true,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    columnDefs: [
      {
        displayName: 'UUID',
        name: 'uuid',
        visible: false,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Name',
        name: 'name',
        minWidth: 220,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Version',
        name: 'version',
        visible: false,
        maxWidth:110,
        cellClass: 'text-center',
        headerCellClass: 'text-center',
       
      },
      {
        displayName: 'Created By',
        name: 'createdBy.ref.name',
        visible: false,
        cellClass: 'text-center',
        maxWidth:100,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Created On',
        visible: false,
        name: 'createdOn',
        minWidth: 160,
        cellClass: 'text-center',
        headerCellClass: 'text-center',

      }
    ]
  };
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'Start Time',
      name: 'startTime',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 190,
      sort: {
        direction: uiGridConstants.ASC,
       // priority: 0,
      },
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.startTime}}</div></div>'
    }
  )
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'End Time',
      name: 'endtime',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 190,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.endTime}}</div></div>'
    }
  )
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'Duration',
      name: 'duration',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 110,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.duration}}</div></div>'
    }
  )
  $scope.gridOptions.columnDefs.push(
{
      displayName: 'Status',
      name: 'status',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 100,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm label-success" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{row.entity.status}}</div></div>'
    }
  );
  $scope.gridOptions.columnDefs.push({
    displayName: 'Action',
    name: 'action',
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    maxWidth: 100,
    cellTemplate: [
      '<div class="ui-grid-cell-contents">',
      '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
      '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
      '    <i class="fa fa-angle-down"></i></button>',
      '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
      '       <li><a ng-disabled="row.entity.type.toLowerCase().indexOf(\'batchexec\')!=-1?[\'COMPLETED\',\'RUNNING\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'trainexec\')!=-1?[\'COMPLETED\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'dagexec\')!=-1?[\'COMPLETED\',\'PENDING\',\'TERMINATING\',\'FAILED\',\'RUNNING\',\'KILLED\',\'STARTING\',\'READY\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'group\')==-1?[\'COMPLETED\',\'KILLED\'].indexOf(row.entity.status)==-1:[\'COMPLETED\',\'RUNNING\',\'KILLED\',\'FAILED\',\'TERMINATING\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.action(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
      '       <li><a ng-disabled="[\'RUNNING\',\'RESUME\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.setStatus(row.entity,\'KILLED\')"><i class="fa fa-times" aria-hidden="true"></i> Kill </a></li>',
      '       <li><a ng-disabled="[\'KILLED\',\'FAILED\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.restartExec(row.entity)"><i class="fa fa-repeat" aria-hidden="true"></i> Restart </a></li>',
      '    </ul>',
      '  </div>',
      '</div>'
    ].join('')
  });

  $scope.gridOptions.data=[];
  $scope.gridOptions.onRegisterApi = function (gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  $scope.execDetail={};
  $scope.execDetail.uuid=$stateParams.id;
  $scope.execDetail.version=$stateParams.version;
  $scope.showGraph=function(){
    $scope.isGraphShow=true;      
  }

  $scope.showResultPage=function(){
    $scope.isGraphShow=false;   
  }

  $scope.getExecListByBatchExec=function(){
    BatchService.getExecListByBatchExec($stateParams.id,$stateParams.version,CF_META_TYPES.batchexec).then(function (response) { onSuccessGetExecListByBatchExec(response.data) });
    var onSuccessGetExecListByBatchExec = function (response) {
      $scope.gridOptions.data=response;
      $scope.originalData=response;
    }
  }
  $scope.getExecListByBatchExec();
  $scope.refreshData = function () {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
	};

  $scope.refresh=function(){
    $scope.getExecListByBatchExec();
  }
  $scope.action = function (data, mode, privilege) {
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['batchexec'].resultState;
    $rootScope.previousState.params = {};
    $rootScope.previousState.params.id = $stateParams.id;
    $rootScope.previousState.params.version = $stateParams.version;
    $rootScope.previousState.params.mode = true;
    var stateName = dagMetaDataService.elementDefs[data.type.toLowerCase()].resultState;
    if (stateName)
      $state.go(stateName, {
        id: data.uuid,
        version: data.version,
        type:data.type.toLowerCase(),
        returnBack : true,
        mode: mode == 'view' ? true : false
      });
  }
  
  $scope.close=function(){
    var stateName ="batchexeclist"; 
    $state.go(stateName);
  }

  $scope.getDetail = function (data) {
    $scope.obj = {};
    $scope.obj=data
    $scope.msg = "Export"
    $scope.type= dagMetaDataService.elementDefs[data.type.toLowerCase().split("exec")[0]].caption
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.submitOk=function(msg){
   if(msg =="Export"){
    $scope.okExport();
   }
   if(msg =="Killed"){
    $scope.okKill();
   }
   if(msg=="Restart"){
    $scope.okRestart();
   }
  }

  $scope.okExport = function () {
    $('#confModal').modal('hide');
    CommonService.getLatestByUuid($scope.obj.uuid,$scope.obj.type).then(function (response) {
      onSuccessGetUuid(response.data)
    });
    var onSuccessGetUuid = function (response) {
      var jsonobj = angular.toJson(response, true);
      var data = new Blob([jsonobj], {
        type: 'application/json;charset=utf-8'
      });
      FileSaver.saveAs(data, response.name + '.json');
      $scope.message = "Batch Exec Downloaded Successfully";
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }

  $scope.setStatus = function (row, status) {
    $scope.obj=row
    $scope.msg = "Killed"
    $scope.type= dagMetaDataService.elementDefs[row.type.toLowerCase().split("exec")[0]].caption
    $scope.obj.setStatus=status
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.okKill = function () {
    var api = false;
    switch ($scope.obj.type.toLowerCase()) {
      case 'dqexec':
        api = 'dataqual';
        break;
      case 'dqgroupExec':
        api = 'dataqual';
        break;
      case 'profileExec':
        api = 'profile';
        break;
      case 'profilegroupExec':
        api = 'profile';
        break;
      case 'ruleExec':
        api = 'rule';
        break;
      case 'rulegroupExec':
        api = 'rule';
        break;
      case 'reconExec':
        api = 'recon';
        break;
      case 'recongroupExec':
        api = 'recon';
        break;
      case 'dagexec':
        api = 'dag';
        break;
      case 'batchexec':
        api = 'batch';
        break;
    }
    if (!api) {
      return
    }
    $('#confModal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content ="Pipeline KILLED Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.put(url + '' + api + '/setStatus?uuid=' + $scope.obj.uuid + '&version=' +  $scope.obj.version + '&type='+$scope.obj.type+'&status=' +  $scope.obj.setStatus).then(function (response) {
      console.log(response);
    });
  }

  $scope.restartExec = function (row, status) {
    $scope.obj=row;
    $scope.msg = "Restart"
    $scope.type= dagMetaDataService.elementDefs[$scope.obj.type.toLowerCase().split("exec")[0]].caption
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }


   $scope.okRestart=function(){
    var api = false;
    switch ($scope.obj.type.toLowerCase()) {
      case 'dqexec':
        api = 'dataqual';
        break;
      case 'dqgroupExec':
        api = 'dataqual';
        break;
      case 'profileExec':
        api = 'profile';
        break;
      case 'profilegroupExec':
        api = 'profile';
        break;
      case 'ruleExec':
        api = 'rule';
        break;
      case 'rulegroupExec':
        api = 'rule';
        break;
      case 'dagexec':
        api = 'dag';
        break;
      case 'reconExec':
        api = 'recon';
        break;
      case 'recongroupExec':
        api = 'recon';
        break;
      case 'batchExec':
        api = 'batch';
        break;
    }
    if (!api) {
      return
    }
    $('#confModal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = "Pipeline Restarted Successfully";
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.post(url + '' + api + '/restart?uuid=' + $scope.obj.uuid + '&version=' + $scope.obj.version + '&type='+$scope.obj.type+'&action=execute').then(function (response) {
      //console.log(response);
    });
   }
  var myVar;
  $scope.autoRefreshOnChange=function () {
    if($scope.autorefresh){
        myVar = setInterval(function(){
            $scope.getExecListByBatchExec();
        }, $scope.autoRefreshCounter+"000");
    }
    else{
        clearInterval(myVar);
    }
  }

  $scope.$on('$destroy', function() {
  // Make sure that the interval is destroyed too
      clearInterval(myVar);
      $scope.gridOptions=null;
  });
  
});

(function($angular, _, $moment, Hammer) {
  'use strict';
	BatchModule.directive('weekdaySelector', [function() {
		// init tracker and sort model
		var _tracker = function(m){
			m.sort();
			return _.times(7, function(i){
				return (_.indexOf(m, i) !== -1);
			});
		};
		
		// toggle day and sort model
		var _toggle = function(m, d, t){
			var i = _.indexOf(m, d);
			t[d] = (i === -1);
			(i > -1) ? m.splice(i, 1) : m.push(d);
			m.sort();
		};

		return {
      restrict: 'E',
      replace: true,

			scope: {
				model: '=?'
			},
			
      template: '<div class="weekday-selector"><ul><li ng-repeat="day in days" tap="toggle(model, $index, tracker)" ng-class="{selected: tracker[$index]}"><span>{{day[0]}}</span></li></ul></div>',

			link: function(scope, element, attrs) {
        scope.days = $moment.weekdays();
       // console.log(scope.days)
				scope.toggle = function(m, d, t) {
					_toggle(m, d, t);
				};
				scope.tracker = _tracker(scope.model);
				scope.$watch('model', function(n){
					scope.tracker = _tracker(n);
				})
			}
    };
  }])
  BatchModule.directive('quarterlySelector', [function() {
		// init tracker and sort model
		var _tracker = function(m){
			m.sort();
			return _.times(7, function(i){
				return (_.indexOf(m, i) !== -1);
			});
		};
		
		// toggle day and sort model
		var _toggle = function(m, d, t){
			var i = _.indexOf(m, d);
			t[d] = (i === -1);
			(i > -1) ? m.splice(i, 1) : m.push(d);
			m.sort();
		};

		return {
      restrict: 'E',
      replace: true,

			scope: {
				model: '=?'
			},
			
      template: '<div class="quarterly-selector"><ul><li ng-repeat="quarterly in quarters" tap="toggle(model, $index, tracker)" ng-class="{selected: tracker[$index]}"><span>{{quarterly[0]}}{{quarterly[1]}}</span></li></ul></div>',

			link: function(scope, element, attrs) {
        scope.quarters =["Q1","Q2","Q3","Q4"];
       // console.log(scope.days);
				scope.toggle = function(m, d, t) {
					_toggle(m, d, t);
				};
				scope.tracker = _tracker(scope.model);
				scope.$watch('model', function(n){
					scope.tracker = _tracker(n);
				})
			}
    };
  }])
  BatchModule.directive('hourlySelector', [function() {
    // init tracker and sort model
    var _tracker = function(m){
      m.sort();
      return _.times(24, function(i){
        return (_.indexOf(m, i) !== -1);
      });
    };
    
    // toggle day and sort model
    var _toggle = function(m, d, t){
      var i = _.indexOf(m, d);
      t[d] = (i === -1);
      (i > -1) ? m.splice(i, 1) : m.push(d);
      m.sort();
    };
  
    return {
      restrict: 'E',
      replace: true,
  
      scope: {
        model: '=?'
      },
      
      template: '<div class="hourly-selector"><ul class="list-unstyled"><li class="col-md-3 odd" ng-repeat="hour in hours" tap="toggle(model, $index, tracker)" ng-class="{selected: tracker[$index]}"><span>{{hour}}</span></li></ul></div>',
  
      link: function(scope, element, attrs) {
        scope.hours =["00","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"];
       // console.log(scope.days);
        scope.toggle = function(m, d, t) {
          _toggle(m, d, t);
        };
        scope.tracker = _tracker(scope.model);
        scope.$watch('model', function(n){
          scope.tracker = _tracker(n);
        })
      }
    };
  }])
	.directive('tap', [function() {
		return function(scope, element, attr) {
			var hammerTap = new Hammer(element[0], {});
			hammerTap.on('tap', function() {
				scope.$apply(function() {
					scope.$eval(attr.tap);
				});
			});
		};
  }])
})(window.angular, window._, window.moment, window.Hammer);

// (function($angular, _, $moment, Hammer) {
//   'use strict';
// 	BatchModule.directive('weekdaySelector', [function() {
// 		// init tracker and sort model
// 		var _tracker = function(m){
// 			m.sort();
// 			return _.times(7, function(i){
// 				return (_.indexOf(m, i) !== -1);
// 			});
// 		};
		
// 		// toggle day and sort model
// 		var _toggle = function(m, d, t){
// 			var i = _.indexOf(m, d);
// 			t[d] = (i === -1);
// 			(i > -1) ? m.splice(i, 1) : m.push(d);
// 			m.sort();
// 		};

// 		return {
//       restrict: 'E',
//       replace: true,

// 			scope: {
// 				model: '=?'
// 			},
			
//       template: '<div class="weekday-selector"><ul><li ng-repeat="day in days" tap="toggle(model, $index, tracker)" ng-class="{selected: tracker[$index]}"><span>{{day[0]}}</span></li></ul></div>',

// 			link: function(scope, element, attrs) {
//         scope.days = $moment.weekdays();
//        // console.log(scope.days)
// 				scope.toggle = function(m, d, t) {
// 					_toggle(m, d, t);
// 				};
// 				scope.tracker = _tracker(scope.model);
// 				scope.$watch('model', function(n){
// 					scope.tracker = _tracker(n);
// 				})
// 			}
//     };
//   }])
  

// 	.directive('tap', [function() {
// 		return function(scope, element, attr) {
// 			var hammerTap = new Hammer(element[0], {});
// 			hammerTap.on('tap', function() {
// 				scope.$apply(function() {
// 					scope.$eval(attr.tap);
// 				});
// 			});
// 		};
//   }])
// })(window.angular, window._, window.moment, window.Hammer);

