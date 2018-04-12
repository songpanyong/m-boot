#==================运营推广增量======================

drop table if exists T_TULIP_ADVERTORIALS;

drop table if exists T_TULIP_SIGN_IN;

drop table if exists t_tulip_platform_banner;

create table T_TULIP_SIGN_IN
(
   oid                  varchar(32) not null,
   userId               varchar(32),
   signInTime           timestamp,
   signDate             date not null,
   primary key (oid)
);

create unique index userId_signDate on T_TULIP_SIGN_IN
(
   signDate,
   userId
);

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