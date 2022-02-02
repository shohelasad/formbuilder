/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/13/15.
 */
"use strict";

/***
 *  List of constant for LocationAndContactController
 *  @const
 * */
var NEW_LOCATION_ADD_EVENT = "_new_location_added_";
var LOCATION_EDIT_EVENT = "_location_updated_";

app.controller("LocationController", function ($scope, $log, $http, $modal, ProgramService, $window) {

    $scope.alertLocationSuccess = false;
    $scope.alertLocationFailure = false;

    $scope.forms = [];
    $scope.successMessage = {};
    $scope.errorMessage = {};

    var programId = $("input[name='programId']").val();

    $scope.addLocationToProgram = function () {
        $modal.open({
            templateUrl: '../../../views/newLocation.html',
            controller: "AddNewLocationController",
            backdrop: 'static',
            resolve: {
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.$on(NEW_LOCATION_ADD_EVENT, function (event, args) {
        $scope.forms.push(args);
        $scope.alertLocationSuccess = true;
        $scope.successMessage = "New Location <strong>" + args.name + "</strong> has been added successfully"
    });

    $scope.editLocation = function (form) {
        $modal.open({
            templateUrl: '../../../views/newLocation.html',
            controller: "EditLocationController",
            backdrop: 'static',
            resolve: {
                form: function () {
                    return form;
                },
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.$on(LOCATION_EDIT_EVENT, function (event, args) {
        var location = _.find($scope.forms, {id: args.id});
        location = args;
        $scope.alertLocationSuccess = true;
        $scope.successMessage = "Location <strong>" + location.name + "</strong> has been updated successfully"
    });

    $scope.deleteLocation = function (location) {
        $scope.submitting = true;

        ProgramService.removeLocationFromProgram(programId, location.id)
            .then(function () {
                $scope.forms = _.without($scope.forms, _.findWhere($scope.forms, {id: location.id}));
                $scope.alertLocationSuccess = true;
                $scope.successMessage = "Location <strong>" + location.name + "</strong> has been deleted successfully"
                $scope.submitting = false;
            }).catch(function (data) {
                $scope.alertLocationFailure = true;
                $scope.errorMessage = "Location <strong>" + location.name + "</strong> couldn't be deleted"
            });
    };

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        ProgramService.findAllLocations(programId).then(function (data) {
            $scope.forms = data;
        });
    };

    $scope.init();
});

app.controller("AddNewLocationController", function ($scope, $rootScope, programId, $modalInstance, $http, $log, ProgramService, StateService, CountryService) {
    $scope.form = {};
    $scope.form.name = "";
    $scope.form.addressLine1 = "";
    $scope.form.addressLine2 = "";
    $scope.form.street = "";
    $scope.form.city = "";
    $scope.form.state = "";
    $scope.form.zip = "";
    $scope.form.country = "";
    $scope.formErrors = [];

    $scope.submitting = false;

    $scope.ok = function () {
        $scope.submitting = true;
        ProgramService.addLocationToProgram(programId, $scope.form)
            .then(function (data) {
                $rootScope.$broadcast(NEW_LOCATION_ADD_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
                $scope.submitting = false;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.init = function () {
        StateService.getStates().then(function (data) {
            $scope.states = data;
        });

        CountryService.getCountries().then(function (data) {
            $scope.countries = data;
        });
    };

    $scope.init();

});

app.controller("EditLocationController", function ($scope, $rootScope, programId, form, $modalInstance, $http, $log, ProgramService, StateService, CountryService) {
    $scope.form = form;

    $scope.ok = function () {

        $scope.submitting = true;
        ProgramService.updateLocationOfProgram(programId, form)
            .then(function (data) {
                $rootScope.$broadcast(LOCATION_EDIT_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
                $scope.submitting = false;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.init = function () {
        StateService.getStates().then(function (data) {
            $scope.states = data;
        });

        CountryService.getCountries().then(function (data) {
            $scope.countries = data;
        });
    };

    $scope.init();
});
