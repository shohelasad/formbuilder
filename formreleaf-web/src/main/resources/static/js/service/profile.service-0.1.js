/**
 * @Md. Aaduzzaman
 * @since 5/14/15.
 */

"use strict";
app.factory("ProfileService", function ($http, $q, $log) {
    var baseApi = "/api/v1/user/";
    //var baseApi = "/api/v1/user/profile/";
    var url = "../../static-data/";

    return {
        getForm: function (profile) {
            return $http.get(url + profile + ".json").then(function (response) {
                return response;
            });
        },

        getProfiles: function (profileType) {
            var deferred = $q.defer();

            $http.get(baseApi + "profile/" + profileType)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        getUserProfile: function (participantName, profileType) {
            var deferred = $q.defer();

            // @RequestMapping(value = "participant/{participantName}/profile/{profileDataType}", method = RequestMethod.GET)

            $http.get(baseApi + "participant/" + participantName + "/profile/" + profileType)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        getUserProfileWithType: function (participantName, profileType) {
            var deferred = $q.defer();

            // @RequestMapping(value = "participant/{participantName}/profile/{profileDataType}", method = RequestMethod.GET)

            $http.get(baseApi + "subParticipant/" + participantName + "/profile/" + profileType)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

    };
});
