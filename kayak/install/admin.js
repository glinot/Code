var Mongolian = require("mongolian")
var crypto = require("crypto")
// Create a server instance with default host and port
var server = new Mongolian("127.0.0.1:27017")

// Get database
var db = server.db("kanotDB")


var system = db.collection("system")

var nb_of_hash = 40000

function nbTimeHash(word){
    for(var i = 0 ; i  < nb_of_hash ; i++ )
        word = hash(word);
    return word
}
function hash(word) {
    return crypto.createHash('sha256').update(word, 'utf-8').digest('hex');
}

var password = nbTimeHash("123456")


var admin = {
    "status" : "verified",
    "tour_is_done" : true,
    "role" : "admin",
    "profile" : {
        "email" : "admin",
        "password" : password,
        "name" : "Admin",
        "surname" : "",
        "pictures" : {
            "profile" : "/img/default_profile.png",
            "background" : "/img/default_background.png"
        }
    },
    "sessions" : [],
    "google_api_tokens" : {
        "calendar" : "",
        "maps" : ""
    }
}
system.insert(admin)
