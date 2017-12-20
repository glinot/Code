/* On charge par défaut la partie training book */
$(document).ready(function () {
  loadInElem("coach_sportman_training_book.html", "#home")
});

/* On charge la bonne partie lorsqu'on click */
$(".top-nav-bar > li ").click(function () {
    var target = $(this).attr("link-to-put");
    loadInElem(target, "#home");
});