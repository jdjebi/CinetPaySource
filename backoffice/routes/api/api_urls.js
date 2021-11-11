var express              = require('express');
var router               = express.Router();
var UrlsPack             = require('../../core/urls');

router.get('', function (req, res, next){

    res.send(UrlsPack);
               
});

module.exports = router;