/**
 * Created by Grégoire Linot on 12/07/2016.
 */
/**
 * Created by Grégoire Linot on 12/07/2016.
 */



// Auth Check
function checkAuthKanote() {
    $.get("/api/auth/is_valid", function (data) {
        if (data.valid) {
            setTimeout(checkAuthKanote, 10000);
        }
        else {
            window.location = "/login.html";
        }
    })
}
checkAuthKanote();

// GOOGLE CALENDAR
var CLIENT_ID = '807790005110-51aglkmouvjitokl3dp5j4fr9eclke01.apps.googleusercontent.com';
CLIENT_ID = '441938037337-qqeqpl0jjlnkhoa29genbhb3e9qa749d.apps.googleusercontent.com';

// google calendar


/*
 * Function to create a special crafted object to send to google calendar
 * */


var gapi_extended = {
    craftGoogleCalendarTraining: function (kanote_training) {
        var base_date = kanote_training.date;
        var basedate = moment(new Date(kanote_training.date));
        var date_deb = basedate.clone().add(kanote_training.heure_deb, "minutes").toDate();
        var date_fin = basedate.clone().add(kanote_training.heure_fin, "minutes").toDate();
        return {
            summary: kanote_training.titre,
            description: kanote_training.objective,
            start: date_deb,
            end: date_fin,
            color: globaltypeToGoogleColor[kanote_training.globaltype],
            lieu : kanote_training.lieu
        };
    },
    postOnGoogleCalendarMulti: function (emails, training, callback, res) {
        res = res || []; // init res
        var email = emails.pop();
        if (email != null) {
            gapi_extended.postOnGoogleCalendar(email, training, function (e) {
                res.push({email: email, id: e.id});
                gapi_extended.postOnGoogleCalendarMulti(emails, training, callback, res);
            });
        }
        else {
            callback(res);
        }
    },

    postOnGoogleCalendar: function (email, training, callback) {
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
            'reminders': {
                'useDefault': false,
                'overrides': [
                    {'method': 'email', 'minutes': 24 * 60},
                    {'method': 'popup', 'minutes': 10}
                ]
            },
            'location' : training.lieu || ""
        };
        var request = gapi.client.calendar.events.insert({
            'calendarId': email,
            'resource': event
        });

        request.execute(function (event) {
            callback(event);

        });

    },
    removeEventOnGoogleCalendar: function (email, event_id, callback) {
        gapi.client.calendar.events.delete({calendarId: email, eventId: event_id}).execute(function (rep) {
            callback(rep);
        });
    },
    removeEventOnGoogleCalendarRecursive: function (google_ids, callback) {
        var id = google_ids.pop();
        if (id) {
            gapi_extended.removeEventOnGoogleCalendar(id.email, id.id, function () {
                gapi_extended.removeEventOnGoogleCalendarRecursive(google_ids, callback);
            });
        }
        else {
            callback();
        }
    },
    updateEventOnGoogleCalendar: function (email, event_id, training, callback) {
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
            'eventId': event_id,
            'resource': event
        }).execute(callback);
    },
    updateEventOnGoogleRecursive: function (gids, training, callback) {
        var id = gids.pop();
        if (id != null) {
            gapi_extended.updateEventOnGoogleCalendar(id.email, id.id, training, function (e) {
                gapi_extended.updateEventOnGoogleRecursive(gids, training, callback);
            });
        }
        else {
            callback();
        }
    }
};

var SCOPES = [
    'https://www.googleapis.com/auth/userinfo.email',
    'https://www.googleapis.com/auth/userinfo.profile',
    "https://www.googleapis.com/auth/calendar.readonly",
    "https://www.googleapis.com/auth/calendar"
];
// Moment SetUp and functions
moment.locale("fr");
function getWeeks(year, month) {
    var dayOne = moment(year + '-' + month + '-1-0-0-0', 'YYYY-MM-DD-HH-mm-ss');
    var nbOfWeeks = Math.floor(dayOne.daysInMonth() / 7);
    var res = [];
    var startOfWeek = dayOne;
    startOfWeek.day(1); // lundi
    for (var i = 0; i < nbOfWeeks; i++) {
        var weekIsoNumber = startOfWeek.isoWeek();
        var wsDate = startOfWeek.format('dddd Do MMMM');
        startOfWeek.add(6, 'day');
        var weDate = startOfWeek.format('dddd Do MMMM');
        startOfWeek.add(1, 'day'); // Lundi
        res.push({
            weekIsoNumber: weekIsoNumber,
            label: wsDate + ' - ' + weDate
        });
    }
    return res;
}
function handleAuthResult(authResult) {
    if (authResult && !authResult.error) {
        loadCalendarApi();
    } else {
        $(document).trigger("google_api_error");
    }
}
function loadCalendarApi() {
    gapi.client.load('calendar', 'v3', calendarApiCallback);
    gapi.client.load('plus', 'v1', meApiCallback);

}
function calendarApiCallback() {
    Materialize.toast("Google calendar chargé avec succès ", 1000);
    $(document).trigger("gapi_calendar_loaded");
}
function meApiCallback() {
    gapi.client.request('oauth2/v1/userinfo').execute(function (me) {
        if (me.email != localStorage.email) {

            angular.injector(['ng', 'kanoteApp']).invoke(function (dialogs) {
                dialogs.triggerEmailMismatch(
                    localStorage.email,
                    me.email
                );
            });
        }
    });
}
function checkAuth() {
    gapi.auth.authorize(
        {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
        handleAuthResult);
}
//Animate CSS
$.fn.extend({
    animateCss: function (animationName) {
        var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
        $(this).addClass('animated ' + animationName).one(animationEnd, function () {
            $(this).removeClass('animated ' + animationName);
        });
    }
});
$.fn.extend({
    magicCss: function (animationName) {
        var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
        $(this).addClass('magictime ' + animationName).one(animationEnd, function () {
            $(this).removeClass('magictime')
            $(this).removeClass(animationName);
        });
    }
})
// Raw JS
function hourToMin(str) {
    var t = (str || "").split(/[h\-:]/gi)
    return parseInt(t[0] || 0) * 60 + parseInt(t[1] || 0)
}
var minToHour = function (mins) {
    var hours = Math.floor(mins / 60);
    var mins = mins - hours * 60;
    return hours + "h" + mins;
}
function getNumWeeksForMonth(year, month) {
    date = new Date(year, month - 1, 1);
    day = date.getDay();
    numDaysInMonth = new Date(year, month, 0).getDate();
    return Math.floor((numDaysInMonth + day) / 7);
}
//--RAW JS -
$(".button-collapse").sideNav({
    menuWidth: 300, // Default is 240
    edge: 'left', // Choose the horizontal origin
    closeOnClick: true // Closes side-nav on <a> clicks, useful for Angular/Meteor
})
$("body").on("swiperight", function () {
    $('.button-collapse').sideNav('show');
});
$("body").on("swipeleft", function () {
    $('.button-collapse').sideNav('hide');
});
// GOOGLE API ASSETS
var globaltypeToGoogleColor = {
    "SPECIFIQUE": "9",
    "CARDIO": "10",
    "RENFORCEMENT MUSCULAIRE": "3",
    "COMPETITIONS ET TESTS": "11",
    "SOINS": "6",
    "STRATEGIE PROJET": "8"
};


//trad
var traduction = {
    // SPECIFIQUE
    "slalom_sans_portes": "Slalom sans portes",
    "slalom_avec_portes": "Slalom avec portes",
    "bateau_directeur": "Bateau directeur",
    "specifique_autre": "Autre (Spécifique)",
    // CARDIO
    "course": "Course à pieds",
    "velo": "Velo de route",
    "vtt": "VTT",
    "ski_de_fond": "Ski de fond",
    "natation": "Natation",
    "surf": "Surf",
    "sport_raquette": "Sport de raquette",
    "cardio_autre": "Autre (Cardio)",
    // RENFORCEMENT MUSCULAIRE
    "rm_salle": "Renforcement Musculaire en salle",
    "rm_embarquee": "Renforcement musculaire embarqué",
    "rm_autre": "Autre (Renforcement musculaire)",
    // COMPETITION ET TEST
    "situation_compet": "Situation de compétition",
    "n1": "N1",
    "icf": "ICF",
    "big_cup": "World Cup / Championnats du monde / Championnats d'Europe",
    "test_terrain": "Test terrain",
    "test_hrv": "Test HRV",
    "compet_autre": "Autre (Compétition)",
    // SOINS
    "kine_osteo": "Kiné/Ostéo",
    "balneo": "Balnéothérapie",
    "relaxation_yoga": "Relaxation/Yoga",
    "chryotherapie": "Chryotherapie",
    "etirements": "Etirements",
    "soins_autre": "Autre (Soins)",
    // STRATEGIE PROJET
    "analyse_video": "Analyse vidéo",
    "entretien": "Entretien avec entraîneur",
    "gestion_conception": "Gestion, conception, entretien matos",
    "travail_projet_vie": "Travail sur le projet de vie",
    "logistique": "Logistique saison, course, actions",
    "strategie_autre": "Autre (Stratégie)",

    // PROCEDE
    "endurance": "Endurance",
    "sv1": "SV 1",
    "sv2": "SV 2",
    "lactique": "Lactique",
    "pma_vma": "PMA/VMA",
    "endurance_vitesse": "Endurance Vitesse",
    "vitesse": "Vitesse",
    "technique": "Technique",
    "rm_f_endurance": "RM Force Endurance",
    "rm_f_puissance": "RM Force Puissance",
    "rm_puissance_vitesse": "RM Puissance Vitesse",
    "rm_f_max": "RM Force Max",
    "rm_f_explosive": "RM Force Explosive",
    "autre_explosive": "Autre",

    // MILIEU
    "eau_plate": "Eau plate",
    "eau_vive_II": "Eau vive Classe II",
    "eau_vive_III": "Eau vive Classe III",
    "eau_vive_IV": "Eau vive Classe IV",
    "autre_milieu": "Autre",

    // BILAN
    "temps_travail_intensite": "Temps de travail à l'intensité",
    "difficulte": "Difficulté",
    "nb_portes_franchies": "Nombre de portes franchies",
    "nb_erreurs": "Nombre d'erreurs",
    "nb_parcours_realises": "Nombre de parcours réalisés",
    "nb_parcours_a_zero": "Nombre de parcours à zéro",
    "nb_penalites_seance": "Nombre de pénalités sur la scéance",
    "nb_km": "Nombre de kms",
    "fc_moyenne": "FC moyenne",
    "fc_max": "FC max",
    "sportman": "Sportif",
    "coach": "Coach",

    //ITEMS Profile
    //HEATH
    "weight": "Poids",
    "fat_percentage": "Pourcentage de matière grasse",
    "height": "Taille",


};


var globaltypeToColor = {
    "SPECIFIQUE": "blue",
    "CARDIO": "green",
    "RENFORCEMENT MUSCULAIRE": "purple",
    "COMPETITIONS ET TESTS": "red",
    "SOINS": "orange",
    "STRATEGIE PROJET": "grey",
};


var globaltypeToImage = {
    "SPECIFIQUE": "specifique.png",
    "CARDIO": "cardio.png",
    "RENFORCEMENT MUSCULAIRE": "muscu.png",
    "COMPETITIONS ET TESTS": "podium.png",
    "SOINS": "health-care.png",
    "STRATEGIE PROJET": "mind-gears.png",
};


var typetoGlobalType = {
    analyse_video: "STRATEGIE_PROJET",
    entretien: "STRATEGIE_PROJET",
    gestion_conception: "STRATEGIE_PROJET",
    travail_projet_vie: "STRATEGIE_PROJET",
    logistique: "STRATEGIE_PROJET",
    strategie_autre: "STRATEGIE_PROJET",
    kine_osteo: "SOINS",
    balneo: "SOINS",
    relaxation_yoga: "SOINS",
    chryotherapie: "SOINS",
    etirements: "SOINS",
    soins_autre: "SOINS",

    situation_compet: "COMPETITIONS ET TESTS",
    n1: "COMPETITIONS ET TESTS",
    icf: "COMPETITIONS ET TESTS",
    big_cup: "COMPETITIONS ET TESTS",
    test_terrain: "COMPETITIONS ET TESTS",
    test_hrv: "COMPETITIONS ET TESTS",
    compet_autre: "COMPETITIONS ET TESTS",

    rm_autre: "RENFORCEMENT MUSCULAIRE",
    rm_salle: "RENFORCEMENT MUSCULAIRE",
    rm_embarquee: "RENFORCEMENT MUSCULAIRE",

    course: "CARDIO",
    velo: "CARDIO",
    vtt: "CARDIO",
    ski_de_fond: "CARDIO",
    natation: "CARDIO",
    sport_raquette: "CARDIO",
    cardio_autre: "CARDIO",

    slalom_sans_portes: "SPECIFIQUE",
    slalom_avec_portes: "SPECIFIQUE",
    bateau_directeur: "SPECIFIQUE",
    specifique_autre: "SPECIFIQUE"
};


//Angular
var kanoteApp = angular.module('kanoteApp', ['angular-chartist', 'ui.materialize']);


kanoteApp.service('dialogs', function () {
    this.triggerError = function (error, onSuccess) {
        $scope = $('#globalError').scope();
        $scope.setMessage(error);
        $scope.onSuccess = onSuccess;
        $('#globalError').openModal();

    };
    this.triggerEmailMismatch = function (kanote, gmail, onSuccess) {
        $scope = $('#modalGoogleErrorEmail').scope();
        $scope.setEmails(kanote, gmail);
        $scope.onSuccess = onSuccess;
        $('#modalGoogleErrorEmail').openModal();

    };
    this.triggerDeleteModal = function (onSuccess, onError) {
        $scope = $("#modalRemove").scope();
        $scope.setOnSuccess(onSuccess);
        $scope.setOnError(onError);
        $("#modalRemove").openModal();
    }
});
kanoteApp.service('share', function () {

    this.selectedTraining = "null";
    var scope = this;
    this.setSelectedTraining = function (t) {
        scope.selectedTraining = t
    };
    this.getSelectedTraining = function () {
        return scope.selectedTraining
    };


});

kanoteApp.controller('modalRemoveCrtl', function ($scope) {


    var success = null;
    var cancel = null;
    $scope.setOnSuccess = function (cb) {
        success = cb;
    };
    $scope.setOnError = function (cb) {
        cancel = cb;
    };
    $scope.cancel = function () {
        cancel();
    };
    $scope.success = function () {
        success();
    };


});
kanoteApp.controller('errorController', function ($scope) {
    $scope.message = "";
    $scope.onSuccess = null;
    $scope.setMessage = function (msg) {
        $scope.$apply(function () {
            $scope.message = msg;
        });
    }

});
kanoteApp.controller('emailMismatchCrtl', function ($scope) {
    $scope.onSuccess = null;
    $scope.setEmails = function (kanote, gmail) {
        $scope.$apply(function () {
            $scope.kanoteEmail = kanote;
            $scope.googleEmail = gmail;
        });
    }

});
kanoteApp.directive('repeatUpdateModal', function () {
    return function (scope, element, attrs) {
        if (scope.$last) {
            $('.modal-trigger').leanModal();
        }
    };
});
kanoteApp.directive('repeatUpdateSelect', function () {
    return function (scope, element, attrs) {
        if (scope.$last) {
            $('select').material_select();
        }
    };
});

kanoteApp.directive('sbLoad', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {
            var fn = $parse(attrs.sbLoad);
            elem.on('load', function (event) {
                scope.$apply(function () {
                    fn(scope, {$event: event});
                });
            });
        }
    };
}]);
// MAIN Controller
kanoteApp.controller('mainCrtl', function ($scope, $http) {


    /* !!!!  GET GOOGLE ids  !!!*/
    $scope.google_api_tokens = {};
    $http.get("/api/tokens/google").then(function (rep) {
        $scope.google_api_tokens = rep.data["google_api_tokens"];
        CLIENT_ID = $scope.google_api_tokens.calendar;
        $.getScript("https://maps.googleapis.com/maps/api/js?key=" + $scope.google_api_tokens.maps + "&signed_in=true&libraries=places");
    });

    $scope.NB_MAX_BACK_IMAGE = 33;

    $scope.backgroundStyle = {"background-image": "url(\"/img/back/" + Math.ceil(new Date().getDate() % $scope.NB_MAX_BACK_IMAGE) + ".jpg"};
    // GLOBAL VARS

    // Coach click on userList
    $scope.selectedUser = {};

    $scope.selectUser = function (u) {
        $scope.selectedUser = u;
    }

    // Coach click on training
    $scope.selectedTraining = {};
    $scope.selectTraining = function (u) {
        $scope.selectedTraining = u;
    }


    // END GLOBAL VARS


    var menu = {
        "sportman": {
            "Carnet d'entrainement": "sportman_training_book.html",
            //  "Statistiques": "sportman_stats.html",  LATER
            "Creer une séance": "sportman_create_training.html",
            "Mon Compte": "sportman_account.html"
        },
        "coach": {
            "Liste des entrainements": "coach_training_book.html",
            "Creer une séance": "coach_create_training.html",
            "Liste des sportifs": "sportman_list.html",
            "Excel": "coach_excel_report.html",
            "Mon Compte": "simple_account.html",
        },

        "admin": {
            "Liste des utilisateurs": "admin_user_list.html",
            "Google Dasboard": "admin_google_dashboard.html",
            "Mon Compte": "simple_account.html"
        }

    };
    $scope.userMenu = {};
    $scope.selectedMenuItem = "";

    $http({
        method: 'GET',
        url: '/api/role'
    }).then(function () {

        $scope.userMenu = menu[localStorage.role];
        $scope.selectedMenuItem = $scope.userMenu[Object.keys($scope.userMenu)[0]];
    }, function () {
    });


    $scope.selectMenuItem = function (url) {
        $scope.selectedMenuItem = url + "?_id=" + Math.ceil(Math.random() * 1000000);
    };

    $scope.goToProfile = function () {
        $scope.selectMenuItem($scope.userMenu["Mon Compte"]);
        $('.button-collapse').sideNav('hide');
    }

    $scope.disconnect = function () {
        Cookies.remove('session_id', {path: '/'});
        location.href = "/login.html";
    };

    $scope.me = {};

    $http.get("/api/profile/me").then(function (res) {
        $scope.me = res.data;
    });

    $scope.translate = function (name) {
        return traduction[name];
    };

    var firstTime = true;
    $scope.openMenu = function () {
        if (firstTime) {
            $("#profile_image").animateCss("bounceIn");
            firstTime = false;
        }
    }

});

//SPORTMAN
kanoteApp.controller("trainingListCrtl", function ($scope, $http, share, dialogs) {


    $scope.trainings = [];
    $scope.noMoreTrainings = false;
    var currentIndex = 0;
    $scope.getTrainings = function () {

        $http.get('/api/trainings/me/basic', {params: {block_index: currentIndex}}).then(function success(rep) {

            $scope.trainings = $scope.trainings.concat(rep.data);
            $scope.noMoreTrainings = (rep.data || []).length == 0;
            currentIndex++;

        }, function error(rep) {


        });
    };

    $scope.getTrainings();

    $scope.currentTraining = null;


    $scope.translate = function (name) {
        return traduction[name];
    };

    $scope.getGlobalTypeColor = function (globaltype) {
        return globaltypeToColor[globaltype];
    };

    $scope.globalTypeToImage = function (gbt) {
        return "/img/globaltype/" + globaltypeToImage[gbt];
    };


    $scope.clickOnRemoveTraining = function () {

        dialogs.triggerDeleteModal(function () {
                $scope.removeTraining();
            },
            function () {

            });
    };
    $scope.removeTraining = function () {
        if (gapi.client.calendar == null) {

            checkAuth();
            $(document).on("gapi_calendar_loaded", function () {


            })
        }
        else {
            gapi_extended.removeEventOnGoogleCalendar($scope.$parent.me.email, $scope.currentTraining.google_calendar_id, function () {
            })
        }

        $http.post('/api/trainings/remove/' + $scope.currentTraining._id).then(
            function () {
                var id = $scope.currentTraining._id;
                var index = -1;
                for (i in $scope.trainings) {
                    if ($scope.trainings[i]._id == id) {
                        $scope.training = $scope.trainings.splice(i, 1);
                        break;
                    }
                }
                Materialize.toast('L\'entrainement a été supprimé', 2000);
            },
            function () {
                dialogs.triggerError('Nous n\'vons pas pu supprimer cet entrainment ');
            });


    };

    $scope.clickOnTraining = function (training) {
        $scope.$parent.selectMenuItem("sportman_training_view.html");
        $scope.currentTraining = training;
        share.setSelectedTraining(training);
        console.log("training");
    };

    $scope.selectCurrentTraining = function (training) {
        $scope.currentTraining = training;

    };

    $scope.createTraining = function () {
        $scope.$parent.selectMenuItem('sportman_create_training.html');
    };


    $scope.getDate = function (t) {
        return moment(new Date(t.date)).format("dddd Do MMMM YYYY");
    }
});
kanoteApp.controller('createTrainingSportmanCrtl', function ($scope, $http, dialogs) {

    $(document).on('gapi_calendar_loaded', function () {
        $scope.$apply(function () {

            $scope.googleSynced = true;
            $("#googleConnexion").closeModal();
        });
    });
    $(document).on("google_api_error", function () {
        $scope.$apply(function () {
            $scope.googleError = true;
        });
    });

    $("#googleConnexion").openModal();
    setTimeout(function(){checkAuth();}, 50);
    $scope.googleSynced = false;
    $scope.googleError = false;



    $scope.newTraining = {
        titre: "",
        date: "",
        lieu: "",
        date_raw: "",
        heure_deb: "",
        heure_fin: "",
        objective: "",
        procede: "sv2",
        milieu: "eau_vive_II",
        type: "slalom_sans_portes",
        globaltype: "",
        description: ""
    };
    $scope.$watch(function () {
        return $scope.newTraining.type
    }, function (old, young) {
        $scope.newTraining.globaltype = typetoGlobalType[$scope.newTraining.type];
    });
    $scope.$watch(function () {
        return $scope.newTraining.date_raw
    }, function (old, young) {
        try {
            $scope.newTraining.date = moment($scope.newTraining.date_raw, 'DD/MM/YYYY').toDate().toISOString();
        }
        catch (e) {
        }
    });


    $scope.checkHours = function () {
        if ($scope.newTraining.heure_deb == "" || $scope.newTraining.heure_fin == "") {
            return true;
        }
        else {
            return hourToMin($scope.newTraining.heure_deb) < hourToMin($scope.newTraining.heure_fin);
        }
    }
    $scope.createNewTraining = function () {
        $scope.newTraining.heure_deb = hourToMin($scope.newTraining.heure_deb);
        $scope.newTraining.heure_fin = hourToMin($scope.newTraining.heure_fin);
        $scope.newTraining.globaltype = typetoGlobalType[$scope.newTraining.type];
        $scope.newTraining.lieu = $("#lieu").val();
        var gtraining = gapi_extended.craftGoogleCalendarTraining($scope.newTraining);
        gapi_extended.postOnGoogleCalendar($scope.$parent.me.email, gtraining, function (event) {

            $scope.newTraining.google_calendar_html_link = event.htmlLink;
            $scope.newTraining.google_calendar_id = event.id;

            $http.post('/api/trainings/create', {"training": $scope.newTraining}).then(function () {
                $scope.$parent.selectMenuItem("sportman_training_book.html");

            }, function () {
                dialogs.triggerError('L\' enregistrement a échoué veuillez verifier que le formulaire soit complet');
            });
        });
    };
    $scope.cancelCreation = function () {
        $scope.$parent.selectMenuItem('sportman_training_book.html');
    };
});
kanoteApp.controller('sportmanTrainingViewCrtl', function ($scope, $http, share, dialogs) {
    $scope.editingMode = false;
    $scope.selectedTraining = share.getSelectedTraining();
    $scope.modified = false;
    $scope.modify = function () {
        $scope.modified = true;
    };
    function getTraining() {
        $http.get('/api/trainings/me/' + $scope.selectedTraining._id).then(
            function (rep) {
                $scope.selectedTraining = rep.data;
                $scope.ratings = [
                    {
                        current: rep.data.feedback.eval_objective || 1,
                        max: 5,
                        name: "Evaluation de l'objectif",
                        ref: "eval_objective"
                    },
                    {
                        current: rep.data.feedback.eval_sensations || 1,
                        max: 10,
                        name: "Sensations",
                        ref: "eval_sensations"
                    },
                    {
                        current: rep.data.feedback.eval_humeur || 1,
                        max: 10,
                        name: "Humeur",
                        ref: "eval_humeur"
                    }
                ];
            },
            function () {
                dialogs.triggerError("Erreur du serveur");
            });
    };
    getTraining();

    $scope.resetTraining = getTraining;

    $scope.getNiceDate = function () {
        return new Date($scope.selectedTraining.date).toLocaleDateString();
    };

    $scope.minToHour = function (mins) {
        var hours = Math.floor(mins / 60);
        var mins = mins - hours * 60;
        return hours + "h" + mins;
    };

    $scope.translate = function (name) {
        return traduction[name];
    };

    $scope.rating = 0;

    $scope.getSelectedRating = function (val, ref) {
        $scope.modified = true;
        $scope.selectedTraining.feedback[ref] = val; //update
    }


    $scope.postTrainingFeedback = function () {
        $http.post('/api/v2/trainings/fill/' + $scope.selectedTraining._id, {training: $scope.selectedTraining}).then(
            function () {
                Materialize.toast("Mise à jour réussie !", 2000);
                $scope.$parent.selectMenuItem("sportman_training_book.html");
            },
            function () {
                dialogs.triggerError("Nous n'avons pas pu mettre à jour l'entrainement");
            }
        );
    }


    $scope.optionalFeedbacks =
    {
        "temps_travail_intensite": "Temps de travail à l'intensité cible",
        "nb_portes_franchies": "Nombre de portes franchies",
        "nb_erreurs": "Nombre d'erreurs (touche,50, sortie de route)",
        "nb_penalites": "Nombre de pénalités",
        "nb_parcours_realises": "Nombre de parcours réalisés",
        "nb_parcours_a_zero": "Nombre de parcours à zéro",
        "nb_km": "Nombre de kms",
        "fc_moyenne": "FC moyenne",
    };
});
kanoteApp.directive('starRating', function () {
    return {
        restrict: 'A',
        template: '<ul class="rating">' +
        '<li ng-repeat="star in stars"  ng-click="toggle($index)" >' +
        '<i class="material-icons md-48 lighten-1-text animated star-icon" ng-class="star">star</i>' +
        '</li>' +
        '</ul>',
        scope: {
            ratingValue: '=',
            max: '=',
            onRatingSelected: '&'
        },
        link: function (scope, elem, attrs) {
            var updateStars = function () {
                scope.stars = [];
                for (var i = 0; i < scope.max; i++) {
                    scope.stars.push({
                        'amber-text': i < scope.ratingValue ? true : null,
                        'bounceIn': i < scope.ratingValue ? true : null,
                        'grey-text': i >= scope.ratingValue ? true : null,
                    });
                }
            };

            scope.toggle = function (index) {
                scope.ratingValue = index + 1;
                scope.onRatingSelected({
                    val: index + 1,
                });
            };

            scope.$watch('ratingValue', function (oldVal, newVal) {
                if (newVal) {
                    updateStars();
                }
            });
        }
    }
});
kanoteApp.controller('sportmanAccountCrtl', function ($scope, $http) {

    $scope.isEditingProfilePicture = false;

    $scope.role = localStorage.role;
    $scope.modified = {
        email: false,
        password: false
    }

    $scope.me = {};

    $scope.getMe = function () {
        $http.get("/api/profile/me").then(function (rep) {
            $scope.me = rep.data;
        });
    };

    $scope.getMe();
    $scope.id_card =
        [
            {name: "Entraineur club", path: "coach.club"},
            {name: "Entraineur EQF", path: "coach.eqf"},
            {name: "Entraineur EQF", path: "coach.eqf"},
        ];

    $scope.modifying = false;

    $scope.cancel = function () {
        $scope.getMe();
        $scope.modifying = false;
    };

    $scope.save = function () {
        $http.post("/api/profile/update/", $scope.me).then(function () {
            Materialize.toast("Profil mis a jour !", 2000);
        }, function () {
            Materialize.toast("Echec de la mise a jour du profil", 2000);
        });
        if ($scope.modified.email) {
            $http.post("/api/profile/email/update/", {email: $scope.me.email}).then(function () {
                Materialize.toast("Email mis a jour !", 2000);
            }, function () {
                Materialize.toast("Echec de la mise a jour de l'email verifiez qu'il s'agit d'une adresse gmail", 2000);
            });
        }
        if ($scope.modified.password) {
            $http.post("/api/profile/password/update/", {password: $scope.password}).then(function () {
                Materialize.toast("Mot de passe  mis a jour !", 2000);
            }, function () {
                Materialize.toast("Echec de la mise a jour du mot de passe", 2000);
            });
        }
        if ($scope.modified.health) {
            $scope.updateAllHealthItems();
        }
        if ($scope.modified.kayak_items) {
            updateKayakItems();
        }

        $scope.modifying = false;
        reset();
    };

    function reset() {
        $scope.modified = {
            email: false,
            password: false,
            health: false

        };
        $scope.getMe();
    };

    //$scope.password={"old" : "" , "new" : ""};


    // Health
    $scope.healthItems = ["fat_percentage", "weight", "height"];
    $scope.updateHealthItem = function (item) {
        $http.post("/api/profile/health/" + item, {value: $scope.me.health[item]}).then(function success(rep) {
            //    Materialize.toast(traduction[item] + " à été mis à jour ",2000);
        }, function error(rep) {
            Materialize.toast(traduction[item] + " n'as pas été mis à jour ", 2000);
        });
    };
    $scope.updateAllHealthItems = function () {
        $scope.healthItems.forEach(function (i) {
            $scope.updateHealthItem(i);
        });
    };

    $scope.editProfilePicture = function () {
        $("#fileInput").click();
    };


    // Kayak items
    $scope.kayakMaterialItems = [
        {
            name: "Bateau",
            path: "boat",
            items: [
                {
                    name: "Modèle",
                    path: "type",
                    modified: false
                },
                {
                    name: "Recoupe",
                    path: "recoupe",
                    modified: false
                },
                {
                    name: "Construction",
                    path: "construction",
                    modified: false
                }
            ]
        }, {
            name: "Pagaie",
            path: "pagaie",
            items: [
                {
                    name: "Modèle",
                    path: "type",
                    modified: false
                },
                {
                    name: "Longueur",
                    path: "longueur",
                    modified: false
                },
                {
                    name: "Surface de pâle",
                    path: "surface",
                    modified: false
                }
            ]
        }
    ];
    $scope.kayakItemsModified = false; // one or more have been modified
    function updateKayakItems() {
        $scope.kayakMaterialItems.forEach(function (item) {
            (item.items || []).forEach(function (item_sub) {
                if (item_sub.modified) {
                    $http.post("/api/profile/" + item.path + "/" + item_sub.path, {value: $scope.me[item.path][item_sub.path]}).then(
                        function () {
                        }, function () {
                        });
                }
                item_sub.modified = false;
            });
        });
    }

    // NG CROP
    $scope.loadingImage = false;
    $scope.myImage = '';
    $scope.myCroppedImage = '';

    $scope.uploadReady = false;

    var handleFileSelect = function (evt) {

        $scope.isEditingProfilePicture = true;
        $scope.uploadReady = false;
        $scope.loadingImage = true;
        var file = evt.currentTarget.files[0];
        var reader = new FileReader();
        reader.onload = function (evt) {
            $scope.$apply(function ($scope) {
                $scope.myImage = evt.target.result;

            });
        };
        reader.readAsDataURL(file);
    };
    $("#fileInput").on('change', handleFileSelect);

    ///  var cropper = {};
    $scope.imageLoaded = function () {
        var image = document.getElementById('image');
        cropper = new Cropper(image, {
            aspectRatio: 1,
            crop: function (e) {
            }
        });
        $scope.uploadReady = true;
        $scope.loadingImage = false;
    }

    $scope.cancelUploadImage = function () {
        $scope.isEditingProfilePicture = false;
        $scope.myImage = "";
        cropper.destroy();
    }
    $scope.uploadImage = function () {
        var dataBase64 = cropper.getCroppedCanvas({height: 512, width: 512}).toDataURL();
        $scope.$parent.me.pictures.profile = dataBase64;
        $scope.me.pictures.profile = dataBase64;
        $http.post("/api/profile/pictures/profile", {data: dataBase64}).then(function () {
            Materialize.toast("Photo de profil mise a jour", 1000);
            // reset state
            $scope.isEditingProfilePicture = false;
            $scope.myImage = "";
            cropper.destroy();
        }, function () {
            Materialize.toast("Erreur mise a jour de la photo de profil", 1000);
        });
    }


});

//ADMIN
kanoteApp.controller('adminUserListCrtl', function ($scope, $http) {
    $scope.validated = [];
    $scope.standby = [];

    function getUsers() {
        $http.get("/api/users/verified").then(function (rep) {
            $scope.validated = rep.data;
        });
        $http.get("/api/users/standby").then(function (rep) {
            $scope.standby = rep.data;
        });
    };
    getUsers();

    $scope.translate = function (name) {
        return traduction[name];
    };

    $scope.remove = function (user) {
        $http.post('/api/users/delete/' + user._id).then(
            function () {
                Materialize.toast(user.profile.name + " " + user.profile.surname + " à été supprimé", 3000);
                getUsers();
            },
            function () {
                Materialize.toast(user.profile.name + " " + user.profile.surname + " n'à pas pu être supprimé", 3000);
            }
        );
    };
    $scope.validate = function (user) {
        $http.post('/api/users/validate/' + user._id).then(
            function () {
                Materialize.toast(user.profile.name + " " + user.profile.surname + " à été validé", 3000);
                getUsers();
            },
            function () {
                Materialize.toast(user.profile.name + " " + user.profile.surname + " n'à pas pu être validé", 3000);
            }
        );
    };

    $scope.userToEdit = {};
    $scope.edit = function (user) {
        $scope.userToEdit = user;
        $('#modalEditUser').openModal();
        setTimeout(Materialize.updateTextFields, 200);
    };

    $scope.updateUser = function () {
        $http.post("/api/admin/users/update", $scope.userToEdit).then(
            function () {
                Materialize.toast($scope.userToEdit.profile.name + " " + $scope.userToEdit.profile.surname + " à été mis à jour", 3000);
                getUsers();
            },
            function () {
                Materialize.toast($scope.userToEdit.profile.name + " " + $scope.userToEdit.profile.surname + " n'à pas pu être mis à jour", 3000);
            }
        );
    };

    //add user
    $scope.userToAdd = {};
    $scope.addUser = function () {
        $http.post('/api/users/register', $scope.userToAdd).then(
            function () {
                Materialize.toast($scope.userToAdd.name + " " + $scope.userToAdd.surname + " à été ajouté", 3000);
                getUsers();
            },
            function () {
                Materialize.toast($scope.userToAdd.name + " " + $scope.userToAdd.surname + " n'à pas pu être ajouté", 3000);
            }
        );
    };
});
kanoteApp.controller('statsCrtl', function ($scope, $http, $timeout) {
    var d = {
        labels: ['W1', 'W2', 'W3', 'W4', 'W5', 'W6', 'W7', 'W8', 'W9', 'W10'],
        series: [
            [1, 2, 4, 8, 6, -20, null, -4, -6, -2]
        ]
    };
    $scope.simpleView =
    {
        week: {"name": "Cette semaine", wide: "week", data: d, stats: {}},
        month: {"name": "Ce mois-ci", wide: "month", data: d, stats: {assiduite: 0.4}},
        year: {"name": "Cette année", wide: "year", data: {labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]}, stats: {}}
    }
    ;
    $scope.updateData = function () {
        // Training Hours
        {
            $http.get('/api/stats/trainings/global/year', {params: {year: 2016}}).then(function (rep) {

                $scope.simpleView.year.data.series = [rep.data];
            });
            $http.get('/api/stats/trainings/global/month', {
                params: {
                    year: 2016,
                    month: 6
                }
            }).then(function (rep) {
                $scope.simpleView.month.data = {series: [rep.data]};
            });
            $http.get('/api/stats/trainings/global/week', {
                params: {
                    year: 2016,
                    weeknum: 29
                }
            }).then(function (rep) {
                $scope.simpleView.week.data.series = [rep.data];
            });
        }

        // Simple Stats
        {
            $http.get("/api/stats/trainings/overview/week", {params: {year: 2016, weeknum: 29}}).then(function (rep) {
                $scope.simpleView.week.stats = rep.data;
            });
            $http.get("/api/stats/trainings/overview/month", {params: {year: 2016, month: 1}}).then(function (rep) {
                $scope.simpleView.month.stats = rep.data;
            });
            $http.get("/api/stats/trainings/overview/year", {params: {year: 2016}}).then(function (rep) {
                $scope.simpleView.year.stats = rep.data;
            });
        }

        //
        {
        }

    };
    $scope.updateData();

    // Date Selection
    $scope.current_date = moment();
    $scope.current_wide = "week";

    // view
    $scope.selected_month = "Janvier";
    $scope.months = ["Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"];
    $scope.years = [];

    // create years
    $scope.selected_year = moment().year();
    {
        var nb = 30;
        var y = moment().year();
        while (nb >= 0) {
            $scope.years.push(y);
            y = y - 1;
            nb--;
        }
    }


    // WACTH
    var updateWeeks = function () {
    }

    $scope.$watch('selected_month', updateWeeks);
    $scope.$watch('selected_year', updateWeeks);


    $timeout(function () {
        $('select').material_select();
        $('.datepicker').pickadate({
            selectMonths: true, // Creates a dropdown to control month
            selectYears: 15, // Creates a dropdown of 15 years to control year,
        });
    });


    $scope.wide = "Année";
    $scope.selectYear = {
        value: moment().year(),
        years: []
    };

    // GET Years
    $http.get("/api/stats/trainings/years").then(function (rep) {
        $scope.selectYear.years = rep.data.years.sort();
        $scope.selectYear.value = $scope.selectYear.years[$scope.selectYear.years.length - 1];
    });
    var months = ["Janvier", "Frévrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"];
    $scope.selectMonth = {
        value: months[moment().month()],
        months: months
    };

    $scope.selectWeek = {
        value: 0,
        weeks: []
    };

    $scope.yearChanged = function () {
        $scope.updateWeek();
    };
    $scope.monthChanged = function () {
        $scope.updateWeek();
    };


    $scope.updateWeek = function () {
        $scope.selectWeek.weeks = getWeeks($scope.selectYear.value, months.indexOf($scope.selectMonth.value) + 1);
        $scope.selectWeek.value = $scope.selectWeek.weeks[0];
    }
});
kanoteApp.controller("adminGoogleDashboard", function ($scope, $http) {
    $scope.google_api_tokens = {maps: "ok"}
    $scope.tokenModified = false;
    $scope.getTokens = function () {
        $http.get("/api/tokens/google").then(function (rep) {
            $scope.google_api_tokens = rep.data["google_api_tokens"];
        });
    }
    $scope.getTokens();

    $scope.postTokens = function () {
        $http.post("/api/tokens/google", {google_api_tokens: $scope.google_api_tokens}).then(function (rep) {
            Materialize.toast("Tokens updated", 1000);
        }, function () {
            Materialize.toast("Tokens not updated", 1000);
        });
    }


    $scope.cancel = $scope.getTokens;

});

// COACH
kanoteApp.controller('createTrainingCoachCrtl', function ($scope, $http, dialogs) {


    var me = $scope.$parent.me;

    /*States*/
    $scope.isCreatingTraining = false;
    $scope.isTrainingCreated = false;

    $scope.accountsToSync = {};
    $scope.accountsToSync[me.email] =
    {
        name: me.name,
        surname: me.surname,
        isSynced: {kanote: false, calendar: false}
    };

    $scope.newTraining = {
        titre: "",
        date: "",
        lieu: "",
        date_raw: "",
        heure_deb: "",
        heure_fin: "",
        objective: "",
        procede: "sv2",
        milieu: "eau_vive_II",
        type: "slalom_avec_portes",
        globaltype: "",
        heure_deb_raw: "",
        heure_fin_raw: "",
        description: "",

    };

    $scope.$watch(function () {
        return $scope.newTraining.date_raw
    }, function (old, young) {
        try {
            $scope.newTraining.date = moment($scope.newTraining.date_raw, 'DD/MM/YYYY').toDate().toISOString();
        }
        catch (e) {
        }
    });


    $scope.sportmen = [];
    $scope.getSportmen = function () {
        $http.get("/api/sportmen/profile/basic").then(function (rep) {
            $scope.sportmen = rep.data;
            $(document).on("gapi_calendar_loaded", function () {
                $scope.getCalendarInfo();
            });
        });
    }
    function findSportmanByEmail(email) {
        for (var i in $scope.sportmen) {
            var s = $scope.sportmen[i];
            if (s.profile.email == email) return {index: i, sportman: s};
        }
    }

    $scope.getCalendarInfo = function () {
        try {

            gapi.client.calendar.calendarList.list().execute(function (rep) {

                rep.items.forEach(function (item, index) {
                    var sportman = findSportmanByEmail(item.id);
                    if (sportman != null && item.accessRole == "owner") {
                        $scope.$apply(function () {

                            $scope.sportmen[sportman.index].isSynced = true;
                        });
                    }
                });
            });
        }
        catch (e) {
            console.error("Google calendar error : " + e);
        }
    }

    $scope.getSportmen();

    $scope.clickOnSportman = function (i, s) {
        if (s.isSynced) {
            s.isSelected = !s.isSelected;
            $("#sportman-" + s._id).animateCss("bounceIn");
        }
        else {
            Materialize.toast("Pour ajouter ce sportif il faut d'abord synchroniser leur comptes sur google calendar");
        }
    }

    $scope.removeChipSportman = function (index, s) {
        $scope.sportmen[index].isSelected = false;
        //  $("chip-sportman-"+s._id).animateCss("fadeOut");
    }


    $scope.isOneSportmanSelected = function () {
        var res = false;
        $scope.sportmen.forEach(function (s, i) {
            if (s.isSelected) {
                res = true;
            }
        });
        return res;

    }

    $scope.selectSportmen = function () {

    }


    $scope.cancelCreation = function () {
        $scope.$parent.selectMenuItem('sportman_training_book.html');
    };


    function createTrainingsOnKanote(sportmen, cb) {
        $http.post('/api/v2/trainings/create', {
            training: $scope.newTraining,
            sportmen: sportmen,
            google_ids: sportmen_ids
        }).then(function () {
            //$scope.$parent.selectMenuItem("coach_training_book.html");
            for (email_f  in $scope.accountsToSync) {
                $scope.isTrainingCreated = true;
                $scope.accountsToSync[email_f].isSynced.kanote = true;
            }
            cb();
        }, function () {
            cb(true);
            dialogs.triggerError('L\' enregistrement a échoué veuillez verifier que le formulaire soit complet');

        });

    }

    function postOnGoogleCalendarMulti(emails, training, callback) {

        var email = emails.pop();
        if (email != null) {
            postOnGoogleCalendar(email, training, function (e) {
                $scope.$apply(function () {
                    $scope.accountsToSync[email].isSynced.calendar = true;
                });
                postOnGoogleCalendarMulti(emails, training, callback);
            });
        }
        else {
            callback();
        }
    }

    var sportmen_ids = []; // ids of google calendars
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
            'reminders': {
                'useDefault': false,
                'overrides': [
                    {'method': 'email', 'minutes': 24 * 60},
                    {'method': 'popup', 'minutes': 10}
                ]
            },
            "location" : training.lieu || ""
        };
        var request = gapi.client.calendar.events.insert({
            'calendarId': email,
            'resource': event
        });

        request.execute(function (event) {
            sportmen_ids.push({email: email, id: event.id, html_link: event.htmlLink});
            callback(event);

        });

    }


    $scope.createNewTraining = function () {

        $scope.isCreatingTraining = true;
        //Convert jobs
        $scope.newTraining.heure_deb = hourToMin($scope.newTraining.heure_deb_raw);
        $scope.newTraining.heure_fin = hourToMin($scope.newTraining.heure_fin_raw);
        $scope.newTraining.globaltype = typetoGlobalType[$scope.newTraining.type];
        $scope.newTraining.lieu = $("#lieu").val();

        var sportmen = $scope.sportmen.filter(function (s) {
            return s.isSelected;
        }).map(function (s) {
            return s._id;
        });

        $scope.sportmen.filter(function (s) {
            return s.isSelected;
        }).forEach(function (s) {
            $scope.accountsToSync[s.profile.email] =
            {
                name: s.profile.name,
                surname: s.profile.surname,
                isSynced: {kanote: false, calendar: false}
            }
        });
        var basedate = moment(new Date($scope.newTraining.date));
        var date_deb = basedate.clone().add($scope.newTraining.heure_deb, "minutes").toDate();
        var date_fin = basedate.clone().add($scope.newTraining.heure_fin, "minutes").toDate();
        var g_training = {
            summary: $scope.newTraining.titre,
            description: $scope.newTraining.objective,
            start: date_deb,
            end: date_fin,
            color: globaltypeToGoogleColor[$scope.newTraining.globaltype],
            lieu : $scope.newTraining.lieu
        };
        var emails = $scope.sportmen.map(function (s) {
            return s.profile.email;
        });
        emails.push($scope.$parent.me.email);
        postOnGoogleCalendarMulti(emails, g_training, function (err) {
            if (err) {
                Materialize.toast("Erreur de syncronisation Google Calendar ", 2000);
            }
            else {

                createTrainingsOnKanote(sportmen, function () {

                });
            }
        })
    };

    $scope.redirectToTrainingList = function () {
        $scope.$parent.selectMenuItem("coach_training_book.html");
    }

    $scope.checkHours = function () {
        if ($scope.newTraining.heure_deb_raw == "" || $scope.newTraining.heure_fin_raw == "") {
            return true;
        }
        else {
            return hourToMin($scope.newTraining.heure_deb_raw) < hourToMin($scope.newTraining.heure_fin_raw);
        }
    }

});
kanoteApp.controller("sportmanListCrtl", function ($scope, $http, dialogs) {

    $scope.sportmen = [];
    $http.get("/api/sportmen/profile/basic").then(function (rep) {
        $scope.sportmen = rep.data;
    });
    $scope.clickOnSportman = function (sportman) {
        $scope.$parent.selectUser(sportman);
        $scope.$parent.selectMenuItem("coach_sportman_account.html");
    };
});
kanoteApp.controller('coachTrainingBookCrtl', function ($scope, $http) {
    $scope.trainings = [];
    $scope.selectedTraining = {};
    $scope.noMoreTrainings = false;
    var currentIndex = 0;
    $scope.getTrainings = function () {
        $http.get("/api/trainings/me/basic", {params: {block_index: currentIndex}}).then(function (rep) {
            $scope.trainings = $scope.trainings.concat(rep.data);
            $scope.noMoreTrainings = (rep.data || []).length == 0;
            currentIndex++;
        });
    }
    $scope.getTrainings();
    $scope.getDate = function (t) {
        return moment(new Date(t.date)).format("dddd Do MMMM YYYY");
    }
    $scope.globalTypeToImage = function (gbt) {
        return "/img/globaltype/" + globaltypeToImage[gbt];
    };
    $scope.formatHour = function (i) {
        var h = Math.floor(i / 60);
        var m = i % 60;
        m = m < 10 ? "0" + m : m;
        return h + "h" + m;
    }
    $scope.getGlobalTypeColor = function (globaltype) {
        return globaltypeToColor[globaltype];
    };
    $scope.clickOnTraining = function (t) {
        $scope.$parent.selectTraining(t);
        $scope.$parent.selectMenuItem("coach_training_view.html");
    }
    $scope.selectCurrentTraining = function (t) {
        $scope.selectedTraining = t;
        $("#modalRemove").openModal();
    }
    function deleteOnKanote() {
        $http.post('/api/trainings/remove/' + $scope.selectedTraining._id).then(
            function () {
                var id = $scope.selectedTraining._id;
                var index = -1;
                for (i in $scope.trainings) {
                    if ($scope.trainings[i]._id == id) {
                        $scope.training = $scope.trainings.splice(i, 1);
                        break;
                    }
                }
                Materialize.toast('L\'entrainement a été supprimé', 2000);
            },
            function () {
                dialogs.triggerError('Nous n\'vons pas pu supprimer cet entrainment ');
            });

    }

    function removeGmailAndkanote() {
        $http.post("/api/trainings/google_id/all/" + $scope.selectedTraining._id).then(
            function (rep) {
                gapi_extended.removeEventOnGoogleCalendarRecursive(rep.data, function () {
                    deleteOnKanote();
                });
            },
            function () {

            });
    }

    $scope.removeTraining = function () {
        if (!gapi.client.calendar) {
            checkAuth();
            $(document).on("gapi_calendar_loaded", removeGmailAndkanote);

        }
        else {
            removeGmailAndkanote();
        }


    };

    // Button floating
    $scope.createTraining = function () {
        $scope.$parent.selectMenuItem("coach_create_training.html");
    }
});
kanoteApp.controller("coachSportmanAccountCrtl", function ($scope, $http, dialogs) {

    $scope.noMoreTrainings = false;
    $scope.selectedUser = $scope.$parent.selectedUser;
    $scope.years = [2015, 2017];
    $scope.selectYears = {
        value: 0,
        years: []
    }

    $http.get("/api/report/years/" + $scope.selectedUser._id).then(function (rep) {
        $scope.selectYears.years = rep.data.years.sort();
        $scope.selectYears.value = rep.data.years[0];
    });

    $scope.downloadexcel = function () {
        $http.get('/api/report/' + $scope.selectedUser._id, {params: {year: $scope.selectYears.value}}).then(function (rep) {
            open("/report/" + rep.data.file_name);
        });
    };
    $scope.trainings = [];
    var currentIndex = 0;
    $scope.getTrainings = function () {
        $http.get('/api/sportmen/trainings/' + $scope.selectedUser._id, {params: {block_index: currentIndex}}).then(function success(rep) {
            $scope.noMoreTrainings = rep.data.length == 0;
            if (!$scope.noMoreTrainings) {
                $scope.trainings = $scope.trainings.concat(rep.data);

                currentIndex++;
            }
        }, function error(rep) {
        });
    };
    $scope.getTrainings();
    $scope.currentTraining = {};
    $scope.clickOnTraining = function (training) {
        $http.get("/api/sportmen/trainings/full/" + $scope.selectedUser._id + "/" + training._id).then(function (rep) {
            $scope.currentTraining = rep.data;
            $("#trainingView").openModal();
        });

    }
    $scope.translate = function (name) {
        return traduction[name];
    };
    $scope.getGlobalTypeColor = function (globaltype) {
        return globaltypeToColor[globaltype];
    };
    $scope.globalTypeToImage = function (gbt) {
        return "/img/globaltype/" + globaltypeToImage[gbt];
    };
    $scope.getDate = function (t) {
        return moment(new Date(t.date)).format("dddd Do MMMM YYYY");
    }
    $scope.formatHour = function (i) {
        var h = Math.floor(i / 60);
        var m = i % 60;
        m = m < 10 ? "0" + m : m;
        return h + "h" + m;
    }

    $scope.removeTraining = function () {
        $http.post('/api/trainings/remove/' + $scope.currentTraining._id).then(
            function () {
                var id = $scope.currentTraining._id;
                var index = -1;
                for (i in $scope.trainings) {
                    if ($scope.trainings[i]._id == id) {
                        $scope.training = $scope.trainings.splice(i, 1);
                        break;
                    }
                }
                Materialize.toast('L\'entrainement a été supprimé', 2000);
            },
            function () {
                dialogs.triggerError('Nous n\'vons pas pu supprimer cet entrainment ');
            });
    };


    // Trick ng repeat
    $scope.range = function (nb) {
        return new Array(nb);
    }

    // Profile
    $scope.userProfile = {};
    function getUserProfile() {

        $http.get("/api/profile/" + $scope.selectedUser._id).then(function (rep) {
            $scope.userProfile = rep.data;
        });
    }

    getUserProfile();

    $scope.displayed = {
        "Général": {
            "Nom": {path: "surname"},
            "Prénom": {path: "name"},
            "Téléphone": {path: "phone"},
            "Formation": {path: "studies"},
            "Coach club": {path: "coach.club"},
            "Coach eqf": {path: "coach.eqf"},
            "Responsable formation": {path: "resp_studies"},
            "Contact responsable formation": {path: "contact_studies"}
        },
        "Santé": {
            "Taille": {path: "health.height", unit: "cm"},
            "Masse grasse": {path: "health.fat_percentage", unit: "%"},
            "Poids": {path: "health.weight", unit: "kg"},
            "HRV": {path: "health.hrv_test", unit: ""},
        },
        "Matériel": {
            "Bateau": {
                "Modèle": {path: "boat.type"},
                "Recoupe": {path: "boat.recoupe"},
                "Construction": {path: "boat.construction"},
            },
            "Pagaie": {
                "Modèle": {path: "pagaie.type"},
                "Longueur": {path: "pagaie.longueur"},
                "Surface": {path: "pagaie.surface"},
            }
        }

    }

    $scope.pathToVal = function (path) {
        path = path || ""
        var props = path.split(".");
        var res = $scope.userProfile;
        while (props.length > 0 && res != null) {
            res = res[props.shift()];
        }
        return res || "";
    }

    // HRV update section

    $scope.hrvModified = false;
    $scope.updateHRV = function () {
        if ($scope.isHRVValid()) {

            $http.post("/api/profile/health/hrv_test/" + $scope.selectedUser._id, {value: $scope.userProfile.health.hrv_test});
            $scope.hrvModified = false;
        }
        else {
            Materialize.toast("HRV Invalid", 1000);
        }
    }
    $scope.cancelHRV = function () {
        $scope.hrvModified = false;
        getUserProfile();
    };
    $scope.isHRVValid = function () {
        var res = /^[0-9]+$/.test(($scope.userProfile.health || {}).hrv_test);
        return res || true;
        /*!!!!!!!!!! Check disabled for some reasons !!!!!!!!*/
    }
});
kanoteApp.controller("coachTrainingViewCrtl", function ($scope, $http) {

    // States
    $scope.bilanModified = false;
    // Add sportmen modal
    $scope.allSportmen = [];
    $scope.getAllSportmen = function () {

        $http.get("/api/sportmen/profile/basic").then(function (rep) {
            $scope.allSportmen = rep.data;


            // Update sync info on sportmen
            if (!gapi.client.calendar) {

                $(document).on("gapi_calendar_loaded", function () {
                    $scope.getCalendarInfo();
                });
            }
            else {
                $scope.getCalendarInfo();
            }
        });
    }
    $scope.training = {};
    $scope.sportmen = [];
    $scope.feedback = null;
    $scope.hasSportmanBeenClicked = false;
    $scope.selectedSportman;
    $http.get("/api/trainings/me/" + $scope.$parent.selectedTraining._id).then(function (rep) {
        $scope.training = rep.data;

        $scope.training.sportmen.forEach(function (s) {

            $http.get("/api/profile/basic/" + s).then(function (rep_s) {
                var url = "/api/feedback/" + $scope.training._id + "/" + s;
                $http.get(url).then(function (rep_feedback) {
                    rep_s.data.feedback = (rep_feedback.data.trainings || {}).feedback;
                    $scope.sportmen.push(rep_s.data);

                });

            });
        });
        $scope.getAllSportmen();
    });
    $scope.getDate = function (t) {
        return moment(new Date(t.date)).format("dddd Do MMMM YYYY");
    }
    $scope.minToHour = function (mins) {
        var hours = Math.floor(mins / 60);
        var mins = mins - hours * 60;
        return hours + "h" + mins;
    };
    $scope.clickOnSportman = function (sportman) {
        $scope.sportmen.forEach(function (s) {
            s.isSelected = sportman._id == s._id;
        });
        $scope.selectedSportman = sportman;
        var url = "/api/feedback/" + $scope.training._id + "/" + sportman._id;
        $http.get(url).then(function (rep) {
            //$scope.feedback = rep.data.trainings.feedback;
            $scope.feedback = sportman.feedback;
            $scope.hasSportmanBeenClicked = true;
        });
    };
    function removeOnKanoteServer(s) {
        $http.post("/api/trainings/remove/" + $scope.training._id + "/" + s._id).then(function () {
            Materialize.toast("La supression a réussie", 2000);
            $scope.sportmen = $scope.sportmen.filter(function (sp) {
                return sp._id != s._id
            });
            // remove is Already added on current sportman
            $scope.allSportmen.forEach(function (sa, i) {
                $scope.allSportmen[i].isAlreadyAdded = $scope.allSportmen[i].isAlreadyAdded && sa._id != s._id;
            });
        }, function () {
            Materialize.toast("La supression n'a pas réussie", 2000);
        });
    }

    function removeOnGoogleCalendar(sportman, google_calendar_id, callback) {
        gapi.client.calendar.events.delete({
            "eventId": google_calendar_id,
            "calendarId": sportman.profile.email
        }).execute(function (event) {
            callback();
        });
    }

    $scope.deleteSportman = function (s) {
        $http.post("/api/trainings/google_id/" + $scope.training._id + "/" + s._id).then(
            function onSuccess(rep) {
                if (gapi.client.calendar) {
                    removeOnGoogleCalendar(s, rep.data.trainings.google_calendar_id, function () {
                        removeOnKanoteServer(s);
                    });
                }
                else {
                    checkAuth();
                    var handleGapiLoaded = function () {
                        removeOnGoogleCalendar(s, rep.data.google_calendar_id, function () {
                            //  $(document).unbind(handleGapiLoaded); // unbind handler
                            $(this).off("gapi_calendar_loaded");
                            removeOnKanoteServer(s);
                        });
                    };
                    $(document).on("gapi_calendar_loaded", handleGapiLoaded);
                }

            },
            function onError() {
                Materialize.toast("La supression n'a pas réussie", 2000);
            });


    };
    $scope.addSportman = function () {
        if (!gapi.client.calendar) checkAuth();
        $scope.allSportmen.forEach(function (s, i) {

            $scope.sportmen.forEach(function (s1) {
                if (s1._id == s._id) {

                    $scope.allSportmen[i].isAlreadyAdded = true;
                }
            });
        });

        $("#addSportmen").openModal();
    };
    function findSportmanByEmail(email) {
        for (var i in $scope.allSportmen) {
            var s = $scope.allSportmen[i];
            if (s.profile.email == email) return {index: i, sportman: s};
        }
    }

    $scope.getCalendarInfo = function () {
        try {

            gapi.client.calendar.calendarList.list().execute(function (rep) {

                rep.items.forEach(function (item, index) {
                    var sportman = findSportmanByEmail(item.id);
                    if (sportman != null && item.accessRole == "owner") {
                        $scope.$apply(function () {

                            $scope.allSportmen[sportman.index].isSynced = true;
                        });
                    }
                });
            });
        }
        catch (e) {
            console.error("Google calendar error : " + e);
        }
    }
    $scope.selectSportman = function (i, s) {
        if (s.isAlreadyAdded) {
            Materialize.toast(s.profile.name + " " + s.profile.surname + " à déja été ajouté ! ", 2000);
        }
        else if (s.isSynced) {

            s.isSelected = !s.isSelected;
            $scope.sportmanAdded = true;
        }
        else {

            Materialize.toast(s.profile.name + " " + s.profile.surname + " n'est pas synchronisé dans Google Calendar! ", 2000);
        }
    };
    // add  new sportmen to the current training on google calendar
    // and then on kanote app server
    $scope.sportmanAdded = false;
    $scope.updateSportmen = function () {
        $scope.sportmanAdded = false;
        var sportmenToPush = $scope.allSportmen.filter(function (s) {
            return s.isSelected;
        });
        var emails = sportmenToPush.map(function (s) {
            return s.profile.email;
        });
        var gtraining = gapi_extended.craftGoogleCalendarTraining($scope.training);
        gapi_extended.postOnGoogleCalendarMulti(emails, gtraining, function onEnd(gapi_ids) {
            $http.post("/api/trainings/sportmen/add/" + $scope.training._id,
                {
                    sportmen: sportmenToPush.map(function (s) {
                        return s._id;
                    }),
                    training: $scope.training,
                    gapi_ids: gapi_ids

                }
            ).then(
                function onSuccess() {

                },
                function onError() {

                });
        });

        $scope.allSportmen.forEach(function (s, i) {
            // update in google calendar and on kanote server
            if (s.isSelected) {

                s.isAlreadyAdded = true;
                s.isSelected = false;
                $scope.sportmen.push(s);
            }

        });
    }

    $scope.editTraining = function () {
        // Update raw
        $scope.training.date_raw = moment($scope.training.date).format("DD/MM/YYYY");
        $scope.training.heure_deb_raw = minToHour($scope.training.heure_deb);
        $scope.training.heure_fin_raw = minToHour($scope.training.heure_fin);
        $('#editTraining').openModal();
    }
    $scope.updateTraining = function () {
        $http.post("/api/trainings/google_id/all/" + $scope.selectedTraining._id).then(
            function (rep) {
                var basedate = moment(new Date($scope.training.date));
                $scope.training.date_deb = hourToMin($scope.training.heure_deb_raw);
                $scope.training.date_fin = hourToMin($scope.training.heure_fin_raw);

                var gtraining = gapi_extended.craftGoogleCalendarTraining($scope.training);
                gapi_extended.updateEventOnGoogleRecursive(rep.data || [], gtraining, function () {

                });
            },
            function () {

            });


    }

    // update coach bilan on kanote Servers

    $scope.updateBilan = function () {
        $http.post("/api/trainings/bilan/" + $scope.training._id, {bilan: $scope.training.bilan}).then(function () {
            Materialize.toast("Le bilan a été mis a jour", 1000);
            $scope.bilanModified = false;
        }, function () {
            Materialize.toast("Le bilan n'a pas été mis a jour", 1000);
        });
    }
    $scope.cancelBilan = function () {
        $scope.bilanModified = false;
        $scope.training.bilan = "";
    }
    $scope.range = function(num) {
        return new Array(num);
    }

});
kanoteApp.controller("coachExcelReport", function ($scope, $http) {
    $scope.yearsSelect = {years: [], year: 0};

    $http.get("/api/report/years").then(function (rep) {
        $scope.yearsSelect.years = rep.data.years;
        $scope.yearsSelect.year = rep.data.years.sort()[0];
    });
    $scope.yearSelected = false;

    $scope.downloadExcel = function () {
        $http.get("/api/report", {params: {year: $scope.yearsSelect.year}}).then(function (rep) {
            window.open("/report/" + rep.data.file_name);
        })
    }
})


// COACH + ADMIN

kanoteApp.controller('coachAdminAccountCrtl', function ($scope, $http) {

    $scope.isEditingProfilePicture = false;

    $scope.role = $scope.$parent.me.role;
    $scope.modified = {
        email: false,
        password: false,
        profile: false
    }

    $scope.me = {};

    $scope.getMe = function () {
        $http.get("/api/profile/me").then(function (rep) {
            $scope.me = rep.data;
        });
    };

    $scope.getMe();
    $scope.id_card =
        [
            {name: "Entraineur club", path: "coach.club"},
            {name: "Entraineur EQF", path: "coach.eqf"},
            {name: "Entraineur EQF", path: "coach.eqf"},
        ];

    $scope.modifying = false;

    $scope.cancel = function () {
        $scope.getMe();
        $scope.modifying = false;
    };

    $scope.save = function () {
        $http.post("/api/profile/update/", $scope.me).then(function () {
            Materialize.toast("Profil mis a jour !", 2000);
        }, function () {
            Materialize.toast("Echec de la mise a jour du profil", 2000);
        });
        if ($scope.modified.email) {
            $http.post("/api/profile/email/update/", {email: $scope.me.email}).then(function () {
                Materialize.toast("Email mis a jour !", 2000);
            }, function () {
                Materialize.toast("Echec de la mise a jour de l'email verifiez qu'il s'agit d'une adresse gmail", 2000);
            });
        }
        if ($scope.modified.password) {
            $http.post("/api/profile/password/update/", {password: $scope.password}).then(function () {
                Materialize.toast("Mot de passe  mis a jour !", 2000);
            }, function () {
                Materialize.toast("Echec de la mise a jour du mot de passe", 2000);
            });
        }
        if ($scope.modified.health) {
            $scope.updateAllHealthItems();
        }
        $scope.modifying = false;
        reset();
    };

    function reset() {
        $scope.modified = {
            email: false,
            password: false,
            health: false

        };
        $scope.getMe();
    };

    //$scope.password={"old" : "" , "new" : ""};


    // Health
    $scope.healthItems = ["fat_percentage", "weight", "height"];
    $scope.updateHealthItem = function (item) {
        $http.post("/api/profile/health/" + item, {value: $scope.me.health[item]}).then(function success(rep) {
            //    Materialize.toast(traduction[item] + " à été mis à jour ",2000);
        }, function error(rep) {
            Materialize.toast(traduction[item] + " n'as pas été mis à jour ", 2000);
        });
    }
    $scope.updateAllHealthItems = function () {
        $scope.healthItems.forEach(function (i) {
            $scope.updateHealthItem(i);
        });
    }

    $scope.editProfilePicture = function () {
        $("#fileInput").click();
    }


    // NG CROP
    $scope.loadingImage = false;
    $scope.myImage = '';
    $scope.myCroppedImage = '';

    $scope.uploadReady = false;

    var handleFileSelect = function (evt) {
        $scope.uploadReady = false;
        $scope.loadingImage = true;

        $scope.isEditingProfilePicture = true;
        var file = evt.currentTarget.files[0];
        var reader = new FileReader();
        reader.onload = function (evt) {
            $scope.$apply(function ($scope) {
                $scope.myImage = evt.target.result;

            });
        };
        reader.readAsDataURL(file);
    };
    $("#fileInput").on('change', handleFileSelect);

    ///  var cropper = {};
    $scope.imageLoaded = function () {
        var image = document.getElementById('image');
        cropper = new Cropper(image, {
            aspectRatio: 1,
            crop: function (e) {
            }
        });
        $scope.uploadReady = true;
        $scope.loadingImage = false;
    }

    $scope.cancelUploadImage = function () {
        $scope.isEditingProfilePicture = false;
        $scope.myImage = "";
        cropper.destroy();
    }
    $scope.uploadImage = function () {
        var dataBase64 = cropper.getCroppedCanvas({height: 512, width: 512}).toDataURL();
        $scope.$parent.me.pictures.profile = dataBase64;
        $scope.me.pictures.profile = dataBase64;
        $http.post("/api/profile/pictures/profile", {data: dataBase64}).then(function () {
            Materialize.toast("Photo de profil mise a jour", 1000);
            // reset state
            $scope.isEditingProfilePicture = false;
            $scope.myImage = "";
            cropper.destroy();
        }, function () {
            Materialize.toast("Erreur mise a jour de la photo de profil", 1000);
        });
    }


});