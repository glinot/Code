var system = require('./db').collection('system')
var validator = require('validator')

var auth = require('./authentification')
var admin = require("./admin")

var NB_HOURS_SESSION = 100;

var login = function (req, res, next) {


    var login = (req.query.login || "") + "";
    var pass = (req.query.password || "") + "";
    var hash = auth.hash(pass + "")
    var session_id = auth.createSessionId()
    var timeout = new Date().getTime() + 3600 * 1000 * NB_HOURS_SESSION;

    system.findAndModify({
        query: {
            "profile.email": login,
            "profile.password": hash,
            "status": "verified"
        },
        update: {
            "$push": {
                "sessions": {
                    session_id: session_id,
                    timeout: timeout
                }
            }
        },
        new: false
    }, function (err, user) {
        if (err == null && user != null) {
            res.json({
                success: true,
                session_id: session_id,
                timeout: timeout,
                name: user.profile.name,
                surname: user.profile.surname,
                email: user.profile.email,
                role: user.role
            })
        } else {
            res.status(401).json({
                msg: "Bad credentials"
            })
        }

    })


};


var isSessionValid = function (req, res) {

    var session_id = req.cookies.session_id;
    if (session_id == null) {
        res.json({"valid": false})
    }
    else {
        system.find({

            "sessions.session_id": session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            },
            "status": "verified"
        }).toArray(function (err, user) {
            if (err) {
                res.status(500).json({"err": "server error"})
            }
            else {
                if (user == null || user.length == 0) {
                    res.json({"valid": false})
                }
                else {
                    res.json({"valid": true})
                }
            }
        })
    }

}

function onValidSession(session_id, callback) {
    if (session_id == null) {
        callback(false);

    }
    else {
        system.find({

            "sessions.session_id": session_id,
            "sessions.timeout": {
                "$gt": new Date().getTime()
            },
            "status": "verified"
        }).toArray(function (err, user) {
            if (err) {
                callback(false);
            }
            else {
                if (user == null || user.length == 0) {
                    callback(false);
                }
                else {

                    callback(true);
                }
            }
        })
    }
}

function registerUser(req, res) {

    var name = req.body.name || ""
    var surname = req.body.surname || ""
    var password = req.body.password || ""
    var role = req.body.role == "coach" ? "coach" : "sportman"
    var email = (req.body.email || "").replace(/\s/g, '')// check gmail


    if (email.endsWith("@gmail.com")) {
        system.find({"profile.email": email}).toArray(function (err, users) {

            if (err) {
                console.log(err)
                res.status(500).json({"error": "server error"});

            }
            else {
                if (Array.isArray(users) && users.length > 0) {
                    res.status(401).json({"error": "user already exists"})
                }
                else {

                    var user = {
                        "status": (req.remoteUser || {}).role == "admin" ? "verified" : "standby",
                        "tour_is_done": false,
                        "role": role,
                        "profile": {
                            "email": email,
                            "password": require("./authentification").hash(password),
                            "name": name,
                            "surname": surname,

                            "pictures": {
                                "profile": "/img/default_profile.png",
                                "background": "/img/default_background.png"
                            },


                        },
                        "sessions": [],
                        "trainings": []
                    }
                    if (role == "sportman") {
                        user.profile.health = {
                            "height": 0,
                            "fat_percentage": 0,
                            "weight": 0,
                            "hrv_test": 0

                        }
                        user.profile.mood = [];
                        user.profile.coach = {
                            club: "",
                            eqf: ""
                        };
                        user.stats = {};
                    }

                    try {
                        system.insert(user);
                        res.json({"success": "ok"})
                    }
                    catch (e) {

                        res.sendStatus(500);
                    }

                }

            }


        });


    }
    else {
        res.status(400).json({"error": "email should be a gmail one "})
    }


}


module.exports = {
    login: login,
    isSessionValid: isSessionValid,
    onValidSession: onValidSession,
    registerUser: registerUser
}
