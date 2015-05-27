function wrapCriteriaPart(skipWrap, message) {
    var newMessage;

    if (!skipWrap) {
        newMessage = '<strong class="has-error">' + message + '</strong>';
    } else {
        newMessage = message;
    }

    return newMessage;
}

$(document).ready(function () {

    jQuery.validator.addMethod("password", function (value, element) {
        console.log('password validate');
        return this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
    }, "Doesn't meet the criteria.");

    //Validate the form
    $('#resetPasswordForm').validate(
        {
            errorElement: "span",
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                console.log('errorPlacement');
                console.log(error);
                console.log(element);

                element.after('<span class="is-icon is-icon-error form-control-feedback"><object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg" width="4" height="16"></object></span>');
                element.after(error);
            },
            rules: {
                password1: {
                    required: true,
                    password: true,
                    remote: {
                        url: "checkPasswordForLast4WithRecoveryCode",
                        type: "post",
                        data: {
                            recoveryCode: function () {
                                return $("#recoveryCode").val();
                            }
                        }
                    }
                },
                password2: {
                    required: true,
                    password: false,
                    equalTo: "#password1"
                }
            },
            messages: {
                password1: {
                    required: "Password is required.",
                    password: function (params, element) {
                        console.log('password custom');
                        console.log(params);
                        console.log(element);
                        var currPass = $(element).val();
                        console.log(currPass);

                        var nonMobileMessage = "Doesn't meet the criteria.";
                        var mobileMessage = '' +
                            wrapCriteriaPart(checkPasswordLength(currPass), 'At least 7 characters') + ', ' +
                            wrapCriteriaPart(checkPasswordNumber(currPass), '1 number') + ', ' +
                            wrapCriteriaPart(checkPasswordUpperCase(currPass), '1 uppercase letter') + ', ' +
                            wrapCriteriaPart(checkPasswordLowerCase(currPass), '1 lowercase letter');

                        return '<span class="mob-help-block hidden-sm hidden-md hidden-lg">' + mobileMessage + '</span>' +
                            '<span class="hidden-xs">' + nonMobileMessage + '</span>';
                    },
                    remote: "Password must not match any of your last 4 passwords."
                },
                password2: {
                    required: "Password is required.",
                    equalTo: "Passwords don't match."
                }
            },
            highlight: function (element) {
                //element is the input failing validation
                console.log('highlight');
                console.log(element);

                //Unhide error message
                $(element).next().removeClass("hidden");

                $(element).closest('.form-group').addClass('has-error has-feedback');
                $(element).closest('.form-group').removeClass('has-success');

                //add span icon error classes
                $(element).next().next().addClass('is-icon is-icon-error form-control-feedback');
                $(element).next().next().html('<object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg" width="4" height="16">');

                //remove span icon success classes
                $(element).next().next().removeClass('is-icon-ok');
            },
            success: function (element) {
                //element is the error element created by the framework
                console.log('success');
                console.log(element);
                console.log($(element).next());
                console.log($(element).next().next());

                $(element).closest('.form-group').addClass('has-success has-feedback');
                $(element).closest('.form-group').removeClass('has-error');

                //Hide error message
                $(element).addClass("hidden");

                //remove span icon error classes
                console.log("remove is-icon-error on");
                console.log($(element).next());
                $(element).next().removeClass('is-icon-error form-control-feedback');
                $(element).next().html('<object type="image/svg+xml" tabindex="-1" data="/img/ic-message-success.svg" width="16" height="16">');

                //add span icon success classes
                $(element).next().addClass('is-icon-ok form-control-feedback');
            }

        });

});