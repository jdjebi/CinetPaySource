var UrlsPack = require('./urls');

module.exports = {

  authUser: function(req, res, next) {
    if (req.isAuthenticated()) {
      return next();
    }
    req.flash('error_msg', 'Connectez vous pour accéder à cette page');
    res.redirect(UrlsPack.backoffice.home);    
  },

  guestUser: function(req, res, next) {
    if (!req.isAuthenticated()) {
      return next();
    }

    res.redirect(UrlsPack.backoffice.home);  
  }
};