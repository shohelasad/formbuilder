/**
 * @author Bazlur Rahman Rokon
 * @since 9/16/15.
 */

const fileMaxSize = 5242880;
const maxFile = 5;

app.directive('fileDirective', function ($http, $q, $compile, $timeout, DocumentService, $log) {

    var getTemplateUrl = function (field) {
        var templateUrl = '../../../views/directive-templates/field/';

        return templateUrl += 'fileupload.html';
    };

    var linker = function (scope, element, attrs, ctrl) {
        // GET template content from path
        var templateUrl = getTemplateUrl(scope.field);
        $http.get(templateUrl).success(function (data) {
            element.html(data);
            $compile(element.contents())(scope);
        });

        element.bind("change", function (changeEvent) {
            scope.$apply(function () {
                var file = changeEvent.target.files[0];
                var inputFieldName = changeEvent.target.name;

                if (file.size > fileMaxSize) {
                    $timeout(function () {
                        scope.field.validator['errorMessage'] = file.name + " exceeded maximum size of 5MB";
                    });
                } else if (scope.field['documents'] && scope.field['documents'].length == maxFile) {
                    $timeout(function () {
                        scope.field.validator['errorMessage'] = "You can' not upload more then " + maxFile + " files";
                    });
                } else {
                    var programId = $('#programId').val();

                    var formData = new FormData();
                    formData.append('file', file);
                    formData.append("programId", programId);

                    DocumentService.upload(formData)
                        .then(function (data) {
                            $timeout(function () {
                                if (scope.field.validator['errorMessage']) {
                                    scope.field.validator['errorMessage'] = null;
                                }

                                if (!scope.field.documents) {
                                    scope.field['documents'] = [];
                                }

                                scope.field['documents'].push(data);

                                if (scope.field['documents'].length > 0) {
                                    scope.field['value'] = "file uploaded"; //marker
                                }
                            });
                        }).catch(function (data) {
                            $timeout(function () {
                                scope.field.validator['errorMessage'] = data.errors[0];
                            });
                        })
                }
            });
        });
    };

    var controller = function ($scope, $filter, $log, $timeout) {
        $scope.delete = function (uuid) {
            var uuids = [uuid];

            DocumentService.delete(uuids)
                .then(function (data) {
                    $timeout(function () {
                        $scope.field['documents'] = _.without($scope.field['documents'], _.findWhere($scope.field['documents'], {uuid: uuid}));

                        if ($scope.field['documents'].length == 0) {
                            var id = $scope.field.id;
                            var fileInput = angular.element($('#' + id));
                            fileInput.val("");
                            $scope.field['value'] = null;
                        }
                    });
                }).catch(function (data) {
                    $timeout(function () {
                        $scope.field.validator['errorMessage'] = data.errors[0];
                    });
                })
        }
    };

    return {
        require: '^form',
        template: '<div>{{field}}</div>',
        restrict: 'E',
        scope: {
            field: '='
        },
        link: linker,
        controller: controller
    };
});