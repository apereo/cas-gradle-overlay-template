Infusion('Widgets.InputButton');
Infusion.Widgets.InputButton.replace = function(target) {
    // Fix for IE7 extra space within buttons
    if (jQuery.browser.msie) {
        jQuery('body').append('<span style="visibility:hidden" id="ieButtonSizeTestElement"></span>');
        var testEleJQ = jQuery('#ieButtonSizeTestElement');
    }
    jQuery('input.inf-button',target).each(function(){
        var thisEleJQ = jQuery(this);
        if (!(thisEleJQ.parents('span.inf-button').get(0))) {
            var className = this.className;
            var title = this.title;
            thisEleJQ.wrap('<span class="'+className+'" title="'+title+'"><span class="inner1"><span class="inner2"></span></span></span>')
                   .parents('span.inf-button').click(function(event){
                       var $target = jQuery(event.target);
                       if( !($target.is("input")) ) {
                         jQuery('input',this).click();
                       }
                    });
            // Fix for IE7 extra space within buttons
            if (jQuery.browser.msie) {
                testEleJQ.text(this.value);
                if (testEleJQ.attr('hasPropertiesSet') != 'true'){
                    var props = ['font-size','font-family','font-weight'];
                    for (var i=0; i< props.length; i++) {
                        testEleJQ.css(props[i], thisEleJQ.css(props[i]));
                    }
                    testEleJQ.attr('hasPropertiesSet','true');
                }
                thisEleJQ.css('width',(testEleJQ.get(0).offsetWidth +
                                       parseInt(thisEleJQ.css('padding-left'),10) + 3 +
                                       parseInt(thisEleJQ.css('padding-right'),10) + 3)+
                                      'px');
            }
        }
    });
    // IE hack to reduce problem with disappearing text and other positioning issues
    if (jQuery.browser.msie) {
        testEleJQ.remove();
        jQuery('.inf-button').css('hasLayout','true');
        jQuery('.inf-button span').css('hasLayout','true');
        jQuery('button').css('hasLayout','true');
    }
};
Infusion.Widgets.InputButton.init = function() {
    YAHOO.util.Event.onDOMReady(function(){
        Infusion.Widgets.InputButton.replace(document);
        jQuery(document).bind("ajaxFill",function(e,fill) {
            Infusion.Widgets.InputButton.replace(fill);
        });
    });
}();
