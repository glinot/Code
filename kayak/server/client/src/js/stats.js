function randArr(nb) {
    var arr = [];
    for (var i = 0; i < nb; i++) {
        if (Math.random() > 0) {
            arr.push(Math.random() * 1000);
        }
        else {
            arr.push(null);
        }
    }
    return arr;
}
/* Section training */
$(".custom-checkbox").click(function () {
    $(this).toggleClass("active").fadeOut(100).toggleClass("disabled").fadeIn(100);
    $(this).trigger("changed", [$(this).hasClass("active")]);
});
// STATS controller
var stats_wide = {"year": 0, "month": 1, "week": 2}
var currentState = stats_wide.week; // default state
var currentDate = moment();// default date : current week
// request stat for one feature
var api_wide_label = ["year", "month", "week"]
function requestFeature(path, onDone, onFail) {
    $.get(path + "/" + api_wide_label[currentState], {
        "year": currentDate.year(),
        "month": currentDate.month(),
        "weeknum": currentDate.isoWeek()
    }).done(onDone).fail(onFail);
}
// update selector view$
function updateView() {
    switch (currentState) {
        case  stats_wide.year :
            $(".text-state").text(currentDate.clone().year());
            break;
        case  stats_wide.month :
            $(".text-state").text(currentDate.clone().date(1).format("MMMM YYYY"));
            break;
        case  stats_wide.week :
            console.log(currentDate.clone().toDate().toString());
            $(".text-state").text(currentDate.clone().weekday(0).format("DD MMMM YYYY") + " - " + currentDate.clone().weekday(6).format("DD MMMM YYYY"));
            break;
    }
}
// default
updateView();
// event next or previous
function changeDate(factor) {

    switch (currentState) {
        case  stats_wide.year :
            currentDate.add(factor, "year");
            break;
        case  stats_wide.month :
            if (factor == 1) {

                currentDate.add(factor, "month").add(1, "day");
            }
            else {
                currentDate.startOf("month").subtract(1, "day").startOf("month");
            }
            break;
        case  stats_wide.week :
            currentDate.add(factor, "week");
            break;
    }
}
// function change state
function changeState(state) {

    currentState = state;
    updateView();
}
// handle change
$(".stats-previous").click(function () {
    changeDate(-1);
    updateView();
    $(this).parent().trigger("changed")
});
$(".stats-next").click(function () {
    changeDate(1);
    updateView();
    $(this).parent().trigger("changed")
});
$(".wrapper-stats").click(function () {
    $(".wrapper-stats").removeClass("active");
    $(this).addClass("active");
    changeState(parseInt($(this).attr("state-index")));
    $(".selector-wide").trigger("changed");

});

// listen on changed date
$(".selector-wide").on("changed", function () {

    console.warn(currentDate.toDate().toString());
    updateStats();
});


var months = ["jan", "fev", "mar", "avr", "mai", "jui", "juil", "aout", "sept", "oct", "nov", "déc"];
function genList(nb) {
    var arr = [];
    for (var i = 1; i <= nb; i++) {
        arr.push(i);
    }
    return arr;
}
function buildLabels() {
    switch (currentState) {
        case  stats_wide.year :
            return months;
            break;
        case  stats_wide.month :
            return genList(currentDate.clone().endOf("month").date());
            break;
        case  stats_wide.week :
            return ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"];
            break;
    }
}


// functions for creating stats


// FUNCTION FOR Curve stats
var trainingsMethodsData = null;
var curves_data = {};
var disabled = [];
function normalize(datas) {
    var max = datas.reduce(function (max, current) {
        if (max < current.length) {
            return current.length;
        }
        else {
            return max
        }
    }, -1);
    var newDatas = []
    for (i in datas) {
        newDatas[i] = []
        for (var j = 0; j < max; j++) {
            newDatas[i].push(datas[i][j]);
        }
    }
    return datas;
}

function getMax(st) {
    if(st == null){
        st = currentState
    }
    switch (st) {
        case  stats_wide.year :
            if (currentDate.year() == moment().year()) {
                return moment().month();
            }
            else {
                return 11;
            }
            break;
        case  stats_wide.month :
            if (currentDate.month() == moment().month() && currentDate.year() == moment().year()) {
                return moment().date() -1;
            }
            else {
                return currentDate.clone().endOf("month").date() - 1;
            }
            break;
        case  stats_wide.week :
            if (currentDate.day() && moment().day() && currentDate.year() == moment().year()) {
                return currentDate.day() - 1
            }
            else {
                return 7;
            }
            break;
    }
}
function updateTrainingsMethodsView(elem) {
    var labels = buildLabels();
    var datas = []
    var methods = []
    var max = getMax();
    for (i in curves_data[elem]) {
        if (!disabled[curves_data[elem][i].name]) {
            var data = curves_data[elem][i].data || []
            for (var j = 0; j < labels.length ; j++) {

                if(j <= max){
                    if (data[j] == null) {
                        data[j] = 0;
                    }

                }
                else {
                    data[j] = null;
                }

            }
            datas.push(data);
        }
        else {
            datas.push([null]);
        }
        methods.push(curves_data[elem][i].name);
    }
    var chart = new Chartist.Line(elem + ' .training-time', {
        labels: labels,
        series: datas
    }, {
        fullWidth: true,
        chartPadding: {
            right: 10,
            left: 10
        },
        lineSmooth: Chartist.Interpolation.step({
            fillHoles: true,
            postpone: true
        }),
        low: 0,
        height: "400px",
        lineSmooth: false
    });


}
function updateSelectors(elem) {
    $(elem).find(".curve-selector tbody tr").not(".model").remove();
    var tbody = $(elem).find(".curve-selector tbody");
    var nb = 0;
    for (sel in curves_data[elem]) {
        var c = $(elem).find(".curve-selector .model").clone(true, true);
        c.find(".selector-name").text(curves_data[elem][sel].name);
        // c.find(".selector-name").text(sel);
        c.removeClass("model");
        c.find(".color-line").addClass("color-" + (++nb));
        tbody.append(c);
    }
}
$(".curve-selector .custom-checkbox").on('changed', function (event, isOK) {
    disabled[$(this).parent().parent().find(".selector-name").text()] = !isOK;
    updateTrainingsMethodsView("#" + $(this).parent().parent().parent().parent().parent().parent().attr("id") || "");
});
function getCurves(api, elem) {
    disabled = [];
    requestFeature(api, function (dataG) {
        curves_data[elem] = [];
        for (i in dataG) {
            curves_data[elem].push({"name": i, data: dataG[i]})
        }
        updateSelectors(elem);
        updateTrainingsMethodsView(elem);
    });
}


function getTrainingMethodCurves() {
    getCurves("/api/stats/trainings/methods", "#training-method-time");
}
function getTrainingProcedeCurves() {
    getCurves("/api/stats/trainings/procede", "#training-procede-time");
}


function getMethodsPercentages() {
    requestFeature("/api/stats/trainings/methods/per", function (dataG) {
            var data = dataG.reduce(function (final, elem) {
                final.labels.push(elem.globaltype)
                final.series.push(elem.percentage)
                return final;
            }, {
                labels: [],
                series: []
            });
            var options = {
                labelInterpolationFnc: function (value) {
                    return value[0]
                },
                height: "600px"
            };
            var responsiveOptions = [
                ['screen and (min-width: 640px)', {
                    chartPadding: 30,
                    labelOffset: 100,
                    labelDirection: 'explode',
                    labelInterpolationFnc: function (value) {
                        return value;
                    }
                }],
                ['screen and (min-width: 1024px)', {
                    labelOffset: 80,
                    chartPadding: 20
                }]
            ];
            new Chartist.Pie('.training-methods-stats', data, options, responsiveOptions);
        }
    );
}
function getProcedePercentages() {
    requestFeature("/api/stats/trainings/procede/per", function (dataG) {
            var data = dataG.reduce(function (final, elem) {
                final.labels.push(elem.globaltype)
                final.series.push(elem.percentage)
                return final;
            }, {
                labels: [],
                series: []
            });
            var options = {
                labelInterpolationFnc: function (value) {
                    return value[0]
                },
                height: "600px"
            };
            var responsiveOptions = [
                ['screen and (min-width: 640px)', {
                    chartPadding: 30,
                    labelOffset: 100,
                    labelDirection: 'explode',
                    labelInterpolationFnc: function (value) {
                        return value;
                    }
                }],
                ['screen and (min-width: 1024px)', {
                    labelOffset: 80,
                    chartPadding: 20
                }]
            ];
            new Chartist.Pie('.training-procede-stats', data, options, responsiveOptions);
        }
    );
}

// HEALTH


var trad_sante = {
    "weight": "Poids",
    "height": "Taille",
    "fat_percentage": "Pourcentage de matière grasse",
    "hr_standing": "Fréquence cardiaque",
    "hrv_test": "Test HRV",
    "fatigue_index": "Fatigue"
};
var health_ressources = ["weight", "height", "fat_percentage", "hr_standing", "hrv_test"];//, "fatigue_index"];
function getHealthCurves() {
    $(".health-curve").not(".model").remove();
    health_ressources.forEach(function (item) {
        requestFeature("/api/stats/" + item, function (dataG) {

            var $stat = $("#model-health").clone(true, true).removeClass("model");
            $stat.find(".health-feature-label").text(trad_sante[item]);
            $stat.find(".health-feature-stats").attr("id", item);
            var data = dataG.map(function (i) {
                return i == null ? 0 : i;
            });
            $("#health-container").append($stat);
            var chart = new Chartist.Line("#" + item, {
                labels: buildLabels(),
                series: [data]
            }, {
                fullWidth: true,
                chartPadding: {
                    right: 10,
                    left: 10
                },
                lineSmooth: Chartist.Interpolation.step({
                    fillHoles: true,
                    postpone: true
                }),
                low: 0,
                height: "400px",
                lineSmooth: false
            });


        });
    });
}

var stats_func = [getMethodsPercentages, getProcedePercentages, getTrainingMethodCurves, getTrainingProcedeCurves, getHealthCurves];
function updateStats() {
    for (i in stats_func) {
        stats_func[i]();
    }
}
updateStats();


//---------------------------- fill overview----------------------------
Math.roundAt = function (num, dec) {
    return Math.round(num * dec) / dec;
}
var assiduite_class = ["progress-bar-danger", "progress-bar-warning", "progress-bar-success"];
function fillOverviewBox(elem, data) {
    for (key in data) {
        data[key] = Math.roundAt(data[key], 10); // tout arrondir au dixième
    }
    $(elem).find(".overview-training-number").text(data.nb_trainings);
    $(elem).find(".overview-training-duree").text(data.duree + " min");
    $(elem).find(".overview-training-distance").text(data.distance);
    var image_index = Math.round(data.ressenti);
    image_index = image_index == 0 ? 1 : image_index
    image_index = image_index > 5 ? 5 : image_index;
    $(elem).find(".overview-training-ressenti img").attr("src", "./img/smiley" + image_index + ".png");
    $(elem).find(".overview-training-assiduite > div").css("width", data.assiduite * 100 + "%");
    $(elem).find(".overview-training-assiduite > div").addClass(assiduite_class[Math.ceil(data.assiduite * 3) - 1])
}
var today = moment();
$.get("/api/stats/trainings/overview/week", {year: today.year(), weeknum: today.isoWeek()}, function (data) {
    fillOverviewBox(".week-overview", data);
});
$.get("/api/stats/trainings/overview/month", {year: today.year(), month: today.month()}, function (data) {
    fillOverviewBox(".month-overview", data);
});
$.get("/api/stats/trainings/overview/year", {year: today.year()}, function (data) {
    fillOverviewBox(".year-overview", data);
});
function fillGlobalTraining(elem, data, state) {
    var labels = [];
    switch (state) {
        case  stats_wide.year :
            labels = months;
            break;
        case  stats_wide.month :
            labels = genList(currentDate.clone().endOf("month").date());
            break;
        case  stats_wide.week :
            labels = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"];
            break;
    }

    var max = getMax(state);
    for (i in data) {
        if(i > max){
            data[i] = null;
        }
    }
    var chart = new Chartist.Line(elem, {
        labels: labels,
        series: [
            data
        ]
    }, {
        fullWidth: true,
        chartPadding: {
            right: 10
        },
        lineSmooth: Chartist.Interpolation.cardinal({
            fillHoles: true,
        }),
        low: 0
    });
}
$.get("/api/stats/trainings/global/week", {year: today.year(), weeknum: today.isoWeek()}, function (data) {
    fillGlobalTraining(".week-stats", data, stats_wide.week);
});
$.get("/api/stats/trainings/global/month", {year: today.year(), month: today.month()}, function (data) {
    fillGlobalTraining(".month-stats", data, stats_wide.month);
});
$.get("/api/stats/trainings/global/year", {year: today.year()}, function (data) {
    fillGlobalTraining(".year-stats", data, stats_wide.year);
});


currentDate = moment();