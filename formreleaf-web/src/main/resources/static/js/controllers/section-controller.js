/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/13/15.
 */


/**
 * Constants for the SectionController
 * @const
 * */
var NEW_SECTION_ADD_EVENT = "_new_section_added_";
var SECTION_EDIT_EVENT = "_section_edited_";

app.controller("SectionController", function ($scope, $modal, $window, $http, ProgramService, $log) {
    $scope.sections = [];

    $scope.alertSuccess = false;
    $scope.alertFailure = false;
    $scope.successMessage = {};
    $scope.errorMessage = {};

    var programId = $("input[name='programId']").val();

    $scope.addSectionToProgram = function () {
        $modal.open({
            templateUrl: '../../../views/newSection.html',
            controller: "AddNewSectionController",
            backdrop: 'static',
            resolve: {
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.editSection = function (section) {

        $modal.open({
            templateUrl: '../../../views/newSection.html',
            controller: "EditSectionController",
            backdrop: 'static',
            resolve: {
                section: function () {
                    return section;
                },
                programId: function () {
                    return programId;
                }
            }
        });
    };

    $scope.$on(NEW_SECTION_ADD_EVENT, function (event, args) {
        $scope.sections.push(args);
        $scope.alertSuccess = true;
        $scope.successMessage = "Section <strong>" + args.name + "</strong> has been added successfully"
    });

    $scope.$on(SECTION_EDIT_EVENT, function (event, args) {
        var section = _.find($scope.sections, {id: args.id});
        section = args;
        $scope.alertLocationSuccess = true;
        $scope.successMessage = "Section <strong>" + args.name + "</strong> has been updated successfully"
    });

    $scope.deleteSection = function (section) {
        ProgramService.removeSectionFromProgram(programId, section.id).then(function (data) {
            $scope.forms = _.without($scope.forms, _.findWhere($scope.forms, {id: location.id}));
            $scope.alertSuccess = true;
            $scope.successMessage = "Section <strong>" + section.name + "</strong> has been deleted successfully"
            $scope.init();
            $scope.submitting = false;
        }).catch(function (data) {
            $scope.alertFailure = true;
            $scope.errorMessage = "Section <strong>" + section.name + "</strong> couldn't be deleted. Section is already registered"
        });
        
        
    };

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    $scope.init = function () {
        ProgramService.findAllSections(programId).then(function (data) {
            $scope.sections = data;
        });
    };

    $scope.init();

    $scope.moveToSectionPage = function () {
        $window.location.href = "/program/create/" + programId + "/participant";
    };
});

app.controller("AddNewSectionController", function ($scope, $rootScope, programId, ProgramService, $modalInstance, $http, $log) {
    $scope.section = {};
    $scope.section.name = "";
    $scope.section.openDate = "";
    $scope.section.closeDate = "";
    $scope.section.sectionCode = "";
    $scope.section.spaceLimit = "";
    $scope.section.price = "";
    $scope.section.meetingTime = "";
    $scope.submiting = false;
    $scope.formErrors = [];

    $scope.ok = function () {
        $scope.submiting = true;

        ProgramService.addSectionToProgram(programId, $scope.section)
            .then(function (data) {
                $rootScope.$broadcast(NEW_SECTION_ADD_EVENT, data);
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

app.controller("EditSectionController", function ($scope, $rootScope, section, programId, ProgramService, $modalInstance, $http, $log) {
    $scope.section = section;
    $scope.submiting = false;

    $scope.ok = function () {
        $scope.submiting = true;

        ProgramService.updateSectionOfProgram(programId, section).then(function (data) {
            $rootScope.$broadcast(SECTION_EDIT_EVENT, data);
            $modalInstance.close();
        }).catch(function (data) {
            $scope.formErrors = data.errors;
            $scope.fieldErrors = data.fieldErrors;
            $scope.submiting = false;
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
