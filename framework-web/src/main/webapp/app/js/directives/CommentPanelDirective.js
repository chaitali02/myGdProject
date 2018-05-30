var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('commentPanelDirective', function ($timeout, CommonService, dagMetaDataService,$rootScope) {
    return {
        scope: {
            type:"=",
            commentData:"=",
            panelopen:"=",
            currentUser:"=",
            mode:'=',
            onClose:'=',
        }, 
        link: function (scope, element, attrs) {
            var notify = {
                type: 'success',
                title: 'Success',
                content: '',
                timeout: 3000 //time in ms
            };
            scope.panelOpen=scope.panelopen;
            
            scope.getCommentByType=function(){
                CommonService.getCommentByType(scope.commentData.uuid,scope.type).then(function (response) { onSuccess(response.data)});
                var onSuccess=function(response){
                    scope.commentResult=response;
                    scope.len=scope.commentResult.length+1;
                }
            }
            scope.getCommentByType();
            scope.closePanle=function(){
                scope.onClose({"panelOpen":false});
            }
            scope.submit=function(desc){
                debugger
                var commentJson={};
                commentJson.desc=scope.commentDesc;
                var dependsOn={}
                var ref={};
                ref.uuid=scope.commentData.uuid;
                ref.type=scope.type;
                dependsOn.ref=ref;
                commentJson.dependsOn=dependsOn;
                console.log(JSON.stringify(commentJson));
                CommonService.submit(commentJson,'comment').then(function (response) { onSuccess(response.data)});
                var onSuccess=function(response){
                console.log(response);
                scope.commentDesc="";
                scope.getCommentByType();
                
                notify.type = 'success',
                notify.title = 'Success',
                notify.content = 'Comment Saved Successfully'
                scope.$emit('notify', notify);
                }
            }
        },
        templateUrl: 'views/comment-panel.html',
       
    };
});

InferyxApp.directive('showMore', [function() {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            text: '=',
            limit:'='
        },

        template: '<div><p ng-show="largeText"> {{ text | subString :0 :end }}.... <a href="javascript:;" ng-click="showMore()" ng-show="isShowMore">Show More</a><a href="javascript:;" ng-click="showLess()" ng-hide="isShowMore">Show Less </a></p><p ng-hide="largeText">{{ text }}</p></div> ',

        link: function(scope, iElement, iAttrs) {

            
            scope.end = scope.limit;
            scope.isShowMore = true;
            scope.largeText = true;

            if (scope.text.length <= scope.limit) {
                scope.largeText = false;
            };

            scope.showMore = function() {

                scope.end = scope.text.length;
                scope.isShowMore = false;
            };

            scope.showLess = function() {

                scope.end = scope.limit;
                scope.isShowMore = true;
            };
        }
    };
}]);

InferyxApp.filter('subString', function() {
    return function(str, start, end) {
        if (str != undefined) {
            return str.substr(start, end);
        }
    }
})