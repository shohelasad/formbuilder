/**
 * @uathor Md. Asaduzzaman
 * @since 6/16/15.
 */

"use strict";

app.factory("ReportService", function ($http, $q, $log) {

    var baseApi = "/api/v1/report/";
    var url = "../../static-data/";

    return {

        save: function (formData) {
            var deferred = $q.defer();

            $http.post("/api/v1/report/save", formData)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });
            return deferred.promise;
        },

        getReportDefinition: function (reportId) {
            var deferred = $q.defer();

            $http.get("/api/v1/report/definition?reportId=" + reportId)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });
            return deferred.promise;
        },

        findAllNames: function () {
            var deferred = $q.defer();

            $http.get("/api/v1/report/names")
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });
            return deferred.promise;
        },

        findFormTemplate: function () {
            var deferred = $q.defer();
            var formsJsonPath = '../../../static-data/program.json';
            $http.get(formsJsonPath)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        getForm: function (filterType) {
            return $http.get(url + filterType + ".json").then(function (response) {
                return response;
            });
        }
    }

});