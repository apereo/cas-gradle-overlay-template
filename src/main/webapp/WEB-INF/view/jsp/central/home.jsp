<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="linkInfusionsoftAppAccount" value="/app/central/linkInfusionsoftAppAccount"/>
<c:url var="linkCustomerHubAccount" value="/app/central/linkCustomerHubAccount"/>
<c:url var="linkCommunityAccount" value="/app/central/linkCommunityAccount"/>
<c:url var="unlinkAccount" value="/app/central/unlinkAccount"/>
<c:url var="createCommunityAccount" value="/app/central/createCommunityAccount"/>
<c:url var="editCommunityAccount" value="/app/central/editCommunityAccount"/>
<c:url var="renameAccount" value="/app/central/renameAccount"/>
<style type="text/css">
    html, body {
        background: #F5F5F5;
    }
</style>
<script type="text/javascript">

    $(document).ready(function () {
        $(".account").hover(
                function () {
                    // TODO - stop hiding this if we want to allow account deletion! -${param.service}
                    //        $(this).find(".account-delete").show();
                },
                function () {
                    $(this).find(".account-delete").hide();
                }
        );

        $(".account .account-delete").click(function (event) {
            event.stopPropagation();

            if (confirm("Unlink this account from your Infusionsoft ID?")) {
                var accountId = $(this).parents(".account").attr("accountId");

                $.ajax({
                    url: "${unlinkAccount}",
                    type: "POST",
                    data: { account: accountId },
                    success: function (response) {
                        $(".account[accountId=" + accountId + "]").remove();
                    }
                });
            }
        });

        $(".account").click(function () {
            // Yes that's right, a div with an href, to avoid 
            // silly nested propagation issues.
            document.location.href = $(this).attr("href");
        });

        $(".quick-editable").each(function () {
            $(this).click(function (event) {
                event.stopPropagation();

                editAlias($(this).attr("accountId"));
            });
        });
    });

    function editAlias(userAccountId) {
        hideQuickEditor();

        var editable = $("#quick-editable-" + userAccountId);

        $(editable).addClass("editing");
        $("#quick-editor #account").val(userAccountId);
        $("#quick-editor #alias").val(editable.html());
        $("#quick-editor").show();
        $("#quick-editor").css("left", editable.offset().left + editable.width());
        $("#quick-editor").css("top", editable.offset().top - 50);
        $("#quick-editor #alias").focus();

        event.stopPropagation();

        return false;
    }

    function updateAlias() {
        var id = $("#quick-editor #account").val();
        var alias = $("#quick-editor #alias").val();

        $.ajax("${renameAccount}", {
            type: "POST",
            data: { id: id, value: alias },
            success: function (response) {
                hideQuickEditor();
                $("#quick-editable-" + id).html(response);
            }
        });

        return false;
    }

    function hideQuickEditor() {
        $(".quick-editable").removeClass("editing");
        $("#quick-editor").hide();
    }

</script>

<c:if test="${!empty connectError}">
    <div class="alert alert-error" style="margin-top: 10px">
        <spring:message code="${connectError}"/>
    </div>
</c:if>

<div class="titlebar">
    <h2 class="apps" style="float: left">
        <spring:message code="central.home.your.accounts"/>
    </h2>

    <c:if test="${connectAccountCrmEnabled || connectAccountCustomerHubEnabled || connectAccountCommunityEnabled}">
        <div class="btn-group" style="float: right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                <spring:message code="central.home.connect.account"/>
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <c:if test="${connectAccountCrmEnabled}">
                    <li><a href="${linkInfusionsoftAppAccount}"><spring:message code="central.home.connect.crm.account"/></a></li>
                </c:if>
                <c:if test="${connectAccountCustomerHubEnabled}">
                    <li><a href="${linkCustomerHubAccount}"><spring:message code="central.home.connect.customerhub.account"/></a></li>
                </c:if>
                <c:if test="${connectAccountCommunityEnabled && !hasCommunityAccount}">
                    <li><a href="${linkCommunityAccount}"><spring:message code="central.home.connect.community.account"/></a></li>
                    <li class="divider"></li>
                    <li><a href="${createCommunityAccount}"><spring:message code="central.home.create.community.account"/></a></li>
                </c:if>
            </ul>
        </div>
    </c:if>
    <div style="clear: both"></div>
</div>

<div class="accounts">
    <c:forEach var="account" items="${accounts}">
        <c:choose>
            <c:when test="${account.appType == 'CRM'}">
                <div accountId="${account.id}" href="${crmProtocol}://${account.appName}.${crmDomain}:${crmPort}" class="account crm-account">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div id="account_${account.id}" class="account-title">
                            <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${fn:escapeXml(empty account.alias ? account.appName : account.alias)}</span>
                        </div>
                        <div class="account-detail"><spring:message code="central.home.crm.account"/></div>
                        <div class="account-detail">${account.appName}.${crmDomain}</div>
                    </div>
                </div>
            </c:when>
            <c:when test="${account.appType == 'COMMUNITY'}">
                <div accountId="${account.id}" href="http://${communityDomain}/caslogin.php" class="account forum-account">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div class="account-title"><spring:message code="central.home.community.account"/></div>
                        <div class="account-detail">${communityDomain}</div>
                    </div>
                </div>
            </c:when>
            <c:when test="${account.appType == 'CUSTOMERHUB'}">
                <div accountId="${account.id}" href="https://${account.appName}.${customerHubDomain}/admin" class="account customerhub-account">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div id="account_${account.id}" class="account-title">
                            <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${fn:escapeXml(empty account.alias ? account.appName : account.alias)}</span>
                        </div>
                        <div class="account-detail"><spring:message code="central.home.customerhub.account"/></div>
                        <div class="account-detail">${account.appName}.${customerHubDomain}</div>
                    </div>
                </div>
            </c:when>
        </c:choose>
    </c:forEach>
    <div href="${marketplaceUrl}" class="account marketplace-account">
        <div class="account-delete">&times;</div>
        <div class="account-info">
            <div id="account_${account.id}" class="account-title">
                <span><spring:message code="central.home.marketplace.account"/></span>
            </div>
            <div class="account-detail">${marketplaceDomain}</div>
        </div>
    </div>
</div>

<div id="quick-editor">
    <form action="${renameAccount}" class="form-vertical" onsubmit="return updateAlias()">
        <fieldset>
            <label for="alias" class="form-label"><spring:message code="central.home.account.alias"/></label>

            <div class="controls">
                <input type="hidden" name="account" id="account"/>
                <input type="text" name="alias" id="alias" style="width: 200px"/>
            </div>
            <div class="controls">
                <c:set var="saveLabel"><spring:message code="central.home.account.alias.save"/></c:set>
                <input type="submit" value="${saveLabel}" class="btn btn-primary"/>
                <a href="javascript:hideQuickEditor()" class="btn"><spring:message code="central.home.account.alias.cancel"/></a>
            </div>
        </fieldset>
    </form>
</div>