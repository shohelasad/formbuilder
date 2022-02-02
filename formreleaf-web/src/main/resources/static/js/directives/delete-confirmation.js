/**
    * @uathor Bazlur Rahman Rokon
    * @since 4/26/15.
    */
app.directive('ngReallyClick', ['$modal',
    function($modal) {

        var ModalInstanceCtrl = function($scope, $modalInstance) {
            $scope.ok = function() {
                $modalInstance.close();
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };

        return {
            restrict: 'A',
            scope:{
                ngReallyClick:"&",
                item:"="
            },
            link: function(scope, element, attrs) {
                element.bind('click', function() {
                    var message = attrs.ngReallyMessage || "Are you sure ?";

                    var modalInstance = $modal.open({
                        templateUrl: '../../../views/directive-templates/deleteConfirm.html',
                        controller: ModalInstanceCtrl
                    });

                    modalInstance.result.then(function() {
                        scope.ngReallyClick({item:scope.item}); //raise an error : $digest already in progress
                    }, function() {

                    });

                });
            }
        }
    }
]);