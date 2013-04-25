<c:set var="daysRemaining" value="${daysToMigrate}"/>

<style type="text/css">

    .greetings a {
        text-decoration: underline;
    }

    .greetings-bg {
        overflow: hidden;
        margin: 0;
        background: #70bc3e; /* Old browsers */
        background: -moz-radial-gradient(center, ellipse cover, #70bc3e 0%, #64b440 100%); /* FF3.6+ */
        background: -webkit-gradient(radial, center center, 0px, center center, 100%, color-stop(0%, #70bc3e), color-stop(100%, #64b440)); /* Chrome,Safari4+ */
        background: -webkit-radial-gradient(center, ellipse cover, #70bc3e 0%, #64b440 100%); /* Chrome10+,Safari5.1+ */
        background: -o-radial-gradient(center, ellipse cover, #70bc3e 0%, #64b440 100%); /* Opera 12+ */
        background: -ms-radial-gradient(center, ellipse cover, #70bc3e 0%, #64b440 100%); /* IE10+ */
        background: radial-gradient(ellipse at center, #70bc3e 0%, #64b440 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#70bc3e', endColorstr='#64b440', GradientType=1); /* IE6-9 fallback on horizontal gradient */
    }

    .greetings {
        margin: 0 auto;
        width: 900px;
        padding: 20px 0 20px 0;
        color: #fff;
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-size: 13px;
    }

    .greetings-left {
        width: 740px;
        float: left;
        padding: 0 0 5px 0;
        line-height: 1.4;
    }

    .greetings-right {
        width: 120px;
        float: left;
        margin: 16px 0 0 20px;
    }

    .greetings-right .days-remaining {
        font-size: 13px;
        margin-top: 5px;
        text-align: center;
    }

    .greetings h1 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 600;
        font-size: 26px;
        margin: 0 0 5px 0;
        line-height: 26px;
        text-align: center;
        color: #fff;
    }

    .greetings p {
        margin: 10px 0 15px 0;
        font-size: 13px;
        line-height: 20px;
        color: #fff;
    }

    .greetings .button-bar {
        margin: 25px 0 0 0;
        text-align: center;
        width: 900px;
    }

    .greetings .btn {
        background-color: #157eba;
        -moz-border-radius: 4px;
        -webkit-border-radius: 4px;
        border-radius: 4px;
        color: #fff;
        font: 600 13px 'Open Sans';
        font-family: 'Open Sans', Arial, Verdana, Sans-Serif;
        border: 1px solid #106596;
        border-bottom: 2px solid #106596;
        text-shadow: 0 1px 0 #116ca0;
        cursor: pointer;
        display: inline-block;
        line-height: normal;
        position: relative;
        text-decoration: none;
        box-shadow: none;
        -webkit-box-shadow: none;
        -moz-box-shadow: none;
        padding: 8px 0px;
        width: 250px;
    }

    .greetings .btn:hover {
        background-color: #1683c1;
        color: #FFFFFF;
    }

    .greetings .btn:active {
        top: 1px;
        border-bottom: 1px solid #106596;
        color: #fff;
    }

    .help-bar {
        background: #ddd;
        height: 40px;
    }

    .help-bar-content {
        margin: 0px auto;
        width: 900px;
        padding-top: 9px;
        text-align: center;
        font-size: 13px;
        padding-right: 27px;
    }

    .help-bar-content span {
        font-size: 15px;
        font-weight: bold;
    }


</style>

<c:if test="${!appMigrated}">
    <div class="greetings-bg">
        <div class="greetings">
            <h1>Introducing Your Infusionsoft ID!</h1>

            <div class="greetings-left">


                <p>
                    Your Infusionsoft ID allows you to sign in to Infusionsoft, CustomerHub, Marketplace and Community
                    using the same email address and password. Eliminate the hassle of having to remember separate sign-in
                    information for each account. Create your Infusionsoft ID today! All customers will need to create
                    their Infusionsoft ID by <strong><fmt:formatDate value="${migrationDate}" pattern="MMMMMMMMMM d, yyyy"/></strong>.
                </p>

                <c:if test="${not empty appUrl && daysRemaining > 0 && hittingCasDirectly}">
                    <p>
                        Not ready to make the switch? <a target="_top" href="${appUrl}/app/authentication/login">Sign in the old way.</a>
                    </p>
                </c:if>

                <div class="button-bar">
                    <c:url var="registrationUrl" value="/app/registration/welcome"/>
                    <a target="_top" href="${registrationUrl}" class="btn">Create Your Infusionsoft ID!</a>
                </div>
            </div>
            <c:set var="daysRemaining" value="${daysRemaining > 0 ? daysRemaining : 0}"/>
            <div class="greetings-right">
                <c:url var="flipCounterImage" value="/images/flip-counter-sprite.png"/>
                <div id="counter"></div>
                <script type="text/javascript">

                    $("#counter").flipCounter({
                        number:${daysRemaining + 3}, // the initial number the counter should display, overrides the hidden field
                        numIntegralDigits: 2, // number of places left of the decimal point to maintain
                        numFractionalDigits: 0, // number of places right of the decimal point to maintain
                        digitClass: "counter-digit", // class of the counter digits
                        counterFieldName: "counter-value", // name of the hidden field
                        digitHeight: 73, // the height of each digit in the flipCounter-medium.png sprite image
                        digitWidth: 59, // the width of each digit in the flipCounter-medium.png sprite image
                        imagePath: "${flipCounterImage}", // the path to the sprite image relative to your html document
                        easing: false, // the easing function to apply to animations, you can override this with a jQuery.easing method
                        duration: 10000, // duration of animations
                        onAnimationStarted: false, // call back for animation upon starting
                        onAnimationStopped: false, // call back for animation upon stopping
                        onAnimationPaused: false, // call back for animation upon pausing
                        onAnimationResumed: false // call back for animation upon resuming from pause
                    });

                    $(document).ready(function () {
                        $("#counter").flipCounter(
                                "startAnimation",
                                {
                                    number: ${daysRemaining + 3}, // the number we want to scroll from
                                    end_number: ${daysRemaining}, // the number we want the counter to scroll to
                                    easing: jQuery.easing.easeOutCubic, // this easing function to apply to the scroll.
                                    duration: 750, // number of ms animation should take to complete
                                    onAnimationStarted: function () {
                                    }, // the function to call when animation starts
                                    onAnimationStopped: function () {
                                    }, // the function to call when animation stops
                                    onAnimationPaused: function () {
                                    }, // the function to call when animation pauses
                                    onAnimationResumed: function () {
                                    } // the function to call when animation resumes from pause
                                }
                        );
                    });

                </script>
                <div class="days-remaining">Days left<br/> to create your ID</div>
            </div>
            <div style="clear: both"></div>
        </div>
        <div class="help-bar">
            <div class="help-bar-content">
                For Infusionsoft ID related issues please read our <a class="learnmore" target="learnmore" href="http://ug.infusionsoft.com/article/AA-01207/0/Infusionsoft-ID-Transition.html">help article </a> or call: <span>1-800-408-2240</span>
            </div>
        </div>
    </div>
</c:if>