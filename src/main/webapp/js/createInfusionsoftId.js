function fadeSidesIn() {
    $('#lanyard_background').fadeIn("slow", function () {
        $('#floaterLeft').fadeIn("slow", function () {
            $('#floaterRight').fadeIn("slow");
        });
    });

}
$(document).ready(function () {














    $('#floaterLeft').hide();
    $('#floaterRight').hide();

    $('#learnMore').on("click", function (e) {
        $("#idcard").flippy({
            verso: "<div class='cardTopBack'> <div id='cardTopTextBack'>Infusionsoft ID FAQ</div></div><div id='cardBottomBack'><div class='faqQuestion'>Why do I need an Infusionsoft ID?</div><div class='faqAnswer'>Your Infusionsoft ID is a new way to sign in that allows<br/>you to access all aspects of Infusionsoft using one email<br/>address and one password.</div><div class='faqQuestion'>Are all users required to create an Infusionsoft ID?</div><div class='faqAnswer'>Yes.</div><div class='faqQuestion'>What are some of the additional benefits of Infusionsoft ID?</div><div class='faqAnswer'><ul><li>Allows access to Infusionsoft, Marketplace, CustomerHub, and Community.</li><li>Allows access to add-ons such as Sync for GMail, Sync for Outlook.</li><li>If you have multiple accounts, you no longer need a separate username and password for each account.</li></ul><hr/></div><div class='faqContact'>If you have addiontal questions,<br/>please call <em>1-877-296-7929</em></div></div>",
            direction: "LEFT",
            duration: "750",
            color_target: "#e4e4e4",
            onStart: function () {
                $('#lanyard_background').fadeOut("slow");
                $('#learnMore').fadeOut("slow", function () {
                    $('#backBtn').fadeIn("slow");
                });
                $('#floaterLeft').fadeOut("slow");
                $('#floaterRight').fadeOut("slow");
            },
            onFinish: function () {
                $('#lanyard_background').fadeIn("slow");
            },
            onReverseStart: function () {
                $('#lanyard_background').fadeOut("slow");
                $('#backBtn').fadeOut("slow", function () {
                    $('#learnMore').fadeIn("slow");
                });
                $('#floaterLeft').fadeOut("slow");
                $('#floaterRight').fadeOut("slow");
            },
            onReverseFinish: function () {
                fadeSidesIn();
            }

        });
        e.preventDefault();
    });

    $('#backBtn').on("click", function (e) {
        $('#idcard').flippyReverse();
        e.preventDefault();
    });

    $('#lanyard_background').hide();
    $('#lanyard').css({'top': "-900px"}).animate({"top": "+=0px"}, 1200, function () {
        $('#lanyard_background').fadeIn("slow", function () {
            $('#main_title').show().css({'top': "-250px"}).animate({"top": "-=12px"}, "slow", function () {
                fadeSidesIn();
            });
        });
    });

});