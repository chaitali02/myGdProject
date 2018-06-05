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
            scope.isFileUpload=false;
            scope.file=[];
            scope.isSubmitDisabled=false;
            scope.limitUploadExecInfo=1;
            scope.privileges = privilegeSvc.privileges['comment'] || [];
            scope.isPrivlage = scope.privileges.indexOf('Add') == -1;
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
                scope.privileges = privilegeSvc.privileges['comment'] || [];
                scope.isPrivlage = scope.privileges.indexOf('Add') == -1;
                
            });      
            scope.getCommentByType=function(){
                CommonService.getCommentByType(scope.commentData.uuid,scope.type).then(function (response) { onSuccess(response.data)});
                var onSuccess=function(response){
                    scope.commentResult=response;
                    if(response.length>0){
                        for(var i=0;i< response.length;i++){
                           if(response[i].uploadExecInfo !=null){
                            scope.commentResult[i].isDownloadable=true;
                            scope.commentResult[i].limitUploadExecInfo=2;
                           }
                        }
                    }
                    scope.len=scope.commentResult.length+1;
                }
            }
            
            scope.closePanle=function(){
                scope.onClose({"panelOpen":false});
            }
            
            scope.ShowMore=function(index){
                scope.commentResult[index].limitUploadExecInfo=scope.commentResult[index].uploadExecInfo.length;
            }
            scope.ShowLess=function(index){
                
                scope.commentResult[index].limitUploadExecInfo=2;
            }
            scope.delete=function(uuid){
                CommonService.delete(uuid,'comment').then(function (response){onSuccess(response.data)})
                var onSuccess=function(response){
                    scope.getCommentByType();

                }
            }

            scope.download=function(data){
                CommonService.download(data.ref.name+" ",data.ref.uuid).then(function (response){ onSuccess(response.data)});
                var onSuccess = function (response) {
                  var filename =response.headers('filename');
                  var contentType = response.headers('content-type'); 
                  var linkElement = document.createElement('a');
                  try {
                    var blob = new Blob([response.data], {
                    type: contentType
                  });
                  var url = window.URL.createObjectURL(blob);
                  console.log(url)
                  linkElement.setAttribute('href', url);
                  linkElement.setAttribute("download",filename);
                  var clickEvent = new MouseEvent("click", {
                    "view": window,
                    "bubbles": true,
                    "cancelable": false
                  });
                  linkElement.dispatchEvent(clickEvent);

                  } catch (ex) {
                  console.log(ex);
                  }
                }
              }

            window.readfileName=function(input){
                if (input.files && input.files[0]) {
                    scope.$apply();
                    var fileLen=scope.file.length; 
                    scope.isFileUpload=true;
                    scope.file[fileLen]=input.files[0]
                    console.log(input.files[0]);
                }
            }

            scope.clearFile=function(index){
                scope.isFileUpload=false;
                scope.file.splice(index,1);
            }

            scope.uploadFiles=function(uuid){
                var fd=new FormData();
                for(var i=0;i<scope.file.length;i++){
                    fd.append('file',scope.file[i])
                }
                
                CommonService.uploadCommentFile(null,fd,uuid,"comment").then(function (response) { onSuccess(response.data) });
                var onSuccess = function (response) {
                    scope.file=[];
                    scope.commentDesc=" ";
                    scope.getCommentByType();
                    scope.isSubmitDisabled=false;
                }
            }

            scope.submit=function(desc){
                scope.isSubmitDisabled=true;
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
                CommonService.submit(commentJson,'comment').then(function (response) { onSuccess(response.data)});
                var onSuccess=function(response){
                    console.log(response);
                    if( scope.file && scope.file.length ==0){
                        scope.commentDesc=" ";
                        scope.getCommentByType();
                        scope.isSubmitDisabled=false;
                    }else{
                        CommonService.getOneById(response,'comment').then(function(response){onSuccessGetOneById(response.data)});
                        var onSuccessGetOneById=function(response){
                            scope.uploadFiles(response.uuid);
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

