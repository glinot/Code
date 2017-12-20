/**
 * Created by Hugues on 08/05/2016.
 */
function fillTraining(){
    $.get("api/trainings/me/"+localStorage.id_training, function (data){
        var objective = data.feedback.eval_objective;
        for(var i = 0; i<=objective; i++) {
            $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<5 ; i++){
            $('.rating-section.objective').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
        }
        var sensations = data.feedback.eval_sensations;
        for(var i = 0; i<=sensations; i++) {
            $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<7 ; i++){
            $('.rating-section.sensations').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
        }
        var humeur = data.feedback.eval_fatigue;
        for(var i = 0; i<=humeur; i++) {
            $('.rating-section.humeur').append("<i index=\"0\" class=\"fa fa-star\" aria-hidden=\"true\"></i>");
        }
        for(i; i<7 ; i++){
            $('.rating-section.humeur').append("<i index=\"0\" class=\"fa fa-star-o\" aria-hidden=\"true\"></i>");
        }
        $('#bilan_seance').text(data.feedback.objectives_text);
        $('#temps_travail_intensite').text(data.feedback.temps_travail_intensite);
        $('#nb_portes_franchies').text(data.feedback.nb_portes_franchies);
        $('#nb_erreurs').text(data.feedback.nb_erreurs);
        $('#nb_parcours_realises').text(data.feedback.nb_parcours_realises);
        $('#nb_parcours_0').text(data.feedback.nb_parcours_a_zero);
        $('#nb_penalites_seance').text(data.feedback.nb_penalites_seance);
        $('#nb_kilometres').text(data.feedback.nb_km);
        $('#fc_max').text(data.feedback.fc_max);
        $('#fc_moyenne').text(data.feedback.fc_moyenne);
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

fillTraining();