<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="security_questions_confirm_js" value="${pageContext.request.contextPath}/js/securityQuestionsConfirm.js"/>
<c:set var="succes_image_url" value="${pageContext.request.contextPath}/img/ic-check-with-background.svg"/>

<%--@elvariable id="flowExecutionKey" type="java.lang.String"--%>

<head>
    <meta name="decorator" content="black-header-minimal"/>
    <meta name="robots" content="noindex">
    <title><spring:message code="security.question.title.label"/></title>
</head>
<body>

<div class="container">
    <div class="page-header">
        <div class="row">
            <div class="col-xs-12 text-center">
                <img src="${succes_image_url}" style="display: none;" class="confirm-success bounceIn">
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12 col-sm-8 col-sm-offset-2 text-center">
                <h2>
                    <spring:message code="security.question.confirm.title.label"/>
                </h2>

                <div class="row">
                    <div class="col-xs-12 col-sm-8 col-sm-offset-2">
                        <p class="subtitle">
                            <spring:message code="security.question.confirm.sub.title.label"/>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-2 col-sm-offset-5 text-center">
            <form method="post">
                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <button type="submit" class="btn btn-primary btn-block" name="_eventId_success" value="success"><spring:message code="button.ok"/></button>
            </form>
        </div>
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="${security_questions_confirm_js}"></script>
</content>

</body>