const passport         = require("passport");
const LocalStrategy    = require("passport-local").Strategy;
const { PrismaClient } = require('@prisma/client');
const UrlsPack         = require('../core/urls');
const axios            = require('axios');

const prisma  = new PrismaClient();

passport.serializeUser((user, done) => {
    done(null,user.id)
});

passport.deserializeUser((id, done) => {
    
    var user = null;
    var err = null;

    async function main() {
        user = await prisma.users.findMany({
            where:{
                id:id
            }
        });
        user = user[0];
    }

    main().catch(e => {
            err = e;
        })
        .finally(async () => {
            await prisma.$disconnect()
        })
        .then(() => {
            done(err,user);
    });

});

passport.use('local', new LocalStrategy(
    function(username, password, done){

        var user = null;
        var err = null;

        axios.post(UrlsPack.apiBackoffice.login,{
            username:username,
            password:password
        })
        .then(function(response){

            console.log(response.data);

            if(response.data.status == true){
                return done(null,response.data.user);       
            }else{
                return done(null, false, { message: "Une erreur c'est produite" })
            }
        })
        .catch(function(error){

            console.log(error.message);

            return done(err, false, { message: error.message });
        })

    }
));

module.exports = passport;