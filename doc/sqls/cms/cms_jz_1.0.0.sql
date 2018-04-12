/*==============================================================*/
/* Table: T_PLATFORM_ACTIVITY                                   */
/*==============================================================*/
create table T_PLATFORM_ACTIVITY
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   title                varchar(50),
   picUrl               varchar(100),
   location             varchar(32) comment 'left:左 right:右 carousel:轮播',
   linkType             int comment '0:链接 1:跳转',
   linkUrl              varchar(256),
   toPage               varchar(30) comment 'newcomer: 新手 seckill: 秒杀',
   status               varchar(50) comment 'on:已上架  off ：已下架   pending：待审核 reviewed 已审核 reject 已驳回',
   creator              varchar(50),
   createTime           timestamp default CURRENT_TIMESTAMP,
   review               varchar(50),
   reviewTime           datetime,
   publisher            varchar(50),
   publishTime          datetime,
   reviewRemark         varchar(200),
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_ACTIVITY
(
   status
);

/*==============================================================*/
/* Table: T_PLATFORM_ACTRULE                                    */
/*==============================================================*/
create table T_PLATFORM_ACTRULE
(
   oid                  varchar(32) not null,
   actRuleTypeId        varchar(32),
   content              longtext,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_ACTRULE_TYPE                               */
/*==============================================================*/
create table T_PLATFORM_ACTRULE_TYPE
(
   id                   varchar(32) not null,
   name                 varchar(32),
   primary key (id)
);

INSERT INTO `T_PLATFORM_ACTRULE_TYPE` (`id`, `name`) VALUES('INVITE','邀请规则');

/*==============================================================*/
/* Table: T_PLATFORM_ADVICE                                     */
/*==============================================================*/
create table T_PLATFORM_ADVICE
(
   oid                  varchar(32) not null,
   tabOid               varchar(32),
   userID               varchar(32),
   userName             varchar(32),
   phoneType            varchar(32) comment 'AZ:安卓，IP:ios',
   content              text,
   createTime           timestamp default CURRENT_TIMESTAMP,
   operator             varchar(32),
   remark               varchar(256),
   dealStatus           varchar(32) comment 'ok:已处理，no:未处理',
   dealTime             timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_ADVICE_TAB                                 */
/*==============================================================*/
create table T_PLATFORM_ADVICE_TAB
(
   oid                  varchar(32) not null,
   name                 varchar(64),
   delStatus            varchar(32) comment 'yes:已删除，no:未删除',
   operator             varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_BANKCARD                                   */
/*==============================================================*/
create table T_PLATFORM_BANKCARD
(
   oid                  varchar(32) not null,
   bankCode             varchar(32) comment '银行编号',
   bankName             varchar(32) comment '银行名称',
   peopleBankCode       varchar(32) comment '人行编码',
   bankLogo             varchar(256) comment '银行logo',
   bankBigLogo          varchar(256) comment '银行长logo',
   bgColor              varchar(128) comment '底色  颜色编码 #FFFFFF',
   withdrawDayLimit     varchar(32) comment '单日提现限额',
   payDayLimit          varchar(32) comment '单日支付限额',
   withdrawOneLimit     varchar(32) comment '单笔提现限额',
   payOneLimit          varchar(32) comment '单笔支付限额',
   withdrawMoonLimit    varchar(32) comment '单月提现额度',
   payMoonLimit         varchar(32) comment '单月支付额度',
   status               varchar(32) comment '状态 pass:通过，refused:驳回，toApprove:待审批',
   creator              varchar(32) comment '编辑者',
   approver             varchar(32) comment '审核者',
   approveRemark        varchar(256) comment '审核意见',
   approveTime          datetime comment '审批时间',
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

insert  into `T_PLATFORM_BANKCARD`(`oid`,`bankCode`,`bankName`,`bankLogo`,`bgColor`,`withdrawDayLimit`,`payDayLimit`,`withdrawOneLimit`,`payOneLimit`,`STATUS`,`creator`,`approver`,`approveRemark`,`approveTime`,`createTime`,`updateTime`,`withdrawMoonLimit`,`payMoonLimit`,`bankBigLogo`,`peopleBankCode`) values ('402881fd5a2607cc015a2698efb30007','BOC','中国银行','/yupload/b531bb0c9634bf6a0ad8f6495ee30216.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','500000','0','50000','pass',NULL,NULL,'1','2017-03-03 14:29:25','2017-02-10 13:57:21','2017-03-03 14:52:13','0','15000000','/yupload/32d844eceabb17e84b602a923cde9de4.png','104'),('402881fd5a2607cc015a269b91830008','CCB','中国建设银行','/yupload/07a09feab692d3955ba5775a08da4e4c.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','500000','0','200000','pass',NULL,NULL,'1','2017-03-03 14:29:23','2017-02-10 14:00:14','2017-03-03 14:52:07','0','15000000','/yupload/71169988e5eceb15b6b5b4849efd153a.png','105'),('402881fd5a2607cc015a269c65190009','ABC','中国农业银行','/yupload/9a36ac73160231124140aeb70fcbe62a.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','500000','0','200000','pass',NULL,NULL,'1','2017-03-03 14:29:20','2017-02-10 14:01:08','2017-03-03 14:52:06','0','15000000','/yupload/c5779da2404d3b3eca4dad16a25fcc83.png','103'),('402881fd5a2607cc015a269d4d90000a','ICBC','中国工商银行','/yupload/a5b9897bff1c22dc98d8a44a5d7581b7.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','50000','0','50000','pass',NULL,NULL,'1','2017-03-03 14:29:14','2017-02-10 14:02:08','2017-03-03 14:52:06','0','1500000','/yupload/6e218b1927e3f01bd26c51efbdfdc4cd.png','102'),('402881fd5a2607cc015a269e91bf000b','CITIC','中信银行','/yupload/b80a918e74b69a244f5f74bf3d1ddec5.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','10000','0','10000','pass',NULL,NULL,'1','2017-03-03 14:29:00','2017-02-10 14:03:31','2017-03-03 14:52:06','0','20000','/yupload/b3bbbc3faa99e15451276ce652e8fa17.png','302'),('402881fd5a2607cc015a269fb7db000c','SZPAB','平安银行','/yupload/cbe826d68e162e2fa091d0ac7eceadc6.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','1000000','0','100000','pass',NULL,NULL,'1','2017-03-03 14:29:06','2017-02-10 14:04:46','2017-03-03 14:52:04','0','30000000','/yupload/fc7e549a3f4285ae7c5c7b82fb6a6f08.png','307'),('402881fd5a2607cc015a26a05b27000d','CMB','招商银行','/yupload/0651d72cb5d66582e06822f3594cd47e.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','100000','0','100000','pass',NULL,NULL,'1','2017-03-03 14:28:58','2017-02-10 14:05:28','2017-03-03 14:52:04','0','3000000','/yupload/b5ec2f87d5bd5df8fe896902fcce05e6.png','308'),('402881fd5a2607cc015a26a2d12b000e','COMM','交通银行','/yupload/a8511129f425571c9f4cdc5abbbceeec.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','9999','0','9999','pass',NULL,NULL,'1','2017-03-03 14:28:54','2017-02-10 14:08:09','2017-03-03 14:52:03','0','299970','/yupload/e610c6e7df95ff21372968b307c58e8e.png','301'),('402881fd5a2607cc015a26a84a5e000f','CEB','中国光大银行','/yupload/e9c00f51b5227db220100fab24b2de36.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','1000000','0','100000','pass',NULL,NULL,'1','2017-03-03 14:28:50','2017-02-10 14:14:08','2017-03-03 14:52:03','0','30000000','/yupload/0c8411da0093c4fe969911b55d463474.png','303'),('402881fd5a2607cc015a26a8e6300010','CMBC','中国民生银行','/yupload/17f84442ce5a538de873401dae290725.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','100000000','0','20000000','pass',NULL,NULL,'1','2017-03-03 14:36:43','2017-02-10 14:14:48','2017-03-03 14:52:03','0','3000000000','/yupload/99061eb5fc67204ab018594ae2c55e21.png','305'),('402881fd5a2607cc015a26a9cacf0011','GDB','广发银行','/yupload/30e0e7acbf22ec6362dd8b796507896c.png','linear-gradient(-126deg, #e75b65 0%, #e9517d 99%)','0','100000000','0','20000000','pass',NULL,NULL,'1','2017-03-03 14:28:44','2017-02-10 14:15:46','2017-03-03 14:52:02','0','3000000000','/yupload/aa54fea6ab6a979d3f1c7d947592b82e.png','306'),('402881fd5a2607cc015a26aa8a110012','CIB','兴业银行','/yupload/d5682389d54592545e7b0db53817d28b.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','50000','0','50000','pass',NULL,NULL,'1','2017-03-03 14:28:40','2017-02-10 14:16:35','2017-03-03 14:52:01','0','1500000','/yupload/cf440ef86bcfdb0a04eb57e88442d8d5.png','309'),('402881fd5a2607cc015a26ab39ef0013','PSBC','中国邮政储蓄银行','/yupload/e32ba6f0b19b404abc72d1bb2c201328.png','linear-gradient(-126deg, #4f83be 0%, #3c5ea9 99%)','0','5000','0','5000','pass',NULL,NULL,'1','2017-03-03 14:15:15','2017-02-10 14:17:20','2017-03-03 14:52:02','0','150000','/yupload/a3440f8cb3644ca831e6af9459a31d42.png','403');

/*==============================================================*/
/* Table: T_PLATFORM_BANNER                                     */
/*==============================================================*/
create table T_PLATFORM_BANNER
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   title                varchar(64),
   imageUrl             varchar(256),
   linkUrl              varchar(256),
   isLink               int comment '0-链接  1-跳转',
   toPage               varchar(32) comment 'T1:活期，T2定期:，T3:注册',
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   releaseStatus        varchar(32) comment 'ok:已发布，no:未发布，wait::待发布',
   sorting              int,
   operator             varchar(32),
   approveOpe           varchar(32),
   releaseOpe           varchar(32),
   remark               varchar(256),
   approveTime          datetime,
   releaseTime          datetime,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_BANNER
(
   releaseStatus
);

/*==============================================================*/
/* Table: T_PLATFORM_CHANNEL                                    */
/*==============================================================*/
create table T_PLATFORM_CHANNEL
(
   oid                  varchar(32) not null,
   code                 varchar(32),
   name                 varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_CONFIGURABLE_ELEMENTS                      */
/*==============================================================*/
create table T_PLATFORM_CONFIGURABLE_ELEMENTS
(
   oid                  varchar(32) not null,
   code                 varchar(32),
   name                 varchar(32),
   type                 varchar(32) comment 'button按钮、link链接、data数据',
   isDisplay            varchar(32) comment 'yes显示 、no不显示',
   content              varchar(256),
   operator             varchar(32),
   creator              varchar(32),
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

insert  into `T_PLATFORM_CONFIGURABLE_ELEMENTS`(`oid`,`code`,`name`,`type`,`isDisplay`,`content`,`operator`,`creator`,`createTime`,`updateTime`) values ('402881fd596dfda501596e3d37b60108','hotline','服务热线','data','yes','400-611-8088','002799cb39d911e6860800163e0021b3','','2017-01-05 18:47:03','2017-03-08 09:57:01');

/*==============================================================*/
/* Table: T_PLATFORM_IMAGES                                     */
/*==============================================================*/
create table T_PLATFORM_IMAGES
(
   oid                  varchar(32) not null,
   imgName              varchar(64),
   imgUrl               varchar(128),
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_INFORMATION                                */
/*==============================================================*/
create table T_PLATFORM_INFORMATION
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   title                varchar(100),
   type                 varchar(50),
   summary              varchar(200),
   content              text,
   url                  varchar(200),
   thumbnailUrl         varchar(200),
   origin               varchar(200),
   isHome               int comment '1-是  2-否',
   status               varchar(50) comment 'published：已发布 publishing：待发布  pending：待审核 reject 已驳回 off:下架',
   createTime           timestamp default CURRENT_TIMESTAMP,
   editor               varchar(100),
   editTime             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   review               varchar(100),
   reviewTime           datetime,
   publisher            varchar(100),
   publishTime          datetime,
   reviewRemark         varchar(200),
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_INFORMATION
(
   type,
   status
);

/*==============================================================*/
/* Table: T_PLATFORM_INFORMATION_TYPE                           */
/*==============================================================*/
create table T_PLATFORM_INFORMATION_TYPE
(
   oid                  varchar(32) not null,
   name                 varchar(50),
   sort                 bigint(10),
   status               bigint(10) comment '0：关闭  1：启用',
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_INFORMATION_TYPE
(
   status
);

/*==============================================================*/
/* Table: T_PLATFORM_MAIL                                       */
/*==============================================================*/
create table T_PLATFORM_MAIL
(
   oid                  varchar(32) not null,
   userOid              varchar(32) comment '所属用户',
   phone                varchar(32) comment '手机号码',
   mailType             varchar(32) comment '类型  all全站信息   person个人信息',
   mesType              varchar(32) comment '内容类型  all全站信息  person个人信息',
   mesTitle             varchar(32) comment '标题',
   mesContent           text comment '内容',
   isRead               varchar(32) comment '是否已读 is是 no否',
   status               varchar(32) comment '状态  toApprove待审核  pass已发送  refused已驳回  delete已删除',
   requester            varchar(32) comment '申请人',
   approver             varchar(32) comment '审核人',
   approveRemark        varchar(256) comment '审核意见',
   remark               varchar(256),
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: mail_Index_1                                          */
/*==============================================================*/
create index mail_Index_1 on T_PLATFORM_MAIL
(
   userOid,
   status
);

/*==============================================================*/
/* Index: mail_Index_2                                          */
/*==============================================================*/
create index mail_Index_2 on T_PLATFORM_MAIL
(
   userOid,
   isRead,
   status
);

/*==============================================================*/
/* Index: mail_Index_3                                          */
/*==============================================================*/
create index mail_Index_3 on T_PLATFORM_MAIL
(
   mailType,
   status
);

/*==============================================================*/
/* Table: T_PLATFORM_NOTICE                                     */
/*==============================================================*/
create table T_PLATFORM_NOTICE
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   title                varchar(64),
   linkUrl              varchar(256),
   linkHtml             text,
   subscript            varchar(32) comment 'New:新 Hot:热  无:无',
   sourceFrom           varchar(64),
   page                 varchar(32) comment 'is:是 no:否',
   top                  varchar(32) comment '1:置顶 2:不置顶',
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   releaseStatus        varchar(32) comment 'ok:已上架，no:已下架，wait:待发布',
   operator             varchar(32),
   approveOpe           varchar(32),
   releaseOpe           varchar(32),
   remark               varchar(256),
   approveTime          datetime,
   releaseTime          datetime,
   onShelfTime          date,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_NOTICE
(
   page,
   releaseStatus
);

/*==============================================================*/
/* Index: Index_2                                               */
/*==============================================================*/
create index Index_2 on T_PLATFORM_NOTICE
(
   releaseStatus
);

/*==============================================================*/
/* Table: T_PLATFORM_PARTNER                                    */
/*==============================================================*/
create table T_PLATFORM_PARTNER
(
   oid                  varchar(32) not null,
   channelOid           varchar(32),
   title                varchar(64),
   imageUrl             varchar(256),
   linkUrl              varchar(256),
   isLink               varchar(32) comment '是否跳转链接is  no',
   isNofollow           varchar(32) comment '是否追踪链接',
   approveStatus        varchar(32) comment 'pass:通过，refused:驳回，toApprove:待审批',
   releaseStatus        varchar(32) comment 'ok:已发布，no:未发布，wait::待发布',
   sorting              int,
   operator             varchar(32),
   approveOpe           varchar(32),
   releaseOpe           varchar(32),
   remark               varchar(256),
   approveTime          datetime,
   releaseTime          datetime,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_PROTOCOL                                   */
/*==============================================================*/
create table T_PLATFORM_PROTOCOL
(
   oid                  varchar(32) not null,
   protocolTypeId       varchar(32),
   content              longtext,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_PROTOCOL_TYPE                              */
/*==============================================================*/
create table T_PLATFORM_PROTOCOL_TYPE
(
   id                   varchar(32) not null,
   name                 varchar(32),
   primary key (id)
);

INSERT INTO `T_PLATFORM_PROTOCOL_TYPE` (`id`, `name`) VALUES('REGIST','注册协议');
INSERT INTO `T_PLATFORM_PROTOCOL_TYPE` (`id`, `name`) VALUES('PRODUCT','产品协议');
INSERT INTO `T_PLATFORM_PROTOCOL_TYPE` (`id`, `name`) VALUES('BANK','绑卡快捷支付协议');
INSERT INTO `T_PLATFORM_PROTOCOL_TYPE` (`id`, `name`) VALUES('PLATFORM','绑卡平台服务协议');

/*==============================================================*/
/* Table: T_PLATFORM_PUSH                                       */
/*==============================================================*/
create table T_PLATFORM_PUSH
(
   oid                  varchar(32) not null,
   title                varchar(50),
   status               varchar(50) comment 'on:已推送，reviewed:已审核，pending:待审核，reject驳回',
   type                 varchar(50) comment 'information:资讯，notice:公告，HQ:活期，DQ:定期，activity:活动',
   url                  varchar(200),
   creator              varchar(50),
   createTime           timestamp default CURRENT_TIMESTAMP,
   review               varchar(50),
   reviewTime           datetime,
   pusher               varchar(50),
   pushTime             datetime,
   reviewRemark         varchar(200),
   summary              varchar(200),
   pushType             varchar(32) comment 'all 全站   person 个人',
   pushUserOid          varchar(32) comment '推送目标用户oid',
   pushUserAcc          varchar(32) comment '推送目标用户账号',
   primary key (oid)
);

/*==============================================================*/
/* Table: T_PLATFORM_VERSION                                    */
/*==============================================================*/
create table T_PLATFORM_VERSION
(
   oid                  varchar(32) not null,
   versionNo            varchar(50),
   fileName             varchar(50),
   fileUrl              varchar(100),
   versionSize          varchar(10),
   description          varchar(300),
   upgradeType          varchar(32) comment 'increment:增量 version:现有版本',
   compulsory           bigint(2) comment '0:否,1:是',
   checkInterval        bigint(2),
   system               varchar(32) comment 'ios:ios，android:安卓，increment:增量',
   status               varchar(50) comment 'reviewed:待发布，on:已发布',
   creator              varchar(50),
   createTime           timestamp default CURRENT_TIMESTAMP,
   review               varchar(50),
   reviewTime           datetime,
   publisher            varchar(50),
   publishTime          datetime,
   expectPublishTime    date,
   reviewRemark         varchar(200),
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_PLATFORM_VERSION
(
   status,
   publishTime,
   upgradeType
);

/*==============================================================*/
/* Index: Index_2                                               */
/*==============================================================*/
create index Index_2 on T_PLATFORM_VERSION
(
   status,
   publishTime,
   upgradeType,
   system
);

alter table T_PLATFORM_ACTIVITY add constraint FK_Reference_5 foreign key (channelOid)
      references T_PLATFORM_CHANNEL (oid) on delete restrict on update restrict;

alter table T_PLATFORM_ACTRULE add constraint FK_Reference_8 foreign key (actRuleTypeId)
      references T_PLATFORM_ACTRULE_TYPE (id) on delete restrict on update restrict;

alter table T_PLATFORM_ADVICE add constraint FK_Reference_2 foreign key (tabOid)
      references T_PLATFORM_ADVICE_TAB (oid) on delete restrict on update restrict;

alter table T_PLATFORM_BANNER add constraint FK_Reference_3 foreign key (channelOid)
      references T_PLATFORM_CHANNEL (oid) on delete restrict on update restrict;

alter table T_PLATFORM_INFORMATION add constraint FK_Reference_6 foreign key (channelOid)
      references T_PLATFORM_CHANNEL (oid) on delete restrict on update restrict;

alter table T_PLATFORM_NOTICE add constraint FK_Reference_4 foreign key (channelOid)
      references T_PLATFORM_CHANNEL (oid) on delete restrict on update restrict;

alter table T_PLATFORM_PARTNER add constraint FK_Reference_9 foreign key (channelOid)
      references T_PLATFORM_CHANNEL (oid) on delete restrict on update restrict;

alter table T_PLATFORM_PROTOCOL add constraint FK_Reference_7 foreign key (protocolTypeId)
      references T_PLATFORM_PROTOCOL_TYPE (id) on delete restrict on update restrict;
