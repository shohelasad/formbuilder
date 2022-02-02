/**
 *  @author Bazlur Rahman Rokon
 *  @since 4/14/15.
 */
'use strict';

app.service('FormService', function FormService($http) {

    var formsJsonPath = '../../static-data/participant.json';

    return {
        form: function (id) {
            var formsJsonUrl = "/program/" + id;

            // $http returns a promise, which has a then function, which also returns a promise
            return $http.get(formsJsonUrl).then(function (response) {
                var requestedForm = {};
                angular.forEach(response.data, function (form) {
                    requestedForm = form;
                });
                return requestedForm;
            });
        },

        forms: function () {

            return $http.get(formsJsonPath).then(function (response) {
                var requestedForm = {};

                angular.forEach(response.data, function (form) {
                    requestedForm = form;
                });
                return requestedForm;
            });
        }
    };
});