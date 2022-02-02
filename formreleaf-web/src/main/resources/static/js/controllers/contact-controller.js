var NEW_CONTACT_ADD_EVENT = "_new_contact_added_";
var CONTACT_EDIT_EVENT = "_contact_edited_";

app.controller("ContactController", function ($scope, $log, $http, $modal, $window, ProgramService) {

    $scope.alertContactSuccess = false;
    $scope.alertContactFailure = false;

    $scope.contacts = [];
    $scope.successMessage = {};
    $scope.errorMessage = {};

    var programId = $("input[name='programId']").val();

    $scope.addContactToProgram = function () {
        $modal.open({
            templateUrl: '../../../views/newContact.html',
            controller: "AddNewContactController",
            backdrop: 'static',
            resolve: {
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.$on(NEW_CONTACT_ADD_EVENT, function (event, contact) {
        $scope.contacts.push(contact);
        $scope.alertContactSuccess = true;
        $scope.successMessage = "New contact <strong>" + contact.firstName + " " + contact.lastName + "</strong> has been added successfully"
    });

    $scope.editContact = function (contact) {
        $modal.open({
            templateUrl: '../../../views/newContact.html',
            controller: "EditContactController",
            backdrop: 'static',
            resolve: {
                contact: function () {
                    return contact;
                },
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.$on(CONTACT_EDIT_EVENT, function (event, args) {
        var contact = _.find($scope.contacts, {id: args.id});
        contact = args;
        $scope.alertContactSuccess = true;
        $scope.successMessage = "Contact <strong>" + contact.firstName + " " + contact.lastName + "</strong> has been updated successfully"
    });

    $scope.deleteContact = function (contact) {
        ProgramService.removeContactFromProgram(programId, contact.id)
            .then(function () {
                $scope.contacts = _.without($scope.contacts, _.findWhere($scope.contacts, {id: contact.id}));
                $scope.alertContactSuccess = true;
                $scope.successMessage = "Contact <strong>" + contact.firstName + " " + contact.lastName + "</strong> has been deleted successfully"
                $scope.submitting = false;
            }).catch(function (data) {
                $scope.alertContactFailure = true;
                $scope.errorMessage = "Location <strong>" + contact.firstName + " " + contact.lastName + "</strong> couldn't be deleted"
            });
    };

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        ProgramService.findAllContacts(programId).then(function (data) {
            $scope.contacts = data;
        });
    };

    $scope.init();

    $scope.moveToSectionPage = function () {
        $window.location.href = "/program/create/" + programId + "/section";
    };
});

app.controller("AddNewContactController", function ($scope, $rootScope, programId, $modalInstance, ProgramService, $http, $log) {

    $scope.contact = {};
    $scope.contact.firstName = "";
    $scope.contact.lastName = "";
    $scope.contact.title = "";
    $scope.contact.workPhone = "";
    $scope.contact.cellPhone = "";
    $scope.contact.email = "";

    $scope.ok = function () {

        $scope.submitting = true;

        ProgramService.addContactToProgram(programId, $scope.contact)
            .then(function (data) {
                $rootScope.$broadcast(NEW_CONTACT_ADD_EVENT, data);
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

app.controller("EditContactController", function ($scope, $rootScope, programId, contact, ProgramService, $modalInstance, $http, $log) {
    $scope.contact = contact;

    $scope.ok = function () {
        ProgramService.updateContactOfProgram(programId, contact)
            .then(function (data) {
                $rootScope.$broadcast(CONTACT_EDIT_EVENT, data);
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
