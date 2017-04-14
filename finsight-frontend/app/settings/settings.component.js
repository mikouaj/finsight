'use strict';

angular.
  module('settings').
  component('settings', {
    templateUrl: 'settings/settings.template.html',
    controller: ['$routeParams',
    function SettingsController($routeParams) {
        var self = this;
        self.activetab=0;
        if($routeParams.hasOwnProperty("tab")) {
          var tabIdx = parseInt($routeParams["tab"]);
          if(!isNaN(tabIdx) && tabIdx>0 && tabIdx<4) {
            self.activetab=tabIdx;
          }
        }
        if($routeParams.hasOwnProperty("newRule")) {
          self.activetab=3; // Idx of rule tab
        }
    }]
  });
