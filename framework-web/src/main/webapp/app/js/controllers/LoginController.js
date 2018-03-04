/**
 *
 */

(function() {
  var LoginModule = angular.module('LoginModule');
  LoginModule.controller('LoginController', ['$rootScope', '$http', '$scope', 'LoginService', '$window', '$cookieStore', '$remember', '$state', function($rootScope, $http, $scope, LoginService, $window, $cookieStore, $remember, $state) {
    if(typeof localStorage.userdetail !="undefined"){
      $window.location.href='index.html';
      return false;
    }
    $scope.statusForgetFrom = false;
    $scope.remember = false;
    $scope.message = "Enter your e-mail address and username below to reset your password."
    if($remember('username')) {  //&& $remember('password')
      $scope.remember = true;
      $scope.UserName = $remember('username');
    //  $scope.Password = $remember('password');
    }
    $scope.rememberMe = function() {
      if ($scope.remember) {
        $remember('username', $scope.UserName);
      //  $remember('password', $scope.Password);
      } else {
        $remember('username', '');
        //$remember('password', '');
      }
    };

    $scope.login = function() {
      $scope.rememberMe();
      LoginService.validataUser($scope.UserName,$scope.Password).then(function(response){onSuccess(response)});
      var onSuccess = function(response){
        //debugger
      //  sessionStorage.loginStatus = false;
        if(response.status == 'true'){
        //  $rootScope.loginStatus=true;
          //sessionStorage.loginStatus=true;
          delete response.message;
          localStorage.userdetail=JSON.stringify(response);
        //  $cookieStore.put('userdetail',response);
          //$cookieStore.put('name',$scope.UserName);
         //$cookieStore.put('sessionId',response.sessionId);

          $window.location.href='index.html';

        }
        else {
          $scope.error;
          $scope.error = response.message
        }
      };
    }
    $scope.showForgetFrom = function() {
      $scope.statusForgetFrom = true;
    }

    $scope.showLoginForm = function() {
      $scope.statusForgetFrom = false;
    }

    $scope.submitForgetForm = function() {
      $scope.message = "Verification Email has been sent to your email Id "
    }

  }]);

})();
