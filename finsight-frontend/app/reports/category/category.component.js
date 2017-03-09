'use strict';

angular.
  module('reports').
  component('categoryreport', {
    templateUrl: 'reports/category/category.template.html',
    controller: ['Backend','$scope','$uibModal',function CategoryReportController(Backend,$scope,$uibModal) {
      var self = this;

      self.typesChart={};
      self.typesChart.type = "PieChart";
      self.typesChart.data={"cols": [
        {id: "t", label: "Type", type: "string"},
        {id: "c", label: "Count", type: "number"}
      ], "rows": []};
      self.destChart={};
      self.destChart.type = "BarChart";
      self.destChart.data={"cols": [
        {id: "t", label: "Type", type: "string"},
        {id: "c", label: "Transactions", type: "number"}
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

      self.getFormattedDate = function(date) {
        var dd = date.getDate();
        if (dd<10) dd= '0'+dd;
        var mm = date.getMonth() + 1;
        if (mm<10) mm= '0'+mm;
        return date.getFullYear()+"-"+mm+"-"+dd;
      }

      self.updateTypesChart = function(typesCountData) {
        var rows=[];
        for(var type in typesCountData) {
          if (typesCountData.hasOwnProperty(type)) {
            rows.push(
              {c:[{v: type},{v: typesCountData[type]},]}
            );
          }
        }
        self.typesChart.data.rows = rows;
      }

      self.updateDestChart = function(destCountData) {

        var rows=[];
        for(var dest in destCountData) {
          if (destCountData.hasOwnProperty(dest)) {
            rows.push(
              {c:[{v: dest},{v: destCountData[dest]},]}
            );
          }
        }
        self.destChart.data.rows = rows;
      }

      self.queryReportData = function(dateFrom,dateTo,labelId) {
        Backend.getApi().then(function(api) {
          api.transactions.get({},{params:{label:labelId,dateFrom:self.getFormattedDate(dateFrom),dateTo:self.getFormattedDate(dateTo)}}).then(function(response) {
            var typeCount={};
            var destCount={};
            for(var id in response.data) {
              var transaction = response.data[id];
              if(typeof typeCount[transaction.type] === 'undefined') {
                typeCount[transaction.type]=1;
              } else {
                typeCount[transaction.type]++;
              }
              self.updateTypesChart(typeCount);

              var dest='N/A';
              if(transaction.type == 'CardOperation') {
                dest = transaction.details.destination;
              }
              if(transaction.type== 'Transfer') {
                dest = transaction.details.dstAccount.name;
              }
              if(typeof destCount[dest] === 'undefined') {
                destCount[dest]=1;
              } else {
                destCount[dest]++;
              }
              self.updateDestChart(destCount);
            }
          });
        });
      }

      self.labelSelected = function labelSelected(item,model) {
        self.queryReportData(self.dateFrom,self.dateTo,model.id);
      }
    }]
  });
