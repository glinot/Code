function calendarOK(email, img) {
    $.notify({
        // options
        title: "<h4 class='text-capitalize'>Succès</h4>",
        message: "<img class='img-circle img-notify'  src='" + img + "'/> Le calendrier de " + email + " à été mis à jour"
    }, {
        // settings
        type: 'success',
        animate: {
            enter: 'animated fadeInDown',
            exit: 'animated fadeOutUp'
        },
        delay: 1000,
        timer: 1000
    });
}

function errorEmailNoBinded(email, img) {
    $.notify({
        // options
        title: "<h4 class='text-capitalize'>Erreur</h4>",
        message: "<img class='img-circle img-notify'  src='" + img + "'/> " + email + " n'est pas partagée avec vous"
    }, {
        // settings
        type: 'danger',
        animate: {
            enter: 'animated fadeInDown',
            exit: 'animated fadeOutUp'
        },
        delay: 1000,
        timer: 1000
    });
}

$('#button_validate').click(function () {
    $('#title_return').text($('#title').val());
    $("#date_return").text($('#get_date').val());
    $("#horaire_deb_return").text($('#heure_deb').val());
    $("#horaire_fin_return").text($('#heure_fin').val());
    $("#objectives_return").text($("#objectives").val());
    $("#procede_return").text(traduction[$("#procede-entrainement").val()]);
    $("#milieu_return").text(traduction[$("#milieu-pratique").val()]);
    $("#moyen_return").text(traduction[$("#moyen-dentrainement").val()]);
})


$("#training-list > li").click(function () {
    localStorage.selectedTraining = $(this).attr('id');
    window.location.hash = "#coach_training_stats";
});

function stringToMinuteInt(h) {
    var t = h.replace(/h/g, ":").split(":");
    if (t.length == 2) {
        var h = parseInt(t[0]);
        var min = parseInt(t[1]);
        return h * 60 + min;
    }
    else {
        return -1;
    }
}

var date_debut;
var date_fin;
function getDate(dt){ // patch chrome


    if(/-/.test(dt)){ // then chrome
        return moment(dt , "YYYY-MM-DD");

    }
    else{
        return moment(dt , "DD/MM/YYYY");
    }
}

$("#validation_seance").click(function () {
    // FOR Chrome wtf
    var titre = $('#title').val();
    var date = moment($('#get_date').val(), "DD/MM/YYYY");  
    var heure_deb = stringToMinuteInt($("#heure_deb").val());
    var heure_fin = stringToMinuteInt($("#heure_fin").val());
    var objective = $("#objectives").val();
    var procede = $("#procede-entrainement").val();
    var milieu = $("#milieu-pratique").val();
    var moyen = $("#moyen-dentrainement").val();
    var typeglobal = $("#moyen-dentrainement").find(":selected").parent().attr("label");
    date = getDate(date);
    date_deb = date.clone().add(heure_deb, "minutes");
    date_fin = date.clone().add(heure_fin, "minutes");
    
    var g_training = {
        summary: titre,
        description: objective,
        start: date_deb,
        end: date_fin,
        color: googleColor[globaltypeToColor[typeglobal]]
    };

    selected_sportmen.forEach(function (s) {
        postOnGoogleCalendar(s.email, g_training, function (res) {
            if (res.status === "confirmed") {
                calendarOK(s.email, s.profile);
            }
            else {
                errorEmailNoBinded(s.email, s.profile);
            }
        });
    });
    // make all
    selected_sportmen.forEach( function(s) {
        $.post("/api/trainings/create/"+s.id, {
            "training": {
                "titre": titre,
                "date": date.toDate().toString(),
                "heure_deb": heure_deb,
                "heure_fin": heure_fin,
                "objective": objective,
                "procede": procede,
                "milieu": milieu,
                "type": moyen,
                "globaltype": typeglobal
            }
        });
    })
    
    //return;
    $.post("/api/trainings/create", {
        "training": {
            "titre": titre,
            "date": date.toDate().toString(),
            "heure_deb": heure_deb,
            "heure_fin": heure_fin,
            "objective": objective,
            "procede": procede,
            "milieu": milieu,
            "type": moyen,
            "globaltype": typeglobal,
            "sportmen" : selected_sportmen.map(function(s){return s.id;})
        }
    }).done(function () {
        $('#myModal2').on('hidden.bs.modal', function () {
            window.location.hash = "#coach_training_list";
            $(this).unbind();
        });
        $('#myModal2').modal("hide");
    });
});

function postTrainingSportman(sportman_item) {

}


function postOnGoogleCalendar(email, training, callback) {
    var event = {
        "colorId": training.color,
        'summary': training.summary,
        'description': training.description,
        'start': {
            'dateTime': training.start.toISOString(),
            'timeZone': 'Europe/Paris'
        },
        'end': {
            'dateTime': training.end.toISOString(),
            'timeZone': 'Europe/Paris'
        },
        'attendees': selected_sportmen.map(function (s) {
            return {"email": s.email}
        }),
        'reminders': {
            'useDefault': false,
            'overrides': [
                {'method': 'email', 'minutes': 24 * 60},
                {'method': 'popup', 'minutes': 10}
            ]
        }
    };
    var request = gapi.client.calendar.events.insert({
        'calendarId': email,
        'resource': event
    });

    request.execute(function (event) {
        callback(event);
    });

}


function loadInElem(url, element) {
    $.get(url, function (data) {
        $(element).html(data);
    });
}


function checkAuth() {
    gapi.auth.authorize(
        {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
        handleAuthResult);

}

var CLIENT_ID = '807790005110-51aglkmouvjitokl3dp5j4fr9eclke01.apps.googleusercontent.com';

var SCOPES = ["https://www.googleapis.com/auth/calendar.readonly", "https://www.googleapis.com/auth/calendar"];

function createTraining(sportmen, training) {


    $.post("/api/trainings/create", {training: training}).done(function (data) {
        if (data.training_id) {
            training._id = _id;
            sportmen.forEach(function (elem) {
                $.post("/api/trainings/create/" + elem, training);
            });
        }
    })

}


function handleAuthResult(authResult) {
    if (authResult && !authResult.error) {
        loadCalendarApi();
    } else {
        console.warn("Not logged in for the moment");
    }
}
function loadCalendarApi() {
    gapi.client.load('calendar', 'v3', calendarApiCallback);
}

function calendarApiCallback() {
    console.log("Google calendar Loaded");

}


//-----------------SELECT SPORTMEN ---------------------------
$("#trigger-sportman-select").click(function () {
    $("#sportman-select .sportman-item").not(".model").remove();
    $.get("/api/sportmen/profile/basic", function (sportmen) {
        sportmen.forEach(function (sportman) {
            var s = $("#model-sportman")
                .clone(true, true)
                .attr("id", sportman._id)
                .attr("email", sportman.profile.email)
                .removeClass("model");

            $(s).find(".user-img").attr('src', sportman.profile.pictures.profile);
            $(s).find(".user-name-text").text(sportman.profile.name);
            $(s).find(".user-surname-text").text(sportman.profile.surname);
            if (sportmanIsSelected({id: sportman._id})) {
                $(s).addClass("active");
            }
            $("#sportman-list").append(s);
        });
    });

    $("#sportman-select").modal("show");

});

$("#search").keyup(function (event) {

    var sStr = $("#search").val();
    if (sStr == '') {

        var a = $("#sportman-list > li").sort(function (e1, e2) {
            var t1 = $(e1).find(".user-name > .user-name-text").text() + " " + $(e1).find(".user-name > .user-surname-text").text();
            var t2 = $(e2).find(".user-name > .user-name-text").text() + " " + $(e2).find(".user-name > .user-surname-text").text();
            return t1 == t2 ? 0 : t1 < t2 ? -1 : 1;
        });
        $("#sportman-list").html(a);
        $("#sportman-list > li ").not("#model-sportman").removeClass("hidden");

    } else {
        var tab = sortUsers(sStr);
        $("#user-list").not("#model-sportman").empty();
        var i = 0;
        var to_disp = 6;
        for (i = 0; i < tab.length; i++) {
            var c = $((tab[i]).elem);
            if ((tab[i]).distance > 0.8 && to_disp > 0) {

                c.removeClass("hidden");
                to_disp--;
            } else {
                c.addClass("hidden");
            }
            $("#user-list").append(c);
        }
    }
});

// ----------------------------------SELECT Sportman--------------------------------------------
var selected_sportmen = [];
function sportmanIsSelected(sportman) {
    for (i in selected_sportmen) {
        if (selected_sportmen[i].id === sportman.id) {
            return true;
        }
    }
    return false;
}
function indexOfSportman(sportman) {
    for (i in selected_sportmen) {
        if (selected_sportmen[i].id === sportman.id) {
            return i;
        }
    }
    return -1;
}
function removeItem(item) {
    var arr = [];
    for (i in selected_sportmen) {
        if (selected_sportmen[i].id !== item.id) {
            arr.push(selected_sportmen[i]);
        }

    }
    selected_sportmen = arr;
}
$(".sportman-item").click(function () {
    var item = {email: $(this).attr("email"), id: $(this).attr("id"), profile: $(this).find(".user-img").attr("src")};
    if (sportmanIsSelected(item)) {
        removeItem(item);
    }
    else {
        selected_sportmen.push(item);
    }
    $(this).toggleClass("active");


});
$("#validate_users").click(function () {
    $("#sportman-select").modal("hide");
});


// CHECK INPUT

function checkDate() {
    // CHROME compat
    var date = moment($('#get_date').val(), "DD/MM/YYYY");
    console.log(date);
    if (!date.isValid()) {
        return {"error": "Date invalide format : Jour/Mois/Année", state: "error"};
    }
    else if (date.isBefore(moment())) {
        return {"warn": "La date se trouve dans le passé", state: "warn"};
    }
    else {
        return {"success": true, state: "success"};
    }
}

function checkHourStart() {
    var hour1 = ($("#heure_deb").val()).replace("h", ":");
    var hour2 = ($("#heure_fin").val()).replace("h", ":");
    var vH1 = stringToMinuteInt(hour1);
    var vH2 = stringToMinuteInt(hour2);
    if (hour2.isHour()) {
        if (vH1 <= vH2) {
            return {"success": true, state: "success"};
        }
        else {
            return {"error": "l'heure de début supérieure à l'heure de fin", state: "error"};
        }
    }
    else {
        if (hour1.isHour()) {
            return {"success": true, state: "success"};
        }
        else {
            return {"error": "Heure invalide", state: "error"};
        }
    }
}

function checkHourEnd() {
    var hour1 = ($("#heure_deb").val()).replace("h", ":");
    var hour2 = ($("#heure_fin").val()).replace("h", ":");
    var vH1 = stringToMinuteInt(hour1);
    var vH2 = stringToMinuteInt(hour2);
    if (hour1.isHour()) {
        if (vH1 <= vH2) {
            return {"success": true, state: "success"};
        }
        else {
            return {"error": "l'heure de début supérieure à l'heure de fin", state: "error"};
        }
    }
    else {
        if (hour2.isHour()) {
            return {"success": true, state: "success"};
        }
        else {
            return {"error": "Heure invalide", state: "error"};
        }
    }
}

function checkSessionTitle() {
    if ($("#title").text() !== "") {
        return {"success": true};
    }
    else {
        return {"error": "Le titre ne peut être vide ."};

    }
}
var check_functions = {"date": checkDate, "hour_start": checkHourStart, "hour_end": checkHourEnd, "title": checkSessionTitle()}
var state_class = {"success": "has-success", "error": "has-error", "warn": "has-warning"};
function rmStateClass($elem) {
    for (st  in state_class) {
        $elem.removeClass(state_class[st]);
    }
}

$(".needs-check > input").keyup(function (e) {
    $(this).parent().find(".status-message").text("");
    var fun = $(this).parent().attr("check-function");
    var c = (check_functions[fun] || console.warn)("Check Function not defined");
    rmStateClass($(this).parent());
    if (c.error) {
        $(this).parent().find(".status-message").text(c.error);
        $(this).parent().addClass(state_class["error"]);
    }
    else if (c.warn) {
        $(this).parent().find(".status-message").text(c.warn);
        $(this).parent().addClass(state_class["warn"]);
    }
    else {
        $(this).parent().addClass(state_class["success"]);
    }
});

$(".needs-check > input").click(function (e) {
    $(this).parent().find(".status-message").text("");
    var fun = $(this).parent().attr("check-function");
    var c = (check_functions[fun] || console.warn)("Check Function not defined");
    rmStateClass($(this).parent());
    if (c.error) {
        $(this).parent().find(".status-message").text(c.error);
        $(this).parent().addClass(state_class["error"]);
    }
    else if (c.warn) {
        $(this).parent().find(".status-message").text(c.warn);
        $(this).parent().addClass(state_class["warn"]);
    }
    else {
        $(this).parent().addClass(state_class["success"]);
    }
});

$('#datepairExample .date').datepicker({
    'format': 'd/mm/yyyy',
    'autoclose': true,
});

// CHROME WTF
