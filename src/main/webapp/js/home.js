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
                url: "/app/central/unlinkAccount",
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
            centralHome.editAlias($(this).attr("accountId"));
        });
    });
    centralHome.attachOnClicks();
});

var centralHome = {
    getAppsGrantedAccessToAccount : function (userId, accountId){
        manageAppAccess.closeManageAppAccessDisplay();
        var preSpinnerReplacedContent = $("#spinner-content-" + accountId).html();
        window.global.showSpinner({id: "spinner-content-" + accountId});

        var appsGrantedAccessToAccountInput = new Object();
        appsGrantedAccessToAccountInput.afterSuccess = centralHome.appsGrantedAccessToAccountAfterSuccess;
        appsGrantedAccessToAccountInput.afterError = centralHome.appsGrantedAccessToAccountAfterError
        appsGrantedAccessToAccountInput.useSpinner = true;
        appsGrantedAccessToAccountInput.preSpinnerReplacedContent = preSpinnerReplacedContent;
        appsGrantedAccessToAccountInput.userId = userId;
        appsGrantedAccessToAccountInput.accountId = accountId;
        manageAppAccess.getAppsGrantedAccessToAccount(appsGrantedAccessToAccountInput);
    },
    appsGrantedAccessToAccountAfterSuccess : function (inputObject, response) {
        $(".crm-account").each(function () {$(this).removeClass('expanded-apps expanded-apps-crm')});
        $(".crm-account-" + inputObject.accountId).addClass('expanded-apps expanded-apps-crm');
        $(".displayManageAccountsMarker").each(function () {$(this).hide()});
        $("#displayManageAccountsContent-" + inputObject.accountId).html(response);
        $("#displayManageAccountsWrapper-" + inputObject.accountId).slideDown(500);
        if(inputObject.preSpinnerReplacedContent){
            $("#spinner-content-" + inputObject.accountId).html(inputObject.preSpinnerReplacedContent);
            $("#manageAccounts-" + inputObject.accountId).click(centralHome.reattachOnClicksAfterSpinnerRefresh)
        }
    },
    appsGrantedAccessToAccountAfterError : function (inputObject, response) {
        $("#spinner-content-" + inputObject.accountId).html(inputObject.preSpinnerReplacedContent);
        $("#manageAccounts-" + inputObject.accountId).click(centralHome.reattachOnClicksAfterSpinnerRefresh)
    },
    attachOnClicks: function(){
        $(".manageAccounts").each(function () {
            $(this).click(function (event) {
                event.stopPropagation();
                var userId = $(event.target).attr("userId");
                var accountId = $(event.target).attr("accountId");
                centralHome.getAppsGrantedAccessToAccount(userId, accountId);
            });
        });
    },
    reattachOnClicksAfterSpinnerRefresh: function(event) {
        event.stopPropagation();
        var userId = $(event.target).attr("userId");
        var accountId = $(event.target).attr("accountId");
        centralHome.getAppsGrantedAccessToAccount(userId, accountId);
    },
    editAlias : function(userAccountId) {
        centralHome.hideQuickEditor();
        var editable = $("#quick-editable-" + userAccountId);
        $(editable).addClass("editing");
        $("#quick-editor #account").val(userAccountId);
        $("#quick-editor #alias").val(editable.html());
        $("#quick-editor").show();
        $("#quick-editor").css("left", editable.offset().left + editable.width());
        $("#quick-editor").css("top", editable.offset().top - 50);
        $("#quick-editor #alias").focus();
        return false;
    },
    updateAlias : function() {
        var id = $("#quick-editor #account").val();
        var alias = $("#quick-editor #alias").val();
        $.ajax("/app/central/renameAccount", {
            type: "POST",
            data: { id: id, value: alias },
            success: function (response) {
                centralHome.hideQuickEditor();
                $("#quick-editable-" + id).html(response);
            }
        });
        return false;
    },
    hideQuickEditor : function() {
        $(".quick-editable").removeClass("editing");
        $("#quick-editor").hide();
    }
};
