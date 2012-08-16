<!doctype html>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%--
 If CAS doesn't know what service people were trying to log into, they come here. Let's just send
 them all to the bouncer and let it figure it out.

 TODO: This may not be needed anymore since changing to a WebFlow redirect...
--%>
<html>
    <head>
        <title>Login Successful</title>
        <meta http-equiv='REFRESH' content="0;url=<c:url value="/central/index"/>"/>
    </head>
    <body>
    </body>
</html>

