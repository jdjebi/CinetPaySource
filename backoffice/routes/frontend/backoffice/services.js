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
            title:"Gestion des services",
            urls:{
                servicesUrls: UrlsPack.services
            }
        }

        return res.render('backoffice/services/services_index', response);
    }
);


// -----------------------------------------------------------------

// SHOW TRANSFERT SERVICE----------------------------------------------------------

router.get('/transfert/:id(\\d+)', 
    authUser,
    function (req, res, next){

        var serviceId = parseInt(req.params.id);

        response = {
            title:"Gestion des services",
            data:{
                serviceId:serviceId,
                urls:{
                    servicesUrls: UrlsPack.services,
                }
            }
        }

        return res.render('backoffice/services/services_show', response);
    }
);

// -----------------------------------------------------------------

// PASSERELLE ------------------------------------------------------

router.get('/system/passerelle', 
    authUser,
    function (req, res, next){

        var serviceId = parseInt(req.params.id);

        response = {
            title:"Services: Passerelle",
            data:{
                urls:{
                    servicesUrls: UrlsPack.services,
                }
            }
        }

        return res.render('backoffice/services/system/passerelle/passerelle_index.ejs', response);
    }
);

// -----------------------------------------------------------------

// PASSERELLE ------------------------------------------------------

router.get('/system/journal', 
    authUser,
    function (req, res, next){

        var serviceId = parseInt(req.params.id);

        response = {
            title:"Services: Journal",
            data:{
                urls:{
                    servicesUrls: UrlsPack.services,
                }
            }
        }

        return res.render('backoffice/services/system/eventlog/eventlog_index.ejs', response);
    }
);

// -----------------------------------------------------------------

module.exports = router;
