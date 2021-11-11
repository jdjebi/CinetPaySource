class TrxPlayer{
    constructor(urls){
        this.urls = urls;
    }

    getUrl(scope,data){
        
        var params = null;

        if(scope == 'ALL'){
            let scope = 'ALL';
            let id = data.gameTrxId;
            params = `?scope=${scope}&gametrx=${id}`;
        }
        
        return this.urls.player_url +  params;
    }
}

module.exports = TrxPlayer;