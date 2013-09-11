var global = {
    showSpinner: function(options){
        var selector = '[id=\"' + options.id + '\"]';
        var spinnerText = "Loading...";

        //Dynamically build the spinner HTML based on the options given.
        var spinnerHtml = "<table";

        if (options.shouldShowSpinnerOnCenter) {
            spinnerHtml += " align='center'";
        }
        //Add style to the table, if available
        if (options.tableStyle) {
            spinnerHtml += " class=\"" + options.tableStyle + "\"";
        }

        //Sets the height of the table, if available
        if (options.tableHeight) {
            spinnerHtml += " style=\"height:" + options.tableHeight + ";\"";
        }

        //Add the spinner
        spinnerHtml += " ><tr><td><img src=\"/images/ajax-spinner.gif\"></td><td>" + spinnerText + "</td></tr>";

        if (options.onlyShowSpinnerText) {
            jQuery(selector).html("<span style=\"color:#000077\">" + spinnerText + "</span>");

        } else {
            jQuery(selector).html(spinnerHtml);
        }
     }
}