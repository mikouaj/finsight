'use strict';

angular.
  module('settings').
  component('card', {
    templateUrl: 'settings/card/card.template.html',
    controller: ['Backend','$scope','$uibModal',function CardController(Backend,$scope,$uibModal) {
      var self = this;
      self.cards = [];
      Backend.getApi().then(function(api) {
      	api.cards.getCards().then(function(response) {
      		self.cards = response.data;
      	});
      });

      self.removeCard = function(idx,card) {
        Backend.getApi().then(function(api) {
          api.cards.delete(card).then(function() {
            self.cards.splice(idx,1);
          });
        });
        self.closeConfirmModal();
      }

      self.updateCard = function(card) {
        Backend.getApi().then(function(api) {
        //angular.extend(card, {cardId:card.id});
          return api.cards.replace(card);
        });
      }

      self.openConfirmModal = function(idx,card) {
        $scope.idx = idx;
        $scope.card = card;
        self.modalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'settings/card/confirmation-modal.template.html',
          scope: $scope,
          size: 'sm'
        });
      }

      self.closeConfirmModal = function() {
        if(self.modalInstance) {
          self.modalInstance.close();
        }
      }

      self.addCard = function() {
        self.inserted = {
          number: '',
          name: ''
        }
        self.cards.push(self.inserted);
      }
    }]
  });