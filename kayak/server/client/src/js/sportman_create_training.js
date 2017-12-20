var date_debut;
var date_fin;

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

$("#validation_seance").click(function () {
    var titre = $('#title').val();
    var date = moment($('#get_date').val(), "DD/MM/YYYY");
    var heure_deb = stringToMinuteInt($("#heure_deb").val());
    var heure_fin = stringToMinuteInt($("#heure_fin").val());
    var objective = $("#objectives").val();
    var procede = $("#procede-entrainement").val();
    var milieu = $("#milieu-pratique").val();
    var moyen = $("#moyen-dentrainement").val();
    var typeglobal = $("#moyen-dentrainement").find(":selected").parent().attr("label");

    date_deb = date.clone().add(heure_deb, "minute");
    date_fin = date.clone().add(heure_fin, "minute");

    var g_training = {
        summary: titre,
        description: objective,
        start: date_deb,
        end: date_fin,
        color: googleColor[globaltypeToColor[typeglobal]]
    };

    postOnGoogleCalendar('primary', g_training, function (res) {
        if (res.status === "confirmed") {
        }
        else {
        }
    });

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
            "globaltype": typeglobal
        }
    }).done(function () {
        $('#myModal2').on('hidden.bs.modal', function () {
            window.location.hash = "#sportman_training_book";
            $(this).unbind();
        });
        $('#myModal2').modal("hide");
    });
});

$('#datepairExample .date').datepicker({
    'format': 'd/mm/yyyy',
    'autoclose': true,
});

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

function checkAuth() {
    gapi.auth.authorize(
        {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
        handleAuthResult);
}

var CLIENT_ID = '807790005110-51aglkmouvjitokl3dp5j4fr9eclke01.apps.googleusercontent.com';

var SCOPES = ["https://www.googleapis.com/auth/calendar.readonly", "https://www.googleapis.com/auth/calendar"];

function postOnGoogleCalendar(email, training, callback) {
    var event = {
        "colorId": training.color,
        'summary': training.summary,
        'description': training.description,
        'start': {
            'dateTime': training.start.toISOString(),
            //'timeZone': 'Europe/Paris'
        },
        'end': {
            'dateTime': training.end.toISOString(),
           //'timeZone': 'Europe/Paris'
        },
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

function checkDate() {
    // CHROME compat
    var date = moment(new Date($("#get_date").val())).set(0, "hours").subtract(1, "minute");
    console.log(date);
    if (date.isBefore(moment())) {
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

var check_functions = {"date": checkDate, "hour_start": checkHourStart, "hour_end": checkHourEnd}
var state_class = {"success": "has-success", "error": "has-error", "warn": "has-warning"};
function rmStateClass($elem) {
    for (st  in state_class) {
        $elem.removeClass(state_class[st]);
    }
}

$(".needs-check > input").change(function (e) {
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

function stringToMinuteInt(h) {
    var t = h.replace("h", ":").split(":");
    if (t.length == 2) {
        var h = parseInt(t[0]);
        var min = parseInt(t[1]);
        return h * 60 + min;
    }
    else {
        return -1;
    }
}