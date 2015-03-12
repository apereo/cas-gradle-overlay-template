<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="listSecurityQuestionUrl" value="/app/securityquestion/list"/>
<c:url var="updateSecurityQuestionUrl" value="/app/securityquestion/update"/>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Survey Question</title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div>
    <form:errors path="*" cssClass="errors"/>

    <form:form modelAttribute="securityQuestion" cssClass="form-horizontal" action="${updateSecurityQuestionUrl}" role="form">
        <form:hidden path="id" id="id"/>
        <div class="form-group">
            <label for="question" class="col-sm-2 control-label"><spring:message code="security.question.text.label"/></label>

            <div class="col-sm-4">
                <form:input cssClass="form-control" id="question" path="question" size="100"/>
            </div>
        </div>
        <div class="form-group">
            <label for="iconPath" class="col-sm-2 control-label"><spring:message code="security.question.icon.label"/></label>

            <div class="col-sm-4">
                <form:input cssClass="form-control" id="iconPath" path="iconPath" size="100"/>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-4">
                <div class="checkbox">
                    <label>
                        <form:checkbox path="enabled" value="enabled"/> <spring:message code="security.question.enabled.label"/>
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-6">
                <div class="pull-right">
                    <a class="btn btn-default" href="${listSecurityQuestionUrl}">
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