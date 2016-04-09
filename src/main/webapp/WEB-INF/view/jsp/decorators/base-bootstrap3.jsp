<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<c:set var="choosenCss" value="${pageContext.request.contextPath}/css/chosen.min.css"/>
<c:set var="bootstrapCss" value="${pageContext.request.contextPath}/bootstrap-3.2.0-dist/css/bootstrap.min.css"/>
<c:set var="bootstrapOverrideCss" value="${pageContext.request.contextPath}/css/bootstrap-app-central.css"/>

<c:set var="bootstrapChoosenCss" value="${pageContext.request.contextPath}/bootstrap-chosen/bootstrap-chosen.css"/>
<c:set var="bootstrapTypeaheadCss" value="${pageContext.request.contextPath}/css/typeahead-bootstrap-fix.css"/>

<c:set var="jqueryJs" value="${pageContext.request.contextPath}/js/jquery-2.1.3/jquery-2.1.3.min.js"/>
<c:set var="bootstrapJs" value="${pageContext.request.contextPath}/bootstrap-3.2.0-dist/js/bootstrap.min.js"/>
<c:set var="placeholderJs" value="${pageContext.request.contextPath}/js/jquery-plugins/placeholder-0.2.4/jquery.placeholder.js"/>
<c:set var="chosenJs" value="${pageContext.request.contextPath}/js/jquery-plugins/chosen-1.1.0/chosen.jquery.min.js"/>
<c:set var="validateJs" value="${pageContext.request.contextPath}/js/jquery-plugins/validate-1.11.1/jquery.validate.min.js"/>
<c:set var="handlebarsJs" value="${pageContext.request.contextPath}/js/handlerbars-2.0.0/handlebars-v2.0.0.js"/>
<c:set var="typeAheadJs" value="${pageContext.request.contextPath}/js/typeahead-0.10.5/typeahead.bundle.js"/>

<c:set var="infusionsoftChosenConfig" value="${pageContext.request.contextPath}/js/infusionsoft-chosen.js"/>

<c:set var="animo_css" value="${pageContext.request.contextPath}/js/animo-1.0.3/animate-animo.min.css"/>
<c:set var="animo_js" value="${pageContext.request.contextPath}/js/animo-1.0.3/animo.min.js"/>

<c:set var="infusionsoft_icon_css" value="${pageContext.request.contextPath}/infusionsoft-icon/style.css"/>

<!DOCTYPE html>

<html lang="en">
<head>
    <c:set var="title">
        <decorator:title/>
    </c:set>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>

    <title>
        <decorator:title/>
    </title>

    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800"/>
    <versioned:link type="text/css" rel="stylesheet" href="${infusionsoft_icon_css}"/>
    <link type="text/css" rel="stylesheet" href="${animo_css}"/>
    <link type="text/css" rel="stylesheet" href="${bootstrapCss}"/>
    <versioned:link type="text/css" rel="stylesheet" href="${bootstrapChoosenCss}"/>
    <versioned:link type="text/css" rel="stylesheet" href="${bootstrapTypeaheadCss}"/>
    <versioned:link type="text/css" rel="stylesheet" href="${bootstrapOverrideCss}"/>

    <script type="text/javascript">
        //Necessary to overcome the known Microsoft Mobile IE bug
        //For more information, see http://mattstow.com/responsive-design-in-ie10-on-windows-phone-8.html#update-20131015
        (function () {
            if ("-ms-user-select" in document.documentElement.style && navigator.userAgent.match(/IEMobile\/10\.0/)) {
                var msViewportStyle = document.createElement("style");
                msViewportStyle.appendChild(
                        document.createTextNode("@-ms-viewport{width:auto!important}")
                );
                document.getElementsByTagName("head")[0].appendChild(msViewportStyle);
            }
        })();
    </script>



    <decorator:head/>
</head>
<body>
<decorator:body/>

<!-- global java script files -->
<script type="text/javascript" src="${jqueryJs}"></script>
<script type="text/javascript" src="${animo_js}"></script>
<script type="text/javascript" src="${bootstrapJs}"></script>
<script type="text/javascript" src="${placeholderJs}"></script>
<script type="text/javascript" src="${chosenJs}"></script>
<script type="text/javascript" src="${validateJs}"></script>
<versioned:script type="text/javascript" src="${infusionsoftChosenConfig}"/>
<script type="text/javascript" src="${handlebarsJs}"></script>
<script type="text/javascript" src="${typeAheadJs}"></script>

<!-- local javascript files -->
<decorator:getProperty property="page.local_script"/>

<script>
    (function (i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r;
        i[r] = i[r] || function () {
            (i[r].q = i[r].q || []).push(arguments)
        }, i[r].l = 1 * new Date();
        a = s.createElement(o),
                m = s.getElementsByTagName(o)[0];
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m)
    })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

    ga('create', 'UA-53595407-1', 'auto');
    ga('send', 'pageview');

</script>

<script type="text/javascript">(function () {
    var walkme = document.createElement('script');
    walkme.type = 'text/javascript';
    walkme.async = true;
    walkme.src = 'https://d3b3ehuo35wzeh.cloudfront.net/users/6543/walkme_6543_https.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(walkme, s);
})();</script>

</body>
</html>
