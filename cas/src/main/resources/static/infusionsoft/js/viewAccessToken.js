$("#btnTest").on("click", function() {
    var $this = $(this);
    var $icon = $this.find("i");
    var $testResponse = $("#testResponse");

    var accessToken = $("#token").text();

    console.log($icon);

    $icon.removeClass("fa-play");
    $icon.addClass("fa-spin fa-spinner");

    $.post("/app/mashery/testApiCall.json", {accessToken: accessToken}, function(data) {
        if(data.success) {
            $icon.removeClass("fa-spin fa-spinner");
            $icon.addClass("fa-thumbs-o-up");

            $this.addClass("btn-success");
        } else {
            $icon.removeClass("fa-spin fa-spinner");
            $this.removeClass("btn-success");

            $icon.addClass("fa-thumbs-o-down");
            $this.addClass("btn-danger");
        }

        $testResponse.text(data.responseText);
    })
});