<c:set var="daysRemaining" value="${sessionScope.daysToMigrate}"/>

<style type="text/css">

    .greetings a {
        text-decoration: underline;
    }

    .greetings-bg {
        height: 220px;
        overflow: hidden;
        margin: 0;
        background: #70bc3e; /* Old browsers */
        background: -moz-radial-gradient(center, ellipse cover,  #70bc3e 0%, #64b440 100%); /* FF3.6+ */
        background: -webkit-gradient(radial, center center, 0px, center center, 100%, color-stop(0%,#70bc3e), color-stop(100%,#64b440)); /* Chrome,Safari4+ */
        background: -webkit-radial-gradient(center, ellipse cover,  #70bc3e 0%,#64b440 100%); /* Chrome10+,Safari5.1+ */
        background: -o-radial-gradient(center, ellipse cover,  #70bc3e 0%,#64b440 100%); /* Opera 12+ */
        background: -ms-radial-gradient(center, ellipse cover,  #70bc3e 0%,#64b440 100%); /* IE10+ */
        background: radial-gradient(ellipse at center,  #70bc3e 0%,#64b440 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#70bc3e', endColorstr='#64b440',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */
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
        width: 760px;
        float: left;
        padding: 0 0 5px 0;
        line-height: 1.4;
    }

    .greetings-right {
        width: 120px;
        float: left;
        margin: 0 0 0 20px;
        height: 150px;
    }

    .greetings-right .days-remaining {
        font-size: 14px;
        margin-top: 10px;
        text-align: center;
    }

    .greetings h1 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: bold;
        font-size: 26px;
        margin: 0 0 10px 0;
        line-height: 26px;
        text-align: center;
        color: #fff;
    }

    .greetings p {
        margin: 10px 0 15px 0;
        font-size: 13px;
    }

    .greetings .button-bar {
        margin: 22px 0 0 0;
        text-align: center;
        width: 900px;
    }

    .greetings .btn {
        text-decoration: none;
        padding: 10px 30px;
        color: #fff;
        text-shadow: none;
        border: 1px solid #1375B5;
        background: #1788bf; /* Old browsers */
        background: -moz-linear-gradient(top, #1788bf 0%, #1375b5 100%); /* FF3.6+ */
        background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#1788bf), color-stop(100%,#1375b5)); /* Chrome,Safari4+ */
        background: -webkit-linear-gradient(top, #1788bf 0%,#1375b5 100%); /* Chrome10+,Safari5.1+ */
        background: -o-linear-gradient(top, #1788bf 0%,#1375b5 100%); /* Opera 11.10+ */
        background: -ms-linear-gradient(top, #1788bf 0%,#1375b5 100%); /* IE10+ */
        background: linear-gradient(to bottom, #1788bf 0%,#1375b5 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#1788bf', endColorstr='#1375b5',GradientType=0 ); /* IE6-9 */
    }

    .greetings .btn:hover {
        background: #3291bc; /* Old browsers */
        background: -moz-linear-gradient(top,  #3291bc 0%, #297db2 100%); /* FF3.6+ */
        background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#3291bc), color-stop(100%,#297db2)); /* Chrome,Safari4+ */
        background: -webkit-linear-gradient(top,  #3291bc 0%,#297db2 100%); /* Chrome10+,Safari5.1+ */
        background: -o-linear-gradient(top,  #3291bc 0%,#297db2 100%); /* Opera 11.10+ */
        background: -ms-linear-gradient(top,  #3291bc 0%,#297db2 100%); /* IE10+ */
        background: linear-gradient(to bottom,  #3291bc 0%,#297db2 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#3291bc', endColorstr='#297db2',GradientType=0 ); /* IE6-9 */
    }

</style>

<c:if test="${!requestScope.appMigrated}">
    <div class="greetings-bg">
        <div class="greetings">
            <h1>Introducing Your Infusionsoft ID!</h1>
            <div class="greetings-left">
                <p>
                    Your Infusionsoft ID can be used to sign in to Infusionsoft, CustomerHub, Marketplace and Community.
                    Create your Infusionsoft ID today! All customers will need to create their Infusionsoft ID by
                    <strong><fmt:formatDate value="${sessionScope.migrationDate}" pattern="MMMMMMMMMM d, yyyy"/></strong>.
                </p>
                <p>
                    <c:if test="${not empty sessionScope.refererUrl && daysRemaining > 0 && hittingCasDirectly}">
                        Not ready to make the switch? <a target="_top" href="${sessionScope.refererUrl}/app/authentication/login">Sign in the old way.</a>
                    </c:if>
                    Have questions about Infusionsoft ID?
                    <a class="learnmore" target="learnmore" href="http://ug.infusionsoft.com/article/AA-01207/0/Infusionsoft-ID-Transition.html">Learn more.</a>
                </p>

                <div class="button-bar">
                    <c:url var="registrationUrl" value="/registration/welcome"/>
                    <a target="_top" href="${registrationUrl}" class="btn" style="padding-left: 20px; padding-right: 20px; font-size: 13px">Create Your Infusionsoft ID!</a>
                </div>
            </div>
            <c:set var="daysRemaining" value="${daysRemaining > 0 ? daysRemaining : 0}"/>
            <div class="greetings-right">
                <c:url var="flipCounterImage" value="/images/flip-counter-sprite.png"/>
                <div id="counter"></div>
                <script type="text/javascript">

                    $("#counter").flipCounter({
                        number:${daysRemaining + 3}, // the initial number the counter should display, overrides the hidden field
                        numIntegralDigits:2, // number of places left of the decimal point to maintain
                        numFractionalDigits:0, // number of places right of the decimal point to maintain
                        digitClass:"counter-digit", // class of the counter digits
                        counterFieldName:"counter-value", // name of the hidden field
                        digitHeight:73, // the height of each digit in the flipCounter-medium.png sprite image
                        digitWidth:59, // the width of each digit in the flipCounter-medium.png sprite image
                        imagePath:"${flipCounterImage}", // the path to the sprite image relative to your html document
                        easing: false, // the easing function to apply to animations, you can override this with a jQuery.easing method
                        duration:10000, // duration of animations
                        onAnimationStarted:false, // call back for animation upon starting
                        onAnimationStopped:false, // call back for animation upon stopping
                        onAnimationPaused:false, // call back for animation upon pausing
                        onAnimationResumed:false // call back for animation upon resuming from pause
                    });

                    $(document).ready(function() {
                        $("#counter").flipCounter(
                                "startAnimation",
                                {
                                    number: ${daysRemaining + 3}, // the number we want to scroll from
                                    end_number: ${daysRemaining}, // the number we want the counter to scroll to
                                    easing: jQuery.easing.easeOutCubic, // this easing function to apply to the scroll.
                                    duration: 750, // number of ms animation should take to complete
                                    onAnimationStarted: function() { }, // the function to call when animation starts
                                    onAnimationStopped: function() { }, // the function to call when animation stops
                                    onAnimationPaused: function() { }, // the function to call when animation pauses
                                    onAnimationResumed: function() { } // the function to call when animation resumes from pause
                                }
                        );
                    });

                </script>
                <div class="days-remaining">Days left<br/> to create your ID</div>
            </div>
            <div style="clear: both"></div>
        </div>
    </div>
</c:if>