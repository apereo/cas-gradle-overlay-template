<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style type="text/css">

    .borderlessTable-zebra-striped {
        border: medium none !important;
        width: 100%;
    }
    table {
        border-collapse: collapse;
        border-spacing: 0;
        empty-cells: show;
        padding: 0;
    }
    .borderlessTable-zebra-striped tr:nth-child(odd) {
        background-color: #F5F5F5;
        border-bottom: 1px solid #EBEBEB;
        border-top: 1px solid #EBEBEB;
    }
    .td-width {
        overflow: hidden;
    }
    .dt {
        white-space: nowrap;
    }
    .dateColumn {
        width: 100px;
    }
    table.borderlessTable-tabular td {
        line-height: 18px;
        padding: 10px 10px 10px 15px;
        text-align: left;
    }
    td {
        color: #444444;
        font-family: 'Open Sans',Arial,Verdana,Sans-Serif;
        font-size: 12px;
    }
    p.borderlessTableHeader {
        color: #444444;
        font-family: "Open Sans" !important;
        font-size: 24px;
        font-style: normal;
        font-weight: 300 !important;
        margin-bottom: 19px !important;
        margin-top: 0 !important;
    }
    table.borderlessTable {
        margin-bottom: 5px !important;
        width: 100%;
    }
    td.borderlessTableTd {
        color: #444444;
        font-family: "Open Sans" !important;
        font-size: 24px;
        font-weight: 300 !important;
        padding-top: 5px;
    }
 </style>
<table class="borderlessTable">
    <tbody>
        <tr>
            <div id="close-${infusionsoftAccountId}"><a href="" onclick="manageAppAccess.closeAppAccessDisplay()">close</a></div>
        </tr>
        <tr>
            <td>
                <div id="accessRevokedSuccess-${infusionsoftAccountId}" class="alert-success" style="margin-top: 10px; display: none"></div>
                <div id="accessRevokedFailed-${infusionsoftAccountId}" class="alert alert-error" style="margin-top: 10px; display: none"></div>
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
                        <span id="appName-${app.id}">${app.name}</span><br>
                        <span id="appDescription-${app.id}">A description blah blah blah</span>
                    </td>
                    <td style="text-align: right" class="addChunkyButton">
                        <div id="editionControls-2">
                            <input type="button" value="Revoke Access" name="Tasks" onfocus="manageAppAccess.populateModalBody(${app.id});" class="inf-button primary" data-toggle="modal" data-target="#myModal">
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <table cellspacing="0px" cellpadding="0px" border="0px" class="borderlessTable-zebra-striped borderlessTable-tabular borderlessTable-data-table is-layout">
            <tbody>
                <tr class="data-td">
                    <td align="left" class="td-width dt dateColumn">You have not granted access to any applications.
                    </td>
                </tr>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
