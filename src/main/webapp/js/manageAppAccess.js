var manageAppAccess = {
    userIdOfUserManagingAppAccess: "",
    accountIdBeingManaged : 0,
    appIdOfAppBeingRevoked : 0,
    appNameOfAppBeingRevoked : "",

    getAppsGrantedAccessToAccount: function(inputObject) {
        manageAppAccess.userIdOfUserManagingAppAccess = inputObject.userId;
        manageAppAccess.accountIdBeingManaged = inputObject.accountId;
        $.ajax("/app/oauth/manageAccounts", {
            type: "GET",
            data: { userId : manageAppAccess.userIdOfUserManagingAppAccess,
                    infusionsoftAccountId: manageAppAccess.accountIdBeingManaged
            },
            success: function (response) {
                inputObject.afterSuccess(inputObject, response);
            },
            error: function(response){
                inputObject.afterError(inputObject, response);
            }
        });
        return false;
    },
    revokeAccess: function(){
        $.ajax("/app/oauth/revokeAccess", {
            type: "POST",
            data: { userId: manageAppAccess.userIdOfUserManagingAppAccess,
                infusionsoftAccountId: manageAppAccess.accountIdBeingManaged,
                masheryAppId: manageAppAccess.appIdOfAppBeingRevoked
            },
            success: function (response) {
                $('#myModal').modal('hide');
                $("#accessRevokedFailed-" + manageAppAccess.accountIdBeingManaged).hide();
                $("#accessRevokedSuccessText-" + manageAppAccess.accountIdBeingManaged).html(manageAppAccess.appNameOfAppBeingRevoked);
                $("#accessRevokedMessageWrapper-"+ manageAppAccess.accountIdBeingManaged).show();
                $("#accessRevokedSuccess-" + manageAppAccess.accountIdBeingManaged).show();
                $("#displayManageAccountsWrapper-" + manageAppAccess.accountIdBeingManaged).slideUp(3000);

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
    closeManageAppAccessDisplay : function(accountId){
        $(".crm-account").each(function () {$(this).removeClass('expanded-apps expanded-apps-crm')});
        $("#displayManageAccountsWrapper-" + accountId).slideUp(500);
        return false;
    },
    populateModalBody: function(appIdOfAppBeingRevoked){
        var appName = $("#appName-" + appIdOfAppBeingRevoked).text();
        $("#modal-body-id p").html("Are you sure you want to revoke access for <span>" + appName + "</span>?");
        manageAppAccess.appIdOfAppBeingRevoked = appIdOfAppBeingRevoked;
        manageAppAccess.appNameOfAppBeingRevoked = appName;
    }
};