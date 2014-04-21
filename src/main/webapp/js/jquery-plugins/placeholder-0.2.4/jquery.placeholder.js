/**
 * jquery.placeholder http://matoilic.github.com/jquery.placeholder
 *
 * @version v0.2.4
 * @author Mato Ilic <info@matoilic.ch>
 * @copyright 2013 Mato Ilic
 *
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 */
;(function($, doc, debug) {
    var input = ('placeholder' in doc.createElement('input')), 
        textarea = ('placeholder' in doc.createElement('textarea')), 
        selector = ':input[placeholder]';
    
    $.placeholder = {input: input, textarea: textarea};
    
    //skip if there is native browser support for the placeholder attribute
    if(!debug && input && textarea) {
        $.fn.placeholder = function() {};
        return;
    }
    
    if(!debug && input && !textarea) {
        selector = 'textarea[placeholder]';
    }

    /* patch jQuery.fn.val to return an empty value if the value matches 
     * the placeholder
     */
    $.fn.realVal = $.fn.val;
    $.fn.val = function() {
        var $element = $(this), val, placeholder;
        if(arguments.length > 0) return $element.realVal.apply(this, arguments);
        
        val = $element.realVal();
        placeholder = $element.attr('placeholder');
        
        return ((val == placeholder) ? '' : val);
    };
    
    function clearForm() {
        $(this).find(selector).each(removePlaceholder);
    }
    
    function extractAttributes(elem) {
        var attr = elem.attributes, copy = {}, skip = /^jQuery\d+/;
        for(var i = 0; i < attr.length; i++) {
            if(attr[i].specified && !skip.test(attr[i].name)) {
                copy[attr[i].name] = attr[i].value;
            }
        }
        return copy;
    }
    
    function removePlaceholder() {
        var $target = $(this), $clone, $orig;
        
        if($target.is(':password')) return;
        
        if($target.data('password')) {
            $orig = $target.next().show().focus();
            $('label[for=' + $target.attr('id') + ']').attr('for', $orig.attr('id'));
            $target.remove();
        } else if($target.realVal() == $target.attr('placeholder')) {
            $target.val('');
            $target.removeClass('placeholder');
        }
    }
    
    function setPlaceholder() {
        var $target = $(this), $clone, plceholder, hasVal, cid;
        placeholder = $target.attr('placeholder');

        if($.trim($target.val()).length > 0) return;
        
        if($target.is(':password')) {
            cid = $target.attr('id') + '-clone';
            $clone = $('<input/>')
                        .attr($.extend(extractAttributes(this), {type: 'text', value: placeholder, 'data-password': 1, id: cid}))
                        .addClass('placeholder');

            $target.before($clone).hide();
            $('label[for=' + $target.attr('id') + ']').attr('for', cid);
        } else {
            $target.val(placeholder);
            $target.addClass('placeholder');
        }
    }
    
    $.fn.placeholder = function() {
        this.filter(selector).each(setPlaceholder);
        return this;
    };
    
    $(function($) {
        var $doc = $(doc);
        $doc.on('submit', 'form', clearForm);
        $doc.on('focus', selector, removePlaceholder);
        $doc.on('blur', selector, setPlaceholder);
        $(selector).placeholder();
    });
})(jQuery, document, window.debug);
