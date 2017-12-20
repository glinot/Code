/**
 * Created by grego on 3/14/2017.
 */
var app = angular.module("Timetable", [])

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter, {'event': event});
                });

                event.preventDefault();
            }
        });
    };
});

app.controller('main', function ($scope, $http) {
    $scope.user = {};
    $http.get('/api/user').then(function (rep) {
        $scope.user = rep.data;
    }, console.log);

    $scope.menu = [
        {
            name: "Courses",
            url: "courses.html"
        },
        {
            name: "Groups",
            url: "groups.html"
        }];
    $scope.selectedMenuItem = $scope.menu[0];

    $scope.selectMenuItem = function (item) {
        console.log(item);
        $scope.selectedMenuItem = item;
    };
    $scope.logout = function(){
        $http.post("/api/logout");
        window.location='/login.html';
    }

});


app.controller("CoursesCrtl", function ($scope, $http) {

    $scope.search = "";
    $scope.getCourses = function () {
        $.get("/api/courses", {q: $scope.search}, function (rep) {
            data = {};
            rep.forEach(function (e, i) {
                data[e.name] = null;
            });

            $('input.autocomplete').autocomplete({
                data: data,
                limit: 20, // The max amount of results that can be shown at once. Default: Infinity.
            });
        });
    };
    $scope.addCourse = function () {
        $http.get("/api/courses", {params: {q: $scope.search}}).then(function (rep) {
            var c = rep.data;
            if (c.length == 0) {
                Materialize.toast('Unknown course', 1000);
            }
            else {
                $http.post("/api/courses/add/" + c[0].id).then(function (rep) {
                    $scope.refreshCourses();
                }, function () {
                    Materialize.toast('Error adding course', 1000);
                });
            }
        });
    }

    $scope.courses = [];
    $scope.refreshCourses = function () {
        $http.get("/api/me/courses").then(function (rep) {
            $scope.courses = rep.data;
        });
    }
    $scope.refreshCourses();

    $scope.toggleCourse = function (course) {
        course.selected = !(course.selected || false);
        console.log(course);
    }
    $scope.showDelete = function () {
        return $scope.courses.filter(function (c) {
                return c.selected;
            }).length > 0;
    }
    $scope.deleteCourses = function () {
        $http.post("/api/me/courses/delete", {
                courses: $scope.courses.filter(function (c) {
                    return c.selected;
                })
            }
        ).then($scope.refreshCourses, $scope.refreshCourses);
    }
});

app.controller('GroupCrtl', function ($scope, $http) {
    $('.modal').modal();
    $scope.search = "";
    $scope.users = [];
    $scope.groupName = "";
    $scope.showFreeTime = false;
    $scope.freeSpots = [];
    $scope.currentGroup = null;
    $scope.week_index = 0;

    $http.get("/api/users").then(function (rep) {
        $scope.users = rep.data;

    });
    $scope.toggleSelectUser = function (u) {
        u.selected = !u.selected;

    };
    $scope.getSelectedUsers = function () {
        return $scope.users.filter(function (u) {
            return u.selected;
        });
    };
    $scope.cancel = function () {
        $scope.users.forEach(function (e) {
            e.selected = false;
        });
        $scope.groupName = "";
    };
    $scope.createGroup = function () {
        $http.post("/api/group/create", {
            group_name: $scope.groupName, users_id: $scope.getSelectedUsers().map(function (e) {
                return e._id;
            })
        }).then(function () {
            $scope.refreshGroups();
        });
    };
    $scope.groups = [];
    $scope.refreshGroups = function () {
        $http.get("/api/me/groups").then(function (rep) {
            $scope.groups = rep.data;
            $scope.groups.forEach($scope.getUsers);
            $('.collapsible').collapsible();
        });
    };
    $scope.refreshGroups();

    $scope.getUsers = function (g) {
        $http.post("/api/users/subset", {users: g.users}).then(function (rep) {
            g.full_users = rep.data;
        });
    };
    $scope.deleteGroup = function (g) {
        $http.post("/api/groups/delete", {_id: g._id}).then(function (rep) {
            $scope.refreshGroups();
        });
    };

    $scope.findFreeTime = function (group) {
        $scope.currentGroup = group;
        $http.get("/api/groups/freetime/" + group._id, {params: {week_index: $scope.week_index || 0}}).then(function (rep) {
            var data = rep.data;
            var res = {};
            for (var i in data) {
                var m_start = moment(new Date(data[i].start))
                var d = m_start.format("dddd");
                if (!res[d]) res[d] = [data[i]];
                else res[d].push(data[i])
            }

            $scope.freeSpots = res;
            $scope.showFreeTime = true;
        })
    };
    $scope.formatCave = function (cave) {
        var m_start = moment(new Date(cave.start));
        var m_end = moment(new Date(cave.end));
        return m_start.format("ha") + " - " + m_end.format("ha");
    };
    $scope.incWeek = function(){
        $scope.week_index++;
        $scope.findFreeTime($scope.currentGroup);
    }
    $scope.decWeek = function(){
        $scope.week_index--;
        $scope.findFreeTime($scope.currentGroup);
    }

});