'use strict';

angular.
  module('transactionList').
  component('transferDetail', {
    templateUrl: 'transaction-list/transfer/transfer-detail.template.html',
    controller: [function TransferDetailController() {
        this.srcPopoverTemplateUrl="transaction-list/transfer/transfer-srcpopover.template.html";
        this.dstPopoverTemplateUrl="transaction-list/transfer/transfer-dstpopover.template.html";
      }
    ],
    bindings: {
      data: '<'
    },
  });