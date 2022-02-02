var NEW_USER_ADD_EVENT = "_new_user_added_";
var USER_EDIT_EVENT = "_user_edited_";

app.controller("UserController", function ($scope, $log, $http, $modal, $window, UserService) {

    $scope.alertUserSuccess = false;
    $scope.alertUserFailure = false;

    $scope.users = [];
    $scope.successMessage = {};
    $scope.errorMessage = {};

    var organizationId = $("input[name='organizationId']").val();

    $scope.addUserToOrganization = function () {
        $modal.open({
            templateUrl: '../../../views/newUser.html',
            controller: "AddNewUserController",
            backdrop: 'static',
            resolve: {
                organizationId: function () {
                    return organizationId;
                }
            }
        });
    };

    $scope.$on(NEW_USER_ADD_EVENT, function (event, user) {
        $scope.users.push(user);
        $scope.alertUserSuccess = true;
        $scope.successMessage = "New user <strong>" + user.firstName + " " + user.lastName + "</strong> has been added successfully"
    });

    $scope.editUser = function (user) {
        $modal.open({
            templateUrl: '../../../views/newUser.html',
            controller: "EditUserController",
            backdrop: 'static',
            resolve: {
                user: function () {
                    return user;
                },
                organizationId: function () {
                    return organizationId;
                }
            }
        });
    };

    $scope.$on(USER_EDIT_EVENT, function (event, args) {
        var user = _.find($scope.users, {id: args.id});
        user = args;
        $scope.alertUserSuccess = true;
        $scope.successMessage = "User <strong>" + user.firstName + " " + user.lastName + "</strong> has been updated successfully"
    });

    $scope.deleteUser = function (user) {
        UserService.removeUserFromOrganization(organizationId, user.id)
            .then(function () {
                $scope.users = _.without($scope.users, _.findWhere($scope.user, {id: user.id}));
                $scope.alertUserSuccess = true;
                $scope.successMessage = "User <strong>" + user.firstName + " " + user.lastName + "</strong> has been deleted successfully"
                $scope.submitting = false;
            }).catch(function (data) {
                $scope.alertUserFailure = true;
                $scope.errorMessage = "User <strong>" + user.firstName + " " + user.lastName + "</strong> couldn't be deleted"
            });
    };

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        UserService.findAllUsers(organizationId).then(function (data) { 
            $scope.users = data; alert($scope.users);
        });
    };

    $scope.init();

});

app.controller("AddNewUserController", function ($scope, $rootScope, organizationId, $modalInstance, UserService, $http, $log) {

    $scope.user = {};
    $scope.user.firstName = "";
    $scope.user.lastName = "";
    $scope.user.password = "";
    $scope.user.email = "";

    $scope.ok = function () {

        $scope.submitting = true;

        UserService.addUserToOrganization(organizatioId, $scope.user)
            .then(function (data) {
                $rootScope.$broadcast(NEW_USER_ADD_EVENT, data);
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
});

app.controller("EditUserController", function ($scope, $rootScope, organizationId, user, UserService, $modalInstance, $http, $log) {
    $scope.user = user;

    $scope.ok = function () {
        UserService.updateUserOfOrganization(organizationId, user)
            .then(function (data) {
                $rootScope.$broadcast(USER_EDIT_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
                $scope.submitting = false;
            })
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
