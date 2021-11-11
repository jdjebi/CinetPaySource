// const { PrismaClient } = require('@prisma/client')
var express            = require('express');
var router             = express.Router();
const axios            = require('axios');
const passport         = require('passport');

// INIT CONF ---------------------------------------

// const prisma = new PrismaClient();

// LOGIN PAGE --------------------------------------

router.post("/login", 
  function (req, res, next){

    var response = {
      auth_success:false,
      comment:""
    };

    passport.authenticate("local", function(err, user, info){

      if(err){
        throw  err;
      }

      if(user){

        response.auth_success = true;
        response.comment = "Connexion réussie.";

        req.login(user, function(err){

          if(err){
            throw  err;
          }else{
            return res.send(response);
          }

        });

      }else{

        response.auth_success = false;
        response.comment = "Identifiant de connexion incorrecte.";

        if(err){
          throw  err;
        }else{
          return res.send(response);
        }
      }
    })(req, res, next);
  }
);

//--------------------------------------------------

module.exports = router;
