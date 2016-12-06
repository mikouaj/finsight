'use strict';

angular.
  module('settings').
  component('account', {
    templateUrl: 'settings/account/account.template.html',
    controller: ['Backend',function AccountController(Backend) {
      var self = this;
      Backend.getApi().then(function(api) {
      	api.accounts.getAccounts().then(function(response) {
      		self.accounts = response.data;
      	});
      });
    }]
  });