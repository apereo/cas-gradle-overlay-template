<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="newSecurityQuestionUrl" value="/app/securityquestion/create"/>
<c:url var="editSecurityQuestionUrl" value="/app/securityquestion/edit"/>
<c:url var="deleteSecurityQuestionUrl" value="/app/securityquestion/delete"/>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--@elvariable id="securityQuestions" type="java.util.List<SecurityQuestion>"--%>

<html>
<head>
    <title>Security Questions</title>
    <meta name="decorator" content="central"/>
</head>
<body>

<h1 style="display: block" class="bounce animated">Animate Me</h1>

<div>
    <form:errors path="*" cssClass="errors"/>
    <div class="panel panel-default">
        <c:if test="${fn:length(securityQuestions) > 0}">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>
                            <spring:message code="security.question.text.label"/>
                        </th>
                        <th>
                            <spring:message code="security.question.enabled.label"/>
                        </th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="securityQuestion" items="${securityQuestions}">
                        <tr>
                            <td>
                                <i class="fa ${securityQuestion.iconPath}"></i>
                                <%--<object type="image/svg+xml" tabindex="-1" data="${securityQuestion.iconPath}" width="16" height="16"></object>--%>
                                    ${securityQuestion.question}
                            </td>

                            <td>
                                <c:if test="${securityQuestion.enabled}">
                                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-message-success.svg" width="16" height="16"></object>
                                </c:if>
                                <c:if test="${!securityQuestion.enabled}">
                                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg" width="16" height="16"></object>
                                </c:if>
                            </td>
                            <td>
                                <div class="btn-group btn-group-xs">
                                    <a class="btn btn-xs btn-primary" href="${editSecurityQuestionUrl}/${securityQuestion.id}"><spring:message code="security.question.edit.label"/></a>
                                    <a class="btn btn-xs btn-danger" href="${deleteSecurityQuestionUrl}/${securityQuestion.id}"><spring:message code="security.question.delete.label"/></a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>

        <c:if test="${fn:length(securityQuestions) == 0}">
            <div class="panel-body">
                <p>
                    No security questions have been setup please add one.
                </p>
            </div>
        </c:if>
        <div class="panel-footer">
            <a class="btn btn-primary" href="${newSecurityQuestionUrl}">Add Question</a>
        </div>
    </div>
</body>
</html>