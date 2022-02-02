/**
 * @author Bazlur Rahman Rokon
 *
 * @date 5/12/15.
 */
"use strict";

var NEW_FIELD_ADD_EVENT = "new-field-added";
var FIELD_EDIT_EVENT = "field-updated";
var participantSaveSuccessMessage = "Participant information has been saved Successfully";

app.controller("FormGeneratorController", function ($scope, $http, $window, $modal, $log, $location, $anchorScroll) {
    var url = "/api/v1/program/formTemplate";

    $scope.form = {};
    $scope.formErrors = {};

    function getFormData() {

        return {
            "programId": $scope.programId,
            "template": angular.toJson($scope.form)
        };
    }

    $scope.init = function () {
        $scope.programId = $("input[name='id']").val();

        var formsJsonPath = '../../../static-data/program.json';

        $http.get("/api/v1/program/" + $scope.programId + "/formTemplate").then(function (response) {
            if (response.status != "200") {
                $http.get(formsJsonPath).then(function (response) {
                    angular.forEach(response.data, function (data) {
                        $scope.form = data;
                    });
                });
            } else {
                angular.forEach(response.data, function (data) {
                    $scope.form = data;
                });
            }
        });
    };

    $scope.addNewField = function (block, section) {

        $modal.open({
            templateUrl: '../../../views/newFieldTemplate.html',
            controller: "NewItemController",
            backdrop: 'static',
            resolve: {
                block: function () {
                    return block;
                },
                sectionId: function () {
                    return section.id;
                }
            }
        });
    };

    $scope.saveAndContinue = function () {
        $scope.save(function () {
            $window.location.href = "/program/create/" + $scope.programId + "/agreement";
        });
    };

    $scope.save = function (callback) {
        var response = $http.post(url, getFormData());
        response.success(function (data, status, headers, config) {
            $scope.message = participantSaveSuccessMessage;
            if (callback != 'undefined' && typeof callback == 'function') {
                callback();
            }
        });

        response.error(function (data, status, headers, config) {
            $scope.formErrors = data.errors;
            $scope.moveToErrorAnchor();
        });
    };

    $scope.$on(NEW_FIELD_ADD_EVENT, function (event, args) {
        var sId = args.sectionId;
        var bId = args.blockId;

        $scope.form.sections.forEach(function (section, id) {
            if (section.id === sId) {
                section.blocks.forEach(function (block, id) {
                    if (block.id === bId) {
                        addNewField(block, args, section);
                    }
                });
            }
        });

        function addNewField(block, args, section) {
            var newField = makeNewField(args);

            if (section.id == 9) {
                newField['concerned'] = true;
                newField['yesConcerned'] = true;
            }

            block.fields.push(newField);
        }
    });

    function makeNewField(field) {

        return {
            "id": guid(),
            "fieldType": field.fieldType,
            "name": field.name,
            "title": field.title,
            "helpText": field.helpText,
            "selected": true,
            "validator": field.validator,
            "options": field.options,
            "custom": true
        };
    }

    $scope.$on(FIELD_EDIT_EVENT, function (event, field) {
        $log.info(field);

        var sId = field.sectionId;
        var bId = field.blockId;
        $log.info(sId, bId);

        $scope.form.sections.forEach(function (section, id) {
            if (section.id === sId) {
                section.blocks.forEach(function (block, id) {
                    if (block.id === bId) {
                        updateField(block, field, section);
                    }
                });
            }
        });

        function updateField(block, fieldToUpdate, section) {
            var newField = makeNewField(field);

            if (section.id == 9) {
                newField['concerned'] = true;
                newField['yesConcerned'] = true;
            }
            var index = _.findIndex(block.fields, {id: fieldToUpdate.id});
            block.fields[index] = newField;
        }
    });

    $scope.moveToErrorAnchor = function () {
        $location.hash('error');
        $anchorScroll();
    };

    $scope.editField = function (field, block, section) {

        $modal.open({
            templateUrl: '../../../views/newFieldTemplate.html',
            controller: "EditItemController",
            backdrop: 'static',
            resolve: {
                field: function () {
                    return field;
                },
                block: function () {
                    return block;
                },
                sectionId: function () {
                    return section.id;
                }
            }
        });
    };

    $scope.removeField = function (block, field) {
        var index = _.findIndex(block.fields, {id: field.id});

        if (index > -1) {
            block.fields.splice(index, 1);
        }
    };

    $scope.init();

});

function getFieldTypes() {

    return [
        {
            name: 'textfield',
            value: 'Textfield'
        },
        {
            name: 'radio',
            value: 'Radio Buttons'
        },
        {
            name: 'dropdown',
            value: 'Dropdown List'
        },
        {
            name: 'date',
            value: 'Date'
        },
        {
            name: 'textarea',
            value: 'Text Area'
        },
        {
            name: 'checkbox',
            value: 'Checkbox'
        },
        {
            name: 'fileupload',
            value: 'File Upload'
        }
    ];
}

function getInputTypes() {
    return [
        {name: "text", value: "text"},
        {name: "email", value: "email"},
        {name: "number", value: "number"},
        {name: "phone", value: "phone"}
    ];
}

app.controller("NewItemController", function ($rootScope, $scope, $modalInstance, block, sectionId, $log) {
    $scope.block = block;
    $scope.addField = {};
    $scope.types = getFieldTypes();
    $scope.inputTypes = getInputTypes();
    $scope.addField.fieldType = $scope.types[0].name;
    $scope.addField.name = "";
    $scope.addField.title = "";
    $scope.addField.helpText = "";
    $scope.addField.blockId = block.id;
    $scope.addField.sectionId = sectionId;
    $scope.addField.validator = {};
    $scope.addField.validator.required = false;
    $scope.addField.validator.minLength = null;
    $scope.addField.validator.maxLength = null;
    $scope.addField.validator.pattern = null;
    $scope.addField.validator.validationType = "text";

    $scope.advance = false;

    $scope.toggleAdvanceValidation = function () {
        $scope.advance = $scope.advance === false ? true : false;
    };

    $scope.onChange = function () {
        $scope.advance = false;
    };

    var lastOptionID = 0;

    // add new option to the field
    $scope.addOption = function (field) {
        if (!field.options) {
            field.options = [];
        }

        if (field.options[field.options.length - 1]) {
            lastOptionID = field.options[field.options.length - 1].id;
        }
        // new option's id
        var optionId = lastOptionID + 1;

        var newOption = {
            "id": optionId,
            "title": "Option " + optionId
        };

        // put new option into options array
        field.options.push(newOption);
    };

    // delete particular option
    $scope.deleteOption = function (field, option) {
        for (var i = 0; i < field.options.length; i++) {
            if (field.options[i].id === option.id) {
                field.options.splice(i, 1);
                break;
            }
        }
    };

    // decides whether field options block will be shown (true for dropdown and radio fields)
    $scope.showAddOptions = function (field) {
        return !!(field.fieldType === "radio" || field.fieldType === "dropdown");
    };

    $scope.showAdvanceValidationOption = function (field) {
        return !!(field.fieldType === "textfield" || field.fieldType === "textarea");
    };

    $scope.ok = function () {
        if (($scope.addField.fieldType === "radio" || $scope.addField.fieldType === "dropdown")
            && ($scope.addField.options == undefined || $scope.addField.options.length == 0)) {

            $scope.error = "Add options which to choose from";

            return;
        } else {
            $scope.error = null;
        }

        $scope.addField.name = ($scope.block.title.replace(/ /g, "_").trim().replace("/", '') + 's_' + $scope.addField.title.replace(/ /g, "_")).trim().replace(/ /g, "_").toLowerCase();
        $rootScope.$broadcast(NEW_FIELD_ADD_EVENT, $scope.addField);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller("EditItemController", function ($rootScope, $scope, $modalInstance, field, block, sectionId, $log) {
    $scope.block = block;
    $scope.types = getFieldTypes();
    $scope.inputTypes = getInputTypes();

    $scope.addField = jQuery.extend(true, {}, field);
    $scope.addField.blockId = block.id;
    $scope.addField.sectionId = sectionId;

    $scope.advance = false;

    $scope.toggleAdvanceValidation = function () {
        $scope.advance = $scope.advance === false ? true : false;
    };

    $scope.onChange = function () {
        $scope.advance = false;
    };

    var lastOptionID = 0;

    // add new option to the field
    $scope.addOption = function (field) {
        if (!field.options) {
            field.options = [];
        }

        if (field.options[field.options.length - 1]) {
            lastOptionID = field.options[field.options.length - 1].id;
        }
        // new option's id
        var optionId = lastOptionID + 1;

        var newOption = {
            "id": optionId,
            "title": "Option " + optionId
        };

        // put new option into options array
        field.options.push(newOption);
    };

    // delete particular option
    $scope.deleteOption = function (field, option) {
        for (var i = 0; i < field.options.length; i++) {
            if (field.options[i].id === option.id) {
                field.options.splice(i, 1);
                break;
            }
        }
    };

    // decides whether field options block will be shown (true for dropdown and radio fields)
    $scope.showAddOptions = function (field) {
        return !!(field.fieldType === "radio" || field.fieldType === "dropdown");
    };

    $scope.showAdvanceValidationOption = function (field) {
        return !!(field.fieldType === "textfield" || field.fieldType === "textarea");
    };

    $scope.ok = function () {
        if (($scope.addField.fieldType === "radio" || $scope.addField.fieldType === "dropdown")
            && ($scope.addField.options == undefined || $scope.addField.options.length == 0)) {

            $scope.error = "Add options which to choose from";

            return;
        } else {
            $scope.error = null;
        }

        $scope.addField.name = ($scope.block.title.replace(/ /g, "_").trim().replace("/", '') + 's_' + $scope.addField.title.replace(/ /g, "_")).trim().replace(/ /g, "_").toLowerCase();
        $rootScope.$broadcast(FIELD_EDIT_EVENT, $scope.addField);
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});