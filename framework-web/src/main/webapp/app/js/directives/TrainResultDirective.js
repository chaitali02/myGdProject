var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('trainResult', function ($timeout,$rootScope,CommonService,dagMetaDataService,CF_META_TYPES) {
    return {
        scope: {
            execjson: "=",
        },
        link: function (scope, element, attrs) {
            
        },
        templateUrl: 'views/train-result-template.html',
    };
});