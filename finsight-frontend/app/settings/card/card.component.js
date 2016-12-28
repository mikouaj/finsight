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

      self.removeCard = function(index) {
        Backend.getApi().then(function(api) {
          api.cards.delete(self.cards[index]).then(function() {
            self.cards.splice(index,1);
          });
        });
        self.closeConfirmModal();
      }

      self.updateCard = function(card) {
        Backend.getApi().then(function(api) {
          //angular.extend(card, {cardId:card.id});
          if(typeof card.id === 'undefined') {
            api.cards.createCard(card).then(function(response) {
              card.id = response.data.id;
              return response;
            });
            //return api.cards.createCard(card);
          } else {
            return api.cards.replace(card);
          }
        });
      }

      self.openConfirmModal = function(index) {
        $scope.index = index;
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

      self.cancelUpdate = function(index) {
        if(self.inserted == self.cards[index]) {
          self.cards.splice(index,1);
        }
      }

      self.checkEmpty = function(data) {
        if(!data) {
          return "can't be empty!";
        }
      }
    }]
  });