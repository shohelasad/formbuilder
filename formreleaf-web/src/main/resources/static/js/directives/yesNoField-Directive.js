/**
    * @author Bazlur Rahman Rokon
    *
    * @date 6/1/15.
    */
'use strict';

app.directive('yesNoFieldDirective', function ($http, $compile) {

    var getTemplateUrl = function (field) {
        var templateUrl = '../../../views/directive-templates/field/';

        return templateUrl += 'yesNoComment.html';
    };

    var linker = function (scope, element) {
        // GET template content from path
        var templateUrl = getTemplateUrl(scope.field);
        $http.get(templateUrl).success(function (data) {
            element.html(data);
            $compile(element.contents())(scope);
        });
    };

    return {
        template: '<div>{{field}}</div>',
        restrict: 'E',
        scope: {
            field: '='
        },
        link: linker
    };
});