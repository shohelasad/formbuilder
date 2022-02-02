/**
 * @author Bazlur Rahman Rokon
 *
 * @date 6/8/15.
 */
"use strict";

app.factory("PolicyService", function ($http, $q, $log) {
    return {
        findAllPolicies: function () {
            var deferred = $q.defer();
            $http.get("/api/v1/organization/policies")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        savePolicy: function (policy) {
            var deferred = $q.defer();

            $http.post("/api/v1/organization/policy", policy)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        updatePolicy: function (policy) {
            var deferred = $q.defer();

            $http.put("/api/v1/organization/policy", policy)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        deletePolicy: function (id) {
            var deferred = $q.defer();

            $http.delete("/api/v1/organization/policy/" + id)
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
