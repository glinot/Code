var weights = null;


function getUserProfile(callback) {

    $.get("/api/profile/me", function (data) {
        callback(data);
    });
}
function refreshProfile() {
    getUserProfile(function (data) {
        if (localStorage.role == "sportman") {
            $("#valeur_poids").text(data.health.weight + " kg");
            $("#valeur_mg").text(data.health.fat_percentage + "%");
            $("#valeur_taille").text(data.health.height + "cm");
            $("#fc_repos").text(data.health.hr_standing + "bpm");
            $("#test_hrv").text(data.health.hrv_test);
            $("#indice_fatigue").text(data.health.fatigue_index);
        }
        else {}
        $("#email").text(data.email);
        $("#password").text(data.password);
        $("#name-profile").text(data.name + " " + data.surname);
        $("#profile_picture").attr("src", data.pictures.profile);
        $("#wall_picture").css("background-image", "url(/img/back/" + (new Date().getDate())%24 + ".jpg)");


    });
}

refreshProfile();


/*Controll the Stats Section*/


function statsHandler(element) {
    this.element = element;

    this.getYear = function (year, onDone, onFail) {
        var getUrl = $(this.element).attr("api-get");
        $.get(getUrl + "/year", {year: year})
            .fail(onFail)
            .done(onDone);
    }.bind(this);
    this.getMonth = function (year, month, onDone, onFail) {
        var getUrl = $(this.element).attr("api-get");
        $.get(getUrl + "/month", {year: year, month: month})
            .fail(onFail)
            .done(onDone);
    }.bind(this);
    this.getWeek = function (year, week, onDone, onFail) {
        var getUrl = $(this.element).attr("api-get");
        $.get(getUrl + "/week", {year: year, week: week})
            .fail(onFail)
            .done(onDone);
    }.bind(this);
}


var states = {year: 0, month: 1, week: 2};
var stateLabel = ["year", "month", "week"];
var months = ["janvier", "fevrier", "mars", "avril", "mai", "juin", "juillet", "aout", "septembre", "octobre", "novembre", "décembre"];


function StatsController(element, apiHandler) {


    this.date = moment().day("monday");

    this.stateSelector = $(element).find("#select-wide");
    this.textState = $(element).find(".text-state");
    this.state = states.week;


    this.updateDateView = function () {

        switch (this.state) {
            case  states.year :
                this.textState.text(this.date.year());
                break;
            case  states.month :
                this.textState.text(months[this.date.month()] + " " + this.date.year());
                break;
            case  states.week :
                this.textState.text(this.date.weekday(0).format("Do MMMM YYYY") + " - " + this.date.weekday(6).format("Do MMMM YYYY"));
                break;


        }
    }.bind(this);


    this.getData = function () {
        switch (this.state) {
            case  states.year :
                apiHandler.getYear(this.date.year(), this.onResult);
                break;
            case  states.month :
                apiHandler.getMonth(this.date.year(), this.date.month() + 1, this.onResult);
                break;
            case  states.week :
                apiHandler.getWeek(this.date.year(), this.date.week(), this.onResult);
                break;

        }


    }.bind(this);
    this.onSateChanged = function () {
        this.state = parseInt(this.stateSelector.find(":selected").attr("option-index"));
        this.getData();
        this.updateDateView();
    }.bind(this);

    this.onPrevious = function () {
        switch (this.state) {
            case  states.year :
                this.date = this.date.subtract(1, "year");
                break;
            case  states.month :

                this.date = this.date.subtract(1, "month");
                break;
            case  states.week :
                this.date = this.date.subtract(1, "week");
                break;

        }
        this.getData();
        this.updateDateView();
    }.bind(this);
    this.onNext = function () {
        switch (this.state) {
            case  states.year :
                this.date = this.date.add(1, "year");
                break;
            case  states.month :

                this.date = this.date.add(1, "month");
                break;
            case  states.week :
                this.date = this.date.add(1, "week");
                break;

        }

        this.getData();
        this.updateDateView();
    }.bind(this);


    // handle results


    this.onResult = function (data) {


        var labels = [];
        switch (this.state) {
            case states.year:
                labels = ["janvier", "fevrier", "mars", "avril", "mai", "juin", "juillet", "aout", "septembre", "octobre", "novembre", "décembre"];
                break;
            case states.month :
                for (var i = 1; i <= data.length; i++)
                    labels.push(i);
                break;
            case states.week :
                labels = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"];
                break;
        }


        // check any data

        var nbFilled, i, len, val;

        nbFilled = false;


        for (i = 0, len = data.length; i < len; i++) {
            val = data[i];
            if (val !== null) {
                nbFilled++;
            }
        }

        if (nbFilled > 0) {
            series = [data];
        }
        else {
            series = [];
        }
        var chart = new Chartist.Line('.stats-panel', {
            labels: labels,
            series: series
        }, {
            fullWidth: true,
            chartPadding: {
                right: 10
            },
            lineSmooth: Chartist.Interpolation.cardinal({
                fillHoles: true,
            }),
            showPoint: nbFilled > 1 ? false : true,
            low: 0,
            height: "400px",
            showArea: true
        });
        chart.on('draw', function (data) {
            if (data.type === 'line' || data.type === 'area') {
                data.element.animate({
                    d: {
                        begin: 2000 * data.index,
                        dur: 2000,
                        from: data.path.clone().scale(1, 0).translate(0, data.chartRect.height()).stringify(),
                        to: data.path.clone().stringify(),
                        easing: Chartist.Svg.Easing.easeOutQuint
                    }
                });
            }
        });

    }.bind(this);


    // init

    this.updateDateView();

}

var handler = new statsHandler(".stats-feature");
var crtl = new StatsController("#stats-control", handler);
$("#stats-control select").change(function () {
    crtl.onSateChanged();

})
$("#stats-control .stats-next").click(function () {
    crtl.onNext();
});

$("#stats-control .stats-previous").click(function () {
    crtl.onPrevious();
});

function randArr(nb) {
    var arr = [];
    for (var i = 0; i < nb; i++) {
        if (Math.random() > 0.8) {
            arr.push(Math.random() * 1000);

        }
        else {
            arr.push(null);
        }
    }
    crtl.onResult(states.year, arr);
}

function randArrT() {
    randArr();
    setTimeout(randArrT, 1000);
}


var api_path = "";


function defaultCheck(val) {

    return {"error": val === "" ? "ne peut pas etre vide " : null};
}
function checkMail(val) {
    return {"error": !/[a-zA-Z0-9\-._]+@gmail.com/.test(val) ? "Adresse gmail valide obligatoire" : null}
}


var check_functions = {
    "password": defaultCheck,
    "email": checkMail

};


var currentDataType;

$(".modify").click(function () {
    currentDataType = $(this).parent().attr("data-type");
    var featureName = $(this).parent().find(".feature-name").text();
    $("#modify-feature").find(".feature-name").text(featureName);
    api_path = $(this).parent().attr("api-post");
    $("#modify-feature").modal("show");

});

$("#submit-modify").click(function () {

    var value = $("#modify-value").val();
   // var check = (check_functions[$(this).attr("data-type")])(value);
    var check = {};
    if (check.error == null) {
        if (api_path != "") {
            console.log(api_path)
            $.post(api_path, {"value": value}, function (data) {
                console.log(data);
            });
            $("#modify-feature").modal("hide");

            refreshProfile();
        }
    }
    else {
        $("#modify-feature").find("#error-message").text(check.error);
    }
});





$(".modify-stats").click(function(){
    currentDataType = $(this).parent().attr("data-type");
    var featureName = $(this).parent().find(".feature-name").text();
    $("#modify-feature").find(".feature-name").text(featureName);
    api_path = $(this).parent().attr("api-post");
    $("#modify-feature").modal("show");
});


//$(".sportman-feature , .coach-feature,  .admin-feature").fadeOut(200).hide();
if (localStorage.role === "sportman") {
    $(".sportman-feature").show();
}
else if (localStorage.role === "coach") {
    $(".coach-feature").show();
    $(".sportman-feature").not(".coach-feature").fadeOut(300);
}
else if (localStorage.role === "admin") {

    $(".sportman-feature , .coach-feature").not(".admin-feature").fadeOut(300);
}


/*----------------------------------------CONTROLER Photo Changer --------------------------------------------------------*/

if (!isSmartphone) {

    $("#profile_picture").click(function () {
        $("#input").click();

    });
}

$("#overlay").click(function () {
    $("#photo_changer_wrapper").fadeOut(1000);
});


function trackFace() {
    $(".cr-image").attr("id", "img-track");
    var img = document.getElementById('img-track');
    var tracker = new tracking.ObjectTracker('face');
    tracking.track(img, tracker);
    tracker.on('track', function (event) {
        event.data.forEach(function (rect) {
            console.log(rect.x + " " + rect.y + " " + rect.width + " " + rect.height);

        });
        if (event.data.length > 0) {
            $uploadCrop.croppie('bind', {
                url: $(img).attr("src"),
                points: [event.data[0].x, event.data[0].y, event.data[0].width + event.data[0].x, event.data[0].height + event.data[0].y]
            });

        }

    });


}


var $uploadCrop;

function readFile(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            deb = e.target.result;
            $uploadCrop.croppie('bind', {
                url: e.target.result
            });
            $('#croppie').css("margin-top", $(window).height() + "px");
            $('#croppie').addClass('ready');
            $(".croppie-wrapper").show();
            $("html,body").scrollTo("#croppie" , function(){
                setTimeout(trackFace,0);
            });

        }

        reader.readAsDataURL(input.files[0]);
    }
    else {
    }
}

$uploadCrop = $('#croppie').croppie({
    viewport: {
        width: 200,
        height: 200,
        type: 'square'
    },
    boundary: {
        width: $("#croppie").width() * 0.5,
        height: $("#croppie").width() * 0.5
    },
    showZoomer: false,
    exif: true
});

$('#input').on('change', function () {
    readFile(this);
});
$('#upload-result').on('click', function (ev) {
    $uploadCrop.croppie('result', {
        type: 'canvas'
    }).then(function (resp) {
        var a = resp.split(',');
        var data = a[1] || "";
        $("#profile_picture").attr("src", resp);
        $.post("/api/profile/pictures/profile", {"data": data}, function (nb) {
            $(".croppie-wrapper").hide();
            $("html,body").scrollTop("body");
            $.get("/api/profile/me", function (profile) {
                $("#lm-user-image").attr("src", profile.pictures.profile);
            });

        });
    });
});

$('#dimiss-result').on('click',function(){
    $(".croppie-wrapper").hide();
    $("html,body").scrollTop("body");
});
$(".croppie-wrapper").hide();



if(tour != null){
    tour.redraw();
}