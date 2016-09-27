'use strict';

angular.
  module('settings').
  component('settings', {
    templateUrl: 'settings/settings.template.html',
    controller: [
    function SettingsController() {
        var self = this;
        self.selectedTab=='Cards';
    }]
  });
