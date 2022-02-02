/**
 *  @author Bazlur Rahman Rokon
 *  @since 4/13/15.
 */

'use strict';


var app = angular
    .module('sportsRegApp', [
        'ui.bootstrap.showErrors',
        'ui.bootstrap',
        'checklist-model',
        'ui.bootstrap.transition',
        'jcs-autoValidate',
        'angular-loading-bar',
        'textAngular',
        'jcs-autoValidate'
    ])
    .run([
        'bootstrap3ElementModifier',
        function (bootstrap3ElementModifier) {
            bootstrap3ElementModifier.enableValidationStateIcons(false);
        }]);

app.controller("ToggleButtonCtrl", function ($scope, $log) {
    $scope.toggle = function (field) {
        field.concernedYes = !field.concernedYes;
    };
});

// Utility class
function objToString(obj, ndeep) {
    if (obj == null) {
        return String(obj);
    }
    switch (typeof obj) {
        case "string":
            return '"' + obj + '"';
        case "function":
            return obj.name || obj.toString();
        case "object":
            var indent = new Array(ndeep || 1).join('\t'), isArray = Array.isArray(obj);
            return '{['[+isArray] + Object.keys(obj).map(function (key) {
                    return '\n\t' + indent + key + ': ' + objToString(obj[key], (ndeep || 1) + 1);
                }).join(',') + '\n' + indent + '}]'[+isArray];
        default:
            return obj.toString();
    }
}

function onError(data) {
    alert("Exception details: " + JSON.stringify({data: data}));
}


function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

function clone(obj) {
    if (obj === null || typeof(obj) !== 'object')
        return obj;

    var temp = obj.constructor(); // changed

    for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
            temp[key] = clone(obj[key]);
        }
    }
    return temp;
}