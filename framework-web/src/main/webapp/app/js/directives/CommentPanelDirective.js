var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('commentPanelDirective', function ($timeout, privilegeSvc,CommonService, dagMetaDataService,$rootScope) {
    return {
        scope: {
            type:"=",
            commentData:"=",
            currentUser:"=",
            mode:'=',
            onClose:'=',
            options: '=',
        }, 
        link: function (scope, element, attrs) {
            scope.panelOpen=false;
            var privileges = privilegeSvc.privileges['comment'] || [];
            scope.isPrivlage = privileges.indexOf('Add') == -1;
            angular.extend(scope.options, {
                panelToggle: function(data){
                  scope.panelOpen=data;
                  if(data=true){
                    scope.getCommentByType();
                  }
                },
                closePanel: function(){
                    scope.panelOpen=false;
                  }
            });
            element.bind("keyup", function(event){
                scope.$apply(function() {
                if(event.which == 27){
                  console.log("escape has been pressed");
                 
                }
        
                });
            }); 
            scope.$on('privilegesUpdated', function (e, data) {
                var privileges = privilegeSvc.privileges['comment'] || [];
                scope.isPrivlage =privileges.indexOf('Add') == -1;
                
            });      
            scope.getCommentByType=function(){
                CommonService.getCommentByType(scope.commentData.uuid,scope.type).then(function (response) { onSuccess(response.data)});
                var onSuccess=function(response){
                    scope.commentResult=response;
                    scope.len=scope.commentResult.length+1;
                }
            }
            
            scope.closePanle=function(){
                scope.onClose({"panelOpen":false});
            }

            window.readfileName=function(input){
                if (input.files && input.files[0]) {
                    scope.$apply();
                    scope.isFileUpload=true;
                    scope.file=input.files[0]
                    scope.fd = new FormData();
                    scope.fd.append('file',input.files[0]);
                    console.log(input.files[0]);
                }
            }
            scope.clearFile=function(){
                scope.isFileUpload=false;
                scope.file=null;
                scope.fd=null;

            }
            scope.submit=function(desc){
                var commentJson={};
                commentJson.desc=scope.commentDesc;
                var dependsOn={}
                var ref={};
                ref.uuid=scope.commentData.uuid;
                ref.type=scope.type;
                dependsOn.ref=ref;
                commentJson.dependsOn=dependsOn;
                commentJson.uploadExec=null;
                console.log(JSON.stringify(commentJson));
                if(scope.isFileUpload ==false){
                    CommonService.submit(commentJson,'comment').then(function (response) { onSuccess(response.data)});
                    var onSuccess=function(response){
                    console.log(response);
                    scope.commentDesc=" ";
                    scope.getCommentByType();
                    }
                }else{
                    CommonService.uploadCommentFile(scope.file.name, scope.fd, "comment").then(function (response) { onSuccess(response.data) });
                    var onSuccess = function (response) {
                        commentJson.uploadExec=response;
                        CommonService.submit(commentJson,'comment').then(function (response) { onSuccess(response.data)});
                        var onSuccess=function(response){
                        console.log(response);
                        scope.commentDesc=" ";
                        scope.getCommentByType();
                        }
                    }

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
});

