var express = require('express')
var app = express()
var api = require("./api")

var db = require("./db")
var User = db.User;

var passport = require('passport');
var GoogleStrategy = require('passport-google-oauth').OAuth2Strategy;


app.use(require('cookie-parser')());
var bodyParser = require("body-parser");
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
    extended: true
}));

app.use(require('express-session')({
    secret: 'zaezefùjf^sdqoijf',
    resave: true,
    saveUninitialized: true,
    cookie: {maxAge: 60000*3600*24}
}));


app.use(passport.initialize());
app.use(passport.session());
passport.serializeUser(function (user, done) {
    done(null, user.google_id);
});

passport.deserializeUser(function (google_id, done) {
    User.findOne({google_id: google_id}, function (err, user) {
        done(err, user)
    })
    //done(null, user);
});
// Use the GoogleStrategy within Passport.
//   Strategies in Passport require a `verify` function, which accept
//   credentials (in this case, an accessToken, refreshToken, and Google
//   profile), and invoke a callback with a user object.
passport.use(new GoogleStrategy({
        clientID: "CLIENT_ID",
        clientSecret: "CLIENT_SECRET",
        callbackURL: "/auth/google/callback"
    },
    function (accessToken, refreshToken, profile, done) {
        User.findOne({google_id: profile.id}, function (err, user) {
            if (err || user == null) {
                dname = profile.displayName.split(" ")
                name = (dname[0] || "")
                surname = (dname[1] || "")
                var u = new User({
                    google_id: profile.id,
                    name: name,
                    surname: surname,
                    picture: (profile.photos[0] || {} ).value || "/anon.png"
                })
                u.save(function (err, u) {
                    done(err, u);
                })
            }
            else {
                done(err, user)
            }
        });

    }
));
// GET /auth/google
//   Use passport.authenticate() as route middleware to authenticate the
//   request.  The first step in Google authentication will involve
//   redirecting the user to google.com.  After authorization, Google
//   will redirect the user back to this application at /auth/google/callback
app.get('/auth/google',
    passport.authenticate('google', {scope: ['https://www.googleapis.com/auth/plus.login']}));

// GET /auth/google/callback
//   Use passport.authenticate() as route middleware to authenticate the
//   request.  If authentication fails, the user will be redirected back to the
//   login page.  Otherwise, the primary route function function will be called,
//   which, in this example, will redirect the user to the home page.
app.get('/auth/google/callback',
    passport.authenticate('google', {failureRedirect: '/login'}),
    function (req, res) {
        res.redirect('/');
    });



app.use('/', express.static('web/bower_components'))

app.use('(/index.html|/)', function (req, res, next) {
    if (req.session && req.session.passport && req.session.passport.user) {
        next()
    }
    else {
        res.redirect("/login.html")
    }
});
app.use("/api", api)
app.use('/', express.static('web'))
app.listen(80, function () {
})