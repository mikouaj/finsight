'use strict';

angular.
  module('reports').
  component('generalreport', {
    templateUrl: 'reports/general/general.template.html',
    controller: ['Backend','$scope','$uibModal',function GeneralReportController(Backend,$scope,$uibModal) {
      var self = this;
      self.labelsHash={};

      self.typesChart={};
      self.typesChart.type = "PieChart";
      self.typesChart.data={"cols": [
        {id: "s", label: "Source", type: "string"},
        {id: "c", label: "Count", type: "number"}
      ], "rows": []};

      self.destChart={};
      self.destChart.type = "BarChart";
      self.destChart.data={"cols": [
        {id: "t", label: "Type", type: "string"},
        {id: "s", label: "Total", type: "number"},
        {id: "c", label: "Cnt", type: "string","p":{"role":"annotation"}}
      ], "rows": []};
      self.destChart.options = {
        chartArea: {width: '60%'},
        hAxis: {
          title: 'Total amount',
          minValue: 0,
        },
        vAxis: {
          title: 'Destination'
        },
        annotations: {
          alwaysOutside: true,
          textStyle: {
            fontSize: 12,
            auraColor: 'none',
            color: '#555'
          },
          boxStyle: {
            stroke: '#ccc',
            strokeWidth: 1,
            gradient: {
              color1: '#f3e5f5',
              color2: '#f3e5f5',
              x1: '0%', y1: '0%',
              x2: '100%', y2: '100%'
            }
          }
        }
      }

      self.monthChart={};
      self.monthChart.type = "ColumnChart";
      self.monthChart.data={"cols": [
        {id: "m", label: "Month", type: "string"},
        {id: "t", label: "Total", type: "number"},
        {id: "c", label: "Cnt", type: "string","p":{"role":"annotation"}}
      ], "rows": []};
      self.monthChart.options = {
        hAxis: {
          title: 'Month'
        },
        vAxis: {
          title: 'Total amount',
          minValue: 0
        }
      };

      self.labelChart={};
      self.labelChart.type = "BarChart";
      self.labelChart.data={"cols": [
        {id: "t", label: "Type", type: "string"},
        {id: "s", label: "Total", type: "number"},
        {id: "c", label: "Cnt", type: "string","p":{"role":"annotation"}}
      ], "rows": []};
      self.labelChart.options = {
        chartArea: {width: '60%'},
        hAxis: {
          title: 'Total amount',
          minValue: 0,
        },
        vAxis: {
          title: 'Label'
        },
        annotations: {
          alwaysOutside: true,
          textStyle: {
            fontSize: 12,
            auraColor: 'none',
            color: '#555'
          },
          boxStyle: {
            stroke: '#ccc',
            strokeWidth: 1,
            gradient: {
              color1: '#f3e5f5',
              color2: '#f3e5f5',
              x1: '0%', y1: '0%',
              x2: '100%', y2: '100%'
            }
          }
        }
      }

      self.initIntelData = function() {
        self.inteliData={};
        self.inteliData.sum=0;
        self.inteliData.cnt=0;
        self.inteliData.typesCount={};
        self.inteliData.monthCount={};
        self.inteliData.destData={};
        self.inteliData.labelData={};
      }

      self.dateFormat = 'yyyy-MM-dd';
      self.dateFrom = new Date();
      self.dateFrom.setMonth(self.dateFrom.getMonth()-1);
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

      self.monthStrings = {
        "01":"Jan", "02":"Feb","03":"Mar","04":"Apr","05":"May","06":"Jun",
        "07":"Jul","08":"Aug","09":"Sep","10":"Oct","11":"Nov","12":"Dec"
      }

      self.dateFromPopupOpen = function() {
        self.dateFromPopupOpened = true;
      }

      self.dateToPopupOpen = function() {
        self.dateToPopupOpened = true;
      }

      self.reloadReport = function() {
        if(self.dateFrom && self.dateTo) {
          self.initIntelData();
          self.queryReportData(self.dateFrom,self.dateTo,self.selectedLabels);
        }
      }

      // Intel related functions

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
              {c:[{v: type},{v: typesCountData[type].cnt}]}
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
              {c:[{v: dest},{v: destCountData[dest].sum},{v: "Count: "+destCountData[dest].cnt}]}
            );
          }
        }
        rows.sort(function(a,b) {
          return b.c[1].v - a.c[1].v;
        });
        self.destChart.data.rows = rows.slice(0,5);
      }

      self.updateMonthChart = function(monthCountData) {
        var rows=[];
        for(var month in monthCountData) {
          if (monthCountData.hasOwnProperty(month)) {
            rows.push(
              {c:[{v: month,f:self.monthStrings[month]},{v: monthCountData[month].sum},{v: "Count: "+monthCountData[month].cnt}]}
            );
          }
        }
        rows.sort(function(a,b) {
          return a.c[0].v - b.c[0].v;
        });
        self.monthChart.data.rows = rows;
      }

      self.updateLabelChart = function(labelData) {
        var rows=[];
        for(var label in labelData) {
          if (labelData.hasOwnProperty(label)) {
            rows.push(
              {c:[{v: label},{v: labelData[label].sum},{v: "Count: "+labelData[label].cnt}]}
            );
          }
        }
        rows.sort(function(a,b) {
          return b.c[1].v - a.c[1].v;
        });
        self.labelChart.data.rows = rows.slice(0,10);
      }

      self.updateSum = function(map,key,amount) {
        if(typeof map[key] === 'undefined') {
          map[key]={sum:amount,cnt:1};
        } else {
          map[key].sum+=amount;
          map[key].cnt++;
        }
      }

      self.getTransactionMonth = function(transaction) {
        var date = new Date(transaction.date);
        var month = date.getMonth() +1;
        if (month<10) month= '0'+month;
        return month;
      }

      self.getDestForTrans = function(transaction) {
        var dest='N/A';
        if(transaction.type == 'CardOperation') {
          dest = transaction.details.destination;
        } else if(transaction.type== 'Transfer') {
          dest = transaction.details.dstAccount.name;
        } else if(transaction.type== 'Commission') {
          dest = "Commission";
        }
        return dest;
      }

      self.queryReportData = function(dateFrom,dateTo,labelIds) {
        Backend.getApi().then(function(api) {
          api.transactions.get({},{params:{label:labelIds,dateFrom:self.getFormattedDate(dateFrom),dateTo:self.getFormattedDate(dateTo)}}).then(function(response) {
            for(var id in response.data) {
              var transaction = response.data[id];
              if(transaction.accountingAmount>0) {
                // skip as it is not an expense
                continue;
              }
              transaction.accountingAmount=Math.abs(transaction.accountingAmount);
              self.inteliData.sum+=transaction.accountingAmount;
              self.inteliData.cnt+=1;

              self.updateSum(self.inteliData.monthCount,self.getTransactionMonth(transaction),transaction.accountingAmount);
              self.updateSum(self.inteliData.destData,self.getDestForTrans(transaction),transaction.accountingAmount);
              var source=transaction.type;
              if(transaction.type=='CardOperation' && transaction.details.card) {
                  source = transaction.details.card.name;
              }
              self.updateSum(self.inteliData.typesCount,source,transaction.accountingAmount);

              for(var l in transaction.labels) {
                var labelId = transaction.labels[l];
                self.updateSum(self.inteliData.labelData,self.labelsHash[labelId].text,transaction.accountingAmount);
              }
            }
            self.updateTypesChart(self.inteliData.typesCount);
            self.updateDestChart(self.inteliData.destData);
            self.updateMonthChart(self.inteliData.monthCount);
            self.updateLabelChart(self.inteliData.labelData);
          });
        });
      }

      //Execution
      self.labelsHash={};
      Backend.getApi().then(function(api) {
        api.labels.get().then(function(response) {
          self.labels = response.data;
          for(var id in response.data) {
            self.labelsHash[response.data[id].id] = response.data[id];
          }
          self.initIntelData();
          self.reloadReport();
        });
      });
    }]
  });
