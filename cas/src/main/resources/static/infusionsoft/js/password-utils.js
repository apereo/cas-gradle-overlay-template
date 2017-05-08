//Password check stuff
function resetCheckPassword() {
    $("#pw_length").removeClass('valid');
    $("#pw_number").removeClass('valid');
    $("#pw_upper").removeClass('valid');
    $("#pw_lower").removeClass('valid');
    $("#pw_previous").removeClass('valid');
}

function checkPasswordReq() {
    var currPass = $('#password').val();
    resetCheckPassword();
    if (currPass.length >= 7) {
        $("#pw_length").addClass('valid');
    }
    if (/\d/.test(currPass)) {
        $("#pw_number").addClass('valid');
    }
    if (/[A-Z]/.test(currPass)) {
        $("#pw_upper").addClass('valid');
    }
    if (/[a-z]/.test(currPass)) {
        $("#pw_lower").addClass('valid');
    }
}

function checkPasswordLength(currPass) {
    return currPass.length >= 7;
}

function checkPasswordNumber(currPass) {
    return /\d/.test(currPass);
}

function checkPasswordUpperCase(currPass) {
    return /[A-Z]/.test(currPass);
}

function checkPasswordLowerCase(currPass) {
    return /[a-z]/.test(currPass);
}