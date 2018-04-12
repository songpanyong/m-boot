#==================运营推广增量======================

TRUNCATE TABLE t_tulip_rule_prop;

insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R001','累计推荐人数量','friends','number','get','个','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R002','累计投资额度','investAmount','double','get','元','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R003','投资额度','orderAmount','double','use','元','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R004','投资时间','investTime','number','use','天','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R005','注册时间','registerTime','number','use','天','yes');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R006','累计投资时长','investDuration','interval','use','天','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R007','累计投资次数','investCount','number','use','次','no');
insert into `t_tulip_rule_prop` (`oid`, `name`, `field`, `unit`, `type`, `unitvalue`, `isdel`) values('R008','首投金额','firstInvestAmount','interval','get','元','no');

TRUNCATE TABLE t_tulip_scene_prop;

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
