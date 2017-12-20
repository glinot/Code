var express = require('express');
var router = express.Router();
var auth = require('./login')
var trainings = require('./trainings')
var profile = require("./profile")
var coach = require("./coach")
var admin = require("./admin")
var feedback = require("./feedback");
var stats = require("./stats")
var tour = require("./tour")
var excel = require("./excel")
var system = require('./db').collection('system')


router.use(function (req, res, next) {
    system.find({
        "status" : "verified",
        "sessions.session_id": req.cookies.session_id+"",
        "sessions.timeout": {
            "$gt": new Date().getTime()
        }
    }).toArray(function (err, arr) {
        req.remoteUser = (arr || [])[0]
        next();
    })
})

router.get("/auth/login", auth.login)


/* TOUR validation*/
router.get("/tour/is_validated", tour.isTourValidated)
router.post("/tour/validate", tour.validateTour)
router.post("/tour/reset", tour.resetTour)

router.get("/role", function(req , res){
    res.json({"role" : (req.remoteUser || {}).role || "UnAuthorized"})
})

router.get("/profile/me", profile.getMyProfile)
router.get("/profile/me/basic", profile.getMyBasicProfile)
router.get("/profile/:sportman_id", coach.getUserProfile)
router.get("/profile/basic/:sportman_id", coach.getUserProfileBasic)


router.get("/trainings/me", trainings.getTrainingsByBlock)
router.get("/trainings/me/basic", trainings.getTrainingsByBlockBasic)
router.get("/trainings/me/:training_id", trainings.getTrainingById)

router.post("/trainings/create", trainings.createNewTraining)
router.post("/v2/trainings/create", trainings.createTrainingCoachAndSportmen)
router.post("/trainings/create/:sportman_id", trainings.createNewTrainingForSportman)
router.post("/trainings/fill/:training_id", trainings.fillTraining)
router.post("/v2/trainings/fill/:training_id", trainings.fillTrainingV2)
router.post("/trainings/remove/:training_id", trainings.removeTraining)
router.post("/trainings/remove/:training_id/:sportman_id", trainings.removeSportmanFromTraining)
router.post("/trainings/add_google_id/:training_id" , trainings.addGoogleId);
router.post("/trainings/google_id/all/:training_id/" , trainings.getGoogleCalendarIds);
router.post("/trainings/google_id/:training_id/:sportman_id" , trainings.getGoogleSportmanCalendarId);
router.post("/trainings/sportmen/add/:training_id" , trainings.addSportmen);

// V2 Features
router.post("/trainings/bilan/:training_id" , trainings.addBilan);

router.get("/sportmen/profile/basic", coach.getSportmenProfileBasic)
router.get("/sportmen/trainings/full/:sportman_id/:training_id", coach.getSportmenTrainingsFull)
router.get("/sportmen/trainings/:sportman_id", coach.getSportmenTrainings)


router.get("/users/:status", admin.listUsers)
router.post("/users/validate/:user_id", admin.validateUser);
router.post("/users/delete/:user_id", admin.deleteUser)
router.post("/admin/users/update/", admin.updateUser)
router.post("/users/register", auth.registerUser)

router.get("/feedback/:training_id/:sportman_id", feedback.getSportmanFeedback)


router.get("/mood", profile.getMoodToday)
router.post("/mood", profile.addMoodToday)

//router.post("/trainings/fill/:training_id" , trainings.fillTraining)
router.post("/profile/boat/type", profile.storeBoatType)
router.post("/profile/boat/recoupe", profile.storeBoatRecoupe)
router.post("/profile/boat/construction", profile.storeBoatConstruction)

router.post("/profile/pagaie/type", profile.storePagaieType)
router.post("/profile/pagaie/longueur", profile.storePagaieLongueur)
router.post("/profile/pagaie/surface", profile.storePagaieSurface)




router.post("/profile/health/weight", profile.storeWeight)
router.post("/profile/health/height", profile.storeHeight)
router.post("/profile/health/fat_percentage", profile.storeFatPercentage)
router.post("/profile/health/hr_standing", profile.storeHrStanding)
router.post("/profile/health/fatigue_index", profile.storeFatigueIndex)
router.post("/profile/password", profile.storePassword)
router.post("/profile/pictures/profile", profile.uploadPhoto)
//V2
router.post("/profile/update/", profile.updateSimpleProfileFeature)
router.post("/profile/email/update/", profile.updateEmail)
router.post("/profile/password/update/", profile.updatePassword)
// COACH ONLY
router.post("/profile/health/hrv_test/:sportman_id", profile.storeHrvTest)

router.get("/stats/trainings/years", stats.getMyYearsOfTrainings)
// get Stats
router.get("/stats/trainings/methods/:wide", stats.getTrainingsHours)
router.get("/stats/trainings/procede/:wide", stats.getTrainingsHoursProcede)
router.get("/stats/trainings/procede/per/:wide", stats.getTrainingsProcedePercentage)
router.get("/stats/trainings/methods/per/:wide", stats.getTrainingsMethodsPercentage)
router.get("/stats/trainings/overview/:wide", stats.getOverview)
router.get("/stats/trainings/global/:wide", stats.getTrainingsGlobal)
router.get("/stats/:feature/:wide", stats.getFeature)

router.get("/stats/trainings/methods/:wide/:sportman_id", stats.getTrainingsHoursBySportman)
router.get("/stats/trainings/procede/:wide/:sportman_id", stats.getTrainingsHoursProcedeBySportman)
router.get("/stats/trainings/procede/per/:wide/:sportman_id", stats.getTrainingsProcedePercentageBySportman)
router.get("/stats/trainings/methods/per/:wide/:sportman_id", stats.getTrainingsMethodsPercentageBySportman)
router.get("/stats/trainings/overview/:wide/:sportman_id", stats.getOverviewBySportman)
router.get("/stats/trainings/global/:wide/:sportman_id", stats.getTrainingsGlobalBySportman)
router.get("/stats/:feature/:wide/:sportman_id", stats.getFeatureBySportman)


router.get("/report" , excel.getExcelReport)
router.get("/report/years" , excel.getEveryYearsOfTrainings)
router.get("/report/:sportman_id", excel.createXLSReportBySportmanIdByYear)
router.get("/report/years/:sportman_id", excel.getEveryYearsOfTrainingsBySportman)
//router.get("/api/stats/year/:resource/:year_number")


// admin

router.get("/tokens/google" , admin.apiTokens)
router.post("/tokens/google" , admin.setApiTokens)


//debug

router.get("/auth/is_valid", auth.isSessionValid)


console.log("------------------------------API------------------------------")


router.stack.forEach(function (r) {
    if (r.route && r.route.path) {
        console.log("/api" + r.route.path)
    }
})


console.log("---------------------------------------------------------------")


module.exports = router;
