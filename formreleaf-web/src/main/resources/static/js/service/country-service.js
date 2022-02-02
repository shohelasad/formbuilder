/**
 *  @author Md. Asaduzzaman
 *  @since 5/19/15.
 */
'use strict';

app.service('CountryService', function ($http, $q, $log) {

    var countryUrl = '../../../static-data/country.json';

    return {
        getCountries: function () {
            var deferred = $q.defer();

            $http.get(countryUrl, {cache: true})
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        }
    }
});