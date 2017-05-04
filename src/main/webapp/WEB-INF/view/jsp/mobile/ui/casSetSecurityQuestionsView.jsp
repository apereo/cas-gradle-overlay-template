<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<c:set var="security_questions_js" value="${pageContext.request.contextPath}/js/securityQuestions.js"/>

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>
<%--@elvariable id="securityQuestions" type="java.util.List<org.apereo.cas.infusionsoft.domain.SecurityQuestion>"--%>
<%--@elvariable id="commandName" type="java.lang.String"--%>
<%--@elvariable id="flowExecutionKey" type="java.lang.String"--%>
<%--@elvariable id="securityQuestionsRequired" type="java.lang.Boolean"--%>

<head>
    <meta name="decorator" content="black-header-minimal"/>
    <meta name="robots" content="noindex">
    <title><spring:message code="security.question.title.label"/></title>
</head>
<body>

<div class="container">
    <div class="page-header">
        <div class="row">
            <div class="col-xs-12 col-sm-8 col-sm-offset-2 text-center">
                <h2>
                    <spring:message code="security.question.title.label"/>
                </h2>

                <div class="row">
                    <div class="col-xs-12 col-sm-8 col-sm-offset-2">
                        <p class="subtitle">
                            <spring:message code="security.question.sub.title.label"/>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-6 col-sm-offset-3">
            <form:form cssClass="form-horizontal" commandName="${commandName}" method="post">
                <form:errors path="*" id="msg" cssClass="text-error" element="p">
                    <p class="text-error">
                        <versioned:objectSvg tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"/>
                        <c:forEach var="error" items="${messages}">
                            ${error}
                        </c:forEach>
                    </p>
                </form:errors>

                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <input type="hidden" id="securityQuestionId" name="securityQuestionId" value="0"/>

                <div class="animationHolder">
                    <div class="form-group">
                        <div class="col-xs-12">
                            <div class="list-group security-question-list">
                                <c:forEach var="securityQuestion" items="${securityQuestions}">
                                    <a href="#" class="question list-group-item" data-question-id="${securityQuestion.id}" data-question-icon="${securityQuestion.iconPath}">
                                        <div class="text-center">
                                            <i class="ic ${securityQuestion.iconPath} hidden-xs vcenter"></i>
                                            <span class="text-center question-text vcenter">${securityQuestion.question}</span>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                        </div>
                    </div>

                    <div class="answer">
                        <div class="form-group">
                            <div class="col-xs-12 col-sm-10 col-sm-offset-1">
                                <div class="well text-center">
                                    <span class="question">
                                        <i id="question-icon"></i>
                                        <span class="text-center question-text vcenter"></span>
                                        <i id="caret" class="fa fa-caret-down pull-right"></i>
                                    </span>
                                </div>
                            </div>
                            <div class="col-xs-12 col-sm-10 col-sm-offset-1">
                                <input type="text" name="response" class="form-control" placeholder="Answer" autocomplete="off">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-xs-12 col-sm-10 col-sm-offset-1">
                                <button type="submit" name="_eventId_submit" value="submit" class="btn btn-primary btn-block"><spring:message code="button.save"/></button>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${!securityQuestionsRequired}">
                    <div class="form-group">
                        <div class="col-sm-12 text-center">
                            <button type="submit" name="_eventId_skip" class="btn btn-link skip" value="skip">skip this for now</button>
                        </div>
                    </div>
                </c:if>
            </form:form>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12 col-sm-6 col-sm-offset-3 text-center">
            <p>Need help? Call toll free:
                <c:forEach var="supportPhoneNumber" items="${supportPhoneNumbers}">
                    <br>${supportPhoneNumber}
                </c:forEach>
            </p>
        </div>
    </div>
</div>

<content tag="local_script">
    <versioned:script type="text/javascript" src="${security_questions_js}"/>
</content>

</body>