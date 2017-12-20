// API

/* Secondes en Heures/Minutes */
function minutesToHM(m) {
    var h = Math.floor(m/60); //Get whole hours
    m -= h*60;
    return h+":"+(m < 10 ? '0'+m : m); //zero padding on minutes and seconds
}

/* On check si la session est valide auprès du serveur si elle ne l'est pas on redirige sur la page de login */
function checkAuth(){
  $.get("/api/auth/check_session",{session_id:$.cookie("session_id")}).done(function(data){
    if(!data.is_valid){
      console.log("not valid session ");
      document.location.href="/login.html";
   }
    else{
      console.log(" valid session ");
    }
  });
}

function sortTab(t1,t2){
    return t2.distance-  t1.distance ;
}

function sortUsers(str){
  if(str ==  ''){
  }
  var users = $(".list-wrapper > ul >li").not("#model");
  var tabl =[];
  users.each(function(index,elem){
    var text = $(elem).find(".user-name > span").text() || "";
    var distance = LiquidMetal.score(text,  str);
    tabl.push({distance : distance, elem : elem});
  });
  return tabl.sort(sortTab);
}

var traduction = {
    "slalom_sans_portes": "Slalom sans portes",
    "slalom_avec_portes": "Slalom avec portes",
    "bateau_directeur": "Bateau directeur",
    "specifique_autre": "Autre (Spécifique)",
    "course": "Course à pieds",
    "velo": "Velo de route",
    "vtt": "VTT",
    "ski_de_fond": "Ski de fond",
    "natation": "Natation",
    "sport_raquette": "Sport de raquette",
    "cardio_autre": "Autre (Cardio)",
    "rm_salle": "Renforcement Musculaire en salle",
    "rm_embarquee": "Renforcement musculaire embarqué",
    "competition": "Compétitions & Tests",
    "slalom": "Slalom",
    "test_terrain": "Test terrain",
    "test_hrv": "Test HRV",
    "rm_autre": "Autre (Renforcement musculaire)",
    "kine_osteo": "Kiné/Ostéo",
    "balneo": "Balnéothérapie",
    "relaxation_yoga": "Relaxation/Yoga",
    "chryotherapie": "Chryotherapie",
    "etirements": "Etirements",
    "soins_autre": "Autre (Soins)",
    "analyse_video": "Analyse vidéo",
    "entretien": "Entretien avec entraîneur",
    "gestion_conception": "Gestion, conception, entretien matos",
    "travail_projet_vie": "Travail sur le projet de vie",
    "logistique": "Logistique saison, course, actions",
    "strategie_autre": "Autre (Stratégie)",

    "endurance":"Endurance",
    "sv1":"SV 1",
    "sv2":"SV 2",
    "pma_vma":"PMA/VMA",
    "lactique":"Lactique",
    "endurance_vitesse":"Endurance de vitesse",
    "vitesse":"Vitesse",
    "technique":"Technique",
    "rm_f_endurance":"RM Force Endurance",
    "rm_f_puissance":"RM Puissance Force",
    "rm_f_max":"RM Force Max",
    "rm_f_explosive":"RM Force Explosive",
    "autre_pe":"Autre",

    "eau_plate":"Eau plate",
    "eau_vive_II":"Eau vive Classe II",
    "eau_vive_III":"Eau vive Classe III",
    "eau_vive_IV":"Eau vive Classe IV",
    "autre_milieu":"Autre",
}
var globaltypeToColor = {
    "SPECIFIQUE": "BLEU",
    "CARDIO": "VERT",
    "RENFORCEMENT MUSCULAIRE": "VIOLET",
    "COMPETITIONS ET TESTS": "ROUGE",
    "SOINS": "ORANGE",
    "STRATEGIE PROJET": "GRIS",
}

var globaltypeToImage = {
    "SPECIFIQUE": "specifique.png",
    "CARDIO": "cardio.png",
    "RENFORCEMENT MUSCULAIRE": "muscu.png",
    "COMPETITIONS ET TESTS": "podium.png",
    "SOINS": "health-care.png",
    "STRATEGIE PROJET": "mind-gears.png",
}


var googleColor = {
    "BLEU": "9",
    "VERT": "10",
    "VIOLET": "3",
    "ROUGE": "11",
    "ORANGE": "6",
    "GRIS": "8"
}

var trainingbookColor = {
    "BLEU": "rgba(52, 152, 219, 0.2)",
    "VERT": "rgba(46, 204, 113, 0.2)",
    "VIOLET": "rgba(155, 89, 182, 0.2)",
    "ROUGE": "rgba(231, 76, 60, 0.2)",
    "ORANGE": "rgba(230, 126, 34, 0.2)",
    "GRIS": "rgba(189, 195, 199, 0.2)"
}