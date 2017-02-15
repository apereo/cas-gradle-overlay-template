$(document).ready(function () {

    $('.linkToExisting').click(function() {
        $('#linkToExistingForm').submit();
        return false;
    });

    initLightBox();

});

var CONTAINER_ID = 'lightbox-terms';

function initLightBox() {

    document.getElementById('legal').addEventListener("scroll", checkScrollHeight);

    function checkScrollHeight(){
        var termsDiv = document.getElementById('legal');
        if ((termsDiv.scrollTop + termsDiv.offsetHeight) >= termsDiv.scrollHeight){
            document.getElementById('acceptTerms').disabled = false;
        }
    }
}

function openLightBox() {
    var $modal = $('#' + CONTAINER_ID);
    $modal.modal('show');
}

function acceptedTerms() {
    var eulaCheckbox = document.querySelector('#eula');
    eulaCheckbox.disabled = false;
    eulaCheckbox.checked = true;

    var $modal = $('#' + CONTAINER_ID);
    $modal.modal('hide');
}