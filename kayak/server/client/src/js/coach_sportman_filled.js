function fillTraining(){
    $.get("/api/feedback/" + localStorage.id_training + "/" + localStorage.sportman_id , function (data){
        var objective = data.trainings[0].feedback.eval_objective;
        for(var i = 0; i<=objective; i++) {
            $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<5 ; i++){
            $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
        }
        var sensations = data.trainings[0].feedback.eval_sensations;
        for(var i = 0; i<=sensations; i++) {
            $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<7 ; i++){
            $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
        }
        var humeur = data.trainings[0].feedback.eval_fatigue;
        for(var i = 0; i<=humeur; i++) {
            $('.rating-section.humeur').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<7 ; i++){
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
    });
}

fillTraining();