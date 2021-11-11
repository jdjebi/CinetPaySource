var createError  = require('http-errors');
var express      = require('express');
var path         = require('path');
var logger       = require('morgan');
var session      = require("express-session");
const passport   = require('./passport/setup');
const flash      = require('connect-flash');
const { env }    = require('process');
const UrlsPack   = require('./core/urls');

// MAIN APP INSTANCE ------------------------------------------

var app = express();

// VIEW -------------------------------------------------------

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json({limit: '500mb', extended: true}));
app.use(express.urlencoded({limit: '500mb', extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

// CONFIG SESSION ----------------------------------------------

const KnexSessionStore = require('connect-session-knex')(session);
const Knex = require('knex');

connectionObj = {
  host: process.env.DATABASE_HOST || 'localhost', 
  user: process.env.DATABASE_USERNAME || 'postgres',
  password: process.env.DATABASE_PASSWORD || '123',
  database: process.env.DATABASE_NAME || 'cinetpay',
  port: process.env.DATABASE_PORT || '5432',
};

const knex = Knex({
  client: 'pg',
  connection: process.env.DATABASE_URL,
});
const store = new KnexSessionStore({
  knex,
  tablename: 'sessions',
  createtable: true
});

// AUTHENTIFICATION -------------------------------------------

app.use(session({
  secret: 'test de session',
  cookie: { },
  resave: true, 
  saveUninitialized: false,
  store
}));
app.use(passport.initialize());
app.use(passport.session());

// FLASH ------------------------------------------------------
app.use(flash());

app.use(function(req, res, next) {
  res.locals.success_msg = req.flash('success_msg');
  res.locals.error_msg = req.flash('error_msg');
  res.locals.error = req.flash('error');
  res.locals.session_urlspack = UrlsPack;
  res.locals.user = req.user;
  next();
});

// ROUTERS ----------------------------------------------------

var authRouter                 = require('./routes/frontend/auth/auth');
var dashboardRouter            = require('./routes/frontend/backoffice/dashboard');
var simulatorRouter            = require('./routes/frontend/backoffice/transactions/simulator');
var transactionsRouter         = require('./routes/frontend/backoffice/transactions/transactions');
var transactionsBuilderRouter  = require('./routes/frontend/backoffice/transactions/generateTransactions');
var operatorsRouter            = require('./routes/frontend/backoffice/operators');
var simboxRouter               = require('./routes/frontend/backoffice/simbox');
var countriesRouter            = require('./routes/frontend/backoffice/countries');
var currenciesRouter           = require('./routes/frontend/backoffice/currencies');
var servicesRouter             = require('./routes/frontend/backoffice/services');
var usersRouter                = require('./routes/frontend/backoffice/users');

var authApiRouter              = require('./routes/api/api_auth');
var urlsRouter                 = require('./routes/api/api_urls');

// ROUTES ----------------------------------------------------

app.use('/', authRouter);
app.use('/auth', authApiRouter);
app.use('/dashboard', dashboardRouter);
app.use('/operateurs', operatorsRouter);
app.use('/simbox', simboxRouter);
app.use('/pays', countriesRouter);
app.use('/devises', currenciesRouter);
app.use('/services', servicesRouter);
app.use('/backoffice/transactions', transactionsRouter);
app.use('/backoffice/transactions/simulateur', simulatorRouter);
app.use('/urls',urlsRouter);
app.use('/transactions/generate', transactionsBuilderRouter);
app.use('/users', usersRouter);

// EXTRAS ROUTES ---------------------------------------------

// app.use(require('express-status-monitor')());

app.use(function(req, res, next) {
  next(createError(404));
});

app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

//------------------------------------------------------------

module.exports = app;