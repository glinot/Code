var isSmartphone = /Mobi/.test(navigator.userAgent)


function goToPage(name) {
    window.location.hash = "#" + (name || "").replace(".html", "");
}


/*
 * ===== DEBUG ONLY  ===
 * */


var get = function (url, params) {
    $.get(url, params, function (data) {
        console.log(data);
    });
};


/*
 * ============= Variables dans LocalStorage ==============
 *
 *-----> Set on login
 *       name  : user name
 *      surname : user surname
 *      role  : user role
 *      sportman_id : si on est sur un coach et qu'on check le profil d'un sportman
 *
 *
 *
 *
 *
 * */



/* Variables globales */

var dragging = false;
var leftClickDown = true;
var menuActive = false;

/* Permet de ne pas utiliser le cache des requetes ajax get post etc */
$.ajaxSetup({
    cache: false
});

/* Cette fonction permet d'ajouter le texte présent sur la page 'URL' dans le container d'id 'ELEMENT'
 * @url : fichier en .html ( ex : "profil.html" )
 * @element : id d'une division définie dans la page html, précédé d'un # ( ex : "#main-container" ) */
function loadInElem(url, element) {
    $.get(url, function (data) {
        $(element).html(data);
    });
}

/* Charge la page des entraînements ("training.html") par défaut dans le corps de notre site ("#main-container") */
$(document).ready(function () {
    goToAnchor();
});

/* Permet d'afficher ou non le menu présent sur la gauche
 * @side-wrapper : menu présent sur le côté gauche
 * @mobile-menu : les 3 petites-barres présentes en haut à gauche
 * @dark-div : permet d'assombrir la page
 * Cas 1 : on affiche le menu, assombrit la page et met en avant le bouton du menu (configuration choix de la page)
 * Cas 2 : on fais disparaître le menu, on remet la page sous la bonne couleur et on met en retrait le bouton du menu (configuration initiale) */
function toggleSideMenu() {
    $("#side-wrapper").toggleClass("hidded");
    $("#mobile-menu").toggleClass("active");
    $("#dark-div").toggleClass("hidded");
    menuActive = !menuActive;
}

/* Quand on clique sur la partie de l'écran assombrie (lorsque le menu est ouvert), alors le menu se referme et le fond noir disparaît */
$("#dark-div").click(function () {
    $("#dark-div").addClass("hidded");
    $("#side-wrapper").addClass("hidded");
    $("#mobile-menu").addClass("active");
});

/* Appelle la fonction toggleSideMenu() lors d'un click sur les 3 barres en haut à gauche de l'écran
 * => Ouvre le menu lorsque l'on click en haut à gauche */
$(".burger-menu").click(function () {
    toggleSideMenu();
});

/* Lors d'un click sur un élément de la liste ".side-menu", (donc profil/statistiques/création de séance...)
 * Permet de charger la page correspondante dans notre "#main-container", tout en appelant toggleSideMenu()
 * Des cas d'erreurs ont été définies, même si ils n'arrivent jamais
 * Un click sur la photo de profil ammène sur le profil */
$(".side-menu > li.user-feature , .side-menu > li.profile-section").click(function () {
    localStorage.sportman_id = "";
    var group = $(this).attr("data-toggle-group");
    if (group == null) return;
    $("[data-toggle-group^=" + "'" + group + "']").removeClass("active");
    $(this).addClass("active");
    var target = $(this).attr("data-fragement-target");
    if (target != null) {
        toggleSideMenu();
        // loadInElem(target, "#main-container");
        window.location.hash = "#" + target.replace(".html", "");
    } else {
        console.warn("no data-fragement-target");

    }
});


/* Détecte lors qu'il y a un "dragging" */
$(window).keydown(function () {
    leftClickDown = true;
});
$(window).mousemove(function () {
    if (leftClickDown) dragging = true;
});
$(window).keyup(function () {
    leftClickDown = false;
    dragging = false;
});

/* Lorsque l'on glisse le doigt/click de la souris puis déplacement sur le côté, alors on appelle toggleSideMenu()
 * Si le menu n'est pas actif, glisser vers la droite ne fait rien */
$(window).swipe({
    swipe: function (event, direction, distance, duration, fingerCount) {
        if (menuActive && direction == "left") {
            toggleSideMenu();
        }
    }
});

/* On se rend à la page d'accueil (ici sportman_training_book) */

function defaultAnchor() {
    if (localStorage.role == "admin") {
        loadInElem("admin_user_list.html", "#main-container");
    }
    else if (localStorage.role == "coach") {
        loadInElem("coach_training_list.html", "#main-container");
    }
    else if (localStorage.role == "sportman") {
        loadInElem("sportman_training_book.html", "#main-container");
    }
}
function goToAnchor() {
    var a = window.location.hash.replace('.html', '');
    if (a) {
        a = a.replace("#", '') + ".html";
        loadInElem(a, "#main-container");

        /* var b = $("li[data-fragement-target=\"" + a + "\"]");
         if (b.length > 0) {
         loadInElem(a, "#main-container");
         } else {
         defaultAnchor();
         }
         */
    } else {
        defaultAnchor();
    }

    $("#main-container").animateCss("fadeIn");
}


// what the fucking fuck function

document.body.addEventListener('touchmove', function (event) {
    event.stopImmediatePropagation();

}, false);


// show user features
$("[level='" + localStorage.role + "']").show();

//set name and surname
$.get("/api/profile/me", function (profile) {
    $("#lm-user-image").attr("src", profile.pictures.profile);
    $("#lm-user-name").text(profile.name);
    $("#lm-user-surname").text(profile.surname);
});


//disconnect event
$("#disconnect").click(function () {
    $.cookie('session_id', null);
    window.location = './login.html';
});

function checkSession() {

    setTimeout(function () {
        $.get("/api/auth/is_valid", function (data) {
            if (!data.valid) {
                window.location = "/login.html";
            }
            else {
                checkSession();
            }
        });
    }, 5000);
}
//checkSession();
var nbStar = 0;

$("#modal-feel .rating-section > i ").click(function () {
    var index = $(this).attr("index");
    nbStar = index;
    var stars = $("#modal-feel .rating-section > i ");
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


$("#modal-feel .submit-btn").click(function () {
    $("#modal-feel").modal("hide");
    $.post("/api/mood", {"indice": nbStar}).done(
        function () {
            //console.log("mood updated");
        }
    );
});
if (localStorage.role == "sportman") {
    // check

    $.get("/api/mood").fail(function () {
        $("#modal-feel .sportman-name").text(localStorage.name);
        $("#modal-feel").modal("show");
    }).done(function (data) {
    });

}


// moment initialisation


moment.locale("fr");


// handle back
$(window).on('hashchange', goToAnchor);
$.fn.scrollTo = function( target, options, callback ){
    if(typeof options == 'function' && arguments.length == 2){ callback = options; options = target; }
    var settings = $.extend({
        scrollTarget  : target,
        offsetTop     : 50,
        duration      : 500,
        easing        : 'swing'
    }, options);
    return this.each(function(){
        var scrollPane = $(this);
        var scrollTarget = (typeof settings.scrollTarget == "number") ? settings.scrollTarget : $(settings.scrollTarget);
        var scrollY = (typeof scrollTarget == "number") ? scrollTarget : scrollTarget.offset().top + scrollPane.scrollTop() - parseInt(settings.offsetTop);
        scrollPane.animate({scrollTop : scrollY }, parseInt(settings.duration), settings.easing, function(){
            if (typeof callback == 'function') { callback.call(this); }
        });
    });
}



$.fn.extend({
    animateCss: function (animationName) {
        var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
        $(this).addClass('animated ' + animationName).one(animationEnd, function() {
            $(this).removeClass('animated ' + animationName);
        });
    }
})