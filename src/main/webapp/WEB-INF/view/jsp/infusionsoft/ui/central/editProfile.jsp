<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="centralUrl" value="/central/home"/>
<c:url var="verifyExistingPasswordUrl" value="/central/verifyExistingPassword"/>

<script type="text/javascript">

    function submitUpdates() {
        if ($("#currentPassword").val()) {
            return true;
        } else {
            return promptCurrentPassword();
        }
    }

    function promptCurrentPassword() {
        $("#currentPasswordDialog").modal();

        return false;
    }

    function confirmCurrentPassword() {
        var currentPassword = $("#currentPasswordVisible").val();
        var confirmed = false;

        $.ajax("${verifyExistingPasswordUrl}", {
            type: "POST",
            data: { currentPassword: currentPassword },
            success: function(response) {
                $("#currentPassword").val($("#currentPasswordVisible").val());
                $("#currentPasswordDialog").modal("hide");
                $("#editProfileForm").submit();
            },
            error: function(response) {
                $("#currentPasswordError").show();
            }
        });

        return false;
    }
</script>

<style type="text/css">

    .form-horizontal .control-label {
        width: 120px;
    }

    .form-horizontal .controls {
        margin-left: 135px;
    }

</style>

<div id="main">
    <h2 class="apps">
        Edit Your Infusionsoft ID
    </h2>

    <p>
        Edit the information that you use to sign into all of your accounts.
    </p>

    <form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal" onsubmit="return submitUpdates()">
        <input name="id" value="${user.id}" type="hidden"/>
        <input id="currentPassword" name="currentPassword" value="" type="hidden"/>

        <c:if test="${error != null}">
            <div class="alert">
                <spring:message code="${error}"/>
            </div>
        </c:if>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="firstName">First Name</label>
                <div class="controls">
                    <input id="firstName" name="firstName" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="lastName">Last Name</label>
                <div class="controls">
                    <input id="lastName" name="lastName" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Email Address</label>
                <div class="controls">
                    <input id="username" name="username" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Confirm Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password"/>
                </div>
            </div>
        </fieldset>

        <div class="buttonbar">
            <input type="submit" value="Save" class="btn btn-primary"/>
            <a class="btn" href="${centralUrl}">Cancel</a>
        </div>
    </form>
</div>

<div class="modal hide" id="currentPasswordDialog" style="width: 300px; margin-left: -150px">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h3>Confirm Your Changes</h3>
    </div>
    <div class="modal-body">
        <div id="currentPasswordError" class="alert alert-error" style="display: none; margin-top: 0">
            You've entered an incorrect password. For security reasons, you have <span id="remainingTries">a few</span>
            more tries before your account is locked.
        </div>
        <form id="confirmCurrentPasswordForm" class="form-vertical" onsubmit="return confirmCurrentPassword()">
            <fieldset>
                <label class="control-label" for="currentPasswordVisible">Current Password</label>
                <div class="controls">
                    <input type="password" id="currentPasswordVisible" name="currentPasswordVisible" style="width: 230px"/>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="modal-footer">
        <a href="javascript:$('#confirmCurrentPasswordForm').submit()" class="btn btn-primary">Confirm</a>
    </div>
</div>