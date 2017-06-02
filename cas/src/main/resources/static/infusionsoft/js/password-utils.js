//Password check stuff
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