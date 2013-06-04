<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>

<!DOCTYPE html>

<page:applyDecorator name="base-no-layout">
    <html lang="en">
    <head>
        <title><decorator:title/></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <decorator:head/>
    </head>
    <body>
    <div id="greenheaderbg">
        <div id="greenheader">&nbsp;</div>
    </div>
    <div>
        <decorator:body/>
    </div>
    </body>
    </html>
</page:applyDecorator>