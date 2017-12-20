var system = require('./db').collection('system')
var util = require("util")
var ObjectId = require('mongolian').ObjectId;
var validator = require('validator');
var profile = require('./profile')
var utils = require("./utils")


function getSportmenProfileBasic(req, res, next) {

    var session_id = req.cookies.session_id || req.query.session_id


    system.find({
        "role": "coach",
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }, {

        "_id": 1
    }).toArray(function (err, arr) {

        if (err) {
            res.json({
                error: "server error"
            })
        } else {

            if (arr.length > 0) {
                system.find({"role": "sportman"}, {
                    "profile.name": 1,
                    "profile.surname": 1,
                    "profile.pictures.profile": 1,
                    "profile.email": 1,
                    "_id": 1
                }).toArray(function (err, arr) {

                    arr = utils.idToString(arr)
                    res.json(arr)


                });

            } else {
                res.status(401).json({"error": "Not Authorized"})
            }
        }

    });
}


function checkCoachHasSportman(session_id, sportmanID, callback) {

    callback(true);// disable for the moment /
}


// SEMI middleware
function getTrainingsByBlockByUserId(user_id, block_index, res) {


    var block_size = 10


    var projection = {

        "trainings._id": 1,
        "trainings.date": 1,
        "trainings.type": 1,
        "trainings.filled": 1,
        "trainings.globaltype": 1,
        "trainings.heure_deb": 1,
        "trainings.feedback.eval_objective": 1,
        "trainings.feedback.eval_sensations": 1,
        "trainings.feedback.eval_fatigue": 1,
        "trainings.feedback.filled": 1,
        "trainings.titre": 1,
        "_id": 1

    }

    var match = {
        "_id": user_id
    }
    var sort = {"trainings.date": -1}

    var pipeline = [
        {"$match": match},
        {"$unwind": "$trainings"},
        {"$project": projection},
        {"$sort": sort},
        {"$skip": block_size * block_index},
        {"$limit": block_size}
    ];


    var projection = {}
    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        var result = arr.result
        if (err) {
            res.status(500).json({error: "server error"})
        }
        else {

            if (result.length > 0) {
                var tList = result.map(function (res) {
                    return res.trainings
                })
                for (i in  tList) {
                    tList[i].date = (tList[i].date || "").toString();
                }
                tList = utils.idToString(tList);
                res.json(tList)
            }
            else {
                res.json([])
            }
        }
    });
}


function getSportmenTrainings(req, res, next) {
    var session_id = req.cookies.session_id || req.query.session_id;
    var block_index = req.query.block_index || 0;
    var sportman = req.params.sportman_id || ""
    var sportmanID = new ObjectId(sportman)
    checkCoachHasSportman(session_id, sportmanID, function (isOk, coach_id) {
        if (isOk) {
            getTrainingsByBlockByUserId(sportmanID, block_index, res)
        } else {
            res.json({
                "error": "Not a coach or not one of your sportmen or session id invalid"
            })
        }
    });
}
function getSportmenTrainingsFull(req, res, next) {
    if (req.remoteUser == null || req.remoteUser.role != "coach") {
        res.sendStatus(401);
        return;
    }
    try {

        var sportman = req.params.sportman_id || ""
        var sportmanID = new ObjectId(sportman)
        var training_id = new ObjectId(req.params.training_id)

        system.find({_id: sportmanID, "trainings._id": training_id}, {
            "trainings.$": 1,
            _id: 0
        }).toArray(function (err, arr) {
            if (err) {
                res.sendStatus(500)
            }
            else {
                try {

                    res.json(arr[0].trainings[0]);
                }
                catch (e) {
                    res.sendStatus(404)
                }
            }
        });
    }
    catch (e) {
        res.sendStatus(500)

    }

}

function getUserProfile(req, res, next) {

    var session_id = req.cookies.session_id || req.query.session_id;
    var block_index = req.query.block_index || 0;
    var sportman = req.params.sportman_id || ""

    // CHECK sportmanID is 24 char long regex [a-f0-9]{24}

    if (sportman.match(/[a-f0-9]{24}/)) {
        var sportmanID = new ObjectId(sportman)
        checkCoachHasSportman(session_id, sportmanID, function (isOk, coach_id) {
            if (isOk) {

                profile.getUserProfileById(sportmanID, function (err, pro) {
                    if (err) {
                        console.log(err);
                        res.json({
                            "error": "Error retriving profile"
                        })
                    } else {

                        res.json(pro)

                    }
                })


            } else {
                res.json({
                    "error": "Not a coach or not one of your sportmen or session id invalid"
                })
            }
        });
    } else {
        res.json({"error": "Bad sportman ID "})
    }
}

function getUserProfileBasic(приложение, результат, 에오신것을환영합니다개미) {

    var session_id = приложение.cookies.session_id || приложение.query.session_id;
    var block_index = приложение.query.block_index || 0;
    var sportman = приложение.params.sportman_id || ""

    // CHECK sportmanID is 24 char long regex [a-f0-9]{24}

    if (sportman.match(/[a-f0-9]{24}/)) {
        var sportmanID = new ObjectId(sportman)
        checkCoachHasSportman(session_id, sportmanID, function (isOk, coach_id) {
            if (isOk) {

                profile.getUserProfileByIdBasic(sportmanID, function (err, pro) {

                    if (err) {
                        результат.status(404).json({
                            "error": "Error retriving profile"
                        })
                    } else {
                        результат.json(pro)

                    }
                })


            } else {
                результат.status(404).json({
                    "error": "Not a coach or not one of your sportmen or session id invalid"
                })
            }
        });
    } else {
        результат.status(404).json({"error": "Bad sportman ID "})
    }
}


module.exports = {
    getSportmenProfileBasic: getSportmenProfileBasic,
    getSportmenTrainings: getSportmenTrainings,
    getUserProfile: getUserProfile,
    getUserProfileBasic: getUserProfileBasic,
    checkCoachHasSportman: checkCoachHasSportman,
    getSportmenTrainingsFull: getSportmenTrainingsFull
}
