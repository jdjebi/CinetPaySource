var express = require('express');
var router = express.Router();

// INIT CONF ---------------------------------------

// DASHBOARD ---------------------------------------

// const { guestUser, authUser } = require('../../../core/auth');
 
router.get('', 
  function(req, res, next) {
    res.render('backoffice/dashboard/dashboard', { title: 'Tableau de bord' });
  }
);

//--------------------------------------------------

module.exports = router;
