<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="src" required="true" description="Url of the image" %>
<%@ attribute name="id" required="false" description="Id of the image" %>
<%@ attribute name="alt" required="false" description="Alternate image text" %>
<%@ attribute name="title" required="false" description="Title" %>
<%@ attribute name="cssClass" required="false" description="CSS Class" %>
<%@ attribute name="style" required="false" description="CSS style" %>
<%@ attribute name="height" required="false" description="Height of the image" %>
<%@ attribute name="width" required="false" description="Width of the image" %>
<%@ attribute name="border" required="false" description="Border around the image" %>
<%@ attribute name="align" required="false" description="Alignment of the image" %>
<%@ attribute name="onclick" required="false" description="Onclick event" %>
<%@ attribute name="name" required="false" description="Name of the image" %> <%-- Not valid attribute. Added for backwards compatibility--%>
<%@ attribute name="dataThemeId" required="false" description="Data theme id of the image" %> <%-- Not valid attribute. Added for backwards compatibility--%>
<%@ attribute name="dataType" required="false" description="Data type of the image" %> <%-- Not valid attribute. Added for backwards compatibility--%>
<%@ attribute name="vspace" required="false" description="Vspace of the image" %> <%-- Deprecated attribute. Added for backwards compatibility--%>

<img <c:if test="${not empty id}">id="${id}" </c:if>
     src="${src}?b=<%=com.infusionsoft.cas.support.BuildVersion.getBuildVersion()%>"
     <c:if test="${not empty cssClass}">class="${cssClass}" </c:if>
     <c:if test="${not empty style}">style="${style}" </c:if>
     <c:if test="${not empty alt}">alt="${alt}" </c:if>
     <c:if test="${not empty title}">title="${title}" </c:if>
     <c:if test="${not empty height}">height="${height}" </c:if>
     <c:if test="${not empty width}">width="${width}" </c:if>
     <c:if test="${not empty border}">border="${border}" </c:if>
     <c:if test="${not empty align}">align="${align}" </c:if>
     <c:if test="${not empty onclick}">onclick="${onclick}" </c:if>
     <c:if test="${not empty name}">name="${name}" </c:if>
     <c:if test="${not empty dataThemeId}">data-theme-id="${dataThemeId}" </c:if>
     <c:if test="${not empty dataType}">data-type="${dataType}" </c:if>
     <c:if test="${not empty vspace}">vspace="${vspace}" </c:if>
/>