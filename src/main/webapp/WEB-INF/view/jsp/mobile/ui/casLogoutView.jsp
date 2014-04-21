<!doctype html>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%--
 After logout, just send them back to the login page for now.
 TODO - meta tag redirects are cheesy. Someday we should figure out how to do this in Web Flow.
--%>
<html>
<head>
    <title>Logout Successful</title>
    <meta http-equiv='REFRESH' content="0;url=<c:url value="/app/central/home"/>"/>
</head>
<body>
</body>
</html>

