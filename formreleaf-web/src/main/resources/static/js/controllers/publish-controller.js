/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/20/15.
 */

"use strict";
app.controller("PublishController", function ($scope, $http, $log, $window, ProgramService) {
    $scope.publish = {};
    $scope.errors = {};

    $scope.programId = $("input[name='programId']").val();
    $scope.publish.registrationOpened = false;
    $scope.publish.programOpened = false;

    $scope.publishProgram = function () {
        ProgramService.savePublish($scope.programId, $scope.publish).then(function (data) {
            $window.location.href = "/program/list";
        }).catch(function (errors) {
            $scope.errors = errors;
        });
    };

    $scope.initialize = function () {

        ProgramService.findPublish($scope.programId).then(function (data) {
            $scope.publish = angular.fromJson(data);

            if ($scope.publish.programStatus == "OPEN") {
                $scope.publish.registrationOpened = true;
            }

            if ($scope.publish.publishStatus=="PUBLIC") {
                $scope.publish.programOpened = true;
            }
        });
    };

    $scope.initialize();
});