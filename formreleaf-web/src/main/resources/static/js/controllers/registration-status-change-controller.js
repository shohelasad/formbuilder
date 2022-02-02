/**
 * @author Bazlur Rahman Rokon
 * @since 6/6/15.
 * Note: this is quick and dirty work-
 * Need to revisit/refactor/separate
 */
'use strict';

var STATUS_CHANGED_EVENT = "status-changed";

app.controller("RegistrationStatusChangeController", function ($scope, $http, $q, $log, $window, $modal, ProgramService) {
    $scope.registrations = {};
    $scope.checkedAll = false;

    $scope.onCheckChange = function () {
        if ($scope.checkedAll) {
            _.chain($scope.registrations).each(function (registration) {
                registration.selected = true;
            })
        } else {
            _.chain($scope.registrations).each(function (registration) {
                registration.selected = false;
            })
        }
    };

    $scope.init = function () {
        $scope.programId = $("input[id='programId']").val();

        ProgramService.findAllRegistrations($scope.programId).then(function (data) {
            $scope.registrations = data;
        });
    };

    $scope.changeStatus = function () {
        $modal.open({
            templateUrl: '../../../views/statusChange.html',
            controller: "ChangeStatusController",
            backdrop: 'static',
            resolve: {
                registrations: function () {
                    return $scope.registrations;
                },
                programId: function () {
                    return $scope.programId;
                }
            }
        });
    };

    $scope.$on(STATUS_CHANGED_EVENT, function (event, args) {
        $scope.registrations = args;
    });

    $scope.init();
});

app.controller("ChangeStatusController", function ($scope, $rootScope, $http, registrations, programId, $modalInstance, ProgramService, $log) {
    $scope.registrationStatuses = [{"name": "Completed", "value": "COMPLETED"}, {
        "name": "Canceled",
        "value": "CANCELED"
    }];
    $scope.registrationApprovals = [{"name": "Approved", "value": "APPROVED"}, {
        "name": "Not Approved",
        "value": "NOT_APPROVED"
    }];

    $scope.registrationStatus = {};
    $scope.registrationApproval = {};

    $scope.ok = function () {

        var selectedRegistration = _.chain(registrations).filter(function (registration) {
            return registration.selected;
        }).each(function (registration) {
            registration.registrationApproval = $scope.registrationApproval.value;
            registration.registrationStatus = $scope.registrationStatus.value;
        }).value();

        ProgramService.changeStatus(programId, selectedRegistration).then(function (data) {
            $rootScope.$broadcast(STATUS_CHANGED_EVENT, data);
            $modalInstance.close();
        }).catch(function (data) {
            $log.error(data);
            $scope.errorMessage = "Unable to change status";
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
