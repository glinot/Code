`/**
 * Created by grego on 01/05/2016.
 */
var system = require('./db').collection('system')
var moment = require("moment")
var scale = {year: 1, month: 2, week: 3}


function getDateOfWeek(w, y) {
    var d = (1 + (w - 1) * 7); // 1st of January + 7 days for each week
    return new Date(y, 0, d);
}

Array.prototype.average = function () {
    return this.reduce(function (a, b) {
            return a + b;
        }) / this.length;
}


Date.prototype.getNumberOfDayInMonth = function () {
    var d = new Date(this.getFullYear(), this.getMonth() + 1, 0);
    return d.getDate();
}

Date.prototype.getWeekNumber = function () {
    var d = new Date(+this);
    d.setHours(0, 0, 0);
    d.setDate(d.getDate() + 4 - (d.getDay() || 7));
    return Math.ceil((((d - new Date(d.getFullYear(), 0, 1)) / 8.64e7) + 1) / 7);
};


function extractArray(result, name) {
    return result.map(function (elem) {
        return elem["stats"][name];
    });
}
function fetchArrayByMonth(arr) {
    var yearData = new Array(12);

    for (i in arr) {
        var date = arr[i].date;
        if (date) {
            month = new Date(date).getMonth()

            if (yearData[month] == null) {
                yearData[month] = [parseFloat(arr[i].value)];
            }
            else {
                yearData[month].push(parseFloat(arr[i].value));
            }
        }
    }
    return yearData.map(function (item) {

        if (item != null) {
            return item.average();
        }
        else {
            return null;
        }

    });


}
function fetchArrayByDayInWeek(arr) {
    var weekData = new Array(7);

    for (i in arr) {
        var date = arr[i].date;
        if (date) {
            day = new Date(date).getDay()

            if (weekData[day] == null) {
                weekData[day] = [parseFloat(arr[i].value)];
            }
            else {
                weekData[day].push(parseFloat(arr[i].value));
            }
        }
    }
    return weekData.map(function (item) {

        if (item != null) {
            return item.average();
        }
        else {
            return null;
        }

    });


}

function fetchArrayByDayInMonth(arr) {

    if (arr.length > 0) {
        var firstDate = arr[0].date
        var yearData = new Array(firstDate.getNumberOfDayInMonth());

        for (i in arr) {
            var date = arr[i].date;
            if (date) {
                date = new Date(date).getDate()

                if (yearData[date] == null) {
                    yearData[date] = [parseFloat(arr[i].value)];
                }
                else {
                    yearData[date].push(parseFloat(arr[i].value));
                }
            }
        }
        return yearData.map(function (item) {

            if (item != null) {
                return item.average();
            }
            else {
                return null;
            }

        });
    }
    else {

        return [];
    }


}

function getRessourceByMonth(session_id,year, month, name, res) {
    var selectedDate = moment(year+month,"YYYYMM")
    var monthBottom = selectedDate.clone().subtract(1, "day").toDate()
    var monthUp = selectedDate.clone().add(1, "month").toDate()

    var projection = {"_id": 0}
    projection["stats." + name] = 1

    var match = {
        "sessions.session_id":session_id ,
        "sessions.timeout" : {
            "$gt" : new Date().getTime()
        }
    }
    match ={}
    match["stats." + name + ".date"] = {
        "$lt": monthUp,
        "$gt": monthBottom
    }

    var sort = {}
    sort["stats." + name + ".date"] = 1

    var pipeline = [{
        "$project": projection
    }, {"$unwind": "$stats." + name}, {
        "$match": match
    },
        {"$sort": sort}
    ];


    var projection = {}
    //projection[]
    //system.find(query )ste
    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        var data = extractArray(arr.result, name);
        var finalData = fetchArrayByDayInMonth(data)
        res.json(finalData);
    });

}

function getRessourceByYear(session_id,year, name, res) {
    var yearBottom = moment(year, "YYYY").subtract(1,"day").toDate()
    var yearUp = moment(year, "YYYY").add(1,"year").toDate()



    var projection = {"_id": 0}
    projection["stats." + name] = 1

    var match = {
        "sessions.session_id":session_id ,
        "sessions.timeout" : {
            "$gt" : new Date().getTime()
        }
    }
    match ={}
    match["stats." + name + ".date"] = {
        "$lt": yearUp,
        "$gt": yearBottom
    }


    var sort = {}
    sort["stats." + name + ".date"] = 1

    var pipeline = [{
        "$project": projection
    }, {"$unwind": "$stats." + name}, {
        "$match": match
    },
        {"$sort": sort}
    ];


    var projection = {}
    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        var data = extractArray(arr.result, name);
        var finalData = fetchArrayByMonth(data)
        res.json(finalData);
    });

}


function getRessourceByWeek(session_id,year, weeknum, name, res) {
    var weekBottom = moment(year+weeknum,"YYYYWW").subtract(1,"day").toDate()
    var weekUp = moment(year+weeknum,"YYYYWW").add(1 , "week").toDate()


    var projection = {"_id": 0}
    projection["stats." + name] = 1

    var match = {
        "sessions.session_id":session_id ,
        "sessions.timeout" : {
            "$gt" : new Date().getTime()
        }
    }
    match ={}
    match["stats." + name + ".date"] = {
        "$lt": weekUp,
        "$gt": weekBottom
    }


    var sort = {}
    sort["stats." + name + ".date"] = 1

    var pipeline = [{
        "$project": projection
    }, {"$unwind": "$stats." + name}, {
        "$match": match
    },
        {"$sort": sort}
    ];


    var projection = {}
    //projection[]
    //system.find(query )ste
    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        var data = extractArray(arr.result, name);
        var finalData = fetchArrayByDayInWeek(data)
        res.json(finalData);
    });

}


var getRessource = function (sc, name, params, res) {

    if (sc == scale.year) {
        getRessourceByYear(params.year, name, res)
    }
    else if (sc == scale.month) {
        getRessourceByMonth(params.year, params.month, name, res);
    }
    else if (sc == scale.week) {
        getRessourceByWeek(params.year, params.week, name, res);
    }

}
module.exports = {

    storeTimeFeature: function (feature_name, path, value, session_id, res) {


        var user = {
            "sessions.session_id": session_id,
            "sessions.timeout": {"$gt": new Date().getTime()}
        }
        var update = {}
        update[path] = value
        system.update(
            user,
            {"$set": update},
            function (err, arr) {
                if (!err) {
                    var toPush = {}
                    toPush["stats." + feature_name] = {"value": value, "date": new Date()}
                    system.update(user, {"$push": toPush}, function (err, arr) {
                        if (arr == 0) {
                            res.status(403).json({"error": "error "})
                        }
                        else {
                            res.json({"success": "true"});
                        }
                    })
                }
                else {
                    res.status(500).json({"error": "error updating "})
                }
            }
        )
    },


    getFeature: function (req, res) {
        var wide = req.params.wide
        var wide = (wide == "year") ? scale.year : wide == "month" ? scale.month : scale.week
        var feature = req.params.feature


        switch (wide) {
            case scale.year:

                getRessourceByYear(req.cookies.session_id,req.query.year, feature, res)
                break;
            case scale.month:
                getRessourceByMonth(req.cookies.session_id,req.query.year, req.query.month, feature, res)
                break;
            case scale.week:
                getRessourceByWeek(req.cookies.session_id,req.query.year, req.query.week, feature, res)
                break;
        }
    }

}
`



