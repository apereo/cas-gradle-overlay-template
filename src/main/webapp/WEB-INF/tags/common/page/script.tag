<%@ tag body-content="empty" %>

<%@ attribute name="type" required="true" description="Type of resource (text/javascript)" %>
<%@ attribute name="src" required="true" description="Specifies the path to the script" %>

<script type="${type}"
        src="${src}?b=<%=com.infusionsoft.cas.support.BuildVersion.getBuildVersion()%>"
></script>