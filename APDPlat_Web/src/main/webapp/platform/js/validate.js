function validTab(tab){
    var result=true;
    tab.items.each(function(f){
           var column=f.items;
           if(column!=undefined){
               column.each(function(e){
                       var t=e.items;
                       if(t!=undefined){
                            t.each(function(f){
                                   if(f.validate && !f.validate()){
                                       result = false;
                                   }
                            });
                       }
                       if(f.validate && !f.validate()){
                           result = false;
                       }
                });
           }
           if(f.validate && !f.validate()){
               result = false;
           }
    });
    return result;
}
function validateTabForm(){
    var tabs = parent.Ext.getCmp('tabs');
    var tab1 = parent.Ext.getCmp('tab1');
    tabs.setActiveTab(tab1);
    var tab1Valid = true;
    var tab2Valid = true;
    var tab3Valid = true;
    tab1Valid = validTab(tab1);
    if (tab1Valid) {
            var tab2 = parent.Ext.getCmp('tab2');
            tab2Valid = validTab(tab2);
    }
    if (tab1Valid && tab2Valid) {
            var tab3 = parent.Ext.getCmp('tab3');
            tab3Valid = validTab(tab3);
    }
    if(!tab1Valid){
        tabs.setActiveTab(tab1);
    }
    if(tab1Valid && !tab2Valid){
        tabs.setActiveTab(tab2);
    }
    if(tab1Valid && tab2Valid && !tab3Valid){
        tabs.setActiveTab(tab3);
    }
}