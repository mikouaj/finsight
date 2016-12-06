'use strict';

angular.
  module('settings').
  component('card', {
    templateUrl: 'settings/card/card.template.html',
    controller: ['Backend',function CardController(Backend) {
      var self = this;
      Backend.getApi().then(function(api) {
      	api.cards.getCards().then(function(response) {
      		self.cards = response.data;
      	});
      });
    }]
  });