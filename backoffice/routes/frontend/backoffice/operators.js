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
            title:"Gestion des opérateurs",
            operatorsUrls: UrlsPack.operators,
            countriesUrls: UrlsPack.countries
        }

        return res.render('backoffice/operators/operators_index', response);
    }
);

// SHOW ----------------------------------------------------------

router.get('/:id(\\d+)', 
    authUser,
    function (req, res, next){

        var operatorId = parseInt(req.params.id);

        response = {
            title:"Gestion des opérateurs",
            data:{
                operatorId:operatorId,
                urls:{
                    operatorsUrls: UrlsPack.operators,
                    countriesUrls: UrlsPack.countries,
                    resourcesUrls: UrlsPack.resources,
                    servicesUrls: UrlsPack.services
                }
            }

        }

        return res.render('backoffice/operators/operators_show', response);
    }
);

// -----------------------------------------------------------------

module.exports = router;
