var express            = require('express');
var router             = express.Router();
var UrlsPack           = require('../../../../core/urls');
var axios              = require('axios');

// INIT CONF -----------------------------------------------------

const { guestUser, authUser } = require('../../../../core/auth');
const { response } = require('express');
const { currencies, operators } = require('../../../../core/urls');

// FUNCTIONS

function randomInt2(min,max){
    return Math.round(Math.floor(Math.random() * (max + 1 - min) + min));
}

function fakeNumber(length){
    var str = "";

    while (str.length < length)
        str += Math.floor(Math.random() * 10 + 1);
    
    return str;
}

function fakeAmount(max,min){
    return Math.round(Math.floor(Math.random() * (max - min) + min) / 10) * 10;
}

// MAIN FUNCTION

function generateTransaction(data, operators){

    let batchnumber = Date.now(); 

    let clientTransactionId = fakeNumber(8);

    let transactions = [];

    let phone, transactionId, amount, operatorIndex, operatorTrx, transaction, prefix  = null;

    let notifyUrl = "https://test.cinetpay.com";

    let operatorsData = {};

    for (let i = 0; i < operators.length; i++) {

        let operator = operators[i];

        operatorsData[operator.alias] = {
            country: operator.country.code,
            alias: operator.alias,
            currency: operator.country.currency.name,
        }

    }

    if(!data.useRandomTrxNbr){
        nbrTrxToGenerate = data.maxTrxNbrToGenerate;
    }else{
        nbrTrxToGenerate = randomInt2(1, data.maxTrxNbrToGenerate);
    }

    for (var i = 0; i < nbrTrxToGenerate; i++) {

        prefix = "+" + fakeNumber(3);

        if(data.useDefaultTrxNbr){
            phone = data.defaultTrxNbr;
        }else{
            phone = fakeNumber(10);            
        }

        transactionId = new Date().getTime() + fakeNumber(randomInt2(0,5));

        if(data.useRandomTrxAmount){
            amount = fakeAmount(10,data.maxTrxAmount);
        }else{
            amount = data.maxTrxAmount;
        }

        operatorIndex = null;
        operatorTrx = null;

        if(data.operator != "all"){

            operatorTrx = operatorsData[data.operator];

            if(!operatorTrx){
                throw new Error("operator code unknow");
            }

        }else{
            operatorIndex = randomInt2(0, operators.length - 1);
            operatorTrx = operatorsData[operators[operatorIndex].alias];
        }

        transaction = {
            transactionId: i + "@"+ transactionId,
            batchnumber: batchnumber,
            amount: amount,
            phone: phone,
            clientTransactionId: clientTransactionId,
            batchnumber: batchnumber,
            notifyUrl: notifyUrl,
            operator: operatorTrx.alias,
            country: operatorTrx.country,
            currency: operatorTrx.currency,
            prefix: prefix
        }

        transactions.push(transaction);
    }

    return {
        batchnumber:batchnumber,
        nbrTrxToGenerate:nbrTrxToGenerate,
        transactions:transactions
    };
}

// INDEX ----------------------------------------------------------

router.post('', 
    function (req, res, next){

        let data = req.body;

        axios.get(UrlsPack.apiBackoffice.operators.backendRest)

            .then((response) => {
                
                let operators = response.data;

                if(operators.length > 0){

                    genrateResults = generateTransaction(data, operators);

                    return res.send(genrateResults);

                }else{ // Si il n'y a aucune operateur

                    return res.send(null);

                }

            })
            .catch((error) => {

                console.log(error.message);

                console.log(error);

                return res.status(500).send(error.message);
            });

    }

);

module.exports = router;
