/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/11/15.
 */
'use strict';

var about = angular.module('AboutModule', [])
    .filter('bytes', function () {
        return function (bytes, precision) {
            if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';
            if (bytes === 0) return '0 bytes';
            if (typeof precision === 'undefined') precision = 1;
            var units = [' bytes', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB'],
                number = Math.floor(Math.log(bytes) / Math.log(1024));
            return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) + '' + units[number];
        };
    })
    .run(function ($rootScope) {
        $rootScope.currentYear = new Date().getFullYear();
    });


about.controller('AboutCtrl', function ($scope, $http, $filter, $interval) {
    $scope.refreshInterval = 30;
    $scope.memoryConfig = {
        options: {
            chart: {
                type: 'area'
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Memory usage'
            },
            subtitle: {
                text: 'Refreshes every ' + $scope.refreshInterval + ' seconds'
            },
            tooltip: {
                shared: true,
                valueSuffix: 'MiB'
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#666666'
                    }
                }
            }
        },
        series: [{
            name: 'Free',
            data: []
        }, {
            name: 'Used',
            data: []
        }],
        loading: false,
        xAxis: {
            categories: [],
            tickmarkPlacement: 'on',
            title: {
                enabled: false
            }
        },
        yAxis: {
            title: {
                text: 'MiB'
            }
        }
    };

    $scope.refresh = function () {
        var formatBytes = function (bytes) {
            return Math.round((bytes / Math.pow(1024, Math.floor(2))) * 10) / 10;
        };

        $http.get('/api/v1/about').success(function (data) {
            $scope.about = data;
            $scope.humanizedUptime = nezasa.iso8601.Period.parseToString(data.vm.uptime, null, null, true);
            var max = 10;
            var cur = $scope.memoryConfig.series[0].data.length;

            if (cur === max) {
                $scope.memoryConfig.series[0].data.splice(0, 1);
                $scope.memoryConfig.series[1].data.splice(0, 1);
                $scope.memoryConfig.xAxis.categories.splice(0, 1);
            }
            $scope.memoryConfig.series[0].data.push(formatBytes($scope.about.vm.freeMemory));
            $scope.memoryConfig.series[1].data.push(formatBytes($scope.about.vm.usedMemory));
            $scope.memoryConfig.xAxis.categories.push($filter('date')(new Date(), "HH:mm:ss"));
        });
    };

    var timer = $interval(function () {
        $scope.refresh();
    }, $scope.refreshInterval * 1000);
    $scope.$on("$destroy", function () {
        $interval.cancel(timer);
    });
    $scope.refresh();
});