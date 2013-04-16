<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="editProfileUrl" value="/app/profile/editProfile"/>
<c:url var="verifyExistingPasswordUrl" value="/app/central/verifyExistingPassword"/>

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
        $("#currentPasswordVisible").focus();

        return false;
    }

    $("#currentPasswordDialog").on("show", function () {

    });

    function confirmCurrentPassword() {
        var currentPassword = $("#currentPasswordVisible").val();
        var confirmed = false;

        $.ajax("${verifyExistingPasswordUrl}", {
            type: "POST",
            data: { currentPassword: currentPassword },
            success: function (response) {
                $("#currentPassword").val($("#currentPasswordVisible").val());
                $("#currentPasswordDialog").modal("hide");
                $("#changePasswordForm").submit();
            },
            error: function (response) {
                $("#currentPasswordError").show();
            }
        });

        return false;
    }

    function submitCurrentPasswordForm() {
        $("#confirmCurrentPasswordForm").submit();
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

<h2 class="apps">
    Change Password
</h2>

<p>
    Change your password that is used to sign into all of your accounts.
</p>

<form id="changePasswordForm" action="updatePassword" method="post" class="form-horizontal" onsubmit="return submitUpdates()">
    <input id="username" name="username" value="${user.username}" type="hidden"/>
    <input id="currentPassword" name="currentPassword" value="" type="hidden"/>

    <c:if test="${error != null}">
        <div class="alert">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <fieldset>
        <div class="control-group">
            <label class="control-label" for="password1">Password</label>

            <div class="controls">
                <input id="password1" name="password1" value="" type="password" autocomplete="off"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="password2">Confirm Password</label>

            <div class="controls">
                <input id="password2" name="password2" value="" type="password" autocomplete="off"/>
            </div>
        </div>
    </fieldset>

    <div class="buttonbar">
        <input type="submit" value="Save" class="btn btn-primary"/>
        <a class="btn" href="${editProfileUrl}">Cancel</a>
    </div>
</form>

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
                    <input type="password" id="currentPasswordVisible" name="currentPasswordVisible" autocomplete="off"
                           style="width: 230px"/>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="modal-footer">
        <a href="javascript:submitCurrentPasswordForm()" class="btn btn-primary">Confirm</a>
    </div>
</div>
