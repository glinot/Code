/**
 * Created by grego on 3/14/2017.
 */
var express = require("express")
var request = require('request')
var cheerio = require('cheerio')
var moment = require("moment")

var mongoose = require('mongoose')
var ObjectId = mongoose.Types.ObjectId;

var db = require('./db')
var course = require("./course")
var events = require("./events")
api = express()

var UserModel = db.User
var CourseModel = db.Course
var GroupModel = db.Group

api.use('/', function (req, res, next) {

    UserModel.findOne({google_id: (req.session.passport || {}).user}, function (err, user) {
        req.user = user;
        next();
    })
});
api.on('mount', function (parent) {
    console.log('API Mounted');
});

api.get("/groups/freetime/:group_id", function (req, res) {
    var group_id = ObjectId(req.params.group_id)
    var week_offset = parseInt(req.query.week_index || "0") || 0

    var startDate
    if (week_offset == 0) startDate = new Date()
    else startDate = moment().set("week", moment().get("week") + week_offset).set("hour", 0).set('minute', 0).set('second', 0).toDate()
    var endDate = moment().set("week", moment().get("week") + 1 + week_offset).set("hour", 0).set('minute', 0).set('second', 0).toDate()
    GroupModel.findOne({_id: group_id}, function (err, group) {
        if (group == null) res.sendStatus(500)
        else {
            UserModel.find({_id: {"$in": group.users}}, function (err, arr) {
                if (arr && arr.length > 0) {

                    var courses = arr.reduce(function (acc, currUser, currI) {
                        acc = acc.concat(currUser.courses);
                        return acc;
                    }, []);
                    CourseModel.aggregate([
                        {"$match": {course_id: {"$in": courses}}},
                        {"$unwind": "$events"},
                        {"$match": {"events.start": {$gte: startDate}}},
                        {"$match": {"events.end": {$lte: endDate}}},
                        {"$sort": {"events.start": 1}}
                    ], function (err, arr) {
                        res.json(events.findFreeCaves(arr.map(function (e) {
                            return e.events
                        }), startDate, endDate))//.map(function(e){return {start : moment(e.start).format("dddd, MMMM Do YYYY, h:mm:ss a"),end : moment(e.end).format("dddd, MMMM Do YYYY, h:mm:ss a"  )}}))
                    });

                }
                else {
                    res.sendStatus(500)
                }
            })
        }
    });
});

api.get("/user", function (req, res) {
    UserModel.findOne({google_id: req.session.passport.user}, function (err, user) {
        res.json(user)
    })
});
api.get("/users", function (req, res) {
    UserModel.find({}, 'name surname picture', function (err, users) {
        res.json(users)
    })
});
api.get('/courses', function (req, res) {
    request('https://vurooster.nl/suggest?q=' + req.query.q, function (error, response, body) {
        $ = cheerio.load(body);
        courses = [];
        $("a").each(function (index, element) {
            h = $(element).attr("href").replace("/course/", "")
            n = $(element).text()
            courses.push({name: n, id: h})

        });
        res.json(courses)
    });
})

api.get('/me/courses', function (req, res, next) {
    var u = req.user
    CourseModel.find({course_id: {"$in": u.courses}}, "name course_id", function (err, courses) {
        res.json(courses);
    });
})
api.get("/me/groups", function (req, res, next) {
    var u = req.user;
    GroupModel.find({_id: {"$in": u.groups}}).exec(function (err, arr) {
        res.json(arr);
    })
});

api.post('/users/subset', function (req, res) {
    UserModel.find({_id: {"$in": req.body.users}}, 'name surname picture', function (err, users) {
        res.json(users);
    });
});


api.post('/courses/add/:id', function (req, res, next) {
    if (req.user.courses.indexOf(req.params.id) < 0) {
        CourseModel.findOne({course_id: req.params.id}, function (err, c) {
            if (err || c == null) {
                course.loadCourse(req.params.id, function (err, course) {
                    if (err) {
                        res.sendStatus(500)
                    }
                    else {
                        var user = req.user
                        user.courses.push(req.params.id)
                        user.save(function (err, user) {
                            if (err || user == null) res.sendStatus(500)
                            else res.sendStatus(200)
                        })
                    }
                })
            }
            else {
                var user = req.user
                user.courses.push(req.params.id)
                user.save(function (err, user) {
                    if (err || user == null) res.sendStatus(500)
                    else res.sendStatus(200)
                })
            }
        });
    }
    else {

        res.sendStatus(200);
    }


})
api.post('/me/courses/delete', function (req, res) {
    var courses = req.body.courses;
    var u = req.user;
    u.courses = u.courses.filter(function (c) {
        return courses.filter(function (c1) {
                return c == c1.course_id
            }).length == 0;
    });
    u.save(function (err, user) {
        if (err) res.sendStatus(500)
        else res.sendStatus(200)
    });

});
api.post('/group/create', function (req, res) {
    var user = req.user;
    var users_id = req.body.users_id.map(function (id) {
        return ObjectId(id)
    });
    users_id.push(user._id);
    users_id.filter(function (elem, pos) {
        return users_id.indexOf(elem) == pos;
    });
    var group_name = req.body.group_name;
    var g = new GroupModel({name: group_name, users: users_id})
    g.save(function (err, group) {
        UserModel.update({_id: {"$in": users_id}}, {"$push": {"groups": g._id}}, {"multi": true}, function (err, obj) {
            res.sendStatus(err ? 500 : 200)
        });
    });


});

api.post("/groups/delete", function (req, res) {
    GroupModel.find({_id: req.body._id}, function (err, groups) {
        if (err || groups == null || groups.length == 0) {
            res.sendStatus(500);
        }
        else {
            var group = groups[0];
            var users = group.users;
            UserModel.update(
                {_id: {"$in": users}},
                {"$pull": {"groups": group._id}},
                {"multi": true}, function (err, obj) {
                    if (err) res.sendStatus(500);
                    else {
                        GroupModel.find({id: group._id}).remove().exec();
                        res.sendStatus(200)
                    }
                });
        }
    })
});

api.post("/logout" , function( req , res ){
    req.logout();
    res.redirect("/login.html");
});
module.exports = api;