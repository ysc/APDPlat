// Add methods to the BasicForm that clears the isDirty flag to return "false" again.
Ext.override(Ext.form.BasicForm,{
    /**
     * clear the value of all items in BasicForm and set originalValue to ''
     * @param {Object} o
     */
	clearValues: function(o){
        o = o || this;
        o.items.each(function(f){
            if(f.items){
                this.clearValues(f);
            } else if(f.setValue){
            	f.setValue('');
                // ckeditor needs being treated specially, or an error will appear in IE
            	if (f.getXType() == "ckeditor"){
                	f.originalValue = '';
                }
                else if (f.getValue){
                	f.originalValue = f.getValue();
                }
            }
        }, this);
        this.clearInvalid();
	}
	
    /**
     * clear isDirty flag of all items of BasicForm
     * @param {Object} o
     *
     * reference: http://www.extjs.com/forum/showthread.php?t=40568
     */
    ,clearDirty : function(o){
        o = o || this;
        o.items.each(function(f){
            if(f.items){
                this.clearDirty(f);
            } else if(typeof(f.originalValue) != "undefined" && f.getValue){	// Ext.isEmpty(f.originalValue) && 
                f.originalValue = f.getValue();
            }
        }, this);
    }
    
    ,setValues : function(values){
        if(Ext.isArray(values)){ // array of objects
            for(var i = 0, len = values.length; i < len; i++){
                var v = values[i];
                var f = this.findField(v.id);
                if(f){
                    f.setValue(v.value);
                	// ckeditor is special
                    if (f.getXType() == "ckeditor"){
                    	f.originalValue = v.value;
                    }
                    // checkboxgroup.originalValue won't be set, or it may cause a bug when reset
                    else if(this.trackResetOnLoad && typeof(f.originalValue) != "undefined" && f.getValue){
                        f.originalValue = f.getValue();
                    }
                }
            }
        }else{ // object hash
            var field, id;
            for(id in values){            	
                if(typeof values[id] != 'function' && (field = this.findField(id))){
                    field.setValue(values[id]);
                    if (this.trackResetOnLoad){
	                    if (field.getXType() == "ckeditor"){
	                    	field.originalValue = values[id];
	                    }
	                    else if(typeof(field.originalValue) != "undefined" && field.getValue){
	                        field.originalValue = field.getValue();
	                    }
                    }
                }
            }
        }
        return this;
    }
    
    ,findField : function(id){
        var field = this.items.get(id); 
        if(!field){  
            this.items.each(function(f){  
                if(f.isXType('radiogroup')||f.isXType('checkboxgroup')){  
                	if (f.isXType('radiogroup'))
                		f.unitedValue = true;
                    f.items.each(function(c){  
                        if(c.isFormField && (c.dataIndex == id || c.id == id || c.getName() == id)){  
                            field = f.unitedValue ? f : c;
                            if (typeof(f.trackResetOnLoad) == "undefined")
                            	f.trackResetOnLoad = this.trackResetOnLoad;
                            return false;  
                        }  
                    }, this);  
                }  

                if(f.isFormField && (f.dataIndex == id || f.id == id || f.getName() == id)){  
                    field = f;  
                    return false;  
                }  
            }, this);  
        } 
        return field || null;
    }
});

