<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<!doctype html>
<html>
<head>
    <meta name="decorator" content="base-bootstrap3"/>
    <meta name="robots" content="noindex">
    <title>Authorize Application</title>

    <link type="text/css" rel="stylesheet" href="<c:url value="/css/oauth.css"/>"/>
</head>
<body>

<div class="container">
    <div class="rounded-box-wide">
        <div class="row">
            <div class="col-sm-12">
                <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>
                <c:if test="${!empty error}">
                    <p class="text-error">
                        <object type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"></object>
                            <spring:message code="${error}"/>
                    </p>
                </c:if>
                <c:if test="${empty error}">
                    <p>The application <strong>${oauthApplication.name} by ${oauthApplication.developedBy}</strong> would like the ability to interact with one of your Infusionsoft applications.</p>

                    <p><em>${oauthApplication.description}</em></p>

                    <c:if test="${fn:length(apps) > 1}">
                        <p>Which application would you like to allow <strong>${oauthApplication.name}</strong> access to?</p>
                    </c:if>
                </c:if>
            </div>
        </div>
        <c:if test="${empty error}">
            <form class="form-horizontal" action="processAuthorization" method="post">
                <input type="hidden" name="client_id" value="${fn:escapeXml(client_id)}">
                <input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}">
                <input type="hidden" name="response_type" value="${fn:escapeXml(response_type)}">
                <input type="hidden" name="requestedScope" value="${fn:escapeXml(requestedScope)}">
                <input type="hidden" name="state" value="${fn:escapeXml(state)}">

                <c:choose>
                    <c:when test="${fn:length(apps) > 1}">
                        <div class="row">
                            <div class="col-sm-12">
                                <select class="form-control chosen-select" data-placeholder="Please Select One" name="application">
                                    <option></option>
                                    <c:forEach var="app" items="${apps}">
                                        <option>${app}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>
                            Allow <strong>${oauthApplication.name}</strong> access?
                        </p>

                        <input type="hidden" name="application" value="${apps[0]}">
                    </c:otherwise>
                </c:choose>

                <div class="row">
                    <div class="col-sm-12">
                        <div class="well">
                            <button name="deny" type="submit" class="btn btn-default">Deny</button>
                            <button name="allow" type="submit" class="btn btn-success">Allow</button>
                        </div>
                    </div>
                </div>
            </form>
        </c:if>
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="<c:url value="/js/jquery-plugins/chosen-1.1.0/chosen.jquery.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/oauth-authorize.js"/>"></script>
</content>

</body>
</html>
