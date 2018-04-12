/*==============================================================*/
/* Table: T_WFD_RECOMMENDER                                     */
/*==============================================================*/
create table T_WFD_RECOMMENDER
(
   oid                  varchar(32) not null,
   userOid              varchar(32),
   recommendLoginName   varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_WFD_USER                                            */
/*==============================================================*/
create table T_WFD_USER
(
   oid                  varchar(32) not null,
   userAcc              varchar(32),
   memberOid            varchar(32),
   userPwd              varchar(64),
   salt                 varchar(32),
   payPwd               varchar(64),
   paySalt              varchar(32),
   status               varchar(32) comment '1:正常，2：冻结',
   source               varchar(32) comment '1.后台添加，0，前台注册',
   sceneId              int,
   channelid            varchar(200),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create unique index Index_1 on T_WFD_USER
(
   sceneId
);

/*==============================================================*/
/* Index: Index_2                                               */
/*==============================================================*/
create unique index Index_2 on T_WFD_USER
(
   userAcc
);

/*==============================================================*/
/* Table: T_WFD_USER_BANK                                       */
/*==============================================================*/
create table T_WFD_USER_BANK
(
   oid                  varchar(32) not null,
   userOid              varchar(32),
   name                 varchar(32),
   idNumb               varchar(32),
   bankName             varchar(32),
   cardNumb             varchar(32),
   phoneNo              varchar(15),
   createTime           timestamp default CURRENT_TIMESTAMP,
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Table: T_WFD_WXEXT                                           */
/*==============================================================*/
create table T_WFD_WXEXT
(
   oid                  varchar(32) not null,
   userOid              varchar(32),
   openid               varchar(32),
   platform             varchar(32),
   updateTime           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   createTime           timestamp default CURRENT_TIMESTAMP,
   primary key (oid)
);

/*==============================================================*/
/* Index: Index_1                                               */
/*==============================================================*/
create index Index_1 on T_WFD_WXEXT
(
   openid
);

/*==============================================================*/
/* Table: T_WFD_USER_BANK_HIS                                   */
/*==============================================================*/
create table T_WFD_USER_BANK_HIS
(
   oid                  varchar(32) not null,
   userOid              varchar(32),
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


alter table T_WFD_RECOMMENDER add constraint FK_Reference_8 foreign key (userOid)
      references T_WFD_USER (oid) on delete restrict on update restrict;

alter table T_WFD_RECOMMENDER add constraint FK_Reference_9 foreign key (recommendLoginName)
      references T_WFD_USER (oid) on delete restrict on update restrict;

alter table T_WFD_USER_BANK add constraint FK_Reference_6 foreign key (userOid)
      references T_WFD_USER (oid) on delete restrict on update restrict;

alter table T_WFD_WXEXT add constraint FK_Reference_2 foreign key (userOid)
      references T_WFD_USER (oid) on delete restrict on update restrict;
