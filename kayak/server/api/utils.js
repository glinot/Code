function idToString(object) {
    var res = {};
    if (Array.isArray(object)) {
        res = [];
        for (var i = 0; i < object.length; i++) {
            res[i] = idToString(object[i]);
        }
    }
    else {
        if (typeof(object) == "object") {

            if(object.bytes == null){

                for (k in object) {
                    if (object[k].bytes != null) {
                        res[k] = object[k].toString();
                    }
                    else {
                        res[k] = idToString(object[k]);
                    }
                }
            }else{
                res = object.toString();
            }
        }
        else {
            res = object;
        }
    }
    return res;
}

function idToString(object) {
    var res = {};
    if (Array.isArray(object)) {
        res = [];
        for (var i = 0; i < object.length; i++) {
            res[i] = idToString(object[i]);
        }
    }
    else {
        if (typeof(object) == "object") {

            if(object.bytes == null){

                for (k in object) {
                    if ((object[k] || {}).bytes != null) {
                        res[k] = object[k].toString();
                    }
                    else {
                        res[k] = idToString(object[k]);
                    }
                }
            }else{
                res = object.toString();
            }
        }
        else {
            res = object;
        }
    }
    return res;
}


function sanityzeEntry(obj){
    return (obj || "")+""
}
module.exports = {
    idToString: idToString,
    sanityzeEntry:sanityzeEntry,
    stringHourToIntMinute : function(str) {
        str = str.replace('h' ,':');
        str = str.replace('H' ,':');
        var a = str.split(':');
        return parseInt(a[0])*60 + parseInt(a[1]);
    },
    cloneObject : function(o){return JSON.parse(JSON.stringify(o));}
    

}