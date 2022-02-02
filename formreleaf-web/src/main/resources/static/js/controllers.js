/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/11/15.
 */
'use strict';

app.controller('ViewCtrl', function ($scope, $log, FormService) {
    $scope.form = {};
    //TODO revisit later
    var id = window.location.pathname.split("/")[3];

    FormService.form(id).then(function (form) {
        $scope.form = angular.fromJson(form);
    });
});

app.controller('DatepickerCtrl', function ($scope) {
    $scope.today = function () {
        $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
        $scope.dt = null;
    };

    // Disable weekend selection
    $scope.disabled = function (date, mode) {
        return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
    };

    $scope.toggleMin = function () {
        $scope.minDate = $scope.minDate ? null : new Date();
    };
    $scope.toggleMin();

    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = true;
    };

    $scope.open1 = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened1 = true;
    };

    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];
});


