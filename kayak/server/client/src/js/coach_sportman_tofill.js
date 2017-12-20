function fillTraining(){
    $.get("/api/feedback/" + localStorage.id_training + "/" + localStorage.sportman_id , function (data){
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