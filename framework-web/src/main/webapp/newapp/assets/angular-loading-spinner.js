(function(){
    angular.module('ngLoadingSpinner', ['angularSpinner'])
    .directive('usSpinner',   ['$http', '$rootScope' ,function ($http, $rootScope,$timeout){
        return {
            link: function (scope, elm, attrs)
            {
                if(attrs.$attr.usSpinnerStandalone) return;
                
                $rootScope.spinnerActive = false;
                
                scope.isLoading = function () {
                	
                	    // code here
                		return $http.pendingRequests.length > 0;
                
                    
                	  
                };
               
                scope.$watch(scope.isLoading, function (loading)
                {
                	
                	$rootScope.spinnerActive = loading;
                    if(loading){
                    
                        elm.removeClass('ng-hide');
                    }else{
                    	setTimeout(function(){
                    		elm.addClass('ng-hide');
                    	}, 500);
                        
                    }
                
                });
            
            }
        };

    }]);
}).call(this);
