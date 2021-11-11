var express = require('express');
var router = express.Router();
var passport = require('passport');
var UrlsPack           = require('../../../core/urls');

// INIT CONF ---------------------------------------

const { guestUser, authUser } = require('../../../core/auth');
 
// LOGIN PAGE --------------------------------------

router.get('', 
  guestUser,
  function(req, res, next) {
    res.render('auth/login', { title: 'Backoffice - Connexion', urls:UrlsPack });
  }
);

// LOGOUT PAGE --------------------------------------

router.get('/deconnexion', 
  authUser,
  function(req, res){
    req.logout();
    res.redirect('/');
  }
);

//--------------------------------------------------

router.get('/home', 
  function(req, res, next) {
    res.render('home', { title: 'Backoffice' });
  }
);

//--------------------------------------------------

module.exports = router;
