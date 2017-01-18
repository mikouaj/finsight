'use strict';

angular.
  module('reports').
  component('categoryreport', {
    templateUrl: 'reports/category/category.template.html',
    controller: ['Backend','$scope','$uibModal',function CategoryReportController(Backend,$scope,$uibModal) {
      var self = this;
    }]
  });