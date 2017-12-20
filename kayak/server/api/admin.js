var system = require('./db').collection('system')
var ObjectId = require("mongolian").ObjectId
var auth = require("./authentification")
function checkIsAdmin(session_id, callback) {
    system.findOne({
        "sessions.session_id": session_id,
        "sessions.timeout": {"$gt": new Date().getTime()},
        "role": "admin"
    }, {"_id": 1}, function (err, res) {
        if (err || res == null) {
            callback(false)
        }
        else {
            callback(true)
        }

    });///

}
function listUsers(req, res) {

    var session_id = req.query.session_id || req.cookies.session_id
    var status = req.params.status


    checkIsAdmin(session_id, function (isAdmin) {
        if (isAdmin) {
            system.find({
                "status": status,
                "role": {"$in": ["sportman", "coach"]}
            }, {
                "_id": 1,
                "role": 1,
                "profile": 1,
                "profile.email": 1,
                "profile.name": 1,
                "profile.surname": 1,
                "profile.pictures.profile": 1
            }).toArray(function (err, array) {
                if (err) {
                    res.json({"error": "error retrieving list", "err": err})
                }
                else {
                    array = array.map(function (elem) {
                        elem._id = elem._id.toString('hex')
                        return elem
                    })
                    res.json(array)
                }
            })
        }
        else {
            res.status(401).json({"error": "you are not an admin"})
        }
    })


}


function validateUser(req, res) {
    checkIsAdmin(req.cookies.session_id, function (isAdmin) {
        if (isAdmin) {
            var user = req.params.user_id;
            if (user) {

                user = new ObjectId(user);

                system.update({
                        "_id": user

                    },
                    {"$set": {"status": "verified"}},
                    function (e, a) {
                        res.json({"nb": 1})
                    }
                );
            }
            else {
                res.status(404).json({"error": "no such user"});
            }

        }
        else {
            res.status(401).json({"error": "not an admin"});
        }
    });
}


function deleteUser(req, res) {

    var user = req.params.user_id
    user = new ObjectId(user)
    var session_id = req.cookies.session_id
    checkIsAdmin(session_id, function (is_admin) {

        system.remove({"_id": user}, function (err, obj) {

            if (is_admin) {

                if (obj > 0 && !err) {
                    res.json({"deleted": user.toString()})
                }
                else {
                    res.status(404).json({"error": "user not deleted"})
                }
            }
            else {
                res.status(401).json({"error": "not an admin "})
            }

        });
    })

}


function updateUser(req, res) {
    U = req.remoteUser
    if (U != null && U.role == "admin") {
        var surname = req.body.profile.surname
        var name = req.body.profile.name
        var email = req.body.profile.email
        var password = req.body.profile.password
        var role = req.body.role
        var id = new ObjectId(req.body._id)
        var update = {
            "$set": {
                "profile.surname": surname,
                "profile.name": name,
                "profile.email": email,
                "role": role
            }
        };
        if (password != null) {

            update.$set["profile.password"] = auth.hash(password)
        }

        system.update({"_id": id}, update, function (e, a) {

            if (e || a == 0) {
                console.log(e)
                res.sendStatus(400);
            }
            else {
                res.sendStatus(200);
            }
        });


    }
    else {
        res.sendStatus(401)
    }

}

function apiTokens(req, res) {


    system.find({"role": "admin"}, {"google_api_tokens": 1, "_id": 0}).toArray(function (err, arr) {
        if (err) {
            res.sendStatus(500)
        }
        else {
            res.json(arr[0]);
        }
    });
}
function setApiTokens(req, res) {
    var u = req.remoteUser;
    try{
        var calendar = req.body.google_api_tokens.calendar+""
        var maps = req.body.google_api_tokens.maps+""
        if (u && u.role == "admin") {
            system.update({"role": "admin"}, {"$set" : {"google_api_tokens.calendar" : calendar ,"google_api_tokens.maps" : maps  }},function (err, arr) {
                if (err) {
                    res.sendStatus(500)
                }
                else {
                    res.json(arr[0]);
                }
            });
        }
        else {
            res.sendStatus(401)
        }
    }
    catch(e){
        res.sendStatus(400)
    }


}
module.exports = {
    listUsers: listUsers,
    validateUser: validateUser,
    checkIsAdmin: checkIsAdmin,
    deleteUser: deleteUser,
    updateUser: updateUser,
    apiTokens: apiTokens,
    setApiTokens: setApiTokens,
}