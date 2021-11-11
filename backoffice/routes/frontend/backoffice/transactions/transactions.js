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
            title:"Gestion des transactions",
            urls: UrlsPack
        }

        return res.render('backoffice/transactions/transactions/transactions_index', response);
    }
);

router.get('/events', 
    authUser,
    function (req, res, next){

        response = {
            title:"Gestion des transactions - Evènement",
            urls: UrlsPack
        }

        return res.render('backoffice/transactions/transactions/transactions_events', response);
    }
);

router.get('/id/:id', 
    authUser,
    function (req, res, next){

        var transactionId = req.params.id;

        response = {
            title:"Gestion des transactions - Transaction",
            urls: UrlsPack,
            transactionId:transactionId
        }

        return res.render('backoffice/transactions/transactions/transactions_show', response);
    }
);

module.exports = router;
