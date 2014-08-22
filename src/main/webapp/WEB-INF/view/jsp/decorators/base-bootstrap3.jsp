<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>

<c:url var="choosenCss" value="/css/chosen.min.css"/>
<c:url var="bootstrapCss" value="/bootstrap-3.2.0-dist/css/bootstrap.min.css"/>
<c:url var="bootstrapOverrideCss" value="/css/bootstrap-app-central-3.2.0.css"/>

<c:url var="bootstrapChoosenCss" value="/bootstrap-chosen/bootstrap-chosen.css"/>
<c:url var="xeditEditableCss" value="/bootstrap3-editable/css/bootstrap-editable.css"/>

<c:url var="jqueryJs" value="/js/jquery-1.11.0/jquery-1.11.0.min.js"/>
<c:url var="bootstrapJs" value="/bootstrap-3.2.0-dist/js/bootstrap.min.js"/>
<c:url var="xeditableJs" value="/bootstrap3-editable/js/bootstrap-editable.min.js"/>
<c:url var="placeholderJs" value="/js/jquery-plugins/placeholder-0.2.4/jquery.placeholder.js"/>
<c:url var="chosenJs" value="/js/jquery-plugins/chosen-1.1.0/chosen.jquery.min.js"/>
<c:url var="validateJs" value="/js/jquery-plugins/validate-1.11.1/jquery.validate.min.js"/>

<c:url var="infusionsoftChosenConfig" value="/js/infusionsoft-chosen.js"/>

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

    <link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800"/>
    <link type="text/css" rel="stylesheet" href="${bootstrapCss}"/>
    <link type="text/css" rel="stylesheet" href="${bootstrapChoosenCss}"/>
    <link type="text/css" rel="stylesheet" href="${xeditEditableCss}"/>
    <link type="text/css" rel="stylesheet" href="${bootstrapOverrideCss}"/>

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

    <script type="text/javascript">
        window.heap = window.heap || [], heap.load = function (t, e) {
            window.heap.appid = t, window.heap.config = e;
            var a = document.createElement("script");
            a.type = "text/javascript", a.async = !0, a.src = ("https:" === document.location.protocol ? "https:" : "http:") + "//cdn.heapanalytics.com/js/heap.js";
            var n = document.getElementsByTagName("script")[0];
            n.parentNode.insertBefore(a, n);
            for (var o = function (t) {
                return function () {
                    heap.push([t].concat(Array.prototype.slice.call(arguments, 0)))
                }
            }, p = ["identify", "track"], c = 0; c < p.length; c++)heap[p[c]] = o(p[c])
        };
        heap.load("994434072");
    </script>

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

    <decorator:head/>
</head>
<body>
<decorator:body/>

<!-- global java script files -->
<script type="text/javascript" src="${jqueryJs}"></script>
<script type="text/javascript" src="${bootstrapJs}"></script>
<script type="text/javascript" src="${placeholderJs}"></script>
<script type="text/javascript" src="${chosenJs}"></script>
<script type="text/javascript" src="${validateJs}"></script>
<script type="text/javascript" src="${xeditableJs}"></script>
<script type="text/javascript" src="${infusionsoftChosenConfig}"></script>

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

<script src="https://www.infusionsoft.com/sites/all/modules/contrib/analytics/marketo.js"></script>

</body>
</html>
