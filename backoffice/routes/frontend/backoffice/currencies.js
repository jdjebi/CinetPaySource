var express            = require('express');
var router             = express.Router();
var UrlsPack           = require('../../../core/urls');

// INIT CONF -----------------------------------------------------

const { guestUser, authUser } = require('../../../core/auth');

// SHOW -----------------------------------------------------------

router.get('', 
    authUser,
    function (req, res, next){
    
        response = {
            title:"Gestion des devises",
            currenciesUrls: UrlsPack.currencies,
        }

        res.render('backoffice/currencies/currencies_show', response);
    }
);

// -----------------------------------------------------------------


module.exports = router;
