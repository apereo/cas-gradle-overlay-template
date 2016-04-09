<%@ tag body-content="scriptless" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="data" required="true" description="Specifies the URL of the resource to be used by the object" %>
<%@ attribute name="type" required="true" description="Type of resource (image/svg+xml)" %>
<%@ attribute name="tabindex" required="false" description="Tab Index of the resource" %>
<%@ attribute name="width" required="false" description="Width of the object" %>
<%@ attribute name="height" required="false" description="Height of the object" %>
<%@ attribute name="cssClass" required="false" description="CSS class to be applied to the object" %>
<jsp:doBody var="body" />
<object type="${type}" data="${data}?b=<%=com.infusionsoft.cas.support.BuildVersion.getBuildVersion()%>" <c:if test="${not empty tabindex}">tabindex="${tabindex}" </c:if><c:if test="${not empty cssClass}">class="${cssClass}" </c:if><c:if test="${not empty height}">height="${height}" </c:if><c:if test="${not empty width}">width="${width}" </c:if>>${body}</object>