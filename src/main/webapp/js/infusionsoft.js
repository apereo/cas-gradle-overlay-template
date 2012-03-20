/**
* It's good practice to have scope management for our Javascript. We should try to scope all Infusion JavaScript
* so that it doesn't collide with out JavaScript packages we may decide to use.
*
* Infusion function finds (or creates, if not previously done) a reference to the specified package
* <pre>
* Infusion("property.package");
* </pre>
* This would create, or return the reference to:
* <pre>
* Infusion.property.package
* </pre>
*
* Be careful when naming packages. Reserved words (e.g. long) may not work in some browsers.
*
* @method Infusion
* @static
* @param  {String*} arguments 1-n namespaces (dot separated) to create/find
* @return {Object}  A reference to the last namespace created/found
*/
window.Infusion = function(path) {
    var parts = path.split('.');
    var pointer = window.Infusion;
    for (var i=0; i< parts.length; i++){
        if (!pointer[parts[i]]){
            pointer[parts[i]] = {name:parts[i]};
        }
        pointer = pointer[parts[i]];
    }
    return pointer;
};
/**
* Need a convenience method to test for the existence of a whole Object path
*
* @method Infusion.has
* @static
* @param  {String*} arguments 1-n namespaces (dot separated) to create/find
* @param  {Object}? optional context Object to check path within.  If not supplied, the window Object will be used
* @return {Boolean}  Whether that path exists within the supplied context
*/
Infusion.has = function(path, context) {
    context = context? context : window;
    var hasIt = true;
    var parts = path.split('.');
    var pointer = context;
    for (var i=0; i< parts.length; i++){
        if (!pointer[parts[i]]){
            hasIt = false;
            break;
        }
        else {
            pointer = pointer[parts[i]];
        }
    }
    return hasIt;
};