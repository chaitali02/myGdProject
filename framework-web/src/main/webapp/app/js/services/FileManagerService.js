AdminModule= angular.module('AdminModule');
AdminModule.service('FileManagerService',function($q,CommonFactory,sortFactory){
    this.SaveFile=function(filename,data,type){
        var url="admin/upload?action=execute&fileName="+filename+"&fileType=csv"
        var deferred = $q.defer();
        CommonFactory.SaveFile(url,data).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
            deferred.resolve({
                data:response
            });
        }
        return deferred.promise;
    }

    this.download=function(fileName,fileType){
        var url="common/download?action=view&type=downloadexec&fileType="+fileType+"&fileName="+fileName;
        var deferred = $q.defer();
        CommonFactory.httpGet(url).then(function(response){onSuccess(response)});
        var onSuccess=function(response){
            deferred.resolve({
                data:response
            });
        }
        return deferred.promise;
    }

    this.getDatasourceByType=function(type){
        var url="metadata/getDatasourceByType?action=view&type=" + type;
        var deferred = $q.defer();
        CommonFactory.httpGet(url).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
            deferred.resolve({
                data:response
            });
        }
        return deferred.promise;
    }
  
});