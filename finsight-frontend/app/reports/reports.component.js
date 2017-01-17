'use strict';

angular.
  module('reports').
  component('reports', {
    templateUrl: 'reports/reports.template.html',
    controller: [
    function SettingsController() {
        var self = this;
        self.selectedTab=='Cards';
    }]
  });
