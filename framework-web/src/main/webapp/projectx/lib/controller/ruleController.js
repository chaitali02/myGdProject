(function () {
    'use strict';
    var ruleModule = angular.module("rule_model");
   
    ruleModule.controller('rule_controller', ['$scope','$http', function($scope,$http){
     $scope.ruleJSON;
     $scope.client={};
     $scope.datapodByRelation;
     $scope.datapodname;
     $scope.datapodAttibuteName
     $scope.client.countries=[];
     $scope.ruledatatapodsource;
     $.ajax({
    	 url: "../data/rules.json",
    	 async:false,
    	 success: function(result){
    		 //alert(JSON.parse(result))
    		 $scope.ruleJSON=JSON.parse(result);
             
           }});
     
     $.ajax({
    	 url: "/framework/metadata/getAll?type=relation",
    	 async:false,
    	 beforeSend: function (request)
         {
             request.setRequestHeader("sessionId"," 582c632eb1dfe37e8a563032");
         },
    	 success: function(response){
    		 var  primary_address={}
    		  
    		 primary_address.country=JSON.parse(response)[0].name
    		 $scope.client.primary_address=primary_address
    		  for(var i=0;i<JSON.parse(response).length;i++){
    			  var  countries={};
    			  countries.name=JSON.parse(response)[i].name;
    			 
    			  $scope.client.countries[i]=countries
    		  }
    	      
    		  
           }});	
    
     $.ajax({
    	 url: "/framework/metadata/getAttributesByRelation?uuid=b386b028-4822-4c85-85a5-454db2af0a01&type=relation",
    	 async:false,
    	 beforeSend: function (request)
         {
             request.setRequestHeader("sessionId","582c632eb1dfe37e8a563032");
         },
    	 success: function(response){
    		  $scope.ruledatatapodsource=response
    		 console.log(JSON.stringify(response))
    		 
    		  
           }});	
	
     $.ajax({
    	 /*url: "/framework/metadata/getDatapodByRelation?relationUuid=864c4aa3-aabb-4a84-84fc-0120828c7301&type=datapod",*/
    	 url: "/framework/metadata/getAllLatest?type=datapod",
    	 async:false,
    	 beforeSend: function (request)
         {
             request.setRequestHeader("sessionId","582c632eb1dfe37e8a563032");
         },
    	 success: function(response){
    		 $scope.datapodname=JSON.parse(response)[0] 
    		 $scope.datapodByRelation=JSON.parse(response);
    		 
    		  
           }});	
  
    $scope.getAttriputeByDatapod=function(){
    $.ajax({
   	 url: "/framework/metadata/getOneById?id="+ $scope.datapodname.id+"&type=datapod",
   	 async:false,
   	 beforeSend: function (request)
        {
            request.setRequestHeader("sessionId","582c632eb1dfe37e8a563032");
        },
   	 success: function(response){
   		 
   		$scope.datapodAttibuteName=JSON.parse(response);
   	
          }});
    }
   }]);
   
    ruleModule.directive('select2', function($timeout) {
        return {
            restrict: 'AC',
            link: function(scope, element, attrs) {
                $timeout(function() {
                    element.show();
                    $(element).select2();
                }); 
            }
        };
    })
    

})();