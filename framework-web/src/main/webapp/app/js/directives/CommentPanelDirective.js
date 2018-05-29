var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('commentPanelDirective', function ($timeout, CommonService, dagMetaDataService,$rootScope) {
    return {
        scope: {
            type:"=",
            commentData:"=",
            panelopen:"="
        },
        link: function (scope, element, attrs) {
            var notify = {
                type: 'success',
                title: 'Success',
                content: '',
                timeout: 3000 //time in ms
            };
            scope.panelOpen=scope.panelopen;
            scope.submit=function(){
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
                notify.type = 'success',
                notify.title = 'Success',
                notify.content = 'Comment Saved Successfully'
                scope.$emit('notify', notify);
                }
            }
        },
        templateUrl: 'views/comment-panel.html',
       
    };
})