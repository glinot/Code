/**
 * Created by grego on 19/05/2016.
 */



var tour = new Tour({onEnd : function(){
    $("#tr-welcome").remove();
    $.post("/api/tour/validate");
}});
var template =

    "<div class='popover tour'>" +
    "<div class='arrow'></div>" +
    "<div class='popover-header' ><h3 class='popover-title'></h3></div>" +
    " <div class='popover-content'></div>" +
    "<div class='popover-navigation'>" +
    "<button class='btn btn-default' data-role='prev'>« Prec</button>" +
    "<span data-role='separator'> </span>" +
    " <button class='btn btn-default' data-role='next'>Suiv »</button>" +
    "<span data-role='separator'>&nbsp;&nbsp;</span>" +
    "<button class='btn btn-default' data-role='end'>Stop</button>";

var template_welcome =

    "<div class='popover tour'>" +
    "<div class='arrow'></div>" +
    "<div class='popover-header' ><h3 class='popover-title'></h3></div>" +
    " <div class='popover-content'></div>" +
    "<div class='popover-navigation'>" +
    " <button class='btn btn-default' data-role='next'>Suiv »</button>" +
    "<span data-role='separator'>&nbsp;&nbsp;</span>" +
    "<button class='btn btn-default' data-role='end'>Stop</button>";


tour.addStep({
    element: "#tr-image-welcome",
    backdrop: true,
    backdropContainer: "#tr-welcome",
    title: "Salut !",
    content: " C'est moi Paul Biviccii le déglingo, laissez moi vous montrer comment fonctionne KaNote",
    template: template_welcome,
    animation: true,
    onShow: function () {
        $("body").append("<div id='tr-welcome' ><img id='tr-image-welcome' class='img-circle' src='/img/paul.jpg'/></div>");
    },
    onNext: function () {
        $("#tr-welcome").fadeOut(500).remove();
    }
});
tour.addStep({
    element: "#lm-user-image",
    title: "Profile",
    content: "Cliquez içi pour afficher votre profil",
    backdrop: true,
    backdropContainer: "#tr-profile",
    onShow: function () {
        $("#mobile-menu").click();
    },
    onNext: function () {
        $("#lm-user-image").click();
    }
    ,
    template: template
});
tour.addStep({
    element: "#tr-basic",
    title: "Informations Basiques",
    backdrop: true,
    backdropContainer: "#main-container",
    content: "Vous pouvez retrouver dans cette section les information basiques vous concernant",
    onShow: function () {
        $("#tr-basic").click();
    }

    ,
    template: template
});

tour.addStep({
    element: "#tr-modify-password",
    title: "Mot de passe",
    backdrop: true,
    backdropContainer: "#tr-modify-password",
    content: "Pour commencer changez votre mot de passe ",
    onNext: function () {
        $("#modify-feature").on("hidden.bs.modal", function () {
            tour.next();
            $(this).unbind('click', arguments.callee);

        });
    },


    template: template
});

tour.addStep({
    element: "#tr-sante",
    title: "Les infos Santé",
    content: "Vous pouvez retrouver et modifier içi vous infos relatives à la santé",
    onShow: function () {
        $("#tr-sante").click();
    },
    onNext: function () {
        toggleSideMenu();
    },
    template: template
});
tour.addStep({
    element: "#tr-training-book",
    title: "Carnet d'entrainement",
    content: "Dans cet onglet retrouvez tout vos entrainements !",
    onNext: function () {
        $("#tr-training-book").click();

    },
    template: template
});
tour.addStep({
    element: "#main-container",
    title: "Entrainement",
    content: '<div><img class="img-circle" src="/img/tr-yes.png"> Indique que vous avez rempli la scéance</div>'
    + '<div><img class="img-circle" src="/img/tr-no.png"> Indique que vous n\'avez pas rempli la scéance</div>',
    onNext: function () {
        $(".user-feature.active").removeClass("active");
        $("#tr-stats").addClass("active");
        toggleSideMenu();
    }
    ,
    template: template

});
tour.addStep({
    element: "#tr-stats",
    title: "Stats",
    content: "Dans cet onglet retrouvez tout vos statistiques !",
    onNext: function () {
        $("#tr-stats").click();

    },
    template: template
});
tour.addStep({
    element: "#main-container",
    title: "Récapitulatif",
    content: "En cliquant sur la vignette vous pouvez visualiser vos stats personnelles",
    onNext: function () {
        $("#tr-month-overview").click();
    },
    template: template
});
tour.addStep({
    element: "#tr-stats-next",
    title: "Temps",
    content: "Voyagez dans le temps avec les chevrons",
    onNext: function () {
        $(".stats-previous").click();


    },
    template: template
});
tour.addStep({
    element: "#tr-create-training",
    title: "Creer un scéance",
    content: "En cliquant sur cet onglet on peut creer un nouvelle scéance",
    onShow: function () {
        $(".user-feature.active").removeClass("active");
        $("#tr-create-training").addClass("active");
        toggleSideMenu();
    },
    template: template
});


$.get("/api/tour/is_validated", function (data) {
    if (!data.tour_is_done) {
        tour.init();
        tour.setCurrentStep(0);
        tour.start(true);
    }
});
