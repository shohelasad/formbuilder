'use strict';

var fieldIsEmpty = "No fields has been selected! Please choose your fields.";

var NEW_ITEM_ADD_EVENT = "new-item-added";
var itemSaveFailureMessage = "Item can not be saved";


app.controller("ReportDefinitionController", function ($scope, $log, $http, $modal, $window, $q, ReportService) {

    $scope.fileName = "";
    $scope.checkedAll = false;
    $scope.checkList = [];
    $scope.reportFilter = {};
    $scope.formErrors = [];

    $scope.onCheckChange = function () {
        if ($scope.checkedAll) {
            _.each($scope.reportFilter.selectedBlocks, function (block) {
                _.each(block.fields, function (field) {
                    field.selected = true;
                })
            })
        } else {
            _.each($scope.reportFilter.selectedBlocks, function (block) {
                _.each(block.fields, function (field) {
                    field.selected = false;
                })
            })
        }
    };

    $scope.selectAll = function (block, index) {
        if ($scope.checkList[index]) {
            _.each(block.fields, function (field) {
                field.selected = true;
            })
        } else {
            _.each(block.fields, function (field) {
                field.selected = false;
            })
        }
    };

    $scope.programFilter = function (program) {
        return !!(program.selected || _.find(program.sections, {"selected": true}));
    };

    $scope.init = function () {
        var reportId = $("input[id='reportId']").val();

        ReportService.getReportDefinition(reportId).then(function (data) {
            $scope.reportFilter = data;

            $scope.checkList = _.map(_.range($scope.reportFilter.selectedBlocks.length), function () {
                return false;
            });
        });
    };

    $scope.init();

    $scope.clearErrors = function () {
        $scope.formErrors = [];
    };

    $scope.filterItem = function () {
        if ($scope.selected_option != "") {
            $modal.open({
                templateUrl: '../../../views/newFilter.html',
                controller: "AddNewFilterController",
                backdrop: 'static',
                size: 'md',
                resolve: {
                    filterType: function () {
                        return $scope.selected_option;
                    },
                    reportFilter: function () {
                        return $scope.reportFilter;
                    }
                }
            });
        }
    };

    $scope.editFilterItem = function (filterItem) {
        $scope.selected_option = filterItem;
        $scope.filterItem();
    };

    $scope.removeRegistrantName = function () {
        $scope.reportFilter.registrantName = null;
    };

    $scope.removePrograms = function () {
        _.each($scope.reportFilter.programs, function (program) {
            program.selected = false;
            _.each(program.sections, function (section) {
                section.selected = false;
            })
        })
    };

    $scope.findSelectedApprovals = function (approvals) {
        return _.filter(approvals, function (approval) {
            return approval.selected;
        });
    };
    $scope.removeApprovalFilter = function () {
        _.each($scope.reportFilter.approvals, function (approval) {
            approval.selected = true;
        })
    };

    $scope.removeDateFilter = function () {
        $scope.reportFilter.registrationDate = null;
    };

    $scope.hasSelectedProgram = function (programs) {
        return _.filter(programs, function (program) {
            return (!!(program.selected || _.find(program.sections, {"selected": true})));
        });
    };

    $scope.$on(NEW_ITEM_ADD_EVENT, function (event, args) {
        $scope.reportFilter = args;
    });

    $scope.successDeletedMessage = "";
    $scope.alert_item_show = false;

    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };

    function save(returnUrl) {
        $scope.clearErrors();

        var find = _.filter($scope.reportFilter.selectedBlocks, function (block) {
            return _.where(block.fields, {selected: true}).length > 0;
        });

        if (find.length < 1) {
            $scope.formErrors.push(fieldIsEmpty);
            return;
        }

        ReportService.save($scope.reportFilter).then(function (data) {

            returnUrl(data);

        }).catch(function (data) {
            $scope.formErrors = data.errors;
        });
    }

    $scope.saveReport = function () {
        save(function (data) {
            $window.location.href = "/report/confirmation";
        })
    };

    $scope.saveAndShare = function () {
        save(function (data) {
            $window.location.href = "/report/" + data.id + "/share";
        })
    }
});

app.controller("AddNewFilterController", function ($scope, $rootScope, $http, filterType, reportFilter, $modalInstance, $log, ReportService) {
    $scope.filter = [];
    $scope.filterType = filterType;
    $scope.reportFilter = reportFilter;
    $scope.filterBy = "";

    $scope.selectAll = function (program) {
        if (program.selected) {
            _.each(program.sections, function (section) {
                section.selected = true;
            });
        } else {
            _.each(program.sections, function (section) {
                section.selected = false;
            });
        }
    };

    $scope.init = function () {
        if (filterType == "program_and_section") {
            $scope.filterBy = "Programs and Sections";
        } else if (filterType == "registration_date") {
            $scope.filterBy = "Registration Date";
        } else if (filterType == "approval") {
            $scope.filterBy = "Approvals";
        } else if (filterType == "registrant_name") {
            $scope.filterBy = "Registrant Name";

            ReportService.findAllNames().then(function (data) {
                $scope.availableTags = data;
            })
        }
    };

    $scope.init();

    $scope.ok = function () {
        $scope.submitting = true;
        $rootScope.$broadcast(NEW_ITEM_ADD_EVENT, $scope.reportFilter);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
