var fs = require('fs');
var system = require('./db').collection('system')
var ObjectId = require("mongolian").ObjectId
var stats = require("./stats")
var auth = require("./authentification")
var crypto = require("crypto")
var heathFeatures = ["weight", ""]
var utils = require("./utils")

function storePasswordFeature(req, res) {
    var pass = req.body.value || ""
    var hash = auth.hash(pass + "")
    system.update({
            "sessions.session_id": req.cookies.session_id,
            "sessions.timeout": {"$gt": new Date().getTime()}
        }, {
            "$set": {"profile.password": hash}
        },
        function (err, arr) {
            if (err) {
                res.status(500).json({"error": "Server Error"})
            }
            else {
                if (arr > 0) {
                    res.status(200).json({"nb": arr})
                }
                else {
                    res.status(401).json({"error": "not authorized"})

                }
            }

        });
}


function isSportman(session_id, callback) {
    system.find({
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        },
        "role": "sportman"
    }).toArray(function (err, arr) {
        if (err) {
            callback(false);
        }
        else {
            if (Array.isArray(arr) && arr.length > 0) {
                callback(true)
            }
            else {
                callback(false)
            }
        }
    });
}
function isCoach(session_id, callback) {
    system.find({
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        },
        "role": "coach"
    }).toArray(function (err, arr) {
        if (err) {
            callback(false);
        }
        else {
            if (Array.isArray(arr) && arr.length > 0) {
                callback(true)
            }
            else {
                callback(false)
            }
        }
    });
}

function getMoodToday(req, res) {


    var session_id = req.cookies.session_id
    system.find({
            "sessions.session_id": req.cookies.session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            },
            "stats.mood.date": new Date().toDateString()
        },
        {"profile.mood": 1, "_id": 0}).toArray(
        function (err, arr) {
            if (err) {
                res.status(500).json({"error": "server error"})

            }
            else {

                if ((arr || []).length > 0) {

                    res.json(arr[0])

                }
                else {
                    res.status(404).json({"error": "no mood today"})
                }
            }
        }
    );


}


function addMoodToday(req, res) {

    var value = req.body.indice
    var user = {
        "sessions.session_id": req.cookies.session_id,
        "sessions.timeout": {"$gt": new Date().getTime()}
    }

    var update = {}
    update["profile.mood"] = value
    system.update(
        user,
        {"$set": update},
        function (err, arr) {
            if (!err) {
                var toPush = {}
                toPush["stats.mood"] = {"value": value, "date": new Date().toDateString()} // custom record
                system.update(user, {"$push": toPush}, function (err, arr) {


                    res.json({"succes": "true"});
                })
            }
            else {
                res.status(500).json({"error": "error updating "})
            }
        }
    )

}
function getMyProfile(req, res, next) {


    var session_id = req.cookies.session_id || req.query.session_id || "";
    system.find({

        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }, {
        "profile": 1,
        "_id": 0

    }).toArray(function (err, users) {
        if (err != null) {
            res.status(301).json({"error": "Bad credentials"});
            console.log(err)

        }
        else {
            if (users.length > 0) {
                var user = users[0]
                user.profile.password = "***********"//not to send plain text password
                res.json(user.profile)
            }
            else {
                res.status(301).json({"error": "No Such User"});
            }
        }

    });


}

function getMyBasicProfile(req, res, next) {


    var session_id = req.cookies.session_id || req.query.session_id;

    if (!validator.isMongoId(session_id)) {

        system.find({

            "sessions.session_id": session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            }
        }, {
            "profile.name": 1,
            "profile.surname": 1,
            "profile.pictures.profile": 1,
            "_id": 0

        }).toArray(function (err, users) {
            if (err != null) {
                res.status(301).json({"error": "Bad credentials"});
                console.log(err)

            }
            else {
                if (users.length > 0) {
                    var user = users[0]
                    res.json(user.profile)
                }
                else {
                    res.status(301).json({"error": "No Such User"});
                }
            }

        });


    }
    else {
        res.json({}) // Not too much informations
    }
}


function getUserProfileById(user_id, callback) {
    system.find({"_id": user_id}, {
        "profile": 1,
        "_id": 0

    }, {"profile": 1}).toArray(function (err, array) {
        if (err || array.length == 0) {
            callback(true)
        }
        else {
            array[0].profile.password = "*********";
            callback(false, array[0].profile)
        }

    });
}
function getUserProfileByIdBasic(user_id, callback) {
    system.find({"_id": user_id},
        {
            "profile.name": 1,
            "profile.surname": 1,
            "profile.email": 1,
            "profile.pictures.profile": 1,
            "_id": 1
        }
    ).toArray(function (err, array) {
        if (err || array.length == 0) {
            callback(true)
        }
        else {

            var rep = array[0]
            rep = utils.idToString(rep)
            rep.profile.password = "********"
            callback(false, rep)
        }

    });
}


function storeBoatType(req, res) {
    stats.storeTimeFeature("boat_type", "profile.boat.type", req.body.value, req.cookies.session_id, res)
}
function storeBoatRecoupe(req, res) {
    stats.storeTimeFeature("boat_recoupe", "profile.boat.recoupe", req.body.value, req.cookies.session_id, res)
}
function storeBoatConstruction(req, res) {
    stats.storeTimeFeature("boat_construction", "profile.boat.construction", req.body.value, req.cookies.session_id, res)
}

function storePagaieType(req, res) {
    stats.storeTimeFeature("pagaie_type", "profile.pagaie.type", req.body.value, req.cookies.session_id, res)
}

function storePagaieLongueur(req, res) {
    stats.storeTimeFeature("pagaie_longueur", "profile.pagaie.longueur", req.body.value, req.cookies.session_id, res)
}

function storePagaieSurface(req, res) {
    stats.storeTimeFeature("pagaie_surface", "profile.pagaie.surface", req.body.value, req.cookies.session_id, res)
}


function storeWeight(req, res) {
    stats.storeTimeFeature("weight", "profile.health.weight", req.body.value, req.cookies.session_id, res)
}
function storeHeight(req, res) {
    stats.storeTimeFeature("height", "profile.health.height", req.body.value, req.cookies.session_id, res)
}
function storeFatPercentage(req, res) {
    stats.storeTimeFeature("fat_percentage", "profile.health.fat_percentage", req.body.value, req.cookies.session_id, res)
}
function storeHrStanding(req, res) {
    stats.storeTimeFeature("hr_standing", "profile.health.hr_standing", req.body.value, req.cookies.session_id, res)
}
function storeFatigueIndex(req, res) {
    stats.storeTimeFeature("fatigue_index", "profile.health.fatigue_index", req.body.value, req.cookies.session_id, res)
}

function storePassword(req, res) {
    storePasswordFeature(req, res)
}

// coach only function
function storeHrvTest(req, res) {
    var u = req.remoteUser;

    if (u) {
        try {
            var sportman_id = new ObjectId(req.params.sportman_id);
            var val = req.body.value;
            system.update({_id: sportman_id}, {
                "$set": {"profile.health.hrv_test": val},
                "$push": {"stats.hrv_test": {value: val, "date": new Date()}}
            }, function (err, nb) {
                if (nb == 0) res.sendStatus(404);
                else {
                    res.sendStatus(err ? 500 : 200);
                }

            });
        }
        catch (e) {
            res.sendStatus(400)
        }
    }
    else {
        res.sendStatus(401)
    }
}

function uploadPhoto(req, res) {
    if(req.remoteUser  == null){res.sendStatus(401) ; return;}

    var data = ((req.body.data || "").split(",")[1] || "")
    var buffer = new Buffer(data || "", 'base64')
    var img_name = req.remoteUser._id.toString();
    var path = "./uploads/img/" + img_name;
    try {
        var stream = fs.createWriteStream(path);
        stream.once('open', function (fd) {
            stream.write(buffer)
            stream.end();
            system.update(
                {
                    "sessions.session_id": req.cookies.session_id || "",
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
                , {
                    "$set": {
                        "profile.pictures.profile": "/img/" + img_name
                    }
                },
                function (e, arr) {
                    if (e) {
                        res.status(500).json({"error": "server error"})
                    }
                    else {
                        res.json(arr);
                    }
                }
            )
        });
    }
    catch (e) {
        res.status(500).json({"error": "server error"})
    }


}


function updateSimpleProfileFeature(req, res) {
    try {
        var name = req.body.name
        var surname = req.body.surname
        var coach = {
            club: (req.body.coach || {}).club,
            eqf: (req.body.coach || {}).eqf
        }
        var phone = req.body.phone
        var studies = req.body.studies
        var resp_studies = req.body.resp_studies
        var contact_studies = req.body.contact_studies

        var boat = (req.body.boat || {})
        var pagaie = (req.body.pagaie || {})

        var boat_type = boat.type || ""
        var boat_recoupe = boat.recoupe || ""
        var boat_construction = boat.construction || ""
        var pagaie_type = pagaie.type || ""
        var pagaie_longueur = pagaie.longueur || ""
        var pagaie_surface = pagaie.surface || ""

        U = req.remoteUser
        system.update({"_id": U._id}, {
            "$set": {
                "profile.name": name,
                "profile.surname": surname,
                "profile.coach": coach,
                "profile.phone": phone,
                "profile.studies": studies,
                "profile.resp_studies": resp_studies,
                "profile.contact_studies": contact_studies,
                "profile.boat.type": boat_type,
                "profile.boat.recoupe": boat_recoupe,
                "profile.boat.construction": boat_construction,
                "profile.pagaie.type": pagaie_type,
                "profile.pagaie.longueur": pagaie_longueur,
                "profile.pagaie.surface": pagaie_surface,
            }
        }, {upsert: true}, function (e, a) {

            if (e) {
                console.log(e);
                res.sendStatus(500)

            }
            else if (a == 1) {
                res.sendStatus(200)

            }
            else {
                res.sendStatus(401)
            }
        });
    }
    catch (e) {
        console.log(e)
        res.sendStatus(400)
    }


}

function updateEmail(req, res) {
    try {
        U = req.remoteUser
        email = req.body.email
        email = email.replace(/ /g, '')
        if (email.endsWith('@gmail.com')) {

            system.update({_id: U._id}, {"$set": {"profile.email": email}}, function (e, a) {
                if (e) {
                    res.sendStatus(500)

                }
                else if (a == 1) {
                    res.sendStatus(200)
                }
                else {
                    res.sendSatus(401)
                }
            });
        }
        else {
            res.sendStatus(400)
        }
    }
    catch (e) {
        console.log(e)
        res.sendStatus(400)
    }

}

function updatePassword(req, res) {
    try {
        U = req.remoteUser
        password = req.body.password


        system.update({_id: U._id, "profile.password": auth.hash(password.old)},
            {"$set": {"profile.password": auth.hash(password.new)}},
            function (e, a) {
                if (e) {
                    res.sendStatus(500)

                }
                else if (a == 1) {
                    res.sendStatus(200)
                }
                else {
                    res.sendSatus(401)
                }
            }
        )
    }
    catch (e) {
        console.log(e)
        res.sendStatus(400)
    }
}


module.exports = {


    isCoach: isCoach,
    updateSimpleProfileFeature: updateSimpleProfileFeature,
    updateEmail: updateEmail,
    updatePassword: updatePassword,
    // MIDDELWARE
    getMyBasicProfile: getMyBasicProfile,
    getMyProfile: getMyProfile,
    getUserProfileById: getUserProfileById,
    getUserProfileByIdBasic: getUserProfileByIdBasic,
    getMoodToday: getMoodToday,
    addMoodToday: addMoodToday,
    storeWeight: storeWeight,
    storeHeight: storeHeight,
    storeFatPercentage: storeFatPercentage,
    storeHrStanding: storeHrStanding,
    storeHrvTest: storeHrvTest,
    storeFatigueIndex: storeFatigueIndex,
    storePassword: storePassword,
    uploadPhoto: uploadPhoto,
    storeBoatType: storeBoatType,
    storeBoatRecoupe: storeBoatRecoupe,
    storeBoatConstruction: storeBoatConstruction,
    storePagaieType: storePagaieType,
    storePagaieLongueur: storePagaieLongueur,
    storePagaieSurface: storePagaieSurface


}
