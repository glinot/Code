/* On check si la session est valide auprès du serveur si elle ne l'est pas on redirige sur la page de login */

var isSmartphone = /Mobi/.test(navigator.userAgent)



var loginState = "login"

function updateView() {
    if (loginState == "login") {
        $("[group='register']").fadeOut().hide();
        $("[group='login']").fadeIn().show();

    }
    else {
        $("[group='login']").fadeOut().hide();
        $("[group='register']").fadeIn().show();
    }
}

function switchState() {
    loginState = loginState == "login" ? "register" : "login";
    updateView();
}
function checkAuth() {
    $.get("/api/auth/check_session", {session_id: $.cookie("session_id")}).done(function (data) {
        if (!data.is_valid) {
            console.log("not valid session ");
            document.location.href = "/login.html";
        }
        else {
            console.log(" valid session ");
            document.location.href = "/index.html";
        }
    });
}

$(function () {
    $('#login-form-link').click(function (e) {
        $("#login-form").delay(100).fadeIn(100);
        $("#register-form").fadeOut(100);
        $('#register-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });
    $('#register-form-link').click(function (e) {
        $("#register-form").delay(100).fadeIn(100);
        $("#login-form").fadeOut(100);
        $('#login-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });
});

$("#login-submit").click(function () {
    $.get("/api/auth/login/", {
        password: $("#password").val(),
        login: $("#username").val()
    }).fail(function (err) {
        console.log("error");
        $("#last-error").text("Erreur dans l'email ou le mot de passe")
    }).done(function (data) {
        var rep = data;
        if (rep.success) {
            // set  the mother fucking cookie
            $.cookie("session_id", rep.session_id, {expires: 365/2});

            localStorage.email = rep.email;
            localStorage.name = rep.name;
            localStorage.surname = rep.surname;
            localStorage.role = rep.role;
            localStorage.sportman_id = "";


            // redirect
            document.location.href = "/index.html";
        }
    });
});


if(isSmartphone){

}
else{

    $('#background').YTPlayer({
        fitToBackground: false,
        videoId: 'D3NZ45e9llI',
        playerVars: {
            modestbranding: 0,
            autoplay: 0,
            controls: 1,
            showinfo: 0,
            branding: 0,
            rel: 0,
            autohide: 0,
            start: 59
        }
    });
}


updateView();

$("#login-register , #register-login").click(function () {
    switchState();
});



$("#username").keyup(function(event){


    if(event.which == 1300 ){ // entrée
        if(!$("#username").text().endsWith("@gmail.com")){
     
        }
    }
});
$("#register-register").click(function () {


    var email = $("#username").val();
    var password = $("#password").val();
    var name  = $("#register-name").val();
    var surname  = $("#register-surname").val();
    var role  = $("#role").val();


    $.post("/api/users/register" , {"role" : role,"email": email, "password": password, "name": name, "surname": surname} ,trigger_success ).fail(trigger_error);

});

$("#username,#password").keypress(function(e){
   if(e.which == 13){
       $("#login-submit").click();
   }
});




function trigger_error(data){
    console.warn(data);

    $("#info-login").modal("show");
    $("#info-login #titre").text("Erreur");
    $("#info-login #message").text((data.responseJSON || {}).error || "Erreur");

}
function trigger_success(data){
    $("#info-login").modal("show");
    $("#info-login #titre").text("Succès");
    $("#info-login #message").text("Vous avez été enregistré, il faut attendre la décision de l'administatrateur");

}