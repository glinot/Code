/**
 * Created by grego on 13/06/2016.
 */
var system = require('./db').collection('system')
var ObjectId = require("mongolian").ObjectId
var moment = require("moment")
var profile = require("./profile");


var folderXLS = "./uploads/reports/"
var fs = require("fs")
var xlsx = require("node-xlsx")


//trad
var traduction = {
    // SPECIFIQUE
    "slalom_sans_portes": "Slalom sans portes",
    "slalom_avec_portes": "Slalom avec portes",
    "bateau_directeur": "Bateau directeur",
    "specifique_autre": "Autre (Spécifique)",
    // CARDIO
    "course": "Course à pieds",
    "velo": "Velo de route",
    "vtt": "VTT",
    "ski_de_fond": "Ski de fond",
    "natation": "Natation",
    "surf": "Surf",
    "sport_raquette": "Sport de raquette",
    "cardio_autre": "Autre (Cardio)",
    // RENFORCEMENT MUSCULAIRE
    "rm_salle": "Renforcement Musculaire en salle",
    "rm_embarquee": "Renforcement musculaire embarqué",
    "rm_autre": "Autre (Renforcement musculaire)",
    // COMPETITION ET TEST
    "situation_compet": "Situation de compétition",
    "n1": "N1",
    "icf": "ICF",
    "big_cup": "World Cup / Championnats du monde / Championnats d'Europe",
    "test_terrain": "Test terrain",
    "test_hrv": "Test HRV",
    "compet_autre": "Autre (Compétition)",
    // SOINS
    "kine_osteo": "Kiné/Ostéo",
    "balneo": "Balnéothérapie",
    "relaxation_yoga": "Relaxation/Yoga",
    "chryotherapie": "Chryotherapie",
    "etirements": "Etirements",
    "soins_autre": "Autre (Soins)",
    // STRATEGIE PROJET
    "analyse_video": "Analyse vidéo",
    "entretien": "Entretien avec entraîneur",
    "gestion_conception": "Gestion, conception, entretien matos",
    "travail_projet_vie": "Travail sur le projet de vie",
    "logistique": "Logistique saison, course, actions",
    "strategie_autre": "Autre (Stratégie)",

    // PROCEDE
    "endurance": "Endurance",
    "sv1": "SV 1",
    "sv2": "SV 2",
    "lactique": "Lactique",
    "pma_vma": "PMA/VMA",
    "endurance_vitesse": "Endurance Vitesse",
    "vitesse": "Vitesse",
    "technique": "Technique",
    "rm_f_endurance": "RM Force Endurance",
    "rm_f_puissance": "RM Force Puissance",
    "rm_puissance_vitesse": "RM Puissance Vitesse",
    "rm_f_max": "RM Force Max",
    "rm_f_explosive": "RM Force Explosive",
    "autre_explosive": "Autre",

    // MILIEU
    "eau_plate": "Eau plate",
    "eau_vive_II": "Eau vive Classe II",
    "eau_vive_III": "Eau vive Classe III",
    "eau_vive_IV": "Eau vive Classe IV",
    "autre_milieu": "Autre",

    // BILAN
    "temps_travail_intensite": "Temps de travail à l'intensité",
    "difficulte": "Difficulté",
    "nb_portes_franchies": "Nombre de portes franchies",
    "nb_erreurs": "Nombre d'erreurs",
    "nb_parcours_realises": "Nombre de parcours réalisés",
    "nb_parcours_a_zero": "Nombre de parcours à zéro",
    "nb_penalites_seance": "Bombre de pénalités sur la scéance",
    "nb_km": "Nombre de kms",
    "fc_moyenne": "FC moyenne",
    "fc_max": "FC max",
    "sportman": "Sportif",
    "coach": "Coach",

    //ITEMS Profile
    //HEATH
    "weight": "Poids",
    "fat_percentage": "Pourcentage de matière grasse",
    "height": "Taille",


};

function createXLSReportBySportmanIdByYear(req, res) {
    try {
        var year = parseInt(req.query.year)
        var sportmanID = new ObjectId(req.params.sportman_id)
        profile.isCoach(req.cookies.session_id, function (isCoach) {
            if (isCoach) {


                system.runCommand('aggregate', {
                    pipeline: [
                        {"$match": {"_id": sportmanID}},
                        {"$unwind": "$trainings"},
                        {"$project": {"profile": 1, "trainings": 1}},
                        {
                            "$match": {
                                "trainings.date": {
                                    "$lt": moment(year + 1, "YYYY").toDate(),
                                    "$gt": moment(year, "YYYY").subtract(1, "day").toDate()
                                }
                            }
                        }
                    ]
                }, function (e, a) {

                    if (e) {
                        res.sendStatus(500)
                    }
                    else {


                        if (a.result.length > 0) {

                            ARR = a.result
                            var data_training = []

                            // header Line
                            data_training.push([ARR[0].profile.surname, ARR[0].profile.name])
                            data_training.push(["date"
                                , "intitulé"
                                , "objectif"
                                , "procédés d'ent"
                                , "moyen d'ent"
                                , "Milieu de pratique"
                                , "Evaluation de l'objectif"
                                , "Indice de fatigue"
                                , "Charge perçue",
                                "Bilan" ,
                                "Temps de travail intensité",
                                "Difficulté",
                                "Nombre de portes franchies",
                                "Nombre d'erreurs",
                                "Nombre de parcours réalisés",
                                "Nombre de parcours à zero",
                                "Nombre de pénalités",
                                "Nombre de Kilometres",
                                "Fréquence caridiaque moyenne",

                            ])


                            // Trainings

                            try {

                                for (i in ARR) {
                                    var training = ((ARR[i] || {}).trainings || {})
                                    training.feedback = training.feedback || {}
                                    if (training && training.date) {

                                        var row = [
                                            (training.date ).toLocaleDateString(),
                                            training.titre,
                                            training.objective,
                                            training.procede,
                                            training.type,
                                            training.milieu,
                                            training.feedback.eval_objective,
                                            training.feedback.eval_fatigue,
                                            training.feedback.eval_sensations, // "CHARGE PERCUES ? ? "
                                            training.objectives_text,
                                            training.temps_travail_intensite,
                                            training.difficulte,
                                            training.nb_portes_franchies,
                                            training.nb_erreurs,
                                            training.nb_parcours_realises,
                                            training.nb_parcours_a_zero,
                                            training.nb_penalites_seance,
                                            training.nb_km,
                                            training.fc_moyenne,
                                            training.fc_max

                                        ]
                                        data_training.push(row)
                                    }
                                }
                            }
                            catch (e) {
                                console.log(e)
                            }


                            var xlsdata = [{"name": "Scéances", data: data_training}]
                            system.find({_id: sportmanID}, {"stats": 1, _id: 0}).toArray(function (err_h, arr_h) {


                                if (arr_h) {

                                    for (name in (arr_h[0] || {}).stats) {
                                        try {

                                            var page = {
                                                name: name, data: arr_h[0].stats[name].map(function (e) {
                                                    return [e.date.toString(), e.value];
                                                })
                                            }
                                            xlsdata.push(page);
                                        }
                                        catch (e) {
                                            console.log(e)
                                        }
                                    }
                                }

                                var buffer = xlsx.build(xlsdata)
                                var name = (ARR[0].profile.surname + "_" + ARR[0].profile.name + "_" + year) + ".xlsx"
                                fs.writeFile(folderXLS + name, buffer, function (err) {
                                    console.log(err)
                                    if (err) {
                                        res.sendStatus(500)
                                    }
                                    else {
                                        res.json({"file_name": name})
                                    }
                                })
                            });


                        }
                        else {
                            res.sendStatus(404)
                        }

                    }
                })
            }
            else {
                res.sendStatus(401)
            }
        })
    }
    catch (e) {
        res.sendStatus(400)
    }
}


function getEveryYearsOfTrainingsBySportman(req, res) {
    try {

        var sportmanID = new ObjectId(req.params.sportman_id)

        system.runCommand('aggregate', {
            pipeline: [
                {"$match": {"_id": sportmanID}},
                {"$unwind": "$trainings"},
                {"$group": {"_id": {"$year": "$trainings.date"}}},
                {"$sort": {"_id": -1}}
            ]
        }, function (err, arr) {
            if (err) {
                res.sendStatus(500)
            }
            else {
                res.json(
                    {
                        years: arr.result.map(function (elem) {
                            return elem._id
                        })
                    }
                )
            }
        })


    }
    catch (e) {
        res.sendStatus(400)
    }
}
function getEveryYearsOfTrainings(req, res) {

    try {


        system.runCommand('aggregate', {
            pipeline: [
                {"$match": {"_id": req.remoteUser._id}}, // Auth is implicit
                {"$unwind": "$trainings"},
                {"$group": {"_id": {"$year": "$trainings.date"}}},
                {"$sort": {"_id": -1}}
            ]
        }, function (err, arr) {
            if (err) {
                res.sendStatus(500)
            }
            else {
                res.json(
                    {
                        years: arr.result.map(function (elem) {
                            return elem._id
                        })
                    }
                )
            }
        })


    }
    catch (e) {
        console.error(e)
        res.sendStatus(400)
    }
}

function getExcelReport(req, res) {
    var u = req.remoteUser

    if (u && u.role == "coach") {
        try {
            var year = parseInt(req.query.year)
            system.runCommand('aggregate', {
                pipeline: [
                    {"$match": {"_id": u._id}},
                    {"$unwind": "$trainings"},
                    {"$project": {"profile": 1, "trainings": 1}},
                    {
                        "$match": {
                            "trainings.date": {
                                "$lt": moment(year + 1, "YYYY").toDate(),
                                "$gt": moment(year, "YYYY").subtract(1, "day").toDate()
                            }
                        }
                    }
                ]
            }, function (e, a) {
                if (e) {
                    res.sendStatus(500)
                }
                else {
                    var xlsdata = [];

                    var trainings_sheet = {name: "Entrainements", data: []}
                    trainings_sheet.data.push([
                        "date",
                        "intitulé",
                        "objectif",
                        "description",
                        "Procédés d'entrainement",
                        "Moyens d'entrainement",
                        "Milieu de pratique",
                        "Bilan"

                    ])

                    try {


                        trainings_sheet.data = trainings_sheet.data.concat(a.result.map(function (t) {
                            return [
                                t.trainings.date.toISOString(),
                                t.trainings.titre,
                                t.trainings.objective,
                                t.trainings.description,
                                traduction[t.trainings.procede],
                                traduction[t.trainings.type],
                                traduction[t.trainings.milieu],
                                t.trainings.bilan

                            ];
                        }))
                        xlsdata.push(trainings_sheet)
                        var buffer = xlsx.build(xlsdata)
                        var name = (u.profile.surname + "_" + u.profile.name + "_" + year) + ".xlsx"
                        fs.writeFile(folderXLS + name, buffer, function (err) {
                            console.log(err)
                            if (err) {
                                res.sendStatus(500)
                            }
                            else {
                                res.json({"file_name": name})
                            }
                        })
                    }
                    catch (e) {
                        res.sendStatus(500)
                    }


                }
            })
        }
        catch (e) {
            req.sendSatus(500)
        }
    }
    else {
        res.sendStatus(401)
    }
}


module.exports = {
    createXLSReportBySportmanIdByYear: createXLSReportBySportmanIdByYear,
    getEveryYearsOfTrainingsBySportman: getEveryYearsOfTrainingsBySportman,
    getEveryYearsOfTrainings:getEveryYearsOfTrainings,
    getExcelReport: getExcelReport

}