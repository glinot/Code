/* On charge la partie statistiques d la personne sélectionnée lorsqu'on click sur le nom / la photo
 * PB : Ne fonctionne pas pour ceux chargés via la BDD */
// Liste des sportifs
var liste_sportif = new Array();

// Texte contenant les sportifs sélectionnés
var sportifs_selectionnes = "";

// Ajoute les sportifs sélectionnés à une liste
function ajout_sportif(string) {
    if (!liste_sportif.includes(string)) {
        liste_sportif.push(string);
        liste_sportif.sort();
    } else {
        var index = liste_sportif.indexOf(string);
        liste_sportif.splice(index, 1);
    }
    texte = "";
    liste_sportif.forEach(ajouter_texte);
    if (texte != "") alert(texte);
}

// Texte contenant la liste des sportifs sélectionnés
function ajouter_texte(element, index, ar) {
    texte += element + ", ";
}

/*
 $("#search").keyup(function(event) {
 var prenom = $("#search").val();
 if(prenom != ""){
 var tab = sortUsers(prenom);
 for (var i = 0; i < tab.length; i++) {
 var c = $((tab[i]).elem);
 $("#user-list").append(c);
 }
 }else{
 // TO DO
 // Trier la liste dans l'ordre alphabétique
 }
 });

 */
// CE QU'IL Y AVAIT AVANT



$("#search").keyup(function (event) {

    var sStr = $("#search").val();
    if (sStr == '') {

        var a = $("#sportman-list > li").sort(function (e1, e2) {
            var t1 = $(e1).find(".user-name > a.user-name-text").text() +" " +$(e1).find(".user-name > a.user-surname-text").text();
            var t2 =  $(e2).find(".user-name > a.user-name-text").text() +" " +$(e2).find(".user-name > a.user-surname-text").text();
            return t1 == t2 ? 0 : t1 < t2 ? -1 : 1;
        });
        $("#sportman-list").html(a);
        $("#sportman-list > li ").not("#model-sportman").removeClass("hidden");

    } else {
        var tab = sortUsers(sStr);
        $("#user-list").not("#model-sportman").empty();
        var i = 0;
        var to_disp = 6;
        for (i = 0; i < tab.length; i++) {
            var c = $((tab[i]).elem);
            if ((tab[i]).distance > 0.8 && to_disp > 0) {

                c.removeClass("hidden");
                to_disp--;
            } else {
                c.addClass("hidden");
            }
            $("#user-list").append(c);
        }
    }
})
/*;*/



// get users


$.get("/api/sportmen/profile/basic", function (sportmen) {
    sportmen.forEach(function (sportman) {
        var s = $("#model-sportman").clone(true , true ).attr("id", sportman._id).removeClass("model");
        $(s).find(".user-img").attr('src', sportman.profile.pictures.profile);
        $(s).find(".user-name-text").text(sportman.profile.name);
        $(s).find(".user-surname-text").text(sportman.profile.surname);
        $("#sportman-list").append(s);
    });
});



$("#sportman-list > li").click(function () {
    window.location.hash = "#coach_sportman_stats";
    localStorage.sportman_id = $(this).attr("id");
});
