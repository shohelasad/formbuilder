'use strict';

var NEW_ITEM_ADD_EVENT = "new-item-added";
var ITEM_EDIT_EVENT = "item-edited";

app.controller("ParticipantController", function ($scope, $log, $http, $modal, $window) {

    $scope.item = [];
    $scope.items = [];
    $scope.lastItemId = 1;
    $scope.profile = $("input[id='profile']").val();

    $scope.addNewItemModal = function (profile) {
        $modal.open({
            templateUrl: '../../../views/newParticipant.html',
            controller: "AddNewParticipantController",
            backdrop: 'static',
            size: 'lg',
            resolve: {
                profile: function () {
                    return profile;
                }
            }
        });
    };

    $scope.$on(NEW_ITEM_ADD_EVENT, function (event, args) {
        $scope.items = angular.fromJson(args);
    });

    $scope.editItem = function (item) {

        $modal.open({
            templateUrl: '../../../views/newParticipant.html',
            controller: "EditParticipantController",
            backdrop: 'static',
            size: 'lg',
            resolve: {
                item: function () {
                    return item;
                }
            }
        });
    };

    $scope.$on(ITEM_EDIT_EVENT, function (event, args) {
        $scope.items = angular.fromJson(args);
    });

    $scope.removeItem = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.items.length; i++) {
            if ($scope.items[i].id == id) {
                $scope.items.splice(i, 1);
                break;
            }
        }
    };

    $scope.deleteItem = function (id) {

        var response = $http.delete("/api/v1/user/" + $scope.profile + "/" + id + "/delete");

        response.success(function (data, status, headers, config) {
            if (status == '200') {
                $scope.items = angular.fromJson(data);
                $scope.alert_item_show = true;
                $scope.successDeletedMessage = "Successfully deleted";
                $scope.removeItem(id);
            }
        });

        response.error(function (data, status, headers, config) {
            $log.error(data, status);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_item_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        var response = $http.get('/api/v1/user/profile/' + $scope.profile);
        response.success(function (data, status, headers, config) {
            $scope.items = angular.fromJson(data);
        });
    };

    $scope.init();

    $scope.moveToNextPage = function () {
        $window.item.href = "/api/v1/user/create/contacts";
    }
});

app.controller("AddNewParticipantController", function ($scope, $rootScope, $http, profile, $modalInstance, $log, ProfileService) {
    $scope.item = {};
    $scope.profileType = profile;

    $scope.init = function () {
        if (profile == "concern" || profile == "medication") {
            ProfileService.getProfiles("participant").then(function (data) {
                $scope.participants = data;
            });
        }

        ProfileService.getForm(profile).then(function (response) {
            $scope.section = angular.fromJson(response.data);
        });
    };

    $scope.selectParticipantName = function (item) {
        $scope.selectedParticipant = item;
    };

    $scope.init();

    $scope.ok = function () {

        $scope.submitting = true;

        var formData = {
            "name": $scope.selectedParticipant,
            "profileData": angular.toJson($scope.section)
        };

        var response = $http.post("/api/v1/user/" + $scope.profileType + "/save", formData);
        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_ITEM_ADD_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(itemSaveFailureMessage);
            }

            $scope.submitting = false;
        });
        response.error(function (data, status, headers, config) {
            if (status == "400") {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            }
            else {
                $scope.formErrors.push(itemSaveFailureMessage);
            }
            $scope.submitting = false;
        });

    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

});

app.controller("EditParticipantController", function ($scope, $rootScope, item, $modalInstance, $http, $log, ProfileService) {
    $scope.item = {};

    if (item !== 'undefined') {
        $scope.item = item;
    }

    $scope.init = function () {
        $scope.profileType = item.profileDataType;

        $scope.section = angular.fromJson(item.profileData);
        $log.debug($scope.section);
    };

    $scope.init();

    $scope.ok = function () {

        $scope.submitting = true;
        var formData = {
            "id": item.id,
            "profileData": angular.toJson($scope.section)
        };

        var response = $http.post("/api/v1/user/" + $scope.profileType + "/save", formData);
        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(ITEM_EDIT_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(itemSaveFailureMessage);
            }

            $scope.submitting = false;
        });

        response.error(function (data, status, headers, config) {
            if (status == "400") {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
                $log.info("fieldErrors: " + objToString($scope.fieldErrors));
            }
            else {
                $scope.formErrors.push(itemSaveFailureMessage);
            }

            $scope.submitting = false;
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

