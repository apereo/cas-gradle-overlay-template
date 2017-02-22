<%@ tag body-content="empty" %>

<%@ attribute name="type" required="true" description="Type of resource (text/javascript)" %>
<%@ attribute name="src" required="true" description="Specifies the path to the script" %>

<script type="${type}"
        src="${src}?b=<%=org.apereo.cas.infusionsoft.support.BuildVersion.getBuildVersion()%>"
></script>