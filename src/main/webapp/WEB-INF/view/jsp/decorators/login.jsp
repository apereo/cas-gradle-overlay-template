<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<c:set var="bootstrapOverrideCss" value="${pageContext.request.contextPath}/css/bootstrap-infusionsoft.css"/>

<page:applyDecorator name="base-bootstrap3">
    <!DOCTYPE html>

    <html lang="en">
    <head>
        <title><decorator:title/></title>

        <meta name="decorator" content="login"/>

        <versioned:link type="text/css" rel="stylesheet" href="${bootstrapOverrideCss}"/>

        <decorator:head/>
    </head>
    <body>
    <decorator:body/>

    <content tag="local_script">
        <decorator:getProperty property="page.local_script"/>
    </content>
    </body>
    </html>
</page:applyDecorator>