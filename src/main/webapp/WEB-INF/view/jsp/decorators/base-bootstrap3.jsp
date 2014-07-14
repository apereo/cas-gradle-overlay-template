<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>

<!DOCTYPE html>

<html lang="en">
<head>
    <c:set var="title">
        <decorator:title/>
    </c:set>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />

    <title>Infusionsoft <c:if test="${!empty title}"> - </c:if><decorator:title/></title>

    <link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/chosen.min.css"/>"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/bootstrap-3.1.1.min.css"/>"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/bootstrap-infusionsoft-3.1.1.0.css"/>"/>
    <script src="https://www.infusionsoft.com/sites/all/modules/contrib/analytics/marketo.js"></script>

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
<script type="text/javascript" src="<c:url value="/js/jquery-1.11.0/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap-3.1.1/bootstrap.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-plugins/placeholder-0.2.4/jquery.placeholder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-plugins/chosen-1.1.0/chosen.jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-plugins/validate-1.11.1/jquery.validate.min.js"/>"></script>

<!-- local javascript files -->
<decorator:getProperty property="page.local_script"/>

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
