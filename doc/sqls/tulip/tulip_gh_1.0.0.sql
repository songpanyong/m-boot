/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/6/28 13:50:57                           */
/*==============================================================*/


drop table if exists T_TULIP_ACCOUNT;

drop table if exists T_TULIP_ACCOUNT_ORDER;

drop table if exists T_TULIP_ACCOUNT_TRANS;

drop table if exists T_TULIP_COMMISSION_ORDER;

drop table if exists T_TULIP_COUPON;

drop table if exists T_TULIP_COUPON_ORDER;

drop table if exists T_TULIP_COUPON_RANGE;

drop table if exists T_TULIP_COUPON_RULE;

drop table if exists T_TULIP_DELIVERY;

drop table if exists T_TULIP_EVENT;

drop table if exists T_TULIP_EVENT_RULE;

drop table if exists T_TULIP_EXCHANGED_BILL;

drop table if exists T_TULIP_FILE;

drop table if exists T_TULIP_GATEWAY_REQUEST_LOG;

drop table if exists T_TULIP_GOODS;

drop table if exists T_TULIP_INTERVAL_SETTING;

drop table if exists T_TULIP_JOB_CONTROLLER;

drop table if exists T_TULIP_PURCHASE_BILL;

drop table if exists T_TULIP_RULE;

drop table if exists T_TULIP_RULE_ITEM;

drop table if exists T_TULIP_RULE_PROP;

drop table if exists T_TULIP_SCENE_PROP;

drop table if exists T_TULIP_SETTING;

drop table if exists T_TULIP_SIGN_IN;

drop table if exists T_TULIP_STATISTICAL;

drop table if exists T_TULIP_TAKE_ADDRESS;

drop table if exists T_TULIP_USER_COUPON;

drop table if exists T_TULIP_USER_INVEST_LOG;

drop table if exists t_tulip_platform_banner;

/*==============================================================*/
/* Table: T_TULIP_ACCOUNT                                       */
/*==============================================================*/
create table T_TULIP_ACCOUNT
(
   oid                  varchar(32) not null,
   accountNo            varchar(32) comment '账户号',
   userOid              varchar(32) comment '用户ID',
   accountType          varchar(4) comment '账户类型 01：积分基本户、02：签到积分户、03：卡券积分户、04：充值积分户',
   accountName          varchar(100) comment '账户名称',
   relationTicketCode   varchar(32) comment '卡券ID',
   relationTicketName   varchar(50) comment '卡券名称',
   balance              decimal(14,2) comment '账户余额',
   openOperator         varchar(32) comment '开户人',
   frozenStatus         varchar(32) comment '是否冻结 N为正常，Y为冻结',
   remark               varchar(500) comment '备注',
   overdueTime          datetime comment '过期时间',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_ACCOUNT comment '运营推广账户';

/*==============================================================*/
/* Table: T_TULIP_ACCOUNT_ORDER                                 */
/*==============================================================*/
create table T_TULIP_ACCOUNT_ORDER
(
   oid                  varchar(32) not null,
   requestNo            varchar(32) comment '请求流水号',
   systemSource         varchar(32) comment '来源系统类型',
   orderNo              varchar(32) comment '请求单据号',
   userOid              varchar(32) comment '用户ID',
   orderType            varchar(32) comment '单据类型 01：签到，02：卡券，03：充值，04：消费',
   relationProductCode  varchar(32) comment '关联产品编号',
   relationProductName  varchar(32) comment '关联产品名称',
   point                decimal(14,2) comment '积分额',
   orderStatus          varchar(32) comment '订单状态',
   orderDesc            varchar(100) comment '订单描述',
   errorMessage         text comment '错误信息',
   remark               varchar(100) comment '备注',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_ACCOUNT_ORDER comment '账户订单';

/*==============================================================*/
/* Table: T_TULIP_ACCOUNT_TRANS                                 */
/*==============================================================*/
create table T_TULIP_ACCOUNT_TRANS
(
   oid                  varchar(32) not null,
   requestNo            varchar(32) comment '请求流水号',
   systemSource         varchar(32) comment '系统来源类型',
   orderNo              varchar(32) comment '请求订单号',
   userOid              varchar(32) comment '用户ID',
   orderType            varchar(10) comment '订单类型',
   relationProductCode  varchar(32) comment '关联产品ID',
   relationProductName  varchar(100) comment '关联产品名称',
   accountType          varchar(10) comment '账户类型',
   accountName          varchar(100) comment '账户名称',
   orderDesc            varchar(500) comment '订单描述',
   direction            varchar(10) comment '积分方向',
   orderPoint           decimal(14,2) comment '订单积分额',
   point                decimal(14,2) comment '交易后余额',
   financeMark          varchar(10) comment '财务入账标识',
   remark               varchar(500) comment '备注',
   isDelete             varchar(2) comment '删除标记',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_ACCOUNT_TRANS comment '账户积分流水表';

/*==============================================================*/
/* Table: T_TULIP_COMMISSION_ORDER                              */
/*==============================================================*/
create table T_TULIP_COMMISSION_ORDER
(
   oid                  varchar(32) not null,
   userId               varchar(32) not null comment '用户ID',
   couponOid            varchar(32) comment '卡券Oid',
   type                 varchar(32) comment '渠道类型，推广人:referee,渠道:channel',
   orderAmount          varchar(32) comment '返佣金额',
   orderStatus          varchar(32) comment '订单状态，通过:pass,驳回:refused,待审批:pending',
   orderCode            varchar(32) comment '订单号',
   createTime           datetime comment '创建时间',
   friendPhone          varchar(11) comment '被邀请人联系方式',
   friendInvest         decimal(10,2) comment '被邀请人投资金额',
   phone                varchar(11) comment '用户联系方式',
   friendInvestTime     datetime comment '被邀请人投资时间',
   auditor              varchar(32) comment '审核人',
   auditTime            datetime comment '审核时间',
   rejectAdvice         varchar(500) comment '驳回意见',
   primary key (oid)
);

alter table T_TULIP_COMMISSION_ORDER comment '佣金订单表';

/*==============================================================*/
/* Table: T_TULIP_COUPON                                        */
/*==============================================================*/
create table T_TULIP_COUPON
(
   oid                  varchar(32) not null comment '编号',
   name                 varchar(50) comment '卡券名称',
   description          varchar(2000) comment '红包内容描述',
   type                 varchar(20) comment 'redPackets.红包;coupon.代金券;rateCoupon,加息券;tasteCoupon.体验金;
            cashCoupon:提现券;pointsCoupon:积分券;',
   couponAmount         decimal(10,2),
   upperAmount          decimal(10,2) comment '卡券金额上限',
   lowerAmount          decimal(10,2) comment '卡券金额下限',
   scale                decimal(4,2),
   amountType           varchar(10) comment 'scale:比例;fixed:固定,random:随机',
   count                int(11) comment '券码发行量',
   isdel                varchar(10) comment '状态:有效 yes;无效 no',
   createUser           varchar(32) comment '创建用户',
   updateUser           varchar(32) comment '更新用户',
   updateTime           datetime comment '更新时间',
   payflag              varchar(10) comment '结算触发时间：purchasing.申购;redeming.赎回;cash.提现',
   overlap              varchar(10) comment '是否可叠加使用yes,no',
   createTime           datetime comment '创建时间',
   disableDate          int(11) comment '领用多少天后失效',
   disableType          varchar(32) comment 'DAY:天，到期时间start+disableDate为yyyy-MM-dd 23:59:59
            HOUR:时，到期时间start+disableDate为 yyyy-MM-dd HH:mm:ss',
   disableTime          date,
   maxRateAmount        decimal(10,2) comment '最高加息金额',
   validPeriod          int(11) comment '优惠天数',
   remainCount          int(11) default 0 comment '剩余数量',
   investAmount         decimal(10,2) comment '投资满额条件',
   investTime           int(11) comment '投资期限条件',
   products             text comment '产品范围描述',
   rules                text comment '使用规则描述',
   totalAmount          decimal(20,2) comment '红包总金额',
   remainAmount         decimal(20,2) comment '剩余红包总金额',
   useCount             int(11) default 0 comment '使用数量',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='券码基本信息表';

alter table T_TULIP_COUPON comment '卡券基本信息表';

/*==============================================================*/
/* Table: T_TULIP_COUPON_ORDER                                  */
/*==============================================================*/
create table T_TULIP_COUPON_ORDER
(
   oid                  varchar(32) not null comment '编号',
   couponId             varchar(32),
   productId            varchar(32) comment '产品编号',
   productName          varchar(100) comment '产品名称',
   userId               varchar(32) comment '用户编号',
   orderType            varchar(32) comment '订单类型 1.申购;2.赎回;3.退货;4.体现',
   dueTime              int(12) comment '期限',
   discount             decimal(10,2) comment '卡券抵扣金额',
   userAmount           decimal(10,2) comment '用户支付金额',
   orderAmount          decimal(10,2) comment '订单总金额',
   rateinvestment       decimal(10,2) comment '产品收益',
   createTime           timestamp comment '订单生成时间',
   orderCode            varchar(32) comment '订单编号',
   orderStatus          varchar(10) comment '订单状态:success,fail',
   isMakedCommisOrder   varchar(10) default '0' comment '是否已经生成佣金订单no:未；yes:已',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='券码结算订单表';

alter table T_TULIP_COUPON_ORDER comment '卡券结算订单表';

/*==============================================================*/
/* Table: T_TULIP_COUPON_RANGE                                  */
/*==============================================================*/
create table T_TULIP_COUPON_RANGE
(
   oid                  varchar(32) not null comment '编号',
   couponBatch          varchar(32) comment '批次号',
   labelCode            varchar(200) comment '产品编码',
   labelName            varchar(200) comment '产品名称',
   primary key (oid),
   key FK_Reference_13 (couponBatch)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='券码使用范围';

/*==============================================================*/
/* Table: T_TULIP_COUPON_RULE                                   */
/*==============================================================*/
create table T_TULIP_COUPON_RULE
(
   oid                  varchar(32) not null,
   ruleId               varchar(32) comment '规则属性编号',
   couponId             varchar(32) comment '卡券ID',
   createTime           datetime comment '创建时间',
   primary key (oid),
   key FK_Reference_14 (couponId),
   key FK_Reference_9 (ruleId)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='券码规则关联表';

alter table T_TULIP_COUPON_RULE comment '卡券规则关联表';

/*==============================================================*/
/* Table: T_TULIP_DELIVERY                                      */
/*==============================================================*/
create table T_TULIP_DELIVERY
(
   oid                  varchar(32) not null,
   orderNumber          varchar(32) comment '订单号',
   userName             varchar(32) comment '下单用户名',
   orderAddress         varchar(256) comment '地址',
   orderedTime          datetime comment '订单时间',
   goodsName            varchar(128) comment '商品名称',
   goodsCount           decimal(14,0) comment '商品数量',
   sendTime             datetime comment '发货时间',
   sendOperater         varchar(32) comment '发货人',
   logisticsCompany     varchar(128) comment '物流公司',
   logisticsNumber      varchar(32) comment '物流号',
   state                tinyint comment '发货状态（0：未发货、1：已发货、2：已取消、3：确认收货）',
   cancelReason         varchar(256) comment '取消原因',
   cancelOperater       varchar(32) comment '取消人',
   cancelTime           datetime comment '取消时间',
   userOid              varchar(32) comment '下单用户',
   point                decimal(14,2) comment '订单积分额',
   isdel                varchar(10) comment '是否删除   yes:是,no:否',
   remark               varchar(256) comment '备注',
   userPhone            varchar(15) comment '下单用户手机号',
   orderPhone           varchar(15) comment '收获人手机号',
   orderName            varchar(50) comment '收获人姓名',
   orderZipCode         varchar(8) comment '收货人邮编',
   primary key (oid)
);

alter table T_TULIP_DELIVERY comment '发货记录';

/*==============================================================*/
/* Table: T_TULIP_EVENT                                         */
/*==============================================================*/
create table T_TULIP_EVENT
(
   oid                  varchar(32) not null,
   title                varchar(100) comment '活动标题',
   description          varchar(2000) comment '活动描述',
   start                date comment '活动开始时间',
   finish               date comment '活动结束时间',
   createUser           varchar(32) comment '创建用户',
   createTime           timestamp comment '创建时间',
   updateUser           varchar(32) comment '修改用户',
   updateTime           timestamp comment '修改时间',
   status               varchar(10) comment '状态 pending:待审批pass:通过 refused:驳回
            ',
   active               varchar(10) comment '上架状态  wait：待上架on：已上架  off：上架
            ',
   type                 varchar(50) comment '活动类型',
   isdel                varchar(10) comment '是否删除 yes  : no',
   primary key (oid)
);

alter table T_TULIP_EVENT comment '活动基础信息表';

/*==============================================================*/
/* Table: T_TULIP_EVENT_RULE                                    */
/*==============================================================*/
create table T_TULIP_EVENT_RULE
(
   oid                  varchar(32) not null,
   eventId              varchar(32),
   ruleId               varchar(32) comment '属性编号',
   createTime           timestamp comment '创建时间',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table T_TULIP_EVENT_RULE comment '活动规则表';

/*==============================================================*/
/* Table: T_TULIP_EXCHANGED_BILL                                */
/*==============================================================*/
create table T_TULIP_EXCHANGED_BILL
(
   oid                  varchar(32) not null,
   goodsOid             varchar(32) comment '积分商品ID',
   goodsName            varchar(20) comment '商品名称',
   type                 varchar(20) comment '商品类型（枚举: real实物、virtual虚拟）',
   userOid              varchar(32) comment '用户ID',
   exchangedCount       decimal(14,0) comment '兑换数量',
   expendPoints         decimal(14,0) comment '消耗的积分数',
   exchangedTime        datetime comment '兑换时间',
   state                tinyint comment '状态：0成功、1：失败',
   primary key (oid)
);

alter table T_TULIP_EXCHANGED_BILL comment '积分兑换记录';

/*==============================================================*/
/* Table: T_TULIP_FILE                                          */
/*==============================================================*/
create table T_TULIP_FILE
(
   oid                  varchar(32) not null,
   goodsOid             varchar(32) comment '商品ID',
   cate                 varchar(32) comment '枚举值:  PLATFORM: 平台文件  DOCUMENT: 资料文件  AGREEMENT: 合同文件  AGMFEEDBACK: 合同反馈  AGMPATCH: 补充合同(资管方>管理人)  INVESTCMD: 投资指令  REMITCMD: 划款指令  DECISION: 产品决议书  GMAGMFEEDBACK: 资管合同反馈  GMCMDFEEDBACK: 资管指令反馈  GMCMDRECEIPT: 资管指令回执  BANKCMDRECERPT: 托管行回执',
   fkey                 varchar(50) comment 'fkey',
   operator             varchar(64) comment '操作人',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_FILE comment '文件管理';

/*==============================================================*/
/* Table: T_TULIP_GATEWAY_REQUEST_LOG                           */
/*==============================================================*/
create table T_TULIP_GATEWAY_REQUEST_LOG
(
   oid                  varchar(32) not null comment '编号',
   appId                varchar(50) comment '请求服务名',
   method               varchar(50) comment '方法名称',
   request              text comment '请求参数',
   response             text comment '响应报文',
   status               varchar(20) comment '状态：1.成功 2.失败 3.执行中',
   responseTime         int(11) comment '响应耗时',
   ip                   varchar(50) comment 'ip',
   requestTime          datetime comment '请求时间',
   description          varchar(100) comment '动作描述',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网关请求时间';

alter table T_TULIP_GATEWAY_REQUEST_LOG comment '网关请求时间';

/*==============================================================*/
/* Table: T_TULIP_GOODS                                         */
/*==============================================================*/
create table T_TULIP_GOODS
(
   oid                  varchar(32) not null,
   name                 varchar(200) comment '商品名称',
   type                 varchar(60) comment '枚举: real实物、virtual虚拟',
   virtualCouponType    varchar(20) comment 'coupon.代金券;rateCoupon,加息券;tasteCoupon.体验金;cashCoupon:提现券',
   issueVirtualCouponId varchar(32) comment '待下发的卡券ID',
   needPoints           decimal(14,2) comment '所需积分',
   totalCount           decimal(14,0) comment '商品总数量',
   exchangedCount       decimal(14,0) comment '已兑换数量',
   remainCount          decimal(14,0) comment '剩余数量',
   state                tinyint comment '商品状态(0:未上架、1:已上架、2:已下架)',
   fileOid              varchar(32) comment '商品图片url',
   remark               varchar(500) comment '商品介绍',
   createOperater       varchar(32) comment '创建人',
   createTime           datetime comment '创建时间',
   updateOperater       varchar(32) comment '修改人',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_GOODS comment '商品表';

/*==============================================================*/
/* Table: T_TULIP_INTERVAL_SETTING                              */
/*==============================================================*/
create table T_TULIP_INTERVAL_SETTING
(
   oid                  varchar(32) not null,
   startMoney           decimal(10,2) comment '开始起投金额',
   endMoney             decimal(10,2) comment '结束投资金额',
   intervalLevel        decimal(10,2) comment '基数',
   createUser           varchar(32),
   createTime           datetime comment '创建时间',
   updateUser           varchar(32),
   updateTime           datetime comment '修改时间',
   isdel                varchar(10) comment '是否删除',
   primary key (oid)
);

alter table T_TULIP_INTERVAL_SETTING comment '基数设置表';

/*==============================================================*/
/* Table: T_TULIP_JOB_CONTROLLER                                */
/*==============================================================*/
create table T_TULIP_JOB_CONTROLLER
(
   oid                  varchar(32) not null,
   jobName              varchar(100) comment '定时任务名称',
   jobCorn              varchar(32) comment '定时表达式',
   updateBy             varchar(32) comment '更新码',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '更新时间',
   primary key (oid)
);

alter table T_TULIP_JOB_CONTROLLER comment '定时任务表';

/*==============================================================*/
/* Table: T_TULIP_PURCHASE_BILL                                 */
/*==============================================================*/
create table T_TULIP_PURCHASE_BILL
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '用户ID',
   settingOid           varchar(32) comment '积分产品ID',
   purshasePoints       decimal(14,2) comment '购买的积分',
   amount               decimal(10,4) comment '花费的金额',
   purshaseTime         datetime comment '购买时间',
   state                tinyint comment '状态：0:成功、1:失败',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_PURCHASE_BILL comment '积分购买记录';

/*==============================================================*/
/* Table: T_TULIP_RULE                                          */
/*==============================================================*/
create table T_TULIP_RULE
(
   oid                  varchar(32) not null comment '编号',
   type                 varchar(10) comment 'get.领用;use.使用;writeoff.核销;',
   weight               char(10) comment '权重类型:or,逻辑或、and,逻辑与',
   actionName           varchar(32),
   expression           text comment '表达式',
   createTime           timestamp comment '创建时间',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则信息表';

alter table T_TULIP_RULE comment '规则信息表';

/*==============================================================*/
/* Table: T_TULIP_RULE_ITEM                                     */
/*==============================================================*/
create table T_TULIP_RULE_ITEM
(
   oid                  varchar(32) not null comment '主键',
   ruleId               varchar(32) comment '规则编号',
   propId               varchar(32) comment '属性编号',
   value                varchar(20) comment '规则属性值',
   expression           varchar(20) comment '表达式',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则属性表';

alter table T_TULIP_RULE_ITEM comment '规则属性表';

/*==============================================================*/
/* Table: T_TULIP_RULE_PROP                                     */
/*==============================================================*/
create table T_TULIP_RULE_PROP
(
   oid                  varchar(32) not null comment '主键',
   name                 varchar(100) comment '属性名称',
   field                varchar(50) comment '属性描述',
   unit                 varchar(10) not null default 'number' comment '单位：per百分比，number数值，interval 数值区间，registerTime 注册时间',
   type                 varchar(10) not null default 'get' comment 'get.领用;use.使用;writeOff.核销;',
   unitvalue            varchar(10) comment '单位值：元，个，百分比等',
   isdel                varchar(10) not null default 'yes' comment '状态：yes 有效;no 无效',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则配置基础属性表';

alter table T_TULIP_RULE_PROP comment '规则配置基础属性表';

insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R001','累计推荐人数量','friends','number','get','个','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R002','累计投资额度','investAmount','double','get','元','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R003','投资额度','orderAmount','double','use','元','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R004','投资时间','investTime','number','use','天','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R005','注册时间','registerTime','number','use','天','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R006','累计投资时长','investDuration','interval','use','天','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R007','累计投资次数','investCount','number','use','次','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R008','首投金额','firstInvestAmount','interval','get','元','no');

/*==============================================================*/
/* Table: T_TULIP_SCENE_PROP                                    */
/*==============================================================*/
create table T_TULIP_SCENE_PROP
(
   oid                  varchar(32) not null comment '主键',
   name                 varchar(50) comment '场景名称',
   description          varchar(100) comment '场景描述',
   code                 varchar(20) comment '场景编码',
   isdel                varchar(10) comment '是否删除标志',
   type                 varchar(10) comment '定时任务:schedule;被动任务:passive',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table T_TULIP_SCENE_PROP comment '场景属性';

insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080001','签到','用户签到','sign','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080002','申购','申购时触发','investment','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080003','实名认证绑卡','用户实名认证绑卡','authentication','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080004','赎回','赎回时触发','redeem','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080005','用户注册','用户注册时触发','register','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080006','到期兑付','到期兑付时触发','bearer','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080007','提现','提现时触发','cash','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080008','退款','退款时触发','refund','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080009','被推荐人首次投资','被推荐人首次投资','firstFriendInvest','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080010','软文转发','软文转发','forwarded','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080011','流标','流标事件','invalidBids','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080012','推荐人活动','推荐人活动','friend','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080013','充值','充值','recharge','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080014','手机认证','手机认证','phoneAuthentication','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080015','绑卡','绑卡','bindingCard','yes','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080016','派发活动','派发活动','schedule','no','schedule');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080017','自定义领取','自定义领取','custom','no','passive');
insert into `t_tulip_scene_prop` (`oid`, `name`, `description`, `code`, `isdel`, `type`) values('402880fd56c58b820156c59765080018','用户生日','用户生日','birthday','no','schedule');

/*==============================================================*/
/* Table: T_TULIP_SETTING                                       */
/*==============================================================*/
create table T_TULIP_SETTING
(
   oid                  varchar(32) not null,
   name                 varchar(200) comment '积分产品名称',
   point                decimal(14,2) comment '积分',
   amount               decimal(14,2) comment '金额',
   totalCount           decimal(14,0) comment '总张数',
   remainCount          decimal(14,0) comment '剩余张数',
   state                tinyint comment '商品状态(-1:删除、0:未上架、1:已上架、2:已下架)',
   createTime           datetime comment '创建时间',
   createOperater       varchar(32) comment '创建用户',
   updateTime           datetime comment '修改时间',
   updateOperater       varchar(32) comment '修改用户',
   primary key (oid)
);

alter table T_TULIP_SETTING comment '购买积分设置表';

/*==============================================================*/
/* Table: T_TULIP_SIGN_IN                                       */
/*==============================================================*/
create table T_TULIP_SIGN_IN
(
   oid                  varchar(32) not null,
   userId               varchar(32),
   signInTime           timestamp,
   signDate             date not null,
   primary key (oid)
);

/*==============================================================*/
/* Index: userId_signDate                                       */
/*==============================================================*/
create unique index userId_signDate on T_TULIP_SIGN_IN
(
   signDate,
   userId
);

/*==============================================================*/
/* Table: T_TULIP_STATISTICAL                                   */
/*==============================================================*/
create table T_TULIP_STATISTICAL
(
   oid                  varchar(32) not null comment '编号',
   cid                  varchar(32) comment '奖品编号',
   provideCount         int(11) comment '发放数量',
   receiveCount         int(11) comment '领取数量',
   useCount             int(11) comment '使用数量',
   closureCount         int(11) comment '已核销数量',
   description          varchar(3000) comment '奖品描述',
   amount               decimal(10,2) comment '转发投资额',
   couponNmae           varchar(50) comment '卡券名称',
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='统计分析表';

alter table T_TULIP_STATISTICAL comment '统计分析表';

/*==============================================================*/
/* Table: T_TULIP_TAKE_ADDRESS                                  */
/*==============================================================*/
create table T_TULIP_TAKE_ADDRESS
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '用户ID',
   name                 varchar(64) comment '收获人姓名',
   takeAddress          varchar(256) comment '收货地址',
   isDefault            tinyint comment '是否为默认地址(1默认，0否)',
   phone                varchar(11) comment '手机号',
   isdel                varchar(3) comment '是否删除（yes：是  no：否）',
   zipCode              varbinary(10) comment '邮编',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '修改时间',
   primary key (oid)
);

alter table T_TULIP_TAKE_ADDRESS comment '收货人地址表';

/*==============================================================*/
/* Table: T_TULIP_USER_COUPON                                   */
/*==============================================================*/
create table T_TULIP_USER_COUPON
(
   oid                  varchar(32) not null comment '编号',
   userId               varchar(32) comment '用户编号',
   phone                varchar(11) comment '用户手机号',
   couponBatch          varchar(32),
   leadTime             datetime comment '领用时间',
   settlement           datetime comment '核销时间',
   useTime              datetime comment '使用时间',
   status               varchar(10) comment '状态:未使用 notUsed，已使用used，expired 已过期,lock 锁定中,writeOff 已核销,invalid 作废',
   name                 varchar(50) comment '卡券名称',
   description          varchar(2000) comment '卡券描述',
   start                datetime comment '生效时间',
   finish               datetime comment '失效时间',
   amount               decimal(10,2) comment '卡券金额',
   amountType           varchar(10) comment '金额类型',
   type                 varchar(20) comment 'redPackets.红包;coupon.优惠券;3.折扣券;tasteCoupon.体验金;rateCoupon,加息券',
   eventType            varchar(32) comment '事件标题',
   investAmount         decimal(10,2) default 0.00 comment '最小投资金额',
   investTime           int(11) comment '最小投资期限',
   products             text comment '产品范围',
   rules                text comment '使用规则',
   validPeriod          int(11) comment '优惠天数',
   maxRateAmount        decimal(10,2) default 0.00 comment '最大优惠金额',
   moiveTicketContent   text,
   primary key (oid)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户拥有券码列表';

alter table T_TULIP_USER_COUPON comment '用户拥有券码列表';

/*==============================================================*/
/* Table: T_TULIP_USER_INVEST_LOG                               */
/*==============================================================*/
create table T_TULIP_USER_INVEST_LOG
(
   oid                  varchar(32) not null comment '主键',
   userId               varchar(32) not null comment '用户编号',
   investAmount         decimal(10,2) not null default 0.00 comment '累计投资额度',
   friends              int(20) not null default 0 comment '推荐人总数',
   investDuration       int(20) not null default 0 comment '累计投资时长，单位：天',
   investCount          int(20) not null default 0 comment '累计投资次数',
   firstInvestTime      datetime comment '首次投资时间',
   registerTime         datetime comment '注册时间',
   firstInvestAmount    decimal(10,2) not null default 0.00 comment '首投金额',
   friendId             varchar(50) comment '推荐人ID',
   phone                varchar(50) comment '用户联系方式',
   birthday             date comment '用户生日',
   name                 varchar(32) comment '用户姓名',
   totalReward          decimal(10,2) comment '累计返佣金额',
   type                 varchar(32) comment 'channel：渠道，referee：推荐人',
   channelAmount        decimal(10,2) comment '该渠道设置的金额',
   auditor              varchar(32) comment '审核人',
   auditorTime          datetime comment '审核时间',
   primary key (oid),
   unique key index_userId (userId)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table T_TULIP_USER_INVEST_LOG comment '用户投资日志表';

/*==============================================================*/
/* Table: t_tulip_platform_banner                               */
/*==============================================================*/
create table t_tulip_platform_banner
(
   oid                  varchar(32) not null,
   channelOid           varchar(32) comment '渠道oid',
   title                varchar(64) comment '标题',
   imageUrl             varchar(256) comment '图片url',
   linkUrl              varchar(256) comment '链接url',
   isLink               tinyint comment '0-链接  1-跳转',
   toPage               varchar(32) comment 'T1:活期，T2定期:，T3:注册',
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   releaseStatus        varchar(32) comment 'ok:已发布，no:未发布，wait::待发布',
   sorting              int comment '排序',
   operator             varchar(32) comment '操作人',
   approveOpe           varchar(32) comment '审核人',
   releaseOpe           varchar(32) comment '发布人',
   remark               varchar(256) comment '备注',
   approveTime          datetime comment '审核时间',
   releaseTime          datetime comment '发布操作时间',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   createTime           timestamp default CURRENT_TIMESTAMP comment '创建时间',
   primary key (oid)
);

alter table t_tulip_platform_banner comment '平台Banner表';

