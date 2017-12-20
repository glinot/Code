function getTrainingByBlock(nb) {
    $.get("/api/sportmen/trainings/"+localStorage.sportman_id, {block_index: nb}, function (trainings) {
        trainings.forEach(function (training) {
            var date = moment(new Date(training.date));
            var t = $("#model-training")
                .clone(true, true)
                .removeClass("model")
                .css("background-color",trainingbookColor[globaltypeToColor[training.globaltype]])
                .attr("id", training._id);
            $(t).find(".globaltype").attr("src", "/img/globaltype/"+globaltypeToImage[training.globaltype])
            $(t).find(".training-date").text(date.format("DD/MM/YYYY"));
            $(t).find(".training-heure").text(minutesToHM(training.heure_deb));
            $(t).find(".training-type").text(traduction[training.type] || "erreur");
            $(t).find(".checkbox").addClass((training.filled=="true" |   training.filled ) ? "active" : "disabled");
            if(training.filled && training.feedback){
                var object = training.feedback.eval_objective;
                var sensa = training.feedback.eval_sensations;
                var fati = training.feedback.eval_fatigue;
                var value = ((object/4)+((fati/6+sensa/6)/2))*5/2;
                if(value==0){
                    value++;
                }
                var src = '../img/smiley'+Math.ceil(value)+'.png';
                console.log(src);
                $(t).find("#smiley").attr("src", src);
            }else{
                $(t).find("#smiley").attr("src", '../img/blank.png');
            }
            $(t).attr("filled", training.filled);
            $("#training-list").append(t);
        });
    });
}
var index = 0;

$("#button-more-training").click(function () {
    getTrainingByBlock(++index);
});

getTrainingByBlock(0);

$("#training-list > li ").click(function () {
    localStorage.id_training = $(this).attr("id");
    if ($(this).attr("filled") == "true") {
        loadInElem("coach_sportman_training_filled.html", "#home");
    } else {
        loadInElem("coach_sportman_training_tofill.html", "#home");
    }
});