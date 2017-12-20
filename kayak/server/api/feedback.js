var system = require('./db').collection('system')
var ObjectId = require("mongolian").ObjectId
var validator = require('validator');
var coach = require("./coach");
function getSportmanFeedbackOld(req, res) {
    try {


        var sportman_id = req.params.sportman_id,
            training_id = req.params.training_id,
            session_id = req.cookies.session_id;

        sportman_id = new ObjectId(sportman_id);
        training_id = new ObjectId(training_id);
        coach.checkCoachHasSportman(session_id, sportman_id, function (isOk, coach_id) {
            if (isOk) {
                system.find({
                    "_id": sportman_id,
                }, {
                    "trainings": {"$elemMatch": {"_id": training_id}},

                }).toArray(function (err, arr) {
                    if (err) {
                        res.status(500).json({"error": "Server error"});

                    }
                    else {
                        if (arr != null && arr.length > 0) {

                            res.json(arr[0]);
                        }
                        else {
                            res.status(404).json({"error": "not found "});
                        }
                    }
                });
            }
            else {
                res.status(401).json({"error": "not authorized here"})
            }
        });
    }
    catch(e){
        res.sendStatus(500);
    }
}


function getSportmanFeedback(req, res) {
    try {
        var sportman_id = req.params.sportman_id,
            training_id = req.params.training_id,
            session_id = req.cookies.session_id;

        sportman_id = new ObjectId(sportman_id);
        training_id = new ObjectId(training_id);

        if (req.remoteUser.role == "coach") {
            system.runCommand('aggregate', {
                    pipeline: [
                        {
                            "$match": {
                                "_id": new ObjectId(req.params.sportman_id)
                            }
                        },
                        {"$unwind": "$trainings"},
                        {"$match": {"trainings._id": new ObjectId(req.params.training_id)}},
                        {"$project": {"trainings.feedback": 1, "trainings.google_calendar_id": 1}}
                    ]
                },
                function (err, arr) {
                    if (!err) {

                        res.json(arr.result[0])
                    }
                    else {
                        res.sendStatus(500)
                    }
                });
        }
        else {
            res.sendStatus(401)
        }

    }
    catch (e) {
        res.sendStatus(500)
    }

};
module.exports = {
    getSportmanFeedback: getSportmanFeedback
}