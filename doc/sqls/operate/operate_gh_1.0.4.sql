/*==============================================================*/
/* Table: T_OP_ADMIN                                            */
/*==============================================================*/
create table T_OP_ADMIN
(
   oid                  varchar(32) not null,
   systemOid            varchar(32),
   sn                   varchar(32),
   account              varchar(32),
   password             varchar(255),
   phone                varchar(32),
   email                varchar(64),
   name                 varchar(32),
   resources            varchar(32) comment 'USERREGIST: 用户注册 PLATFORM: 平台注册',
   status               varchar(32) comment 'VALID: 正常 INVALID: 无效 FREEZE: 冻结 EXPIRED: 已过期',
   comment              varchar(255),
   loginIp              varchar(32),
   loginTime            datetime,
   operator             varchar(32),
   validTime            datetime,
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

insert  into `T_OP_ADMIN`(`oid`,`systemOid`,`sn`,`account`,`password`,`phone`,`email`,`name`,`resources`,`status`,`comment`,`loginIp`,`loginTime`,`operator`,`validTime`,`updateTime`,`createTime`) values ('cf0367cf644011e6b6b400163e0021b3','GhTulip','202016081600000001','ghdev','Fymk2xeuO8+U6F75O8mS9ZxnWUq/tOpa','18257894561','114@qq.com','国槐开发者国槐开发者','SYSTEM','VALID',NULL,'127.0.0.1:44659','2016-12-29 11:57:49','002799cb39d911e6860800163e0021b3',NULL,'2016-11-25 09:29:28','2016-11-25 09:29:31');
insert  into `T_OP_ADMIN`(`oid`,`systemOid`,`sn`,`account`,`password`,`phone`,`email`,`name`,`resources`,`status`,`comment`,`loginIp`,`loginTime`,`operator`,`validTime`,`updateTime`,`createTime`) values ('002799cb39d911e6860800163e0021b3','MIMOSA','202016062400000001','ghdev','AWti7yPAVikzK879kLxBBqqrRRZStG8W','18257894561','114@qq.com','国槐开发者','SYSTEM','VALID','','127.0.0.1:40493','2016-12-27 19:36:58','002799cb39d911e6860800163e0021b3',NULL,'2016-11-25 09:21:47','2016-11-25 09:21:49');

/*==============================================================*/
/* Index: admin_system                                          */
/*==============================================================*/
create unique index admin_system on T_OP_ADMIN
(
   account,
   systemOid
);

/*==============================================================*/
/* Table: T_OP_ADMIN_LOG                                        */
/*==============================================================*/
create table T_OP_ADMIN_LOG
(
   oid                  varchar(32) not null,
   adminOid             varchar(32),
   type                 varchar(32) comment 'REGIST: 注册 FREEZE: 冻结 UNFREEZE: 解冻 LOGIN: 登录 LOGOUT: 登出 RESETPWD: 重置密码',
   operator             varchar(32),
   operateIp            varchar(32),
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OP_ADMIN_ROLE                                       */
/*==============================================================*/
create table T_OP_ADMIN_ROLE
(
   oid                  varchar(32) not null,
   adminOid             varchar(32),
   roleOid              varchar(32),
   operator             varchar(32),
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

insert  into `T_OP_ADMIN_ROLE`(`oid`,`adminOid`,`roleOid`,`operator`,`updateTime`,`createTime`) values ('4ae5fe8a644111e6b6b400163e0021b3','cf0367cf644011e6b6b400163e0021b3','1614df24644111e6b6b400163e0021b3','002799cb39d911e6860800163e0021b3','2016-11-25 09:31:15','2016-11-25 09:31:18');
insert  into `T_OP_ADMIN_ROLE`(`oid`,`adminOid`,`roleOid`,`operator`,`updateTime`,`createTime`) values ('8a21d312558700d501558b855a530002','002799cb39d911e6860800163e0021b3','8a21d312558178a4015586240c8c0048','002799cb39d911e6860800163e0021b3','2016-11-25 09:24:13','2016-11-25 09:24:26');

/*==============================================================*/
/* Table: T_OP_ROLE                                             */
/*==============================================================*/
create table T_OP_ROLE
(
   oid                  varchar(32) not null,
   systemOid            varchar(32),
   name                 varchar(32),
   operator             varchar(32),
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

insert  into `T_OP_ROLE`(`oid`,`systemOid`,`name`,`operator`,`updateTime`,`createTime`) values ('1614df24644111e6b6b400163e0021b3','GhTulip','系统管理员 ','cf0367cf644011e6b6b400163e0021b3','2016-08-17 14:09:13','2016-08-17 14:09:13');
insert  into `T_OP_ROLE`(`oid`,`systemOid`,`name`,`operator`,`updateTime`,`createTime`) values ('8a21d312558178a4015586240c8c0048','MIMOSA','admin','002799cb39d911e6860800163e0021b3','2016-06-25 13:59:17','2016-06-25 13:59:17');

/*==============================================================*/
/* Table: T_OP_SYSTEM                                           */
/*==============================================================*/
create table T_OP_SYSTEM
(
   oid                  varchar(32) not null,
   name                 varchar(32),
   primary key (oid)
);

insert  into `T_OP_SYSTEM`(`oid`,`name`) values ('GhTulip','运营系统');
insert  into `T_OP_SYSTEM`(`oid`,`name`) values ('MIMOSA','全资管系统管理');

/*==============================================================*/
/* Table: T_OP_SYSTEM_MENU                                      */
/*==============================================================*/
create table T_OP_SYSTEM_MENU
(
   oid                  varchar(32) not null,
   config               text,
   system               varchar(32),
   operator             varchar(32),
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

insert  into `T_OP_SYSTEM_MENU`(`oid`,`config`,`system`,`operator`,`updateTime`,`createTime`) values ('MENU_GhTulip','[{"parent":"","buttons":[],"children":[{"parent":"nKKaixTZ82JYJ3fm","buttons":[],"children":[{"parent":"xyCYDCyarWDhjKFD","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-circle-o","id":"SFW3Tsc8NbA8XeMJ","text":"活动管理","pageId":"event"},{"parent":"xyCYDCyarWDhjKFD","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-circle-o","id":"MYzWJZSXctKPef3P","text":"投资用户管理","pageId":"userInvest"}],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-tachometer","id":"xyCYDCyarWDhjKFD","text":"活动管理","pageId":""},{"parent":"nKKaixTZ82JYJ3fm","buttons":[],"children":[{"parent":"pMQFFAX8fynb4eKR","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-circle-o","id":"Sc6cxZ7pZp5iWDmB","text":"红包","pageId":"coupon1"},{"parent":"pMQFFAX8fynb4eKR","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-circle-o","id":"F5W8ySPZdrzkGtd8","text":"代金券","pageId":"coupon2"},{"parent":"pMQFFAX8fynb4eKR","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-circle-o","id":"aprGeprZsMshcZpE","text":"加息券","pageId":"coupon3"},{"parent":"pMQFFAX8fynb4eKR","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"","id":"sn8JQG7jFHsFxQDN","text":"体验金","pageId":"coupon4"},{"parent":"pMQFFAX8fynb4eKR","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"","id":"ECbFTfxSbwf8hryf","text":"卡券明细","pageId":"couponDetail"}],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-credit-card","id":"pMQFFAX8fynb4eKR","text":"卡券管理","pageId":""},{"parent":"nKKaixTZ82JYJ3fm","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-line-chart","id":"CptekbbNHCfsDbPG","text":"数据分析","pageId":"data-analyze"}],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"","id":"nKKaixTZ82JYJ3fm","text":"运营管理","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"nKKaixTZ82JYJRfm","buttons":[],"children":[{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"id":"BnjPtJR2XAj63pMi","text":"菜单管理","pageId":"menu"},{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"id":"cjQd56mXnWAmzyeh","text":"角色管理","pageId":"role"},{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["1614df24644111e6b6b400163e0021b3"],"id":"bJ6zjZxBWzSPhDkS","text":"用户管理","pageId":"user"}],"roles":["1614df24644111e6b6b400163e0021b3"],"icon":"fa-lock","id":"4z7pxRnB3nKGsX5p","text":"权限管理","pageId":""}],"roles":["1614df24644111e6b6b400163e0021b3"],"id":"nKKaixTZ82JYJRfm","text":"系统设置","pageId":""}]','GhTulip','01bb31a2c59811e5a0bb00163e0021b3','2016-08-16 19:15:58','2016-08-16 19:16:00');
insert  into `T_OP_SYSTEM_MENU`(`oid`,`config`,`system`,`operator`,`updateTime`,`createTime`) values ('MENU_MIMOSA','[{"parent":"","buttons":[],"children":[{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"fS8T7YRTafZ7ZB8e","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"6eWDrZAjQ5T5KGEd","text":"平台首页","pageId":"platformIndex"},{"parent":"fS8T7YRTafZ7ZB8e","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"rbMda5Fz4AYiXaAY","text":"发行人首页","pageId":"publisherIndex"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-home","id":"fS8T7YRTafZ7ZB8e","text":"首页","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"NQBrXW8SRWQejyhN","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"addChannel","tableId":"","className":"","id":"JaZ2ib7mxhJseR2G","text":"新建渠道","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"channelTable","className":"apply","id":"aZKMWkHDtzT5KR63","text":"申请启用/停用","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"we5Y2KfA6ynxthN8","text":"全部渠道","pageId":"channel"},{"parent":"NQBrXW8SRWQejyhN","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"waitTable","className":"item-confirm","id":"BHhtmY6BNa7MfbbS","text":"通过/驳回","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"3fADJBAZZNQPkc4W","text":"渠道审批","pageId":"channelapply"},{"parent":"NQBrXW8SRWQejyhN","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productTable","className":"item-input","id":"zWpTZ535JXTC3PaJ","text":"产品:录入明细","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"GBDXTsZ5wyNk26FM","text":"渠道详情","pageId":"channeldetail"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-truck","id":"NQBrXW8SRWQejyhN","text":"渠道管理","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"WS4sYGJSEEHXhbKR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"n7mF3xwXEJ3xiEMY","text":"发行人资金管理","pageId":"publisherGacha"},{"parent":"WS4sYGJSEEHXhbKR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"sZBBjCSPFKDXnkDc","text":"发行人-平台轧差","pageId":"platform-publisherGacha"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-money","id":"WS4sYGJSEEHXhbKR","text":"资金管理","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"FJthK8mc4CbTrZmJ","text":"发行人账户管理","pageId":"c_account"},{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"jyYzchCBwjyGddmBHX","text":"投资人账户管理","pageId":"u_account"},{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"jyYzchCBwjyHYTBHX","text":"账户账务接口日志管理","pageId":"journal"},{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"jyYzchCBwjysmBHX","text":"结算系统接口日志管理","pageId":"settlementLogs"},{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"4heJePZFyep45aw6","text":"发行人账户管理详情","pageId":"c_accountdetail"},{"parent":"bDxymrWhNmM73KQa","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"8aXawSnawRfkAw24","text":"投资人账户管理详情","pageId":"u_accountdetail"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-suitcase","id":"bDxymrWhNmM73KQa","text":"账户管理","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"bDxymrWhNmM722","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"4hegafdddddaw6","text":"平台账户总额对账","pageId":"checkUserAmt"},{"parent":"bDxymrWhNmM722","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"4hegfaf6","text":"账户资金变动明细对账","pageId":"checkUserMoneyDetail"},{"parent":"bDxymrWhNmM722","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"4heJePZ3345aw6","text":"业务-结算对账","pageId":"checkOrder"},{"parent":"bDxymrWhNmM722","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"8aXawSnawR4424","text":"业务-结算补账","pageId":"modifyOrder"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-sliders","id":"bDxymrWhNmM722","text":"对账管理","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtr55pMibz","text":"未绑卡查询","pageId":"nocard"},{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnj777uuMibz","text":"首次绑卡未成功列表","pageId":"failcard"},{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnj7ju8uMibz","text":"未充值列表","pageId":"norecharge"},{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnhy66ibz","text":"首次充值未成功列表","pageId":"failrecharge"},{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnaaararrarar","text":"未购买列表","pageId":"nobuy"},{"parent":"4z7exRnB3dsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnaaarara3rar","text":"用户来源分布统计","pageId":"distribution"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-bullseye","id":"4z7exRnB3dsX5pdzgl","text":"运营查询","pageId":""},{"parent":"HYdiHAbRphknesNY","buttons":[],"children":[{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"NMsMdk5DFFQaQFxkX","text":"渠道管理","pageId":"cmschannel"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"22sMdk5DFFQaQFxkX","text":"元素管理","pageId":"elementManage"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"NMsMdkGHRTQaQFxkX","text":"协议管理","pageId":"protocol"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"NMsMdk5DTrRTYUFkX","text":"Banner管理","pageId":"banner"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"kw5tMxDY5eJ5StzP","text":"公告管理","pageId":"notice"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"xWsmDB8XyCDRJSft","text":"活动管理","pageId":"activity"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"FWk8DfRFha8YYw6h","text":"资讯管理","pageId":"information"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"ZTEkHCJAHiZG4ajE","text":"意见反馈","pageId":"advice"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"ByK6cNwb8zQTXsKS","text":"推送管理","pageId":"push"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"JMkd7n3wJKBTZxbc","text":"版本控制","pageId":"version"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"zQQMJrZfdaKzCEAz","text":"活动规则管理","pageId":"actrule"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"zQQgaaKzCEAz","text":"站内信管理","pageId":"mail"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"zQQgafafaAz","text":"银行卡管理","pageId":"bankCard"},{"parent":"WKnn284dasZC5ewn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"zQQgagggfafaAz","text":"合作伙伴管理","pageId":"partner"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-sticky-note","id":"WKnn284dasZC5ewn","text":"内容管理","pageId":""}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"HYdiHAbRphknesNY","text":"理财销售系统","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"CDkypGmTm7rWwwcx","buttons":[],"children":[{"parent":"6nWZNkQFyXyAw7s3","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"productAdd","tableId":"","className":"","id":"nbYzaEBKJpR6P8zM","text":"新建产品","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productDesignTable","className":"item-update","id":"3PNwZFNhFbJ7tZpX","text":"编辑产品","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productDesignTable","className":"item-invalid","id":"XyWf2eH3Tb38E5GR","text":"产品作废","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productDesignTable","className":"item-reward","id":"KJmfkZ4TmJ73R3dX","text":"奖励收益设置","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"productAudit","tableId":"","className":"","id":"eK7SQdhTYe34Pkjz","text":"提交审核","position":"normal"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"PTehSf6nSWsHEjPa","text":"产品申请管理","pageId":"productDesign"},{"parent":"6nWZNkQFyXyAw7s3","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productAuditTable","className":"item-approve","id":"8p4CH7cQax73NKJP","text":"批准","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productAuditTable","className":"item-reject","id":"BxY3bibbCZKCApEb","text":"驳回","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"PrBXBk3CysPK8Ce2","text":"产品审核管理","pageId":"productAudit"},{"parent":"6nWZNkQFyXyAw7s3","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productReviewTable","className":"item-approve","id":"Skk86tyZ2tKCnYx8","text":"批准","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productReviewTable","className":"item-reject","id":"3mt8scyBpMzbS7r7","text":"驳回","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"rMfezsN7SacMPt25","text":"产品复核管理","pageId":"productReview"},{"parent":"6nWZNkQFyXyAw7s3","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"WNypkjRGASd8R5A8","text":"产品运营管理","pageId":"productDuration"},{"parent":"6nWZNkQFyXyAw7s3","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"channelApply","tableId":"","className":"","id":"y4BPce6iW6fHaWj2","text":"发行渠道申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"openPurchase","tableId":"","className":"","id":"wwDedF2bpTPhYtiF","text":"开启申购申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"closePurchase","tableId":"","className":"","id":"SicXtH47JN2TXC4E","text":"关闭申购申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"openRemeed","tableId":"","className":"","id":"dizQmTKPGYST6563","text":"开启赎回申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"closeRemeed","tableId":"","className":"","id":"wi8KMJrsZGmMzibF","text":"关闭赎回申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"availbleSaleVolume","tableId":"","className":"","id":"7BG6TPBftCDKJjrD","text":"可售份额申请","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"tradingRuleSet","tableId":"","className":"","id":"J3P6W5MsjKFJ2eXd","text":"交易规则设置","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"manageTradeorder","tableId":"","className":"","id":"PYakjDFtmn3PHdc3","text":"赎回单处理","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"clearingProduct","tableId":"","className":"","id":"Qrr55twSWjz7sSQj","text":"触发清盘","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"channelTable","className":"item-cancel","id":"cnXTaeZjRRhYf3RY","text":"发行渠道申请撤销","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"channelTable","className":"item-pass","id":"KiiZjrJZGcy46EcN","text":"发行渠道申请通过","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"channelTable","className":"item-fail","id":"BiineAnrdzwkJJQC","text":"发行渠道申请驳回","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"channelTable","className":"item-delete","id":"zSxHKd4Z4JFdbQTf","text":"发行渠道申请删除","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productPurchaseRemeedTable","className":"item-cancel","id":"chJ6Z6ZAG26fEYZa","text":"运营申请撤销","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productPurchaseRemeedTable","className":"item-pass","id":"2j3aeX7nJmQm6E2S","text":"运营申请通过","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productPurchaseRemeedTable","className":"item-fail","id":"Riw5HSA8KMKZ8MFH","text":"运营申请驳回","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"productPurchaseRemeedTable","className":"item-delete","id":"pbecM8DidnXp7N3G","text":"运营申请删除","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"availbleSaleVolumeTable","className":"item-cancel","id":"jQitxYRGDyfGZPBB","text":"可售份额申请撤销","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"availbleSaleVolumeTable","className":"item-pass","id":"JfJx7PiBc3kywA28","text":"可售份额申请通过","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"availbleSaleVolumeTable","className":"item-fail","id":"STNt6Rib8tHsdTRt","text":"可售份额申请驳回","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"availbleSaleVolumeTable","className":"item-delete","id":"FWwkGNYxxEB8PhjJ","text":"可售份额申请删除","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"b4TYXE4kmHdNj5Pb","text":"产品运营工作台","pageId":"productDetail"},{"parent":"6nWZNkQFyXyAw7s3","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-sticky-note","id":"NPSMDaWyGGkBwwAd","text":"产品标签管理","pageId":"labelManagement"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-cubes","id":"6nWZNkQFyXyAw7s3","text":"产品管理","pageId":""},{"parent":"CDkypGmTm7rWwwcx","buttons":[],"children":[{"parent":"xKZH4wnbD3QCC6Jn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"Cmz7k6A26aWZy6TH","text":"持有人名册管理","pageId":"investor"},{"parent":"xKZH4wnbD3QCC6Jn","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"6wWtSDmcSBkTwfBr","text":"未确认持有人订单管理","pageId":"inputOrder"},{"parent":"xKZH4wnbD3QCC6Jn","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"orderAdd","tableId":"","className":"","id":"Dc55xM3hk5dZQdYW","text":"新建订单","position":"normal"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"spvOrderTable","className":"item-invalid","id":"Q5CRAG3py8FYB2cy","text":"作废","position":"table"},{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"spvOrderTable","className":"item-audit","id":"CkipdSCwj6XkPGaX","text":"审核确定","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"3kjFaezJKAfwAfwB","text":"发行人交易管理","pageId":"spvTrade"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-tachometer","id":"xKZH4wnbD3QCC6Jn","text":"运营管理","pageId":""},{"parent":"CDkypGmTm7rWwwcx","buttons":[],"children":[{"parent":"xNPWQ5fQpterihQ4","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"hyBHkwemBfrtfpPJ","text":"凭证查询","pageId":"document"},{"parent":"xNPWQ5fQpterihQ4","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"6A6ErRddiG2WmQpn","text":"资产负债情况试算表","pageId":"bookBalance"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-book","id":"xNPWQ5fQpterihQ4","text":"会计分录","pageId":""}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"CDkypGmTm7rWwwcx","text":"理财发行登记系统","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"xGnxwxnytZ6w24kQ","buttons":[],"children":[{"parent":"4z7pxRnB3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtJR2XAdj63pMibz","text":"非现金类标的申请管理","pageId":"pactApply"},{"parent":"4z7pxRnB3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtJR2dXAdj63pMibz","text":"非现金类标的审核管理","pageId":"pactAccess"},{"parent":"4z7pxRnB3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtJR2ddXAdj63pMibz","text":"非现金类投资标的备选库","pageId":"pactStorage"},{"parent":"4z7pxRnB3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtdddddj63pMibz","text":"非现金类持仓标的管理","pageId":"pactHold"},{"parent":"4z7pxRnB3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtdgg3pMibz","text":"非现金类未持仓标的管理","pageId":"pactNotHold"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-bicycle","id":"4z7pxRnB3ndKGsX5pdzgl","text":"非现金类标的管理","pageId":""},{"parent":"xGnxwxnytZ6w24kQ","buttons":[],"children":[{"parent":"4z7pxRgg3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"Bnjeedgg3pMibz","text":"现金类标的申请","pageId":"liquidAssetApply"},{"parent":"4z7pxRgg3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjeedrrMibz","text":"现金类标的审核","pageId":"liquidAssetAccess"},{"parent":"4z7pxRgg3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnwwdrrMibz","text":"现金类标的备选库","pageId":"liquidAssetStorage"},{"parent":"4z7pxRgg3ndKGsX5pdzgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnwwddddrrMibz","text":"历史现金类标的","pageId":"liquidAssetHistory"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-fax","id":"4z7pxRgg3ndKGsX5pdzgl","text":"现金类标的管理","pageId":""},{"parent":"xGnxwxnytZ6w24kQ","buttons":[],"children":[{"parent":"4z7pxRgg3ndKGsX5pdzddgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjeedrddddrMibz","text":"投资组合管理","pageId":"portfolioApply"},{"parent":"4z7pxRgg3ndKGsX5pdzddgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjddeettdrMibz","text":"投资组合审核","pageId":"portfolioAcess"},{"parent":"4z7pxRgg3ndKGsX5pdzddgl","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjeettdrMibz","text":"投资组合运营管理","pageId":"portfolioManagement"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-legal","id":"4z7pxRgg3ndKGsX5pdzddgl","text":"投资组合管理","pageId":""}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"xGnxwxnytZ6w24kQ","text":"资管系统","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"nZtBzFnmN2sfkEtG","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-exchange","id":"nHsYc3P4kim2Trbf","text":"订单管理","pageId":"receiveOrderSettlement"},{"parent":"nZtBzFnmN2sfkEtG","buttons":[],"children":[{"parent":"DQh7ERsAMAH2dQGR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"SabPKcjTd5tdDtke","text":"指令管理","pageId":"paymentSettlement"},{"parent":"DQh7ERsAMAH2dQGR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"cRhcsdZzWz2AhrP8","text":"指令审核管理","pageId":"paymentAuditSettlement"},{"parent":"DQh7ERsAMAH2dQGR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"SabPga5tdDtke","text":"提现申请管理","pageId":"largeAmountPaymentSettle"},{"parent":"DQh7ERsAMAH2dQGR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"SabPgajjDtke","text":"提现审核管理","pageId":"paymentAuditSettlementBatch"},{"parent":"DQh7ERsAMAH2dQGR","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"SabPgaggjjDtke","text":"提现审核结果查询","pageId":"paymentAuditResult"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-expeditedssl","id":"DQh7ERsAMAH2dQGR","text":"指令管理","pageId":""},{"parent":"nZtBzFnmN2sfkEtG","buttons":[],"children":[{"parent":"22E5PQN4tseTJGak","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"pym4DzNAQEQAC77E","text":"结算通道","pageId":"comChannel"},{"parent":"22E5PQN4tseTJGak","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"M6FK3AmxS6Ta3Grr","text":"结算通道银行管理","pageId":"comChannelBank"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-odnoklassniki-square","id":"22E5PQN4tseTJGak","text":"结算设置","pageId":""},{"parent":"nZtBzFnmN2sfkEtG","buttons":[],"children":[{"parent":"enwXr858YSznPw2Y","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"seEKX5WC2wmy2QNj","text":"异常单据查询","pageId":"exceptionOrderCheck"},{"parent":"enwXr858YSznPw2Y","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"234eEKX5Bydp4uuq","text":"宝付对账","pageId":"baofooReconciliation"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-balance-scale","id":"enwXr858YSznPw2Y","text":"对账管理","pageId":""},{"parent":"nZtBzFnmN2sfkEtG","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-backward","id":"xWBBEY4dSwBXTNWW","text":"银行回调管理","pageId":"bankCallback"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"nZtBzFnmN2sfkEtG","text":"结算平台系统","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"GBEtdtfpckZXmbtf","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-users","id":"3iDMppC753d4Pzk4","text":"账户列表","pageId":"financeAccount"},{"parent":"GBEtdtfpckZXmbtf","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-user-plus","id":"Gb2xQ328MaJxxnFT","text":"系统用户查询","pageId":"financeUser"},{"parent":"GBEtdtfpckZXmbtf","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-sort-alpha-desc","id":"h8HfdCtKb7st5YBr","text":"账户流水","pageId":"accountTrans"},{"parent":"GBEtdtfpckZXmbtf","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-street-view","id":"xWBBEY4dSwddNWW","text":"单据入账查询","pageId":"accountOrder"},{"parent":"GBEtdtfpckZXmbtf","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-credit-card","id":"EEBBEY4dSwBXTNWW","text":"绑卡查询","pageId":"accountSign"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"GBEtdtfpckZXmbtf","text":"账户账务系统","pageId":""},{"parent":"","buttons":[],"children":[{"parent":"nd5EkiYiyxCFerMA","buttons":[],"children":[{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"BnjPtJR2XAj63pMi","text":"菜单管理","pageId":"menu"},{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"cjQd56mXnWAmzyeh","text":"角色管理","pageId":"role"},{"parent":"4z7pxRnB3nKGsX5p","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"bJ6zjZxBWzSPhDkS","text":"用户管理","pageId":"user"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-lock","id":"4z7pxRnB3nKGsX5p","text":"权限管理","pageId":""},{"parent":"nd5EkiYiyxCFerMA","buttons":[],"children":"","roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-gear","id":"4z7pxRnB3nKGs2p","text":"系统配置管理","pageId":"switchcraft"},{"parent":"nd5EkiYiyxCFerMA","buttons":[],"children":"","roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-gear","id":"4z7pxddRnB3nKGs2p","text":"定时任务管理","pageId":"jobLock"},{"parent":"nd5EkiYiyxCFerMA","buttons":[],"children":[{"parent":"rmcrB24deEySC3wP","buttons":[{"enable":"YES","roles":["8a21d312558178a4015586240c8c0048"],"buttonId":"","tableId":"dataTable","className":"item-update","id":"7XtPaXxyCJ6hBawH","text":"修改","position":"table"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"TKP6fN6A4WP7WsAy","text":"科目设置","pageId":"acctAccount"},{"parent":"rmcrB24deEySC3wP","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"SSw4wzewStE8T4By","text":"凭证模板","pageId":"docTemplate"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-calculator","id":"rmcrB24deEySC3wP","text":"会计分录设置","pageId":""},{"parent":"nd5EkiYiyxCFerMA","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-calendar-plus-o","id":"Qw2CCPJiGjQQyDye","text":"交易日历","pageId":"tradeCalendar"},{"parent":"nd5EkiYiyxCFerMA","buttons":[],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"fa-battery-3","id":"NPSMDaWyWWkBwwAd","text":"体验金日志","pageId":"couponLog"}],"roles":["8a21d312558178a4015586240c8c0048"],"icon":"","id":"nd5EkiYiyxCFerMA","text":"后台管理系统","pageId":""}]','MIMOSA','002799cb39d911e6860800163e0021b3','2016-12-16 09:51:02','2016-09-22 19:22:35');

alter table T_OP_ADMIN add constraint FK_Reference_2 foreign key (systemOid)
      references T_OP_SYSTEM (oid) on delete restrict on update restrict;

alter table T_OP_ADMIN_LOG add constraint FK_Reference_3 foreign key (adminOid)
      references T_OP_ADMIN (oid) on delete restrict on update restrict;

alter table T_OP_ADMIN_ROLE add constraint FK_Reference_4 foreign key (roleOid)
      references T_OP_ROLE (oid) on delete restrict on update restrict;

alter table T_OP_ADMIN_ROLE add constraint FK_Reference_5 foreign key (adminOid)
      references T_OP_ADMIN (oid) on delete restrict on update restrict;

alter table T_OP_ROLE add constraint FK_Reference_1 foreign key (systemOid)
      references T_OP_SYSTEM (oid) on delete restrict on update restrict;
