<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<style type="text/css">

    #editForm .control-label {
        width: 160px;
    }

    #editForm .controls {
        margin-left: 180px;
    }

</style>

<c:url var="centralUrl" value="/central/home"/>

<div id="main">
    <h2 class="apps">
        Edit Your Community Profile
    </h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin-top: 5px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form id="editForm" action="updateCommunityAccount" method="post" class="form-horizontal">
        <input name="id" type="hidden" value="${account.getId()}"/>

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

        <div class="buttonbar">
            <input type="submit" value="Save" class="btn btn-primary"/>
            <a href="${centralUrl}" class="btn">Cancel</a>
        </div>
    </form>
</div>
