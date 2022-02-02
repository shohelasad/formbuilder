/**
 *  @author Bazlur Rahman Rokon
 *  @since 4/14/15.
 */

'use strict';

app.directive('formDirective', function () {
    return {
        controller: function ($scope, $q, $log, $modal, $timeout, ProfileService, DocumentService, $window) {
            var blockToAdd = {};
            var medicationCounter = 0;

            $scope.autoFillerItems = [];
            //TODO: for the sake of the demo, have to rewrite later. Dirty code continued for the time being...
            $scope.init = function () {
                $q.all([ProfileService.getProfiles("participant"),//1
                    ProfileService.getProfiles("medication"),//8
                    ProfileService.getProfiles("concern")//9
                ]).then(function (data) {
                    $scope.autoFillerItems.push({id: 1, profiles: data[0]});
                    $scope.autoFillerItems.push({id: 8, profiles: data[1]});
                    $scope.autoFillerItems.push({id: 9, profiles: data[2]});
                });
            };

            $scope.findAutoFillItem = function (id) {
                return _.find($scope.autoFillerItems, function (item) {
                    if (item.id == id) {
                        return item;
                    }
                });
            };

            $scope.hasFieldsInBlocks = function (section) {
                return _.chain(section.blocks).filter(function (block) {
                        return _.chain(block.fields).filter(function (filed) {
                                return filed.selected;
                            }).size() > 0;
                    }).size() > 0;
            };

            $scope.autofillAll = function (item, sections, section) {

                medicationCounter = 0;

                if (section.id != 1) {

                    var profileData = [];
                    var multiData = [];
                    _.chain(angular.fromJson(item.profileData).blocks).each(function (block) {
                        _.each(block.fields, function (field) {
                            if (field.value != null) {
                                profileData.push(field);
                            }
                        });

                        if (block.allowMultiple) {
                            multiData.push({title: block.title, multidata: block.multiFields});
                        }

                    });

                    angular.forEach(section.blocks, function (block, key) {
                        angular.forEach(block.fields, function (field, key) {
                            if (field.selected) {
                                _.each(profileData, function (item) {
                                    if (item.id == field.id) {
                                        field.value = item.value;
                                        if (field.concerned) {
                                            field.concernedYes = item.concernedYes;
                                        }
                                    }
                                });
                            }
                        });

                        if (block.allowMultiple) {

                            for (var mIndex = 0; mIndex < multiData.length; mIndex++) {
                                var mItem = multiData[mIndex];
                                if (mItem.title = block.title) {
                                    block.multiFields = mItem.multidata;
                                }
                            }
                        }
                    });
                    return;
                }

                var participantName = item.name;
                var profileData = [];

                _.chain(angular.fromJson(item.profileData).blocks).each(function (block) {
                    _.each(block.fields, function (field) {
                        if (field.value != null) {
                            profileData.push(field);
                        }
                    })
                });

                _.chain(angular.fromJson(item).blocks).each(function (block) {
                    _.each(block.fields, function (field) {
                        if (field.value != null) {
                            profileData.push(field);
                        }
                    })
                });

                angular.forEach(sections, function (section, key) {
                    angular.forEach(section.blocks, function (block, key) {
                        angular.forEach(block.fields, function (field, key) {
                            if (field.selected) {
                                _.each(profileData, function (item) {
                                    if (item.id == field.id) {
                                        field.value = item.value;
                                        if (field.concerned) {
                                            field.concernedYes = item.concernedYes;
                                        }
                                    }
                                });
                            }
                        });

                        if (block.allowMultiple) {

                            medicationCounter++;
                            var cloned = clone(block);

                            if (medicationCounter > 1) {
                                block.multiFields.push(
                                    _.map(cloned.fields, function (field) {
                                        field.id = guid();
                                        field.name = field.name + "_" + medicationCounter;
                                        field.title = field.title + " " + medicationCounter;
                                        return field;
                                    }));
                            } else {
                                block.multiFields.push(cloned.fields);
                            }
                        }
                    });
                });

                var arrLength = $scope.autoFillerItems.length;

                for (var i = arrLength - 1; i > 2; i--) {
                    $scope.autoFillerItems.splice(i, 1);
                }

                $q.all([ProfileService.getUserProfile(participantName, "participant"),
                    ProfileService.getUserProfile(participantName, "address"),
                    ProfileService.getUserProfile(participantName, "parent"),
                    ProfileService.getUserProfile(participantName, "contact"),
                    ProfileService.getUserProfile(participantName, "physician"),
                    ProfileService.getUserProfile(participantName, "insurance"),
                    ProfileService.getUserProfile(participantName, "medication"),
                    ProfileService.getUserProfile(participantName, "concern")
                ]).then(function (data) {

                    angular.forEach(sections, function (section, key) {
                        angular.forEach(section.blocks, function (block, key) {
                            angular.forEach(block.fields, function (field, key) {
                                if (field.selected) {
                                    field.value = "";
                                }
                            });

                            if (block.allowMultiple) {
                                block.multiFields = [];
                            }
                        });
                    });

                    for (var i = 0; i < 8; i++) {

                        medicationCounter = 0;
                        item = data[i];

                        if (!item[0]) {
                            continue;
                        }

                        item = item[0];
                        item = item.profileData;
                        item = JSON.parse(item);

                        profileData = [];
                        multiData = [];
                        angular.forEach(item.blocks, function (block, key) {
                            angular.forEach(block.fields, function (field, key) {
                                profileData.push(field);
                            });

                            if (block.allowMultiple) {
                                multiData.push({title: block.title, multidata: block.multiFields});
                            }
                        });

                        if (profileData.length == 0) continue;

                        angular.forEach(sections, function (section, key) {
                            angular.forEach(section.blocks, function (block, key) {
                                angular.forEach(block.fields, function (field, key) {
                                    if (field.selected) {
                                        _.each(profileData, function (item) {
                                            if (item.id == field.id) {
                                                field.value = item.value;
                                                if (field.concerned) {
                                                    field.concernedYes = item.concernedYes;
                                                }
                                                if (item.documents) {
                                                    field['documents'] = item.documents;
                                                }
                                            }
                                        });
                                    }
                                });

                                if (block.allowMultiple) {

                                    for (var mIndex = 0; mIndex < multiData.length; mIndex++) {
                                        var mItem = multiData[mIndex];
                                        if (mItem.title == block.title) {
                                            block.multiFields = mItem.multidata;
                                        }
                                    }
                                }
                            });
                        });
                    }
                });

                var arrLength = $scope.autoFillerItems.length;

                for (var i = arrLength - 1; i > 2; i--) {
                    $scope.autoFillerItems.splice(i, 1);
                }

                $q.all([ProfileService.getUserProfileWithType(participantName, "address"),
                    ProfileService.getUserProfileWithType(participantName, "parent"),
                    ProfileService.getUserProfileWithType(participantName, "contact"),
                    ProfileService.getUserProfileWithType(participantName, "physician"),
                    ProfileService.getUserProfileWithType(participantName, "insurance"),
                ]).then(function (data) {
                    $scope.autoFillerItems.push({id: 2, profiles: data[0]});
                    $scope.autoFillerItems.push({id: 3, profiles: data[1]});
                    $scope.autoFillerItems.push({id: 5, profiles: data[2]});
                    $scope.autoFillerItems.push({id: 6, profiles: data[3]});
                    $scope.autoFillerItems.push({id: 7, profiles: data[4]});
                });
            };

            $scope.autofill = function (item, section) {
                var profileData = [];
                _.chain(angular.fromJson(item.profileData).blocks).each(function (block) {
                    _.each(block.fields, function (field) {
                        if (field.value != null) {
                            profileData.push(field);
                        }
                    })
                });

                angular.forEach(section.blocks, function (block, key) {
                    angular.forEach(block.fields, function (field, key) {
                        if (field.selected) {
                            _.each(profileData, function (item) {
                                if (item.id == field.id) {
                                    field.value = item.value;
                                    if (field.concerned) {
                                        field.concernedYes = item.concernedYes;
                                    }
                                    if (item.documents) {
                                        field['documents'] = item.documents;
                                    }
                                }
                            });
                        }
                    });

                    if (block.allowMultiple) {

                        medicationCounter++;
                        var cloned = clone(block);

                        if (medicationCounter > 1) {
                            block.multiFields.push(
                                _.map(cloned.fields, function (field) {
                                    field.id = guid();
                                    field.name = field.name + "_" + medicationCounter;
                                    field.title = field.title + " " + medicationCounter;
                                    return field;
                                }));
                        } else {
                            block.multiFields.push(cloned.fields);
                        }
                    }
                });
            };

            $scope.$on('newMedicationAdded', function (event, args) {
                medicationCounter++;

                var cloned = clone(args);
                if (medicationCounter > 1) {
                    blockToAdd.multiFields.push(
                        _.map(cloned.fields, function (field) {
                            field.id = guid();
                            field.name = field.name + "_" + medicationCounter;
                            field.title = field.title + " " + medicationCounter;
                            return field;
                        }));
                } else {
                    blockToAdd.multiFields.push(cloned.fields);
                }
                $scope.$parent.formOnChange();
            });

            $scope.addNewMedication = function (section, block) {
                blockToAdd = block;
                $modal.open({
                    templateUrl: '../../../views/newMedication.html',
                    controller: function ($scope, $rootScope, block, $modalInstance) {
                        $scope.block = block;

                        $scope.ok = function () {
                            $rootScope.$broadcast("newMedicationAdded", $scope.block);
                            $modalInstance.close();
                        };

                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                    },
                    backdrop: 'static',
                    resolve: {
                        block: function () {
                            var clonedBlock = clone(block);

                            _.chain(clonedBlock.fields).each(function (field) {
                                field.value = null;
                                if (field.fieldType === 'fileupload') {
                                    field.documents = [];
                                }
                            });

                            return clonedBlock;
                        }
                    }
                });
            };

            $scope.removeMedication = function (block, items) {
                var documentField = _.find(items, function (item) {
                    return item.documents;
                });

                if (documentField) {
                    var uuids = _.pluck(documentField.documents, "uuid");
                    DocumentService.delete(uuids);
                }

                block.multiFields = _.reject(block.multiFields, function (fields) {
                    return _.find(fields, function (field) {
                        return field.id == items[0].id;
                    });
                });

                medicationCounter--;
                $scope.$parent.formOnChange();
            };

            $timeout(function () {
                $scope.init();
            }, 0);
        },
        templateUrl: '../../../views/directive-templates/form/form.html',
        restrict: 'E',
        scope: {
            form: '='
        }
    };
});
app.directive('formOnChange', function ($parse, $interpolate) {
    return {
        require: "form",
        link: function (scope, element, attr, form) {
            var cb = $parse(attr.formOnChange);
            element.on("change", function () {
                cb(scope);
            });
        }
    };
});