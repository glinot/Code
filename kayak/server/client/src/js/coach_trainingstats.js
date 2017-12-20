$.get("/api/trainings/me/" + localStorage.selectedTraining, function (data) {
    data.sportmen.forEach(function (s) {
        $.get("/api/profile/basic/" + s, function (sportman) {
            var s = $("#model-sportman").clone(true, true).attr("id", sportman._id).removeClass("model");
            $(s).find(".user-img").attr('src', sportman.profile.pictures.profile);
            $(s).find(".user-name-text").text(sportman.profile.name);
            $(s).find(".user-surname-text").text(sportman.profile.surname);
            $("#sportman-list").append(s);
        });
    });
    var date = new Date(data.date);
    $("#training-date").text(date.toLocaleDateString());
    $("#training-type").text(traduction[data.type]);
    $("#training-debut").text(minutesToHM(data.heure_deb));
    $("#training-delay").text((data.heure_fin - data.heure_deb) + " min");
    $("#training-description").text(data.objective);
    $("#training-moyen").text(data.moyen);
    $("#training-objective").text(data.objective);
});

$("#sportman-list > li ").click(function () {
    localStorage.sportman_id = $(this).attr("id");
    fillTraining();
});

function fillTraining(){
    $.get("/api/feedback/" + localStorage.id_training + "/" + localStorage.sportman_id , function (data){
        console.log(localStorage.id_training);
        console.log(localStorage.sportman_id);
        if(data.trainings && data.trainings[0].filled == true) {
            $("#feedback_sportman").removeClass("hidden");
            $("#no_feedback").addClass("hidden");
            $('.rating-section.objective, .rating-section.sensations, .rating-section.humeur').html("");
            var objective = data.trainings[0].feedback.eval_objective;
            for (var i = 0; i <= objective; i++) {
                $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
            }
            for (i; i < 5; i++) {
                $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
            }
            var sensations = data.trainings[0].feedback.eval_sensations;
            for (var i = 0; i <= sensations; i++) {
                $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
            }
            for (i; i < 7; i++) {
                $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
            }
            var humeur = data.trainings[0].feedback.eval_fatigue;
            for (var i = 0; i <= humeur; i++) {
                $('.rating-section.humeur').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
            }
            for (i; i < 7; i++) {
                $('.rating-section.humeur').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
            }
            $('#bilan_seance').text(data.trainings[0].feedback.objectives_text);
            $('#temps_travail_intensite').text(data.trainings[0].feedback.temps_travail_intensite);
            $('#difficulte').text(data.trainings[0].feedback.difficulte);
            $('#nb_portes_franchies').text(data.trainings[0].feedback.nb_portes_franchies);
            $('#nb_erreurs').text(data.trainings[0].feedback.nb_erreurs);
            $('#nb_parcours_realises').text(data.trainings[0].feedback.nb_parcours_realises);
            $('#nb_parcours_0').text(data.trainings[0].feedback.nb_parcours_a_zero);
            $('#nb_penalites_seance').text(data.trainings[0].feedback.nb_penalites_seance);
            $('#nb_kilometres').text(data.trainings[0].feedback.nb_km);
            $('#fc_max').text(data.trainings[0].feedback.fc_max);
            $('#fc_moyenne').text(data.trainings[0].feedback.fc_moyenne);
            $('#title_return').text(data.trainings[0].titre);
            $('#date_return').text(new Date(data.trainings[0].date).toLocaleDateString());
            $('#horaire_deb_return').text(minutesToHM(data.trainings[0].heure_deb));
            $('#horaire_fin_return').text(minutesToHM(data.trainings[0].heure_fin));
            $('#objectives_return').text(data.trainings[0].objective);
            $('#procede_return').text(traduction[data.trainings[0].procede]);
            $('#milieu_return').text(traduction[data.trainings[0].milieu]);
            $('#moyen_return').text(traduction[data.trainings[0].type]);
        } else {
            console.log("non");
            $("#feedback_sportman").addClass("hidden");
            $("#no_feedback").removeClass("hidden");
        }
    });
}
