var express            = require('express');
var router             = express.Router();
var UrlsPack           = require('../../../core/urls');

// INIT CONF -----------------------------------------------------

const { guestUser, authUser } = require('../../../core/auth');

// INDEX ----------------------------------------------------------

router.get('', 
    authUser,
    function (req, res, next){

        response = {
            title:"Gestion des SIMBOX",
            urls:{
                simboxesUrls: UrlsPack.simboxes
            }
        }

        return res.render('backoffice/simbox/simbox_index', response);
    }
);


// -----------------------------------------------------------------

module.exports = router;
