/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/20/15.
 */

"use strict";

app.factory("UserService", function ($http, $q, $log) {
    var userApi = "/api/v1/user/";

    return {
        findAddresses: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/addresses").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        },
        findContacts: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/contacts").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        },

        findDoctors: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/doctors").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        },

        findInsurances: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/doctors").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        },

        isCurrentLoggedUserIsOrganization: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/is-organization").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        },
        
        isAuthenticated: function () {
            var deferred = $q.defer();

            $http.get(userApi + "/public/is-authenticated").success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        }
    };

});
