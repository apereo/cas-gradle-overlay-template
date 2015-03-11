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

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>
<%--@elvariable id="securityQuestions" type="java.util.List<com.infusionsoft.cas.domain.SecurityQuestion>"--%>
<%--@elvariable id="commandName" type="java.lang.String"--%>
<%--@elvariable id="flowExecutionKey" type="java.lang.String"--%>
<%--@elvariable id="securityQuestionsRequired" type="java.lang.Boolean"--%>

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

                <form:form cssClass="form-horizontal" commandName="${commandName}" method="post">
                    <form:errors path="*" id="msg" cssClass="text-error" element="p">
                        <p class="text-error">
                            <object type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"></object>
                            <c:forEach var="error" items="${messages}">
                                ${error}
                            </c:forEach>
                        </p>
                    </form:errors>

                    <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                    <input type="hidden" name="_eventId" value="submit"/>

                    <div class="form-group">
                        <div class="col-sm-12">
                            <form:select path="securityQuestionId" cssClass="cs-select cs-skin-slide" items="${securityQuestions}" itemLabel="question" itemValue="id"/>
                        </div>

                        <div class="col-sm-12">
                            <form:input cssCclass="form-control" id="response" path="response" type="text"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-xs-12">
                            <div class="pull-right">
                                <c:if test="${!securityQuestionsRequired}">
                                    <button type="submit" name="skip" class="btn btn-link" value="true">Skip</button>
                                </c:if>
                                <button type="submit" class="btn btn-primary "><spring:message code="button.save"/></button>
                            </div>
                        </div>
                    </div>
                </form:form>
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