/**
    *  @author Md. Asaduzzaman
    *  @since 4/14/15.
    */

'use strict';

app.directive('sectionDirective', function () {
    
	$log.info("sectionDirective");
    
    return {
        controller: function($scope){
            $scope.submit = function(){
                alert('Form submitted..');
                $scope.form.submitted = true;
            }

            $scope.cancel = function(){
                alert('Form canceled..');
            }
        },
        templateUrl: '../../views/directive-templates/form/section.html',
        restrict: 'E',
        scope: {
            section:'='
        }
    };
});