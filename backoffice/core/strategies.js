var strategies = {
    databases:[
        {label:"PostgreSQL",code:"postgres"},
        {label:"MongoDB",code:"mongodb"},
        {label:"ElasticSearch",code:"elastic"}
    ],

    insertion:[
        {label:"Indépendante",code:"onetoone"},
        {label:"Massive",code:"massive"},
        {label:"Par transaction",code:"transaction"}
    ],

    info:{
        database:{
            postgres:"PostgreSQL",
            mongodb:"MongoDB",
            elastic:"ElasticSearch",
        },
        insertion:{
            onetoone:"Indépendante",
            massive:"Massive",
            transaction:"Par transaction",
        }
    }
};

module.exports = strategies;