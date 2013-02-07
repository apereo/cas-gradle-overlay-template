<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="linkInfusionsoftAppAccount" value="/central/linkInfusionsoftAppAccount"/>
<c:url var="linkCustomerHubAccount" value="/central/linkCustomerHubAccount"/>
<c:url var="linkCommunityAccount" value="/central/linkCommunityAccount"/>
<c:url var="unlinkAccount" value="/central/unlinkAccount"/>
<c:url var="createCommunityAccount" value="/central/createCommunityAccount"/>
<c:url var="editCommunityAccount" value="/central/editCommunityAccount"/>
<c:url var="renameAccount" value="/central/renameAccount"/>

<script type="text/javascript">

    $(document).ready(function() {
        $(".account").hover(
            function() {
                // TODO - stop hiding this if we want to allow account deletion
                //        $(this).find(".account-delete").show();
            },
            function() {
                $(this).find(".account-delete").hide();
            }
        );

        $(".account .account-delete").click(function(event) {
            event.stopPropagation();

            if (confirm("Unlink this account from your Infusionsoft ID?")) {
                var accountId = $(this).parents(".account").attr("accountId");

                $.ajax({
                    url: "${unlinkAccount}",
                    type: "POST",
                    data: { account: accountId },
                    success: function(response) {
                        $(".account[accountId=" + accountId + "]").remove();
                    }
                });
            }
        });

        $(".account").click(function() {
            // Yes that's right, a div with an href, to avoid 
            // silly nested propagation issues.
        	document.location.href = $(this).attr("href");
        });

        $(".quick-editable").each(function() {
            $(this).click(function(event) {
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
            success: function(response) {
                $(".quick-editable").removeClass("editing");
                $("#quick-editor").hide();
                $("#quick-editable-" + id).html(alias);
            }
        });

        return false;
    }

    function hideQuickEditor() {
        $("#quick-editor").hide();
    }

</script>

<div id="main">
    <div class="titlebar">
        <h2 class="apps" style="float: left">
            Your Accounts
        </h2>
        <div class="btn-group" style="float: right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                Connect an Account
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="${linkInfusionsoftAppAccount}">Connect an Infusionsoft Account</a></li>
                <li><a href="${linkCustomerHubAccount}">Connect a CustomerHub Account</a></li>
                <c:if test="${!hasCommunityAccount}">
                    <li><a href="${linkCommunityAccount}">Connect a Community Profile</a></li>
                    <li class="divider"></li>
                    <li><a href="${createCommunityAccount}">Create a Community Profile</a></li>
                </c:if>
            </ul>
        </div>
        <div style="clear: both"></div>
    </div>

    <c:if test="${fn:length(accounts) > 0}">
        <div class="accounts">
            <c:forEach var="account" items="${accounts}">
                <c:choose>
                    <c:when test="${account.appType == 'crm'}">
                        <div accountId="${account.id}" href="${crmProtocol}://${account.appName}.${crmDomain}:${crmPort}" class="account crm-account">
                            <div class="account-delete">&times;</div>
                            <div class="account-info">
                                <div id="account_${account.id}" class="account-title">
                                    <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${empty account.alias ? account.appName : account.alias}</span>
                                </div>
                                <div class="account-detail">Infusionsoft Account</div>
                                <div class="account-detail">${account.appName}.${crmDomain}</div>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${account.appType == 'community'}">
                        <div accountId="${account.id}" href="http://${communityDomain}/caslogin.php" class="account forum-account">
                            <div class="account-delete">&times;</div>
                            <div class="account-info">
                                <div class="account-title">Infusionsoft Community</div>
                                <div class="account-detail">${communityDomain}</div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div accountId="${account.id}" href="https://${account.appName}.${customerHubDomain}" class="account customerhub-account">
                            <div class="account-delete">&times;</div>
                            <div class="account-info">
                                <div id="account_${account.id}" class="account-title">
                                    <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${empty account.alias ? account.appName : account.alias}</span>
                                </div>
                                <div class="account-detail">CustomerHub App</div>
                                <div class="account-detail">${account.appName}.${customerHubDomain}</div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <div href="${marketplaceUrl}" class="account marketplace-account">
                <div class="account-delete">&times;</div>
                <div class="account-info">
                    <div id="account_${account.id}" class="account-title">
                        <span>Marketplace</span>
                    </div>
                    <div class="account-detail">${marketplaceDomain}</div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<div id="quick-editor">
    <form action="${renameAccount}" class="form-vertical" onsubmit="return updateAlias()">
        <fieldset>
            <label for="alias" class="form-label">Account Alias</label>
            <div class="controls">
                <input type="hidden" name="account" id="account"/>
                <input type="text" name="alias" id="alias" style="width: 200px"/>
            </div>
            <div class="controls">
                <input type="submit" value="Save" class="btn btn-primary"/>
                <a href="javascript:hideQuickEditor()" class="btn">Cancel</a>
            </div>
        </fieldset>
    </form>
</div>