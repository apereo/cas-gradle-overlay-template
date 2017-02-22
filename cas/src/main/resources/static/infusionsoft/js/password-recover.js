$(document).ready(function () {

    //Validate the form
    $('#recoverPasswordForm').validate(
        {
            errorElement: "span",
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                element.after('<span class="is-icon is-icon-error form-control-feedback"><object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg" width="4" height="16"></object></span>');
                element.after(error);

            },
            rules: {
                recoveryCode: {
                    required: true
                }
            },
            messages: {
                recoveryCode: "Please enter the recovery code before continuing."

            },
            highlight: function (element) {
                $(element).closest('.form-group').addClass('has-error has-feedback');
            },
            success: function (element) {
                element.closest('.form-group').removeClass('has-error has-feedback');
                element.addClass("hidden");
                element.next().addClass("hidden");
            }

        });

});