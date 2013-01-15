<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="anonymousNoLogo"/>

<style type="text/css">

    #login {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #forgot-password {
        width: 336px;
        margin: 5px auto;
    }

    .alert {
        margin: -20px -20px 20px -20px;
    }

    .greetings-bg-old {
        background: #93d558; /* Old browsers */
        background: -moz-radial-gradient(center, ellipse cover,  #93d558 0%, #70bc3e 100%); /* FF3.6+ */
        background: -webkit-gradient(radial, center center, 0px, center center, 100%, color-stop(0%,#93d558), color-stop(100%,#70bc3e)); /* Chrome,Safari4+ */
        background: -webkit-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* Chrome10+,Safari5.1+ */
        background: -o-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* Opera 12+ */
        background: -ms-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* IE10+ */
        background: radial-gradient(ellipse at center,  #93d558 0%,#70bc3e 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#93d558', endColorstr='#70bc3e',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */
    }

    .greetings-bg {
        background: #93d558; /* Old browsers */
        background: -moz-radial-gradient(center, ellipse cover,  #93d558 0%, #70bc3e 100%); /* FF3.6+ */
        background: -webkit-gradient(radial, center center, 0px, center center, 100%, color-stop(0%,#93d558), color-stop(100%,#70bc3e)); /* Chrome,Safari4+ */
        background: -webkit-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* Chrome10+,Safari5.1+ */
        background: -o-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* Opera 12+ */
        background: -ms-radial-gradient(center, ellipse cover,  #93d558 0%,#70bc3e 100%); /* IE10+ */
        background: radial-gradient(ellipse at center,  #93d558 0%,#70bc3e 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#93d558', endColorstr='#70bc3e',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */
    }

    .greetings {
        margin: 0 auto;
        width: 760px;
        padding: 20px 0 20px 0;
        color: #fff;
        font-family: "HelveticaNeue-Light", sans-serif;
        font-size: 13px;
    }

    .greetings-left {
        width: 550px;
        float: left;
        padding: 0 20px 5px 0;
        border-right: 1px solid #71ba2f;
    }

    .greetings-right {
        width: 120px;
        float: left;
        padding: 0 0 0 20px;
        height: 150px;
    }

    .greetings h1 {
        font-family: "HelveticaNeue-Light", sans-serif;
        font-weight: normal;
        font-size: 26px;
        margin: 0 0 15px 0;
        line-height: 26px;
    }

    .greetings p {
        margin: 10px 0 15px 0;
        font-size: 13px;
    }

    .greetings .button-bar {
        margin: 27px 0 0 0;
    }
    .greetings .button-well {
        padding: 10px 6px 14px 6px;
        background: #6bac41;
        border-bottom: 1px solid #83c557;
        border-radius: 5px;
        margin: 5px 10px 15px 0;
    }

</style>

<c:set var="daysRemaining" value="${sessionScope.daysToMigrate}"/>

<c:if test="${!requestScope.appMigrated}">
    <div class="greetings-bg">
        <div class="greetings">
            <div class="greetings-left">
                <h1>Easier Access with One Email, One Password.</h1>
                <p>
                    It’s now easier to access all aspects of Infusionsoft with one email address and password!
                    Eliminate the hassle of multiple usernames and passwords for your Infusionsoft account(s),
                    CustomerHub, and community forums.
                </p>
                <p>
                    Not ready today? No rush. We'll count down the days you have left to switch.
                </p>

                <div class="button-bar">
                    <c:url var="registrationUrl" value="/registration/welcome"/>
                    <span class="button-well"><a href="${registrationUrl}" class="btn" style="padding-left: 20px; padding-right: 20px; font-size: 13px">Create My Infusionsoft ID</a></span>
                    <c:if test="${not empty sessionScope.refererUrl && daysRemaining > 0}">
                        <a href="${sessionScope.refererUrl}/app/authentication/login">No thanks. I'll sign in the old way.</a>
                    </c:if>
                </div>
            </div>
            <c:if test="${daysRemaining > 0}">
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
                    <div style="font-size: 18px; margin-top: 20px; text-align: center">Days left<br/> to update your login</div>
                </div>
            </c:if>
            <div style="clear: both"></div>
        </div>
    </div>
</c:if>

<c:if test="${requestScope.appMigrated}">
    <div style="height: 100px"></div>
</c:if>

<div id="biglogo" style="margin-top: 0px; height: 100px"></div>

<div id="login">
    <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
        <form:errors path="*" id="msg" cssClass="alert alert-error" element="div" />

        <div class="control-group">
            <label for="username" class="control-label">Email Address</label>
            <div class="controls">
                <form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" style="width: 266px" />
            </div>
        </div>

        <div class="control-group">
            <label for="password" class="control-label">Password</label>
            <div class="controls">
                <form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" style="width: 266px" />
            </div>
        </div>

        <input type="hidden" name="lt" value="${loginTicket}" />
        <input type="hidden" name="execution" value="${flowExecutionKey}" />
        <input type="hidden" name="_eventId" value="submit" />

        <div class="control-group" style="text-align: right">
            <label class="checkbox" style="float: left; width: 150px; text-align: left; padding-top: 7px"><input type="checkbox" name="rememberMe" id="rememberMe" value="true" /> Stay signed in</label>

            <input class="btn btn-primary" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit" />
        </div>
    </form:form>
</div>

<div id="forgot-password">
    <c:url var="forgotPasswordUrl" value="/registration/forgot"/>
    <a href="${forgotPasswordUrl}">Forgot your password?</a>
</div>

<div style="margin: 50px auto; width: 960px; height: 960px">
    <iframe src="https://infusionmedia.s3.amazonaws.com/cas/login-include.html" width="960" height="960" style="border: none"></iframe>
</div>
