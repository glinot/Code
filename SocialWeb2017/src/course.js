/**
 * Created by grego on 3/15/2017.
 */

var ical = require('node-ical');
var db = require("./db")
var request = require('request')
var cheerio = require('cheerio')

var CourseModel = db.Course


function getCourseName(course_id, callback) {
    request('https://vurooster.nl/course/' + course_id, function (error, response, body) {
        $ = cheerio.load(body);
        callback(error, ($("title").text() || "").replace("VUrooster - ",""))
    })
}
function loadCourse(course_id, callback) {
    getCourseName(course_id, function (err, name) {
        if(err)callback(err,null)
        ical.fromURL('https://vurooster.nl/feedcourse/' + course_id, {}, function (err, data) {
            var events = []
            for (var k in data) {
                if (data[k] && data[k].type == "VEVENT") {
                    var evt = data[k];
                    var start = evt.start
                    var end = evt.end
                    events.push({start: start, end: end})
                }
            }
            var course = new CourseModel({name: name, course_id: course_id, events: events});
            course.save(callback);
        });
    })
}


module.exports = {
    loadCourse: loadCourse
}