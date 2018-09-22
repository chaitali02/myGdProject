var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('commentPanelDirective', function ($timeout, privilegeSvc,CommonService, dagMetaDataService,$rootScope,$anchorScroll) {
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
            console.log(scope.commentData)
            scope.isRequire=true
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

            scope.focusRow = function(rowId){
    
                $timeout(function() {
                  $location.hash(rowId);
                  $anchorScroll();
                });
              }
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
                            scope.commentResult[i].isEditComment=false;
                            for(var j=0;j<response[i].uploadExecInfo.length;j++){
                                scope.commentResult[i].uploadExecInfo[j].isDelete=false;
                            }
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

            scope.ShowMoreFile=function(){
                scope.limitUploadExecInfo=scope.file.length;
            }
            scope.ShowLessFile=function(){
                
                scope.limitUploadExecInfo=1;
            }
            scope.delete=function(uuid,type,parentIndex,index,isEditComment){
                if(type =='comment'){
                    CommonService.delete(uuid,'comment').then(function (response){onSuccess(response.data)})
                    var onSuccess=function(response){
                        scope.getCommentByType();

                    }
                }
                else if(type == 'uploadExec'){
                    CommonService.getOneByUuidAndVersion(uuid,'','uploadExec').then(function(response){onSuccessGetOneByUuidandVersion(response.data)});
                        var onSuccessGetOneByUuidandVersion=function(response){
                            CommonService.delete(response.id,'uploadExec').then(function (response){onSuccess(response.data)})
                            var onSuccess=function(response){
                                if(!isEditComment)
                                scope.getCommentByType();
                                else{
                                    scope.commentResult[parentIndex].uploadExecInfo[index].isDelete=true;
                                }
                            }
                        }
                }
                else if(type == 'simple'){
                    scope.commentResult[parentIndex].uploadExecInfo.splice(index,1);
                }
            }
            
            scope.edit=function(index){
                scope.commentResult[index].isEditComment=true;
                
            }
            // scope.editSubmit=function(index){
            //     scope.submit=(null,index);
            // }
            scope.download=function(data){
                if(data.ref.type == "simple"){
                    return false
                }
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
            window.editReadfileName=function(input){
                var parentIndex=input.attributes.parentIndex.nodeValue;
                var uploadExecInfo={}
                var ref={}
                ref.type='simple';
                uploadExecInfo.ref=ref;
                var value=input.files[0];
                uploadExecInfo.value=value;
                uploadExecInfo.isDelete=false
                if(scope.commentResult[parentIndex].uploadExecInfo ==null){
                    scope.commentResult[parentIndex].uploadExecInfo=[];
                }
                scope.commentResult[parentIndex].limitUploadExecInfo=2;
                scope.commentResult[parentIndex].uploadExecInfo.push(uploadExecInfo);
                console.log(scope.commentResult);
            }

            scope.clearFile=function(index){
                scope.isFileUpload=false;
                scope.file.splice(index,1);
            }

            scope.uploadFiles=function(uuid,version,file){
                var fd=new FormData();
                for(var i=0;i<file.length;i++){
                    fd.append('file',file[i])
                }
                
                CommonService.upload(null,fd,uuid,version,"comment",null).then(function (response) { onSuccess(response.data) });
                var onSuccess = function (response) {
                    scope.file=[];
                    scope.isRequire=false
                    setTimeout(function(){ 
                        scope.isRequire=true;
                        scope.commentDesc="" 
                    },100);
                    scope.getCommentByType();
                    scope.isSubmitDisabled=false;
                }
            }

            scope.submit=function(myform,parentIndex,index,data){
              //  console.log(myform.desc.$modelValue)
                scope.isSubmitDisabled=true;
                var commentJson={};
                var file;
                if(index !=null){
                    commentJson.uuid=data.uuid;
                    file=[];
                    var count=0;
                    if(scope.commentResult[parentIndex].uploadExecInfo !=null){
                        for(var j=0;j<scope.commentResult[parentIndex].uploadExecInfo.length;j++){
                            if(scope.commentResult[parentIndex].uploadExecInfo[j].ref.type =='simple'){
                                file[count]=scope.commentResult[parentIndex].uploadExecInfo[j].value;
                                count=count+1;
                            }
                            
                        }
                    }
                }else{
                    file=scope.file;
                }
                commentJson.desc=myform.desc.$modelValue;
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
                    if( file && file.length ==0){
                        scope.isRequire=false
                        setTimeout(function(){ 
                            scope.isRequire=true;
                            scope.commentDesc="" 
                        },100);
                        
                        scope.getCommentByType();
                     //   scope.focusRow(scope.commentResult.length+1)
                        scope.isSubmitDisabled=false;
                    }else{
                        CommonService.getOneById(response,'comment').then(function(response){onSuccessGetOneById(response.data)});
                        var onSuccessGetOneById=function(response){
                          
                            scope.uploadFiles(response.uuid,response.version,file);
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

        // template: '<div><p ng-show="largeText" style="margin:0px"> {{ text | subString :0 :end }}.... <a href="javascript:;" ng-click="showMore()" ng-show="isShowMore">Show More</a><a href="javascript:;" ng-click="showLess()" ng-hide="isShowMore">Show Less </a></p><p ng-hide="largeText" style="margin:0px">{{ text }}</p></div> ',
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

