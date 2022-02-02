/**
 * @author Bazlur Rahman Rokon
 *
 * @date 6/8/15.
 */

app.factory("AgreementService", function ($http, $q, $log) {
    return {
        findAllAgreements: function () {
            var deferred = $q.defer();
            $http.get("/api/v1/organization/agreements")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        saveAgreement: function (agreement) {
            var deferred = $q.defer();

            $http.post("/api/v1/organization/agreement", agreement)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        updateAgreement: function (agreement) {
            var deferred = $q.defer();

            $http.put("/api/v1/organization/agreement", agreement)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        deleteAgreement: function (id) {
            var deferred = $q.defer();

            $http.delete("/api/v1/organization/agreement/" + id)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        }
    };
});