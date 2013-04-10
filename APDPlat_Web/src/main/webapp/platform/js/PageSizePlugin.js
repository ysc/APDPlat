/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/**
 * 扩展列表分页组件，支持显示条数，分页条数，总页数，选择跳转页面。
 * 
 */
Ext.ux.PageSizePlugin = Ext.extend(Ext.PagingToolbar, {
	displayMsg : "第{0}-{1}条&nbsp;&nbsp;共{2}条&nbsp;&nbsp;&nbsp;&nbsp;共{3}页",
	style : 'font-weight:900',
	rowComboSelect : true,
	displayInfo : true,
	doLoad : function(start) {
		var o = {}, pn = this.getParams() || {};
		o[pn.start] = start;
		o[pn.limit] = this.pageSize;
		if (this.store.baseParams && this.store.baseParams[pn.limit])
			this.store.baseParams[pn.limit] = this.pageSize;
		if (this.fireEvent('beforechange', this, o) !== false) {
			this.store.load({
						params : o
					});
		}
	},
	onPagingSelect : function(combo, record, index) {
		var d = this.getPageData(), pageNum;
		pageNum = this.readPage(d);
		if (pageNum !== false) {
			pageNum = Math.min(Math.max(1, record.data.pageIndex), d.pages) - 1;
			this.doLoad(pageNum * this.pageSize);
		}
	},
	readPage : Ext.emptyFn,
	onLoad : function(store, r, o) {
		var d = this.getPageData(), ap = d.activePage, ps = d.pages;
		this.combo.store.removeAll();
		if (ps == 0) {
			this.combo.store.add(new Ext.data.Record({
						pageIndex : 1
					}));
			this.combo.setValue(1);
		} else {
			for (var i = 0; i < ps; i++) {
				this.combo.store.add(new Ext.data.Record({
							pageIndex : i + 1
						}));
			}
			this.combo.setValue(ap);
		}
		if (this.rowComboSelect)
			this.rowcombo.setValue(this.pageSize);
		Ext.ux.PageSizePlugin.superclass.onLoad.apply(this, arguments);
	},
	updateInfo : function() {
		if (this.displayItem) {
			var count = this.store.getCount();
			var activePage = this.getPageData().activePage;
			var msg = count == 0 ? this.emptyMsg : String.format(
					this.displayMsg, this.cursor + 1, this.cursor + count,
					this.store.getTotalCount(), Math.ceil(this.store
							.getTotalCount()
							/ this.pageSize), activePage);
			this.displayItem.setText(msg);
		}
	},
	// 选择每页多少条数据
	onComboPageSize : function(combo, record, index) {
                //全局变量
		pageSize = record.get('pageSize');
		this.store.pageSize = this.pageSize = pageSize;
		var d = this.getPageData(), pageNum;
		pageNum = this.readPage(d);
		if (pageNum !== false) {
			pageNum = Math.min(Math.max(1, record.data.pageIndex), d.pages) - 1;
			this.doLoad(0);
		}
	},
	initComponent : function() {
                var _pageSize=this.store.pageSize;
		if (_pageSize) {
			this.pageSize = _pageSize;
		}
		this.combo = Ext.ComponentMgr.create(Ext.applyIf(this.combo || {}, {
					value : 1,
					width : 50,
					store : new Ext.data.SimpleStore({
								fields : ['pageIndex'],
								data : [[1]]
							}),
					mode : 'local',
					xtype : 'combo',
					minListWidth : 50,
					allowBlank : false,
					triggerAction : 'all',
					displayField : 'pageIndex',
					allowDecimals : false,
					allowNegative : false,
					enableKeyEvents : true,
					selectOnFocus : true,
					submitValue : false
				}));
		this.combo.on("select", this.onPagingSelect, this);
		this.combo.on('specialkey', function() {
					this.combo.setValue(this.combo.getValue());
				}, this);

		var T = Ext.Toolbar;

		var pagingItems = [];

		if (this.displayInfo) {
			pagingItems.push(this.displayItem = new T.TextItem({}));
		}

		if (this.rowComboSelect) {
			var data = this.rowComboData ? this.rowComboData : [[5], [10], [15], [20]];
			this.rowcombo = this.rowcombo || Ext.create({
						store : new Ext.data.SimpleStore({
									fields : ['pageSize'],
									data : data
								}),
						value : this.pageSize,
						width : 50,
						mode : 'local',
                                                editable:false,
						xtype : 'combo',
						allowBlank : false,
						minListWidth : 50,
						displayField : 'pageSize',
						triggerAction : 'all'
					});
			pagingItems.push(this.rowcombo, "条/页&nbsp;&nbsp;");

			this.rowcombo.on("select", this.onComboPageSize, this);
			this.rowcombo.on('specialkey', function() {
						this.combo.setValue(this.combo.getValue());
					}, this);
		}

		// this.totalPage = new T.TextItem({})
		pagingItems.push('->', this.first = new T.Button({
							tooltip : this.firstText,
							overflowText : this.firstText,
							iconCls : 'x-tbar-page-first',
							disabled : true,
							handler : this.moveFirst,
							scope : this
						}), this.prev = new T.Button({
							tooltip : this.prevText,
							overflowText : this.prevText,
							iconCls : 'x-tbar-page-prev',
							disabled : true,
							handler : this.movePrevious,
							scope : this
						}), '-', this.beforePageText,
				this.inputItem = this.combo,
				this.afterTextItem = new T.TextItem({
							text : String.format(this.afterPageText, 1)
						}), '-', this.next = new T.Button({
							tooltip : this.nextText,
							overflowText : this.nextText,
							iconCls : 'x-tbar-page-next',
							disabled : true,
							handler : this.moveNext,
							scope : this
						}), this.last = new T.Button({
							tooltip : this.lastText,
							overflowText : this.lastText,
							iconCls : 'x-tbar-page-last',
							disabled : true,
							handler : this.moveLast,
							scope : this
						}), '-', this.refresh = new T.Button({
							tooltip : this.refreshText,
							overflowText : this.refreshText,
							iconCls : 'x-tbar-loading',
							handler : this.doRefresh,
							scope : this
						}));

		var userItems = this.items || this.buttons || [];
		if (this.prependButtons===true) {
			this.items = userItems.concat(pagingItems);
		}else if(Ext.isNumber(this.prependButtons)){
			pagingItems.splice.apply(pagingItems,[this.prependButtons,0].concat(userItems));
			this.items = pagingItems;
		}else{
			this.items = pagingItems.concat(userItems);
		}
		delete this.buttons;
		Ext.PagingToolbar.superclass.initComponent.call(this);
		this.addEvents('change', 'beforechange');
		this.on('afterlayout', this.onFirstLayout, this, {
					single : true
				});
		this.cursor = 0;
		this.bindStore(this.store, true);
	}
});