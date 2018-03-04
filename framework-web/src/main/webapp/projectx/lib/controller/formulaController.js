(function () {
	

   'use strict';
   var formulaModule = angular.module('formula_model', [ 'ngMaterial', 'ngSanitize' ]);
   
   formulaModule.controller("formula_controller", function($scope) {
		$scope.names = [ "Email", "Tobias", "Linus" ];
		$scope.functions = [ "Case", "When", "Then", "End" ];
		
		
		$scope.clear = function(){  
			var myEl = angular.element( document.querySelector( '#formulaInfo' ) );
			myEl.empty(); 
		}
		$scope.addRelationAttr = function(){ 
			var inputValue = angular.element( document.querySelector( '#formulaInfo' ) );
                        var spanValue = $scope.attributeName;
                        var countAttrVal = 1;
                        for (var i = 0; i < inputValue.children().length; ++i) {
			   countAttrVal++;
			}
                        
            var spanVal = "<span id='btn_"+countAttrVal +"'> " + spanValue+ " </span>";
			$scope.textarea = spanVal;	
			inputValue.append(spanVal);
		}
		
	  
		$scope.addName = function(){ 
			     var inputValue = angular.element( document.querySelector( '#formulaInfo' ) );
                 var spanValue = $scope.name;
                 var countInputVal=1;
                 for (var i = 0; i < inputValue.children().length; ++i) {
			        countInputVal++;
			     }
            var spanVal = "<span id='btn_"+countInputVal +"'> " + spanValue+ " </span>";
			$scope.textarea = spanVal;	
			inputValue.append(spanVal);
 		}
		
		$scope.undo = function(){ 
			var myEl2 = angular.element( document.querySelector( '#formulaInfo' ).lastChild );
			myEl2.remove();
		}
		

	});

   formulaModule.directive("addbuttons", function($compile) {
              
              return function(scope, element, attrs) {
                        var inputValue = angular.element( document.querySelector( '#formulaInfo' ) );
			element.bind("click", function() {
                                var count=1;
		                for (var i = 0; i < inputValue.children().length; ++i) {
				   count++;
				}
                               
				var val = attrs.value;
                                var spanVal = "<span id='btn_"+count+"'> " + val+ " </span>";
                                angular.element(document.getElementById('formulaInfo')).append(
					$compile(spanVal)(scope)
				);
			});
		};

	});
})();