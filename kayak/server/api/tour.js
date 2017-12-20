/**
 * Created by grego on 22/05/2016.
 */
// TOUR functions


var system = require('./db').collection('system')


module.exports = {
    resetTour : function(req , res){

        var session_id = req.cookies.session_id
        system.update(
            {
                "sessions.session_id" : session_id,
                "sessions.timeout" : {
                    "$gt" : new Date().getTime()
                }
            }
            ,{
                "$set" : {
                    "tour_is_done" : false
                }
            },
            function(err,nb_updated){
                if(err){
                    res.sendStatus(500);
                }
                else{
                    if(nb_updated > 0){
                        res.sendStatus(200);
                    }
                    else{
                        res.sendStatus(401);
                    }
                }
            }
        )


    },
    validateTour : function(req , res){

        var session_id = req.cookies.session_id
        system.update(
            {
                "sessions.session_id" : session_id,
                "sessions.timeout" : {
                    "$gt" : new Date().getTime()
                }
            }
            ,{
                "$set" : {
                    "tour_is_done" : true
                }
            },
            function(err,nb_updated){
                if(err){
                    res.sendStatus(500);
                }
                else{
                    if(nb_updated > 0){
                        res.sendStatus(200);
                    }
                    else{
                        res.sendStatus(401);
                    }
                }
            }
        )


    },
    isTourValidated : function(req , res){
        system.find({
            "sessions.session_id" : req.cookies.session_id,
            "sessions.timeout" : {
                "$gt" : new Date().getTime()
            }
        },{"tour_is_done":1, "_id": 0}) .toArray(function(err , arr){
            if(err){
                res.sendStatus(500)
            }
            else{
                if(arr.length > 0 ){
                    res.json(arr[0])
                }
                else{
                    res.sendStatus(401)
                }
            }
        })
    }
}