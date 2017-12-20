/**
 * Created by grego on 01/05/2016.
 */
var system = require('./db').collection('system')
var ObjectId = require("mongolian").ObjectId
var moment = require("moment")
var scale = {year: 1, month: 2, week: 3}


function getDateOfWeek(w, y) {
    var d = (1 + (w - 1) * 7); // 1st of January + 7 days for each week
    return new Date(y, 0, d);
}

Array.prototype.average = function () {
    return (this || []).reduce(function (a, b) {
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

function getRessourceByMonth(session_id, year, month, name, res) {
    var selectedDate = moment(year + month, "YYYYMM")
    var monthBottom = selectedDate.clone().subtract(1, "day").toDate()
    var monthUp = selectedDate.clone().add(1, "month").toDate()

    var projection = {"_id": 0, "sessions": 1}
    projection["stats." + name] = 1

    var match = {
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }
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
function getRessourceByYear(session_id, year, name, res) {
    var yearBottom = moment(year, "YYYY").subtract(1, "day").toDate()
    var yearUp = moment(year, "YYYY").add(1, "year").toDate()


    var projection = {"_id": 0, "sessions": 1}
    projection["stats." + name] = 1

    var match = {
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }
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
function getRessourceByWeek(session_id, year, weeknum, name, res) {
    var weekBottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
    var weekUp = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    var projection = {"_id": 0, "sessions": 1}
    projection["stats." + name] = 1
    var match = {
        "sessions.session_id": session_id,
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }
    match = {}
    match["stats." + name + ".date"] = {
        "$lt": weekUp,
        "$gt": weekBottom
    }


    var sort = {}
    sort["stats." + name + ".date"] = 1

    var pipeline = [
        {
            "$project": projection
        },
        {
            "$unwind": "$stats." + name

        },


        {
            "$match": {
                "sessions.session_id": session_id,
                "sessions.timeout": {
                    "$gt": new Date().getTime()
                }
            }
        },


        {"$match": match},
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

function getTrainingsHours(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {"type": "$trainings.globaltype"}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var resArr = arr.result || []
            var final = resArr.reduce(function (total, elem) {

                if (total[elem._id.type] == null) {
                    total[elem._id.type] = []
                }
                total[elem._id.type][elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, {})
            res.json(final)

        }
    });
}

function getTrainingsMethodsPercentage(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: {"type": "$trainings.globaltype"},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            var total = resArr.reduce(function (a, b) {
                return a + b.duree
            }, 0);
            res.json(resArr.map(function (elem) {

                return {"percentage": elem.duree / total, globaltype: elem._id.type}
            }));

        }


    });
}

function getTrainingsHoursProcede(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    var time_group_offset = 0; // offet ex month begins by 1 instead of 0var time_group_offset = 0; // offet ex month begins by 1 instead of 0
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {"type": "$trainings.procede"}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            res.json(resArr.reduce(function (total, elem) {

                if (total[elem._id.type] == null) {
                    total[elem._id.type] = []
                }
                total[elem._id.type][elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, {}));

        }
    });
}

function getTrainingsProcedePercentage(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: {"type": "$trainings.procede"},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            var total = resArr.reduce(function (a, b) {
                return a + b.duree
            }, 0);
            res.json(resArr.map(function (elem) {

                return {"percentage": elem.duree / total, globaltype: elem._id.type}
            }));

        }


    });
}

function getTrainingsGlobal(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var resArr = arr.result || []
            var final = resArr.reduce(function (total, elem) {


                total[elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, [])
            res.json(final)

        }
    });
}

function getOverview(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "sessions.session_id": req.cookies.session_id,
                    "sessions.timeout": {
                        "$gt": new Date().getTime()
                    }
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    "_id": "$trainings.filled",
                    "nbOf": {"$sum": 1},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}},
                    "distance": {"$sum": "$trainings.distance"},
                    "ressenti": {"$avg": "$trainings.ressenti"}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var fres = {
                "assiduite": 0,
                "distance": 0,
                "duree": 0,
                "ressenti": 0,
                "nb_trainings": 0
            }
            var filled = 0
            var ares = arr.result || []
            for (i in ares) {
                filled += ares[i]._id ? ares[i].nbOf : 0 || 0
                fres.nb_trainings += ares[i].nbOf || 0
                fres.distance += ares[i].distance || 0
                fres.duree += ares[i].duree || 0
                fres.ressenti += ares[i].ressenti || 0

            }
            fres.assiduite = filled / fres.nb_trainings

            res.json(fres);

        }


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


// COACH SIDE


function getRessourceByMonthBySportman(sportman_id, year, month, name, res) {
    var selectedDate = moment(year + month, "YYYYMM")
    var monthBottom = selectedDate.clone().subtract(1, "day").toDate()
    var monthUp = selectedDate.clone().add(1, "month").toDate()

    var projection = {"_id": 1}
    projection["stats." + name] = 1

    var match = {
        _id: new ObjectId(sportman_id)
    }
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


    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        var data = extractArray(arr.result, name);
        var finalData = fetchArrayByDayInMonth(data)
        res.json(finalData);
    });

}
function getRessourceByYearBySportman(sportman_id, year, name, res) {
    var yearBottom = moment(year, "YYYY").subtract(1, "day").toDate()
    var yearUp = moment(year, "YYYY").add(1, "year").toDate()


    var projection = {"_id": 1}
    projection["stats." + name] = 1

    var match = {
        "_id": new ObjectId(sportman_id)
    }
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
function getRessourceByWeekBySportman(sportman_id, year, weeknum, name, res) {
    var weekBottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
    var weekUp = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    var projection = {"_id": 1}
    projection["stats." + name] = 1
    var match = {}
    match["stats." + name + ".date"] = {
        "$lt": weekUp,
        "$gt": weekBottom
    }

    var sort = {}
    sort["stats." + name + ".date"] = 1
    var pipeline = [

        {
            "$project": projection
        },
        {
            "$unwind": "$stats." + name

        },
        {"$match": {"_id": new ObjectId(sportman_id)}},
        {"$match": match},
        {"$sort": sort}
    ];


    system.runCommand("aggregate", {
        pipeline: pipeline
    }, function (err, arr) {

        if (err) {
            res.sendStatus(500);
        }
        else {
            var data = extractArray(arr.result, name);
            var finalData = fetchArrayByDayInWeek(data)
            res.json(finalData);
        }
    });

}

function getTrainingsHoursBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {"type": "$trainings.globaltype"}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var resArr = arr.result || []
            var final = resArr.reduce(function (total, elem) {

                if (total[elem._id.type] == null) {
                    total[elem._id.type] = []
                }
                total[elem._id.type][elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, {})
            res.json(final)

        }
    });
}
function getTrainingsMethodsPercentageBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: {"type": "$trainings.globaltype"},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            var total = resArr.reduce(function (a, b) {
                return a + b.duree
            }, 0);
            res.json(resArr.map(function (elem) {

                return {"percentage": elem.duree / total, globaltype: elem._id.type}
            }));

        }


    });
}
function getTrainingsHoursProcedeBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    var time_group_offset = 0; // offet ex month begins by 1 instead of 0var time_group_offset = 0; // offet ex month begins by 1 instead of 0
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {"type": "$trainings.procede"}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            res.json(resArr.reduce(function (total, elem) {

                if (total[elem._id.type] == null) {
                    total[elem._id.type] = []
                }
                total[elem._id.type][elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, {}));

        }
    });
}

function getTrainingsProcedePercentageBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: {"type": "$trainings.procede"},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {
            var resArr = arr.result || []
            var total = resArr.reduce(function (a, b) {
                return a + b.duree
            }, 0);
            res.json(resArr.map(function (elem) {

                return {"percentage": elem.duree / total, globaltype: elem._id.type}
            }));

        }


    });
}
function getTrainingsGlobalBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    var aggr_selector = "$dayOfWeek"
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
        aggr_selector = "$dayOfWeek"
        time_group_offset = 0;
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
        aggr_selector = "$dayOfMonth"
        time_group_offset = 1;
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
        aggr_selector = "$month"
        time_group_offset = 1;
    }

    var _id = {}
    var select = {}
    select[aggr_selector] = "$trainings.date"
    _id["time_group"] = select

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    _id: _id,
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var resArr = arr.result || []
            var final = resArr.reduce(function (total, elem) {


                total[elem._id.time_group - time_group_offset] = elem.duree
                return total
            }, [])
            res.json(final)

        }
    });
}
function getOverviewBySportman(req, res) {
    var wide = req.params.wide || "week"
    var up, bottom;
    var year = req.query.year || new Date().getFullYear()
    if (wide === "week") {
        var weeknum = req.query.weeknum || 0
        bottom = moment(year + weeknum, "YYYYWW").subtract(1, "day").toDate()
        up = moment(year + weeknum, "YYYYWW").add(1, "week").toDate()
    }
    else if (wide === "month") {
        var month = req.query.month
        bottom = moment(year + month, "YYYYMM").subtract(1, "day").toDate()
        up = moment(year + month, "YYYYMM").add(1, "month").toDate()
    }
    else if (wide === "year") {
        up = moment(year, "YYYY").add(1, "year").toDate()
        bottom = moment(year, "YYYY").subtract(1, "day").toDate()
    }

    system.runCommand("aggregate", {
        pipeline: [
            {
                "$match": {
                    "_id": new ObjectId(req.params.sportman_id)
                }
            },
            {"$unwind": "$trainings"},
            {
                "$match": {

                    "trainings.date": {"$gt": bottom, "$lt": up}
                }
            },
            {
                "$group": {
                    "_id": "$trainings.filled",
                    "nbOf": {"$sum": 1},
                    "duree": {"$sum": {"$subtract": ["$trainings.heure_fin", "$trainings.heure_deb"]}},
                    "distance": {"$sum": "$trainings.distance"},
                    "ressenti": {"$avg": "$trainings.ressenti"}
                }
            }
        ]
    }, function (err, arr) {
        if (err) {
            res.status(500).json({"error": "error server "})
        }
        else {

            var fres = {
                "assiduite": 0,
                "distance": 0,
                "duree": 0,
                "ressenti": 0,
                "nb_trainings": 0
            }
            var filled = 0
            var ares = arr.result || []
            for (i in ares) {
                filled += ares[i]._id ? ares[i].nbOf : 0 || 0
                fres.nb_trainings += ares[i].nbOf || 0
                fres.distance += ares[i].distance || 0
                fres.duree += ares[i].duree || 0
                fres.ressenti += ares[i].ressenti || 0

            }
            fres.assiduite = filled / fres.nb_trainings

            res.json(fres);

        }


    });
}
function getMyYearsOfTrainings(req, res) {
    try {


        system.runCommand('aggregate', {
            pipeline: [
                {"$match": {"_id": req.remoteUser._id}},
                {"$unwind": "$trainings"},
                {"$group": {"_id": {"$year": "$trainings.date"}}},
                {"$sort": {"_id": -1}}
            ]
        }, function (err, arr) {
            if (err) {
                res.sendStatus(500)
            }
            else {
                res.json(
                    {
                        years: arr.result.map(function (elem) {
                            return elem._id
                        })
                    }
                )
            }
        })


    }
    catch (e) {
        res.sendStatus(400)
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

                getRessourceByYear(req.cookies.session_id, req.query.year, feature, res)
                break;
            case scale.month:
                getRessourceByMonth(req.cookies.session_id, req.query.year, req.query.month, feature, res)
                break;
            case scale.week:
                getRessourceByWeek(req.cookies.session_id, req.query.year, req.query.week, feature, res)
                break;
        }
    },
    getTrainingsHours: getTrainingsHours,
    getTrainingsMethodsPercentage: getTrainingsMethodsPercentage,
    getTrainingsHoursProcede: getTrainingsHoursProcede,
    getTrainingsProcedePercentage: getTrainingsProcedePercentage,
    getOverview: getOverview,
    getTrainingsGlobal: getTrainingsGlobal,
    // COACH access
    getFeatureBySportman: function (req, res) {
        var wide = req.params.wide
        var wide = (wide == "year") ? scale.year : wide == "month" ? scale.month : scale.week
        var feature = req.params.feature

        switch (wide) {
            case scale.year:

                getRessourceByYearBySportman(req.params.sportman_id, req.query.year, feature, res)
                break;
            case scale.month:
                getRessourceByMonthBySportman(req.params.sportman_id, req.query.year, req.query.month, feature, res)
                break;
            case scale.week:
                getRessourceByWeekBySportman(req.params.sportman_id, req.query.year, req.query.week, feature, res)
                break;
        }
    },
    getTrainingsGlobalBySportman: getTrainingsGlobalBySportman,
    getTrainingsHoursBySportman: getTrainingsHoursBySportman,
    getTrainingsMethodsPercentageBySportman: getTrainingsMethodsPercentageBySportman,
    getTrainingsHoursProcedeBySportman: getTrainingsHoursProcedeBySportman,
    getTrainingsProcedePercentageBySportman: getTrainingsProcedePercentageBySportman,
    getOverviewBySportman: getOverviewBySportman,
    getTrainingsGlobalBySportman: getTrainingsGlobalBySportman,
    getTrainingsGlobalBySportman: getTrainingsGlobalBySportman,
    getMyYearsOfTrainings: getMyYearsOfTrainings

}