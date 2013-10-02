<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="plain"/>

<style type="text/css">

    #access_multi {
        width: 600px;
        padding: 30px;
        margin: 40px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

    .accessText {
        text-align: center;
        font-size: 16px;
        margin: 45px auto 0px;
        width: 500px;
    }


    .controls input[type=text], .controls input[type=password] {
        width: 316px;
    }

    .buttons {
        margin: 50px auto 20px;
        text-align: center;
    }

    .btn-primary.access-btn {
        width: 150px;
    }

    .btn-secondary.access-btn {
        width: 150px;
    }

    select {
        border: 1px solid #CCCCCC;
        border-radius: 3px 3px 3px 3px;
        color: #444444;
        display: inline-block;
        font-size: 13px;

        padding: 4px;
        width: 250px;
        margin: 0 auto;
    }

    .selectApp {
        margin: 0 auto;
        display: inline-block;
    }

</style>
<div id="greenheaderbg">
    <div id="greenheader">&nbsp;</div>
</div>
<div class="wrapper">
    <div>

        <div id="access_multi">
            <div class="accessText">
                <p>
                    The application <strong>${masheryApplication.name}</strong> by <strong>${masheryMember.displayName}</strong> would like the ability to interact with one of your Infusionsoft applications.
                </p>

                <p style="font-size: 14px;">
                    <em>${masheryApplication.description}</em>
                </p>

                <form style="text-align:center; margin:30px 0 ;" action="processAuthorization" method="post">
                    <input type="hidden" name="client_id" value="${fn:escapeXml(client_id)}">
                    <input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}">
                    <input type="hidden" name="response_type" value="${fn:escapeXml(response_type)}">
                    <input type="hidden" name="requestedScope" value="${fn:escapeXml(requestedScope)}">
                    <input type="hidden" name="state" value="${fn:escapeXml(state)}">
                    <c:choose>
                        <c:when test="${fn:length(apps) > 1}">
                            <p>
                                Which application would you like to allow <strong>${masheryApplication.name}</strong> access to?
                            </p>


                            <div class="selectApp">
                                <p>
                                    <select class="inf-select default-input field-valid" name="application">
                                        <option value="">Please Select One</option>
                                        <c:forEach var="app" items="${apps}">
                                            <option value="${app}">${app}</option>
                                        </c:forEach>
                                    </select>
                                </p>
                            </div>

                        </c:when>
                        <c:otherwise>
                            <p>
                                Allow <strong>${masheryApplication.name}</strong> access?
                            </p>

                            <input type="hidden" name="application" value="${apps[0]}">
                        </c:otherwise>
                    </c:choose>

                    <div class="buttons">
                        <input name="deny" value="Deny" class="btn btn-secondary access-btn" type="submit">
                        <input name="allow" value="Allow" class="btn btn-primary access-btn" type="submit">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
