/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/14/15.
 */

"use strict";
app.factory("RegistrationService", function ($http, $q, $log) {
    return {

        save: function (formData) {
            var deferred = $q.defer();

            $http.post("/api/v1/registration/save", formData)
                .success(function (data, status, headers, config) {
                    deferred.resolve(status);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });
            return deferred.promise;
        },

        autSave: function (formData) {
            var deferred = $q.defer();

            $http.post("/api/v1/registration/auto-save", formData)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data, status, headers, config);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data, status, headers, config);
                });
            return deferred.promise;
        },

        findFormData: function (id) {
            var deferred = $q.defer();

            $http.get('/api/v1/registration/' + id + '/formData')
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllSections: function (id) {
            var deferred = $q.defer();

            $http.get('/api/v1/registration/' + id + '/sections')
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findSignature: function (id) {
            var deferred = $q.defer();

            $http.get('/api/v1/registration/' + id + '/signature')
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findRegistration: function (id) {
            var deferred = $q.defer();

            $http.get('/api/v1/registration/' + id)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        
        findProgramSections: function (id) {
            var deferred = $q.defer();
            $http.get('/api/v1/program/' + id + '/sections')
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        
        changeSection: function (id, sectionId) {
            var deferred = $q.defer(); 
            $http.post('/api/v1/registration/' + id + "/change-section", sectionId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        deleteRegistration: function (id) {
            var deferred = $q.defer();

            $http.delete('/api/v1/registration/delete/' + id)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        }
    }
});