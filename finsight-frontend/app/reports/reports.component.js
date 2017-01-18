'use strict';

angular.
  module('reports').
  component('reports', {
    templateUrl: 'reports/reports.template.html',
    controller: [
    function ReportsController() {
        var self = this;
        self.selectedTab=='Report1';

        self.testChart={};
        self.testChart.type = "ColumnChart";
		self.testChart.data = {
			"cols": [{id: "t", label: "Topping", type: "string"}, {id: "s", label: "Slices", type: "number"}],
			"rows": [{c: [{v: "Mushrooms"},{v: 3}]},
					 {c: [{v: "Srums"},{v: 10}]}
			]
		}; 
    }]
  });
