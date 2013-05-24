<c:set var="daysRemaining" value="${daysToMigrate}"/>

<style type="text/css">

    .greetings a {
        text-decoration: underline;
    }

    .greetings-bg {
        overflow: hidden;
        margin: 0;
        background: #fffd9c;
        height: 274px;
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
        float: left;
        padding: 0 0 5px 0;
        line-height: 1.4;
        width: 880px;
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
        color: #444;
    }

    .greetings h1 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 600;
        font-size: 30px;
        margin: 0 0 10px 0;
        line-height: 30px;
        text-align: center;
        color: #de4326;
    }

    .greetings p {
        margin: 10px 0 15px 0;
        font-size: 14px;
        line-height: 20px;
        color: #444;
    }

    .greetings .button-bar {
        margin: 31px 0 10px 0;
        text-align: center;
        width: 900px;
    }

    .greetings .btn {
        background-color: #157eba;
        -moz-border-radius: 4px;
        -webkit-border-radius: 4px;
        border-radius: 4px;
        color: #fff;
        font: 600 14px 'Open Sans';
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

    .highlight {
        text-decoration: underline;
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
        font-size: 14px;
    }

    .help-bar-content span {
        font-size: 15px;
        font-weight: bold;
    }

    .greetings .skip {
        background-color: #70BD49;
        -moz-border-radius: 4px;
        -webkit-border-radius: 4px;
        border-radius: 4px;
        color: #fff;
        font: 600 14px 'Open Sans';
        font-family: 'Open Sans', Arial, Verdana, Sans-Serif;
        border: 1px solid #4E972A;
        border-bottom: 2px solid #4E972A;
        text-shadow: 0 1px 0 rgb(87, 155, 60);
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

    .greetings .skip:hover {
        background-color: #77C94E;
        color: #fff;
    }

    .greetings .skip:active {
        top: 1px;
        border-bottom: 1px solid #4E972A;
        color: #fff;
    }

    .button-bar span {
        color: #666;
        display: inline-block;
        padding: 0px 15px;
    }


</style>

<c:if test="${!appMigrated}">

    <div class="greetings-bg">
        <div class="greetings">
            <h1>We Have Changed How You Sign In</h1>

            <div class="greetings-left">

                <p>Introducing <strong>Infusionsoft ID!</strong> We have eliminated the hassle of having a separate username and password for each of your accounts. With Infusionsoft ID you can use the same <span class="highlight">email address</span> and password to access Infusionsoft, CustomerHub, Marketplace, Community and more.</p>

                <div class="button-bar">
                    <c:url var="registrationUrl" value="/app/registration/welcome"/>
                    <a target="_top" href="${registrationUrl}" class="btn skip">Create Your New Infusionsoft ID!</a>
                    <c:if test="${not empty appUrl && hittingCasDirectly}">
                        <span>OR</span>
                        <a target="_top" href="${appUrl}/app/authentication/login" class="btn">Sign In The Old Way For Now</a>
                    </c:if>
                </div>
            </div>

            <div style="clear: both"></div>
        </div>
        <div class="help-bar">
            <div class="help-bar-content">
                For Infusionsoft ID related issues please read our <a class="learnmore" target="learnmore" href="http://ug.infusionsoft.com/article/AA-01207/0/Infusionsoft-ID-Transition.html">help article </a>, watch our <a class="learnmore" target="learnmore" href="http://helpcenter.infusionsoft.com/api/popUpVideo.php?id=2312711342001">video tutorial</a> or call: <span>1-800-408-2240</span>
            </div>
        </div>
    </div>

</c:if>