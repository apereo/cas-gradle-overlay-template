<%--@elvariable id="client_id" type="java.lang.String"--%>
<%--@elvariable id="redirect_uri" type="java.lang.String"--%>
<%--@elvariable id="response_type" type="java.lang.String"--%>
<%--@elvariable id="requestedScope" type="java.lang.String"--%>
<%--@elvariable id="state" type="java.lang.String"--%>
<%--@elvariable id="error" type="java.lang.String"--%>
<%--@elvariable id="oauthApplication" type="com.infusionsoft.cas.oauth.dto.OAuthApplication"--%>
<%--@elvariable id="apps" type="List<com.infusionsoft.cas.domain.UserAccount>"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:set var="infusionsoftIdImage" value="${pageContext.request.contextPath}/img/infusionsoft_Id.png"/>

<!doctype html>
<html>
<head>
    <meta name="decorator" content="black-header-minimal"/>
    <meta name="robots" content="noindex">
    <title>Authorize Application</title>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-xs-12 text-center">
            <div class="page-header">
                <%--Just a blank page header to give proper spacing--%>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-9 col-md-6 col-lg-5 col-centered">
            <div class="panel panel-default">
                <div class="panel-body">
                    <form action="processAuthorization" role="form" method="post">
                        <input type="hidden" name="client_id" value="${fn:escapeXml(client_id)}">
                        <input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}">
                        <input type="hidden" name="response_type" value="${fn:escapeXml(response_type)}">
                        <input type="hidden" name="requestedScope" value="${fn:escapeXml(requestedScope)}">
                        <input type="hidden" name="state" value="${fn:escapeXml(state)}">

                        <c:choose>
                            <c:when test="${!empty error}">
                                <p class="text-error">
                                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"></object>
                                    <spring:message code="${error}"/>
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p>The application <strong>${oauthApplication.name} by ${oauthApplication.developedBy}</strong> would like the ability to interact with one of your Infusionsoft applications.</p>

                                <blockquote>
                                    <p><em>${oauthApplication.description}</em></p>
                                </blockquote>

                                <c:choose>
                                    <c:when test="${fn:length(apps) > 1}">
                                        <p>Which application would you like to allow <strong>${oauthApplication.name}</strong> access to?</p>

                                        <div class="form-group">
                                            <select class="form-control chosen-select" data-placeholder="Please Select One" name="application">
                                                <option></option>
                                                <c:forEach var="app" items="${apps}">
                                                    <option>${app}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p>
                                            Allow <strong>${oauthApplication.name}</strong> access?
                                        </p>

                                        <input type="hidden" name="application" value="${apps[0]}">
                                    </c:otherwise>
                                </c:choose>

                            </c:otherwise>
                        </c:choose>

                        <div class="form-group pull-right">
                            <button name="deny" type="submit" class="btn btn-default">Deny</button>
                            <button name="allow" type="submit" class="btn btn-primary">Allow</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<content tag="local_script">

</content>

</body>
</html>
