$("#training-list > li").click(function () {
    localStorage.selectedTraining = $(this).attr('id');
    window.location.hash = "#coach_training_stats";
});


$.get("/api/trainings/me/basic", function (trainings) {
	trainings.forEach(function (training) {
		var date = moment(new Date(training.date));
		var t = $("#model-training")
			.clone(true, true)
			.removeClass("model")
			.attr("style", "background-color: "+trainingbookColor[globaltypeToColor[training.globaltype]]+";")
			.attr("id", training._id);
		$(t).find(".training-date").text(date.format("DD/MM/YYYY"));
		$(t).find(".training-heure").text(minutesToHM(training.heure_deb));
		$(t).find(".training-type").text(traduction[training.type] || "erreur");
		$("#training-list").append(t);
	});
});


