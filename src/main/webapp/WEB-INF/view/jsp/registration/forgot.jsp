<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<head>

    <meta name="decorator" content="black-header-minimal" />

    <meta name="robots" content="noindex">

    <style type="text/css">

        #forgot {
            width: 299px;
            margin: 10px auto;
        }

        #back-to-signin {
            margin: 5px auto;
            text-align: center;
        }

        input[type="text"] {
            padding: 6px 6px;
        }

    </style>

    <script type="text/javascript">

        $(document).ready(function () {

            //Validate the form
            $('#fm1').validate(
                    {
                        rules: {
                            username: {
                                required: true,
                                email: true
                            }
                        },
                        messages: {
                            username: {
                                required: "<spring:message code='user.error.email.blank'/>",
                                email: "<spring:message code='user.error.email.invalid'/>"
                            }

                        },
                        highlight: function (element) {
                            $(element).closest('.control-group').removeClass('success').addClass('error');
                        },
                        success: function (element) {
                            element.closest('.control-group').removeClass('error');
                            element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
                        }

                    });

        });

    </script>

</head>
<body>

<div id="top-spacer" style="height: 101px;"></div>
<div id="forgot">
    <div class="top-blue">
        Password Reset
    </div>
    <div class="bottom-brown">

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <spring:message code="${error}"/>
            </div>
        </c:if>

        <form:form action="recover" method="post" id="fm1" class="form-vertical">
            <div class="control-group">
                <label for="username" class="control-label">Please <c:if test="${empty username}">enter</c:if><c:if test="${!empty username}">verify</c:if> your email address</label>
                <div class="controls">
                    <input type="text" class="required" id="username" name="username" size="25" tabindex="1" style="width: 231px" value="${fn:escapeXml(username)}" />
                </div>
            </div>

            <div class="control-group" style="text-align: right">
                <input class="btn btn-primary" name="submit" accesskey="l" value="Next" tabindex="4" type="submit" />
            </div>
        </form:form>
    </div>
</div>

<div id="back-to-signin">
    <c:set var="loginUrl" value="${pageContext.request.contextPath}/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>

</body>