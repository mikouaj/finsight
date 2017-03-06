'use strict';

angular.
  module('reports').
  component('categoryreport', {
    templateUrl: 'reports/category/category.template.html',
    controller: ['Backend','$scope','$uibModal',function CategoryReportController(Backend,$scope,$uibModal) {
      var self = this;

      self.typesChart={};
      self.typesChart.type = "PieChart";
      self.typesChart.options = {
        'title': 'Transaction types'
      };
      self.typesChart.data={"cols": [
        {id: "t", label: "Type", type: "string"},
        {id: "s", label: "Transactions", type: "number"}
      ], "rows": []};

      var transactionCache={};

      Backend.getApi().then(function(api) {
        api.labels.get().then(function(response) {
          self.labels = response.data;
        });
      });

      self.dateFormat = 'yyyy-MM-dd';

      self.dateFrom = new Date(2000,1,1);
      self.dateFromPopupOpened = false;
      self.dateFromOptions = {
        formatYear: 'yy',
        maxDate: new Date(),
        minDate: new Date(2000,1,1),
        startingDay: 1
      };

      self.dateTo = new Date();
      self.dateToPopupOpened = false;
      self.dateToOptions = {
        formatYear: 'yy',
        maxDate: new Date(),
        minDate: self.dateFrom,
        startingDay: 1
      };

      self.dateFromPopupOpen = function() {
        self.dateFromPopupOpened = true;
      }

      self.dateToPopupOpen = function() {
        self.dateToPopupOpened = true;
      }

      self.updateTypesChart = function(typesCountData) {
        var rows=[];
        for(var type in typesCountData) {
          if (typesCountData.hasOwnProperty(type)) {
            console.log(type);
            rows.push(
              {c:[{v: type},{v: typesCountData[type]},]}
            );
          }
        }
        self.typesChart.data.rows = rows;
      }

      self.queryReportData = function(dateFrom,dateTo,labelId) {
        Backend.getApi().then(function(api) {
          api.transactions.get({},{params:{label:labelId}}).then(function(response) {
            var typeCount={};
            for(var id in response.data) {
              if(typeof typeCount[response.data[id].type] === 'undefined') {
                typeCount[response.data[id].type]=1;
              } else {
                typeCount[response.data[id].type]++;
              }
            }
            self.updateTypesChart(typeCount);
          });
        });
      }

      self.labelSelected = function labelSelected(item,model) {
        self.queryReportData(self.dateFrom,self.dateTo,model.id);
      }
    }]
  });
