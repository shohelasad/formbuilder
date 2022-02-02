/**
 * @author Bazlur Rahman Rokon
 * @since 5/14/15.
 *
 * this is quick and dirty work, have to revisit and refactor
 */
"use strict";

var sectionNotSelected = "Please select at least one section!";
var agreementNotSelected = "Please read all `must be agreed` agreements and check the box to mark your acknowledgement!!";
var signatureNotAdded = "Please put your sign before you submit";
var participationInformation = "Please fill out the required participation information";

app.controller("RegistrationController", function ($scope, $http, $q, $log, $window, $document, $timeout, ProgramService, UserService, RegistrationService, $location, $anchorScroll) {
    $scope.form = {};
    $scope.sections = {};
    $scope.agreements = {};
    $scope.policies = {};
    $scope.programId = $("input[id='programId']").val();
    $scope.registrationId = $("input[id='registrationId']").val();
    $scope.emptyImage = null;
    $scope.signIt = true;
    $scope.editIt = false;
    $scope.registration = {};
    $scope.formErrors = [];
    $scope.hasError = false;
    $scope.isOrganization = false;
    $scope.isAuthenticated = false;

    $scope.signatures = [];

    $scope.init = function () {

        if ($scope.registrationId) {  	
        	UserService.isCurrentLoggedUserIsOrganization().then(function (data) {
                 $scope.isOrganization = data;
            });
        	
            RegistrationService.findFormData($scope.registrationId).then(function (data) {
                $scope.form = data;
            });

            RegistrationService.findAllSections($scope.registrationId).then(function (data) {
                $scope.sections = data;
            });

            RegistrationService.findSignature($scope.registrationId).then(function (data) {

                _.chain(data).each(function (signature) {
                    var newSignee = {};
                    newSignee.id = signature.id;
                    newSignee.sign = signature.sign;
                    newSignee.canvasId = "signaturePad-" + signature.agreement.id;
                    newSignee.agreement = signature.agreement;

                    if (signature.sign) {
                        newSignee.signIt = false;
                        newSignee.editIt = true;
                    } else {
                        newSignee.signIt = true;
                        newSignee.editIt = false;
                    }

                    $scope.signatures.push(newSignee);
                });

                $timeout(function () {
                    angular.forEach($scope.signatures, function (signature, index) {
                        var elementId = 'signaturePad-' + signature.agreement.id;
                        var drawingCanvas = document.getElementById(elementId);
                        signature['signaturePad'] = new SignaturePad(drawingCanvas, {
                            onEnd: function () {
                                $scope.formOnChange();
                            }
                        });
                    });
                }, 0);
            });
            
            ProgramService.findAllPolicies($scope.programId).then(function (data) {
                $scope.policies = data;
            });
            
        } 
        else {
        	
        	$scope.sections = ProgramService.findAllSection($scope.programId).then(function (data) {
                $scope.sections = data;
            });
        	
        	UserService.isAuthenticated().then(function (data) {	
        		$scope.isAuthenticated = data;
        		if($scope.isAuthenticated) {
	        		UserService.isCurrentLoggedUserIsOrganization().then(function (data) {
	        	            $scope.isOrganization = data;
	        	        });
		        	
		            ProgramService.findFormTemplate($scope.programId).then(function (data) {
		                angular.forEach(data, function (form) {
		                    $scope.form = form;
		                });
		            });
		
		            ProgramService.findAllAgreements($scope.programId).then(function (data) {
		                $scope.agreements = data;
		
		                _.chain($scope.agreements).each(function (agreement) {
		                    var newSignee = {};
		                    newSignee.sign = null;
		                    newSignee.canvasId = "signaturePad-" + agreement.id;
		                    newSignee.agreement = agreement;
		                    newSignee.signIt = true;
		                    newSignee.editIt = false;
		                    $scope.signatures.push(newSignee);
		                });
		
		                $timeout(function () {
		                    angular.forEach($scope.signatures, function (signature, index) {
		                        var elementId = 'signaturePad-' + signature.agreement.id;
		                        var drawingCanvas = document.getElementById(elementId);
		                        signature['signaturePad'] = new SignaturePad(drawingCanvas, {
		                            onEnd: function () {
		                                $scope.formOnChange();
		                            }
		                        });
		                    });
		                }, 0);
		            });
		            
		            ProgramService.findAllPolicies($scope.programId).then(function (data) {
		                $scope.policies = data;
		            });
        		}
        	});
        }

    };

    $scope.updateSelection = function (position, sections) {
        angular.forEach(sections, function (section, index) {
            if (position != index)
                section.selected = false;
        });
    };

    $scope.edit = function (id) {
        _.chain($scope.signatures).each(function (signature) {
            if (signature.canvasId == id) {
                signature.signIt = !signature.signIt;
                signature.editIt = !signature.editIt;
                signature.signaturePad.clear();
            }
        });
    };

    $scope.clear = function (id) {
        _.chain($scope.signatures).each(function (signature) {
            if (signature.canvasId == id) {
                signature.signaturePad.clear();
            }
        });
    };

    $scope.loadPreviousInfo = function () {
        ProgramService.loadPreviousInfo($scope.programId).then(function (data) {
            $scope.form = data;
        }).catch(function (error) {
            $scope.hasError = true;
            angular.forEach(error.errors, function (value, key) {
                $scope.formErrors.push(value);
            });
        });
    };

    $scope.clearErrors = function () {
        $scope.hasError = false;
        $scope.formErrors = [];
    };

    $scope.submitForm = function () {
        $scope.clearErrors();

        var hasFieldError = false;

        if ($scope.participantForm.$invalid) {
            var error = $scope.participantForm.$error;
            angular.forEach(error.required, function (field) {
                if (field.$invalid) {
                    var name = field.$name;
                    if ((name.search("concernsAndComments") != -1) || (name.search("medications") != -1)) {
                        //do nothing
                    } else {
                        hasFieldError = true;
                    }
                }
            });

            if (hasFieldError) {
                $scope.hasError = true;
                $scope.formErrors.push(participationInformation);
            }
        }

        var sectionIds = _.chain($scope.sections).filter(function (section) {
            return section.selected;
        }).flatten().pluck("id").uniq().value();

        if (($scope.sections != null && $scope.sections.length > 0) && (sectionIds == null || sectionIds.length < 1)) {
            $scope.hasError = true;
            $scope.formErrors.push(sectionNotSelected);
        }

        var errorCount = 0;
        _.chain($scope.signatures).each(function (signature) {
            if (signature.signaturePad.isEmpty() && signature.signIt == true) {
                $scope.hasError = true;
                signature['error'] = "You must put your signature here";
                errorCount++;
            } else if (!signature.signaturePad.isEmpty() && signature.signIt == true) {
                signature.sign = signature.signaturePad.toDataURL();
                signature['error'] = null;
            }
        });

        if (errorCount > 0) {
            $scope.formErrors.push(signatureNotAdded);
            errorCount = 0;
        }

        if ($scope.hasError) {
            $scope.moveToErrorAnchor();

            return;
        }

        RegistrationService.save(getFormData()).then(function (status) {
            $window.location.href = "/registration/confirmation";
        }).catch(function (data) {
            $scope.hasError = false;
            $scope.formErrors = data.errors;
        });
    };

    function getFormData() {
        var sectionIds = _.chain($scope.sections).filter(function (section) {
            return section.selected;
        }).flatten().pluck("id").uniq().value();

        _.chain($scope.signatures).each(function (signature) {
            if (!signature.signaturePad.isEmpty() && signature.signIt == true) {
                signature.sign = signature.signaturePad.toDataURL();
            }
        });

        return {
            "id": $scope.registrationId,
            "programId": $scope.programId,
            "formTemplate": angular.toJson($scope.form),
            "sectionIds": sectionIds,
            "signatures": $scope.signatures
        };
    }

    $scope.moveToErrorAnchor = function () {
        $location.hash('errorBlock').replace();
        $anchorScroll();
    };

    $scope.init();

    var auto_save_interval_threshold = 10000;
    var timeout = null;

    $scope.formOnChange = function () {
        $log.debug("form changed");

        // if user organization, we are not going to save anything
        if ($scope.isOrganization) {
            return;
        }

        if (timeout) {
            $log.debug("another change detected, appending changes");
            $timeout.cancel(timeout);
        }

        timeout = $timeout(function () {
            $scope.saveUpdates();
        }, auto_save_interval_threshold);
    };

    $scope.saveUpdates = function () {
        RegistrationService.autSave(getFormData()).then(function (data, status) {
            $scope.registrationId = data.id;
            displaySuccessToast(data.lastModifiedDate);
        }).catch(function (data) {
            $scope.hasError = false;
            $scope.formErrors = data.errors;
        });
    };
});

function displaySuccessToast(data) {

    toastr.info("at " + new Date(data).toLocaleTimeString().toString(),
        "Your progress has been saved",
        {
            "newestOnTop": true,
            "positionClass": 'toast-top-right',
            "timeOut": "0",
            "closeButton": true,
            "closeHtml": '<button type="button">&times;</button>'
        })
}