var manageAppAccess = {
    userIdOfUserManagingAppAccess: "",
    accountIdBeingManaged : 0,
    appIdOfAppBeingRevoked : 0,
    appNameOfAppBeingRevoked : "",

    getAppsGrantedAccessToAccount: function(inputObject) {
        this.userIdOfUserManagingAppAccess = inputObject.userId;
        this.accountIdBeingManaged = inputObject.accountId;
        $.ajax("/app/central/manageAccounts", {
            type: "GET",
            data: { userId : manageAppAccess.userIdOfUserManagingAppAccess,
                    infusionsoftAccountId: manageAppAccess.accountIdBeingManaged
            },
            success: function (response) {
;               inputObject.afterSuccess(inputObject, response);
            },
            error: function(response){
                inputObject.afterError(inputObject, response);
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
                $("#accessRevokedMessageWrapper-"+ manageAppAccess.accountIdBeingManaged).show();
                $("#accessRevokedSuccess-" + manageAppAccess.accountIdBeingManaged).show();
            },
            error: function(response){
                $('#myModal').modal('hide');
                $("#accessRevokedSuccess-"+ manageAppAccess.accountIdBeingManaged).hide();
                $("#accessRevokedFailedText-"+ manageAppAccess.accountIdBeingManaged).html(manageAppAccess.appNameOfAppBeingRevoked);
                $("#accessRevokedMessageWrapper-"+ manageAppAccess.accountIdBeingManaged).show();
                $("#accessRevokedFailed-"+ manageAppAccess.accountIdBeingManaged).show();
            }
        });
        return false;
    },
    populateModalBody: function(appIdOfAppBeingRevoked){
        var appName = $("#appName-" + appIdOfAppBeingRevoked).text();
        $("#modal-body-id p").html("Are you sure you want to revoke access for <span>" + appName + "</span>?");
        this.appIdOfAppBeingRevoked = appIdOfAppBeingRevoked;
        this.appNameOfAppBeingRevoked = appName;
    },
    closeManageAppAccessDisplay : function () {
        $("#displayManageAccountsWrapper-" + this.accountIdBeingManaged).hide();
    }
}