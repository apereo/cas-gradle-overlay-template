<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="cs_select_css" value="/css/cs-select.css"/>
<c:url var="cs_skin_slide_css" value="/css/cs-skin-slide.css"/>
<c:url var="loginUrl" value="/login"/>

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>

<head>
    <meta name="decorator" content="login"/>
    <meta name="robots" content="noindex">
    <title><spring:message code="security.question.title.label"/></title>

    <link type="text/css" rel="stylesheet" href="${cs_select_css}"/>
    <link type="text/css" rel="stylesheet" href="${cs_skin_slide_css}"/>
</head>
<body>

<div class="container">
    <div class="rounded-box-wide">
        <div class="row">
            <div class="col-xs-12">
                <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>

                <h2><spring:message code="security.question.title.label"/></h2>

                <p class="text-info">
                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-message-info.svg" width="16" height="16"></object>
                    <spring:message code="security.question.not.set.page.instructions"/>
                </p>

                <form class="form-horizontal">
                    <input id="service" name="service" value="${service}" type="hidden"/>
                    <c:forEach var="i" varStatus="status" begin="${fn:length(user.securityQuestionResponses)}" end="${numSecurityQuestionsRequired-1}">
                        <div class="form-group">
                            <div class="col-sm-12">
                                <select class="cs-select cs-skin-slide" name="securityQuestionResponses[${i}].securityQuestion.id">
                                    <c:forEach items="${securityQuestions}" var="securityQuestion">
                                        <option value="${securityQuestion.id}">${securityQuestion.question}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-sm-12">
                                <input class="form-control" id="response" name="securityQuestionResponses[${status.index}].response" type="text"/>
                            </div>
                        </div>
                    </c:forEach>

                    <div class="form-group">
                        <div class="col-xs-12">
                            <div class="pull-right">
                                <button type="submit" class="btn btn-primary "><spring:message code="button.save"/></button>
                            </div>
                        </div>
                    </div>
                </form>

                <a href="${loginUrl}">Back to Sign In</a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="need-help col-md-12">
            <p>Need help? Call toll free:
                <c:forEach var="supportPhoneNumber" items="${supportPhoneNumbers}">
                    <br>${supportPhoneNumber}
                </c:forEach>
            </p>
        </div>
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="<c:url value="/js/classie.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/selectFx.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/securityQuestions.js"/>"></script>
</content>

</body>