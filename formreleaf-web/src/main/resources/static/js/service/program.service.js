/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/14/15.
 */

"use strict";
app.factory("ProgramService", function ($http, $q, $log) {
    var baseApi = "/api/v1/program/";

    return {
        findFormTemplate: function (id) {
            var deferred = $q.defer();

            $http.get(baseApi + id + "/formTemplate")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        //It seems redundent fuction; has function name with findAllSections
        findAllSection: function (id) {
            var deferred = $q.defer();

            $http.get(baseApi + "/public/" + id + "/sections")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllAgreements: function (id) {
            var deferred = $q.defer();

            $http.get(baseApi + id + "/agreements")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllPolicies: function (id) {
            var deferred = $q.defer();
            $http.get(baseApi + id + "/policies")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findPublish: function (id) {
            var deferred = $q.defer();
            $http.get(baseApi + id + "/publish")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        savePublish: function (id, publish) {
            var deferred = $q.defer();

            $http.post(baseApi + id + "/publish", publish)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        loadPreviousInfo: function (id) {
            var deferred = $q.defer();

            $http.get(baseApi + id + "/previousRegistrationData")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllRegistrations: function (id) {
            var deferred = $q.defer();

            $http.get(baseApi + id + "/registrants")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        changeStatus: function (id, registrations) {
            var deferred = $q.defer();

            $http.post(baseApi + id + "/registrants/change-status", registrations)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        addPolicyToProgram: function (id, policy) {
            var deferred = $q.defer();

            $http.post(baseApi + id + "/policy", policy)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        removePolicyFromProgram: function (id, policyId) {
            var deferred = $q.defer();

            $http.delete(baseApi + id + "/policy/" + policyId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        addAgreementToProgram: function (id, agreement) {
            var deferred = $q.defer();

            $http.post(baseApi + id + "/agreement", agreement)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        removeAgreementFromProgram: function (id, agreementId) {
            var deferred = $q.defer();

            $http.delete(baseApi + id + "/agreement/" + agreementId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllLocations: function (id) {
            var deferred = $q.defer();
            $http.get(baseApi + id + "/locations")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        addLocationToProgram: function (id, location) {
            var deferred = $q.defer();
            $http.post(baseApi + id + "/location", location)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        updateLocationOfProgram: function (id, location) {
            var deferred = $q.defer();
            $http.put(baseApi + id + "/location", location)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        removeLocationFromProgram: function (id, locationId) {
            var deferred = $q.defer();
            $http.delete(baseApi + id + "/location/" + locationId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllContacts: function (id) {
            var deferred = $q.defer();
            $http.get(baseApi + id + "/contacts")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        addContactToProgram: function (id, contact) {
            var deferred = $q.defer();
            $http.post(baseApi + id + "/contact", contact)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        updateContactOfProgram: function (id, location) {
            var deferred = $q.defer();
            $http.put(baseApi + id + "/contact", location)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        removeContactFromProgram: function (id, contactId) {
            var deferred = $q.defer();
            $http.delete(baseApi + id + "/contact/" + contactId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        findAllSections: function (id) {
            var deferred = $q.defer();
            $http.get(baseApi + "/public/" + id + "/sections")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },
        addSectionToProgram: function (id, section) {
            var deferred = $q.defer();
            $http.post(baseApi + id + "/section", section)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        updateSectionOfProgram: function (id, section) {
            var deferred = $q.defer();
            $http.put(baseApi + id + "/section", section)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        removeSectionFromProgram: function (id, sectionId) {
            var deferred = $q.defer();
            $http.delete(baseApi + id + "/section/delete/" + sectionId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        }
    };
})
;
