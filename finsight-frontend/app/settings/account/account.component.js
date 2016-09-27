'use strict';

angular.
  module('settings').
  component('account', {
    templateUrl: 'settings/account/account.template.html',
    controller: ['Account',function AccountController(Account) {
      var self = this;
      self.accounts = Account.query();
    }]
  });