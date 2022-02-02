'use strict';

app.controller("ReportController", function ($scope, $log, $http, $window, $q, ReportService) {
    $scope.reports = [];

    $scope.init = function () {
        var response = $http.get('/api/v1/report/list');
        response.success(function (data, status, headers, config) {
            $scope.reports = data;
        });
    };

    $scope.init();

    $scope.deleteReport = function (id) {

        var response = $http.delete("/api/v1/report/delete/" + id);
        response.success(function (data, status, headers, config) {
            if (status == '200') {
                $scope.alert_contact_show = true;
                $scope.successDeletedMessage = "Report successfully deleted";
                $scope.removeReport(id);
            }
        });
        response.error(function (data, status, headers, config) {
            $log.error(data);
        });
    };

    $scope.removeReport = function (id) {
        for (var i = 0; i < $scope.reports.length; i++) {
            if ($scope.reports[i].id == id) {
                $scope.reports.splice(i, 1);
                break;
            }
        }
    };
});
