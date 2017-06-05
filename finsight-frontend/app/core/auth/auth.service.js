'use strict';

angular.
  module('core.auth').
  factory('AuthService', ['Backend','UserSession',
    function(Backend,UserSession) {
      var self = this;
    }
  ]).
  service('UserSession', function() {
      this.create = function(userId) {
        this.userId = userId;
      }
      this.destory = function() {
        this.userId = null;
      }
  }).
  constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    loginFailed: 'auth-login-failed',
    logoutSuccess: 'auth-logout-success',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized'
  });
