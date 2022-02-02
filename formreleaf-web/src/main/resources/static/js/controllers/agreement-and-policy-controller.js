/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/13/15.
 *
 * Note: this is quick and dirty work-
 * Need to revisit/refactor/separate
 */

var NEW_AGREEMENT_ADD_EVENT = "new-agreement-added";
var AGREEMENT_EDIT_EVENT = "agreement-edited";
var NEW_POLICY_ADD_EVENT = "new-policy-added";
var POLICY_EDIT_EVENT = "policy-edited";

app.controller("AgreementController", function ($scope, $modal, $window, AgreementService, $http, $log) {
    $scope.agreements = [];  
    $scope.successMessage = {};
    $scope.errorMessage = {};
    $scope.alertAgreementSuccess = false;
    $scope.alertAgreementFailure = false;

    $scope.addNewAgreementToOrganization = function () {
        $modal.open({
            templateUrl: '../../../views/newAgreement.html',
            controller: "AddNewAgreementController",
            backdrop: 'static',
            size: 'lg'
        });
    };

    $scope.deleteAgreement = function (id) {
    	$scope.submitting = true;
        AgreementService.deleteAgreement(id).then(function (data) {
            $scope.agreements = _.without($scope.agreements, _.findWhere($scope.agreements, {id: id}));
            $scope.successMessage = "Agrrement has been deleted successfully"
            $scope.submitting = false;
            $scope.alertAgreementSuccess = true;
        });
    };

    $scope.editAgreement = function (agreement) {
        $modal.open({
            templateUrl: '../../../views/newAgreement.html',
            controller: "EditAgreementController",
            backdrop: 'static',
            size: 'lg',
            resolve: {
                agreement: function () {
                    return agreement;
                }
            }
        });
    };

    $scope.$on(NEW_AGREEMENT_ADD_EVENT, function (event, args) {
        $scope.agreements.push(args);
    });

    $scope.$on(AGREEMENT_EDIT_EVENT, function (event, args) {
        var agreement = _.find($scope.agreements, {id: args.id});
        agreement = args;
    });

    $scope.moveToSectionPage = function () {
        var programId = $("input[name='programId']").val();

        $window.location.href = "/program/create/" + programId + "/publish";
    };

    $scope.init = function () {
        AgreementService.findAllAgreements().then(function (data) {
            $scope.agreements = data;
        });
    };

    $scope.submit = function () {
        $scope.selectedIds = [];
    };
    
    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init();
});

app.controller("AddNewAgreementController", function ($scope, $timeout, AgreementService, $http, $rootScope, $modalInstance, $log) {
    $scope.agreement = {};
    $scope.agreement.title = "";
    $scope.agreement.mustBeAgreed = false;
    $scope.agreement.seleted = false;
    $scope.agreement.details = "";

    $scope.ok = function () {
        AgreementService.saveAgreement($scope.agreement)
            .then(function (data) {
                $rootScope.$broadcast(NEW_AGREEMENT_ADD_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditAgreementController", function ($scope, $rootScope, AgreementService, agreement, $modalInstance, $log) {
    $scope.agreement = agreement;

    $scope.ok = function () {
        AgreementService.updateAgreement($scope.agreement)
            .then(function (data) {
                $rootScope.$broadcast(AGREEMENT_EDIT_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

// Policy Controllers

app.controller("PolicyController", function ($scope, $modal, PolicyService, $window, $http, $log) {
    $scope.policies = [];
    $scope.successMessage = {};
    $scope.errorMessage = {};
    $scope.alertPolicySuccess = false;
    $scope.alertPolicyFailure = false;
  
    $scope.addNewPolicy = function () {
        $modal.open({
            templateUrl: '../../../views/newPolicy.html',
            controller: "AddNewPolicyController",
            backdrop: 'static',
            size: 'lg'
        });
    };
    
    $scope.deletePolicy = function (id) {
    	$scope.submitting = true;
        PolicyService.deletePolicy(id).then(function (data) {
            $scope.policies = _.without($scope.policies, _.findWhere($scope.policies, {id: id}));
            $scope.successMessage = "Policy has been deleted successfully"
            $scope.submitting = false;
            $scope.alertPolicySuccess = true;
        });
    };

    $scope.editPolicy = function (policy) {
        $modal.open({
            templateUrl: '../../../views/newPolicy.html',
            controller: "EditPolicyController",
            backdrop: 'static',
            size: 'lg',
            resolve: {
                policy: function () {
                    return policy;
                }
            }
        });
    };

    $scope.$on(NEW_POLICY_ADD_EVENT, function (event, args) {
        $scope.policies.push(args);
    });

    $scope.$on(POLICY_EDIT_EVENT, function (event, args) {
        var agreement = _.find($scope.policies, {id: args.id});
        agreement = args;
    });

    $scope.moveToSectionPage = function () {
        $scope.programId = $("input[name='programId']").val();
        $window.location.href = "/program/create/" + $scope.programId + "/publish";
    };

    $scope.init = function () {
        PolicyService.findAllPolicies().then(function (data) {
            $scope.policies = (data);
        });
    };
    
    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init();
});

app.controller("AddNewPolicyController", function ($scope, $rootScope, PolicyService, $modalInstance, $log) {
    $scope.policy = {};
    $scope.policy.title = "";
    $scope.policy.seleted = false;
    $scope.policy.details = "";

    $scope.ok = function () {
        PolicyService.savePolicy($scope.policy)
            .then(function (data) {
                $rootScope.$broadcast(NEW_POLICY_ADD_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditPolicyController", function ($scope, $rootScope, policy, PolicyService, $modalInstance, $log) {
    $scope.policy = {};

    if (policy !== 'undefined') {
        $scope.policy = policy;
    }

    $scope.ok = function () {
        PolicyService.updatePolicy($scope.policy)
            .then(function (data) {
                $rootScope.$broadcast(POLICY_EDIT_EVENT, data);
                $modalInstance.close();
            }).catch(function (data) {
                $scope.formErrors = data.errors;
                $scope.fieldErrors = data.fieldErrors;
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

//agreement and policy both

app.controller("AgreementAndPolicyController", function ($scope, $http, $log, ProgramService, $window) {
    $scope.program = {};
    $scope.program.agreements = [];
    $scope.program.policies = [];
    $scope.url = "";

    var programId = $("input[name='programId']").val();

    $scope.addPolicyToProgram = function (policy) {
        ProgramService.addPolicyToProgram(programId, policy).then(function (data) {
            $scope.program.policies = data;
        }).catch(function (data) {
            $scope.errors = data.errors;
        });
    };

    $scope.addAgreementToProgram = function (agreement) {
        ProgramService.addAgreementToProgram(programId, agreement).then(function (data) {
            $scope.program.agreements = data;
        }).catch(function (data) {
            $scope.errors = data.errors;
        });
    };

    $scope.removeAgreementFromProgram = function (agreement) {
        ProgramService.removeAgreementFromProgram(programId, agreement.id)
            .then(function (data) {
                $scope.program.agreements = _.without($scope.program.agreements, _.findWhere($scope.program.agreements, {id: agreement.id}));
            }).catch(function (data) {
                $scope.errors = data.errors;
            });
    };

    $scope.removePolicyFromProgram = function (policy) {
        ProgramService.removePolicyFromProgram(programId, policy.id)
            .then(function (data) {
                $scope.program.policies = _.without($scope.program.policies, _.findWhere($scope.program.policies, {id: policy.id}));
            }).catch(function (data) {
                $scope.errors = data.errors;
            });
    };

    $scope.initialize = function () {
        ProgramService.findAllAgreements(programId).then(function (data) {
            $scope.program.agreements = data;
        });

        ProgramService.findAllPolicies(programId).then(function (data) {
            $scope.program.policies = data;
        });
    };

    $scope.initialize();

    $scope.moveToNextPage = function () {
        $window.location.href = "/program/create/" + programId + "/publish";
    }
});
