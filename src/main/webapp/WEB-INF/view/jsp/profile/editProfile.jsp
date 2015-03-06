<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:url var="centralUrl" value="/app/central/home"/>
<c:url var="changePasswordUrl" value="/app/profile/changePassword"/>

<%--@elvariable id="user" type="com.infusionsoft.cas.domain.User"--%>
<%--@elvariable id="securityQuestions" type="java.util.List<SecurityQuestion>"--%>
<%--@elvariable id="securityQuestionResponses" type="java.util.List<SecurityQuestionResponse>"--%>
<%--@elvariable id="numSecurityQuestionsRequired" type="java.lang.integer"--%>

<html>
<head>
    <meta name="decorator" content="central"/>
    <title><spring:message code="editprofile.title.label"/></title>
</head>

<body>
<p>
    Edit the information that you use to sign into all of your accounts.
</p>

<div role="tabpanel">
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active">
            <a href="#personal" aria-controls="personal" role="tab" data-toggle="tab">Personal</a>
        </li>
        <li role="presentation">
            <a href="#security" aria-controls="security" role="tab" data-toggle="tab">Security</a>
        </li>
    </ul>

    <form:form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal" role="form" modelAttribute="editProfileForm">
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane fade in active" id="personal">
            <div class="form-group">
                <label class="col-sm-2 control-label">Infusionsoft ID</label>

                <div class="col-sm-4">
                    <p class="form-control-static">
                            ${fn:escapeXml(user != null ? user.username : '')}
                        <span class="help-block"><a href="${changePasswordUrl}">Change Password</a></span>
                    </p>
                </div>

            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label" for="firstName">First Name</label>

                <div class="col-sm-4">
                    <input class="form-control" id="firstName" name="firstName" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label" for="lastName">Last Name</label>

                <div class="col-sm-4">
                    <input class="form-control" id="lastName" name="lastName" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                </div>
            </div>
        </div>
        <div role="tabpanel" class="tab-pane fade" id="security">
            <c:forEach var="securityQuestionResponse" items="${user.securityQuestionResponses}" varStatus="status">
                <c:if test="${status.index < fn:length(user.securityQuestionResponses)}">
                    <input type="hidden" name="securityQuestionResponses[${status.index}].id" value="${securityQuestionResponse.id}">

                    <div class="form-group">
                        <label class="col-sm-4 control-label" for="securityQuestion">${securityQuestionResponse.securityQuestion.question}</label>

                        <div class="col-sm-2">
                            <input class="form-control" id="securityQuestion" name="securityQuestionResponses[${status.index}].response" value="${securityQuestionResponse.response}" type="text"/>
                        </div>
                    </div>
                </c:if>
            </c:forEach>

            <c:forEach var="i" varStatus="status" begin="${fn:length(user.securityQuestionResponses)}" end="${numSecurityQuestionsRequired-1}">
                <div class="form-group">
                    <div class="col-sm-4 ">
                        <select class="form-control" name="securityQuestionResponses[${i}].securityQuestion.id">
                            <c:forEach items="${securityQuestions}" var="securityQuestion">
                                <option value="${securityQuestion.id}">${securityQuestion.question}</option>
                            </c:forEach>
                        </select>
                    </div>


                    <div class="col-sm-2">
                        <input class="form-control" id="response" name="securityQuestionResponses[${status.index}].response" type="text"/>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="form-group">
            <div class="col-sm-6">
                <div class="pull-right">
                    <a class="btn btn-default" href="${centralUrl}">
                        <spring:message code="button.cancel"/>
                    </a>
                    <button type="submit" class="btn btn-primary "><spring:message code="button.save"/></button>
                </div>
            </div>
        </div>

        </form:form>
    </div>
</div>
</body>
</html>