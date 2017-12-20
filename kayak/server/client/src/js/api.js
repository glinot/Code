var API = {

  getUserProfile: function(callback) {

    $.get("/api/profile/me",function(data){
        callback(data);
    });

  }


};
