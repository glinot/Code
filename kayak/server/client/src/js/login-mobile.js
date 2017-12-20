/**
 * Created by grego on 07/05/2016.
 */

$(".register-feature").hide();
var register = false;
$("#register").click(function () {
    if (register) {
        $(".register-feature").hide();
        $("#validation_button").text("Se connecter");
        $(this).text("S'enregistrer");
    }
    else {

        $("#validation_button").text("S'enregistrer");
        $(this).text("Se connecter");
        $(".register-feature").show();
    }
    register = ! register;
});

$("#validation_button").click(function () {



    var password= $("#password").val();
    var email= $("#password").val();
    var name= $("#password").val();
    var surname= $("#password").val();
    var role= $("#role").val();


    if(register){
        $.post("/api/users/register" , {"role" : role,"email": email, "password": password, "name": name, "surname": surname} ).fail(function(){
            $("#modal-messsage-box .modal-title").text("Erreur");
            $("#modal-messsage-box #message").text("Erreur d'enregistrement");
            $("#modal-messsage-box").modal("show");
        }).done(function(){
            $("#modal-messsage-box .modal-title").text("Succès");
            $("#modal-messsage-box #message").text("L'enregsitrement a réussi");
            $("#modal-messsage-box").modal("show");
        });
    }
    else{
        $.get("/api/auth/login/", {
            password: $("#password").val(),
            login: $("#email").val()
        }).fail(function (err) {
            $("#modal-messsage-box .modal-title").text("Erreur");
            $("#modal-messsage-box #message").text("Erreur dans le mot de passe ou dans l'email ");
            $("#modal-messsage-box").modal("show");
        }).done(function (data) {
            var rep = data;
            if (rep.success) {
                // set  the mother fucking cookie
                $.cookie("session_id", rep.session_id, {expire: rep.timeout});

                localStorage.name = rep.name;
                localStorage.surname = rep.surname;
                localStorage.role = rep.role;
                localStorage.email = rep.email;


                // redirect
                document.location.href = "/index.html";
            }
        });;
    }
});



