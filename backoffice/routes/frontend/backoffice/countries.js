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
            title:"Gestion des pays",
            countriesUrls: UrlsPack.countries,
            currenciesUrls: UrlsPack.currencies
        }

        res.render('backoffice/countries/countries_show', response);
    }
);

// -----------------------------------------------------------------


module.exports = router;
