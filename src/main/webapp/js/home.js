$(document).on("click", ".list-group-item", function() {

    $this = $(this);

    // Check that the xeditable popup is not open
    if($this.find("[editable-active]").length === 0) { // means that editable popup is not open so we can do the stuff
        window.location = $this.data('url');
    }
});

$(document).ready(function () {
    $.fn.editable.defaults.mode = 'inline';
    $('.aliasable').each(function() {
        $this = $(this);

        console.log($this);

        $this.editable();

        $this.on('shown', function(e, reason) {
            $("#divApplicationImage").addClass("hidden-xs");
            $("#divChevron").addClass("hidden-xs");
            return $(this).attr("editable-active", true);
        });

        $this.on('hidden', function(e, reason) {
            $("#divApplicationImage").removeClass("hidden-xs");
            $("#divChevron").removeClass("hidden-xs");
            return $(this).removeAttr("editable-active");
        });
    });

    $('.accessTokensAllowed').each(function() {
        $this = $(this);

        console.log($this);

        $this.collapse();

        $this.on('shown.bs.collapse', function(e) {
            e.stopPropagation();
        });

        $this.on('hidden.bs.collapse', function(e) {
            e.stopPropagation();
        });
    });

//    centralHome.attachOnClicks();
});

var centralHome = {
    getAppsGrantedAccessToAccount: function (userId, accountId) {
        var displayIsClosed = $("#displayManageAccountsWrapper-" + accountId).is(':visible') ? false : true;
        manageAppAccess.closeManageAppAccessDisplay(accountId);
        if (displayIsClosed) {
            var preSpinnerReplacedContent = $("#spinner-content-" + accountId).html();
            $("#spinner-content-" + accountId).removeClass('app-access');
            window.global.showSpinner({id: "spinner-content-" + accountId});

            var appsGrantedAccessToAccountInput = new Object();
            appsGrantedAccessToAccountInput.afterSuccess = centralHome.appsGrantedAccessToAccountAfterSuccess;
            appsGrantedAccessToAccountInput.afterError = centralHome.appsGrantedAccessToAccountAfterError
            appsGrantedAccessToAccountInput.useSpinner = true;
            appsGrantedAccessToAccountInput.preSpinnerReplacedContent = preSpinnerReplacedContent;
            appsGrantedAccessToAccountInput.userId = userId;
            appsGrantedAccessToAccountInput.accountId = accountId;
            manageAppAccess.getAppsGrantedAccessToAccount(appsGrantedAccessToAccountInput);
        }
    },
    appsGrantedAccessToAccountAfterSuccess: function (inputObject, response) {
        $(".crm-account").each(function () {
            $(this).removeClass('expanded-apps expanded-apps-crm')
        });
        $(".crm-account-" + inputObject.accountId).addClass('expanded-apps expanded-apps-crm');
        $(".displayManageAccountsMarker").each(function () {
            $(this).hide()
        });
        $("#displayManageAccountsContent-" + inputObject.accountId).html(response);
        $("#displayManageAccountsWrapper-" + inputObject.accountId).slideDown(500);
        if (inputObject.preSpinnerReplacedContent) {
            $("#spinner-content-" + inputObject.accountId).html(inputObject.preSpinnerReplacedContent);
            $("#manageAccounts-" + inputObject.accountId).click(centralHome.reattachOnClicksAfterSpinnerRefresh);
            $("#spinner-content-" + inputObject.accountId).addClass('app-access');  //to restore "key" background image on "Manage App Access span"
        }
    },
    appsGrantedAccessToAccountAfterError: function (inputObject, response) {
        $("#spinner-content-" + inputObject.accountId).html(inputObject.preSpinnerReplacedContent);
        $("#manageAccounts-" + inputObject.accountId).click(centralHome.reattachOnClicksAfterSpinnerRefresh);
    },
    attachOnClicks: function () {
        $(".manageAccounts").each(function () {
            $(this).click(function (event) {
                event.stopPropagation();
                var userId = $(event.target).attr("userId");
                var accountId = $(event.target).attr("accountId");
                centralHome.getAppsGrantedAccessToAccount(userId, accountId);
            });
        });
    },
    reattachOnClicksAfterSpinnerRefresh: function (event) {
        event.stopPropagation();
        var userId = $(event.target).attr("userId");
        var accountId = $(event.target).attr("accountId");
        centralHome.getAppsGrantedAccessToAccount(userId, accountId);
    }
};
