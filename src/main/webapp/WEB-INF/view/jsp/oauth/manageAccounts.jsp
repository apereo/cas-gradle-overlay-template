<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="borderlessTable">
    <tbody>
        <tr>
            <div id="close-${infusionsoftAccountId}" class="close-link"><a onclick="return manageAppAccess.closeManageAppAccessDisplay(${infusionsoftAccountId});">close</a>&nbsp;<img src="/images/close-arrow.png" /></div>
        </tr>
        <tr id="accessRevokedMessageWrapper-${infusionsoftAccountId}" style="display: none">
            <td>
                <div id="accessRevokedSuccess-${infusionsoftAccountId}" class="alert-success" style="margin-top: 10px; display: none">
                    <span>You have successfully revoked access for <span id="accessRevokedSuccessText-${infusionsoftAccountId}"></span></span>
                </div>
                <div id="accessRevokedFailed-${infusionsoftAccountId}" class="alert alert-error" style="margin-top: 10px; display: none">
                    <span>Unable to revoke access for <span id="accessRevokedFailedText-${infusionsoftAccountId}"></span></span>
                </div>
            </td>
        </tr>
        <tr>
            <td align="left" class="borderlessTableTd">
                <p class="borderlessTableHeader">
                    Apps Accessing This Account
                </p>
            </td>
        </tr>
    </tbody>
</table>
<c:choose>
    <c:when test="${fn:length(appsGrantedAccess) > 0}">
        <table cellspacing="0px" cellpadding="0px" border="0px" class="borderlessTable-zebra-striped borderlessTable-tabular borderlessTable-data-table is-layout">
            <tbody>
            <c:forEach var="app" items="${appsGrantedAccess}">
                <tr class="data-td">
                    <td align="left" class="td-width dt dateColumn">
                        <span class="app-name" id="appName-${app.id}">${app.name}</span><br>
                        <%--<span id="appDescription-${app.id}">A description blah blah blah</span>--%>
                    </td>
                    <td style="text-align: right" class="addChunkyButton">
                        <div id="editionControls-2">
                            <input type="button" value="Revoke Access" name="Tasks" onfocus="manageAppAccess.populateModalBody(${app.id});" class="inf-button primary btn" data-toggle="modal" data-target="#myModal">
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <table style="width: 100%;" border="0px" cellpadding="0px" cellspacing="0px"><tbody><tr><td align="left"><div class="no-content">You haven't given any apps access to this account. You can find apps in the <a href="http://marketplace.infusionsoft.com/apps" target="_blank">Marketplace</a>.</div></td>
        </tr></tbody></table>
    </c:otherwise>
</c:choose>
