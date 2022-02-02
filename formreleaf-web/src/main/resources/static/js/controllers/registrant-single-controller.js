/**
 * @author Bazlur Rahman Rokon
 *
 * @date 6/3/15.
 */

app.controller("RegistrationSingleViewController", function ($scope, $http, $q, $log, $document, $window, RegistrationService, UserService, ProgramService) {
    $scope.formData = {};
    $scope.signatures = {};
    $scope.policies = {};
    $scope.isOrganization = false;
    $scope.sections = {};
    $scope.registration = {};
    $scope.editSection = false;

    $scope.registrationId = $("input[id='registrationId']").val();
    $scope.programId = $("input[id='programId']").val();

    $scope.registrationApprovals = [
        {"name": "Approved", "value": "APPROVED"},
        {"name": "Not Approved", "value": "NOT_APPROVED"}
    ];

    $scope.registrationApproval = {};

    $scope.init = function () {
        UserService.isCurrentLoggedUserIsOrganization().then(function (data) {
            $scope.isOrganization = data;
        });
    };

    $scope.viewSectionEdit = function (registrationId, programId) {

        RegistrationService.findProgramSections(programId).then(function (data) {
            $scope.sections = data;
        });

        $scope.editSection = !$scope.editSection;
    };

    $scope.viewParticipant = function (registrationId, programId) {

        $q.all([RegistrationService.findFormData(registrationId),
            RegistrationService.findSignature(registrationId),
            ProgramService.findAllPolicies(programId)
        ]).then(function (data) {
            $scope.formData = data[0];
            $scope.signatures = data[1];
            $scope.policies = data[2];
        });
    };

    $scope.hasFieldsInBlocks = function (section) {
        return _.chain(section.blocks).filter(function (block) {
                return _.chain(block.fields).filter(function (filed) {
                        return filed.selected;
                    }).size() > 0;
            }).size() > 0;
    };

    $scope.changeStatus = function () {
        var registration = {
            "id": $scope.registrationId,
            "registrationApproval": $scope.registrationApproval.value
        };

        ProgramService.changeStatus($scope.programId, [registration]).then(function (data) {
            $window.location.reload();
        });
    };


    $scope.changeSection = function () {
        RegistrationService.changeSection($scope.registrationId, angular.fromJson($scope.section).id).then(function (data) {
            $window.location.reload();
        });
    };

    $scope.init();

    $scope.deleteRegistration = function (id) {
        RegistrationService.deleteRegistration(id)
            .then(function (data) {
                $window.location.href = "/registration/list";
            })
    }
});
