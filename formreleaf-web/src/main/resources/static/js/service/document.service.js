/**
 * @author Bazlur Rahman Rokon
 * @since 9/16/15.
 */


app.service('DocumentService', function ($http, $q, $log) {

    return {
        delete: function (uuids) {
            var deferred = $q.defer();

            $http.delete("/api/v1/document/delete/" + uuids)
                .success(function (data, status, headers, config) {
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                    deferred.reject(data);
                });

            return deferred.promise;
        },

        upload: function (formData) {
            var deferred = $q.defer();

            $http.post("/api/v1/document/upload", formData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).success(function (data, status, headers, config) {
                deferred.resolve(data);
            }).error(function (data, status, headers, config) {
                deferred.reject(data);
            });

            return deferred.promise;
        }
    }
});