var mongoose = require('mongoose');
var Schema = mongoose.Schema;
mongoose.connect('mongodb://localhost/CourseMatch');


var User = mongoose.model('User', {
    name: String,
    surname: String,
    groups: [{type: Schema.Types.ObjectId, ref: 'Group'}],
    google_id: String,
    picture: String,
    courses :[String]
})

var Group = mongoose.model('Group', {
    users: [{type: Schema.Types.ObjectId, ref: 'User'}],
    name: String

})

var Session = mongoose.model('Session', {
    token: String,
    timestamp: Number,
    google_id: String
});


var Course = mongoose.model('Course',
    {
        name : String,
        course_id: String,
        events: [{start: Date, end: Date}]
    }
);


module.exports = {
    User: User,
    Group: Group,
    Session: Session,
    Course : Course
}