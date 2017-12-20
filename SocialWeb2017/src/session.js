/**
 * Created by grego on 3/15/2017.
 */
var db = require("./db")
var crypto = require("crypto")
var Session = db.Session
module.exports ={

    createSession : function(google_id,callback ){
        var token = crypto.randomBytes(64).toString('hex');
        var u = new Session({
            google_id : google_id,
            token : token,
            timestamp : new Date().getTime() + 1000 * 3600 * 24 * 30 *6 // 6 months
        })
        sess.save(function(err , sess){
            callback(err,sess);
        });

    },
    deleteSession : function(token){
      Session.find({token : token}).remove().exec();
    }


}