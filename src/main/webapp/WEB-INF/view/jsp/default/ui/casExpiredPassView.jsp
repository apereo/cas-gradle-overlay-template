<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<meta name="decorator" content="green-header-minimal"/>

<style type="text/css">
    #reset {
        width: 299px;
        margin: 10px auto;
    }
    input[type="text"] {
        padding: 6px 6px;
    }

</style>

<script>
    $(document).ready(function () {
        $("#fm1").submit(function () {
            $.post("/app/profile/ajaxUpdatePassword", $("#fm1").serialize(), "json")
                    .done(function () {
                        //
                        window.location = "/login?service=${service}";
                    })
                    .fail(function (data) {
                        var myData = $.parseJSON(data.responseText);
                        $("#error").html(myData.errorMessage);
                        $("#error").removeClass("hide");
                    });

            return false;
        });
    });
</script>

<div id="top-spacer" style="height: 101px;"></div>
<div id="reset">

    <div class="top-blue">
        Your Password Expired
    </div>
    <div class="bottom-brown">
        <div id="error" class="alert alert-error hide"></div>

        <form id="fm1" class="form-vertical">
            <div class="alert alert-info">
                It's been 90 days since you last changed your password. For security reasons please create a new one.
            </div>

            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="password1">New Password</label>

                    <div class="controls">
                        <input id="password1" name="password1" value="" type="password" style="width: 233px" autocomplete="off"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password2">Confirm Password</label>

                    <div class="controls">
                        <input id="password2" name="password2" value="" type="password" style="width: 233px" autocomplete="off"/>
                    </div>
                </div>
            </fieldset>

            <input name="username" type="hidden" value="${credentials.username}"/>
            <input name="currentPassword" type="hidden" value="${credentials.password}"/>
            <input id="redirectFrom" name="redirectFrom" value="expirePassword" type="hidden"/>

            <div class="control-group" style="text-align: right">
                <input class="btn btn-primary" name="submit" accesskey="l" value="Change Password" tabindex="4" type="submit"/>
            </div>
        </form>
    </div>
</div>
