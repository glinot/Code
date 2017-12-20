var api = require(__dirname + '/api/api');
var profile = require(__dirname + '/api/profile');
var express = require('express');
var cookieParser = require('cookie-parser');
var app = express();
var auth = require(__dirname + '/api/login');
var multer = require('multer');


var call = 0;


// pour pouvoir parser les cookies ^^
app.use(cookieParser());


var bodyParser = require('body-parser');
//app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser({limit: '50mb'}));
app.use(bodyParser.urlencoded({extended: true})); // support encoded bodies


app.use('/', function (req, res, next) {

    var ua = req.headers['user-agent']

    if (req.path == '/' || req.path == "/index.html") {
        auth.onValidSession(req.cookies.session_id,
            function (isValid, session) {
                if (!isValid) {
                    if (/mobile/i.test(ua)) {
                        res.redirect('/login-mobile.html');
                    }
                    else {

                        res.redirect('/login.html');
                    }
                } else {
                    //winston.info('session_id obsolete rederecting to login');
                    next();
                }
            }
        );
        return;

    } else if (req.path == "/login.html") {
        auth.onValidSession(req.cookies.session_id,
            function (isValid, session) {
                if (isValid) {
                    res.redirect('/index.html');
                } else {
                    //winston.info('session_id obsolete rederecting to login');
                    if (/mobile/i.test(ua)) {
                        res.redirect('/login-mobile.html');
                    }
                    else {

                        next();
                    }

                }
            }
        );
        return;

    }


    next();

});



app.use('/', express.static('client/v2/src'));
app.use('/', express.static('client/src'));
app.use('/img', express.static('uploads/img'));



app.use('/report',function(req, res , next){
    profile.isCoach(req.cookies.session_id , function(isCoach){
        if(isCoach){
            next();
        }
        else{
            res.sendStatus(401)
        }
    })
});
app.use('/report', express.static('uploads/reports'));

app.use('/api', api); // routes api
app.listen(80, function () {
});
