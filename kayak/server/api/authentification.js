var crypto = require("crypto")

var session_id_len = 32;
var nb_of_hash = 40000

function nbTimeHash(word){
    for(var i = 0 ; i  < nb_of_hash ; i++ )
        word = hash(word);
    return word
}
function hash(word) {
    return crypto.createHash('sha256').update(word, 'utf-8').digest('hex');
}


function createSessionId() {
    return crypto.randomBytes(Math.ceil(session_id_len / 2)).toString("hex").slice(0, session_id_len);
}


module.exports = {
    createSessionId: createSessionId,
    hash : nbTimeHash


};
