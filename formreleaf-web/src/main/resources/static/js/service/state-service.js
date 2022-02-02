/**
 *  @author Md. Asaduzzaman
 *  @since 5/19/15.
 */
'use strict';

app.service('StateService', function ($http, $q, $log) {

    var stateUrl = '../../../static-data/state.json';

    return {
        getStates: function () {
            var deferred = $q.defer();

            $http.get(stateUrl, {cache: true})
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        }
    }
})
;