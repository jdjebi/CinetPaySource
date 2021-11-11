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
            title:"Gestion des utilisateurs",
            urls: UrlsPack
        }

        return res.render('backoffice/users/users_index', response);
    }
);


// -----------------------------------------------------------------

// SHOW ----------------------------------------------------------

router.get('/:id(\\d+)', 
    authUser,
    function (req, res, next){

        var userId = parseInt(req.params.id);

        response = {
            title:"Gestion des utilisateurs",
            userId:userId
        }

        return res.render('backoffice/users/users_show', response);
    }
);


// -----------------------------------------------------------------

module.exports = router;
