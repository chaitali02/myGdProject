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
});