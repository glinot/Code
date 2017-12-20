$(".sup").click(function () {
    loadInElem("supress_account.html", "#main-container");
});

$(".retour").click(function () {
        loadInElem("admin_user_list.html", "#main-container");
    }
);

$(".mod").click(function () {
    activeLiModal = this;
    $("#Modify").modal("show");

});


$(".add_user").click(function () {
    activeLiModal = this;
    $("#Add").modal("show");

});


// list users on the server


function updateUserList(){

    $.get("/api/users/verified", function (actives) {
        $(".user-verified").not(".model").remove();
        actives.forEach(function (active) {
            var a = $("#admv-user-model").clone(true, true).removeClass('model').attr('id', active._id);
            $(a).find(".profile-name").text(active.profile.name);
            $(a).find(".role").text(active.role);
            $(a).find(".profile-email").text(active.profile.email);
            $("#adm-active-users").append(a);
        });

    });
    $.get("/api/users/standby", function (usr_standby) {
        $(".user-standby").not(".model").remove();
        usr_standby.forEach(function (stdb) {
            var a = $("#adms-user-model").clone(true, true).removeClass('model').attr('id', stdb._id);
            $(a).find(".profile-name").text(stdb.profile.name);
            $(a).find(".role").text(stdb.role);
            $(a).find(".profile-email").text(stdb.profile.email);
            $("#adm-standby-users").append(a);
        });
    });
}
updateUserList();

var nb_verified = 0;
var nb_standby = 0;



function updateNbOf(){
    nb_standby = $(".user-standby").not(".model").find("input:checked").length
    nb_verified = $(".user-verified").not(".model").find("input:checked").length
}
function updateButtonState() {
    if (nb_verified > 0 || nb_standby > 0) {
        $("#delete-btn").removeClass("disabled");
        if (nb_standby > 0) {
            $("#validate-btn").removeClass("disabled");

        }
        if ((nb_verified == 0 && nb_standby == 1 ) || (nb_verified == 1 && nb_standby == 0)) {
            $("#edit-btn").removeClass("disabled");
        }
        else {
            $("#edit-btn").addClass("disabled");
        }
    }
    else {

        $("#delete-btn ,#edit-btn, #validate-btn").addClass("disabled");
    }
}

updateButtonState();
function updateTextNbOf(){
    $("#nb-verified").text(nb_verified);
    $("#nb-standby").text(nb_standby);
}
$(".selector-verified").click(function () {

    updateNbOf();
    updateTextNbOf();
    updateButtonState();

});
$(".selector-standby").click(function () {
    updateNbOf();
    updateTextNbOf();
    updateButtonState();
});

$("#delete-btn").click(function () {
    if (nb_standby > 0 || nb_verified > 0) {
        var users = $('.user-verified , .user-standby').filter(function(){
            return $(this).find(".selector-standby ,.selector-verified").is(":checked");
        }).not(".model");
        users.each(function(i,user){
            var _id = $(user).attr("id");
            $.post("/api/users/delete/"+_id, function(data){
                if($(user).hasClass('.user-verified')){
                    $("#nb-verified").text(--nb_verified);
                }
                else{
                    $("#nb-standby").text(--nb_standby);
                }
                $(user).fadeOut().remove();
                updateNbOf();
                updateTextNbOf();
                updateButtonState();
            });
        });
        console.log(users);
    }

});

$("#button-add-user").click(function(){
    var email = $("#add-user-email").val();
    var password = $("#add-user-password").val();
    var name = $("#add-user-name").val();
    var surname = $("#add-user-surname").val();
    var role = $("#add-user-coach").is(":checked") ?  "coach" : "sportman";

    $.post("/api/users/register" , {"role" : role,"email": email, "password": password, "name": name, "surname": surname} ).done(function(){
        $('#add-user-modal').modal("hide")
    });
    updateNbOf();
    updateTextNbOf();
    updateButtonState();
    updateUserList();

});

$("#validate-btn").click(function(){

    if(nb_standby > 0 ){
        var u = $( ".user-standby").not(".model");
        u.each(function(i ,elem){
            var id = $(elem).attr("id");
            $.post("/api/users/validate/"+id);
        });
        updateNbOf();
        updateTextNbOf();
        updateButtonState();
        updateUserList();
    }
});

