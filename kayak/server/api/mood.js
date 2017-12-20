/**
 * Created by grego on 30/04/2016.
 */
/**
 * Created by grego on 30/04/2016.
 */
var system = require('./db').collection('system')


module.exports = {


    get: {

        getMood: function (req, res) {


            var session_id = req.cookies.session_id
            system.runCommand("aggregate", {
                pipeline: [
                    {
                        "$match": {
                            "sessions.session_id": session_id,
                            "sessions.timeout": {
                                "$gt": new Date().getTime()
                            }
                        }

                    }, {
                        "$project": {
                            "profile.mood": 1,
                            "_id": 0
                        }
                    }, {
                        "$unwind": "$profile.mood"
                    }, {
                        "$match": {
                            "profile.mood.day": new Date().toDateString()
                        }
                    }]
            }, function (err, arr) {


                if (err) {
                    res.status(500).json({"error": "server error"})
                }
                else {
                    if (arr.result && arr.result.length > 0) {

                        res.json(arr.result[0]);


                    }
                    else {
                        res.status(404).json({"error": "no such mood"})

                    }

                }
            })


        }
    }

}





