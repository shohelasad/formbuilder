'use strict';

var NEW_ITEM_ADD_EVENT = "new-item-added";
var ITEM_EDIT_EVENT = "item-edited";
var NEW_ADDRESS_ADD_EVENT = "new-address-added";
var ADDRESS_EDIT_EVENT = "address-edited";
var NEW_CONTACT_ADD_EVENT = "new-contact-added";
var CONTACT_EDIT_EVENT = "contact-edited";
var NEW_DOCTOR_ADD_EVENT = "new-doctor-added";
var DOCTOR_EDIT_EVENT = "doctor-edited";
var NEW_INSURANCE_ADD_EVENT = "new-insurance-added";
var INSURANCE_EDIT_EVENT = "insurance-edited";

var contactSaveFailureMessage = "The contact information could not be saved !";
var addressSaveFailureMessage = "The address information could not be saved !";
var addressSaveFailureMessage = "The address information could not be saved !";
var addressSaveFailureMessage = "The address information could not be saved !";


var app = angular.module('profileApp', ['ui.bootstrap.showErrors', 'ui.bootstrap']);

app.controller("ParticipantController", function ($scope, $log, $http, $modal, $window) {

    $scope.item = [];
    $scope.items = [];
    $scope.lastItemId = 1;

    $scope.addNewItemModal = function (id) {
        $modal.open({
            templateUrl: '../../../views/newAddParticipant.html',
            controller: "AddNewParticipantController",
            backdrop: 'static',
            resolve: {
                id: function () {
                    return id;
                }
            }
        });
    };

    $scope.$on(NEW_ITEM_ADD_EVENT, function (event, args) {
        $scope.participants = args;
    });


    $scope.editItem = function (address) {

        $modal.open({
            templateUrl: '../../../views/newAddParticipant.html',
            controller: "EditParticipantController",
            backdrop: 'static',
            resolve: {
                item: function () {
                    return item;
                }
            }
        });

    };

    $scope.$on(ITEM_EDIT_EVENT, function (event, args) {
        $scope.addresses = args;
    });

    $scope.removeItem = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.addresses.length; i++) {
            if ($scope.items[i].id == id) {
                $scope.items.splice(i, 1);
                break;
            }
        }
    };

    $scope.deleteItem = function (addressId) {

        var response = $http.delete("/api/v1/user/participant/delete/" + addressId);

        response.success(function (data, status, headers, config) {
            $log.info("deleted : " + objToString(data));

            if (status == '200') {
                $scope.alert_address_show = true;
                $scope.successDeletedMessage = "Successfully deleted";
                $scope.removeItem(itemId);
            }
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_item_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        var response = $http.get('/api/v1/user/participants/');
        response.success(function (data, status, headers, config) {
            $scope.items = angular.fromJson(data);
            $log.info("items: " + objToString($scope.items))
        });
    };

    $scope.init();

    $scope.moveToNextPage = function () {
        $window.item.href = "/api/v1/user/create/contacts";
    }
});

app.controller("AddNewParticipantController", function ($scope, $rootScope, $http, id, $modalInstance, $log, StateService, CountryService) {
    $scope.item = {};
    $scope.item.id = id;
    $scope.item.name = "";
    $scope.item.addressLine1 = "";
    $scope.item.addressLine2 = "";
    $scope.item.street = "";
    $scope.item.city = "";
    $scope.item.state = "";
    $scope.item.zip = "";
    $scope.item.country = "";
    $scope.item = {};

    $scope.init = function () {
        /*	StateService.getStates().then(function(response){
         $scope.states = response.data;
         //$log.info("states: " + objToString($scope.states));
         });

         CountryService.getCountries().then(function(response){
         $scope.countries = response.data;
         //$log.info("countries: " + objToString($scope.countries));
         }); */
    };

    $scope.init();


    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/participant', angular.toJson($scope.item));

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

app.controller("EditParticipantController", function ($scope, $rootScope, item, $modalInstance, $http, $log, StateService, CountryService) {
    $scope.item = {};

    if (address !== 'undefined') {
        $scope.item = item;
    }

    $scope.init = function () {
        /*StateService.getStates().then(function(response){
         $scope.states = response.data;
         //$log.info("states: " + objToString($scope.states));
         });

         CountryService.getCountries().then(function(response){
         $scope.countries = response.data;
         //$log.info("countries: " + objToString($scope.countries));
         }); */
    };

    $scope.init();

    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/participant', angular.fromJson($scope.item));

        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_ITEM_EDIT_EVENT, data);
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

/*------------------------------------------------------address controller-------------------------------------*/


app.controller("AddressController", function ($scope, $log, $http, $modal, $window) {

    $scope.address = [];
    $scope.addresses = [];
    $scope.lastAddedAddressId = 1;

    $scope.addNewAddressModal = function (id) {
        $modal.open({
            templateUrl: '../../../views/newAddress.html',
            controller: "AddNewAddressController",
            backdrop: 'static',
            resolve: {
                id: function () {
                    return id;
                }
            }
        });
    };

    $scope.$on(NEW_ADDRESS_ADD_EVENT, function (event, args) {
        $scope.addresses = args;
    });


    $scope.editAddress = function (address) {

        $modal.open({
            templateUrl: '../../../views/newAddress.html',
            controller: "EditAddressController",
            backdrop: 'static',
            resolve: {
                address: function () {
                    return address;
                }
            }
        });

    };

    $scope.$on(ADDRESS_EDIT_EVENT, function (event, args) {
        $scope.addresses = args;
    });


    $scope.removeAddress = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.addresses.length; i++) {
            if ($scope.addresses[i].id == id) {
                $scope.addresses.splice(i, 1);
                break;
            }
        }
    };

    $scope.deleteAddress = function (addressId) {

        var response = $http.delete("/api/v1/user/address/delete/" + addressId);

        response.success(function (data, status, headers, config) {
            $log.info("deleted : " + objToString(data));

            if (status == '200') {
                $scope.alert_address_show = true;
                $scope.successDeletedMessage = "Address successfully deleted";
                $scope.removeAddress(addressId);
            }
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_address_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        var addressResponse = $http.get('/api/v1/user/addresses/');
        addressResponse.success(function (data, status, headers, config) {
            $scope.addresses = angular.fromJson(data);
            $log.info("addresses: " + objToString($scope.addresses))
        });


    };

    $scope.init();

    $scope.moveToContactPage = function () {
        $window.address.href = "/api/v1/user/create/contacts";
    }
});

app.controller("AddNewAddressController", function ($scope, $rootScope, $http, id, $modalInstance, $log, StateService, CountryService) {
    $scope.address = {};
    $scope.address.id = id;
    $scope.address.name = "";
    $scope.address.addressLine1 = "";
    $scope.address.addressLine2 = "";
    $scope.address.street = "";
    $scope.address.city = "";
    $scope.address.state = "";
    $scope.address.zip = "";
    $scope.address.country = "";
    $scope.addresses = {};

    $scope.init = function () {
        StateService.getStates().then(function (response) {
            $scope.states = response.data;
            //$log.info("states: " + objToString($scope.states));
        });

        CountryService.getCountries().then(function (response) {
            $scope.countries = response.data;
            //$log.info("countries: " + objToString($scope.countries));
        });
    };

    $scope.init();


    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/address', angular.toJson($scope.address));

        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_ADDRESS_ADD_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });

        response.error(function (data, status, headers, config) {
            if (status == "400") {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });

    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

});

app.controller("EditAddressController", function ($scope, $rootScope, address, $modalInstance, $http, $log, StateService, CountryService) {
    $scope.address = {};

    if (address !== 'undefined') {
        $scope.address = address;
    }

    $scope.init = function () {
        StateService.getStates().then(function (response) {
            $scope.states = response.data;
            //$log.info("states: " + objToString($scope.states));
        });

        CountryService.getCountries().then(function (response) {
            $scope.countries = response.data;
            //$log.info("countries: " + objToString($scope.countries));
        });
    };

    $scope.init();

    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/address', angular.toJson($scope.address));

        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_ADDRESS_EDIT_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
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
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });


    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("ContactController", function ($scope, $log, $http, $modal, $window) {

    // $scope.alertContactSuccess = false;
    // $scope.alertContactFailure = false;

    $scope.contacts = [];
    $scope.lastAddedContactId = 1;

    $scope.addNewContactModal = function (id) {
        $modal.open({
            templateUrl: '../../../views/newContact.html',
            controller: "AddNewContactController",
            backdrop: 'static',
            resolve: {
                id: function () {
                    return id;
                }
            }
        });
    };

    $scope.$on(NEW_CONTACT_ADD_EVENT, function (event, args) {
        $log.info("data: " + objToString(args));
        $scope.contacts = args;
    });


    $scope.editContact = function (contact) {
        $modal.open({
            templateUrl: '../../../views/newContact.html',
            controller: "EditContactController",
            backdrop: 'static',
            resolve: {
                contact: function () {
                    return contact;
                }
            }
        });
    };

    $scope.$on(CONTACT_EDIT_EVENT, function (event, args) {
        $scope.contacts = args;
    });


    $scope.removeContact = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.contacts.length; i++) {
            if ($scope.contacts[i].id == id) {
                $scope.contacts.splice(i, 1);
                break;
            }
        }
    };


    $scope.deleteContact = function (contactId) {

        var response = $http.delete("/api/v1/user/contact/delete/" + contactId);

        response.success(function (data, status, headers, config) {
            if (status == '200') {
                $scope.alert_contact_show = true;
                $scope.successDeletedMessage = "Contact successfully deleted";
                $scope.removeContact(contactId);
            }
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_contact_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {

        var contactResponse = $http.get('/api/v1/user/contacts/');
        contactResponse.success(function (data, status, headers, config) {
            $scope.contacts = angular.fromJson(data);
        });
    };

    $scope.init();


    $scope.moveToSectionPage = function () {
        $scope.userId = $("input[name='userId']").val();
        $window.address.href = "/api/v1/user/create/insurances";
    }
});


app.controller("AddNewContactController", function ($scope, $rootScope, id, $modalInstance, $http, $log) {
    $scope.contact = {};
    $scope.contact.id = id;
    $scope.contact.firstName = "";
    $scope.contact.lastName = "";
    $scope.contact.title = "";
    $scope.contact.workPhone = "";
    $scope.contact.cellPhone = "";
    $scope.contact.email = "";

    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/contact', angular.toJson($scope.contact));

        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_CONTACT_ADD_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });

        response.error(function (data, status, headers, config) {
            if (status == "400") {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });

    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditContactController", function ($scope, $rootScope, contact, $modalInstance, $http, $log) {
    $scope.contact = {};

    if (contact !== 'undefined') {
        $scope.contact = contact;
    }

    $scope.ok = function () {

        $scope.submitting = true;
        var response = $http.post('/api/v1/user/create/contact', angular.fromJson($scope.contact));

        response.success(function (data, status, headers, config) {
            if (status == "200") {
                $rootScope.$broadcast(NEW_CONTACT_EDIT_EVENT, data);
                $modalInstance.close();
            }
            else {
                $scope.formErrors.push(contactSaveFailureMessage);
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
                $scope.formErrors.push(contactSaveFailureMessage);
            }

            $scope.submitting = false;
        });


    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

/*----------------------------------------------doctor-----------------------------*/

app.controller("DoctorController", function ($scope, $log, $http, $modal, $window) {

    $scope.doctors = [];

    $scope.lastAddeddoctorId = 1;

    $scope.addNewDoctorModal = function (id) {
        $modal.open({
            templateUrl: '../../../views/newDoctor.html',
            controller: "AddNewDoctorController",
            backdrop: 'static',
            resolve: {
                id: function () {
                    return id;
                }
            }
        });
    };

    $scope.$on(NEW_DOCTOR_ADD_EVENT, function (event, args) {
        $log.info("data: " + objToString(args));

        $scope.lastAddeddoctorId++;
        $scope.doctors.push(args);

        /* var formData = {
         "doctors": $scope.doctors
         };*/

        var response = $http.post('/api/v1/user/create/doctor', args);

        response.success(function (data, status, headers, config) {
            $scope.doctor = angular.fromJson(data);
            $log.info("doctor: " + objToString($scope.doctor))
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });

    });


    $scope.editDoctor = function (doctor) {
        $modal.open({
            templateUrl: '../../../views/newDoctor.html',
            controller: "EditDoctorController",
            backdrop: 'static',
            resolve: {
                doctor: function () {
                    return doctor;
                }
            }
        });
    };

    $scope.$on(DOCTOR_EDIT_EVENT, function (event, args) {

        for (var i = 0; i < $scope.doctors.length; i++) {
            if ($scope.doctors[i].id == args.id) {
                $scope.doctors[i] = args;
                break;
            }
        }

        /* var formData = {
         "doctors": $scope.doctors
         };
         */
        var response = $http.post('/api/v1/user/create/doctor', args);

        response.success(function (data, status, headers, config) {
            $scope.doctor = angular.fromJson(data);
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });

    });

    $scope.removeDoctor = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.doctors.length; i++) {
            if ($scope.doctors[i].id == id) {
                $scope.doctors.splice(i, 1);
                break;
            }
        }
    };

    $scope.deleteDoctor = function (doctorId) {

        var response = $http.delete("/api/v1/user/doctor/delete/" + doctorId);

        response.success(function (data, status, headers, config) {
            if (status == '200') {
                $scope.alert_doctor_show = true;
                $scope.successDeletedMessage = "Doctor successfully deleted";
                $scope.removeDoctor(doctorId);
            }
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_doctor_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {

        var doctorResponse = $http.get('/api/v1/user/doctors/');
        doctorResponse.success(function (data, status, headers, config) {
            $scope.doctors = angular.fromJson(data);
        });
    };

    $scope.init();


    $scope.moveToSectionPage = function () {
        $scope.userId = $("input[name='userId']").val();
        $window.address.href = "/api/v1/user/create/doctors";
    }
});


app.controller("AddNewDoctorController", function ($scope, $rootScope, id, $modalInstance, $log) {
    $scope.doctor = {};
    $scope.doctor.id = id;
    $scope.doctor.firstName = "";
    $scope.doctor.lastName = "";
    $scope.doctor.workPhone = "";
    $scope.doctor.email = "";

    $scope.ok = function () {
        $rootScope.$broadcast(NEW_DOCTOR_ADD_EVENT, $scope.doctor);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditDoctorController", function ($scope, $rootScope, doctor, $modalInstance, $log) {
    $scope.doctor = {};

    if (doctor !== 'undefined') {
        $scope.doctor = doctor;
    }

    $scope.ok = function () {
        $rootScope.$broadcast(DOCTOR_EDIT_EVENT, $scope.doctor);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


/*----------------------------------------------Insurance-----------------------------*/

app.controller("InsuranceController", function ($scope, $log, $http, $modal, $window) {

    $scope.insurances = [];

    $scope.lastAddedinsuranceId = 1;

    $scope.addNewInsuranceModal = function (id) {
        $modal.open({
            templateUrl: '../../../views/newInsurance.html',
            controller: "AddNewInsuranceController",
            backdrop: 'static',
            resolve: {
                id: function () {
                    return id;
                }
            }
        });
    };

    $scope.$on(NEW_INSURANCE_ADD_EVENT, function (event, args) {
        $log.info("data: " + objToString(args));

        $scope.lastAddedinsuranceId++;
        $scope.insurances.push(args);

        /* var formData = {
         "insurances": $scope.insurances
         };
         */
        var response = $http.post('/api/v1/user/create/insurance', args);

        response.success(function (data, status, headers, config) {
            $scope.insurance = angular.fromJson(data);
            $log.info("insurance: " + objToString($scope.insurances))
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });

    });


    $scope.editInsurance = function (insurance) {
        $modal.open({
            templateUrl: '../../../views/newInsurance.html',
            controller: "EditInsuranceController",
            backdrop: 'static',
            resolve: {
                insurance: function () {
                    return insurance;
                }
            }
        });
    };

    $scope.$on(INSURANCE_EDIT_EVENT, function (event, args) {

        for (var i = 0; i < $scope.insurances.length; i++) {
            if ($scope.insurances[i].id == args.id) {
                $scope.insurances[i] = args;
                break;
            }
        }

        /* var formData = {
         "insurances": $scope.insurances
         };
         */
        var response = $http.post('/api/v1/user/create/insurance', args);

        response.success(function (data, status, headers, config) {
            $scope.insurance = angular.fromJson(data);
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });

    });

    $scope.removeInsurance = function (id) {
        $log.info("id: " + id);

        for (var i = 0; i < $scope.insurances.length; i++) {
            if ($scope.insurances[i].id == id) {
                $scope.insurances.splice(i, 1);
                break;
            }
        }
    };

    $scope.deleteInsurance = function (insuranceId) {

        var response = $http.delete("/api/v1/user/insurance/delete/" + insuranceId);

        response.success(function (data, status, headers, config) {
            if (status == '200') {
                $scope.alert_insurance_show = true;
                $scope.successDeletedMessage = "Insurance successfully deleted";
                $scope.removeInsurance(insuranceId);
            }
        });

        response.error(function (data, status, headers, config) {
            onError(data);
        });
    };

    $scope.successDeletedMessage = "";
    $scope.alert_insurance_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {

        var insuranceResponse = $http.get('/api/v1/user/insurances/');
        insuranceResponse.success(function (data, status, headers, config) {
            $scope.insurances = angular.fromJson(data);
        });
    };

    $scope.init();


    /* $scope.moveToSectionPage = function () {
     $scope.userId = $("input[name='userId']").val();
     $window.address.href = "/api/v1/user/create/insurances";
     }*/
});


app.controller("AddNewInsuranceController", function ($scope, $rootScope, id, $modalInstance, $log) {
    $scope.insurance = {};
    $scope.insurance.id = id;
    $scope.insurance.carrier = "";
    $scope.insurance.subscriberName = "";
    $scope.insurance.groupMember = "";

    $scope.ok = function () {
        $rootScope.$broadcast(NEW_INSURANCE_ADD_EVENT, $scope.insurance);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditInsuranceController", function ($scope, $rootScope, insurance, $modalInstance, $log) {
    $scope.insurance = {};

    if (insurance !== 'undefined') {
        $scope.insurance = insurance;
    }

    $scope.ok = function () {
        $rootScope.$broadcast(INSURANCE_EDIT_EVENT, $scope.insurance);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


//Utility class
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
            var indent = Array(ndeep || 1).join('\t'), isArray = Array.isArray(obj);
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