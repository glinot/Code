var system = require('./db').collection('system')
var db = require('./db')
var ObjectId = require("mongolian").ObjectId
var utils = require("./utils")
var coach = require("./coach")
var moment = require("moment")

function getTrainingById(req, res, next) {

    var id = req.params.training_id
    var session_id = req.cookies.session_id || req.query.session_id
    id = new ObjectId(id)
    if (id == null) {
        res.status(404).json({
            error: true,
            message: "No such training id"
        })
    } else {

        system.find({
            "trainings._id": id,
            "sessions.session_id": session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            }
        }, {
            trainings: {"$elemMatch": {"_id": id}},
            _id: 0
        }).sort({
            "trainings.date": -1
        }).toArray(function (err, arr) {
            if (arr.length > 0 && arr[0].trainings != null && arr[0].trainings.length > 0) {


                if (arr[0].trainings[0].date && arr[0].trainings[0].date.toISOString) {
                    arr[0].trainings[0].date = arr[0].trainings[0].date.toISOString()
                }
                else {
                    arr[0].trainings[0].date = new Date(arr[0].trainings[0].date).toISOString()
                }
                res.json(utils.idToString(arr[0].trainings[0]));
            }
            else {
                res.status(404).json({"error": "no such training"});
            }

        });


    }

}


//pbm git a checker


function getTrainingsByBlock(req, res, next) {

    var block_index = req.query.block_index || 0
    var block_size = 10
    var session_id = req.cookies.session_id
    system.find({
            "sessions.session_id": session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            }
        }, {
            "trainings": 1,
            "_id": 1
        }
    ).sort({
        "trainings.date": -1
    }).skip(block_size * block_index)
        .limit(block_size)
        .toArray(function (err, result) {
            if (err) {
                res.json({error: err})
            } else {

                if (result.length > 0) {

                    res.json(result[0].trainings)
                }
                else {
                    res.json([])
                }
            }
        })

}
function getTrainingsByBlockBasic(req, res, next) {

    var block_index = req.query.block_index || 0
    var block_size = 10
    var session_id = req.cookies.session_id


    var projection = {
        "trainings.google_calendar_id" : 1,
        "trainings.origin" : 1 ,
        "trainings._id": 1,
        "trainings.titre": 1,
        "trainings.date": 1,
        "trainings.lieu": 1,
        "trainings.type": 1,
        "trainings.filled": 1,
        "trainings.globaltype": 1,
        "trainings.procede": 1,
        "trainings.heure_deb": 1,
        "trainings.heure_fin": 1,
        "trainings.feedback.eval_objective": 1,
        "trainings.feedback.eval_sensations": 1,
        "trainings.feedback.eval_fatigue": 1,
        "trainings.feedback.filled": 1,
        "_id": 1

    }

    var match = {
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
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

        if (err) {
            res.status(500).json({error: "server error"})
        }
        else {

            var result = arr.result
            if ((result || []).length > 0) {
                var tList = result.map(function (res) {
                    return res.trainings
                })
                for (i in  tList) {
                    tList[i].date = (tList[i].date || "").toString();
                }
                tList = require('./utils').idToString(tList);
                res.json(tList)
            }
            else {
                res.json([])
            }
        }
    });


}


function createNewTrainingSportman(sportman_id, training, res, objectId) {
    training._id = objectId || new ObjectId()
    system.update({"_id": sportman_id}, {"$push": {"trainings": training}}, function (err, nb) {

        if (err || nb == 0) {
            res.status(500).json({"error": "error inserting "})
        }
        else {
            res.json({"success": "true"})
        }
    });

}

function createNewTrainingCoach(coach_id, sportmen, training, res) {
    training._id = new ObjectId()
    training.sportmen = sportmen.map(function (s) {
        return new ObjectId(s)
    })

    var trainingSportmen = training;
    system.update({"_id": coach_id}, {"$push": {"trainings": training}}, function (err, nb) {

        if (err || nb == 0) {
            res.status(500).json({"error": "error inserting "})
        }
        else {
            res.json({"success": "true", "training_id": training._id.toString()})
        }
    });


}


function createNewTrainingForSportman(req, res) {
    var sportman_id = "";
    try {
        sportman_id = new ObjectId(req.params.sportman_id)
    }
    catch (e) {
        sportman_id = ""
    }
    var training = {
        "origin" : req.remoteUser.role,
        "filled": false,
        "date": new Date(req.body.training.date) || "",
        "lieu": req.body.training.lieu || "",
        "type": req.body.training.type || "",
        "globaltype": req.body.training.globaltype || "",
        "heure_deb": parseInt(req.body.training.heure_deb) || 0,
        "heure_fin": parseInt(req.body.training.heure_fin) || 0,
        "milieu": req.body.training.milieu || "",
        "objective": req.body.training.objective || "",
        "description" : req.body.training.description || "",
        "procede": req.body.training.procede || "",
        "titre": req.body.training.titre || "",
        "google_calendar_id": req.body.google_calendar_id || "",
        "bilan" : "",
        "feedback": {
            "eval_objective": "",
            "eval_sensations": "",
            "eval_fatigue": "",
            "objectives_text": "",
            "temps_travail_intensite": "",
            "difficulte": "",
            "nb_portes_franchies": "",
            "nb_erreurs": "",
            "nb_parcours_realises": "",
            "nb_parcours_a_zero": "",
            "nb_penalites_seance": "",
            "nb_km": "",
            "fc_moyenne": "",
            "fc_max": ""
        }
    }


    coach.checkCoachHasSportman(req.cookies.session_id, sportman_id, function (isCoach) {
        if (isCoach) {
            createNewTrainingSportman(sportman_id, training, res)
        }
        else {
            res.status(403).json({"error": "not a coach"})
        }
    });
}


function updateGoogleIds(users, training_id, callback) {
    var user = users.pop();
    if (user != null) {
        system.update({"profile.email": user.email, "trainings._id": training_id},
            {
                "$set": {
                    "trainings.$.google_calendar_id": user.id,
                    "trainings.$.google_calendar_html_link": user.html_link
                },

            }, true, false, function (err, arr) {
                updateGoogleIds(users, training_id, callback);
            });
    }
    else {
        callback();
    }
}
function createTrainingCoachAndSportmen(req, res) {
    if (req.remoteUser.role != "coach") {
        res.sendStatus(401);
        return;
    }
    try {


        var training = {
            "origin" : "coach",
            "filled": false,
            "date": new Date(req.body.training.date) || "",
            "lieu": req.body.training.lieu || "",
            "type": req.body.training.type || "",
            "globaltype": req.body.training.globaltype || "",
            "heure_deb": parseInt(req.body.training.heure_deb) || 0,
            "heure_fin": parseInt(req.body.training.heure_fin) || 0,
            "milieu": req.body.training.milieu || "",
            "objective": req.body.training.objective || "",
            "description" : req.body.training.description || "",
            "procede": req.body.training.procede || "",
            "titre": req.body.training.titre || "",
            "google_calendar_id": req.body.google_calendar_id || "",
            "google_calendar_html_link": "",
            "bilan" : ""

        }
        var feedback = {
            "eval_objective": "",
            "eval_sensations": "",
            "eval_fatigue": "",
            "objectives_text": "",
            "temps_travail_intensite": "",
            "difficulte": "",
            "nb_portes_franchies": "",
            "nb_erreurs": "",
            "nb_parcours_realises": "",
            "nb_parcours_a_zero": "",
            "nb_penalites_seance": "",
            "nb_km": "",
            "fc_moyenne": "",
            "fc_max": ""
        };


        var tCoach = utils.cloneObject(training);
        tCoach.sportmen = req.body.sportmen.map(function (s) {
            return new ObjectId(s)
        });
        var tSportman = utils.cloneObject(training);
        tSportman.feedback = feedback;


        var commonObjectId = new ObjectId()

        tCoach._id = commonObjectId
        tSportman._id = commonObjectId

        tCoach.date=new Date(req.body.training.date) || ""
        tSportman.date=new Date(req.body.training.date) || ""
        system.update({"_id": req.remoteUser._id}, {"$push": {"trainings": tCoach}}, function (err, arr) {
            if (err) {
                console.log(err);
                res.sendStatus(500);
            }
            else {
                system.update({_id: {"$in": tCoach.sportmen}}, {"$push": {"trainings": tSportman}}, false, true, function (err_1, arr_1) {
                    updateGoogleIds(req.body.google_ids || [], commonObjectId, function () {
                        res.sendStatus(err_1 == null ? 200 : 500);

                    });
                });
            }
        })


    }
    catch (e) {
        console.log(e);
        res.sendStatus(400);
    }

}


function createNewTraining(req, res) {

    if (req.body.training != null) {

        try {

            var hourDeb = req.body.training.heure_deb;
            var hourFin = req.body.training.heure_fin;
            var training = {
                "origin" :req.remoteUser.role,
                "filled": false,
                "date": new Date(req.body.training.date) || "",
                "lieu": req.body.training.lieu || "",
                "type": req.body.training.type || "",
                "globaltype": req.body.training.globaltype || "",
                "heure_deb": hourDeb,
                "heure_fin": hourFin,
                "milieu": req.body.training.milieu || "",
                "objective": req.body.training.objective || "",
                "procede": req.body.training.procede || "",
                "titre": req.body.training.titre || "",
                "google_calendar_id": req.body.training.google_calendar_id || "",
                "google_calendar_html_link": req.body.training.google_calendar_html_link||"",
                "bilan" : "",
                "description" : req.body.training.description || "",
                "feedback": {
                    "eval_objective": 1,
                    "eval_sensations": 1,
                    "eval_fatigue": 1,
                    "objectives_text": "",
                    "temps_travail_intensite": "",
                    "difficulte": "",
                    "nb_portes_franchies": "",
                    "nb_erreurs": "",
                    "nb_parcours_realises": "",
                    "nb_parcours_a_zero": "",
                    "nb_penalites_seance": "",
                    "nb_km": "",
                    "fc_moyenne": "",
                    "fc_max": ""
                }
            }

            system.find({
                "sessions.session_id": req.cookies.session_id,
                "sessions.timeout": {
                    "$gt": new Date().getTime()
                }
            }, {"role": 1}).toArray(function (err, arr) {


                if (err) {
                    res.status(500).json({error: "server error"})
                }
                else {

                    if (arr && arr.length > 0) {

                        var role = arr[0].role
                        if (role === "sportman") {
                            createNewTrainingSportman(arr[0]._id, training, res)
                        }
                        else if (role == "coach") {
                            createNewTrainingCoach(arr[0]._id, req.body.training.sportmen || [], training, res)
                        }

                    }
                    else {
                        res.status(401).json({"error": "Not Authorized"})
                    }
                }
            });
        }
        catch (e) {
            console.log(e);
            res.sendStatus(400);
        }


    }
    else {
        res.sendStatus(400);
    }
}

/**
 *
 * @deprecated since v2
 */
function fillTraining(req, res) {
    var session_id = req.cookies.session_id
    var training_id = req.params.training_id
    var tid = new ObjectId(training_id)
    var training = req.body.training // <--- $.post("/api/trainings/fill/572fa84c0000007822000002" ,{training : {"filled" : "true"}})
    system.find({
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }, {"_id": 1}).toArray(function (e, arr) {
        if (e) {
            res.status(500).json({"error": "server error"})
        }
        else {
            if (arr.length == 0) {
                res.status(404).json({"error": "Session expired"})
            }
            else {
                db.runCommand(
                    {
                        update: "system",
                        updates: [
                            {
                                q: {
                                    "sessions.session_id": session_id,
                                    "trainings._id": tid
                                }, u: {
                                "$set": {
                                    "trainings.$.feedback.filled": true,
                                    "trainings.$.feedback.eval_objective": training.eval_objective || 1,
                                    "trainings.$.feedback.eval_sensations": training.eval_sensations || 1,
                                    "trainings.$.feedback.eval_fatigue": training.eval_fatigue || 1,
                                    "trainings.$.feedback.objectives_text": training.objectives_text || "",
                                    "trainings.$.feedback.temps_travail_intensite": training.temps_travail_intensite || "",
                                    "trainings.$.feedback.difficulte": training.difficulte || "",
                                    "trainings.$.feedback.nb_portes_franchies": training.nb_portes_franchies || "",
                                    "trainings.$.feedback.nb_erreurs": training.nb_erreurs || "",
                                    "trainings.$.feedback.nb_parcours_realises": training.nb_parcours_realises || "",
                                    "trainings.$.feedback.nb_parcours_a_zero": training.nb_parcours_a_zero || "",
                                    "trainings.$.feedback.nb_penalites_seance": training.nb_penalites_seance || "",
                                    "trainings.$.feedback.nb_km": training.nb_km || "",
                                    "trainings.$.feedback.fc_moyenne": training.fc_moyenne || "",
                                    "trainings.$.feedback.fc_max": training.fc_max || ""
                                }
                            },
                                upsert: false, multi: true
                            }
                        ]
                    },
                    function (err, arr) {
                        res.json({arr: arr})
                    }
                )
            }
        }
    })


}
function fillTrainingV2(req, res) {
    var session_id = req.cookies.session_id
    var training_id = req.params.training_id
    var tid = new ObjectId(training_id)
    var training = req.body.training // <--- $.post("/api/trainings/fill/572fa84c0000007822000002" ,{training : {"filled" : "true"}})
    system.find({
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }, {"_id": 1}).toArray(function (e, arr) {
        if (e) {
            res.status(500).json({"error": "server error"})
        }
        else {
            if (arr.length == 0) {
                res.status(404).json({"error": "Session expired"})
            }
            else {
                db.runCommand(
                    {
                        update: "system",
                        updates: [
                            {
                                q: {
                                    "sessions.session_id": session_id,
                                    "trainings._id": tid
                                }, u: {
                                "$set": {
                                    "trainings.$.feedback.filled": true,
                                    "trainings.$.feedback.eval_objective": training.feedback.eval_objective || 1,
                                    "trainings.$.feedback.eval_sensations": training.feedback.eval_sensations || 1,
                                    "trainings.$.feedback.eval_fatigue": training.feedback.eval_fatigue || 1,
                                    "trainings.$.feedback.objectives_text": training.feedback.objectives_text || "",
                                    "trainings.$.feedback.temps_travail_intensite": training.feedback.temps_travail_intensite || "",
                                    "trainings.$.feedback.difficulte": training.feedback.difficulte || "",
                                    "trainings.$.feedback.nb_portes_franchies": training.feedback.nb_portes_franchies || "",
                                    "trainings.$.feedback.nb_erreurs": training.feedback.nb_erreurs || "",
                                    "trainings.$.feedback.nb_parcours_realises": training.feedback.nb_parcours_realises || "",
                                    "trainings.$.feedback.nb_parcours_a_zero": training.feedback.nb_parcours_a_zero || "",
                                    "trainings.$.feedback.nb_penalites_seance": training.feedback.nb_penalites_seance || "",
                                    "trainings.$.feedback.nb_km": training.feedback.nb_km || "",
                                    "trainings.$.feedback.fc_moyenne": training.feedback.fc_moyenne || "",
                                    "trainings.$.feedback.fc_max": training.feedback.fc_max || ""
                                }
                            },
                                upsert: false, multi: true
                            }
                        ]
                    },
                    function (err, arr) {
                        res.json({arr: arr})
                    }
                )
            }
        }
    })


}

function removeTraining(req, res) {
    if (req.remoteUser) {
        U = req.remoteUser
        var training_id = req.params.training_id
        var tid = new ObjectId(training_id)
        if (U.role == "coach") {
            db.runCommand(
                {
                    update: "system",
                    updates: [
                        {
                            q: {}, u: {
                            "$pull": {trainings: {_id: tid}}
                        },
                            upsert: false, multi: true
                        }
                    ]
                },
                function (err, arr) {
                    res.json({arr: arr})
                }
            )


        }
        else { // to glitch  admin

            db.runCommand(
                {
                    update: "system",
                    updates: [
                        {
                            q: {_id: U._id}, u: {
                            "$pull": {trainings: {_id: tid}}
                        },
                            upsert: false, multi: true
                        }
                    ]
                },
                function (err, arr) {
                    res.json({arr: arr})
                }
            )
        }

    }
    else {
        res.sendStatus(401);
    }

}

function removeSportmanFromTraining(req, res) {
    var u = req.remoteUser
    if (u != null && u.role == "coach") {
        try {
            var sportmanID = new ObjectId(req.params.sportman_id)
            var trainingID = new ObjectId(req.params.training_id)
            system.update({_id: sportmanID}, {$pull: {trainings: {_id: trainingID}}}, function (err, arr) {
                if (err == null) {
                    system.update({
                        _id: u._id,
                        "trainings._id": trainingID
                    }, {"$pull": {"trainings.$.sportmen": sportmanID}}, function (err, arr) {

                        if (err) {
                            res.sendStatus(500)
                        }
                        else {
                            res.sendStatus(200);
                        }

                    });
                }
                else {
                    res.sendStatus(500)
                }
            })
        }
        catch (e) {
            console.log(e)
        }
    }
    else {
        res.sendStatus(401);
    }
}
function addGoogleId(req, res) {
    if (req.remoteUser) {
        try {
            system.update({_id: req.remoteUser._id, "trainings._id": new ObjectId(req.params.training_id)},
                {"$set": {"trainings.$.google_calendar_id": req.body.google_calendar_id}}, {upsert: true},
                function (err, arr) {
                    if (err || arr == 0) {
                        res.sendStatus(400);
                    }
                    else {
                        res.sendStatus(200);
                    }
                }
            )
        }
        catch (e) {

        }
    }
    else {
        res.sendStatus(401);
    }
}

function getGoogleSportmanCalendarId(req, res) {
    if (req.remoteUser && req.remoteUser.role == "coach") {
        try {
            system.runCommand("aggregate",
                {
                    pipeline: [
                        {"$match": {"_id": new ObjectId(req.params.sportman_id)}},
                        {"$unwind": "$trainings"},
                        {"$match": {"trainings._id": new ObjectId(req.params.training_id)}},
                        {"$project": {"trainings.google_calendar_id": 1, "_id": 0}}

                    ]
                }, function (err, arr) {
                    if (err) {
                        res.sendStatus(500)
                    }
                    else {
                        res.json(arr.result[0]);
                    }
                });
        }
        catch (e) {

        }
    }
    else {
        res.sendStatus(401);
    }
}
// add sportmen to exiting training
function addSportmen(req, res) {
    if (req.remoteUser != null && req.remoteUser.role == "coach") {


        try {
            var sportmen = req.body.sportmen.map(function (id) {
                return new ObjectId(id);
            });
            var training_id = new ObjectId(req.params.training_id);
            var training = req.body.training;


            var toPushTraining = {
                _id: training_id,
                origin : req.remoteUser.role,
                filled: false,
                date: new Date(training.date),
                lieu: JSON.stringify(training.lieu),
                globaltype: "" + (training.globaltype),
                heure_deb: parseInt(training.heure_deb),
                heure_fin: parseInt(training.heure_fin),
                milieu: "" + (training.milieu),
                objective: "" + (training.objective),
                description: "" + (training.description),
                procede: "" + (training.procede),
                titre: "" + (training.titre),
                type: "" + (training.type),
                google_calendar_id: "" + (training.google_calendar_id),
                google_calendar_html_link: "",
                feedback: {
                    eval_objective: "",
                    eval_sensations: "",
                    eval_fatigue: "",
                    objectives_text: "",
                    temps_travail_intensite: "",
                    difficulte: "",
                    nb_portes_franchies: "",
                    nb_erreurs: "",
                    nb_parcours_realises: "",
                    nb_parcours_a_zero: "",
                    nb_penalites_seance: "",
                    nb_km: "",
                    fc_moyenne: "",
                    fc_max: ""
                }

            };
            system.update({
                _id: req.remoteUser._id,
                "trainings._id": training_id
            }, {"$pushAll": {"trainings.$.sportmen": sportmen}}, function (err, nb) {

                if (err || nb == 0) {
                    res.sendStatus(500);
                }
                else {
                    system.update({_id: {"$in": sportmen}}, {"$push": {"trainings": toPushTraining}}, false, true, function (err_1, nb_1) {
                        if (err_1 || nb_1 == 0) {
                            res.sendStatus(500);
                        }
                        else {
                            updateGoogleIds(req.body.gapi_ids, training_id, function (err) {
                                if (err) {
                                    res.sendStatus(500)
                                }
                                else {
                                    res.sendStatus(200);
                                }
                            });
                        }
                    });
                }


            });
        }
        catch (e) {
            console.log(e);

            res.sendStatus(500);
        }

    }
    else {
        res.sendStatus(401);
    }
}
function getGoogleCalendarIds(req, res) {
    if (req.remoteUser && req.remoteUser.role == "coach") {
        try {
            var training_id = new ObjectId(req.params.training_id);

            system.find({_id: req.remoteUser._id, "trainings._id": training_id}, {
                "trainings.$.sportmen": 1,
                _id: 0
            }).toArray(function (err, arr) {

                var users = [];
                try {
                    users = arr[0].trainings[0].sportmen;
                }
                catch (e) {
                }
                ;
                users.push(req.remoteUser._id);
                system.runCommand('aggregate', {
                        pipeline: [

                            {"$match": {"_id": {"$in": users}}},
                            {"$unwind": "$trainings"},
                            {"$match": {"trainings._id": training_id}},
                            {"$project": {"trainings.google_calendar_id": 1, _id: 0, "profile.email": 1}}

                        ]
                    },
                    function (err, arr) {
                        if (!err) {
                            res.json(arr.result.map(function (s) {
                                return {email: s.profile.email, id: s.trainings.google_calendar_id};
                            }));
                        }
                        else {
                            res.sendStatus(500);
                        }
                    });
            });

        } catch (e) {

            res.sendStatus(500);

        }

    }
    else {
        res.sendStatus(401);
    }
}


function addBilan(req, res) {

    if(!req.remoteUser && req.remoteUser.role != "coach"){res.sendStatus(401) ; return ;}
    try {

        var training_id = new ObjectId(req.params.training_id);
        var bilan = req.body.bilan || ""

        system.update({_id : req.remoteUser._id , "trainings._id" : training_id },
            {"$set" : {"trainings.$.bilan" : bilan }},
            function(err , arr){
                res.sendStatus(err ?  500 : 200)
            }
        );


    }
    catch (e) {
        res.sendStatus(500)
    }
}
module.exports = {
    //getMyTrainings: getMyTrainings,
    getTrainingById: getTrainingById,
    getTrainingsByBlock: getTrainingsByBlock,
    getTrainingsByBlockBasic: getTrainingsByBlockBasic,
    createNewTraining: createNewTraining,
    createNewTrainingForSportman: createNewTrainingForSportman,
    fillTraining: fillTraining,
    fillTrainingV2: fillTrainingV2,
    removeTraining: removeTraining,
    createTrainingCoachAndSportmen: createTrainingCoachAndSportmen,
    removeSportmanFromTraining: removeSportmanFromTraining,
    addGoogleId: addGoogleId,
    getGoogleSportmanCalendarId: getGoogleSportmanCalendarId,
    addSportmen: addSportmen,
    getGoogleCalendarIds: getGoogleCalendarIds,
    addBilan: addBilan

}
