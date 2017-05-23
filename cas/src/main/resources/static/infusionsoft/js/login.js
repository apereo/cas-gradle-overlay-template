$(document).ready(function () {
    $("#username").focus();

    //Validate the form
    $('#loginForm').validate(
        {
            errorElement: "span",
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                console.log('error placement');
                element.after('<span class="is-icon is-icon-error form-control-feedback"><img src="/infusionsoft/img/ic-message-danger.svg" width="4" height="16"></span>');
                element.after(error);

            },
            rules: {
                username: {
                    required: true,
                    email: true
                },
                password: {
                    required: true,
                    password: false
                }
            },
            messages: {
                username: {
                    required: "This is a required field",
                    email: "Please enter a valid email address (e.g. email@example.com)"
                },
                password: {
                    required: "This is a required field"
                }
            },
            highlight: function (element) {
                $(element).closest('.form-group').addClass('has-error has-feedback');

                //Unhide error message
                $(element).next().removeClass("hidden");
            },
            success: function (element) {
                element.closest('.form-group').removeClass('has-error has-feedback');

                //Hide error message
                element.addClass("hidden");

                element.next().addClass("hidden");
            }

        });
});

function submitForgotPasswordForm() {
    var forgotPasswordForm = $("#forgotPasswordForm");
    var username = $("#username").val();
    if (username) {
        $(forgotPasswordForm[0]['username']).val(username);
        forgotPasswordForm.submit();
        return false;
    } else {
        return true;
    }
}