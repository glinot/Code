$.get("/api/report/years/" + localStorage.sportman_id, function (data) {
    data.years.forEach(function (e) {

        $("#sel_year").append("<option>" + e + "</option>")
    })

});
$("#download").click(function () {
     $.get("/api/report/"+localStorage.sportman_id , {year : parseInt($("#sel_year").val())} , function(data){
        open("/report/"+data.file_name);
     })
});