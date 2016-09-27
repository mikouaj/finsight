'use strict';

angular.
  module('settings').
  component('card', {
    templateUrl: 'settings/card/card.template.html',
    controller: ['Card',function CardController(Card) {
      var self = this;
      self.cards = Card.query();
    }]
  });