<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="plain"/>

<style type="text/css">

    #access {
        width: 600px;
        padding: 30px;
        margin: 40px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

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

    #instructions {
        font-size: 11px;
        font-style: italic;
        color: #ccc;
        text-align: center;
        margin: 15px auto;
    }

    .controls input[type=text], .controls input[type=password] {
        width: 316px;
    }

    .alert {
        margin: -20px -20px 20px -20px;
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
<div id="modalheaderbg">
    <div id="modalheader">&nbsp;</div>
</div>
<div class="wrapper">
    <div>

        <div id="access_multi">
            <div class="accessText">The application <strong>${masheryApplication.name}</strong> by ${masheryMember.displayName} would like the ability to interact with one of your Infusionsoft applications.<br/>
                <form style="text-align:center; margin:30px 0 ;" action="processAuthorization" method="post">
                    <input type="hidden" name="client_id" value="${client_id}">
                    <input type="hidden" name="redirect_uri" value="${redirect_uri}">
                    <input type="hidden" name="response_type" value="${response_type}">
                    <c:choose>
                        <c:when test="${fn:length(apps) > 1}">
                            <br/>
                            Which application would you like to allow <strong>${masheryApplication.name}</strong> access to?

                            <div class="selectApp">
                                <select class="inf-select default-input field-valid" name="scope">
                                    <option value="">Please Select One</option>
                                    <c:forEach var="app" items="${apps}">
                                        <option value="${app}">${app}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <br/>
                            Allow <strong>${masheryApplication.name}</strong> access?

                            <input type="hidden" name="scope" value="${apps[0]}">
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
