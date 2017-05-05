<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="marketingOptionShow" value="${pageContext.request.contextPath}/app/marketingoptions/show"/>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Marketing Options</title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div>
    <form:errors path="*" cssClass="errors"/>

    <form:form modelAttribute="marketingOptions" cssClass="form-horizontal" action="/app/marketingoptions/update" role="form">
        <form:hidden path="id" id="id"/>
        <div class="form-group">
            <label for="href" class="col-sm-2 control-label"><spring:message code="marketing.href.label"/></label>

            <div class="col-sm-4">
                <form:input cssClass="form-control" id="href" path="href" size="100"/>
            </div>
        </div>
        <div class="form-group">
            <label for="mobileImageSrcUrl" class="col-sm-2 control-label"><spring:message code="marketing.mobile.image.source.url.label"/></label>

            <div class="col-sm-4">
                <form:input cssClass="form-control" id="mobileImageSrcUrl" path="mobileImageSrcUrl" size="100"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label" for="desktopImageSrcUrl"><spring:message code="marketing.desktop.image.source.url.label"/></label>

            <div class="col-sm-4">
                <form:input cssClass="form-control" id="desktopImageSrcUrl" path="desktopImageSrcUrl" size="100"/>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-4">
                <div class="checkbox">
                    <label>
                        <form:checkbox path="enableAds" value="enableAdds"/> <spring:message code="marketing.enable.adds.label"/>
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-6">
                <div class="pull-right">
                    <a class="btn btn-default" href="${marketingOptionShow}">
                        <spring:message code="button.cancel"/>
                    </a>
                    <button type="submit" class="btn btn-primary "><spring:message code="button.save"/></button>
                </div>
            </div>
        </div>
    </form:form>
</div>
</body>
</html>