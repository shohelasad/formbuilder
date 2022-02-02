app.config(['$provide',
  function($provide) {
    $provide.decorator('taOptions', ['$delegate',
      function(  taOptions) {
        // $delegate is the taOptions we are decorating
        taOptions.defaultTagAttributes = {
                a : {
                    target: "_blank"
                }
            };
        
        // here we override the default toolbars specified in taOptions.
        taOptions.toolbar = [
          ['clear', 'h1', 'h2', 'h3'],
          ['ul', 'ol'],
          ['bold', 'italics','html'],
          [ 'insertLink']
        ];

        return taOptions;
      }
    ]);
  }
]);

app.controller("EditorController", ['$scope',
  function($scope) {
    $scope.html = "";
  }
]);
