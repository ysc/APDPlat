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

Ext.apply(Ext.form.VTypes, {
	/**
	 * 日期验证器 用于验证起始日期必须早于结束日期
	 * 
	 * <pre>
	 * <code>
	 * //例子:
items:[
	{xtype:"datefield",id:"startDate",fieldLabel:"开始时间",name:"start",vtype:"daterange",endDateField:"endDate"},
	{xtype:"datefield",id:"endDate",fieldLabel:"结束时间",name:"end",vtype:"daterange",startDateField:"startDate"},
]
    	</code>
    	</pre>
	 * @param {Date} val 日期
	 * @param {Date} field Ext.form.DateField
	 */
    daterange : function(val, field) {
        var date = field.parseDate(val);
        if (!date) {
            return;
        }
        if (field.startDateField
                && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax
                        .getTime()))) {
            var start = Ext.getCmp(field.startDateField);
            start.setMaxValue(date);
            start.validate();
            this.dateRangeMax = date;
        } else if (field.endDateField
                && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin
                        .getTime()))) {
            var end = Ext.getCmp(field.endDateField);
            end.setMinValue(date);
            end.validate();
            this.dateRangeMin = date;
        }
        return true;
    }
});

var vcity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",
            21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",
            33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",
            42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",
            51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",
            63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"
           };

//检查号码是否符合规范，包括长度，类型
isCardNo = function(card)
{
    //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
    var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
    if(reg.test(card) == false)
    {
        return false;
    }

    return true;
};

//取身份证前两位,校验省份
checkProvince = function(card)
{
    var province = card.substr(0,2);
    if(vcity[province] == undefined)
    {
        return false;
    }
    return true;
};

//检查生日是否正确
checkBirthday = function(card)
{
    var len = card.length;
    //身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字
    if(len == '15')
    {
        var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
        var arr_data = card.match(re_fifteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date('19'+year+'/'+month+'/'+day);
        return verifyBirthday('19'+year,month,day,birthday);
    }
    //身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X
    if(len == '18')
    {
        var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
        var arr_data = card.match(re_eighteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date(year+'/'+month+'/'+day);
        return verifyBirthday(year,month,day,birthday);
    }
    return false;
};

//校验日期
verifyBirthday = function(year,month,day,birthday)
{
    var now = new Date();
    var now_year = now.getFullYear();
    //年月日是否合理
    if(birthday.getFullYear() == year && (birthday.getMonth() + 1) == month && birthday.getDate() == day)
    {
        //判断年份的范围（3岁到100岁之间)
        var time = now_year - year;
        if(time >= 3 && time <= 100)
        {
            return true;
        }
        return false;
    }
    return false;
};

//校验位的检测
checkParity = function(card)
{
    //15位转18位
    card = changeFivteenToEighteen(card);
    var len = card.length;
    if(len == '18')
    {
        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
        var cardTemp = 0, i, valnum;
        for(i = 0; i < 17; i ++)
        {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        valnum = arrCh[cardTemp % 11];
        if (valnum == card.substr(17, 1))
        {
            return true;
        }
        return false;
    }
    return false;
};

//15位转18位身份证号
changeFivteenToEighteen = function(card)
{
    if(card.length == '15')
    {
        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
        var cardTemp = 0, i;
        card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
        for(i = 0; i < 17; i ++)
        {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        card += arrCh[cardTemp % 11];
        return card;
    }
    return card;
};
parent.Ext.apply(parent.Ext.form.VTypes,
{
   idCard: function(val, field)
  {
      var card=val.toString();
    //是否为空
    if(card == '')
    {
        //document.getElementById('card_no').focus;
        return false;
    }
    //校验长度，类型
    if(isCardNo(card) == false)
    {
        return false;
    }
    //检查省份
    if(checkProvince(card) == false)
    {
        return false;
    }
    //校验生日
    if(checkBirthday(card) == false)
    {
        return false;
    }
    //检验位的检测
    if(checkParity(card) == false)
    {
        return false;
    }
    return true;

  },
  idCardText: '您输入的身份证号码不正确,请重新输入！',

  password: function(val, field)
  {
        if (field.initialPassField)
        {
            var pwd = Ext.getCmp(field.initialPassField);
            return (val == pwd.getValue());
        }
        return true;
  },
  passwordText: '两次输入的密码不一致！',

  chinese:function(val,field)
  {
        var reg = /^[\u4e00-\u9fa5]+$/i;
        if(!reg.test(val))
        {
            return false;
        }
        return true;
  },
  chineseText:'请输入中文',

  age:function(val,field)
  {
        try
        {
            if(parseInt(val) >= 18 && parseInt(val) <= 100)
                return true;
            return false;
        }
        catch(err)
        {
            return false;
        }
  },
  ageText:'年龄输入有误',

  alphanum:function(val,field)
  {
        try
        {
            if(!/\W/.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  alphanumText:'请输入英文字母或是数字,其它字符是不允许的.',

  url:function(val,field)
  {
        try
        {
            if(/^(http|https|ftp):\/\/(([A-Z0-9][A-Z0-9_-]*)(\.[A-Z0-9][A-Z0-9_-]*)+)(:(\d+))?\/?/i.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  urlText:'请输入有效的URL地址.',

  max:function(val,field)
  {
        try
        {
            if(parseFloat(val) <= parseFloat(field.max))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  maxText:'超过最大值',

  min:function(val,field)
  {
        try
        {
            if(parseFloat(val) >= parseFloat(field.min))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  minText:'小于最小值',

  datecn:function(val,field)
  {
        try
        {
            var regex = /^(\d{4})-(\d{2})-(\d{2})$/;
            if(!regex.test(val)) return false;
            var d = new Date(val.replace(regex, '$1/$2/$3'));
            return (parseInt(RegExp.$2, 10) == (1+d.getMonth())) && (parseInt(RegExp.$3, 10) == d.getDate())&&(parseInt(RegExp.$1, 10) == d.getFullYear());
        }
        catch(e)
        {
            return false;
        }
  },
  datecnText:'请使用这样的日期格式: yyyy-mm-dd. 例如:2008-06-20.',

  integer:function(val,field)
  {
        try
        {
            if(/^[-+]?[\d]+$/.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  integerText:'请输入正确的整数',

  minlength:function(val,field)
  {
        try
        {
            if(val.length >= parseInt(field.minlen))
                return true;
            return false
        }
        catch(e)
        {
            return false;
        }
  },
  minlengthText:'长度过小',

  maxlength:function(val,field)
  {
     try
     {
        if(val.length <= parseInt(field.maxlen))
            return true;
        return false;
     }
     catch(e)
     {
        return false;
     }
  },
  maxlengthText:'长度过大',

  ip:function(val,field)
  {
        try
        {
            if((/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(val)))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  ipText:'请输入正确的IP地址',

  phone:function(val,field)
  {
        try
        {
            if(/^((0[1-9]{3})?(0[12][0-9])?[-])?\d{6,8}$/.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  phoneText:'请输入正确的电话号码,如:0920-29392929',

  mobilephone:function(val,field)
  {
        try
        {
            if(/(^0?[1][35][0-9]{9}$)/.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  mobilephoneText:'请输入正确的手机号码',
    //EXT
    onlyEXT:function(val){
            var pattern = /[^EXT]/g;
            return !pattern.test(val);
    },
    onlyEXTText:'exttttttliu',
    onlyEXTMask:/[EXT]/,
     //Date
     mydate:function(val) {
            var dt = Date.parseDate(val,'Y:m:d',true);
            return dt ? val : false;
     },
    mydateText:'rturtutrrtrrerrr',
    mydateMask:/[0-9:]/,

  alpha:function(val,field)
  {
        try
        {
            if( /^[a-zA-Z]+$/.test(val))
                return true;
            return false;
        }
        catch(e)
        {
            return false;
        }
  },
  alphaText:'请输入英文字母'
});