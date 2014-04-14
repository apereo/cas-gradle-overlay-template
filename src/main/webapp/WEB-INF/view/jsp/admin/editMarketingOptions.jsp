<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Marketing Options</title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div>
     <form:errors path="*" cssClass="errors"/>

    <form:form modelAttribute="marketingOptions" cssClass="form-horizontal" action="/app/marketingoptions/update">
        <form:hidden path="id" id="id"/>
        <div class="control-group">
            <label class="control-label" for="href"><spring:message code="marketing.href.label"/></label>

            <div class="controls">
                <form:input id="href" path="href" size="100"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="mobileImageSrcUrl"><spring:message code="marketing.mobile.image.source.url.label"/></label>

            <div class="controls">
                <form:input id="mobileImageSrcUrl" path="mobileImageSrcUrl" size="100"/>
            </div>
        </div>
        <div class="control-group">
            <label style="width: 129px" class="control-label" for="desktopImageSrcUrl"><spring:message code="marketing.desktop.image.source.url.label"/></label>

            <div class="controls">
                <form:input id="desktopImageSrcUrl" path="desktopImageSrcUrl" size="100"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"><spring:message code="marketing.enable.adds.label"/></label>

            <div class="controls">
                    <label class="checkbox">
                        <form:checkbox path="enableAds" value="enableAdds" />
                    </label>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            <a href="/app/marketingoptions/show">
                <button type="button" class="btn"><spring:message code="button.cancel"/></button>
            </a>
        </div>
    </form:form>
</div>
</body>
</html>