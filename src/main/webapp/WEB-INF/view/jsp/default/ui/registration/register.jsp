<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${error == null}">
    {"status": "OK"}
</c:if>
<c:if test="${error != null}">
    {"status": "ERROR", message: '<spring:message code="${error}"/>'}
</c:if>