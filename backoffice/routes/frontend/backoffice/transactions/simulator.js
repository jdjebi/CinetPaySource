var express            = require('express');
var router             = express.Router();
var UrlsPack           = require('../../../../core/urls');

// INIT CONF -----------------------------------------------------

const { guestUser, authUser } = require('../../../../core/auth');

// INDEX ----------------------------------------------------------

router.get('', 
    authUser,
    function (req, res, next){

        response = {
            title:"Simulateur",
            operatorUrls: UrlsPack.apiBackoffice.operators,
            servicesUrls: UrlsPack.services
        }

        return res.render('backoffice/transactions/simulator/simulator', response);
    }
);


router.get('/v2', 
    authUser,
    function (req, res, next){

        response = {
            title:"Simulateur v2",
            urls: UrlsPack,
        }

        return res.render('backoffice/transactions/simulator/simulator2', response);
    }
);

module.exports = router;
