$(document).ready(function() {

    $("#username").focus();

    //Validate the form
    $('#forgotPasswordForm').validate(
        {
            errorElement: "span",
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                element.after('<span class="is-icon is-icon-error form-control-feedback"><object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg" width="4" height="16"></object></span>');
                element.after(error);

            },
            rules: {
                username: {
                    required: true,
                    email: true
                }
            },
            messages: {
                username: {
                    required: "Email Address is required.",
                    email: "Please enter a valid email address (e.g. email@example.com)"
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