var nbStar_eval = 2 ;
var nbStar_sensations = 3;
var nbStar_humeur = 3;

fillInfo();

function fillInfo(){
    $.get("api/trainings/me/"+localStorage.id_training, function (data){
        $('#title_return').text(data.titre);
        $('#date_return').text(new Date(data.date).toLocaleDateString());
        $('#horaire_deb_return').text(minutesToHM(data.heure_deb));
        $('#horaire_fin_return').text(minutesToHM(data.heure_fin));
        $('#objectives_return').text(data.objective);
        $('#procede_return').text(traduction[data.procede]);
        $('#milieu_return').text(traduction[data.milieu]);
        $('#moyen_return').text(traduction[data.type]);
    });
}

$(".rating-section.objective > i ").click(function () {
    var index = $(this).attr("index");
    nbStar_eval = index;
    var stars = $(".rating-section.objective > i ");
    stars.each(function (i, elem) {
        if (i <= index) {
            $(elem).addClass("fa-star").removeClass("fa-star-o");
        }
        else {
            $(elem).removeClass("fa-star").addClass("fa-star-o");
        }
    })
    ;
});

$(".rating-section.humeur > i ").click(function () {
    var index = $(this).attr("index");
    nbStar_humeur = index;
    var stars = $(".rating-section.humeur > i ");
    stars.each(function (i, elem) {
        if (i <= index) {
            $(elem).addClass("fa-star").removeClass("fa-star-o");
        }
        else {
            $(elem).removeClass("fa-star").addClass("fa-star-o");
        }
    })
    ;
});

$(".rating-section.sensations > i ").click(function () {
    var index = $(this).attr("index");
    nbStar_sensations = index;
    var stars = $(".rating-section.sensations > i ");
    stars.each(function (i, elem) {
        if (i <= index) {
            $(elem).addClass("fa-star").removeClass("fa-star-o");
        }
        else {
            $(elem).removeClass("fa-star").addClass("fa-star-o");
        }
    });
});

$(".btn-success ").click(function () {

    var eval_objective = nbStar_eval;
    var eval_sensations = nbStar_sensations;
    var eval_fatigue = nbStar_humeur;
    var objectives_text = $('#objectives_text').val();
    var temps_travail_intensite = $('#temps_travail_intensite').val();
    var nb_portes_franchies = $('#nb_portes_franchies').val();
    var nb_erreurs = $('#nb_erreurs').val();
    var nb_parcours_realises = $('#nb_parcours_realises').val();
    var nb_parcours_a_zero = $('#nb_parcours_a_zero').val();
    var nb_penalites_seance = $('#nb_penalites_seance').val();
    var nb_km = $('#nb_km').val();
    var fc_moyenne = $('#fc_moyenne').val();
    var fc_max = $('#fc_max').val();
    var training_return = {
        "filled": "true",
        "eval_objective": eval_objective,
        "eval_sensations": eval_sensations,
        "eval_fatigue": eval_fatigue,
        "objectives_text": objectives_text,
        "temps_travail_intensite": temps_travail_intensite,
        "nb_portes_franchies": nb_portes_franchies,
        "nb_erreurs": nb_erreurs,
        "nb_parcours_realises": nb_parcours_realises,
        "nb_parcours_a_zero": nb_parcours_a_zero,
        "nb_penalites_seance": nb_penalites_seance,
        "nb_km": nb_km,
        "fc_moyenne": fc_moyenne,
        "fc_max": fc_max
    };
    console.log(training_return);
    var loc_id = localStorage.id_training;
    $.post("/api/trainings/fill/"+loc_id, {"training": training_return});
    window.location.hash = "sportman_training_book";
});