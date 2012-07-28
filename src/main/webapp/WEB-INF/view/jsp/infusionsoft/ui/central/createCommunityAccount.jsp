<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="modal"/>

<c:url var="centralUrl" value="/central/home"/>
<c:url var="associateUrl" value="/central/associate"/>

<script type="text/javascript">

    function linkExistingCommunityAccount() {
        var appUsername = $("#appUsername").val();
        var appPassword = $("#appPassword").val();

        $.ajax("${associateUrl}", {
            type: "POST",
            data: { appUsername: appUsername, appPassword: appPassword, appType: "community", appName: "community" },
            success: function(data) {
                alert("success");
            },
            error: function() {
                alert("error");

                $("#linkExistingProfileError").show();
            }
        });

    }

</script>

<style type="text/css">

    #register {
        width: 600px;
        padding: 30px;
        margin: 10px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

    .alert {
        margin: -20px -20px 20px -20px;
    }

    #createForm .control-label {
        width: 160px;
    }

    #createForm .controls {
        margin-left: 180px;
    }

</style>

<div class="modal-title">
    Setup Your Community Profile
</div>

<div id="register">
    <div class="alert alert-info">
        Already have a community profile?
        <a href="#linkExistingProfileDialog" data-toggle="modal">Link your existing profile to your Infusionsoft ID.</a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin-top: 5px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form id="createForm" action="createCommunityAccount" method="post" class="form-horizontal">
        <h2>Required Information</h2>
        <fieldset>
            <div class="control-group">
                <label for="displayName" class="control-label">Display Name</label>
                <div class="controls">
                    <input id="displayName" name="displayName" type="text" value="${fn:escapeXml(details.displayName)}"/>
                </div>
            </div>
            <div class="control-group">
                <label for="infusionsoftExperience" class="control-label">Infusionsoft Experience</label>
                <div class="controls">
                    <c:forEach var="i" items="${infusionsoftExperienceLevels}">
                        <c:if test="${i == details.infusionsoftExperience}">
                            <label class="radio"><input id="infusionsoftExperience" name="infusionsoftExperience" type="radio" value="${i}" checked="checked"/> <spring:message code="community.infusionsoftExperience.${i}"/></label>
                        </c:if>
                        <c:if test="${i != details.infusionsoftExperience}">
                            <label class="radio"><input id="infusionsoftExperience" name="infusionsoftExperience" type="radio" value="${i}"/> <spring:message code="community.infusionsoftExperience.${i}"/></label>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </fieldset>

        <h2>Additional Information</h2>
        <fieldset>
            <div class="control-group">
                <label for="timeZone" class="control-label">Time Zone</label>
                <div class="controls">
                    <select id="timeZone" name="timeZone" type="text">
                        <option value="-12" class="">(GMT -12:00) Eniwetok, Kwajalein</option>
                        <option value="-11" class="">(GMT -11:00) Midway Island, Samoa</option>
                        <option value="-10" class="">(GMT -10:00) Hawaii</option>
                        <option value="-9" class="">(GMT -9:00) Alaska</option>
                        <option value="-8" class="">(GMT -8:00) Pacific Time (US &amp; Canada)</option>
                        <option value="-7" class="" selected="selected">(GMT -7:00) Mountain Time (US &amp; Canada)</option>
                        <option value="-6" class="">(GMT -6:00) Central Time (US &amp; Canada), Mexico City</option>
                        <option value="-5" class="">(GMT -5:00) Eastern Time (US &amp; Canada), Bogota, Lima</option>
                        <option value="-4.5" class="">(GMT -4:30) Caracas</option>
                        <option value="-4" class="">(GMT -4:00) Atlantic Time (Canada), La Paz, Santiago</option>
                        <option value="-3.5" class="">(GMT -3:30) Newfoundland</option>
                        <option value="-3" class="">(GMT -3:00) Brazil, Buenos Aires, Georgetown</option>
                        <option value="-2" class="">(GMT -2:00) Mid-Atlantic</option>
                        <option value="-1" class="">(GMT -1:00 hour) Azores, Cape Verde Islands</option>
                        <option value="0" class="">(GMT) Western Europe Time, London, Lisbon, Casablanca</option>
                        <option value="1" class="">(GMT +1:00 hour) Brussels, Copenhagen, Madrid, Paris</option>
                        <option value="2" class="">(GMT +2:00) Kaliningrad, South Africa, Cairo</option>
                        <option value="3" class="">(GMT +3:00) Baghdad, Riyadh, Moscow, St. Petersburg</option>
                        <option value="3.5" class="">(GMT +3:30) Tehran</option>
                        <option value="4" class="">(GMT +4:00) Abu Dhabi, Muscat, Yerevan, Baku, Tbilisi</option>
                        <option value="4.5" class="">(GMT +4:30) Kabul</option>
                        <option value="5" class="">(GMT +5:00) Ekaterinburg, Islamabad, Karachi, Tashkent</option>
                        <option value="5.5" class="">(GMT +5:30) Mumbai, Kolkata, Chennai, New Delhi</option>
                        <option value="5.75" class="">(GMT +5:45) Kathmandu</option>
                        <option value="6" class="">(GMT +6:00) Almaty, Dhaka, Colombo</option>
                        <option value="6.5" class="">(GMT +6:30) Yangon, Cocos Islands</option>
                        <option value="7" class="">(GMT +7:00) Bangkok, Hanoi, Jakarta</option>
                        <option value="8" class="">(GMT +8:00) Beijing, Perth, Singapore, Hong Kong</option>
                        <option value="9" class="">(GMT +9:00) Tokyo, Seoul, Osaka, Sapporo, Yakutsk</option>
                        <option value="9.5" class="">(GMT +9:30) Adelaide, Darwin</option>
                        <option value="10" class="">(GMT +10:00) Eastern Australia, Guam, Vladivostok</option>
                        <option value="11" class="">(GMT +11:00) Magadan, Solomon Islands, New Caledonia</option>
                        <option value="12" class="">(GMT +12:00) Auckland, Wellington, Fiji, Kamchatka</option>
                    </select>
                </div>
            </div>
            <div class="control-group">
                <label for="notificationEmailAddress" class="control-label">Notification Email Address</label>
                <div class="controls">
                    <input id="notificationEmailAddress" name="notificationEmailAddress" type="text" value="${fn:escapeXml(details.notificationEmailAddress)}"/>
                </div>
            </div>
            <div class="control-group">
                <label for="twitterHandle" class="control-label">Twitter Handle</label>
                <div class="controls">
                    <input id="twitterHandle" name="twitterHandle" type="text" value="${fn:escapeXml(details.twitterHandle)}"/>
                </div>
            </div>
        </fieldset>

        <h2>Forum Rules</h2>
        <div style="height: 200px; overflow-y: scroll; border: 1px solid #DDDDDD; margin-bottom: 20px">
            <div style="padding: 15px">
                <p>
                    Registration to this forum is free! We do insist that you abide by the rules and policies detailed
                    below. If you agree to the terms, please check the 'I agree' checkbox and press the 'Complete Registration' button below.
                    If you would like to cancel the registration, click <a href="forum.php">here</a>
                    to return to the forums index.
                </p>

                <p>
                    In an effort to maintain the best possible user experience for our members, we ask that you abide by the Community Guidelines which govern your use of our forums.
                    Our online community is diverse and a variety of discussion areas are provided in order to host different types of discussions. Please keep in mind that individual guidelines posted (“stickied”) in various community areas may carry additional standards.
                    In short, be civil, have fun and be helpful.
                </p>

                <ol>
                    <li><strong>Respect others.</strong> Realize that not all users will necessarily agree with your perspective or point of view.  Keep discussions constructive and not directed against any particular group or individual participants.</li>
                    <li><strong>Protect your Infusionsoft account.</strong> Security very important to us. Never share your password or API Key to anyone on the forums. Infusionsoft staff will never require this information to access your account. If you copy and paste your API key, it will be redacted. API Keys should be treated like passwords.</li>
                    <li><strong>Do not spam.</strong> It may be tempting to promote products or services unrelated to the active discussions throughout the community. Please don’t. Users may only promote their business passively by use of the “signatures” feature. No signature (except by Infusionsoft Staff) may exceed 400 pixels in height. If you wish to connect with like-minded users and want to let people know your website, please do so in the “New Users” forum. </li>
                    <li><strong>Abide by the law.</strong> Your use of the Community Forums is governed by the laws of Arizona in the United States of America. Discussions that pertain to loss of services or relate to illegal activities will not be permitted. </li>
                    <li><strong>Be helpful.</strong> It is common courtesy to follow-up on a discussion topic with an update from when you originally posted it. This is helpful for actual participants in the thread, but also helpful for visitors who visit it later on. While not required, we’d like to ask users to be mindful of this when asking for peer to peer support. </li>
                </ol>

                <p>
                    Additionally, these terms apply to your use and access to the Infusionsoft Community:
                </p>

                <ul>
                    <li><strong>Liability:</strong> Infusionsoft and its affiliates/partners are not liable for any damage caused from advice or instructions given in the community. While this has never been an issue before, we just want to make that clear. It is possible advice may be incorrect. For critical/major support issues, please contact Infusionsoft directly at 1-866-800-0004 for assistance. </li>

                    <li><strong>Moderation &amp; Enforcement:</strong> Infusionsoft does not have a duty to review, edit or change any information posted to its forums by other users. Users may report objectionable content via the “Report” link next to an offending post and staff may review and take action on it later. Escalations can be made via email to community@infusionsoft.com. </li>

                    <li><strong>Privacy:</strong> Infusionsoft will not disclose any user information to third-parties as explained in the Privacy Policy. However, any information you post to the community will be available publicly unless otherwise stated. This includes your username, profile information and any discussions that you participate in. </li>

                    <li><strong>Copyright:</strong> All content posted to the Infusionsoft Community is to be considered the sole property of Infusionsoft and all activity is done with no expectations of royalties or license. When posting content (or links to it), infringing content will be removed without notice. This includes, but not limited to unlicensed images, music, videos, Torrents, “warez”, or any intellectual property belonging to someone else.</li>
                </ul>

                <p>
                If you have any questions, please email community@infusionsoft.com to discuss any of these terms.
                </p>

                <p>
                Thanks,
                <br>
                JOSEPH MANNA<br>
                Community Manager
                </p>
            </div>
        </div>

        <div>
            <label class="checkbox"><input name="agreeToRules" value="true" type="checkbox"/> I have read and agree to abide by the Infusionsoft CoFmmunity Forums rules.</label>
        </div>

        <div class="buttonbar" style="text-align: right">
            <a href="${centralUrl}" class="btn">Back</a>
            <input type="submit" value="Create Profile" class="btn btn-primary"/>
        </div>
    </form>
</div>

<div class="modal hide" id="linkExistingProfileDialog" style="width: 370px; margin-left: -185px">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h3>Link Your Existing Community Profile</h3>
    </div>
    <div class="modal-body">
        <div id="linkExistingProfileError" class="alert alert-error" style="display: none; margin-top: 0">
            Invalid username and password!
        </div>
        <p>Enter the username and password you were using before you created your Infusionsoft ID to link your existing community profile.</p>
        <form class="form-vertical">
            <fieldset>
                <label class="control-label" for="appUsername">Username</label>
                <div class="controls">
                    <input type="text" id="appUsername" name="appUsername" style="width: 300px"/>
                </div>
                <label class="control-label" for="appPassword">Password</label>
                <div class="controls">
                    <input type="text" id="appPassword" name="appPassword" style="width: 300px"/>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="modal-footer">
        <a href="javascript:linkExistingCommunityAccount()" class="btn btn-primary">Link Profile</a>
    </div>
</div>