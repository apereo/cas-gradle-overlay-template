var userAccounts = new Bloodhound({
    datumTokenizer: function (datum) {
        var tokens = [];
        tokens.push(datum.infusionsoftId);
        tokens.push(datum.appName);

        return tokens;
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    limit: 20,
    remote: {
        url: '/app/support/infusionsoftIdSearch.json?query=%QUERY',
        filter: function (list) {
            console.log(list);
            return list.userAccounts;
        }
    }
});

userAccounts.initialize();

$('.typeahead').typeahead({
        highlight: true,
        hint: false
    },
    {
        name: 'user-accounts',
        displayKey: function (suggestion) {
            return '';
        },
        templates: {
            suggestion: Handlebars.compile('<h4>{{infusionsoftId}} <small>{{appUrl}}</small></h4>')
        },
        source: userAccounts.ttAdapter()
    }
).on("typeahead:selected", function (event, suggestion, dataset) {
        console.log(suggestion);
        $('#appName').val(suggestion.appName);
        $('#username').val(suggestion.infusionsoftId);
        $('#appUrl').text(suggestion.appUrl);
        $('#usernameLabel').text(suggestion.infusionsoftId);
        $('#formSearch').submit();
    }
);

$("body").popover({
    html: true,
    selector: '[rel=popover]',
    trigger: 'focus',
    content: function () {
        console.log("content");
        var $this = $(this);
        return $this.next("div").html();
    }
});

$("#username").focus();
