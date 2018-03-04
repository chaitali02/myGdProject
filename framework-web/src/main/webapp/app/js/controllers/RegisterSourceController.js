/**
 *
 */

AdminModule = angular.module('AdminModule');

AdminModule.controller('RegisterSourceController', function($stateParams, $rootScope, $scope, RegisterSourceService) {

  $scope.sourceTypes = ["FILE", "HIVE","IMPALA","MYSQL","ORACLE"];
  $scope.sourcedetail = [];
  $scope.isSubmitEnable = true;
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
};
	$scope.refreshData = function() {
    $scope.sourecedata = null;
    $scope.spinner = false;
		$scope.sourcetype= null;
		$scope.datasourcedata=null;
		$scope.isDataError = false;
  }  
  

  $scope.onChangeSourceType = function() {
    $scope.sourecedata = null;
    $scope.spinner = false;
    $scope.alldatasource=null;
//    RegisterSourceService.getDatasourceByType($scope.sourcetype).then(function(response) {
//      onSuccessGetDatasourceByType(response.data)
//    })
//    var onSuccessGetDatasourceByType = function(response) {
//      $scope.alldatasource = response
//    }
  RegisterSourceService.getDatasourceByApp($rootScope.appUuid).then(function(response) {
  onSuccessGetDatasourceByType(response.data)
})
var onSuccessGetDatasourceByType = function(response) {
  $scope.alldatasource = response
}

    
  }
  $scope.onChangeSourceType();
  $scope.selectDataSource = function() {

    $scope.isDataInpogress = true;
    $scope.isDataError = false;
    if ($scope.datasourcedata != null) {
      $scope.spinner = true;
      RegisterSourceService.getRegistryByDatasource($scope.datasourcedata.uuid).then(function(response) {
        onSuccessGetRegistryByDatasource(response.data)
      }, function(response) {
        onError(response.data)
      });
      var onSuccessGetRegistryByDatasource = function(response) {
        console.log(JSON.stringify(response))
        //$scope.data=response;
        $scope.spinner = false;
        $scope.isDataInpogress = false;
        $scope.sourecedata = response
      }
      var onError = function(response) {
        debugger
        $scope.isDataInpogress = true;
        $scope.isDataError = true;
        $scope.msgclass = "errorMsg";
        $scope.datamessage = "Some Error Occurred";
        $scope.spinner = false;
      }
    }

  }

  /*$scope.registerdatapod=function(){

		RegisterSourceService.getRegisterHiveDB($scope.datasourcedata.uuid,$scope.datasourcedata.version).then(function(response){onSuccessGetRegisterHiveDB(response.data)});
		var onSuccessGetRegisterHiveDB=function(response){
			console.log(JSON.stringify(response))
			$scope.selectDataSource();
		}

   }*/

  /*$scope.getSourceDetail=function(data){
	  alert(data)
	  var len=$scope.sourcedetail.length;
	  var tabledata=data.split(",");
	  var sourcedetail={}
	  sourcedetail.id=tabledata[0];
	  sourcedetail.name=tabledata[1];
	  sourcedetail.status=tabledata[2];
	  $scope.sourcedetail[len]=sourcedetail
	  alert(JSON.stringify($scope.sourcedetail))
	  //var filepath=$scope.datasourcedata.path+"/"+name[1]+".csv"
	  RegisterSourceService.getcreateAndLoad(filepath).then(function(response){onSuccessGetcreateAndLoad(response.data)});
		var onSuccessGetcreateAndLoad=function(response){
			console.log(JSON.stringify(response))
			$scope.selectDataSource();
		}
  }	*/

  $scope.selectAllRegisterSource = function() {
    $scope.isSubmitEnable = !$scope.allselect;
    angular.forEach($scope.sourecedata, function(source) {
      source.selected = $scope.allselect;
    });
  }
  $scope.selectRegisterSource = function(index, data) {
    var result = true;
    for (var i = 0; i < $scope.sourecedata.length; i++) {
      if ($scope.sourecedata[i].selected == true) {

        result = false
        i = $scope.sourecedata.length;
      }
    }
    $scope.isSubmitEnable = result
  }

  $scope.submitRegisgterSource = function() {
    $scope.isSubmitEnable = true;
    $scope.dataLoading = true;
    var sourcearray = [];
    var count = 0;
    for (var i = 0; i < $scope.sourecedata.length; i++) {
      var sourcejson = {};
      if ($scope.sourecedata[i].selected == true) {
        sourcejson.id = $scope.sourecedata[i].id
        sourcejson.name = $scope.sourecedata[i].name;
        sourcejson.dese = $scope.sourecedata[i].dese;
        sourcejson.registeredOn = $scope.sourecedata[i].registeredOn;
        sourcejson.status = $scope.sourecedata[i].status
        sourcearray[count] = sourcejson;
        count = count + 1;
      }

    }
    /*if($scope.sourcetype =="HIVE" && sourcearray.length == $scope.sourecedata.length){

		 sourcearray=null;
	 }*/
    console.log(JSON.stringify(sourcearray))
    RegisterSourceService.getRegister($scope.datasourcedata.uuid, $scope.datasourcedata.version, sourcearray,$scope.datasourcedata.type).then(function(response) {
      onSuccessGetcreateAndLoad(response.data)
    });
    var onSuccessGetcreateAndLoad = function(response) {
      console.log(JSON.stringify(response))
      $scope.dataLoading = false;

      //$scope.selectDataSource();
      for (var i = 0; i < response.length; i++) {
        var id = response[i].id - 1
        $scope.sourecedata[id].selected = "false"
        $scope.sourecedata[id].registeredOn = response[i].registeredOn;
        $scope.sourecedata[id].desc = response[i].desc;
        $scope.sourecedata[id].status = response[i].status;
      }
      notify.type='success',
      notify.title= 'Success',
     notify.content='Source Saved Successfully'
     $scope.$emit('notify', notify);
    }
  }
});
