/**
 *
 */

var DatapodModule = angular.module('DatapodModule');

/*datapodmodule.directive('fileModel', ['$parse', function ($parse) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            alert(model)
	            var modelSetter = model.assign;

	            element.bind('change', function(){
	                scope.$apply(function(){
	                	scope.fileUplodStatus=false;
	                	scope.uplodbutton=false;
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };
}]);*/
