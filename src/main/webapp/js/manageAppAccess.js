var manageAppAccess = {
    userIdOfUserManagingAppAccess: "",
    accountIdBeingManaged : 0,
    appIdOfAppBeingRevoked : 0,
    appNameOfAppBeingRevoked : "",

    getAppsGrantedAccessToAccount: function(userId, accountIdBeingManaged, preSpinnerReplacedContent) {
        this.userIdOfUserManagingAppAccess = userId;
        this.accountIdBeingManaged = accountIdBeingManaged;
        $.ajax("/app/central/manageAccounts", {
            type: "GET",
            data: { userId : manageAppAccess.userIdOfUserManagingAppAccess,
                    infusionsoftAccountId: accountIdBeingManaged
            },
            success: function (response) {
                $("#displayManageAccountsContent-" + accountIdBeingManaged).html(response);
                $(".displayManageAccounts").each(function () {$(this).hide()});
                $("#displayManageAccountsWrapper-" + accountIdBeingManaged).show();
                $("#manageAccounts-" + manageAppAccess.accountIdBeingManaged).html(preSpinnerReplacedContent);
            }
        });
        return false;
    },
    revokeAccess: function(){
        $.ajax("/app/central/revokeAccess", {
            type: "POST",
            data: { userId: this.userIdOfUserManagingAppAccess,
                infusionsoftAccountId: this.accountIdBeingManaged,
                masheryAppId: this.appIdOfAppBeingRevoked
            },
            success: function (response) {
                $('#myModal').modal('hide');
                $("#accessRevokedFailed-" + manageAppAccess.accountIdBeingManaged).hide();
                $("#accessRevokedSuccessText-" + manageAppAccess.accountIdBeingManaged).html(manageAppAccess.appNameOfAppBeingRevoked);
                $("#accessRevokedSuccess-" + manageAppAccess.accountIdBeingManaged).show();
            },
            error: function(response){
                $('#myModal').modal('hide');
                $("#accessRevokedSuccess-"+ manageAppAccess.accountIdBeingManaged).hide();
                $("#accessRevokedFailedText-"+ manageAppAccess.accountIdBeingManaged).html(manageAppAccess.appNameOfAppBeingRevoked);
                $("#accessRevokedFailed-"+ manageAppAccess.accountIdBeingManaged).show();
            }
        });
        return false;
    },
    populateModalBody: function(appIdOfAppBeingRevoked){
        var appName = $("#appName-" + appIdOfAppBeingRevoked).text();
        $("#modal-body-id p").html("Are you sure you want to revoke access for " + appName + "?");
        this.appIdOfAppBeingRevoked = appIdOfAppBeingRevoked;
        this.appNameOfAppBeingRevoked = appName;
    },
    attachOnClicks: function(){
        if($(".manageAccounts")){
            manageAppAccess.userIdOfUserManagingAppAccess = $(".manageAccounts").first().attr("userId");
        }
        $(".manageAccounts").each(function () {
            $(this).click(function (event) {
                event.stopPropagation();
                manageAppAccess.accountIdBeingManaged =  $(this).attr("accountId");
                manageAppAccess.closeManageAppAccessDisplay();
                var preSpinnerReplacedContent = $("#manageAccounts-" + manageAppAccess.accountIdBeingManaged).text();
                window.global.showSpinner({id: "manageAccounts-" + manageAppAccess.accountIdBeingManaged});
                manageAppAccess.getAppsGrantedAccessToAccount(manageAppAccess.userIdOfUserManagingAppAccess, manageAppAccess.accountIdBeingManaged, preSpinnerReplacedContent);
            });
        });
    },
    closeManageAppAccessDisplay : function () {
        $("#displayManageAccountsWrapper-" + this.accountIdBeingManaged).hide();
    }
}