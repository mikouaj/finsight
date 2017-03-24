'use strict';

angular.
  module('reports').
  component('reports', {
    templateUrl: 'reports/reports.template.html',
    controller: [
    function ReportsController() {
        var self = this;
        self.selectedTab=='General';
    }]
  });
