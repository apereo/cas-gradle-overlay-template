<%--@elvariable id="masheryAccessToken" type="org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryAccessToken"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="oauthSearchUrl" value="${pageContext.request.contextPath}/app/mashery/userApplicationSearch?username=${username}&appName=${appName}"/>
<c:set var="viewAccessTokenJs" value="${pageContext.request.contextPath}/js/viewAccessToken.js"/>

<html>
<head>
    <title><spring:message code="view.mashery.access.token.label"/></title>
    <meta name="decorator" content="central"/>
</head>
<body>
<form:form class="form-horizontal">
    <div class="form-group">
        <label class="col-sm-2 control-label" for="token"><spring:message code="mashery.accessToken.label"/></label>

        <div class="col-sm-4">
            <p id="token" class="form-control-static">${masheryAccessToken.token}</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="client_id"><spring:message code="mashery.client_id.label"/></label>

        <div class="col-sm-4">
            <p id="client_id" class="form-control-static">${masheryAccessToken.clientId}</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="tokenType"><spring:message code="mashery.tokenType.label"/></label>

        <div class="col-sm-4">
            <p id="tokenType" class="form-control-static">${masheryAccessToken.tokenType}</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="grant_type"><spring:message code="mashery.grant_type.label"/></label>

        <div class="col-sm-4">
            <p id="grant_type" class="form-control-static">${masheryAccessToken.grantType}</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="expires"><spring:message code="mashery.expires.label"/></label>

        <div class="col-sm-4">
            <p id="expires" class="form-control-static">
                <joda:format value="${masheryAccessToken.expires}" style="SL"/>
            </p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="scope"><spring:message code="mashery.scope.label"/></label>

        <div class="col-sm-4">
            <p id="scope" class="form-control-static">${masheryAccessToken.scope}</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="user_context"><spring:message code="mashery.user_context.label"/></label>

        <div class="col-sm-4">
            <p id="user_context" class="input-xlarge form-control-static">${masheryAccessToken.userContext}</p>
        </div>
    </div>

    <c:if test="${!empty masheryAccessToken.extended}">
        <div class="form-group">
            <label class="col-sm-2 control-label" for="extended"><spring:message code="mashery.extended.label"/></label>

            <div class="col-sm-4">
                <p id="extended" class="input-xlarge form-control-static">${masheryAccessToken.extended}</p>
            </div>
        </div>
    </c:if>

    <div class="form-group">
        <div class="col-sm-6">
            <div class="pull-right">
                <a class="btn btn-default" href="${oauthSearchUrl}">Cancel</a>
                <button id="btnTest" type="button" class="btn btn-primary">
                    <i class="fa fa-play"></i>
                    Test
                </button>
            </div>
        </div>
    </div>


    <p id="testResponse" class="help-block"></p>
</form:form>

<content tag="local_script">
    <versioned:script type="text/javascript" src="${viewAccessTokenJs}"/>
</content>

</body>
</html>