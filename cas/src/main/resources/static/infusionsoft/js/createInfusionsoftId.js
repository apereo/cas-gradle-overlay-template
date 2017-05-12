$(document).ready(function () {

    $('.linkToExisting').click(function() {
        $('#linkToExistingForm').submit();
        return false;
    });

    document.getElementById('eula').addEventListener("click", checkTheEula);
    document.getElementById('acceptTerms').addEventListener("click", acceptTerms);

    initLightBox();
});

var CONTAINER_ID = 'lightbox-terms';

function initLightBox() {

    document.getElementById('legal').addEventListener("scroll", checkScrollHeight);

    function checkScrollHeight() {
        var termsDiv = document.getElementById('legal');
        if ((termsDiv.scrollTop + termsDiv.offsetHeight) >= termsDiv.scrollHeight){
            document.getElementById('acceptTerms').disabled = false;
        }
    }
}

function checkTheEula(e) {
    e.preventDefault();
    openLightBox();
}

function openLightBox() {
    var $modal = $('#' + CONTAINER_ID);
    $modal.modal('show');
}

function acceptTerms() {
    var eulaCheckbox = document.getElementById('eula');
    eulaCheckbox.checked = true;

    var $modal = $('#' + CONTAINER_ID);
    $modal.modal('hide');

    document.getElementById('eula').removeEventListener("click", checkTheEula);
}