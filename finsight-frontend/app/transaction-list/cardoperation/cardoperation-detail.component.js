'use strict';

angular.
  module('transactionList').
  component('cardoperationDetail', {
    templateUrl: 'transaction-list/cardoperation/cardoperation-detail.template.html',
    controller: [function CardOperationDetailController() {
      this.cardPopoverTemplateUrl="transaction-list/cardoperation/cardoperation-cardpopover.template.html";
    }],
    bindings: {
      data: '<'
    },
  });