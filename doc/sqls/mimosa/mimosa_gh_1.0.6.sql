/*==============================================================*/
/* mimosa业务相关表                                        */
/*==============================================================*/

/*==============================================================*/
/* Table: T_ACCT_ACCOUNT                                        */
/*==============================================================*/
create table T_ACCT_ACCOUNT
(
   oid                  varchar(32) not null,
   type                 varchar(32) not null comment '资产: ASSETS
            负债: LIABILITY
            所有者权益: EQUITY',
   code                 varchar(32),
   name                 varchar(32),
   direction            varchar(32) comment '借: Dr
            贷: Cr',
   state                varchar(32) comment '启用: ENABLE;
            禁用: DISABLE;',
   parent               varchar(32),
   primary key (oid)
);

insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1001','ASSETS','1001','银行存款','Dr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1101','ASSETS','1101','交易性金融资产','Dr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('110101','ASSETS','110101','信托资产','Dr','DISABLE','1101');
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('110151','ASSETS','110151','货币基金','Dr','DISABLE','1101');
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1111','ASSETS','1111','投资资产','Dr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1201','ASSETS','1201','应收投资收益','Dr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1301','ASSETS','1301','应收投资结算款','Dr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('1401','ASSETS','1401','预付费金','Dr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('2001','LIABILITY','2001','应付投资结算款','Cr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('2101','LIABILITY','2101','应付备付金','Cr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('2201','LIABILITY','2201','未分配收益','Cr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('2301','LIABILITY','2301','应付费金','Cr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('2401','LIABILITY','2401','预收备付金','Cr','DISABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('3001','EQUITY','3001','所有者权益','Cr','ENABLE',NULL);
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('300101','EQUITY','300101','投资者所有者权益','Cr','ENABLE','3001');
insert  into `T_ACCT_ACCOUNT`(`oid`,`type`,`code`,`name`,`direction`,`state`,`parent`) values ('300102','EQUITY','300102','SPV所有者权益','Cr','ENABLE','3001');

/*==============================================================*/
/* Table: T_ACCT_BOOKS                                          */
/*==============================================================*/
create table T_ACCT_BOOKS
(
   oid                  varchar(32) not null,
   relative             varchar(32),
   accountOid           varchar(32),
   balance              decimal(16,4),
   openingBalance       decimal(16,4),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_ACCT_DOCUMENT                                       */
/*==============================================================*/
create table T_ACCT_DOCUMENT
(
   oid                  varchar(32) not null,
   docwordOid           varchar(32) not null,
   type                 varchar(32),
   relative             varchar(32) not null comment '这个关联到触发凭证的 资产池',
   ticket               varchar(32) comment '这个关联到触发凭证的事件, 一般是订单id',
   acctSn               int not null,
   acctDate             date,
   invoiceNum           int comment '凭证单据的数量, 后台的一般是0',
   drAmount             decimal(16,4),
   crAmount             decimal(16,4),
   createTime           datetime,
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_ACCT_DOCUMENT_ENTRY                                 */
/*==============================================================*/
create table T_ACCT_DOCUMENT_ENTRY
(
   oid                  varchar(32) not null,
   relative             varchar(32),
   ticket               varchar(32) comment '这个关联到触发凭证的事件, 一般是订单id',
   documentOid          varchar(32) not null,
   bookOid              varchar(32) not null,
   digest               varchar(128),
   drAmount             decimal(16,4),
   crAmount             decimal(16,4),
   seq                  int,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_ACCT_DOCUMENT_SN                                    */
/*==============================================================*/
create table T_ACCT_DOCUMENT_SN
(
   oid                  varchar(32) not null,
   sn                   int,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_ACCT_DOCWORD                                        */
/*==============================================================*/
create table T_ACCT_DOCWORD
(
   oid                  varchar(32) not null,
   dicword              varchar(32),
   title                varchar(32),
   dft                  varchar(32) comment '是: YES
            否: NO',
   primary key (oid)
);

insert  into `T_ACCT_DOCWORD`(`oid`,`dicword`,`title`,`dft`) values ('PAYMENT','付','付款凭证','NO');
insert  into `T_ACCT_DOCWORD`(`oid`,`dicword`,`title`,`dft`) values ('RECEIPT','收','收款凭证','NO');
insert  into `T_ACCT_DOCWORD`(`oid`,`dicword`,`title`,`dft`) values ('RECORD','记','记账凭证','YES');
insert  into `T_ACCT_DOCWORD`(`oid`,`dicword`,`title`,`dft`) values ('TRANSFER','转','转账凭证','NO');

/*==============================================================*/
/* Table: T_ACCT_DOC_TEMPLATE                                   */
/*==============================================================*/
create table T_ACCT_DOC_TEMPLATE
(
   oid                  varchar(32) not null,
   docwordOid           varchar(32) not null,
   typeOid              varchar(32) comment '关联字典表',
   name                 varchar(32),
   initName             varchar(32),
   state                varchar(32) comment '有效: ENABLE
            无效: DISABLE',
   primary key (oid)
);

insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0010_FEJS_0001_QSRK','RECORD','0010_FEJS','份额交收清算入款','份额交收清算入款','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0010_FEJS_0002_QSCK','RECORD','0010_FEJS','份额交收清算出款','份额交收清算出款','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0020_JSZF_0001_JSRK','RECORD','0020_JSZF','结算支付入款','结算支付入款','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0020_JSZF_0002_JSCK','RECORD','0020_JSZF','结算支付出款','结算支付出款','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0001_HBJJSG','RECORD','0030_NBTC','货币基金申购','货币基金申购','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0002_HBJJSH','RECORD','0030_NBTC','货币基金赎回','货币基金赎回','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0003_XTZCSG','RECORD','0030_NBTC','信托资产申购','信托资产申购','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0004_XTZCBXDF','RECORD','0030_NBTC','信托资产本息兑付','信托资产本息兑付','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0005_XTZCYJZR','RECORD','0030_NBTC','信托资产溢价转让','信托资产溢价转让','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0006_XTZCZJZR','RECORD','0030_NBTC','信托资产折价转让','信托资产折价转让','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0030_NBTC_0007_XTZCZR','RECORD','0030_NBTC','信托资产转入','信托资产转入','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0031_SPVT_0001_SG','RECORD','0031_SPVT','SPV申购交易','SPV申购交易','ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0031_SPVT_0002_SH','RECORD','0031_SPVT','SPV赎回交易','SPV赎回交易','ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0040_BFJM_0001_BFJDR','RECORD','0040_BFJM','备付金调入','备付金调入','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0040_BFJM_0002_BFJCH','RECORD','0040_BFJM','备付金偿还','备付金偿还','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0050_SYQR_0001_XTZCSYQR','RECORD','0050_SYQR','信托资产收益确认','信托资产收益确认','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0050_SYQR_0002_HBJJSYQR','RECORD','0050_SYQR','货币基金收益确认','货币基金收益确认','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0050_SYQR_0003_YHCKLXQR','RECORD','0050_SYQR','银行存款利息确认','银行存款利息确认','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0050_SYQR_0004_YSTZSYGX','RECORD','0050_SYQR','应收投资收益勾销','应收投资收益勾销','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0051_SPVI_0001_SYZZ','RECORD','0051_SPVI','资管计划收益增值','资管计划收益增值','ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0051_SPVI_0002_SYJZ','RECORD','0051_SPVI','资管计划收益减值','资管计划收益减值','ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0060_GZJZ_0001_TARGETZZ','RECORD','0060_GZJZ','投资标的资产增值','投资标的资产增值','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0060_GZJZ_0002_TARGETJZ','RECORD','0060_GZJZ','投资标的资产减值','投资标的资产减值','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0060_GZJZ_0003_CASHTOOLZZ','RECORD','0060_GZJZ','现金管理工具资产增值','现金管理工具资产增值','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0060_GZJZ_0004_CASHTOOLJZ','RECORD','0060_GZJZ','现金管理工具资产减值','现金管理工具资产减值','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0070_SYFP_0001_SYFPQR','RECORD','0070_SYFP','收益分配','收益分配','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0071_SPVA_0001_SYFF','RECORD','0071_SPVA','资管计划收益发放','资管计划收益发放','ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0080_FYJT_0001_FJJT','RECORD','0080_FYJT','计提管理费和销售佣金','计提管理费和销售佣金','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0090_FYTQ_0001_FYTQ','RECORD','0090_FYTQ','提取管理费和销售佣金','提取管理费和销售佣金','DISABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0100_FEQR_0001_FEQRRK', 'RECORD', '0100_FEQR', '份额确认入款', '份额确认入款', 'ENABLE');
insert  into `T_ACCT_DOC_TEMPLATE`(`oid`,`docwordOid`,`typeOid`,`name`,`initName`,`state`) values ('0100_FEQR_0002_FEQRCK', 'RECORD', '0100_FEQR', '份额确认出款', '份额确认出款', 'ENABLE');

/*==============================================================*/
/* Table: T_ACCT_DOC_TEMPLATE_ENTRY                             */
/*==============================================================*/
create table T_ACCT_DOC_TEMPLATE_ENTRY
(
   oid                  varchar(32) not null,
   templateOid          varchar(32) not null,
   direction            varchar(32) comment '借: Dr
            贷: Cr',
   accountOid           varchar(32) not null,
   digest               varchar(128),
   seq                  int,
   primary key (oid)
);

insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0010_0001_1301_01','0010_FEJS_0001_QSRK','Dr','1301','清算入款',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0010_0001_3001_02','0010_FEJS_0001_QSRK','Cr','3001','清算入款',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0010_0002_2001_02','0010_FEJS_0002_QSCK','Cr','2001','清算出款',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0010_0002_3001_01','0010_FEJS_0002_QSCK','Dr','3001','清算出款',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0020_0001_1001_01','0020_JSZF_0001_JSRK','Dr','1001','结算入款',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0020_0001_1301_02','0020_JSZF_0001_JSRK','Cr','1301','结算入款',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0020_0002_1001_02','0020_JSZF_0002_JSCK','Cr','1001','结算出款',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0020_0002_2001_01','0020_JSZF_0002_JSCK','Dr','2001','结算出款',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0001_1001_02','0030_NBTC_0001_HBJJSG','Cr','1001','货币基金申购',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0001_110151_01','0030_NBTC_0001_HBJJSG','Dr','110151','货币基金申购',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0002_1001_01','0030_NBTC_0002_HBJJSH','Dr','1001','货币基金赎回',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0002_110151_02','0030_NBTC_0002_HBJJSH','Cr','110151','货币基金赎回',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0003_1001_02','0030_NBTC_0003_XTZCSG','Cr','1001','信托资产申购',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0003_110101_01','0030_NBTC_0003_XTZCSG','Dr','110101','信托资产申购',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_1001_05','0030_NBTC_0004_XTZCBXDF','Dr','1001','信托资产本息兑付',5);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_1001_13','0030_NBTC_0004_XTZCBXDF','Dr','1001','信托资产本息兑付',13);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_01','0030_NBTC_0004_XTZCBXDF','Dr','110101','信托资产本息兑付',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_03','0030_NBTC_0004_XTZCBXDF','Dr','110101','信托资产本息兑付',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_06','0030_NBTC_0004_XTZCBXDF','Cr','110101','信托资产本息兑付',6);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_08','0030_NBTC_0004_XTZCBXDF','Cr','110101','信托资产本息兑付',8);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_10','0030_NBTC_0004_XTZCBXDF','Cr','110101','信托资产本息兑付',10);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_12','0030_NBTC_0004_XTZCBXDF','Cr','110101','信托资产本息兑付',12);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_110101_14','0030_NBTC_0004_XTZCBXDF','Cr','110101','信托资产本息兑付',14);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_1201_02','0030_NBTC_0004_XTZCBXDF','Cr','1201','信托资产本息兑付',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_1201_07','0030_NBTC_0004_XTZCBXDF','Dr','1201','信托资产本息兑付',7);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_1201_11','0030_NBTC_0004_XTZCBXDF','Dr','1201','信托资产本息兑付',11);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_2201_04','0030_NBTC_0004_XTZCBXDF','Cr','2201','信托资产本息兑付',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0004_2201_09','0030_NBTC_0004_XTZCBXDF','Dr','2201','信托资产本息兑付',9);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_1001_05','0030_NBTC_0005_XTZCYJZR','Dr','1001','信托资产溢价转让',5);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_110101_01','0030_NBTC_0005_XTZCYJZR','Dr','110101','信托资产溢价转让',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_110101_03','0030_NBTC_0005_XTZCYJZR','Dr','110101','信托资产溢价转让',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_110101_06','0030_NBTC_0005_XTZCYJZR','Cr','110101','信托资产溢价转让',6);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_1201_04','0030_NBTC_0005_XTZCYJZR','Cr','1201','信托资产溢价转让',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0005_2201_02','0030_NBTC_0005_XTZCYJZR','Cr','2201','信托资产溢价转让',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_1001_05','0030_NBTC_0006_XTZCZJZR','Dr','1001','信托资产折价转让',5);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_110101_02','0030_NBTC_0006_XTZCZJZR','Cr','110101','信托资产折价转让',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_110101_04','0030_NBTC_0006_XTZCZJZR','Cr','110101','信托资产折价转让',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_110101_06','0030_NBTC_0006_XTZCZJZR','Cr','110101','信托资产折价转让',6);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_1201_03','0030_NBTC_0006_XTZCZJZR','Dr','1201','信托资产折价转让',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0006_2201_01','0030_NBTC_0006_XTZCZJZR','Dr','2201','信托资产折价转让',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0007_1001_02','0030_NBTC_0007_XTZCZR','Cr','1001','信托资产转入',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0030_0007_110101_01','0030_NBTC_0007_XTZCZR','Dr','110101','信托资产转入',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1111_01','0031_SPVT_0001_SG','Dr','1111','SPV申购交易',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1111_03','0031_SPVT_0001_SG','Dr','1111','SPV申购交易',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1111_05','0031_SPVT_0001_SG','Dr','1111','SPV申购交易',5);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1111_07','0031_SPVT_0001_SG','Dr','1111','SPV申购交易',7);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1401_06','0031_SPVT_0001_SG','Cr','1401','SPV申购交易',6);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_1401_11','0031_SPVT_0001_SG','Dr','1401','SPV申购交易',11);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_2301_04','0031_SPVT_0001_SG','Cr','2301','SPV申购交易',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_2301_09','0031_SPVT_0001_SG','Dr','2301','SPV申购交易',9);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_300102_02','0031_SPVT_0001_SG','Cr','300102','SPV申购交易',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_300102_08','0031_SPVT_0001_SG','Cr','300102','SPV申购交易',8);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_300102_10','0031_SPVT_0001_SG','Cr','300102','SPV申购交易',10);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0001_300102_12','0031_SPVT_0001_SG','Cr','300102','SPV申购交易',12);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1111_02','0031_SPVT_0002_SH','Cr','1111','SPV赎回交易',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1111_04','0031_SPVT_0002_SH','Cr','1111','SPV赎回交易',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1111_06','0031_SPVT_0002_SH','Cr','1111','SPV赎回交易',6);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1111_08','0031_SPVT_0002_SH','Cr','1111','SPV赎回交易',8);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1401_05','0031_SPVT_0002_SH','Dr','1401','SPV赎回交易',5);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_1401_12','0031_SPVT_0002_SH','Cr','1401','SPV赎回交易',12);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_2301_03','0031_SPVT_0002_SH','Dr','2301','SPV赎回交易',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_2301_10','0031_SPVT_0002_SH','Cr','2301','SPV赎回交易',10);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_300102_01','0031_SPVT_0002_SH','Dr','300102','SPV赎回交易',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_300102_07','0031_SPVT_0002_SH','Dr','300102','SPV赎回交易',7);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_300102_09','0031_SPVT_0002_SH','Dr','300102','SPV赎回交易',9);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0031_0002_300102_11','0031_SPVT_0002_SH','Dr','300102','SPV赎回交易',11);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0040_0001_1001_01','0040_BFJM_0001_BFJDR','Dr','1001','备付金调入',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0040_0001_2101_02','0040_BFJM_0001_BFJDR','Cr','2101','备付金调入',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0040_0002_1001_02','0040_BFJM_0002_BFJCH','Cr','1001','备付金偿还',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0040_0002_2101_01','0040_BFJM_0002_BFJCH','Dr','2101','备付金偿还',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0001_110101_01','0050_SYQR_0001_XTZCSYQR','Dr','110101','信托资产收益确认',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0001_2201_02','0050_SYQR_0001_XTZCSYQR','Cr','2201','信托资产收益确认',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0002_110151_01','0050_SYQR_0002_HBJJSYQR','Dr','110151','货币基金收益确认',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0002_2201_02','0050_SYQR_0002_HBJJSYQR','Cr','2201','货币基金收益确认',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0003_1001_01','0050_SYQR_0003_YHCKLXQR','Dr','1001','银行存款利息确认',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0003_2201_02','0050_SYQR_0003_YHCKLXQR','Cr','2201','银行存款利息确认',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0004_1201_02','0050_SYQR_0004_YSTZSYGX','Cr','1201','应收投资收益勾销',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0050_0004_2201_01','0050_SYQR_0004_YSTZSYGX','Dr','2201','应收投资收益勾销',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0001_1111_01','0051_SPVI_0001_SYZZ','Dr','1111','资管计划收益增值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0001_1111_03','0051_SPVI_0001_SYZZ','Dr','1111','资管计划收益增值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0001_1201_04','0051_SPVI_0001_SYZZ','Cr','1201','资管计划收益增值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0001_2201_02','0051_SPVI_0001_SYZZ','Cr','2201','资管计划收益增值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0002_1111_02','0051_SPVI_0002_SYJZ','Cr','1111','资管计划收益减值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0002_1111_04','0051_SPVI_0002_SYJZ','Cr','1111','资管计划收益减值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0002_1201_03','0051_SPVI_0002_SYJZ','Dr','1201','资管计划收益减值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0051_0002_2201_01','0051_SPVI_0002_SYJZ','Dr','2201','资管计划收益减值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0001_110101_01','0060_GZJZ_0001_TARGETZZ','Dr','110101','投资标的资产增值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0001_110101_03','0060_GZJZ_0001_TARGETZZ','Dr','110101','投资标的资产增值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0001_1201_04','0060_GZJZ_0001_TARGETZZ','Cr','1201','投资标的资产增值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0001_2201_02','0060_GZJZ_0001_TARGETZZ','Cr','2201','投资标的资产增值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0002_110101_02','0060_GZJZ_0002_TARGETJZ','Cr','110101','投资标的资产减值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0002_110101_04','0060_GZJZ_0002_TARGETJZ','Cr','110101','投资标的资产减值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0002_1201_03','0060_GZJZ_0002_TARGETJZ','Dr','1201','投资标的资产减值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0002_2201_01','0060_GZJZ_0002_TARGETJZ','Dr','2201','投资标的资产减值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0003_110151_01','0060_GZJZ_0003_CASHTOOLZZ','Dr','110151','现金管理工具增值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0003_110151_03','0060_GZJZ_0003_CASHTOOLZZ','Dr','110151','现金管理工具增值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0003_1201_04','0060_GZJZ_0003_CASHTOOLZZ','Cr','1201','现金管理工具增值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0003_2201_02','0060_GZJZ_0003_CASHTOOLZZ','Cr','2201','现金管理工具增值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0004_110151_02','0060_GZJZ_0004_CASHTOOLJZ','Cr','110151','现金管理工具减值',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0004_110151_04','0060_GZJZ_0004_CASHTOOLJZ','Cr','110151','现金管理工具减值',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0004_1201_03','0060_GZJZ_0004_CASHTOOLJZ','Dr','1201','现金管理工具减值',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0060_0004_2201_01','0060_GZJZ_0004_CASHTOOLJZ','Dr','2201','现金管理工具减值',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0070_0001_1201_03','0070_SYFP_0001_SYFPQR','Dr','1201','收益分配',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0070_0001_2201_01','0070_SYFP_0001_SYFPQR','Dr','2201','收益分配',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0070_0001_3001_02','0070_SYFP_0001_SYFPQR','Cr','3001','收益分配',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0070_0001_3001_04','0070_SYFP_0001_SYFPQR','Cr','3001','收益分配',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0071_0001_1201_03','0071_SPVA_0001_SYFF','Dr','1201','资管计划收益发放',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0071_0001_2201_01','0071_SPVA_0001_SYFF','Dr','2201','资管计划收益发放',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0071_0001_300101_02','0071_SPVA_0001_SYFF','Cr','300101','资管计划收益发放',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0071_0001_300101_04','0071_SPVA_0001_SYFF','Cr','300101','资管计划收益发放',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0080_0001_1201_03','0080_FYJT_0001_FJJT','Dr','1201','费金计提',3);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0080_0001_2201_01','0080_FYJT_0001_FJJT','Dr','2201','费金计提',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0080_0001_2301_02','0080_FYJT_0001_FJJT','Cr','2301','费金计提',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0080_0001_2301_04','0080_FYJT_0001_FJJT','Cr','2301','费金计提',4);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0090_0001_1001_02','0090_FYTQ_0001_FYTQ','Cr','1001','费金提取',2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0090_0001_2301_01','0090_FYTQ_0001_FYTQ','Dr','2301','费金提取',1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0100_0001_300101_01', '0100_FEQR_0001_FEQRRK', 'Dr', '300101', '份额确认入款', 1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0100_0001_300102_02', '0100_FEQR_0001_FEQRRK', 'Cr', '300102', '份额确认入款', 2);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0100_0002_300102_01', '0100_FEQR_0002_FEQRCK', 'Dr', '300102', '份额确认出款', 1);
insert  into `T_ACCT_DOC_TEMPLATE_ENTRY`(`oid`,`templateOid`,`direction`,`accountOid`,`digest`,`seq`) values ('0100_0002_300101_02', '0100_FEQR_0002_FEQRCK', 'Dr', '300101', '份额确认出款', 2);

/*==============================================================*/
/* Table: T_ACCT_DOC_TYPE                                       */
/*==============================================================*/
create table T_ACCT_DOC_TYPE
(
   oid                  varchar(32) not null,
   name                 varchar(32),
   primary key (oid)
);

insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0010_FEJS','份额交收/清算');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0020_JSZF','资产池结算支付');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0030_NBTC','资产池内部调仓');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0031_SPVT','SPV交易');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0040_BFJM','备付金管理');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0050_SYQR','资产池收益确认');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0051_SPVI','资管计划收益确认');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0060_GZJZ','估值校准');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0070_SYFP','资产池收益分配');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0071_SPVA','资管计划收益发放');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0080_FYJT','计提管理费和销售佣金');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0090_FYTQ','提取管理费和销售佣金');
insert  into `T_ACCT_DOC_TYPE`(`oid`,`name`) values ('0100_FEQR', '份额确认');

/*==============================================================*/
/* Table: T_ACCT_SWITCH                                         */
/*==============================================================*/
create table T_ACCT_SWITCH
(
   oid                  varchar(32) not null,
   code                 varchar(32) comment '配置编码',
   name                 varchar(32) comment '配置名称',
   type                 varchar(32) comment '类型 switch开关  configure参数',
   content              varchar(256) comment '配置内容',
   status               varchar(32) comment '状态  toApprove待审核  pass已通过  refused已驳回  enable已启用  disable已禁用',
   whiteStatus          varchar(32) comment '黑白名单状态  white启用白名单 black启用黑名单  no禁用',
   requester            varchar(32) comment '申请人',
   approver             varchar(32) comment '审核人',
   approveRemark        varchar(256) comment '审核意见',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

insert  into `T_ACCT_SWITCH`(`oid`,`code`,`name`,`type`,`content`,`status`,`whiteStatus`,`requester`,`approver`,`approveRemark`,`createTime`,`updateTime`) values 
('1','WithdrawNum','一个月提现次数','configure','3','enable','no',NULL,NULL,'pass','2017-02-15 20:46:32','2017-02-15 20:55:25'),
('2','TradingDayWithdrawFee','交易日提现手续费','configure','2','enable','no',NULL,NULL,'pass','2017-02-15 20:46:18','2017-02-15 20:55:14'),
('3','NoTradingDayWithdrawFee','非交易日提现手续费','configure','3','enable','no',NULL,NULL,'pass','2017-02-15 20:47:27','2017-02-15 20:55:15'),
('4','Register','注册','switch','','enable','no',NULL,NULL,'pass','2017-02-15 20:49:31','2017-02-15 20:55:13'),
('5','Login','登录','switch','','enable','no',NULL,NULL,'pass','2017-02-15 20:49:27','2017-02-15 20:55:13'),
('6','Recharge','充值','switch','','enable','no',NULL,NULL,'pass','2017-02-15 20:49:22','2017-02-15 20:55:12'),
('7','Withdraw','提现','switch','','enable','no',NULL,NULL,'pass','2017-02-15 20:49:08','2017-02-15 20:55:11'),
('8','ApplyPurchase','申购','switch','','enable','no',NULL,NULL,'pass','2017-02-15 20:50:14','2017-02-15 20:58:15'),
('9','WithdrawDayLimit','平台单日提现限额','configure','200000','enable','no',NULL,NULL,'pass','2017-02-15 20:50:14','2017-02-15 20:58:15');

/*==============================================================*/
/* Table: T_ACCT_SWITCH_BLACK                                   */
/*==============================================================*/
create table T_ACCT_SWITCH_BLACK
(
   oid                  varchar(32) not null,
   switchOid            varchar(32) comment '所属系统配置',
   userOid              varchar(32) comment '用户Oid',
   userAcc              varchar(32) comment '用户手机',
   operator             varchar(32) comment '操作人',
   note                 varchar(256) comment '备注',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_ACCT_SWITCH_WHITE                                   */
/*==============================================================*/
create table T_ACCT_SWITCH_WHITE
(
   oid                  varchar(32) not null,
   switchOid            varchar(32) comment '所属系统配置',
   userOid              varchar(32) comment '用户Oid',
   userAcc              varchar(32) comment '用户手机',
   operator             varchar(32) comment '操作人',
   note                 varchar(256) comment '备注',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL                                       */
/*==============================================================*/
create table T_GAM_ASSETPOOL
(
   oid                  varchar(32) not null,
   spvOid               varchar(32),
   name                 varchar(128),
   targetRate           decimal(8,6),
   cashtoolRate         decimal(8,6),
   cashRate             decimal(8,6),
   cashPosition         decimal(16,4),
   freezeCash           decimal(16,4),
   freezeCashForFund    decimal(16,4),
   scale                decimal(16,4),
   transitCash          decimal(16,4),
   deviationValue       decimal(16,4),
   baseDate             date,
   marketValue          decimal(16,4),
   state                varchar(32),
   creater              varchar(32),
   operator             varchar(32),
   cashFactRate         decimal(16,6),
   cashtoolFactRate     decimal(16,6),
   targetFactRate       decimal(16,6),
   confirmProfit        decimal(16,4),
   factProfit           decimal(16,4),
   scheduleState        varchar(32),
   incomeState          varchar(32),
   factValuation        varchar(32),
   unDistributeProfit   decimal(16,4),
   payFeigin            decimal(16,4),
   spvProfit            decimal(16,4),
   investorProfit       decimal(16,4),
   shares               decimal(16,4),
   nav                  decimal(16,4),
   countintChargefee    decimal(16,4),
   drawedChargefee      decimal(16,4),
   organization         varchar(64),
   planName             varchar(64),
   bank                 varchar(32),
   account              varchar(32),
   contact              varchar(32),
   telephone            varchar(32),
   calcBasis            int comment '360 或365',
   updateTime           datetime,
   createTime           datetime,
   netValue             decimal(16,4),
   trusteeRate          decimal(16,6),
   trusteeFee           decimal(16,4),
   manageRate           decimal(16,6),
   manageFee            decimal(16,4),
   nonTradingDays       int,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL_INCOMESCHEDULE_APPLY                  */
/*==============================================================*/
create table T_GAM_ASSETPOOL_INCOMESCHEDULE_APPLY
(
   oid                  varchar(32) not null,
   assetpoolOid         varchar(32) comment '所属资产池',
   schedulingOid        varchar(32) comment '所属排期',
   basicDate            date comment '排期日期',
   annualizedRate       decimal(8,4) comment '年化收益率',
   creator              varchar(32) comment '申请人',
   createTime           datetime comment '申请时间',
   approver             varchar(32) comment '审批人',
   approverTime         datetime comment '审批时间',
   status               varchar(32) comment 'toApprove待审核,pass通过,reject驳回,delete删除,lose已失效',
   type                 varchar(32) comment 'new新建，update修改，delete删除',
   primary key (oid)
);

/*==============================================================*/
/* Index: idx_incomeschedule_apply_dt_stat                      */
/*==============================================================*/
create index idx_incomeschedule_apply_dt_stat on T_GAM_ASSETPOOL_INCOMESCHEDULE_APPLY
(
   basicDate,
   status
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL_INCOMESCHEDULE_SCHEDULING             */
/*==============================================================*/
create table T_GAM_ASSETPOOL_INCOMESCHEDULE_SCHEDULING
(
   oid                  varchar(32) not null,
   assetpoolOid         varchar(32) comment '所属资产池',
   basicDate            date comment '排期日期',
   annualizedRate       decimal(8,4) comment '年化收益率',
   status               varchar(32) comment 'toApprove待审核，pass待执行，finish已完成，fail失败，lose已失效',
   errorMes             text comment '失败原因',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: idx_incomescheduling_dt_stat                          */
/*==============================================================*/
create index idx_incomescheduling_dt_stat on T_GAM_ASSETPOOL_INCOMESCHEDULE_SCHEDULING
(
   basicDate,
   status
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL_INCOME_ALLOCATE                       */
/*==============================================================*/
create table T_GAM_ASSETPOOL_INCOME_ALLOCATE
(
   oid                  varchar(32) not null,
   eventOid             varchar(32) not null,
   productOid           varchar(32) not null,
   baseDate             date,
   capital              decimal(16,4),
   allocateIncome       decimal(16,4),
   rewardIncome         decimal(16,4),
   ratio                decimal(16,4),
   wincome              decimal(16,4),
   days                 int,
   allocateIncomeType   varchar(32) comment 'raiseIncome:募集期收益，durationIncome:存续期收益',
   successAllocateIncome decimal(16,4),
   successAllocateRewardIncome decimal(16,4),
   leftAllocateIncome   decimal(16,4),
   leftAllocateBaseIncome decimal(16,4),
   leftAllocateRewardIncome decimal(16,4),
   successAllocateInvestors int,
   failAllocateInvestors int,
   couponIncome         decimal(16,4),
   successAllocateCouponIncome decimal(16,4),
   leftAllocateCouponIncome decimal(16,4),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL_INCOME_ALLOCATE_FAIL                  */
/*==============================================================*/
create table T_GAM_ASSETPOOL_INCOME_ALLOCATE_FAIL
(
   oid                  varchar(32) not null,
   allocateOid          varchar(32),
   failTime             datetime,
   failComment          text,
   successTime          datetime,
   allocateDate         date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ASSETPOOL_INCOME_EVENT                          */
/*==============================================================*/
create table T_GAM_ASSETPOOL_INCOME_EVENT
(
   oid                  varchar(32) not null,
   assetPoolOid         varchar(32) not null,
   baseDate             date,
   allocateIncome       decimal(16,4),
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   days                 int,
   status               varchar(32) comment '待审核: CREATE
            发放中: ALLOCATING
            发放完成: ALLOCATED
            发放失败: ALLOCATEFAIL
            驳回: FAIL
            已删除: DELETE
            ',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_CCP_WARRANTOR                                   */
/*==============================================================*/
create table T_GAM_CCP_WARRANTOR
(
   oid                  varchar(32) not null,
   title                varchar(32),
   lowScore             int,
   highScore            int,
   weight               decimal(8,4),
   primary key (oid)
);

INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('5f70b29171e24786bc5b912a3741df3c','E',50,59,1);
INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('6aead63b86b94b01a225ad5f0e6f1690','F',0,49,1);
INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('8f514ea288ce458eac58ea082637f47f','A',90,100,1);
INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('aac09f9f8ca549eeaaf3beb265c6ae75','C',70,79,1);
INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('d25e4bda107c4b29842ed393255680e9','D',60,69,1);
INSERT  INTO `T_GAM_CCP_WARRANTOR`(`oid`,`title`,`lowScore`,`highScore`,`weight`) VALUES ('f8764c06335a41a3b2c8824e1e5d459f','B',80,89,1);

/*==============================================================*/
/* Table: T_GAM_CCP_WARRANTY_EXPIRE                             */
/*==============================================================*/
create table T_GAM_CCP_WARRANTY_EXPIRE
(
   oid                  varchar(32) not null,
   title                varchar(32),
   weight               decimal(8,4),
   primary key (oid)
);

INSERT  INTO `T_GAM_CCP_WARRANTY_EXPIRE`(`oid`,`title`,`weight`) VALUES ('321c617a963d47dc9ddbdc5725f2f43e','1年以上3年以内（含3年）',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_EXPIRE`(`oid`,`title`,`weight`) VALUES ('a45c98aa9ee34010927b5a5e0d893ee7','6个月以上1年以内（含1年）',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_EXPIRE`(`oid`,`title`,`weight`) VALUES ('b6485ef1d6ec42bb81a9429c8c31a6e7','3个月以上6个月以内（含6个月）',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_EXPIRE`(`oid`,`title`,`weight`) VALUES ('ebef1bd748cf4ea0b2048de69303fabf','3个月以内（含3个月）',1);

/*==============================================================*/
/* Table: T_GAM_CCP_WARRANTY_LEVEL                              */
/*==============================================================*/
create table T_GAM_CCP_WARRANTY_LEVEL
(
   oid                  varchar(32) not null,
   name                 varchar(32),
   wlevel               varchar(32) comment '低: LOW
            中: MID
            高: HIGH',
   coverLow             varchar(10) comment '0: 不包括, 显示 (
            1: 包括, 显示 [',
   lowFactor            decimal(8,4),
   highFactor           decimal(8,4),
   coverHigh            varchar(10) comment '0: 不包括, 显示 )
            1: 包括, 显示 ]',
   primary key (oid)
);

INSERT  INTO `T_GAM_CCP_WARRANTY_LEVEL`(`oid`,`wlevel`,`name`,`coverLow`,`lowFactor`,`highFactor`,`coverHigh`) VALUES ('01','LOW','低','[','0.0000','0.4000',')');
INSERT  INTO `T_GAM_CCP_WARRANTY_LEVEL`(`oid`,`wlevel`,`name`,`coverLow`,`lowFactor`,`highFactor`,`coverHigh`) VALUES ('02','MID','中','[','0.4000','0.6000',')');
INSERT  INTO `T_GAM_CCP_WARRANTY_LEVEL`(`oid`,`wlevel`,`name`,`coverLow`,`lowFactor`,`highFactor`,`coverHigh`) VALUES ('03','HIGH','高','[','0.6000','1.0000',']');

/*==============================================================*/
/* Table: T_GAM_CCP_WARRANTY_MODE                               */
/*==============================================================*/
create table T_GAM_CCP_WARRANTY_MODE
(
   oid                  varchar(32) not null,
   type                 varchar(32) comment '保证方式: GUARANTEE
            抵押方式: MORTGAGE
            质押方式: HYPOTHECATION',
   title                varchar(64),
   weight               decimal(8,4),
   primary key (oid)
);

INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('0e18ce39c10e490dbb7c493a7c87ef75','HYPOTHECATION','企业债券',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('1c8deb0905ef4e809447881cbbf4c1b3','HYPOTHECATION','政府国债',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('1d7551dc5ce541e88d7b5c609d9bd2f7','GUARANTEE','银行信用等级AA公司保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('1f2c146a7b63454db08e4ca7f389268b','GUARANTEE','非银行金融机构保证（含境内外资非银行金融机构）',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('262a008c3fa64bbd91a4ae5761264e66','GUARANTEE','上市公司保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('3299d2e844044c4c83295c7942e8479f','GUARANTEE','银行信用等级A公司保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('362564a03169445ba96ba50ddb0dcab9','GUARANTEE','银行信用等级AAA公司保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('499a85cb913f412581b2c39985fba281','GUARANTEE','具有代偿能力自然人连带责任保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('50de83deeb4b43ce82c29e266d75e467','HYPOTHECATION','上市公司股票（ST除外）',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('51bff2d85e5a4658baddfcace74051dc','HYPOTHECATION','人民币存单抵押',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('5aa9ab52af6e40c08f50f1fe759179e0','HYPOTHECATION','金融债券',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('68df363825f44316846475afa4b50a31','HYPOTHECATION','银行信用等级AA公司股权',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('77b467d9248746b69be8c0104a7f7b27','HYPOTHECATION','银行信用等级BBB公司股权',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('7ac83a7462b94a0bbe4872994abeff6a','HYPOTHECATION','银行信用等级A公司股权',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('89ef93c8130f4a429d979e6423aa2eb6','MORTGAGE','不完全产权楼宇抵押',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('92932504c32c4a908ff4226bb0386aa1','HYPOTHECATION','外币存单抵押、汇票抵押（币种为在中国银行能够自动兑换之货币）',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('9ad321bfed384bbd9ff48f0aac86e195','HYPOTHECATION','商业银行及下政策性银行承兑票据贴现',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('a8fee7aa63e142da879d6c3ad3e1a636','MORTGAGE','完全产权楼宇抵押',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('a91b1d02f214482780a04c7337a2a596','HYPOTHECATION','上市公司未流通股票',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('b003538f7ad04ec6992a8dccf8e2feef','HYPOTHECATION','银行信用等级AAA 公司股权',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('d25eb533eb8442caa83b47326553df06','GUARANTEE','商业银行及政策性银行保证（含境内外资银行）',0);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('ec3d75312cfa46988067a0119306d4af','MORTGAGE','完全产权动产抵押',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('f8c73939940c48f2abf8c45c4ef41574','MORTGAGE','其他抵押',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('ff01d2cebf4c4348ba588a03728650ea','GUARANTEE','具有代偿能力的自然人一般责任保证',1);
INSERT  INTO `T_GAM_CCP_WARRANTY_MODE`(`oid`,`type`,`title`,`weight`) VALUES ('ffa129c71b7d4d539cf61870fd80886e','GUARANTEE','银行信用等级BBB公司保证',1);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_CATE                                   */
/*==============================================================*/
create table T_GAM_CCR_RISK_CATE
(
   oid                  varchar(32) not null,
   type                 varchar(32) not null comment '风险预警指标: WARNING
            风险评分指标: SCORE',
   title                varchar(128) not null,
   primary key (oid)
);

insert  into `T_GAM_CCR_RISK_CATE`(`oid`,`type`,`title`) values ('19ec716c1e5611e6bc0b00163e0021b3','SCORE','本息逾期'),('8cfdbcae837940379af8a1ea528d6e16','SCORE','测试指标分类'),('f3c49dee9bb145cc94dcbd89220292b8','SCORE','测试分类-01');

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_INDICATE                               */
/*==============================================================*/
create table T_GAM_CCR_RISK_INDICATE
(
   oid                  varchar(32) not null,
   cateOid              varchar(32) not null,
   title                varchar(128) not null,
   state                varchar(32) not null comment '启用: ENABLE
            停用: DISABLE',
   dataType             varchar(32) not null comment '数值: NUMBER
            数值区间: NUMRANGE
            文本: TEXT
            ',
   dataUnit             varchar(32) not null,
   primary key (oid)
);

insert  into `T_GAM_CCR_RISK_INDICATE`(`oid`,`cateOid`,`title`,`state`,`dataType`,`dataUnit`) values ('1cedcfda1e5611e6bc0b00163e0021b3','19ec716c1e5611e6bc0b00163e0021b3','逾期金额','ENABLE','NUMBER','万元'),('2bf11d368efc4db390f1beea3e7acbfc','f3c49dee9bb145cc94dcbd89220292b8','测试指标项-01','ENABLE','TEXT','万'),('716c79a77e244f10a8063a6119be74a9','8cfdbcae837940379af8a1ea528d6e16','文本类指标','ENABLE','TEXT',''),('da2a8a84bc8d44b1885e64bb08523e35','8cfdbcae837940379af8a1ea528d6e16','数值区间类指标','ENABLE','NUMRANGE','%'),('fe51b6371fdc469ea772f15f248d3e22','8cfdbcae837940379af8a1ea528d6e16','数值类指标','ENABLE','NUMBER','元');

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_INDICATE_COLLECT                       */
/*==============================================================*/
create table T_GAM_CCR_RISK_INDICATE_COLLECT
(
   oid                  varchar(32) not null,
   indicateOid          varchar(32) not null,
   relative             varchar(64) not null,
   collectOption        varchar(32),
   collectData          varchar(32),
   collectScore         int,
   primary key (oid)
);

insert  into `T_GAM_CCR_RISK_INDICATE_COLLECT`(`oid`,`indicateOid`,`relative`,`collectOption`,`collectData`,`collectScore`) values ('0a81f526b2c447469bc3e2d27020d350','1cedcfda1e5611e6bc0b00163e0021b3','73c4c0f45aeb41d2bf7ea314a87db06d','802ab84adf1b4355b197f96d4a28913a','YQJE-03',8),('1adee3d7ff2d4a6ba974963475a8d904','da2a8a84bc8d44b1885e64bb08523e35','xxxxxxxxxxxxxxxx','ea5d2cbe7b5c469b850747ac078eceb9','1',1),('2aae29fa63c84ab6915b027d6ff35d79','2bf11d368efc4db390f1beea3e7acbfc','402881fe54ebb7f00154ebb92c380000','5043757112fa462da30f84c0473e8244','xxxxxxx2',2),('409f597d0ecb49d28f3e0237bdddf953','da2a8a84bc8d44b1885e64bb08523e35','402881fe54ebb7f00154ebb92c380000','c3a0f9afc7c0440c8faa3313e1fd4487','11',8),('476016a4fa054fb6bcbe130f9925092e','1cedcfda1e5611e6bc0b00163e0021b3','xxxxxxxxxxxxxxxx','8588186f3ff642b080a9a2096b38b2a9','YQJE-02',7),('49985a4bfc9744c3a59180d7fab50c51','716c79a77e244f10a8063a6119be74a9','dc370b9f8c864a59ab383f309db1fccb','8bc8f615ae2c49bea99e34947cc459d3','T2',2),('56a854c23b174b8286321260f8019efb','716c79a77e244f10a8063a6119be74a9','xxxxxxxxxxxxxxxx','8bc8f615ae2c49bea99e34947cc459d3','T2',2),('6ebb74e974364d8292e912f6a274d9c1','fe51b6371fdc469ea772f15f248d3e22','xxxxxxxxxxxxxxxx','70dd30bd19704ce9952071af11d76861','333',3),('72bd119aef714b6eb5b0ad306585184d','716c79a77e244f10a8063a6119be74a9','73c4c0f45aeb41d2bf7ea314a87db06d','8bc8f615ae2c49bea99e34947cc459d3','T2',2),('7b38ccc26a5c4ceb9fd46cc671e7e68e','fe51b6371fdc469ea772f15f248d3e22','73c4c0f45aeb41d2bf7ea314a87db06d','a3b5b7e1109b495ab5d7ae80ef61e781','222',2),('7e833d684ac74dc4bec0fe5e35c3c2c5','fe51b6371fdc469ea772f15f248d3e22','402881fe54ebb7f00154ebb92c380000','cc23a1261cef46d4a619369afaab4c0d','111',1),('8070d127c5d84446a195068cee4aa51f','da2a8a84bc8d44b1885e64bb08523e35','73c4c0f45aeb41d2bf7ea314a87db06d','c3a0f9afc7c0440c8faa3313e1fd4487','12',8),('9f7b2712983f47ab91abb191c3237a6c','da2a8a84bc8d44b1885e64bb08523e35','dc370b9f8c864a59ab383f309db1fccb','c3a0f9afc7c0440c8faa3313e1fd4487','12',8),('a375550c84c34f44a025303661eeba49','fe51b6371fdc469ea772f15f248d3e22','dc370b9f8c864a59ab383f309db1fccb','a3b5b7e1109b495ab5d7ae80ef61e781','222',2),('af1566ed14084ae190f15d30ebe3f692','2bf11d368efc4db390f1beea3e7acbfc','dc370b9f8c864a59ab383f309db1fccb','2540353adf014b6d8bb2f3663a52453a','xxxxxxxxx3',3),('bc8303a0cb5542db93191046ed438bc6','1cedcfda1e5611e6bc0b00163e0021b3','402881fe54ebb7f00154ebb92c380000','802ab84adf1b4355b197f96d4a28913a','YQJE-03',8),('e6f23d63a119405eb5e8e14307a67445','2bf11d368efc4db390f1beea3e7acbfc','73c4c0f45aeb41d2bf7ea314a87db06d','2540353adf014b6d8bb2f3663a52453a','xxxxxxxxx3',3),('e98a0ce4f22c4f71a699a12cc396b108','716c79a77e244f10a8063a6119be74a9','402881fe54ebb7f00154ebb92c380000','887b8834759047db8b69e2c30a6173b9','T1',1),('ef562b29091941de89499989c8e0edbf','2bf11d368efc4db390f1beea3e7acbfc','xxxxxxxxxxxxxxxx','f8d2a98a95394e9cb02fecc5fe13e376','N/A',3),('fb3b0833c0fe4dedbf0e22ad53337e15','1cedcfda1e5611e6bc0b00163e0021b3','dc370b9f8c864a59ab383f309db1fccb','802ab84adf1b4355b197f96d4a28913a','YQJE-03',8);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_OPTIONS                                */
/*==============================================================*/
create table T_GAM_CCR_RISK_OPTIONS
(
   oid                  varchar(32) not null,
   indicateOid          varchar(32) not null,
   score                int,
   dft                  varchar(32) comment '是: YES
            否: NO',
   param0               varchar(64),
   param1               varchar(64),
   param2               varchar(64),
   param3               varchar(64),
   seq                  int,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_WARNING                                */
/*==============================================================*/
create table T_GAM_CCR_RISK_WARNING
(
   oid                  varchar(32) not null,
   indicateOid          varchar(32) not null,
   title                varchar(128),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_WARNING_COLLECT                        */
/*==============================================================*/
create table T_GAM_CCR_RISK_WARNING_COLLECT
(
   oid                  varchar(32) not null,
   wariningOid          varchar(32) not null,
   relative             varchar(64) not null,
   collectOption        varchar(32),
   collectData          varchar(32),
   wlevel               varchar(32),
   handleLevel          varchar(32),
   createTime           timestamp,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_WARNING_HANDLE                         */
/*==============================================================*/
create table T_GAM_CCR_RISK_WARNING_HANDLE
(
   oid                  varchar(32) not null,
   collectOid           varchar(32) not null,
   report               varchar(128),
   meeting              varchar(128),
   summary              varchar(512),
   handle               varchar(32) comment '保留风险等级: RETAIN
            风险已处置: REMOVE
            风险降级: REDUCE',
   createTime           timestamp,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_CCR_RISK_WARNING_OPTIONS                        */
/*==============================================================*/
create table T_GAM_CCR_RISK_WARNING_OPTIONS
(
   oid                  varchar(32) not null,
   wariningOid          varchar(32) not null,
   wlevel               varchar(32) comment '低: LOW
            中: MID
            高: HIGH',
   param0               varchar(64),
   param1               varchar(64),
   param2               varchar(64),
   param3               varchar(64),
   seq                  int,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_DICT                                            */
/*==============================================================*/
create table T_GAM_DICT
(
   oid                  varchar(32) not null,
   cate                 varchar(32) comment '枚举值:
            ASSETTYPE: 资产类型
            ACCRUALCYCLE: 付息周期',
   name                 varchar(32),
   rank                 int,
   poid                 varchar(32),
   primary key (oid)
);


INSERT INTO T_GAM_DICT (`oid`, `cate`, `name`, `rank`,poid) VALUES ('A_DEBT_SERVICE_DUE', 'ACCRUALTYPE', '一次性付息', '5',NULL);
INSERT INTO T_GAM_DICT (`oid`, `cate`, `name`, `rank`,poid) VALUES ('EACH_INTEREST_RINCIPAL_DUE', 'ACCRUALTYPE', '按月付息到期还本', '1',NULL);
INSERT INTO T_GAM_DICT (`oid`, `cate`, `name`, `rank`,poid) VALUES ('FIXED-PAYMENT_MORTGAGE', 'ACCRUALTYPE', '等额本息', '2',NULL);
INSERT INTO T_GAM_DICT (`oid`, `cate`, `name`, `rank`,poid) VALUES ('FIXED-BASIS_MORTGAGE', 'ACCRUALTYPE', '等额本金', '3',NULL);


INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ASSETPOOLSTATE_01','ASSETPOOLSTATE','未审核',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ASSETPOOLSTATE_02','ASSETPOOLSTATE','存续期',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ASSETPOOLSTATE_03','ASSETPOOLSTATE','未通过',3,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('CASHTOOLTYPE_01','CASHTOOLTYPE','货币基金',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ESTATEPROPTYPE_01','ESTATEPROPTYPE','商品房开发',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ESTATEPROPTYPE_02','ESTATEPROPTYPE','公建配套设施建设',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ESTATEPROPTYPE_03','ESTATEPROPTYPE','旧房翻新改建',3,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ESTATEPROPTYPE_04','ESTATEPROPTYPE','政府保障房',4,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('ESTATEPROPTYPE_05','ESTATEPROPTYPE','国防军事项目',5,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('PRODUCTTYPE_01','PRODUCTTYPE','封闭式',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('PRODUCTTYPE_02','PRODUCTTYPE','开放式',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('PROJECTTYPE_01','PROJECTTYPE','金融',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('PROJECTTYPE_02','PROJECTTYPE','地产',2,NULL);

INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_05', 'TARGETTYPE', '券商资管计划', 1, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_06', 'TARGETTYPE', '基金/基金子公司资管计划', 2, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_07', 'TARGETTYPE', '保险资管计划', 3, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_04', 'TARGETTYPE', '信托计划-房地产类', 4, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_03', 'TARGETTYPE', '信托计划-政信类', 5, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_12', 'TARGETTYPE', '信托计划-工商企业类', 6, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_13', 'TARGETTYPE', '信托计划-金融产品投资类', 7, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_01', 'TARGETTYPE', '证券类', 8, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_02', 'TARGETTYPE', '股权投资类', 9, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_14', 'TARGETTYPE', '银行理财类', 10, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_16', 'TARGETTYPE', '商票', 11, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_15', 'TARGETTYPE', '银票', 12, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_17', 'TARGETTYPE', '现金贷', 13, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_18', 'TARGETTYPE', '消费分期', 14, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_19', 'TARGETTYPE', '供应链金融产品类', 15, NULL);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_20','TARGETTYPE','房抵贷','16',null);
INSERT INTO T_GAM_DICT(oid, cate, NAME, rank, poid) VALUES ('TARGETTYPE_08', 'TARGETTYPE', '债权及债权收益类', 16, NULL);

INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TARGETHOLDPORPUSH_01','TARGETHOLDPORPUSH','交易性金融资产',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TARGETHOLDPORPUSH_02','TARGETHOLDPORPUSH','可供出售类金融资产',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TARGETHOLDPORPUSH_03','TARGETHOLDPORPUSH','持有至到期投资',3,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TARGETHOLDPORPUSH_04','TARGETHOLDPORPUSH','应收款项类投资',4,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('CASHTOOLHOLDPORPUSH_01','CASHTOOLHOLDPORPUSH','交易性金融资产',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('CASHTOOLHOLDPORPUSH_02','CASHTOOLHOLDPORPUSH','可供出售类金融资产',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('CASHTOOLHOLDPORPUSH_03','CASHTOOLHOLDPORPUSH','持有至到期投资',3,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('CASHTOOLHOLDPORPUSH_04','CASHTOOLHOLDPORPUSH','应收款项类投资',4,NULL);

INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TRANSTYPE_01','TRANSTYPE','债权展期',1,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TRANSTYPE_02','TRANSTYPE','担保代偿',2,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TRANSTYPE_03','TRANSTYPE','保险理赔',3,NULL);
INSERT  INTO `T_GAM_DICT`(`oid`,`cate`,`name`,`rank`,`poid`) VALUES ('TRANSTYPE_04','TRANSTYPE','企业回购',4,NULL);

/*==============================================================*/
/* Table: T_GAM_FILE                                            */
/*==============================================================*/
create table T_GAM_FILE
(
   oid                  varchar(32) not null,
   fkey                 varchar(32) not null comment '文件分组的作用',
   name                 varchar(64) not null,
   furl                 varchar(255) not null,
   size                 int,
   sizeh                varchar(64),
   cate                 varchar(32) comment '枚举:
            USER:用户级
            PLATFORM:平台级',
   state                varchar(32) comment '枚举:
            INVALID:无效
            VALID:有效',
   fversion             varchar(16) comment '用yyyyMMddHHmmss方式表示
            e.g.: 20151205152630
            同时上传的一组文件, 这个值应该相同
            后续可以用这个值区分新增文件',
   operator             varchar(64),
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_INCOME_REWARD                                   */
/*==============================================================*/
create table T_GAM_INCOME_REWARD
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   startDate            int,
   endDate              int,
   ratio                decimal(8,4),
   dratio               decimal(16,12),
   level                varchar(32),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT                                         */
/*==============================================================*/
create table T_GAM_PRODUCT
(
   oid                  varchar(32) not null,
   assetPoolOid         varchar(32),
   spvOid               varchar(32),
   code                 varchar(32),
   name                 varchar(64),
   fullName             varchar(128),
   administrator        varchar(64),
   type                 varchar(32) comment '关联数据字典
            PRODUCTTYPE_01: 封闭式
            PRODUCTTYPE_02: 开放式',
   reveal               varchar(1024),
   revealComment        varchar(512),
   currency             varchar(32) comment '人民币: CNY',
   incomeCalcBasis      varchar(64) comment '365或360计息',
   operationRate        decimal(8,4),
   accrualCycleOid      varchar(32) comment '这个是指几天发一次收益',
   raiseStartDateType   varchar(32) comment '固定时间 MANUALINPUT
            与首次渠道上架时间相同 FIRSTRACKTIME',
   raiseStartDate       date,
   raisePeriodDays      int comment '募集期:()个自然日',
   lockPeriodDays       int comment '锁定期:()个自然日 一旦申购，将冻结此金额T+5天。',
   foundDays            int,
   interestsFirstDays   int comment '起息日:募集满额后()个自然日',
   durationPeriodDays   int comment '存续期:()个自然日',
   expAror              decimal(8,4),
   expArorSec           decimal(8,4),
   basicRatio           decimal(8,4),
   rewardInterest       decimal(8,4),
   netUnitShare         decimal(8,4),
   investMin            decimal(20,4),
   investAdditional     decimal(20,4),
   investMax            decimal(20,4),
   investDateType       varchar(32) comment 'T: 交易日
            D: 自然日',
   minRredeem           decimal(20,4),
   maxRredeem           decimal(20,4),
   additionalRredeem    decimal(20,4),
   rredeemDateType      varchar(32) comment 'T: 交易日
            D: 赎回日',
   netMaxRredeemDay     decimal(20,4),
   dailyNetMaxRredeem   decimal(20,4),
   maxHold              decimal(20,4),
   singleDailyMaxRedeem decimal(20,4),
   accrualRepayDays     int comment '存续期结束后第()个自然日',
   purchaseConfirmDays  int comment '申购确认日:()个',
   redeemConfirmDays    int comment '赎回确认日:()个',
   setupDateType        varchar(32) comment '固定时间 MANUALINPUT
            与首次渠道上架时间相同 FIRSTRACKTIME',
   setupDate            date comment '产品成立时间（存续期开始时间）',
   investComment        varchar(1024),
   instruction          varchar(1024),
   riskLevel            varchar(32) comment 'R1: 谨慎型
            R2: 稳健型
            R3: 平衡型
            R4: 进取型
            R5: 激进型
            ',
   investorLevel        varchar(32) comment 'conservative		保守型
            relativeConservative	相对保守型
            prudent			稳健型
            relativePositive		相对积极型
            positive			积极型',
   fileKeys             varchar(128),
   state                varchar(32) comment 'CREATE:新建，UPDATE：修改，AUDITING：审核中，AUDITFAIL：审核不通过，AUDITPASS：审核通过(复核中)，REVIEWFAIL：复核不通过，REVIEWPASS：复核通过，NOTSTARTRAISE： 未开始募集(定期)，RAISING：募集中(定期)，RAISEEND：募集结束(定期)，RAISEFAIL：募集失败(定期)，NOTSTARTDURATION：存续期未开始(活期)，DURATIONING：存续期(定期活期)，DURATIONEND：存续期结束(定期活期)，CLEARING：清算中(活期定期)，CLEARED：已清算(活期定期)',
   operator             varchar(32),
   raiseEndDate         date,
   raiseFailDate        date,
   durationPeriodEndDate date,
   isDeleted            varchar(32) comment '是: YES
            否: NO',
   auditState           varchar(32) comment '未提交审核 NOCOMMIT
            待审核(已经提交:审核中) AUDITING
            待复核(已经提交复核:复核中) REVIEWING
            待批准(已经提交批准申请:批准申请中) APPROVALING
            批准 APPROVAL
            驳回 REJECT
            
            ',
   raisedTotalNumber    decimal(20,4) comment '只累计申购的份额',
   currentVolume        decimal(20,4),
   previousCurVolume    decimal(20,4),
   previousCurVolumePercent decimal(20,4),
   isPreviousCurVolume  varchar(20),
   collectedVolume      decimal(20,4),
   maxSaleVolume        decimal(20,4) comment '可售份额申请, 通过的, 最高可售份额.',
   lockCollectedVolume  decimal(20,4),
   repayDate            date,
   repayInterestStatus  varchar(32) comment 'toRepay:待付息，repaying：付息中，repayed：已付息',
   repayLoanStatus      varchar(32) comment 'toRepay:待还本，repaying：还本中，repayed：已还本',
   newestProfitConfirmDate date,
   investFileKey        varchar(32),
   serviceFileKey       varchar(32),
   isOpenPurchase       varchar(32) comment 'YES:是，NO:否',
   isOpenRemeed         varchar(32) comment 'YES:是，NO:否',
   purchaseApplyStatus  varchar(32) comment '开启申购申请 : APPLY_ON
            关闭申购申请 : APPLY_OFF
            无: NONE',
   redeemApplyStatus    varchar(32) comment '开启赎回申请 : APPLY_ON
            关闭赎回申请 : APPLY_OFF
            无 : NONE',
   clearingTime         datetime,
   clearedTime          datetime,
   dealStartTime        varchar(6) comment '格式：HHmmss',
   dealEndTime          varchar(6) comment '格式：HHmmss',
   productLabel         varchar(32) comment 'seckill:秒杀，recom:推荐，freshman:新手，experienceFund:体验金,default:默认',
   purchasePeopleNum    int,
   purchaseNum          int,
   singleDayRedeemCount int,
   clearingOperator     varchar(32),
   recPeriodExpAnYield  decimal(20,4),
   raiseFullFoundType   varchar(20),
   autoFoundDays        int,
   overdueStatus        varchar(10) comment 'YES:逾期，NO：没有逾期',
   redeemWithoutInterest varchar(32) comment 'on:开户，off:关闭',
   closingRule          varchar(32),
   incomeDealType       varchar(32) comment 'cash:现金分红,reinvest:结转',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

alter table T_GAM_PRODUCT comment '产品剩余份额	raisedTotalNumber
累计已售份额	collectedVolume
';

/*==============================================================*/
/* Index: IDX_T_GAM_PRODUCT_STATE                               */
/*==============================================================*/
create index IDX_T_GAM_PRODUCT_STATE on T_GAM_PRODUCT
(
   state
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_CHANNEL                                 */
/*==============================================================*/
create table T_GAM_PRODUCT_CHANNEL
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   channelOid           varchar(32) not null,
   orderOid             varchar(32),
   operator             varchar(32),
   marketState          varchar(32),
   rackTime             datetime,
   downTime             datetime,
   status               varchar(32) comment '有效: VALID
            乐视需求: 销售平台回退: BACKOUT',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_CHANNEL_ORDER                           */
/*==============================================================*/
create table T_GAM_PRODUCT_CHANNEL_ORDER
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   channelOid           varchar(32) comment '开启申购
            关闭申购
            开启赎回
            关闭赎回',
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   status               varchar(32) comment '待审核: SUBMIT
            审核通过: PASS
            审核驳回: FAIL
            删除: DELETE',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_INVEST_FILE_ORDER                       */
/*==============================================================*/
create table T_GAM_PRODUCT_INVEST_FILE_ORDER
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   fkey                 varchar(32) comment '开启申购
            关闭申购
            开启赎回
            关闭赎回',
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   status               varchar(32) comment '待审核: SUBMIT
            审核通过: PASS
            审核驳回: FAIL
            删除: DELETE',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_LOG                                     */
/*==============================================================*/
create table T_GAM_PRODUCT_LOG
(
   oid                  varchar(32) not null,
   type                 varchar(32) comment '准入审核: ACCESS
            修改审核: RECTIFY',
   productOid           varchar(32) not null,
   productRectifyOid    varchar(32),
   auditType            varchar(32),
   auditComment         varchar(1024),
   auditState           varchar(32),
   auditor              varchar(32),
   auditTime            varchar(32),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_OPERATING_ORDER                         */
/*==============================================================*/
create table T_GAM_PRODUCT_OPERATING_ORDER
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   type                 varchar(32) comment '开启申购
            关闭申购
            开启赎回
            关闭赎回',
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   status               varchar(32) comment '待审核: SUBMIT
            审核通过: PASS
            审核驳回: FAIL
            删除: DELETE',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_RECTIFY                                 */
/*==============================================================*/
create table T_GAM_PRODUCT_RECTIFY
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   administrator        varchar(64),
   reveal               varchar(1024),
   investComment        varchar(1024),
   instruction          varchar(1024),
   riskLevel            varchar(32),
   files                text,
   state                varchar(32),
   updater              varchar(32),
   operator             varchar(32),
   createTime           datetime,
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_SALE_POSITION_ORDER                     */
/*==============================================================*/
create table T_GAM_PRODUCT_SALE_POSITION_ORDER
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   schedulingOid        varchar(32),
   basicDate            date,
   volume               decimal(16,4) comment '开启申购
            关闭申购
            开启赎回
            关闭赎回',
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   errorMessage         text,
   status               varchar(32) comment '待审核: SUBMIT
            审核通过: PASS
            审核驳回: FAIL
            删除: DELETE
            已生效: ACTIVE
            生效失败: DEACTIVE
            ',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PRODUCT_SALE_SCHEDULING                         */
/*==============================================================*/
create table T_GAM_PRODUCT_SALE_SCHEDULING
(
   oid                  varchar(32) not null,
   productOid           varchar(32) not null,
   basicDate            date,
   auditAmount          decimal(20,4),
   applyAmount          decimal(20,4),
   approvalAmount       decimal(20,4),
   syncTime             datetime,
   createTime           datetime,
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Index: product_sell_scheduling_productOid_basicDate          */
/*==============================================================*/
create unique index product_sell_scheduling_productOid_basicDate on T_GAM_PRODUCT_SALE_SCHEDULING
(
   productOid,
   basicDate
);

/*==============================================================*/
/* Table: T_GAM_SPV_ORDER                                       */
/*==============================================================*/
create table T_GAM_SPV_ORDER
(
   oid                  varchar(32) not null,
   spvOid               varchar(32),
   assetPoolOid         varchar(32),
   holdOid              varchar(32),
   orderCode            varchar(50),
   orderType            varchar(32) comment '申购: INVEST
            赎回: REDEEM
            ',
   orderCate            varchar(32) comment '交易订单: TRADE
            冲账订单: STRIKE',
   orderAmount          decimal(20,2),
   orderDate            date,
   orderVolume          decimal(20,4),
   orderStatus          varchar(32) comment '未确认: SUBMIT
            已确认: CONFIRM
            失效: DISABLE
            清算中: CALCING',
   entryStatus          varchar(32) comment '未入账: NO
            已入账: YES',
   payFee               decimal(20,2),
   creater              varchar(32) comment 'investor:投资，platform:平台，publisher:发行人',
   createTime           datetime,
   auditor              varchar(32),
   completeTime         datetime,
   updateTime           datetime not null,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_ACC_LOG                                       */
/*==============================================================*/
create table T_MONEY_ACC_LOG
(
   oid                  varchar(32) not null,
   interfaceCode        varchar(32),
   interfaceName        varchar(100),
   errorCode            varchar(100),
   errorMessage         longtext,
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   sendObj              longtext,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_ACC_LOG_ERRORCODE                                 */
/*==============================================================*/
create index IDX_ACC_LOG_ERRORCODE on T_MONEY_ACC_LOG
(
   errorCode
);

/*==============================================================*/
/* Table: T_MONEY_ACC_LOG_HIS                                   */
/*==============================================================*/
create table T_MONEY_ACC_LOG_HIS
(
   oid                  varchar(32) not null,
   interfaceCode        varchar(32),
   interfaceName        varchar(100),
   errorCode            varchar(100),
   errorMessage         longtext,
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   sendObj              longtext,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_ACC_LOG_ERRORCODE                                 */
/*==============================================================*/
create index IDX_ACC_LOG_ERRORCODE on T_MONEY_ACC_LOG_HIS
(
   errorCode
);

/*==============================================================*/
/* Table: T_MONEY_CACHE_EXECUTE_LOG                             */
/*==============================================================*/
create table T_MONEY_CACHE_EXECUTE_LOG
(
   oid                  varchar(32) not null,
   batchNo              varchar(100),
   executeCommand       varchar(200),
   hkey                 varchar(200),
   field                varchar(32),
   value                varchar(32),
   backValue            varchar(32),
   errorCommand         varchar(32),
   executeSuccessStatus varchar(32) comment 'false,true',
   executeFailedStatus  varchar(32),
   errorCount           int,
   executeTime          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: CACHE_EXECUTE_LOG_createTime                          */
/*==============================================================*/
create index CACHE_EXECUTE_LOG_createTime on T_MONEY_CACHE_EXECUTE_LOG
(
   createTime
);

/*==============================================================*/
/* Index: IDX_BATCHNO                                           */
/*==============================================================*/
create index IDX_BATCHNO on T_MONEY_CACHE_EXECUTE_LOG
(
   batchNo
);

/*==============================================================*/
/* Table: T_MONEY_CHECK_COMPAREDATA                             */
/*==============================================================*/
create table T_MONEY_CHECK_COMPAREDATA
(
   oid                  varchar(32) not null,
   checkOid             varchar(32),
   orderCode            varchar(32),
   orderType            varchar(32) comment 'invest:申购，redeem: 赎回',
   orderAmount          decimal(22,4),
   orderStatus          varchar(32),
   buzzDate             date,
   orderTime            datetime,
   investorOid          varchar(32),
   checkStatus          varchar(32) comment 'yes:已对账，no:未对账',
   productType          varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: taskStatus                                            */
/*==============================================================*/
create index taskStatus on T_MONEY_CHECK_COMPAREDATA
(
   investorOid
);

/*==============================================================*/
/* Table: T_MONEY_CHECK_COMPAREDATA_RESULT                      */
/*==============================================================*/
create table T_MONEY_CHECK_COMPAREDATA_RESULT
(
   oid                  varchar(32) not null,
   checkOid             varchar(32),
   checkCode            varchar(32),
   orderCode            varchar(32),
   orderType            varchar(32) comment 'invest:申购，redeem: 赎回',
   orderAmount          decimal(22,4),
   orderStatus          varchar(32),
   buzzDate             date,
   investorOid          varchar(32),
   checkOrderCode       varchar(32),
   orderTime            datetime,
   checkOrderType       varchar(32) comment 'invest:申购，redeem: 赎回',
   checkOrderAmount     decimal(22,4),
   checkOrderStatus     varchar(32),
   checkInvestorOid     varchar(32),
   checkStatus          varchar(32) comment '多账：moreThen, 少账：lessThen, 一致：equals，异常：exception',
   dealStatus           varchar(32) comment 'toDeal:待处理，dealing:处理中，dealt:已处理',
   productType          varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: taskStatus                                            */
/*==============================================================*/
create index taskStatus on T_MONEY_CHECK_COMPAREDATA_RESULT
(
   investorOid
);

/*==============================================================*/
/* Table: T_MONEY_CHECK_COMPAREDATA_RESULT_HIS                  */
/*==============================================================*/
create table T_MONEY_CHECK_COMPAREDATA_RESULT_HIS
(
   oid                  varchar(32) not null,
   checkOid             varchar(32),
   orderCode            varchar(32),
   orderType            varchar(32) comment 'invest:申购，redeem: 赎回',
   orderAmount          decimal(22,4),
   orderStatus          varchar(32),
   buzzDate             date,
   investorOid          varchar(32),
   checkOrderCode       varchar(32),
   orderTime            datetime,
   checkOrderType       varchar(32) comment 'invest:申购，redeem: 赎回',
   checkOrderAmount     decimal(22,4),
   checkOrderStatus     varchar(32),
   checkInvestorOid     varchar(32),
   checkStatus          varchar(32) comment '多账：moreThen, 少账：lessThen, 一致：equals，异常：exception',
   dealStatus           varchar(32) comment 'toDeal:待处理，dealing:处理中，dealt:已处理',
   productType          varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: taskStatus                                            */
/*==============================================================*/
create index taskStatus on T_MONEY_CHECK_COMPAREDATA_RESULT_HIS
(
   investorOid
);

/*==============================================================*/
/* Table: T_MONEY_CITY                                          */
/*==============================================================*/
create table T_MONEY_CITY
(
   oid                  varchar(36) not null,
   cityCode             varchar(32),
   cityName             varchar(200),
   cityParentCode       varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "11", "北京", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110101", "东城区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110102", "西城区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110103", "崇文区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110104", "宣武区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110105", "朝阳区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110106", "丰台区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110107", "石景山区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110108", "海淀区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110109", "门头沟区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110111", "房山区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110112", "通州区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110113", "顺义区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110114", "昌平区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110115", "大兴区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110116", "怀柔区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110117", "平谷区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110228", "密云县",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110229", "延庆县",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "110230", "其它区",  "11");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "12", "天津", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120101", "和平区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120102", "河东区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120103", "河西区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120104", "南开区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120105", "河北区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120106", "红桥区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120107", "塘沽区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120108", "汉沽区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120109", "大港区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120110", "东丽区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120111", "西青区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120112", "津南区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120113", "北辰区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120114", "武清区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120115", "宝坻区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120116", "滨海新区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120221", "宁河县",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120223", "静海县",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120225", "蓟县",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "120226", "其它区",  "12");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "13", "河北", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130100", "石家庄市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130200", "唐山市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130300", "秦皇岛市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130400", "邯郸市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130500", "邢台市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130600", "保定市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130700", "张家口市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130800", "承德市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "130900", "沧州市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "131000", "廊坊市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "131100", "衡水市",  "13");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "14", "山西", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140100", "太原市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140200", "大同市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140300", "阳泉市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140400", "长治市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140500", "晋城市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140600", "朔州市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140700", "晋中市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140800", "运城市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "140900", "忻州市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "141000", "临汾市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "141100", "吕梁市",  "14");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "15", "内蒙", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150100", "呼和浩特市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150200", "包头市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150300", "乌海市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150400", "赤峰市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150500", "通辽市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150600", "鄂尔多斯市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150700", "呼伦贝尔市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150800", "巴彦淖尔市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "150900", "乌兰察布市",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "152200", "兴安盟",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "152500", "锡林郭勒盟",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "152900", "阿拉善盟",  "15");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "21", "辽宁", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210100", "沈阳市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210200", "大连市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210300", "鞍山市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210400", "抚顺市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210500", "本溪市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210600", "丹东市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210700", "锦州市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210800", "营口市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "210900", "阜新市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "211000", "辽阳市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "211100", "盘锦市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "211200", "铁岭市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "211300", "朝阳市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "211400", "葫芦岛市",  "21");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "22", "吉林", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220100", "长春市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220200", "吉林市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220300", "四平市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220400", "辽源市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220500", "通化市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220600", "白山市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220700", "松原市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "220800", "白城市",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "222400", "延边朝鲜族自治州",  "22");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "23", "黑龙江", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230100", "哈尔滨市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230200", "齐齐哈尔市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230300", "鸡西市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230400", "鹤岗市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230500", "双鸭山市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230600", "大庆市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230700", "伊春市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230800", "佳木斯市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "230900", "七台河市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "231000", "牡丹江市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "231100", "黑河市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "231200", "绥化市",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "232700", "大兴安岭地区",  "23");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "31", "上海", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310101", "黄浦区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310103", "卢湾区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310104", "徐汇区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310105", "长宁区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310106", "静安区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310107", "普陀区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310108", "闸北区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310109", "虹口区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310110", "杨浦区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310112", "闵行区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310113", "宝山区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310114", "嘉定区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310115", "浦东新区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310116", "金山区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310117", "松江区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310118", "青浦区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310119", "南汇区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310120", "奉贤区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310152", "川沙区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310230", "崇明县",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "310231", "其它区",  "31");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "32", "江苏", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320100", "南京市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320200", "无锡市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320300", "徐州市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320400", "常州市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320500", "苏州市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320600", "南通市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320700", "连云港市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320800", "淮安市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "320900", "盐城市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "321000", "扬州市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "321100", "镇江市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "321200", "泰州市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "321300", "宿迁市",  "32");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "33", "浙江", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330100", "杭州市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330200", "宁波市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330300", "温州市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330400", "嘉兴市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330500", "湖州市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330600", "绍兴市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330700", "金华市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330800", "衢州市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "330900", "舟山市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "331000", "台州市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "331100", "丽水市",  "33");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "34", "安徽", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340100", "合肥市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340200", "芜湖市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340300", "蚌埠市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340400", "淮南市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340500", "马鞍山市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340600", "淮北市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340700", "铜陵市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "340800", "安庆市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341000", "黄山市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341100", "滁州市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341200", "阜阳市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341300", "宿州市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341500", "六安市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341600", "亳州市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341700", "池州市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "341800", "宣城市",  "34");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "35", "福建", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350100", "福州市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350200", "厦门市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350300", "莆田市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350400", "三明市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350500", "泉州市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350600", "漳州市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350700", "南平市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350800", "龙岩市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "350900", "宁德市",  "35");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "36", "江西", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360100", "南昌市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360200", "景德镇市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360300", "萍乡市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360400", "九江市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360500", "新余市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360600", "鹰潭市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360700", "赣州市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360800", "吉安市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "360900", "宜春市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "361000", "抚州市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "361100", "上饶市",  "36");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "37", "山东", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370100", "济南市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370200", "青岛市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370300", "淄博市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370400", "枣庄市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370500", "东营市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370600", "烟台市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370700", "潍坊市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370800", "济宁市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "370900", "泰安市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371000", "威海市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371100", "日照市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371200", "莱芜市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371300", "临沂市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371400", "德州市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371500", "聊城市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371600", "滨州市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "371700", "菏泽市",  "37");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "41", "河南", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410100", "郑州市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410200", "开封市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410300", "洛阳市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410400", "平顶山市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410500", "安阳市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410600", "鹤壁市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410700", "新乡市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410800", "焦作市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410881", "济源市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "410900", "濮阳市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411000", "许昌市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411100", "漯河市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411200", "三门峡市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411300", "南阳市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411400", "商丘市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411500", "信阳市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411600", "周口市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "411700", "驻马店市",  "41");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "42", "湖北", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420100", "武汉市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420200", "黄石市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420300", "十堰市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420500", "宜昌市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420600", "襄阳市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420700", "鄂州市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420800", "荆门市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "420900", "孝感市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "421000", "荆州市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "421100", "黄冈市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "421200", "咸宁市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "421300", "随州市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "422800", "恩施土家族苗族自治州",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "429004", "仙桃市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "429005", "潜江市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "429006", "天门市",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "429021", "神农架林区",  "42");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "43", "湖南", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430100", "长沙市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430200", "株洲市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430300", "湘潭市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430400", "衡阳市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430500", "邵阳市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430600", "岳阳市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430700", "常德市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430800", "张家界市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "430900", "益阳市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "431000", "郴州市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "431100", "永州市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "431200", "怀化市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "431300", "娄底市",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "433100", "湘西土家族苗族自治州",  "43");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "44", "广东", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440100", "广州市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440200", "韶关市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440300", "深圳市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440400", "珠海市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440500", "汕头市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440600", "佛山市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440700", "江门市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440800", "湛江市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "440900", "茂名市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441200", "肇庆市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441300", "惠州市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441400", "梅州市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441500", "汕尾市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441600", "河源市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441700", "阳江市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441800", "清远市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "441900", "东莞市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "442000", "中山市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "445100", "潮州市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "445200", "揭阳市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "445300", "云浮市",  "44");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "45", "广西", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450100", "南宁市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450200", "柳州市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450300", "桂林市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450400", "梧州市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450500", "北海市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450600", "防城港市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450700", "钦州市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450800", "贵港市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "450900", "玉林市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "451000", "百色市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "451100", "贺州市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "451200", "河池市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "451300", "来宾市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "451400", "崇左市",  "45");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "46", "海南", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "460100", "海口市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "460200", "三亚市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469001", "五指山市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469002", "琼海市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469003", "儋州市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469005", "文昌市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469006", "万宁市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469007", "东方市",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469025", "定安县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469026", "屯昌县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469027", "澄迈县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469028", "临高县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469030", "白沙黎族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469031", "昌江黎族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469033", "乐东黎族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469034", "陵水黎族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469035", "保亭黎族苗族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469036", "琼中黎族苗族自治县",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469037", "西沙群岛",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469038", "南沙群岛",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "469039", "中沙群岛的岛礁及其海域",  "46");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "55", "重庆", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500101", "万州区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500102", "涪陵区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500103", "渝中区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500104", "大渡口区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500105", "江北区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500106", "沙坪坝区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500107", "九龙坡区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500108", "南岸区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500109", "北碚区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500110", "万盛区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500111", "双桥区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500112", "渝北区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500113", "巴南区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500114", "黔江区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500115", "长寿区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500222", "綦江县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500223", "潼南县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500224", "铜梁县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500225", "大足县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500226", "荣昌县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500227", "璧山县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500228", "梁平县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500229", "城口县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500230", "丰都县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500231", "垫江县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500232", "武隆县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500233", "忠县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500234", "开县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500235", "云阳县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500236", "奉节县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500237", "巫山县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500238", "巫溪县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500240", "石柱土家族自治县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500241", "秀山土家族苗族自治县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500242", "酉阳土家族苗族自治县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500243", "彭水苗族土家族自治县",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500381", "江津区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500382", "合川区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500383", "永川区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500384", "南川区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "500385", "其它区",  "55");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "51", "四川", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510100", "成都市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510300", "自贡市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510400", "攀枝花市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510500", "泸州市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510600", "德阳市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510700", "绵阳市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510800", "广元市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "510900", "遂宁市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511000", "内江市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511100", "乐山市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511300", "南充市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511400", "眉山市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511500", "宜宾市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511600", "广安市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511700", "达州市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511800", "雅安市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "511900", "巴中市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "512000", "资阳市",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "513200", "阿坝藏族羌族自治州",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "513300", "甘孜藏族自治州",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "513400", "凉山彝族自治州",  "51");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "52", "贵州", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "520100", "贵阳市",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "520200", "六盘水市",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "520300", "遵义市",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "520400", "安顺市",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "522200", "铜仁地区",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "522300", "黔西南布依族苗族自治州",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "522400", "毕节地区",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "522600", "黔东南苗族侗族自治州",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "522700", "黔南布依族苗族自治州",  "52");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "53", "云南", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530100", "昆明市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530300", "曲靖市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530400", "玉溪市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530500", "保山市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530600", "昭通市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530700", "丽江市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530800", "普洱市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "530900", "临沧市",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "532300", "楚雄彝族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "532500", "红河哈尼族彝族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "532600", "文山壮族苗族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "532800", "西双版纳傣族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "532900", "大理白族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "533100", "德宏傣族景颇族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "533300", "怒江傈僳族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "533400", "迪庆藏族自治州",  "53");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "54", "西藏", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "540100", "拉萨市",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542100", "昌都地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542200", "山南地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542300", "日喀则地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542400", "那曲地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542500", "阿里地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "542600", "林芝地区",  "54");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "61", "陕西", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610100", "西安市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610200", "铜川市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610300", "宝鸡市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610400", "咸阳市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610500", "渭南市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610600", "延安市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610700", "汉中市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610800", "榆林市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "610900", "安康市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "611000", "商洛市",  "61");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "62", "甘肃", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620100", "兰州市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620200", "嘉峪关市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620300", "金昌市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620400", "白银市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620500", "天水市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620600", "武威市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620700", "张掖市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620800", "平凉市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "620900", "酒泉市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "621000", "庆阳市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "621100", "定西市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "621200", "陇南市",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "622900", "临夏回族自治州",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "623000", "甘南藏族自治州",  "62");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "63", "青海", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "630100", "西宁市",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632100", "海东地区",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632200", "海北藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632300", "黄南藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632500", "海南藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632600", "果洛藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632700", "玉树藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "632800", "海西蒙古族藏族自治州",  "63");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "64", "宁夏", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "640100", "银川市",  "64");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "640200", "石嘴山市",  "64");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "640300", "吴忠市",  "64");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "640400", "固原市",  "64");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "640500", "中卫市",  "64");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "65", "新疆", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "650100", "乌鲁木齐市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "650200", "克拉玛依市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652100", "吐鲁番地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652200", "哈密地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652300", "昌吉回族自治州",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652700", "博尔塔拉蒙古自治州",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652800", "巴音郭楞蒙古自治州",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "652900", "阿克苏地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "653000", "克孜勒苏柯尔克孜自治州",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "653100", "喀什地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "653200", "和田地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "654000", "伊犁哈萨克自治州",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "654200", "塔城地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "654300", "阿勒泰地区",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "659001", "石河子市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "659002", "阿拉尔市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "659003", "图木舒克市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "659004", "五家渠市",  "65");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "71", "台湾", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710100", "台北市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710200", "高雄市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710300", "台南市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710400", "台中市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710500", "金门县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710600", "南投县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710700", "基隆市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710800", "新竹市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "710900", "嘉义市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711100", "新北市",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711200", "宜兰县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711300", "新竹县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711400", "桃园县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711500", "苗栗县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711700", "彰化县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "711900", "嘉义县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "712100", "云林县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "712400", "屏东县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "712500", "台东县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "712600", "花莲县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "712700", "澎湖县",  "71");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "72", "香港", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "810100", "香港岛",  "72");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "810200", "九龙",  "72");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "810300", "新界",  "72");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "73", "澳门", "");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "820100", "澳门半岛",  "73");
INSERT INTO T_MONEY_CITY(oid, cityCode, cityName, cityParentCode) VALUES (UUID(), "820200", "离岛",  "73");

/*==============================================================*/
/* Table: T_MONEY_CORPORATE                                     */
/*==============================================================*/
create table T_MONEY_CORPORATE
(
   oid                  varchar(32) not null comment '请求审核订单号',
   account              varchar(32),
   auditOrderNo         varchar(32),
   identityId           varchar(32),
   identityType         varchar(16),
   memberType           varchar(4),
   name                 varchar(32),
   phonetic             varchar(64),
   companyName          varchar(128),
   logo                 varchar(255),
   website              varchar(128),
   address              varchar(128),
   licenseNo            varchar(64),
   licenseAddress       varchar(64),
   licenseExpireDate    varchar(16),
   businessScope        varchar(256),
   contact              varchar(32),
   telephone            varchar(32),
   email                varchar(64),
   organizationNo       varchar(32),
   summary              varchar(512),
   legalPerson          varchar(64),
   certNo               varchar(32),
   certType             varchar(32),
   legalPersonPhone     varchar(32),
   bankCode             varchar(32),
   bankAccountNo        varchar(64),
   cardId               varchar(32),
   cardType             varchar(16),
   cardAttribute        varchar(16),
   province             varchar(128),
   city                 varchar(128),
   bankBranch           varchar(256),
   fileName             varchar(64),
   digest               varchar(64),
   digestType           varchar(16),
   extendParam          varchar(256),
   auditStatus          int comment '1:审核中; 2:成功; 3:失败;',
   auditMessage         varchar(256),
   status               int comment '1:启用; 2:锁定;',
   operator             varchar(32),
   updateTime           datetime,
   createTime           datetime,
   isOpen               varchar(20),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_COUPON_LOG                                    */
/*==============================================================*/
create table T_MONEY_COUPON_LOG
(
   oid                  varchar(32) not null,
   status               varchar(32) comment 'success:成功,failed:失败',
   type                 varchar(32) comment 'register:注册,referee:推荐人',
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   userOid              varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_cl_createTime                                     */
/*==============================================================*/
create index IDX_cl_createTime on T_MONEY_COUPON_LOG
(
   createTime
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BANK                                 */
/*==============================================================*/
create table T_MONEY_INVESTOR_BANK
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   name                 varchar(32),
   idCard               varchar(32),
   bankName             varchar(50),
   debitCard            varchar(32),
   phoneNo              varchar(15),
   bindStatus           varchar(32) comment 'ok:绑卡成功 no:未绑卡成功',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BANK_investorOid                         */
/*==============================================================*/
create unique index IDX_INVESTOR_BANK_investorOid on T_MONEY_INVESTOR_BANK
(
   investorOid
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BANK_idCard                              */
/*==============================================================*/
create index IDX_INVESTOR_BANK_idCard on T_MONEY_INVESTOR_BANK
(
   idCard
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BANK_debitCard                           */
/*==============================================================*/
create index IDX_INVESTOR_BANK_debitCard on T_MONEY_INVESTOR_BANK
(
   debitCard
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BANKORDER                            */
/*==============================================================*/
create table T_MONEY_INVESTOR_BANKORDER
(
   oid                  varchar(32) not null,
   investorOid          varchar(32) not null,
   orderCode            varchar(50),
   orderType            varchar(30) comment 'deposit;充值; withdraw.提现.',
   feePayer             varchar(20) comment 'platform:平台,user:用户',
   fee                  decimal(20,2),
   orderAmount          decimal(20,2),
   orderStatus          varchar(30) comment 'submitted:已申请，submitFailed:申请失败，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功',
   completeTime         datetime,
   orderTime            datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BANKORDER_createTime                     */
/*==============================================================*/
create index IDX_INVESTOR_BANKORDER_createTime on T_MONEY_INVESTOR_BANKORDER
(
   createTime
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BANK_HIS                             */
/*==============================================================*/
create table T_MONEY_INVESTOR_BANK_HIS
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   name                 varchar(32),
   idNumb               varchar(32),
   bankName             varchar(64),
   cardNumb             varchar(32),
   phoneNo              varchar(32),
   operator             varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BANK_HIS_investorOid                     */
/*==============================================================*/
create index IDX_INVESTOR_BANK_HIS_investorOid on T_MONEY_INVESTOR_BANK_HIS
(
   investorOid
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BASEACCOUNT                          */
/*==============================================================*/
create table T_MONEY_INVESTOR_BASEACCOUNT
(
   oid                  varchar(32) not null,
   memberId             varchar(32),
   phoneNum             varchar(32),
   realName             varchar(50),
   idNum                varchar(32),
   uid                  varchar(32),
   status               varchar(32) comment 'normal:正常; forbidden:禁用;',
   balance              decimal(20,2),
   owner                varchar(32) comment 'investor:投资者，platform:平台',
   isFreshman           varchar(32) comment '是：yes，否：no',
   userPwd              varchar(64),
   salt                 varchar(32),
   payPwd               varchar(64),
   paySalt              varchar(32),
   source               varchar(32) comment 'backEnd:后台添加，frontEnd:前台注册',
   channelid            varchar(200),
   withdrawFrozenBalance decimal(20,2),
   rechargeFrozenBalance decimal(20,2),
   applyAvailableBalance decimal(20,2),
   withdrawAvailableBalance decimal(20,2),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

INSERT INTO T_MONEY_INVESTOR_BASEACCOUNT(oid, phoneNum, realName, status, balance, owner, source, withdrawFrozenBalance, rechargeFrozenBalance, applyAvailableBalance, withdrawAvailableBalance, updateTime, createTime) VALUES('superaccount', '13888888888', '超级买卖单户', 'normal', '0', 'platform', 'backEnd', '0', '0','0', '0', NOW(), NOW());

/*==============================================================*/
/* Index: IDX_INVESTOR_BASEACCOUNT_phoneNum                     */
/*==============================================================*/
create unique index IDX_INVESTOR_BASEACCOUNT_phoneNum on T_MONEY_INVESTOR_BASEACCOUNT
(
   phoneNum
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BASEACCOUNT_memberId                     */
/*==============================================================*/
create index IDX_INVESTOR_BASEACCOUNT_memberId on T_MONEY_INVESTOR_BASEACCOUNT
(
   memberId
);

/*==============================================================*/
/* Index: IDX_INVESTOR_BASEACCOUNT_uid                          */
/*==============================================================*/
create unique index IDX_INVESTOR_BASEACCOUNT_uid on T_MONEY_INVESTOR_BASEACCOUNT
(
   uid
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BASEACCOUNT_REFEREE                  */
/*==============================================================*/
create table T_MONEY_INVESTOR_BASEACCOUNT_REFEREE
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   referRegAmount       int,
   yesterdayRecommenders int,
   referPurchasePeopleAmount int,
   referPurchaseMoneyVolume decimal(20,4),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_BASEACCOUNT_REFER_DETAILS            */
/*==============================================================*/
create table T_MONEY_INVESTOR_BASEACCOUNT_REFER_DETAILS
(
   oid                  varchar(32) not null,
   refereeOid           varchar(32),
   investorOid          varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_CASHFLOW                             */
/*==============================================================*/
create table T_MONEY_INVESTOR_CASHFLOW
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   tradeOrderOid        varchar(32),
   bankOrderOid         varchar(32),
   couponOrderOid       varchar(32),
   orderOid             VARCHAR(32),
   tradeAmount          decimal(20,4),
   tradeType            varchar(30) comment 'invest:投资，fastRedeem:快赎，normalRedeem:普赎，clearRedeem:清盘普赎，refund:退款，cash:还本/付息，repayLoan:还本，repayInterest:付息，buy:买卖，fee:手续费，deposit;充值; withdraw:提现',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_CHECK                                */
/*==============================================================*/
create table T_MONEY_INVESTOR_CHECK
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   moneyAmount          decimal(20,2),
   capitalAmount        decimal(20,2),
   recharge             decimal(20,2),
   withdraw             decimal(20,2),
   tnInterest           decimal(20,2),
   t0Interest           decimal(20,2),
   couponAmt            decimal(20,2),
   yesterdayCapitalAmt  decimal(20,2),
   balance              decimal(20,2),
   applyBalance         decimal(20,2),
   t0ApplyAmt           decimal(20,2),
   t0HoldAmt            decimal(20,2),
   tnApplyAmt           decimal(20,2),
   tnHoldlAmt           decimal(20,2),
   allRecorrectAmt      decimal(20,2),
   checkStatus          varchar(32) comment '对账成功:ok 对账失败:failed 对账忽略:ignore',
   userStatus           varchar(32) comment '锁定:isLock 未锁定:isOk',
   checkTime            varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_COUPONORDER                          */
/*==============================================================*/
create table T_MONEY_INVESTOR_COUPONORDER
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   channelOid           varchar(32),
   orderCode            varchar(32),
   coupons              varchar(32),
   couponType           varchar(32),
   couponAmount         decimal(20,2),
   orderStatus          varchar(32) comment 'submitted:已提交，submitFailed:提交失败，toPay:待支付，payFailed:支付失败,done:支付成功',
   orderTime            datetime,
   completeTime         datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_TULIP_LOG_ERRORCODE                               */
/*==============================================================*/
create index IDX_TULIP_LOG_ERRORCODE on T_MONEY_INVESTOR_COUPONORDER
(
   orderCode
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_DEPOSIT_APPLY                        */
/*==============================================================*/
create table T_MONEY_INVESTOR_DEPOSIT_APPLY
(
   oid                  varchar(32) not null,
   investorOid          varchar(32) not null,
   orderAmount          decimal(20,2),
   sendObj              text,
   errorCode            int comment 'submitted:已申请，submitFailed:申请失败，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功',
   errorMessage         text,
   payNo                varchar(100),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_DEPOSIT_APPLY_investorOid                         */
/*==============================================================*/
create index IDX_DEPOSIT_APPLY_investorOid on T_MONEY_INVESTOR_DEPOSIT_APPLY
(
   investorOid
);

/*==============================================================*/
/* Index: IDX_DEPOSIT_APPLY_payNo                               */
/*==============================================================*/
create index IDX_DEPOSIT_APPLY_payNo on T_MONEY_INVESTOR_DEPOSIT_APPLY
(
   payNo
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_DETAIL_CHECK                         */
/*==============================================================*/
create table T_MONEY_INVESTOR_DETAIL_CHECK
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   balance              decimal(20,2),
   recorrectBalance     decimal(20,2),
   checkStatus          varchar(32) comment '对账成功:ok 对账失败:failed',
   checkTime            varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_RECORRECT                            */
/*==============================================================*/
create table T_MONEY_INVESTOR_RECORRECT
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   recorrectDirection   varchar(32) comment 'add:入款 reduce:出款',
   recorrectAmt         decimal(20,2),
   recorrectType        varchar(32) comment '账户余额:balance 资金明细变动:orderDetailChange',
   orderType            varchar(32) comment 'recharge:充值 withdraw:提现 buy:申购 redeem:赎回 intConfirm:收益确认',
   recorrectReason      varchar(256),
   doCheckType          varchar(32) comment 'platform:平台创建 man:手工创建',
   preCapitalAmt        decimal(20,2),
   operator             varchar(32),
   checkTime            varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_STATISTICS                           */
/*==============================================================*/
create table T_MONEY_INVESTOR_STATISTICS
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   totalDepositAmount   decimal(20,4),
   totalWithdrawAmount  decimal(20,4),
   totalInvestAmount    decimal(20,4),
   totalRedeemAmount    decimal(20,4),
   totalIncomeAmount    decimal(20,4),
   totalRepayLoan       decimal(20,4),
   t0YesterdayIncome    decimal(20,4),
   tnTotalIncome        decimal(20,4),
   t0TotalIncome        decimal(20,4),
   t0CapitalAmount      decimal(20,4),
   tnCapitalAmount      decimal(20,4),
   experienceCouponAmount decimal(20,4),
   totalInvestProducts  int,
   totalDepositCount    int,
   totalWithdrawCount   int,
   totalInvestCount     int,
   totalRedeemCount     int,
   todayDepositCount    int,
   todayWithdrawCount   int,
   todayInvestCount     int,
   todayRedeemCount     int,
   todayDepositAmount   decimal(20,4),
   todayWithdrawAmount  decimal(20,4),
   todayInvestAmount    decimal(20,4),
   todayRedeemAmount    decimal(20,4),
   monthWithdrawCount   int,
   firstInvestTime      datetime,
   incomeConfirmDate    date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_INVESTOR_STATISTICS` (`oid`, `investorOid`, `totalDepositAmount`, `totalWithdrawAmount`, `totalInvestAmount`, `totalRedeemAmount`, `totalIncomeAmount`, `totalRepayLoan`, `t0YesterdayIncome`, `tnTotalIncome`, `t0TotalIncome`, `t0CapitalAmount`, `tnCapitalAmount`, `totalInvestProducts`, `totalDepositCount`, `totalWithdrawCount`, `totalInvestCount`, `totalRedeemCount`, `todayDepositCount`, `todayWithdrawCount`, `todayInvestCount`, `todayRedeemCount`, `firstInvestTime`, `incomeConfirmDate`, `updateTime`, `createTime`, `todayDepositAmount`, `todayWithdrawAmount`, `todayInvestAmount`, `todayRedeemAmount`) values('superaccount','superaccount','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0','0','0','0','0','0','0','0','0',NULL,'2016-08-31','2016-09-01 10:46:58','2016-08-20 14:04:57','0.0000','0.0000','0.0000','0.0000');

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_TRADEORDER                           */
/*==============================================================*/
create table T_MONEY_INVESTOR_TRADEORDER
(
   oid                  varchar(32) not null,
   investorOid          varchar(32) not null,
   publisherOid         varchar(32),
   productOid           varchar(32),
   channelOid           varchar(32),
   investorOffsetOid    varchar(32),
   publisherOffsetOid   varchar(32),
   checkOid             varchar(32),
   holdOid              varchar(32),
   orderCode            varchar(50),
   orderType            varchar(32) comment 'invest:投资，fastRedeem:快赎，normalRedeem:普赎，clearRedeem:清盘普赎，refund:退款，cash:还本/付息，repayLoan:还本，repayInterest:付息，buy:买卖,writeOff:冲销,cashFailed:募集失败退款,expGoldInvest:体验金投资,reInvest:补投资单,noPayInvest:活转定,expGoldRedeem:体验金赎回,reRedeem:补赎回单
            ',
   orderAmount          decimal(20,2),
   orderVolume          decimal(20,4),
   payAmount            decimal(20,4),
   usedCoupons          varchar(32) comment 'yes:已使用，no:未使用',
   payStatus            varchar(32) comment 'toPay:待支付，submitFailed:申请失败，payFailed:支付失败，paySuccess:支付成功，payExpired:支付超时',
   acceptStatus         varchar(32) comment 'toAccept:待受理，accepted:已受理,acceptFailed:受理失败',
   refundStatus         varchar(32) comment 'toRefund:待退款，refunding:退款中，refunded:已退款，refundFailed:退款失败',
   orderStatus          varchar(32) comment 'submitted:已申请，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功，payExpired:支付超时，accepted:已受理，confirmed:份额已确认，done:交易成功，refunded:已退款，abandoned:已作废',
   checkStatus          varchar(32) comment 'yes:已对，no:未对',
   contractStatus       varchar(32) comment ' toHtml：待生成html，htmlOk：已生成html， htmlFail：生成html失败， pdfOK:已生成pdf',
   createMan            varchar(32) comment 'investor:投资，platform:平台，publisher:发行人',
   orderTime            datetime,
   completeTime         datetime,
   publisherClearStatus varchar(32) comment 'toClear:待清算，clearing:清算中，cleared:已清算,clearFailed:清算失败',
   publisherConfirmStatus varchar(32) comment 'toConfirm:待交收，confirming:交收中，confirmed:已交收，confirmFailed:交收失败',
   publisherCloseStatus varchar(32) comment 'toClose:待结算，closing:结算中，closed:已结算，closeFailed:结算支付失败',
   investorClearStatus  varchar(32) comment 'toClear:待清算，clearing:清算中，cleared:已清算,clearFailed:清算失败',
   investorCloseStatus  varchar(32) comment 'toClose:待结算，closing:结算中，closed:已结算，closeFailed:结算失败',
   holdVolume           decimal(20,8),
   redeemStatus         varchar(32) comment 'yes:可以，no:不可以',
   accrualStatus        varchar(32),
   beginAccuralDate     date,
   corpusAccrualEndDate date,
   beginRedeemDate      date,
   totalIncome          decimal(20,8),
   totalBaseIncome      decimal(20,8),
   totalRewardIncome    decimal(20,8),
   totalCouponIncome    decimal(20,8),
   yesterdayBaseIncome  decimal(20,8),
   yesterdayRewardIncome decimal(20,8),
   yesterdayIncome      decimal(20,8),
   yesterdayCouponIncome decimal(20,8),
   remainderBaseIncome  decimal(20,8),
   remainderRewardIncome decimal(20,8),
   remainderCouponIncome decimal(20,8),
   toConfirmIncome      decimal(20,8),
   incomeAmount         decimal(20,8),
   expectIncomeExt      decimal(20,8),
   expectIncome         decimal(20,8),
   value                decimal(20,8),
   confirmDate          date,
   holdStatus           varchar(32) comment 'toConfirm:待确认，holding:持有中，expired:已到期，partHolding:部分持有，closed:已结算,abandoned:  已作废',
   province             varchar(32),
   city                 varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_orderCode                              */
/*==============================================================*/
create unique index IDX_TRADEORDER_orderCode on T_MONEY_INVESTOR_TRADEORDER
(
   orderCode
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_pub_ostat_offset                       */
/*==============================================================*/
create index IDX_TRADEORDER_pub_ostat_offset on T_MONEY_INVESTOR_TRADEORDER
(
   publisherOid
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_investor_orderType                     */
/*==============================================================*/
create index IDX_TRADEORDER_investor_orderType on T_MONEY_INVESTOR_TRADEORDER
(
   investorOid,
   orderType
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_tradeOrder_uTime                       */
/*==============================================================*/
create index IDX_TRADEORDER_tradeOrder_uTime on T_MONEY_INVESTOR_TRADEORDER
(
   updateTime
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_prod_type_chan_oStat                   */
/*==============================================================*/
create index IDX_TRADEORDER_prod_type_chan_oStat on T_MONEY_INVESTOR_TRADEORDER
(
   productOid,
   channelOid,
   orderType
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_INVESTOR_TRADEORDER_orderTime          */
/*==============================================================*/
create index IDX_TRADEORDER_INVESTOR_TRADEORDER_orderTime on T_MONEY_INVESTOR_TRADEORDER
(
   orderTime
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_holdStatus                             */
/*==============================================================*/
create index IDX_TRADEORDER_holdStatus on T_MONEY_INVESTOR_TRADEORDER
(
   holdStatus
);

/*==============================================================*/
/* Index: IDX_TRADEORDER_holdOid_baDate                         */
/*==============================================================*/
create index IDX_TRADEORDER_holdOid_baDate on T_MONEY_INVESTOR_TRADEORDER
(
   holdOid,
   beginAccuralDate
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_TRADEORDER_ACCEPT                    */
/*==============================================================*/
create table T_MONEY_INVESTOR_TRADEORDER_ACCEPT
(
   oid                  int not null auto_increment,
   cid                  varchar(32),
   ckey                 varchar(32),
   reqid                varchar(32),
   investorOid          varchar(32),
   tradeOrderOid        varchar(32),
   productOid           varchar(100),
   orderAmount          decimal(20,2),
   orderTime            bigint,
   dealStatus           varchar(32) comment 'no:待处理，yes:已处理',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: idx_order_dealStatus_oTime                            */
/*==============================================================*/
create index idx_order_dealStatus_oTime on T_MONEY_INVESTOR_TRADEORDER_ACCEPT
(
   dealStatus,
   orderTime
);

/*==============================================================*/
/* Table: T_MONEY_INVESTOR_TRADEORDER_COUPON                    */
/*==============================================================*/
create table T_MONEY_INVESTOR_TRADEORDER_COUPON
(
   oid                  varchar(32) not null,
   investorOid          varchar(32),
   orderOid             varchar(32),
   couponAmount         decimal(20,4),
   coupons              varchar(300),
   couponType           varchar(32),
   additionalInterestRate decimal(10,4),
   affectiveDays        int,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_ORDER_COUPON_orderOid                             */
/*==============================================================*/
create index IDX_ORDER_COUPON_orderOid on T_MONEY_INVESTOR_TRADEORDER_COUPON
(
   orderOid
);

/*==============================================================*/
/* Index: IDX_ORDER_COUPON_investor_cType                       */
/*==============================================================*/
create index IDX_ORDER_COUPON_investor_cType on T_MONEY_INVESTOR_TRADEORDER_COUPON
(
   investorOid,
   couponType
);

/*==============================================================*/
/* Index: IDX_ORDER_COUPON_investor_order_cType                 */
/*==============================================================*/
create index IDX_ORDER_COUPON_investor_order_cType on T_MONEY_INVESTOR_TRADEORDER_COUPON
(
   investorOid,
   orderOid,
   couponType
);

/*==============================================================*/
/* Table: T_MONEY_JOB                                           */
/*==============================================================*/
create table T_MONEY_JOB
(
   oid                  varchar(32) not null,
   jobId                varchar(100),
   batchCode            varchar(100),
   batchStartTime       datetime,
   batchEndTime         datetime,
   workerPid            varchar(100),
   jobStatus            varchar(100),
   jobMessage           text,
   jobError             varchar(100),
   offsetOid            varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_JOB_LOCK                                      */
/*==============================================================*/
create table T_MONEY_JOB_LOCK
(
   oid                  varchar(32) not null,
   jobId                varchar(200),
   jobTime              varchar(200),
   jobStatus            varchar(12),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: jobId                                                 */
/*==============================================================*/
create unique index jobId on T_MONEY_JOB_LOCK
(
   jobId
);

/*==============================================================*/
/* Table: T_MONEY_JOB_LOG                                       */
/*==============================================================*/
create table T_MONEY_JOB_LOG
(
   oid                  varchar(32) not null,
   jobId                varchar(200),
   batchStartTime       datetime,
   batchEndTime         datetime,
   jobStatus            varchar(12),
   jobMessage           text,
   machineIp            varchar(200),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_LX_SERFEE                                     */
/*==============================================================*/
create table T_MONEY_LX_SERFEE
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   productOid           varchar(32),
   totalVolume          text,
   tDay                 date,
   fee                  text,
   feePercent           varchar(32) comment 'toConfirm:待确认，confirmed:已确认',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_MSG_LOG                                       */
/*==============================================================*/
create table T_MONEY_MSG_LOG
(
   oid                  varchar(32) not null,
   interfaceCode        varchar(32),
   interfaceName        varchar(100),
   errorCode            varchar(100),
   errorMessage         longtext,
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   sendObj              longtext,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_MSG_LOG_ERRORCODE                                 */
/*==============================================================*/
create index IDX_MSG_LOG_ERRORCODE on T_MONEY_MSG_LOG
(
   errorCode
);

/*==============================================================*/
/* Table: T_MONEY_ORDER_ABANDONLOG                              */
/*==============================================================*/
create table T_MONEY_ORDER_ABANDONLOG
(
   oid                  varchar(32) not null,
   originalOrderCode    varchar(32),
   refundOrderCode      varchar(32),
   remark               varchar(500),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_ORDER_LOG                                     */
/*==============================================================*/
create table T_MONEY_ORDER_LOG
(
   oid                  varchar(32) not null,
   tradeOrderOid        varchar(32),
   orderType            varchar(32) comment 'invest:投资，normalRedeem:普通赎回, investClose:  投资平仓',
   orderStatus          varchar(32) comment 'accepted:成功受理，refused:已拒绝, confirmed:投资份额确认, done:结算完成,closed:订单完全赎回,partHolding:订单部分赎回,abandoned:已作废',
   referredOrderCode    varchar(32),
   referredOrderAmount  decimal(20,4),
   errorCode            int,
   errorMessage         text,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: orderCode                                             */
/*==============================================================*/
create index orderCode on T_MONEY_ORDER_LOG
(
   tradeOrderOid
);

/*==============================================================*/
/* Table: T_MONEY_PAYMENT_LOG                                   */
/*==============================================================*/
create table T_MONEY_PAYMENT_LOG
(
   oid                  varchar(32) not null,
   notifyId             varchar(50),
   paymentStatus        varchar(30) comment 'success:成功; failure:失败;',
   batchPaymentNum      varchar(50),
   orderCode            varchar(50),
   paymentCallbackTradeNum varchar(50),
   responseCode         varchar(50),
   responseMessage      text,
   handleType           varchar(30) comment 'appyCall:调用申请; callback:回调',
   paymentSummary       varchar(500),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: index_notifyId                                        */
/*==============================================================*/
create index index_notifyId on T_MONEY_PAYMENT_LOG
(
   notifyId
);

/*==============================================================*/
/* Index: Index_paymentNum                                      */
/*==============================================================*/
create index Index_paymentNum on T_MONEY_PAYMENT_LOG
(
   orderCode
);

/*==============================================================*/
/* Table: T_MONEY_PAY_LOG                                       */
/*==============================================================*/
create table T_MONEY_PAY_LOG
(
   oid                  varchar(32) not null,
   interfaceName        varchar(100),
   orderCode            varchar(100),
   handleType           varchar(100),
   errorCode            int,
   errorMessage         text,
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   content              text,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_ACCOUNTINGNOTIFY                     */
/*==============================================================*/
create table T_MONEY_PLATFORM_ACCOUNTINGNOTIFY
(
   oid                  varchar(32) not null,
   notifyId             varchar(32),
   notifyType           varchar(32),
   notifyContent        text,
   errorCode            varchar(32),
   errorMessage         text,
   notifyStatus         varchar(32) comment 'toConfirm:待确认，confirmed:已确认',
   notifyTime           datetime,
   notifyConfirmedTime  datetime,
   notifyTimes          int,
   seqId                int,
   busDate              date,
   productOid           varchar(32),
   channelOid           varchar(32),
   costFee              decimal(16,2),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: notifyId                                              */
/*==============================================================*/
create unique index notifyId on T_MONEY_PLATFORM_ACCOUNTINGNOTIFY
(
   notifyId
);

/*==============================================================*/
/* Index: nStat_nTimes                                          */
/*==============================================================*/
create index nStat_nTimes on T_MONEY_PLATFORM_ACCOUNTINGNOTIFY
(
   notifyStatus,
   notifyTimes
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_BASEACCOUNT                          */
/*==============================================================*/
create table T_MONEY_PLATFORM_BASEACCOUNT
(
   oid                  varchar(32) not null,
   platformUid          varchar(32),
   balance              decimal(20,4),
   superAccBorrowAmount decimal(20,4),
   status               varchar(32) not null comment 'normal:正常; forbiden:禁用;',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_PLATFORM_BASEACCOUNT` (`oid`, `platformUid`, `balance`, `status`, `updateTime`, `createTime`, `superAccBorrowAmount`) values('platformoid', NULL,'0','normal','2016-09-01 17:20:48','2016-08-15 18:30:29','0');

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_CHANNEL                              */
/*==============================================================*/
create table T_MONEY_PLATFORM_CHANNEL
(
   oid                  varchar(32) not null,
   channelCode          varchar(32),
   channelName          varchar(200),
   cid                  varchar(128),
   ckey                 varchar(128),
   joinType             varchar(32) comment 'pc:pc，app:app，wx:wx',
   partner              varchar(50),
   channelContactName   varchar(32),
   channelEmail         varchar(32),
   channelPhone         varchar(32),
   channelAddress       varchar(32),
   channelStatus        varchar(32) comment 'on:已启用，off:已停用',
   channelId            varchar(32),
   channelFee           decimal(20,4),
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   deleteStatus         varchar(32) comment 'yes:已删除，no:正常',
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Index: cid_ckey                                              */
/*==============================================================*/
create unique index cid_ckey on T_MONEY_PLATFORM_CHANNEL
(
   cid,
   ckey
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_CHANNEL_APPROVAL                     */
/*==============================================================*/
create table T_MONEY_PLATFORM_CHANNEL_APPROVAL
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   channelName          varchar(200),
   channelApproveCode   varchar(32),
   requestType          varchar(32) comment 'on:申请开启，off:申请关闭',
   requester            varchar(32),
   requestTime          datetime,
   approveMan           varchar(32),
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   remark               text,
   updateTime           datetime,
   createTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_CHANNEL_STATISTICS                   */
/*==============================================================*/
create table T_MONEY_PLATFORM_CHANNEL_STATISTICS
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   todayInvestAmount    decimal(20,4),
   todayRedeemAmount    decimal(20,4),
   todayCashAmount      decimal(20,4),
   todayCashFailedAmount decimal(20,4),
   totalCashFailedAmount decimal(20,4),
   totalInvestAmount    decimal(20,4),
   totalRedeemAmount    decimal(20,4),
   totalCashAmount      decimal(20,4),
   investDate           date,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_PLATFORM_CHANNEL_STATISTICS_investDate            */
/*==============================================================*/
create index IDX_PLATFORM_CHANNEL_STATISTICS_investDate on T_MONEY_PLATFORM_CHANNEL_STATISTICS
(
   investDate
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_ERRORLOG                             */
/*==============================================================*/
create table T_MONEY_PLATFORM_ERRORLOG
(
   oid                  varchar(32) not null,
   uid                  varchar(200),
   reqUri               varchar(1000),
   params               text,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_FINANCE_CHECK                        */
/*==============================================================*/
create table T_MONEY_PLATFORM_FINANCE_CHECK
(
   oid                  varchar(32) not null,
   checkCode            varchar(32) not null,
   checkDate            date not null,
   checkStatus          varchar(32) comment 'toCheck:待对账,checking:对账中,checkSuccess:对账成功,checkFailed:对账失败',
   confirmStatus        varchar(32) comment 'yes:已确认，no:未确认',
   totalCount           int,
   wrongCount           int,
   checkDataSyncStatus  varchar(32) comment 'syncOk:已同步，toSync:待同步,syncing:同步中,syncFailed:同步失败',
   operator             varchar(32),
   beginTime            datetime,
   endTime              datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   unique key unique_checkCode (checkCode)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_FINANCE_MODIFYORDER                  */
/*==============================================================*/
create table T_MONEY_PLATFORM_FINANCE_MODIFYORDER
(
   oid                  varchar(32) not null,
   checkOid             varchar(32),
   productOid           varchar(32),
   investorOid          varchar(32),
   resultOid            varchar(32),
   orderCode            varchar(32),
   orderTime            datetime,
   tradeType            varchar(32) comment 'buy: 申购，redeem:赎回',
   orderAmount          decimal(20,2),
   opType               varchar(32) comment '补单：fixOrder，废单平仓：discardHold，废单退款：discardRefund，退款：refund',
   premodifyStatus      varchar(32) comment 'submitted:已申请，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功，payExpired:支付超时，accepted:已受理，confirmed:份额已确认，done:交易成功，refunded:已退款，abandoned:已作废',
   postmodifyStatus     varchar(32) comment 'submitted:已申请，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功，payExpired:支付超时，accepted:已受理，confirmed:份额已确认，done:交易成功，refunded:已退款，abandoned:已作废',
   reason               varchar(500),
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   dealStatus           varchar(32) comment 'toDeal:待处理，dealing:处理中，dealt:已处理',
   operator             varchar(32) not null,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_MODIFYORDER_oCode_tType                           */
/*==============================================================*/
create index IDX_MODIFYORDER_oCode_tType on T_MONEY_PLATFORM_FINANCE_MODIFYORDER
(
   orderCode,
   tradeType
);

/*==============================================================*/
/* Index: IDX_MODIFYORDER_tType_uTime                           */
/*==============================================================*/
create index IDX_MODIFYORDER_tType_uTime on T_MONEY_PLATFORM_FINANCE_MODIFYORDER
(
   tradeType,
   updateTime
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_INFORM                               */
/*==============================================================*/
create table T_MONEY_PLATFORM_INFORM
(
   oid                  varchar(32) not null,
   informCode           varchar(32),
   informType           varchar(32) comment 'productPass:产品通过，productRefused:产品拒绝，publisherCloseCollect:日结催收，publisherCloseExpired:日结逾期',
   informContent        text,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_INVESTOR_OFFSET                      */
/*==============================================================*/
create table T_MONEY_PLATFORM_INVESTOR_OFFSET
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   offsetCode           varchar(32),
   offsetDate           date,
   redeemAmount         decimal(20,2),
   clearStatus          varchar(32) comment 'toClear:待清算，clearing:清算中，cleared:已清算',
   closeStatus          varchar(32) comment 'toClose:待结算，closing:结算中，closed:已结算，closeSubmitFailed:结算申请失败，closePayFailed:结算支付失败',
   offsetFrequency      varchar(32) comment 'fast:快速，normal:普通',
   closeMan             varchar(32) comment 'platform:平台，publisher:发行人',
   toCloseRedeemAmount  int,
   overdueStatus        varchar(32) comment 'yes:已逾期，no:未逾期',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: offsetCode                                            */
/*==============================================================*/
create unique index offsetCode on T_MONEY_PLATFORM_INVESTOR_OFFSET
(
   offsetCode
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_LABEL                                */
/*==============================================================*/
create table T_MONEY_PLATFORM_LABEL
(
   oid                  varchar(32) not null,
   labelCode            varchar(32),
   labelName            varchar(32),
   labelType            varchar(32) comment 'general:基本，extend:扩展',
   labelDesc            varchar(500),
   isOk                 varchar(32) comment 'yes:可用，no:不可用',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_LABEL_PRODUCT                        */
/*==============================================================*/
create table T_MONEY_PLATFORM_LABEL_PRODUCT
(
   oid                  varchar(32) not null,
   labelOid             varchar(32),
   productOid           varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_NOTIFY                               */
/*==============================================================*/
create table T_MONEY_PLATFORM_NOTIFY
(
   oid                  varchar(32) not null,
   notifyId             varchar(32),
   notifyType           varchar(32) comment 'accepted:成功受理，refused:已拒绝, confirmed:投资份额确认, done:结算完成,closed:订单完全赎回,partHolding:订单部分赎回,abandoned:已作废,investableVolumeChange:剩余可投变化',
   notifyContent        text,
   errorCode            int,
   errorMessage         text,
   notifyStatus         varchar(32) comment 'toConfirm:待确认，confirmed:已确认',
   notifyTime           datetime,
   notifyConfirmedTime  datetime,
   notifyTimes          int,
   seqId                int,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: notifyId                                              */
/*==============================================================*/
create unique index notifyId on T_MONEY_PLATFORM_NOTIFY
(
   notifyId
);

/*==============================================================*/
/* Index: nStat_nTimes                                          */
/*==============================================================*/
create index nStat_nTimes on T_MONEY_PLATFORM_NOTIFY
(
   notifyStatus,
   notifyTimes
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_NOTIFY_HIS                           */
/*==============================================================*/
create table T_MONEY_PLATFORM_NOTIFY_HIS
(
   oid                  varchar(32) not null,
   notifyId             varchar(32),
   notifyType           varchar(32) comment 'accepted:成功受理，refused:已拒绝, confirmed:投资份额确认, done:结算完成,closed:订单完全赎回,partHolding:订单部分赎回,abandoned:已作废,investableVolumeChange:剩余可投变化',
   notifyContent        text,
   errorCode            int,
   errorMessage         text,
   notifyStatus         varchar(32) comment 'toConfirm:待确认，confirmed:已确认',
   notifyTime           datetime,
   notifyConfirmedTime  datetime,
   notifyTimes          int,
   seqId                int,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: notifyId                                              */
/*==============================================================*/
create unique index notifyId on T_MONEY_PLATFORM_NOTIFY_HIS
(
   notifyId
);

/*==============================================================*/
/* Index: nStat_nTimes                                          */
/*==============================================================*/
create index nStat_nTimes on T_MONEY_PLATFORM_NOTIFY_HIS
(
   notifyStatus,
   notifyTimes
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_PAYMENT_CHANNEL                      */
/*==============================================================*/
create table T_MONEY_PLATFORM_PAYMENT_CHANNEL
(
   oid                  varchar(32) not null,
   channelName          varchar(50),
   channelCode          varchar(50),
   inputFee             decimal(14,4),
   inputFeeType         varchar(30) comment 'percent:百分比,fixed:固定值',
   inputPayer           varchar(30) comment 'platform:平台,user:用户',
   outputFee            decimal(14,2),
   outputFeeType        varchar(30) comment 'percent:百分比,fixed:固定值',
   outputPayer          varchar(30),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_PLATFORM_PAYMENT_CHANNEL` (`oid`, `channelName`, `channelCode`, `inputFee`, `inputFeeType`, `inputPayer`, `outputFee`, `outputFeeType`, `outputPayer`, `updateTime`, `createTime`) values('4028ee815682dbdf015682dbf9120000','新浪支付通道','SinaPaymentChannel','0.0025','percent','platform','2.00','fixed','user','2016-08-13 15:44:29','2016-08-13 15:44:29');

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create unique index Index_1 on T_MONEY_PLATFORM_PAYMENT_CHANNEL
(
   channelCode
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_PUBLISHER_OFFSET                     */
/*==============================================================*/
create table T_MONEY_PLATFORM_PUBLISHER_OFFSET
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   publisherOid         varchar(32),
   offsetCode           varchar(32),
   offsetDate           date,
   investAmount         decimal(20,2),
   redeemAmount         decimal(20,2),
   netPosition          decimal(20,2),
   clearStatus          varchar(32) comment 'toClear:待清算，clearing:清算中，cleared:已清算',
   confirmStatus        varchar(32) comment 'toConfirm:待交收，confirming:交收中，confirmed:已交收',
   closeStatus          varchar(32) comment 'toClose:待结算，closing:结算中，closed:已结算，closeSubmitFailed:结算申请失败，closePayFailed:结算支付失败',
   closeMan             varchar(32) comment 'platform:平台，publisher:发行人',
   toCloseRedeemAmount  int,
   overdueStatus        varchar(32) comment 'yes:已逾期，no:未逾期',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: publisher_offsetCode                                  */
/*==============================================================*/
create unique index publisher_offsetCode on T_MONEY_PLATFORM_PUBLISHER_OFFSET
(
   publisherOid,
   offsetCode
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_PUBLISHER_ORDER                      */
/*==============================================================*/
create table T_MONEY_PLATFORM_PUBLISHER_ORDER
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   offsetOid            varchar(32) not null,
   orderCode            varchar(50),
   orderType            varchar(30) comment 'borrow:借款; return:还款',
   orderAmount          decimal(20,2),
   orderStatus          varchar(30) comment 'refused:已拒绝，toPay:待支付，submitFailed:申请失败，payFailed:支付失败，paySuccess:支付成功',
   orderTime            datetime,
   completeTime         datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_PUBLISHER_PRODUCT_OFFSET             */
/*==============================================================*/
create table T_MONEY_PLATFORM_PUBLISHER_PRODUCT_OFFSET
(
   oid                  varchar(32) not null,
   offsetOid            varchar(32),
   publisherOid         varchar(32),
   productOid           varchar(32),
   offsetCode           varchar(32),
   offsetDate           date,
   investAmount         decimal(20,2),
   redeemAmount         decimal(20,2),
   netPosition          decimal(20,2),
   clearStatus          varchar(32) comment 'toClear:待清算，clearing:清算中，cleared:已清算',
   confirmStatus        varchar(32) comment 'toConfirm:待交收，confirming:交收中，confirmed:已交收',
   closeStatus          varchar(32) comment 'toClose:待结算，closing:结算中，closed:已结算，closeSubmitFailed:结算申请失败，closePayFailed:结算支付失败',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: publisher_offsetCode                                  */
/*==============================================================*/
create index publisher_offsetCode on T_MONEY_PLATFORM_PUBLISHER_PRODUCT_OFFSET
(
   publisherOid,
   offsetCode
);

/*==============================================================*/
/* Index: productOid_offsetCode                                 */
/*==============================================================*/
create index productOid_offsetCode on T_MONEY_PLATFORM_PUBLISHER_PRODUCT_OFFSET
(
   productOid,
   offsetCode
);

/*==============================================================*/
/* Index: offsetOid_oStat_cStat                                 */
/*==============================================================*/
create index offsetOid_oStat_cStat on T_MONEY_PLATFORM_PUBLISHER_PRODUCT_OFFSET
(
   offsetOid,
   clearStatus,
   confirmStatus,
   closeStatus
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_RESERVEDACCOUNT                      */
/*==============================================================*/
create table T_MONEY_PLATFORM_RESERVEDACCOUNT
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   reservedId           varchar(32),
   balance              decimal(20,4),
   totalDepositAmount   decimal(20,4),
   totalWithdrawAmount  decimal(20,4),
   superAccBorrowAmount decimal(20,4),
   basicAccBorrowAmount decimal(20,4),
   operationId          varchar(32),
   operationAccBorrowAmount decimal(20,4),
   lastBorrowTime       datetime,
   lastReturnTime       datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_PLATFORM_RESERVEDACCOUNT` (`oid`, `platformOid`, `reservedId`, `balance`, `totalDepositAmount`, `totalWithdrawAmount`,`lastBorrowTime`, `lastReturnTime`, `updateTime`, `createTime`, `basicAccBorrowAmount`, `superAccBorrowAmount`, `operationId`, `operationAccBorrowAmount`) values('reservedoid','platformoid', NULL,'0','0','0.0000','2016-09-01 17:20:48','2016-08-24 11:10:53','2016-09-01 17:20:48','2016-08-19 09:41:38','0','0','operationId', '0');

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_RESERVEDACCOUNT_CASHFLOW             */
/*==============================================================*/
create table T_MONEY_PLATFORM_RESERVEDACCOUNT_CASHFLOW
(
   oid                  varchar(32) not null,
   reservedOid          varchar(32),
   orderOid             varchar(32),
   tradeAmount          decimal(20,4),
   tradeType            varchar(32) comment 'borrow:借款;return:还款',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_RESERVED_COUPONCASHDETAILS           */
/*==============================================================*/
create table T_MONEY_PLATFORM_RESERVED_COUPONCASHDETAILS
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   reservedOrderOid     varchar(32),
   cashAmount           decimal(20,2),
   coupons              varchar(32),
   orderCode            varchar(32),
   cashStatus           varchar(32) comment 'toCash:待核销，cashed:已核销，cashFailed:核销失败',
   cashTime             datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_RESERVED_ORDER                       */
/*==============================================================*/
create table T_MONEY_PLATFORM_RESERVED_ORDER
(
   oid                  varchar(32) not null,
   reservedOid          varchar(32) not null,
   orderCode            varchar(50),
   orderType            varchar(30) comment 'borrow:借款; return:还款',
   orderAmount          decimal(20,2),
   orderStatus          varchar(32) comment 'refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功',
   couponCashStatus     varchar(32) comment 'toCash:待核销，cashFailed:核销失败，cashed:已核销',
   completeTime         datetime,
   relatedAcc           varchar(32) comment '超级户：superAcc，基本户：basicAcc，运营户：operationAcc',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_STATISTICS                           */
/*==============================================================*/
create table T_MONEY_PLATFORM_STATISTICS
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   totalTradeAmount     decimal(20,4),
   totalLoanAmount      decimal(20,4),
   totalReturnAmount    decimal(20,4),
   totalInterestAmount  decimal(20,4),
   investorTotalDepositAmount decimal(20,4),
   investorTotalWithdrawAmount decimal(20,4),
   publisherTotalDepositAmount decimal(20,4),
   publisherTotalWithdrawAmount decimal(20,4),
   registerAmount       int,
   investorAmount       int,
   investorHoldAmount   int,
   overdueTimes         int,
   productAmount        int,
   closedProductAmount  int,
   toCloseProductAmount int,
   onSaleProductAmount  int,
   publisherAmount      int,
   verifiedInvestorAmount int,
   activeInvestorAmount int,
   confirmDate          date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_PLATFORM_STATISTICS` (`oid`, `platformOid`, `totalTradeAmount`, `totalLoanAmount`, `totalReturnAmount`, `totalInterestAmount`, `investorTotalDepositAmount`, `investorTotalWithdrawAmount`, `publisherTotalDepositAmount`, `publisherTotalWithdrawAmount`, `registerAmount`, `investorAmount`, `investorHoldAmount`, `overdueTimes`, `productAmount`, `closedProductAmount`, `toCloseProductAmount`, `onSaleProductAmount`, `publisherAmount`, `verifiedInvestorAmount`, `activeInvestorAmount`, `updateTime`, `createTime`) values('platformsta','platformoid','0','0.0000','0.0000','0.0000','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','2016-09-04 12:51:00','2016-08-19 09:41:25');

/*==============================================================*/
/* Index: IDX_T_MONEY_PLATFORM_STATISTICS_CREATETIME            */
/*==============================================================*/
create index IDX_T_MONEY_PLATFORM_STATISTICS_CREATETIME on T_MONEY_PLATFORM_STATISTICS
(
   createTime
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_STATISTICS_HISTORY                   */
/*==============================================================*/
create table T_MONEY_PLATFORM_STATISTICS_HISTORY
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   totalTradeAmount     decimal(20,4),
   totalLoanAmount      decimal(20,4),
   totalReturnAmount    decimal(20,4),
   totalInterestAmount  decimal(20,4),
   investorTotalDepositAmount decimal(20,4),
   investorTotalWithdrawAmount decimal(20,4),
   publisherTotalDepositAmount decimal(20,4),
   publisherTotalWithdrawAmount decimal(20,4),
   registerAmount       int,
   investorAmount       int,
   investorHoldAmount   int,
   overdueTimes         int,
   productAmount        int,
   closedProductAmount  int,
   toCloseProductAmount int,
   onSaleProductAmount  int,
   publisherAmount      int,
   verifiedInvestorAmount int,
   activeInvestorAmount int,
   confirmDate          date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_PLATFORM_STATISTICS_HISTORY_CONFIRMDATE           */
/*==============================================================*/
create index IDX_PLATFORM_STATISTICS_HISTORY_CONFIRMDATE on T_MONEY_PLATFORM_STATISTICS_HISTORY
(
   confirmDate
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_STATISTICS_HOURTRADE                 */
/*==============================================================*/
create table T_MONEY_PLATFORM_STATISTICS_HOURTRADE
(
   oid                  varchar(32) not null,
   platformOid          varchar(32),
   totalDepositAmount   decimal(20,4),
   totalInvestorAmount  int,
   totalRedeemAmount    decimal(20,4),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PLATFORM_SUPERACC_ORDER                       */
/*==============================================================*/
create table T_MONEY_PLATFORM_SUPERACC_ORDER
(
   oid                  varchar(32) not null,
   platformOid          varchar(32) not null,
   investorOid          varchar(32),
   orderCode            varchar(50),
   orderType            varchar(30) comment 'borrow:借款; return:还款',
   orderAmount          decimal(20,2),
   orderStatus          varchar(30) comment 'refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功',
   completeTime         datetime,
   relatedAcc           varchar(30) comment '超级户：superAcc',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_BANKORDER                           */
/*==============================================================*/
create table T_MONEY_PUBLISHER_BANKORDER
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32) not null,
   orderCode            varchar(50),
   orderType            varchar(30) comment 'deposit;充值; withdraw.提现.',
   feePayer             varchar(10) comment 'platform:平台,user:用户',
   fee                  decimal(20,2),
   orderAmount          decimal(20,2),
   orderStatus          varchar(30) comment 'submitted:已申请，submitFailed:申请失败，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功',
   orderTime            datetime,
   completeTime         datetime,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_BASEACCOUNT                         */
/*==============================================================*/
create table T_MONEY_PUBLISHER_BASEACCOUNT
(
   oid                  varchar(32) not null,
   memberId             varchar(32),
   phone                varchar(100),
   realName             varchar(100),
   certificateNo        varchar(100),
   bankName             varchar(100),
   collectionSettlementBalance decimal(20,2),
   availableAmountBalance decimal(20,2),
   frozenAmountBalance  decimal(20,2),
   withdrawAvailableAmountBalance decimal(20,2),
   cardNo               varchar(100),
   basicBalance         decimal(20,2),
   status               varchar(30) not null comment 'normal:正常; forbiden:禁用;',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_BASEACCOUNT_LOGINACC                */
/*==============================================================*/
create table T_MONEY_PUBLISHER_BASEACCOUNT_LOGINACC
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   loginAcc             varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: loginAcc                                              */
/*==============================================================*/
create unique index loginAcc on T_MONEY_PUBLISHER_BASEACCOUNT_LOGINACC
(
   loginAcc
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_CASHFLOW                            */
/*==============================================================*/
create table T_MONEY_PUBLISHER_CASHFLOW
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   publisherOrderOid    varchar(32),
   bankOrderOid         varchar(32),
   tradeAmount          decimal(20,4),
   tradeType            varchar(32) comment 'borrow:借款; return:还款;deposit;充值; withdraw.提现;fee:手续费;',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_HOLD                                */
/*==============================================================*/
create table T_MONEY_PUBLISHER_HOLD
(
   oid                  varchar(32) not null,
   productOid           varchar(32),
   publisherOid         varchar(32),
   investorOid          varchar(32),
   assetpoolOid         varchar(32),
   totalVolume          decimal(20,4),
   holdVolume           decimal(20,4),
   toConfirmInvestVolume decimal(20,4),
   toConfirmRedeemVolume decimal(20,4),
   totalInvestVolume    decimal(20,4),
   lockRedeemHoldVolume decimal(20,4),
   redeemableHoldVolume decimal(20,4),
   accruableHoldVolume  decimal(20,4),
   value                decimal(20,4),
   expGoldVolume        decimal(20,4),
   holdTotalIncome      decimal(20,4),
   totalBaseIncome      decimal(20,4),
   totalRewardIncome    decimal(20,4),
   totalCouponIncome    decimal(20,4),
   holdYesterdayIncome  decimal(20,4),
   yesterdayBaseIncome  decimal(20,4),
   yesterdayRewardIncome decimal(20,4),
   yesterdayCouponIncome decimal(20,4),
   incomeAmount         decimal(20,4),
   redeemableIncome     decimal(20,4),
   lockIncome           decimal(20,4),
   confirmDate          date,
   expectIncomeExt      decimal(20,4),
   expectIncome         decimal(20,4),
   accountType          varchar(32) comment '持有人: INVESTOR
            发行人 : SPV',
   dayRedeemVolume      decimal(20,4),
   dayInvestVolume      decimal(20,4),
   dayRedeemCount       int,
   maxHoldVolume        decimal(20,4),
   holdStatus           varchar(32) comment 'toConfirm:待确认，holding:持有中，expired:已到期，closing:结算中，closed:已结算，refunded:已退款，refunding:退款中',
   latestOrderTime      datetime,
   productAlias         varchar(32),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: hold_investor_prod                                    */
/*==============================================================*/
create unique index hold_investor_prod on T_MONEY_PUBLISHER_HOLD
(
   investorOid,
   productOid
);

/*==============================================================*/
/* Index: prod_hStat_iType                                      */
/*==============================================================*/
create index prod_hStat_iType on T_MONEY_PUBLISHER_HOLD
(
   productOid,
   accountType,
   holdStatus
);

/*==============================================================*/
/* Index: hold_uTime                                            */
/*==============================================================*/
create index hold_uTime on T_MONEY_PUBLISHER_HOLD
(
   updateTime
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_HOLDAPART_CLOSEDETAILS     */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_HOLDAPART_CLOSEDETAILS
(
   oid                  varchar(32) not null,
   redeemOrderOid       varchar(32),
   investOrderOid       varchar(32),
   changeVolume         decimal(20,4),
   changeDirection      varchar(32) comment 'in:进，out:出',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   productOid           varchar(32),
   investorOid          varchar(32),
   basicRatio           decimal(8,4),
   rewardIncomeRatio    decimal(8,4),
   incomeRatio          decimal(8,4),
   holdDays             bigint(20),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME                 */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   productOid           varchar(32),
   incomeOid            varchar(32),
   investorOid          varchar(32),
   incomeAmount         decimal(20,4),
   baseAmount           decimal(20,4),
   rewardAmount         decimal(20,4),
   totalSnapshotVolume  decimal(20,4),
   holdVolume           decimal(20,4),
   accureVolume         decimal(20,4),
   couponAmount         decimal(20,4),
   confirmDate          date,
   isClosedToBalance    varchar(32) comment 'yes:是，no:否',
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: hold_invest_dt_prod                                   */
/*==============================================================*/
create index hold_invest_dt_prod on T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME
(
   investorOid,
   confirmDate,
   productOid
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT              */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   productOid           varchar(32),
   investorOid          varchar(32),
   orderOid             varchar(32),
   totalSnapshotVolume  decimal(20,8),
   snapshotVolume       decimal(20,8),
   snapShotDate         date comment 'in:进，out:出',
   beginRedeemDate      date,
   holdDays             int,
   baseIncome           decimal(20,8),
   rewardIncome         decimal(20,8),
   holdIncome           decimal(20,8),
   rewardRuleOid        varchar(40),
   rewardIncomeRatio    decimal(20,8),
   additionalInterestRate decimal(10,4),
   affectiveDays        int,
   couponIncome         decimal(20,8),
   remainderBaseIncome  decimal(20,8),
   remainderRewardIncome decimal(20,8),
   remainderCouponIncome decimal(20,8),
   mdBaseIncome         decimal(20,8),
   mdRewardIncome       decimal(20,8),
   mdCouponIncome       decimal(20,8),
   latestRemainderBaseIncome decimal(20,8),
   latestRemainderRewardIncome decimal(20,8),
   latestRemainderCouponIncome decimal(20,8),
   holdVolume           decimal(20,8),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_snapshot_pOid_ssDate                              */
/*==============================================================*/
create index IDX_snapshot_pOid_ssDate on T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT
(
   productOid,
   snapShotDate
);

/*==============================================================*/
/* Index: IDX_snapshot_pOid_orderOid_ssDate                     */
/*==============================================================*/
create index IDX_snapshot_pOid_orderOid_ssDate on T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT
(
   productOid,
   orderOid,
   snapShotDate
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT_TMP          */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT_TMP
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   productOid           varchar(32),
   investorOid          varchar(32),
   totalSnapshotVolume  decimal(20,8),
   holdVolume           decimal(20,8),
   snapshotVolume       decimal(20,8),
   snapShotDate         date,
   baseIncome           decimal(20,8),
   rewardIncome         decimal(20,8),
   holdIncome           decimal(20,8),
   lockHoldIncome       decimal(20,8),
   redeemableHoldIncome decimal(20,8),
   couponIncome         decimal(20,8),
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_snapshot_pOid_investorOid_ssDate                  */
/*==============================================================*/
create index IDX_snapshot_pOid_investorOid_ssDate on T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT_TMP
(
   productOid,
   investorOid,
   snapShotDate
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_INCOME                     */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_INCOME
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   productOid           varchar(32),
   investorOid          varchar(32),
   incomeOid            varchar(32),
   holdIncomeOid        varchar(32),
   rewardRuleOid        varchar(32),
   levelIncomeOid       varchar(32),
   orderOid             varchar(32),
   incomeAmount         decimal(20,8),
   baseAmount           decimal(20,8),
   rewardAmount         decimal(20,8),
   accureVolume         decimal(20,8),
   couponAmount         decimal(20,8),
   remainderBaseIncome  decimal(20,8),
   remainderRewardIncome decimal(20,8),
   remainderCouponIncome decimal(20,8),
   mdBaseIncome         decimal(20,8),
   mdRewardIncome       decimal(20,8),
   mdCouponIncome       decimal(20,8),
   latestRemainderBaseIncome decimal(20,8),
   latestRemainderRewardIncome decimal(20,8),
   latestRemainderCouponIncome decimal(20,8),
   totalSnapshotVolume  decimal(20,8),
   holdVolume           decimal(20,8),
   confirmDate          date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: prod_hOid_cDt                                         */
/*==============================================================*/
create index prod_hOid_cDt on T_MONEY_PUBLISHER_INVESTOR_INCOME
(
   productOid,
   confirmDate,
   holdOid
);

/*==============================================================*/
/* Index: haIncome_uTime                                        */
/*==============================================================*/
create index haIncome_uTime on T_MONEY_PUBLISHER_INVESTOR_INCOME
(
   updateTime
);

/*==============================================================*/
/* Index: IDX_investor_income_pid_iid_rid_cDate                 */
/*==============================================================*/
create index IDX_investor_income_pid_iid_rid_cDate on T_MONEY_PUBLISHER_INVESTOR_INCOME
(
   productOid,
   investorOid,
   rewardRuleOid,
   confirmDate
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_INTEREST_RESULT            */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_INTEREST_RESULT
(
   oid                  varchar(32) not null,
   allocateOid          varchar(32),
   productOid           varchar(32),
   allocateDate         date,
   successAllocateIncome decimal(16,4),
   successAllocateBaseIncome decimal(16,4),
   successAllocateRewardIncome decimal(16,4),
   successAllocateCouponIncome decimal(16,4),
   leftAllocateIncome   decimal(16,4),
   leftAllocateBaseIncome decimal(16,4),
   leftAllocateRewardIncome decimal(16,4),
   leftAllocateCouponIncome decimal(16,4),
   successAllocateInvestors int,
   failAllocateInvestors int,
   status               varchar(32) comment 'ALLOCATED: 发放完成;  ALLOCATEFAIL: 发放失败
            ',
   anno                 varchar(1000),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME                */
/*==============================================================*/
create table T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   productOid           varchar(32),
   rewardRuleOid        varchar(32),
   investorOid          varchar(32),
   holdIncomeOid        varchar(32),
   incomeAmount         decimal(20,4),
   baseAmount           decimal(20,4),
   rewardAmount         decimal(20,4),
   accureVolume         decimal(20,4),
   couponAmount         decimal(20,4),
   value                decimal(20,4),
   confirmDate          date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   totalSnapshotVolume  decimal(20,4),
   holdVolume           decimal(20,4),
   primary key (oid)
);

/*==============================================================*/
/* Index: li_uTime                                              */
/*==============================================================*/
create index li_uTime on T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME
(
   updateTime
);

/*==============================================================*/
/* Index: invest_prod_rRule                                     */
/*==============================================================*/
create index invest_prod_rRule on T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME
(
   investorOid,
   productOid,
   rewardRuleOid
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_PRODUCT_AGREEMENT                   */
/*==============================================================*/
create table T_MONEY_PUBLISHER_PRODUCT_AGREEMENT
(
   oid                  varchar(32) not null,
   productOid           varchar(32),
   orderOid             varchar(32),
   agreementCode        varchar(100),
   agreementName        varchar(200),
   agreementUrl         varchar(500),
   agreementType        varchar(32) comment 'investing:产品投资协议，service:信息服务协议',
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_PRODUCT_REWARDINCOMEPRACTICE        */
/*==============================================================*/
create table T_MONEY_PUBLISHER_PRODUCT_REWARDINCOMEPRACTICE
(
   oid                  varchar(32) not null,
   productOid           varchar(32),
   rewardRuleOid        varchar(32),
   totalHoldVolume      decimal(20,8),
   totalRewardIncome    decimal(20,4) comment 'on:开，off:关',
   totalCouponIncome    decimal(20,4),
   tDate                date,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_PRODUCT_STATISTICS                  */
/*==============================================================*/
create table T_MONEY_PUBLISHER_PRODUCT_STATISTICS
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   productOid           varchar(32),
   investAmount         decimal(20,4),
   investRank           int(11),
   totalInvestAmount    decimal(20,4),
   totalInvestRank      int,
   investDate           date,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_PUBLISHER_PRODUCT_STATISTICS_investDate           */
/*==============================================================*/
create index IDX_PUBLISHER_PRODUCT_STATISTICS_investDate on T_MONEY_PUBLISHER_PRODUCT_STATISTICS
(
   investDate
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_STATISTICS                          */
/*==============================================================*/
create table T_MONEY_PUBLISHER_STATISTICS
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   totalDepositAmount   decimal(20,4),
   totalWithdrawAmount  decimal(20,4),
   totalLoanAmount      decimal(20,4),
   totalReturnAmount    decimal(20,4),
   totalInterestAmount  decimal(20,4),
   todayInvestAmount    decimal(20,4),
   todayT0InvestAmount  decimal(20,4),
   todayTnInvestAmount  decimal(20,4),
   todayRedeemAmount    decimal(20,4),
   todayRepayInvestAmount decimal(20,4),
   todayRepayInterestAmount decimal(20,4),
   overdueTimes         int,
   productAmount        int,
   onSaleProductAmount  int,
   closedProductAmount  int,
   toCloseProductAmount int,
   investorAmount       int,
   investorHoldAmount   int,
   todayT0InvestorAmount int,
   todayTnInvestorAmount int,
   todayInvestorAmount  int,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_STATISTICS_HISTORY                  */
/*==============================================================*/
create table T_MONEY_PUBLISHER_STATISTICS_HISTORY
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   totalDepositAmount   decimal(20,4),
   totalWithdrawAmount  decimal(20,4),
   totalLoanAmount      decimal(20,4),
   totalReturnAmount    decimal(20,4),
   totalInterestAmount  decimal(20,4),
   todayInvestAmount    decimal(20,4),
   todayT0InvestAmount  decimal(20,4),
   todayTnInvestAmount  decimal(20,4),
   todayRedeemAmount    decimal(20,4),
   todayRepayInvestAmount decimal(20,4),
   todayRepayInterestAmount decimal(20,4),
   overdueTimes         int,
   productAmount        int,
   onSaleProductAmount  int,
   closedProductAmount  int,
   toCloseProductAmount int,
   investorAmount       int,
   investorHoldAmount   int,
   todayT0InvestorAmount int,
   todayTnInvestorAmount int,
   todayInvestorAmount  int,
   confirmDate          date,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_PUBLISHER_STATISTICS_HISTORY_CONFIRMDATE          */
/*==============================================================*/
create index IDX_PUBLISHER_STATISTICS_HISTORY_CONFIRMDATE on T_MONEY_PUBLISHER_STATISTICS_HISTORY
(
   confirmDate
);

/*==============================================================*/
/* Table: T_MONEY_PUBLISHER_STATISTICS_HOURTRADE                */
/*==============================================================*/
create table T_MONEY_PUBLISHER_STATISTICS_HOURTRADE
(
   oid                  varchar(32) not null,
   publisherOid         varchar(32),
   totalDepositAmount   decimal(20,4),
   totalInvestorAmount  int,
   totalRedeemAmount    decimal(20,4),
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_MONEY_REDIS_SYNC                                    */
/*==============================================================*/
create table T_MONEY_REDIS_SYNC
(
   oid                  int not null auto_increment,
   syncOid              varchar(32),
   syncOidType          varchar(32) comment 'product:产品,investorHold:投资者持仓,spvHold:SPV持仓,investorStatistic:投资人统计,investor:投资人,channel:渠道，label:标签,assetpool:资产池,tradeorder:委托单',
   productOid           varchar(32),
   assetpoolOid         varchar(32),
   xid                  int,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: idx_redis_sync_xid                                    */
/*==============================================================*/
create index idx_redis_sync_xid on T_MONEY_REDIS_SYNC
(
   xid
);

/*==============================================================*/
/* Table: T_MONEY_SERIALTASK                                    */
/*==============================================================*/
create table T_MONEY_SERIALTASK
(
   oid                  varchar(32) not null,
   taskCode             varchar(32),
   taskParams           text,
   executeStartTime     datetime,
   executeEndTime       datetime,
   priority             int,
   taskStatus           varchar(32) comment 'toRun:待执行，failed:失败，done:完成，running:执行中',
   taskError            text,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

insert into `T_MONEY_SERIALTASK` (`oid`, `taskCode`, `taskParams`, `executeStartTime`, `executeEndTime`, `taskStatus`, `taskError`, `updateTime`, `createTime`) values('mainTask','mainTask',NULL,NULL,NULL,'toRun',NULL,'2016-10-19 10:45:33','2016-10-19 10:45:33');

/*==============================================================*/
/* Index: taskStatus                                            */
/*==============================================================*/
create index taskStatus on T_MONEY_SERIALTASK
(
   taskStatus
);

/*==============================================================*/
/* Table: T_MONEY_TULIP_LOG                                     */
/*==============================================================*/
create table T_MONEY_TULIP_LOG
(
   oid                  varchar(32) not null,
   interfaceCode        varchar(32),
   interfaceName        varchar(100),
   errorCode            varchar(100),
   errorMessage         longtext,
   sendedTimes          int,
   limitSendTimes       int,
   nextNotifyTime       datetime,
   sendObj              longtext,
   updateTime           timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp not null default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: IDX_TULIP_LOG_ERRORCODE                               */
/*==============================================================*/
create index IDX_TULIP_LOG_ERRORCODE on T_MONEY_TULIP_LOG
(
   errorCode
);

/*==============================================================*/
/* Table: T_OPE_FAILCARD                                        */
/*==============================================================*/
create table T_OPE_FAILCARD
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   name                 varchar(32) comment '姓名',
   phone                varchar(32) comment '手机',
   source               varchar(256) default '未知' comment '注册渠道',
   bindTime             datetime comment '绑卡时间',
   systemReason         varchar(256) comment '系统原因',
   lastFeedback         varchar(256) comment '最终反馈',
   isFeedback           varchar(32) comment '是否反馈 is是 no否',
   isBind               varchar(32) comment '是否已绑卡成功 is是 no否',
   bindSuccessTime      datetime comment '首次绑卡成功时间',
   operator             varchar(32) comment '操作人',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OPE_FAILRECHARGE                                    */
/*==============================================================*/
create table T_OPE_FAILRECHARGE
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   name                 varchar(32) comment '姓名',
   phone                varchar(32) comment '手机',
   source               varchar(256) default '未知' comment '注册渠道',
   rechargeTime         datetime comment '充值时间',
   systemReason         varchar(256) comment '系统原因',
   lastFeedback         varchar(256) comment '最终反馈',
   isFeedback           varchar(32) comment '是否反馈 is是 no否',
   isCharge             varchar(32) comment '是否充值成功 is是 no否',
   rechargeSuccessTime  datetime comment '首次充值成功时间',
   operator             varchar(32) comment '操作人',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OPE_NOBUY                                           */
/*==============================================================*/
create table T_OPE_NOBUY
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   name                 varchar(32) comment '姓名',
   phone                varchar(32) comment '手机',
   source               varchar(256) default '未知' comment '注册渠道',
   rechargeTime         datetime comment '首充成功时间',
   lastFeedback         varchar(256) comment '最终反馈',
   isFeedback           varchar(32) comment '是否反馈 is是 no否',
   isBuy                varchar(32) comment '是否已购买 is是 no否',
   buyTime              datetime default NULL comment '首次购买成功时间',
   operator             varchar(32) comment '操作人',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OPE_NOCARD                                          */
/*==============================================================*/
create table T_OPE_NOCARD
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   name                 varchar(32) comment '姓名',
   phone                varchar(32) comment '手机',
   source               varchar(256) default '未知' comment '注册渠道',
   registerTime         datetime comment '注册时间(以该时间查询)',
   lastFeedback         varchar(256) comment '最终反馈',
   isFeedback           varchar(32) comment '是否反馈 is是 no否',
   isBind               varchar(32) comment '是否已绑卡 is是 no否',
   bindSuccessTime      datetime comment '首次绑卡成功时间',
   operator             varchar(32) comment '操作人',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OPE_NORECHARGE                                      */
/*==============================================================*/
create table T_OPE_NORECHARGE
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   name                 varchar(32) comment '姓名',
   phone                varchar(32) comment '手机',
   source               varchar(256) default '未知' comment '注册渠道',
   bindTime             datetime comment '绑卡成功时间',
   lastFeedback         varchar(256) comment '最终反馈',
   isFeedback           varchar(32) comment '是否反馈 is是 no否',
   isCharge             varchar(32) comment '是否已充值 is是 no否',
   rechargeSuccessTime  datetime comment '首次充值成功时间',
   operator             varchar(32) comment '操作人',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_OPE_SELECTTIME                                      */
/*==============================================================*/
create table T_OPE_SELECTTIME
(
   oid                  varchar(32) not null,
   name                 varchar(32) comment '对象',
   time                 bigint comment '时间',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_TRADE_CALENDAR                                      */
/*==============================================================*/
create table T_TRADE_CALENDAR
(
   oid                  varchar(32) not null,
   exchangeCD           varchar(16),
   calendarDate         date,
   isOpen               int,
   isWork               int,
   prevTradeDate        date,
   prevWorkDate         date,
   isWeekEnd            int,
   isMonthEnd           int,
   isQuarterEnd         int,
   isYearEnd            int,
   primary key (oid)
);

insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-02','XSHG','2016-12-02',1,1,'2016-12-01','2016-12-01',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-03','XSHG','2016-12-03',0,0,'2016-12-02','2016-12-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-04','XSHG','2016-12-04',0,0,'2016-12-02','2016-12-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-05','XSHG','2016-12-05',1,1,'2016-12-02','2016-12-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-06','XSHG','2016-12-06',1,1,'2016-12-05','2016-12-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-07','XSHG','2016-12-07',1,1,'2016-12-06','2016-12-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-08','XSHG','2016-12-08',1,1,'2016-12-07','2016-12-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-09','XSHG','2016-12-09',1,1,'2016-12-08','2016-12-08',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-10','XSHG','2016-12-10',0,0,'2016-12-09','2016-12-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-11','XSHG','2016-12-11',0,0,'2016-12-09','2016-12-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-12','XSHG','2016-12-12',1,1,'2016-12-09','2016-12-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-13','XSHG','2016-12-13',1,1,'2016-12-12','2016-12-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-14','XSHG','2016-12-14',1,1,'2016-12-13','2016-12-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-15','XSHG','2016-12-15',1,1,'2016-12-14','2016-12-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-16','XSHG','2016-12-16',1,1,'2016-12-15','2016-12-15',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-17','XSHG','2016-12-17',0,0,'2016-12-16','2016-12-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-18','XSHG','2016-12-18',0,0,'2016-12-16','2016-12-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-19','XSHG','2016-12-19',1,1,'2016-12-16','2016-12-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-20','XSHG','2016-12-20',1,1,'2016-12-19','2016-12-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-21','XSHG','2016-12-21',1,1,'2016-12-20','2016-12-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-22','XSHG','2016-12-22',1,1,'2016-12-21','2016-12-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-23','XSHG','2016-12-23',1,1,'2016-12-22','2016-12-22',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-24','XSHG','2016-12-24',0,0,'2016-12-23','2016-12-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-25','XSHG','2016-12-25',0,0,'2016-12-23','2016-12-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-26','XSHG','2016-12-26',1,1,'2016-12-23','2016-12-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-27','XSHG','2016-12-27',1,1,'2016-12-26','2016-12-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-28','XSHG','2016-12-28',1,1,'2016-12-27','2016-12-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-29','XSHG','2016-12-29',1,1,'2016-12-28','2016-12-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-30','XSHG','2016-12-30',1,1,'2016-12-29','2016-12-29',1,1,1,1);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2016-12-31','XSHG','2016-12-31',0,0,'2016-12-30','2016-12-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-01','XSHG','2017-01-01',0,0,'2016-12-30','2016-12-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-02','XSHG','2017-01-02',0,0,'2016-12-30','2016-12-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-03','XSHG','2017-01-03',1,1,'2016-12-30','2016-12-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-04','XSHG','2017-01-04',1,1,'2017-01-03','2017-01-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-05','XSHG','2017-01-05',1,1,'2017-01-04','2017-01-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-06','XSHG','2017-01-06',1,1,'2017-01-05','2017-01-05',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-07','XSHG','2017-01-07',0,0,'2017-01-06','2017-01-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-08','XSHG','2017-01-08',0,0,'2017-01-06','2017-01-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-09','XSHG','2017-01-09',1,1,'2017-01-06','2017-01-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-10','XSHG','2017-01-10',1,1,'2017-01-09','2017-01-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-11','XSHG','2017-01-11',1,1,'2017-01-10','2017-01-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-12','XSHG','2017-01-12',1,1,'2017-01-11','2017-01-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-13','XSHG','2017-01-13',1,1,'2017-01-12','2017-01-12',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-14','XSHG','2017-01-14',0,0,'2017-01-13','2017-01-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-15','XSHG','2017-01-15',0,0,'2017-01-13','2017-01-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-16','XSHG','2017-01-16',1,1,'2017-01-13','2017-01-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-17','XSHG','2017-01-17',1,1,'2017-01-16','2017-01-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-18','XSHG','2017-01-18',1,1,'2017-01-17','2017-01-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-19','XSHG','2017-01-19',1,1,'2017-01-18','2017-01-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-20','XSHG','2017-01-20',1,1,'2017-01-19','2017-01-19',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-21','XSHG','2017-01-21',0,0,'2017-01-20','2017-01-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-22','XSHG','2017-01-22',0,0,'2017-01-20','2017-01-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-23','XSHG','2017-01-23',1,1,'2017-01-20','2017-01-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-24','XSHG','2017-01-24',1,1,'2017-01-23','2017-01-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-25','XSHG','2017-01-25',1,1,'2017-01-24','2017-01-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-26','XSHG','2017-01-26',1,1,'2017-01-25','2017-01-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-27','XSHG','2017-01-27',0,0,'2017-01-26','2017-01-26',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-28','XSHG','2017-01-28',0,0,'2017-01-26','2017-01-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-29','XSHG','2017-01-29',0,0,'2017-01-26','2017-01-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-30','XSHG','2017-01-30',0,0,'2017-01-26','2017-01-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-01-31','XSHG','2017-01-31',0,0,'2017-01-26','2017-01-26',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-01','XSHG','2017-02-01',0,0,'2017-01-26','2017-01-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-02','XSHG','2017-02-02',0,0,'2017-01-26','2017-01-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-03','XSHG','2017-02-03',1,1,'2017-01-26','2017-01-26',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-04','XSHG','2017-02-04',0,1,'2017-02-03','2017-02-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-05','XSHG','2017-02-05',0,0,'2017-02-03','2017-02-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-06','XSHG','2017-02-06',1,1,'2017-02-03','2017-02-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-07','XSHG','2017-02-07',1,1,'2017-02-06','2017-02-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-08','XSHG','2017-02-08',1,1,'2017-02-07','2017-02-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-09','XSHG','2017-02-09',1,1,'2017-02-08','2017-02-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-10','XSHG','2017-02-10',1,1,'2017-02-09','2017-02-09',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-11','XSHG','2017-02-11',0,0,'2017-02-10','2017-02-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-12','XSHG','2017-02-12',0,0,'2017-02-10','2017-02-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-13','XSHG','2017-02-13',1,1,'2017-02-10','2017-02-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-14','XSHG','2017-02-14',1,1,'2017-02-13','2017-02-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-15','XSHG','2017-02-15',1,1,'2017-02-14','2017-02-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-16','XSHG','2017-02-16',1,1,'2017-02-15','2017-02-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-17','XSHG','2017-02-17',1,1,'2017-02-16','2017-02-16',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-18','XSHG','2017-02-18',0,0,'2017-02-17','2017-02-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-19','XSHG','2017-02-19',0,0,'2017-02-17','2017-02-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-20','XSHG','2017-02-20',1,1,'2017-02-17','2017-02-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-21','XSHG','2017-02-21',1,1,'2017-02-20','2017-02-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-22','XSHG','2017-02-22',1,1,'2017-02-21','2017-02-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-23','XSHG','2017-02-23',1,1,'2017-02-22','2017-02-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-24','XSHG','2017-02-24',1,1,'2017-02-23','2017-02-23',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-25','XSHG','2017-02-25',0,0,'2017-02-24','2017-02-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-26','XSHG','2017-02-26',0,0,'2017-02-24','2017-02-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-27','XSHG','2017-02-27',1,1,'2017-02-24','2017-02-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-02-28','XSHG','2017-02-28',1,1,'2017-02-27','2017-02-27',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-01','XSHG','2017-03-01',1,1,'2017-02-28','2017-02-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-02','XSHG','2017-03-02',1,1,'2017-03-01','2017-03-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-03','XSHG','2017-03-03',1,1,'2017-03-02','2017-03-02',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-04','XSHG','2017-03-04',0,0,'2017-03-03','2017-03-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-05','XSHG','2017-03-05',0,0,'2017-03-03','2017-03-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-06','XSHG','2017-03-06',1,1,'2017-03-03','2017-03-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-07','XSHG','2017-03-07',1,1,'2017-03-06','2017-03-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-08','XSHG','2017-03-08',1,1,'2017-03-07','2017-03-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-09','XSHG','2017-03-09',1,1,'2017-03-08','2017-03-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-10','XSHG','2017-03-10',1,1,'2017-03-09','2017-03-09',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-11','XSHG','2017-03-11',0,0,'2017-03-10','2017-03-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-12','XSHG','2017-03-12',0,0,'2017-03-10','2017-03-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-13','XSHG','2017-03-13',1,1,'2017-03-10','2017-03-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-14','XSHG','2017-03-14',1,1,'2017-03-13','2017-03-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-15','XSHG','2017-03-15',1,1,'2017-03-14','2017-03-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-16','XSHG','2017-03-16',1,1,'2017-03-15','2017-03-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-17','XSHG','2017-03-17',1,1,'2017-03-16','2017-03-16',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-18','XSHG','2017-03-18',0,0,'2017-03-17','2017-03-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-19','XSHG','2017-03-19',0,0,'2017-03-17','2017-03-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-20','XSHG','2017-03-20',1,1,'2017-03-17','2017-03-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-21','XSHG','2017-03-21',1,1,'2017-03-20','2017-03-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-22','XSHG','2017-03-22',1,1,'2017-03-21','2017-03-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-23','XSHG','2017-03-23',1,1,'2017-03-22','2017-03-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-24','XSHG','2017-03-24',1,1,'2017-03-23','2017-03-23',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-25','XSHG','2017-03-25',0,0,'2017-03-24','2017-03-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-26','XSHG','2017-03-26',0,0,'2017-03-24','2017-03-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-27','XSHG','2017-03-27',1,1,'2017-03-24','2017-03-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-28','XSHG','2017-03-28',1,1,'2017-03-27','2017-03-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-29','XSHG','2017-03-29',1,1,'2017-03-28','2017-03-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-30','XSHG','2017-03-30',1,1,'2017-03-29','2017-03-29',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-03-31','XSHG','2017-03-31',1,1,'2017-03-30','2017-03-30',1,1,1,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-01','XSHG','2017-04-01',0,1,'2017-03-31','2017-03-31',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-02','XSHG','2017-04-02',0,0,'2017-03-31','2017-04-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-03','XSHG','2017-04-03',0,0,'2017-03-31','2017-04-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-04','XSHG','2017-04-04',0,0,'2017-03-31','2017-04-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-05','XSHG','2017-04-05',1,1,'2017-03-31','2017-04-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-06','XSHG','2017-04-06',1,1,'2017-04-05','2017-04-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-07','XSHG','2017-04-07',1,1,'2017-04-06','2017-04-06',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-08','XSHG','2017-04-08',0,0,'2017-04-07','2017-04-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-09','XSHG','2017-04-09',0,0,'2017-04-07','2017-04-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-10','XSHG','2017-04-10',1,1,'2017-04-07','2017-04-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-11','XSHG','2017-04-11',1,1,'2017-04-10','2017-04-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-12','XSHG','2017-04-12',1,1,'2017-04-11','2017-04-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-13','XSHG','2017-04-13',1,1,'2017-04-12','2017-04-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-14','XSHG','2017-04-14',1,1,'2017-04-13','2017-04-13',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-15','XSHG','2017-04-15',0,0,'2017-04-14','2017-04-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-16','XSHG','2017-04-16',0,0,'2017-04-14','2017-04-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-17','XSHG','2017-04-17',1,1,'2017-04-14','2017-04-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-18','XSHG','2017-04-18',1,1,'2017-04-17','2017-04-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-19','XSHG','2017-04-19',1,1,'2017-04-18','2017-04-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-20','XSHG','2017-04-20',1,1,'2017-04-19','2017-04-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-21','XSHG','2017-04-21',1,1,'2017-04-20','2017-04-20',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-22','XSHG','2017-04-22',0,0,'2017-04-21','2017-04-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-23','XSHG','2017-04-23',0,0,'2017-04-21','2017-04-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-24','XSHG','2017-04-24',1,1,'2017-04-21','2017-04-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-25','XSHG','2017-04-25',1,1,'2017-04-24','2017-04-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-26','XSHG','2017-04-26',1,1,'2017-04-25','2017-04-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-27','XSHG','2017-04-27',1,1,'2017-04-26','2017-04-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-28','XSHG','2017-04-28',1,1,'2017-04-27','2017-04-27',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-29','XSHG','2017-04-29',0,0,'2017-04-28','2017-04-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-04-30','XSHG','2017-04-30',0,0,'2017-04-28','2017-04-28',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-01','XSHG','2017-05-01',0,0,'2017-04-28','2017-04-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-02','XSHG','2017-05-02',1,1,'2017-04-28','2017-04-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-03','XSHG','2017-05-03',1,1,'2017-05-02','2017-05-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-04','XSHG','2017-05-04',1,1,'2017-05-03','2017-05-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-05','XSHG','2017-05-05',1,1,'2017-05-04','2017-05-04',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-06','XSHG','2017-05-06',0,0,'2017-05-05','2017-05-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-07','XSHG','2017-05-07',0,0,'2017-05-05','2017-05-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-08','XSHG','2017-05-08',1,1,'2017-05-05','2017-05-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-09','XSHG','2017-05-09',1,1,'2017-05-08','2017-05-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-10','XSHG','2017-05-10',1,1,'2017-05-09','2017-05-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-11','XSHG','2017-05-11',1,1,'2017-05-10','2017-05-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-12','XSHG','2017-05-12',1,1,'2017-05-11','2017-05-11',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-13','XSHG','2017-05-13',0,0,'2017-05-12','2017-05-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-14','XSHG','2017-05-14',0,0,'2017-05-12','2017-05-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-15','XSHG','2017-05-15',1,1,'2017-05-12','2017-05-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-16','XSHG','2017-05-16',1,1,'2017-05-15','2017-05-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-17','XSHG','2017-05-17',1,1,'2017-05-16','2017-05-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-18','XSHG','2017-05-18',1,1,'2017-05-17','2017-05-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-19','XSHG','2017-05-19',1,1,'2017-05-18','2017-05-18',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-20','XSHG','2017-05-20',0,0,'2017-05-19','2017-05-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-21','XSHG','2017-05-21',0,0,'2017-05-19','2017-05-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-22','XSHG','2017-05-22',1,1,'2017-05-19','2017-05-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-23','XSHG','2017-05-23',1,1,'2017-05-22','2017-05-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-24','XSHG','2017-05-24',1,1,'2017-05-23','2017-05-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-25','XSHG','2017-05-25',1,1,'2017-05-24','2017-05-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-26','XSHG','2017-05-26',1,1,'2017-05-25','2017-05-25',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-27','XSHG','2017-05-27',0,1,'2017-05-26','2017-05-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-28','XSHG','2017-05-28',0,0,'2017-05-26','2017-05-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-29','XSHG','2017-05-29',0,0,'2017-05-26','2017-05-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-30','XSHG','2017-05-30',0,0,'2017-05-26','2017-05-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-05-31','XSHG','2017-05-31',1,1,'2017-05-26','2017-05-27',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-01','XSHG','2017-06-01',1,1,'2017-05-31','2017-05-31',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-02','XSHG','2017-06-02',1,1,'2017-06-01','2017-06-01',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-03','XSHG','2017-06-03',0,0,'2017-06-02','2017-06-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-04','XSHG','2017-06-04',0,0,'2017-06-02','2017-06-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-05','XSHG','2017-06-05',1,1,'2017-06-02','2017-06-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-06','XSHG','2017-06-06',1,1,'2017-06-05','2017-06-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-07','XSHG','2017-06-07',1,1,'2017-06-06','2017-06-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-08','XSHG','2017-06-08',1,1,'2017-06-07','2017-06-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-09','XSHG','2017-06-09',1,1,'2017-06-08','2017-06-08',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-10','XSHG','2017-06-10',0,0,'2017-06-09','2017-06-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-11','XSHG','2017-06-11',0,0,'2017-06-09','2017-06-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-12','XSHG','2017-06-12',1,1,'2017-06-09','2017-06-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-13','XSHG','2017-06-13',1,1,'2017-06-12','2017-06-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-14','XSHG','2017-06-14',1,1,'2017-06-13','2017-06-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-15','XSHG','2017-06-15',1,1,'2017-06-14','2017-06-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-16','XSHG','2017-06-16',1,1,'2017-06-15','2017-06-15',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-17','XSHG','2017-06-17',0,0,'2017-06-16','2017-06-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-18','XSHG','2017-06-18',0,0,'2017-06-16','2017-06-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-19','XSHG','2017-06-19',1,1,'2017-06-16','2017-06-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-20','XSHG','2017-06-20',1,1,'2017-06-19','2017-06-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-21','XSHG','2017-06-21',1,1,'2017-06-20','2017-06-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-22','XSHG','2017-06-22',1,1,'2017-06-21','2017-06-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-23','XSHG','2017-06-23',1,1,'2017-06-22','2017-06-22',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-24','XSHG','2017-06-24',0,0,'2017-06-23','2017-06-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-25','XSHG','2017-06-25',0,0,'2017-06-23','2017-06-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-26','XSHG','2017-06-26',1,1,'2017-06-23','2017-06-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-27','XSHG','2017-06-27',1,1,'2017-06-26','2017-06-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-28','XSHG','2017-06-28',1,1,'2017-06-27','2017-06-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-29','XSHG','2017-06-29',1,1,'2017-06-28','2017-06-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-06-30','XSHG','2017-06-30',1,1,'2017-06-29','2017-06-29',1,1,1,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-01','XSHG','2017-07-01',0,0,'2017-06-30','2017-06-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-02','XSHG','2017-07-02',0,0,'2017-06-30','2017-06-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-03','XSHG','2017-07-03',1,1,'2017-06-30','2017-06-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-04','XSHG','2017-07-04',1,1,'2017-07-03','2017-07-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-05','XSHG','2017-07-05',1,1,'2017-07-04','2017-07-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-06','XSHG','2017-07-06',1,1,'2017-07-05','2017-07-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-07','XSHG','2017-07-07',1,1,'2017-07-06','2017-07-06',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-08','XSHG','2017-07-08',0,0,'2017-07-07','2017-07-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-09','XSHG','2017-07-09',0,0,'2017-07-07','2017-07-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-10','XSHG','2017-07-10',1,1,'2017-07-07','2017-07-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-11','XSHG','2017-07-11',1,1,'2017-07-10','2017-07-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-12','XSHG','2017-07-12',1,1,'2017-07-11','2017-07-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-13','XSHG','2017-07-13',1,1,'2017-07-12','2017-07-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-14','XSHG','2017-07-14',1,1,'2017-07-13','2017-07-13',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-15','XSHG','2017-07-15',0,0,'2017-07-14','2017-07-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-16','XSHG','2017-07-16',0,0,'2017-07-14','2017-07-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-17','XSHG','2017-07-17',1,1,'2017-07-14','2017-07-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-18','XSHG','2017-07-18',1,1,'2017-07-17','2017-07-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-19','XSHG','2017-07-19',1,1,'2017-07-18','2017-07-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-20','XSHG','2017-07-20',1,1,'2017-07-19','2017-07-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-21','XSHG','2017-07-21',1,1,'2017-07-20','2017-07-20',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-22','XSHG','2017-07-22',0,0,'2017-07-21','2017-07-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-23','XSHG','2017-07-23',0,0,'2017-07-21','2017-07-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-24','XSHG','2017-07-24',1,1,'2017-07-21','2017-07-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-25','XSHG','2017-07-25',1,1,'2017-07-24','2017-07-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-26','XSHG','2017-07-26',1,1,'2017-07-25','2017-07-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-27','XSHG','2017-07-27',1,1,'2017-07-26','2017-07-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-28','XSHG','2017-07-28',1,1,'2017-07-27','2017-07-27',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-29','XSHG','2017-07-29',0,0,'2017-07-28','2017-07-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-30','XSHG','2017-07-30',0,0,'2017-07-28','2017-07-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-07-31','XSHG','2017-07-31',1,1,'2017-07-28','2017-07-28',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-01','XSHG','2017-08-01',1,1,'2017-07-31','2017-07-31',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-02','XSHG','2017-08-02',1,1,'2017-08-01','2017-08-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-03','XSHG','2017-08-03',1,1,'2017-08-02','2017-08-02',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-04','XSHG','2017-08-04',1,1,'2017-08-03','2017-08-03',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-05','XSHG','2017-08-05',0,0,'2017-08-04','2017-08-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-06','XSHG','2017-08-06',0,0,'2017-08-04','2017-08-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-07','XSHG','2017-08-07',1,1,'2017-08-04','2017-08-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-08','XSHG','2017-08-08',1,1,'2017-08-07','2017-08-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-09','XSHG','2017-08-09',1,1,'2017-08-08','2017-08-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-10','XSHG','2017-08-10',1,1,'2017-08-09','2017-08-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-11','XSHG','2017-08-11',1,1,'2017-08-10','2017-08-10',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-12','XSHG','2017-08-12',0,0,'2017-08-11','2017-08-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-13','XSHG','2017-08-13',0,0,'2017-08-11','2017-08-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-14','XSHG','2017-08-14',1,1,'2017-08-11','2017-08-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-15','XSHG','2017-08-15',1,1,'2017-08-14','2017-08-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-16','XSHG','2017-08-16',1,1,'2017-08-15','2017-08-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-17','XSHG','2017-08-17',1,1,'2017-08-16','2017-08-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-18','XSHG','2017-08-18',1,1,'2017-08-17','2017-08-17',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-19','XSHG','2017-08-19',0,0,'2017-08-18','2017-08-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-20','XSHG','2017-08-20',0,0,'2017-08-18','2017-08-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-21','XSHG','2017-08-21',1,1,'2017-08-18','2017-08-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-22','XSHG','2017-08-22',1,1,'2017-08-21','2017-08-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-23','XSHG','2017-08-23',1,1,'2017-08-22','2017-08-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-24','XSHG','2017-08-24',1,1,'2017-08-23','2017-08-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-25','XSHG','2017-08-25',1,1,'2017-08-24','2017-08-24',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-26','XSHG','2017-08-26',0,0,'2017-08-25','2017-08-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-27','XSHG','2017-08-27',0,0,'2017-08-25','2017-08-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-28','XSHG','2017-08-28',1,1,'2017-08-25','2017-08-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-29','XSHG','2017-08-29',1,1,'2017-08-28','2017-08-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-30','XSHG','2017-08-30',1,1,'2017-08-29','2017-08-29',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-08-31','XSHG','2017-08-31',1,1,'2017-08-30','2017-08-30',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-01','XSHG','2017-09-01',1,1,'2017-08-31','2017-08-31',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-02','XSHG','2017-09-02',0,0,'2017-09-01','2017-09-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-03','XSHG','2017-09-03',0,0,'2017-09-01','2017-09-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-04','XSHG','2017-09-04',1,1,'2017-09-01','2017-09-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-05','XSHG','2017-09-05',1,1,'2017-09-04','2017-09-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-06','XSHG','2017-09-06',1,1,'2017-09-05','2017-09-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-07','XSHG','2017-09-07',1,1,'2017-09-06','2017-09-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-08','XSHG','2017-09-08',1,1,'2017-09-07','2017-09-07',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-09','XSHG','2017-09-09',0,0,'2017-09-08','2017-09-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-10','XSHG','2017-09-10',0,0,'2017-09-08','2017-09-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-11','XSHG','2017-09-11',1,1,'2017-09-08','2017-09-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-12','XSHG','2017-09-12',1,1,'2017-09-11','2017-09-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-13','XSHG','2017-09-13',1,1,'2017-09-12','2017-09-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-14','XSHG','2017-09-14',1,1,'2017-09-13','2017-09-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-15','XSHG','2017-09-15',1,1,'2017-09-14','2017-09-14',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-16','XSHG','2017-09-16',0,0,'2017-09-15','2017-09-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-17','XSHG','2017-09-17',0,0,'2017-09-15','2017-09-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-18','XSHG','2017-09-18',1,1,'2017-09-15','2017-09-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-19','XSHG','2017-09-19',1,1,'2017-09-18','2017-09-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-20','XSHG','2017-09-20',1,1,'2017-09-19','2017-09-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-21','XSHG','2017-09-21',1,1,'2017-09-20','2017-09-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-22','XSHG','2017-09-22',1,1,'2017-09-21','2017-09-21',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-23','XSHG','2017-09-23',0,0,'2017-09-22','2017-09-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-24','XSHG','2017-09-24',0,0,'2017-09-22','2017-09-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-25','XSHG','2017-09-25',1,1,'2017-09-22','2017-09-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-26','XSHG','2017-09-26',1,1,'2017-09-25','2017-09-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-27','XSHG','2017-09-27',1,1,'2017-09-26','2017-09-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-28','XSHG','2017-09-28',1,1,'2017-09-27','2017-09-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-29','XSHG','2017-09-29',1,1,'2017-09-28','2017-09-28',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-09-30','XSHG','2017-09-30',0,1,'2017-09-29','2017-09-29',0,1,1,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-01','XSHG','2017-10-01',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-02','XSHG','2017-10-02',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-03','XSHG','2017-10-03',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-04','XSHG','2017-10-04',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-05','XSHG','2017-10-05',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-06','XSHG','2017-10-06',0,0,'2017-09-29','2017-09-30',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-07','XSHG','2017-10-07',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-08','XSHG','2017-10-08',0,0,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-09','XSHG','2017-10-09',1,1,'2017-09-29','2017-09-30',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-10','XSHG','2017-10-10',1,1,'2017-10-09','2017-10-09',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-11','XSHG','2017-10-11',1,1,'2017-10-10','2017-10-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-12','XSHG','2017-10-12',1,1,'2017-10-11','2017-10-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-13','XSHG','2017-10-13',1,1,'2017-10-12','2017-10-12',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-14','XSHG','2017-10-14',0,0,'2017-10-13','2017-10-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-15','XSHG','2017-10-15',0,0,'2017-10-13','2017-10-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-16','XSHG','2017-10-16',1,1,'2017-10-13','2017-10-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-17','XSHG','2017-10-17',1,1,'2017-10-16','2017-10-16',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-18','XSHG','2017-10-18',1,1,'2017-10-17','2017-10-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-19','XSHG','2017-10-19',1,1,'2017-10-18','2017-10-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-20','XSHG','2017-10-20',1,1,'2017-10-19','2017-10-19',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-21','XSHG','2017-10-21',0,0,'2017-10-20','2017-10-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-22','XSHG','2017-10-22',0,0,'2017-10-20','2017-10-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-23','XSHG','2017-10-23',1,1,'2017-10-20','2017-10-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-24','XSHG','2017-10-24',1,1,'2017-10-23','2017-10-23',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-25','XSHG','2017-10-25',1,1,'2017-10-24','2017-10-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-26','XSHG','2017-10-26',1,1,'2017-10-25','2017-10-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-27','XSHG','2017-10-27',1,1,'2017-10-26','2017-10-26',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-28','XSHG','2017-10-28',0,0,'2017-10-27','2017-10-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-29','XSHG','2017-10-29',0,0,'2017-10-27','2017-10-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-30','XSHG','2017-10-30',1,1,'2017-10-27','2017-10-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-10-31','XSHG','2017-10-31',1,1,'2017-10-30','2017-10-30',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-01','XSHG','2017-11-01',1,1,'2017-10-31','2017-10-31',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-02','XSHG','2017-11-02',1,1,'2017-11-01','2017-11-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-03','XSHG','2017-11-03',1,1,'2017-11-02','2017-11-02',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-04','XSHG','2017-11-04',0,0,'2017-11-03','2017-11-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-05','XSHG','2017-11-05',0,0,'2017-11-03','2017-11-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-06','XSHG','2017-11-06',1,1,'2017-11-03','2017-11-03',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-07','XSHG','2017-11-07',1,1,'2017-11-06','2017-11-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-08','XSHG','2017-11-08',1,1,'2017-11-07','2017-11-07',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-09','XSHG','2017-11-09',1,1,'2017-11-08','2017-11-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-10','XSHG','2017-11-10',1,1,'2017-11-09','2017-11-09',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-11','XSHG','2017-11-11',0,0,'2017-11-10','2017-11-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-12','XSHG','2017-11-12',0,0,'2017-11-10','2017-11-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-13','XSHG','2017-11-13',1,1,'2017-11-10','2017-11-10',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-14','XSHG','2017-11-14',1,1,'2017-11-13','2017-11-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-15','XSHG','2017-11-15',1,1,'2017-11-14','2017-11-14',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-16','XSHG','2017-11-16',1,1,'2017-11-15','2017-11-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-17','XSHG','2017-11-17',1,1,'2017-11-16','2017-11-16',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-18','XSHG','2017-11-18',0,0,'2017-11-17','2017-11-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-19','XSHG','2017-11-19',0,0,'2017-11-17','2017-11-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-20','XSHG','2017-11-20',1,1,'2017-11-17','2017-11-17',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-21','XSHG','2017-11-21',1,1,'2017-11-20','2017-11-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-22','XSHG','2017-11-22',1,1,'2017-11-21','2017-11-21',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-23','XSHG','2017-11-23',1,1,'2017-11-22','2017-11-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-24','XSHG','2017-11-24',1,1,'2017-11-23','2017-11-23',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-25','XSHG','2017-11-25',0,0,'2017-11-24','2017-11-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-26','XSHG','2017-11-26',0,0,'2017-11-24','2017-11-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-27','XSHG','2017-11-27',1,1,'2017-11-24','2017-11-24',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-28','XSHG','2017-11-28',1,1,'2017-11-27','2017-11-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-29','XSHG','2017-11-29',1,1,'2017-11-28','2017-11-28',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-11-30','XSHG','2017-11-30',1,1,'2017-11-29','2017-11-29',0,1,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-01','XSHG','2017-12-01',1,1,'2017-11-30','2017-11-30',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-02','XSHG','2017-12-02',0,0,'2017-12-01','2017-12-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-03','XSHG','2017-12-03',0,0,'2017-12-01','2017-12-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-04','XSHG','2017-12-04',1,1,'2017-12-01','2017-12-01',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-05','XSHG','2017-12-05',1,1,'2017-12-04','2017-12-04',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-06','XSHG','2017-12-06',1,1,'2017-12-05','2017-12-05',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-07','XSHG','2017-12-07',1,1,'2017-12-06','2017-12-06',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-08','XSHG','2017-12-08',1,1,'2017-12-07','2017-12-07',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-09','XSHG','2017-12-09',0,0,'2017-12-08','2017-12-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-10','XSHG','2017-12-10',0,0,'2017-12-08','2017-12-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-11','XSHG','2017-12-11',1,1,'2017-12-08','2017-12-08',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-12','XSHG','2017-12-12',1,1,'2017-12-11','2017-12-11',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-13','XSHG','2017-12-13',1,1,'2017-12-12','2017-12-12',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-14','XSHG','2017-12-14',1,1,'2017-12-13','2017-12-13',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-15','XSHG','2017-12-15',1,1,'2017-12-14','2017-12-14',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-16','XSHG','2017-12-16',0,0,'2017-12-15','2017-12-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-17','XSHG','2017-12-17',0,0,'2017-12-15','2017-12-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-18','XSHG','2017-12-18',1,1,'2017-12-15','2017-12-15',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-19','XSHG','2017-12-19',1,1,'2017-12-18','2017-12-18',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-20','XSHG','2017-12-20',1,1,'2017-12-19','2017-12-19',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-21','XSHG','2017-12-21',1,1,'2017-12-20','2017-12-20',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-22','XSHG','2017-12-22',1,1,'2017-12-21','2017-12-21',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-23','XSHG','2017-12-23',0,0,'2017-12-22','2017-12-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-24','XSHG','2017-12-24',0,0,'2017-12-22','2017-12-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-25','XSHG','2017-12-25',1,1,'2017-12-22','2017-12-22',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-26','XSHG','2017-12-26',1,1,'2017-12-25','2017-12-25',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-27','XSHG','2017-12-27',1,1,'2017-12-26','2017-12-26',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-28','XSHG','2017-12-28',1,1,'2017-12-27','2017-12-27',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-29','XSHG','2017-12-29',1,1,'2017-12-28','2017-12-28',1,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-30','XSHG','2017-12-30',0,0,'2017-12-29','2017-12-29',0,0,0,0);
insert  into `T_TRADE_CALENDAR`(`oid`,`exchangeCD`,`calendarDate`,`isOpen`,`isWork`,`prevTradeDate`,`prevWorkDate`,`isWeekEnd`,`isMonthEnd`,`isQuarterEnd`,`isYearEnd`) values ('XSHG2017-12-31','XSHG','2017-12-31',0,0,'2017-12-29','2017-12-29',0,1,1,1);

/*==============================================================*/
/* common相关表                                        */
/*==============================================================*/

DROP TABLE IF EXISTS `T_COMMON_AUDITEVENT`;

CREATE TABLE `T_COMMON_AUDITEVENT` (
  `oid` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `reqId` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventPath` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventStatus` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `extraMessage` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventOwner` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventSource` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `eventType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventData` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_CALENDAR`;

CREATE TABLE `T_COMMON_CALENDAR` (
  `oid` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sDate` date DEFAULT NULL,
  `eDate` date DEFAULT NULL,
  `operator` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `regenerator` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_EVENTLOG`;

CREATE TABLE `T_COMMON_EVENTLOG` (
  `oid` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `reqId` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventPath` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventStatus` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `extraMessage` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventOwner` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventSource` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `eventType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventData` longtext COLLATE utf8_unicode_ci,
  `eventName` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_MAPPINGINFO`;

CREATE TABLE `T_COMMON_MAPPINGINFO` (
  `oid` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lastUpdate` datetime DEFAULT NULL,
  PRIMARY KEY (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


/*==============================================================*/
/* 资管系统2相关表                                    */
/*==============================================================*/

/*==============================================================*/
/* Table: T_GAM_ASSET_ORDER                                     */
/*==============================================================*/
create table T_GAM_ASSET_ORDER
(
   oid                  varchar(32) not null,
   type                 varchar(32) comment '现金类标的
            非现金类标的',
   portfolioOid         varchar(32),
   liquidAssetOid       varchar(32),
   illiquidAssetOid     varchar(32),
   illiquidAssetRepaymentOid varchar(32),
   orderState           varchar(32) comment '待审核
            审核通过
            审核失败
            已删除',
   dealType             varchar(32) comment '申购 PURCHASE
            认购 SUBSCRIPE
            赎回 REDEEM
            还款 REPAYMENT',
   orderAmount          decimal(16,4),
   tradeShare           decimal(16,4),
   capital              decimal(16,4),
   income               decimal(16,4),
   orderDate            date,
   exceptWay            varchar(32) comment '账面价值法 BOOK_VALUE
            摊余成本法 AMORTISED_COST',
   forceClose           varchar(32) comment 'YES
            NO',
   auditor              varchar(32),
   auditTime            date,
   auditMark            varchar(512),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_AUDIT_LOG                                       */
/*==============================================================*/
create table T_GAM_AUDIT_LOG
(
   OID                  varchar(32) not null,
   portfolioOid         varchar(32),
   portfolioName        varchar(32),
   auditType            varchar(32) comment 'portfolio 新建投资组合；
            netValue 净值校准；
            trade 资产交易；
            income 收益分配；
            chargeFee 费金提取；
            cash 现金校准；
            deviation 损益校准；',
   targetName           varchar(32),
   creater              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   auditState           varchar(32) comment 'pass 通过；
            reject 驳回；',
   auditMark            longtext,
   primary key (OID)
);

/*==============================================================*/
/* Table: T_GAM_CHARGEFEE                                       */
/*==============================================================*/
create table T_GAM_CHARGEFEE
(
   oid                  varchar(32) not null,
   portfolioOid         varchar(32),
   portfolioName        varchar(32),
   classify             varchar(32) comment 'count 累计；
            draw 计提；',
   updateDate           date,
   addFee               decimal(16,4),
   addTrusteeFee        decimal(16,4),
   addManageFee         decimal(16,4),
   trusteeFee           decimal(16,4),
   manageFee            decimal(16,4),
   chargeFee            decimal(16,4),
   askDate              datetime,
   asker                varchar(32),
   feeType              varchar(32),
   getTrusteeFee        decimal(16,4),
   getManageFee         decimal(16,4),
   getChargeFee         decimal(16,4),
   digest               longtext,
   state                varchar(32),
   auditor              varchar(32),
   auditState           varchar(32),
   auditTime            datetime,
   auditMark            longtext,
   creater              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ILLIQUID_ASSET                                  */
/*==============================================================*/
create table T_GAM_ILLIQUID_ASSET
(
   oid                  varchar(32) not null,
   sn                   varchar(32),
   name                 varchar(32),
   type                 varchar(32) comment '现金贷；
            消费分期；
            银票；
            商票；',
   raiseScope           decimal(16,4),
   life                 int,
   lifeUnit             varchar(32) comment '枚举: 
            年: YEAR
            月: MONTH
            日: DAY',
   lifed                int comment '如果 ( 标的期限单位 == 日 ) 
                lifed = 标的期限
            
            如果 ( 标的期限单位 == 月 )
                lifed = 标的期限 X 30
            
            如果 ( 标的期限单位 == 年 )
                lifed = 标的期限 X 合同年天数
            ',
   collectStartDate     date,
   collectEndDate       date,
   collectIncomeRate    decimal(8,4),
   expAror              decimal(8, 4) comment 'annualized rate of return',
   overdueRate          decimal(8,4),
   accrualType          varchar(32) comment '到期一次性还本付息 : A_DEBT_SERVICE_DUE
            按月付息到期还本 : EACH_INTEREST_RINCIPAL_DUE
            等额本息 : FIXED-PAYMENT_MORTGAGE
            等额本金 : FIXED-BASIS_MORTGAGE',
   accrualCycleType     varchar(64) comment '枚举值
            自然年: NATURAL_YEAR
            合同年: CONTRACT_YEAR
            ',
   setDate              date comment '信托类: 成立日期
            票据类: 出票日期
            消费金融类: 债权起息日
            供应链金融产品类: 起息日
            债权及债权收益类: 成立日',
   restStartDate        date comment '信托类: 收益开始日
            票据类: 出票日期
            消费金融类: 债权起息日
            供应链金融产品类: 起息日
            债权及债权收益类: 成立日 + 1天',
   restEndDate          date comment '信托类: 收益截止日(起息日+资产期限)
            票据类: 票面到期日
            消费金融类: 债权止息日(债权起息日+分期期限)
            供应链金融产品类: 止息日
            债权及债权收益类: 成立日 + 1天 + 资产期限',
   overdueDay           date,
   overdueDays          int,
   accrualDate          int,
   purchaseValue        decimal(16,4),
   starValue            decimal(16,4),
   ticketValue          decimal(16,4),
   capitalSettlementDate date,
   returnBackDate       date,
   capitalBackDate      date,
   contractDays         int comment '直接存360或者365',
   expIncome            decimal(16,4),
   applyAmount          decimal(16,4),
   holdShare            decimal(16,4),
   holdIncome           decimal(16,4),
   lockupCapital        decimal(16,4),
   lockupIncome         decimal(16,4),
   lifeState            varchar(32) comment '未开始募集 : B4_COLLECT
            募集期 : COLLECTING
            募集结束 : OVER_COLLECT
            未成立 : UNSETUP
            成立 : SETUP
            起息 : VALUEDATE
            到期 : OVER_VALUEDATE
            本息兑付 : REPAYMENTS
            逾期 : OVERDUE
            逾期兑付 : OVERDUE_REPAYMENTS
            逾期转让 : OVERDUE_TRANSFER
            逾期坏账核销 : OVERDUE_CANCELLATION
            转让 : TRANSFER
            坏账核销 : CANCELLATION',
   state                varchar(32) comment 'create 新建；
            auditing 审核中；
            duration 审核通过；
            reject 驳回；
            invalid 作废；',
   ticketNumber         varchar(64),
   productType          varchar(64),
   drawer               varchar(32),
   drawerAccount        varchar(32),
   drawerrBank          varchar(128),
   payee                varchar(32),
   payeeAccount         varchar(32),
   payeeBank            varchar(128),
   accepter             varchar(32),
   accepterAccount      varchar(32),
   accepterBank         varchar(128),
   borrower             varchar(32),
   city                 varchar(32),
   province             varchar(64),
   borrowerID           varchar(32),
   firstBorrowDate      date,
   repaymentDate        date,
   repaymentTimes       int,
   overdueTimes         int,
   payCapitalFrom       varchar(64),
   holdPorpush          varchar(64),
   investment           varchar(1024),
   subjectRating        varchar(32),
   ratingAgency         varchar(64),
   ratingTime           datetime,
   prior                varchar(64),
   expArorDesc          varchar(1024),
   superiority          varchar(1024),
   financer             varchar(64),
   financerDesc         varchar(1024),
   warrantor            varchar(64),
   warrantorDesc        longtext,
   usages               longtext,
   repayment            longtext,
   risk                 longtext,
   servicePlatform      varchar(64),
   regulatoryBank       varchar(64),
   recordOrganization   varchar(64),
   productSpecifications varchar(128),
   riskDisclosure       varchar(128),
   platformServiceAgreement varchar(128),
   photocopy            varchar(128),
   rejectDesc           varchar(1024),
   collectScore         int,
   collectScoreWeight   decimal(8,4),
   riskRate             decimal(8,4),
   price                decimal(8,4),
   dayProfit            decimal(16,4),
   totalPfofit          decimal(16,4),
   valuations           decimal(16,4),
   netValue             decimal(16,4),
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   court                varchar(128),
   messageDis           varchar(128),
   missKey              varchar(128),
   intengo              varchar(128),
   assure               varchar(128),
   codeImageName        varchar(128),
   bankImageName        varchar(128),
   marryImageName       varchar(128),
   houseImageName       varchar(128),
   mortgageAmount       decimal(3,2),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ILLIQUID_ASSET_PROJECT                          */
/*==============================================================*/
create table T_GAM_ILLIQUID_ASSET_PROJECT
(
   oid                  varchar(32) not null,
   illiquidAssetOid     varchar(32) not null,
   projectName          varchar(64),
   projectType          varchar(32),
   projectTypeName      varchar(32),
   projectManager       varchar(32),
   projectCity          varchar(128),
   funderAddr           varchar(128),
   fundUsage            varchar(1024),
   payment              varchar(1024),
   trustMeasures        varchar(32),
   trustMeasuresName    varchar(32),
   projectAddr          varchar(1024),
   developerRank        varchar(32),
   estateProp           varchar(32),
   estatePropName       varchar(32),
   estateCompletion     varchar(32),
   govLevel             varchar(64),
   warrantor            varchar(32) comment '是: YES
            否: NO',
   warrantorAddr        varchar(1024),
   warrantorCapital     decimal(16,4),
   warrantorDebt        decimal(16,4),
   pledge               varchar(32) comment '是: YES
            否: NO',
   mortgager            varchar(32),
   pledgor              varchar(32),
   pledgeAddr           varchar(1024),
   pledgeType           varchar(1024),
   pledgeWeight         varchar(64),
   pledgeName           varchar(1024),
   pledgeAssessment     varchar(64),
   pledgeValuation      decimal(16,4),
   pledgePriority       varchar(32),
   pledgeRatio          decimal(16,4),
   margin               decimal(16,4),
   projectScale         decimal(16,4),
   financeCosts         decimal(16,4),
   spv                  varchar(64),
   spvTariff            decimal(8,4),
   projectGrade         varchar(32) comment 'AAA、AA+、AA、AA-、A+、A、A-、BBB+、BBB、BBB-',
   gradeAssessment      varchar(32) comment '东方金诚、中诚信、联合、大公国际、上海新世纪、其他',
   gradeTime            datetime,
   guaranteeModeOid     varchar(32),
   guaranteeModeTitle   varchar(64),
   guaranteeModeWeight  decimal(8,4),
   guaranteeModeExpireOid varchar(32),
   guaranteeModeExpireTitle varchar(64),
   guaranteeModeExpireWeight decimal(8,4),
   mortgageModeOid      varchar(32),
   mortgageModeTitle    varchar(64),
   mortgageModeWeight   decimal(8,4),
   mortgageModeExpireOid varchar(32),
   mortgageModeExpireTitle varchar(32),
   mortgageModeExpireWeight decimal(8,4),
   hypothecation        varchar(32) comment '是: YES
            否: NO',
   hypothecationModeOid varchar(32),
   hypothecationModeTitle varchar(64),
   hypothecationModeWeight decimal(8,4),
   hypothecationModeExpireOid varchar(32),
   hypothecationModeExpireTitle varchar(32),
   hypothecationModeExpireWeight decimal(8,4),
   riskFactor           decimal(8,4),
   creator              varchar(32),
   operator             varchar(32),
   createTime           datetime,
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ILLIQUID_OVERDUE                                */
/*==============================================================*/
create table T_GAM_ILLIQUID_OVERDUE
(
   oid                  varchar(32) not null,
   illiquidAssetOid     varchar(32) not null,
   overdueStartDate     date not null,
   overdueEndDate       date,
   overdueDays          int not null,
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_ILLIQUID_REPAYMENT_PLAN                         */
/*==============================================================*/
create table T_GAM_ILLIQUID_REPAYMENT_PLAN
(
   OID                  varchar(32) not null,
   illiquidAssetOid     varchar(32),
   issue                int,
   repaymentType        varchar(32),
   intervalDays         int,
   startDate            date,
   endDate              date,
   principal            decimal(16,6),
   interest             decimal(16,6),
   dueDate              date,
   repayment            decimal(16,4),
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   operateTime          datetime,
   primary key (OID)
);

/*==============================================================*/
/* Table: T_GAM_INVEST_SCOPE                                    */
/*==============================================================*/
create table T_GAM_INVEST_SCOPE
(
   oid                  varchar(32) not null,
   relationOid          varchar(32),
   assetTypeOid         varchar(32),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_LIQUID_ASSET                                    */
/*==============================================================*/
create table T_GAM_LIQUID_ASSET
(
   oid                  varchar(32) not null,
   sn                   varchar(32),
   name                 varchar(64),
   type                 varchar(32) comment 'ETF
            LOF
            CASH: 货币型
             ',
   operationMode        varchar(32) comment 'O为开放式, Open的首字母, 不是数字0
            C为封闭式, Close的首字母',
   confirmDays          int,
   perfBenchmark        varchar(1024),
   incomeSchedule       varchar(32) comment '日结: DAY
            月结: MONTH',
   exchangeCd           varchar(32),
   managerName          varchar(64),
   managementCompany    varchar(64),
   managementFullName   varchar(64),
   custodian            varchar(64),
   custodianFullName    varchar(64),
   investField          varchar(1024),
   investTarget         varchar(1024),
   holdPorpush          varchar(32),
   riskLevel            varchar(32) comment '低风险 : LOW
            中风险 : MID
            高风险 : HIGH
            ',
   valueDate            date,
   profitDate           date,
   dailyProfitDate      date,
   dailyProfit          decimal(8,4),
   weeklyYield          decimal(8,4),
   dividendType         varchar(32) comment '红利再投 现金分红等
            ',
   baseAmount           decimal(16,4),
   baseYield            decimal(8,4),
   yield                decimal(8,4),
   contractDays         int,
   state                varchar(32) comment 'create 创建；
            auditing 审核中；
            duration 审核通过；
            reject 驳回；
            invalid 作废；',
   applyAmount          decimal(16,4),
   holdShare            decimal(16,4),
   lockupShare          decimal(16,4),
   price                decimal(8,4),
   dayProfit            decimal(16,4),
   totalPfofit          decimal(16,4),
   valuations           decimal(16,4),
   netValue             decimal(16,4),
   lastValueDate        date,
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_LIQUID_ASSET_LOG                                */
/*==============================================================*/
create table T_GAM_LIQUID_ASSET_LOG
(
   oid                  varchar(32) not null,
   liquidAssetOid       varchar(32),
   eventTime            date,
   eventType            varchar(32),
   operator             varchar(32),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_LIQUID_ASSET_YIELD                              */
/*==============================================================*/
create table T_GAM_LIQUID_ASSET_YIELD
(
   oid                  varchar(32) not null,
   liquidAssetOid       varchar(32),
   profitDate           date,
   dailyProfit          decimal(8,4),
   weeklyYield          decimal(8,4),
   creator              varchar(32),
   createTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO                                       */
/*==============================================================*/
create table T_GAM_PORTFOLIO
(
   oid                  varchar(32) not null,
   name                 varchar(128),
   spvOid               varchar(32),
   liquidRate           decimal(8,6),
   illiquidRate         decimal(8,6),
   cashRate             decimal(8,6),
   liquidFactRate       decimal(16,6),
   illiquidFactRate     decimal(16,6),
   cashFactRate         decimal(16,6),
   manageRate           decimal(16,6),
   trusteeRate          decimal(16,6),
   calcBasis            int comment '360 或365',
   organization         varchar(64),
   planName             varchar(64),
   bank                 varchar(32),
   account              varchar(32),
   contact              varchar(32),
   telephone            varchar(32),
   nav                  decimal(16,4),
   shares               decimal(16,4),
   netValue             decimal(16,4),
   baseDate             date,
   dimensions           decimal(16,4),
   cashPosition         decimal(16,4),
   liquidDimensions     decimal(16,4),
   illiquidDimensions   decimal(16,4),
   deviationValue       decimal(16,4),
   freezeCash           decimal(16,4),
   dimensionsDate       date,
   state                varchar(32) comment 'create 新建待审核；
            duration 审核通过；
            reject 驳回;
            invalid 作废;',
   creater              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   auditor              varchar(32),
   auditMark            longtext,
   auditTime            datetime,
   drawedChargefee      decimal(16,4),
   countintChargefee    decimal(16,4),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ADJUST                                */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ADJUST
(
   oid                  varchar(32) not null,
   portfolioOid         varchar(32),
   adjustDate           date,
   type                 varchar(32) comment 'cash 现金校准；
            income 损益校准；',
   amount               decimal(16,4),
   state                varchar(32) comment 'audit 申请待审核；
            done 审核通过待；
            reject 审核未通过；',
   asker                varchar(32),
   askVolume            decimal(16,4),
   askCapital           decimal(16,4),
   askTime              datetime,
   auditor              varchar(32),
   auditVolume          decimal(16,4),
   auditCapital         decimal(16,4),
   auditState           varchar(32),
   auditMark            longtext,
   auditTime            datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ESTIMATE                              */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ESTIMATE
(
   oid                  varchar(32) not null,
   portfolioOid         varchar(32) not null,
   liquidEstimate       decimal(16,4),
   illiquidEstimate     decimal(16,4),
   cashEstimate         decimal(16,4),
   manageChargefee      decimal(16,4),
   trusteeChargefee     decimal(16,4),
   chargefee            decimal(16,4),
   estimateDate         date,
   estimateTime         datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD                         */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD
(
   oid                  varchar(32) not null,
   illiquidAssetOid     varchar(32),
   portfolioOid         varchar(32),
   investDate           date,
   valueDate            date,
   expectValue          decimal(16,6) comment '估值 = 持有份额 X 单位净值',
   holdIncome           decimal(16,6),
   holdShare            decimal(16,4),
   natValue             decimal(8,4) comment '= 估值/持有份额',
   lockupCapital        decimal(16,6),
   lockupIncome         decimal(16,6),
   totalPfofit          decimal(16,6),
   newValueDate         date,
   newPfofit            decimal(16,6),
   exceptWay            varchar(32) comment '账面价值法 BOOK_VALUE
            摊余成本法 AMORTISED_COST',
   holdState            varchar(32) comment '持仓中 HOLDING
            已平仓: CLOSED',
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD_ESTIMATE                */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD_ESTIMATE
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   illiquidAssetOid     varchar(32),
   portfolioOid         varchar(32),
   portfolioEstimateOid varchar(32),
   lastHoldShare        decimal(16,4),
   lastHoldIncome       decimal(16,4),
   lastEstimate         decimal(16,4),
   lastUnitNet          decimal(16,8),
   holdShare            decimal(16,4),
   holdIncome           decimal(16,4),
   estimate             decimal(16,4),
   unitNet              decimal(16,8),
   profit               decimal(16,4),
   profitRate           decimal(16,8),
   lifeState            varchar(32),
   estimateDate         date,
   estimateTime         datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART                    */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   illiquidAssetOid     varchar(32),
   portfolioOid         varchar(32),
   orderOid             varchar(32),
   expectValue          decimal(16,6),
   holdShare            decimal(16,6),
   holdIncome           decimal(16,6),
   lockupCapital        decimal(16,6),
   lockupIncome         decimal(16,6),
   unitNet              decimal(16,8),
   investDate           datetime,
   valueDate            date comment '建仓日期后一天',
   totalPfofit          decimal(16,6),
   newValueDate         date,
   newProfit            decimal(16,6),
   holdState            varchar(32) comment '持仓 HOLDING
            平仓 CLOSED',
   exceptWay            varchar(32) comment '账面价值法 BOOK_VALUE
            摊余成本法 AMORTISED_COST',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART_ESTIMATE           */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART_ESTIMATE
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   partOid              varchar(32),
   illiquidAssetOid     varchar(32),
   holdEstimateOid      varchar(32),
   portfolioOid         varchar(32),
   portfolioEstimateOid varchar(32),
   lastHoldShare        decimal(16,4),
   lastHoldIncome       decimal(16,4),
   lastEstimate         decimal(16,4),
   lastUnitNet          decimal(16,8),
   holdShare            decimal(16,4),
   holdIncome           decimal(16,4),
   estimate             decimal(16,4),
   unitNet              decimal(16,8),
   profit               decimal(16,4),
   profitRate           decimal(16,8),
   exceptWay            varchar(32) comment '账面价值法 BOOK_VALUE
            摊余成本法 AMORTISED_COST',
   lifeState            varchar(32),
   estimateDate         date,
   estimateTime         datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART_REPAYMENT          */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART_REPAYMENT
(
   oid                  varchar(32) not null,
   holdPartOid          varchar(32),
   repaymentOid         varchar(32),
   orderOid             varchar(32),
   repaymentCapital     decimal(16,6),
   repaymentIncome      decimal(16,6),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_ILLIQUID_HOLD_REPAYMENT               */
/*==============================================================*/
create table T_GAM_PORTFOLIO_ILLIQUID_HOLD_REPAYMENT
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   issue                int,
   repaymentType        varchar(32),
   intervalDays         int,
   startDate            date,
   dueDate              date,
   endDate              date,
   principalPlan        decimal(16,6),
   interestPlan         decimal(16,6),
   repaymentPlan        decimal(16,4),
   principal            decimal(16,4),
   interest             decimal(16,4),
   repayment            decimal(16,4),
   lastIssue            varchar(32) comment '是: YES
            否: NO',
   state                varchar(32) comment '未到期 : UNDUE
            已到期未还款 : PAYING
            还款待审核 : AUDIT
            已还款 : PAID',
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   operateTime          datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_INVEST_LOSSES                         */
/*==============================================================*/
create table T_GAM_PORTFOLIO_INVEST_LOSSES
(
   oid                  varchar(32) not null,
   type                 varchar(32) comment '现金类标的: LIQUID
            非现金类标的: ILLIQUID',
   portfolioOid         varchar(32),
   liquidAssetOid       varchar(32),
   illiquidAssetOid     varchar(32),
   illiquidAssetRepaymentOid varchar(32),
   orderOid             varchar(32),
   orderDate            date,
   investCapital        decimal(16,4),
   investIncome         decimal(16,4),
   holdShare            decimal(16,4),
   selloutShare         decimal(16,4),
   selloutPrice         decimal(16,4),
   losses               decimal(16,4),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_LIQUID_HOLD                           */
/*==============================================================*/
create table T_GAM_PORTFOLIO_LIQUID_HOLD
(
   oid                  varchar(32) not null,
   liquidAssetOid       varchar(32),
   portfolioOid         varchar(32),
   investDate           date,
   valueDate            date,
   holdAmount           decimal(16,6) comment '当前最新估值金额',
   holdShare            decimal(16,6),
   investAmount         decimal(16,6),
   investCome           decimal(16,6),
   totalPfofit          decimal(16,6),
   newValueDate         date,
   newPfofit            decimal(16,6),
   lockupAmount         decimal(16,6),
   holdState            varchar(32) comment '持仓 HOLDING
            平仓 CLOSED',
   creator              varchar(32),
   createTime           datetime,
   operator             varchar(32),
   updateTime           datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_LIQUID_HOLD_ESTIMATE                  */
/*==============================================================*/
create table T_GAM_PORTFOLIO_LIQUID_HOLD_ESTIMATE
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   liquidAssetOid       varchar(32),
   portfolioOid         varchar(32),
   portfolioEstimateOid varchar(32),
   lastEstimate         decimal(16,4),
   lastPriceRatio       decimal(16,8),
   lastUnitNet          decimal(16,8),
   basic                decimal(16,8),
   estimate             decimal(16,4),
   profit               decimal(16,4),
   priceRatio           decimal(16,8),
   unitNet              decimal(16,8),
   profitRate           decimal(16,8),
   estimateDate         date,
   estimateTime         datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_LIQUID_HOLD_PART                      */
/*==============================================================*/
create table T_GAM_PORTFOLIO_LIQUID_HOLD_PART
(
   oid                  varchar(32) not null,
   holdOid              varchar(32),
   liquidAssetOid       varchar(32),
   portfolioOid         varchar(32),
   orderOid             varchar(32),
   holdAmount           decimal(16,6),
   holdShare            decimal(16,6),
   investAmount         decimal(16,6),
   investShare          decimal(16,6),
   freezeHoldAmount     decimal(16,6),
   unitNet              decimal(16,8),
   priceRatio           decimal(16,8),
   investDate           datetime,
   valueDate            date comment '建仓日期后一天',
   holdState            varchar(32) comment '持仓 HOLDING
            平仓 CLOSED',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_LIQUID_HOLD_PART_ESTIMATE             */
/*==============================================================*/
create table T_GAM_PORTFOLIO_LIQUID_HOLD_PART_ESTIMATE
(
   oid                  varchar(32) not null,
   partOid              varchar(32) not null,
   holdOid              varchar(32),
   holdEstimateOid      varchar(32),
   liquidAssetOid       varchar(32),
   portfolioOid         varchar(32),
   portfolioEstimateOid varchar(32),
   lastEstimate         decimal(16,4),
   lastPriceRatio       decimal(16,8),
   lastUnitNet          decimal(16,8),
   basic                decimal(16,8),
   profit               decimal(16,4),
   estimate             decimal(16,4),
   priceRatio           decimal(16,8),
   unitNet              decimal(16,8),
   profitRate           decimal(16,8),
   estimateDate         date,
   estimateTime         datetime,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_LIQUID_HOLD_PART_REDEEM               */
/*==============================================================*/
create table T_GAM_PORTFOLIO_LIQUID_HOLD_PART_REDEEM
(
   oid                  varchar(32) not null,
   redeemOid            varchar(32),
   partsOid             varchar(32),
   redeemShare          decimal(16,6),
   redeemAmount         decimal(16,6),
   redeemCapital        decimal(16,6),
   redeemIncome         decimal(16,6),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_NET_CORRECT                           */
/*==============================================================*/
create table T_GAM_PORTFOLIO_NET_CORRECT
(
   oid                  varchar(32) not null,
   portfolioOid         varchar(32),
   orderOid             varchar(32),
   netDate              date,
   share                decimal(16,6),
   nav                  decimal(16,6),
   net                  decimal(16,6),
   lastShare            decimal(16,6),
   lastNav              decimal(16,6),
   lastNet              decimal(16,6),
   chargeAmount         decimal(16,6),
   withdrawAmount       decimal(16,6),
   tradeAmount          decimal(16,6),
   netYield             decimal(8,4),
   primary key (oid)
);

/*==============================================================*/
/* Table: T_GAM_PORTFOLIO_NET_CORRECT_ORDER                     */
/*==============================================================*/
create table T_GAM_PORTFOLIO_NET_CORRECT_ORDER
(
   oid                  varchar(32) not null,
   portfolioOid         varchar(32),
   netDate              date,
   share                decimal(16,6),
   nav                  decimal(16,6),
   net                  decimal(16,6),
   chargeAmount         decimal(16,6),
   withdrawAmount       decimal(16,6),
   tradeAmount          decimal(16,6),
   netYield             decimal(16,6),
   creator              varchar(32),
   createTime           datetime,
   auditor              varchar(32),
   auditTime            datetime,
   orderState           varchar(32) comment '未审核 NONE
            已审核 PASS
            已驳回 FAIL',
   auditMark            varchar(512),
   primary key (oid)
);



