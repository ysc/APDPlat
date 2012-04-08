// vim: ts=4:sw=4:nu:fdc=4:nospell
/*global Ext */
/**
 * @class   Ext.ux.tree.CheckTreePanel
 * @extends Ext.tree.TreePanel
 *
 * <p>
 * I've taken Condor's work from 
 * <a href="http://extjs.com/forum/showthread.php?t=44369">http://extjs.com/forum/showthread.php?t=44369</a>,
 * removed tri-state functionality and put all files into one extension.
 * </p>
 * <p>
 * CheckTreePanel has been designed to fully load nodes in one shot using
 * nested "children" array. 
 * </p>
 *
 * @author   Ing. Jozef Sakáloš
 * @copyright (c) 2009, by Ing. Jozef Sakáloš
 * @version  1.0
 * @date     9. January 2009
 * @revision $Id: Ext.ux.tree.CheckTreePanel.js 593 2009-02-24 10:17:11Z jozo $
 *
 * @license Ext.ux.tree.CheckTreePanel is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @forum    56875
 * @demo     http://checktree.extjs.eu
 * @download 
 * <ul>
 * <li><a href="http://checktree.extjs.eu/checktree.tar.bz2">checktree.tar.bz2</a></li>
 * <li><a href="http://checktree.extjs.eu/checktree.tar.gz">checktree.tar.gz</a></li>
 * <li><a href="http://checktree.extjs.eu/checktree.zip">checktree.zip</a></li>
 * </ul>
 *
 * @donate
 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_blank">
 * <input type="hidden" name="cmd" value="_s-xclick">
 * <input type="hidden" name="hosted_button_id" value="3430419">
 * <input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" 
 * border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
 * <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
 * </form>
 */

Ext.ns('Ext.ux.tree');

// {{{
/**
 * Creates new CheckTreePanel
 * @constructor
 * @param {Object} config The configuration object
 */
Ext.ux.tree.CheckTreePanel = Ext.extend(Ext.tree.TreePanel, {

	// {{{
	// default config
	/**
	 * @cfg {String} bubbleCheck 
	 * Check/uncheck also parent nodes. Valid values: none, checked, unchecked, all. Defaults to 'checked'.
	 */
	 bubbleCheck:'checked' 

	/**
	 * @cfg {String} cascadeCheck 
	 * Check/uncheck also child nodes. Valid values: none, checked, unchecked, all. Defaults to 'unchecked'.
	 */
	,cascadeCheck:'unchecked'

	/**
	 * @cfg {Boolean} deepestOnly 
	 * Set this to true for getValue to return only deepest checked node ids. Defaults to false.
	 */
	,deepestOnly:false

	/**
	 * @cfg {Boolean} expandOnCheck 
	 * Expand the node on check if true. Defaults to true;
	 */
	,expandOnCheck:true

	/**
	 * @cfg {Boolean/Object} filter
	 * Set it to false to not create tree filter or set a custom filter. Defaults to true.
	 */
	,filter:true

	/**
	 * @cfg {String} separator
	 * Separator for convertValue function. Defaults to ',' (comma).
	 */
	,separator:','

	/**
	 * @cfg {String} cls
	 * Class to add to CheckTreePanel. A suitable css file must be included. Defaults to 'ux-checktree'.
	 */
	,cls:'ux-checktree'

	/**
	 * @cfg {Object} baseAttrs
	 * baseAttrs for loader. Defaults to {} (empty object).
	 */
	,baseAttrs:{}
	// }}}
	// {{{
	,initComponent:function() {

		// use our event model
		this.eventModel = new Ext.ux.tree.CheckTreeEventModel(this);

		// call parent initComponent
		Ext.ux.tree.CheckTreePanel.superclass.initComponent.apply(this, arguments);

		// pass this.baseAttrs and uiProvider down the line
		var baseAttrs = Ext.apply({uiProvider:Ext.ux.tree.CheckTreeNodeUI}, this.baseAttrs);
		Ext.applyIf(this.loader, {baseAttrs:baseAttrs, preloadChildren:true});

		// make sure that nodes are deeply preloaded
		if(true === this.loader.preloadChildren) {
			this.loader.on('load', function(loader, node) {
				node.cascade(function(n) {
					loader.doPreload(n);
					n.loaded = true;
				});
			});
		}

		// create tree filter
		if(true === this.filter) {
			var Filter = Ext.ux.tree.TreeFilterX ? Ext.ux.tree.TreeFilterX : Ext.tree.TreeFilter;
			this.filter = new Filter(this, {autoClear:true});
		}

	} // eo function initComponent
	// }}}
	// {{{
	/*
	 * get "value" (array of checked nodes ids)
	 * @return {Array} Array of chcecked nodes ids
	 */
	,getValue:function() {
		var a = [];
		this.root.cascade(function(n) {
			if(true === n.attributes.checked) {
				if(false === this.deepestOnly || !this.isChildChecked(n)) {
					a.push(n.id);
				}
			}
		}, this);
		return a;
	} // eo function getValue
	// {{{
	/**
	 * Helper function for getValue
	 * @param {Ext.tree.TreeNode} node
	 * @return {Boolean} true if node has a child checked, false otherwise
	 * @private
	 */
	,isChildChecked:function(node) {
		var checked = false;
		Ext.each(node.childNodes, function(child) {
			if(child.attributes.checked) {
				checked = true;
			}
		});
		return checked;
	} // eo function isChildChecked
	// }}}
	// }}}
	// {{{
	/*
	 * clears "value", unchecks all nodes
	 * @return {Ext.ux.tree.CheckTreePanel} this
	 */
	,clearValue:function() {
		this.root.cascade(function(n) {
			var ui = n.getUI();
			if(ui && ui.setChecked) {
				ui.setChecked(false);
			}
		});
		this.value = [];
		return this;
	} // eo function clearValue
	// }}}
	// {{{
	/*
	 * converts passed value to array
	 * @param {Mixed} val variable number of arguments, e.g. convertValue('1,8,7', 3, [9,4])
	 * @return {Array} converted value
	 */
	,convertValue:function(val) {
		// init return array
		var a = [];

		// calls itself recursively if necessary
		if(1 < arguments.length) {
			for(var i = 0; i < arguments.length; i++) {
				a.push(this.convertValue(arguments[i]));
			}
		}

		// nothing to do for arrays
		else if(Ext.isArray(val)) {
			a = val;
		}

		// just push numbers
		else if('number' === typeof val) {
			a.push(val);
		}

		// split strings
		else if('string' === typeof val) {
			a = val.split(this.separator);
		}

		return a;
	} // eo function convertValue
	// }}}
	// {{{
	/*
	 * Set nodes checked/unchecked
	 * @param {Mixed} val variable number of arguments, e.g. setValue('1,8,7', 3, [9,4])
	 * @return {Array} value. Array of checked nodes
	 */
	,setValue:function(val) {

		// uncheck all first
		this.clearValue();

		// process arguments
		this.value = this.convertValue.apply(this, arguments);

		// check nodes
		Ext.each(this.value, function(id) {
			var n = this.getNodeById(id);
			if(n) {
				var ui = n.getUI();
				if(ui && ui.setChecked) {
					ui.setChecked(true);

					// expand checked nodes
					if(true === this.expandOnCheck) {
						n.bubbleExpand();
					}
				}
			}
		}, this);

		return this.value;
	} // eo function setValue
	// }}}
	// {{{
	/*
	 * arbitrary attribute of checked nodes (text by default) is joined and separated with this.separator
	 * @param {String} attr Attribute to serialize
	 * @return {String} serialized attr of checked nodes
	 */
	,serialize:function(attr) {
		attr = attr || 'text';
		var a = [];
		this.root.cascade(function(n) {
			if(true === n.attributes.checked) {
				if(false === this.deepestOnly || !this.isChildChecked(n)) {
					a.push(n[attr]);
				}
			}
		}, this);
		return a.join(this.separator + ' ');
	} // eo function serialize
	// }}}
	// {{{
	/*
	 * alias for serialize function
	 * @param {String} attr Attribute to serialize
	 * @return {String} serialized attr of checked nodes
	 */
	,getText:function(attr) {
		return this.serialize(attr);
	} // eo function getText
	// }}}
	// {{{
	/**
	 * Creates hidden field if we're running in form for BasicForm::getValues to work
	 * @private
	 */
	,onRender:function() {
		Ext.ux.tree.CheckTreePanel.superclass.onRender.apply(this, arguments);
		if(true === this.isFormField) {
			this.hiddenField = this.body.createChild({
				tag:'input', type:'hidden', name:this.name || this.id
			}, undefined, true);
		}
	} // eo function onRender
	// }}}
	// {{{
	/**
	 * Updates hidden field if one exists on checkchange
	 * @private
	 */
	,updateHidden:function() {
		if(this.hiddenField) {
			this.hiddenField.value = this.getValue().join(this.separator);
		}
	} // eo function updateHidden
	// }}}

	// form field compatibility methods
	// todo: They could be made much more clever
	,clearInvalid:Ext.emptyFn
	,markInvalid:Ext.emptyFn
	,validate:function() {
		return true;
	}
	,isValid:function() {
		return true;
	}
	,getName:function() {
		return this.name || this.id || '';
	}

}) // eo extend

Ext.reg('checktreepanel', Ext.ux.tree.CheckTreePanel);
// }}}
// {{{
/**
 * @private
 * @ignore
 */
Ext.override(Ext.tree.TreeNode, {
	/**
	 * Expands all parent nodes of this node
	 * @private
	 */
	bubbleExpand:function() {
		var root = this.getOwnerTree().root;
		var branch = [];
		var p = this;
		while(p !== root) {
			p = p.parentNode;
			branch.push(p);
		}
		branch.reverse();
		Ext.each(branch, function(n) {
			n.expand(false, false);
		});
	}
});
// }}}
// {{{
/**
 * @class Ext.ux.tree.CheckTreeNodeUI
 * @extends Ext.tree.TreeNodeUI
 *
 * Adds checkbox to the tree node UI. This class is not intended
 * to be instantiated explicitly; it is used internally in CheckTreePanel.
 *
 * @author   Ing. Jozef Sakáloš
 * @copyright (c) 2009, by Ing. Jozef Sakáloš
 * @version  1.0
 * @date     9. January 2009
 *
 * @license Ext.ux.tree.CheckTreeNodeUI is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @donate
 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_blank">
 * <input type="hidden" name="cmd" value="_s-xclick">
 * <input type="hidden" name="hosted_button_id" value="3430419">
 * <input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" 
 * border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
 * <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
 * </form>
 */
/**
 * @constructor
 * @Creates new CheckTreeNodeUI
 * @param {Ext.tree.TreeNode} node Node to create the UI for
 */
Ext.ux.tree.CheckTreeNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {

	// {{{
	/**
	 * This is slightly adjusted original renderElements method
	 * @private
	 */
	renderElements:function(n, a, targetNode, bulkRender){
		
		this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() :'';
		var checked = n.attributes.checked;
		var href = a.href ? a.href : Ext.isGecko ? "" :"#";
        var buf = [
			 '<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">'
			,'<span class="x-tree-node-indent">',this.indentMarkup,"</span>"
			,'<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />'
			,'<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" :""),(a.iconCls ? " "+a.iconCls :""),'" unselectable="on" />'
			,'<img src="'+this.emptyIcon+'" class="x-tree-checkbox'+(true === checked ? ' x-tree-node-checked' :'')+'" />'
			,'<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="1" '
			,a.hrefTarget ? ' target="'+a.hrefTarget+'"' :"", '><span unselectable="on">',n.text,"</span></a></div>"
			,'<ul class="x-tree-node-ct" style="display:none;"></ul>'
			,"</li>"
		].join('');
		var nel;
		if(bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl())){
			this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
		}else{
			this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
		}
		this.elNode = this.wrap.childNodes[0];
		this.ctNode = this.wrap.childNodes[1];
		var cs = this.elNode.childNodes;
		this.indentNode = cs[0];
		this.ecNode = cs[1];
		this.iconNode = cs[2];
		this.checkbox = cs[3];
		this.cbEl = Ext.get(this.checkbox);
		this.anchor = cs[4];
		this.textNode = cs[4].firstChild;
	} // eo function renderElements
	// }}}
	// {{{
	/**
	 * Sets iconCls
	 * @param {String} iconCls
	 * @private
	 */
	,setIconCls:function(iconCls) {
		Ext.fly(this.iconNode).set({cls:'x-tree-node-icon ' + iconCls});
	} // eo function setIconCls
	// }}}
	// {{{
	/**
	 * @return {Boolean} true if the node is checked, false otherwise
	 * @private
	 */
	,isChecked:function() {
		return this.node.attributes.checked === true;
	} // eo function isChecked
	// }}}
	// {{{
	/**
	 * Called when check changes
	 * @private
	 */
	,onCheckChange:function() {
		var checked = this.isChecked();
		var tree = this.node.getOwnerTree();
		var bubble = tree.bubbleCheck;
		var cascade = tree.cascadeCheck;

		if('all' === bubble || (checked && 'checked' === bubble) || (!checked && 'unchecked' === bubble)) {
			this.updateParent(checked);
		}
		if('all' === cascade || (checked && 'checked' === cascade) || (!checked && 'unchecked' === cascade)) {
			this.updateChildren(checked);
		}

		tree.updateHidden();
		this.fireEvent('checkchange', this.node, checked);
	} // eo function onCheckChange
	// }}}
	// {{{
	/**
	 * Sets node UI checked/unchecked
	 * @param {Boolean} checked true to set node checked, false to uncheck
	 * @return {Boolean} checked
	 */
	,setChecked:function(checked) {
		checked = true === checked ? checked : false;
		var cb = this.cbEl || false;
		if(cb) {
			true === checked ? cb.addClass('x-tree-node-checked') : cb.removeClass('x-tree-node-checked');
		}
		this.node.attributes.checked = checked;
		this.onCheckChange();
		return checked;
	} // eo function setChecked
	// }}}
	// {{{
	/**
	 * Toggles check
	 * @return {Boolean} value after toggle
	 */
	,toggleCheck:function() {
		var checked = !this.isChecked();
		this.setChecked(checked);
		return checked;
	} // eo function toggleCheck
	// }}}
	// {{{
	/**
	 * Sets parents checked/unchecked. Used if bubbleCheck is not 'none'
	 * @param {Boolean} checked
	 * @private
	 */
	,updateParent:function(checked) {
		var p = this.node.parentNode;
		var ui = p ? p.getUI() : false;
		
		if(ui && ui.setChecked) {
			ui.setChecked(checked);
		}
	} // eo function updateParent
	// }}}
	// {{{
	/**
	 * Sets children checked/unchecked. Used if cascadeCheck is not 'none'
	 * @param {Boolean} checked
	 * @private
	 */
	,updateChildren:function(checked) {
		this.node.eachChild(function(n) {
			var ui = n.getUI();
			if(ui && ui.setChecked) {
				ui.setChecked(checked);
			}
		});
	} // eo function updateChildren
	// }}}
	// {{{
	/**
	 * Checkbox click event handler
	 * @private
	 */
	,onCheckboxClick:function() {
		if(!this.disabled) {
			this.toggleCheck();
		}
	} // eo function onCheckboxClick
	// }}}
	// {{{
	/**
	 * Checkbox over event handler
	 * @private
	 */
	,onCheckboxOver:function() {
		this.addClass('x-tree-checkbox-over');
	} // eo function onCheckboxOver
	// }}}
	// {{{
	/**
	 * Checkbox out event handler
	 * @private
	 */
	,onCheckboxOut:function() {
		this.removeClass('x-tree-checkbox-over');
	} // eo function onCheckboxOut
	// }}}
	// {{{
	/**
	 * Mouse down on checkbox event handler
	 * @private
	 */
	,onCheckboxDown:function() {
		this.addClass('x-tree-checkbox-down');
	} // eo function onCheckboxDown
	// }}}
	// {{{
	/**
	 * Mouse up on checkbox event handler
	 * @private
	 */
	,onCheckboxUp:function() {
		this.removeClass('x-tree-checkbox-down');
	} // eo function onCheckboxUp
	// }}}
 
}); // eo extend
// }}}
// {{{
/**
 * @class   Ext.ux.tree.CheckTreeEventModel
 * @extends Ext.tree.TreeEventModel
 *
 * Tree event model suitable for use with CheckTreePanel.
 * This class is not intended to be instantiated explicitly by a user
 * but it is used internally by CheckTreePanel.
 *
 * @author   Ing. Jozef Sakáloš
 * @copyright (c) 2009, by Ing. Jozef Sakáloš
 * @version  1.0
 * @date     9. January 2009
 *
 * @license Ext.ux.tree.CheckTreeEventModel is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @donate
 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_blank">
 * <input type="hidden" name="cmd" value="_s-xclick">
 * <input type="hidden" name="hosted_button_id" value="3430419">
 * <input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" 
 * border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
 * <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
 * </form>
 */
/**
 * Create new CheckTreeEventModel
 * @constructor
 * @param {Ext.ux.tree.CheckTreePanel} tree The tree to apply this event model to
 */
Ext.ux.tree.CheckTreeEventModel = Ext.extend(Ext.tree.TreeEventModel, {
     initEvents:function(){
        var el = this.tree.getTreeEl();
        el.on('click', this.delegateClick, this);
        if(this.tree.trackMouseOver !== false){
            el.on('mouseover', this.delegateOver, this);
            el.on('mouseout', this.delegateOut, this);
        }
        el.on('mousedown', this.delegateDown, this);
        el.on('mouseup', this.delegateUp, this);
        el.on('dblclick', this.delegateDblClick, this);
        el.on('contextmenu', this.delegateContextMenu, this);
    }
	,delegateOver:function(e, t){
        if(!this.beforeEvent(e)){
            return;
        }
        if(this.lastEcOver){
            this.onIconOut(e, this.lastEcOver);
            delete this.lastEcOver;
        }
        if(this.lastCbOver){
            this.onCheckboxOut(e, this.lastCbOver);
            delete this.lastCbOver;
        }
        if(e.getTarget('.x-tree-ec-icon', 1)){
            this.lastEcOver = this.getNode(e);
            this.onIconOver(e, this.lastEcOver);
        }
        else if(e.getTarget('.x-tree-checkbox', 1)){
            this.lastCbOver = this.getNode(e);
            this.onCheckboxOver(e, this.lastCbOver);
        }
        if(t = this.getNodeTarget(e)){
            this.onNodeOver(e, this.getNode(e));
        }
    }
	,delegateOut:function(e, t){
        if(!this.beforeEvent(e)){
            return;
        }
        if(e.getTarget('.x-tree-ec-icon', 1)){
            var n = this.getNode(e);
            this.onIconOut(e, n);
            if(n == this.lastEcOver){
                delete this.lastEcOver;
            }
        }
        else if(e.getTarget('.x-tree-checkbox', 1)){
            var n = this.getNode(e);
            this.onCheckboxOut(e, n);
            if(n == this.lastCbOver){
                delete this.lastCbOver;
            }
        }
        if((t = this.getNodeTarget(e)) && !e.within(t, true)){
            this.onNodeOut(e, this.getNode(e));
        }
    }
	,delegateDown:function(e, t){
        if(!this.beforeEvent(e)){
            return;
        }
        if(e.getTarget('.x-tree-checkbox', 1)){
            this.onCheckboxDown(e, this.getNode(e));
        }
    }
	,delegateUp:function(e, t){
        if(!this.beforeEvent(e)){
            return;
        }
        if(e.getTarget('.x-tree-checkbox', 1)){
            this.onCheckboxUp(e, this.getNode(e));
        }
    }
	,delegateOut:function(e, t){
        if(!this.beforeEvent(e)){
            return;
        }
        if(e.getTarget('.x-tree-ec-icon', 1)){
            var n = this.getNode(e);
            this.onIconOut(e, n);
            if(n == this.lastEcOver){
                delete this.lastEcOver;
            }
        }
        else if(e.getTarget('.x-tree-checkbox', 1)){
            var n = this.getNode(e);
            this.onCheckboxOut(e, n);
            if(n == this.lastCbOver){
                delete this.lastCbOver;
            }
        }
        if((t = this.getNodeTarget(e)) && !e.within(t, true)){
            this.onNodeOut(e, this.getNode(e));
        }
    }
	,delegateClick:function(e, t){
		if(!this.beforeEvent(e)){
			return;
		}
		if(e.getTarget('.x-tree-checkbox', 1)){
			this.onCheckboxClick(e, this.getNode(e));
		}
		else if(e.getTarget('.x-tree-ec-icon', 1)){
			this.onIconClick(e, this.getNode(e));
		}
		else if(this.getNodeTarget(e)){
			this.onNodeClick(e, this.getNode(e));
		}
	}
	,onCheckboxClick:function(e, node){
		node.ui.onCheckboxClick();
	}
	,onCheckboxOver:function(e, node){
		node.ui.onCheckboxOver();
	}
	,onCheckboxOut:function(e, node){
		node.ui.onCheckboxOut();
	}
	,onCheckboxDown:function(e, node){
		node.ui.onCheckboxDown();
	}
	,onCheckboxUp:function(e, node){
		node.ui.onCheckboxUp();
	}
}); // eo extend
// }}}

// eof
