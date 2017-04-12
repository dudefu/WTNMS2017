--CREATE OR REPLACE FORCE VIEW "ADMSERVER"."TRAP_FIXING_VIEW" ("TRAPID", "IPVALUE", "WARNTYPE", "STATUS", "DESCS", "WARNINGLEVEL", "BEGINTIME") AS 
--select trap.id trapid,trap.ipValue ipvalue,trap.warningType warntype,trap.currentStatus status,trap.descs descs,wt.warningLevel warninglevel,nt.beginTime begintime
--from ADMSERVER.TrapWarningEntity trap,ADMSERVER.WarningFixStatus ft,ADMSERVER.WARNINGTYPE wt
--where trap.warningType=wt.warningType(+) and
--      trap.FIXID = ft.id and
--      trap.currentStatus='1';
--      
--CREATE OR REPLACE FORCE VIEW "ADMSERVER"."TRAP_NEW_VIEW" ("TRAPID", "IPVALUE", "WARNTYPE", "STATUS", "DESCS", "WARNINGLEVEL", "BEGINTIME") AS 
--select trap.id trapid,trap.ipValue ipvalue,trap.warningType warntype,trap.currentStatus status,trap.descs descs,wt.warningLevel warninglevel,nt.beginTime begintime
--from ADMSERVER.TrapWarningEntity trap,ADMSERVER.WarningNewStatus nt,ADMSERVER.WARNINGTYPE wt
--where trap.warningType=wt.warningType(+) and
--      trap.NEWID = nt.id and
--      trap.currentStatus='0';
--      
--      
--create or replace PACKAGE ADMSERVERPKG AS
 --type TRAPCursor is ref cursor; 
--END;
--
----create or replace PROCEDURE           IP_USER_PROCEDURE
----(IPVALUE IN VARCHAR2,P_CURSOR out ADMSERVERPKG.TRAPCursor) AS
----BEGIN
----OPEN P_CURSOR FOR 
----  select distinct userentity.USERNAME,userentity.careLevel,userentity.WARNINGSTYLE, person.email,person.mobile,person.name
----from ADMSERVER.SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area,USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person
----where switcher.baseconfig_id=config.id and
----     switcher.address_id = address.id and
----      area.id = address.areaid and
----      userentity.id = userarea.USERENTITY_ID and
----     person.id = userentity.personid and
----      us erarea.areas_id = area.id;  and
----      config.ipvalue=IPVALUE;
----END;
--
--
--create or replace PROCEDURE           USER_FIXING_WARNING_PROCEDURE
--(USERNAME IN VARCHAR2, CARELEVEL IN VARCHAR2,P_CURSOR out ADMSERVERPKG.TRAPCursor) AS
--BEGIN
--    OPEN p_cursor FOR
--select trap.* 
--   from ADMSERVER.trap_fixing_view trap,ADMSERVER.SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area, USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person
--   where  trap.ipValue=config.ipValue and
--          address.id = switcher.address_id and
--          config.id = switcher.baseconfig_id and
--          area.id = address.areaid and
--          userarea.areas_id = area.id and
--          userentity.id = userarea.USERENTITY_ID and
--          INSTR(userentity.careLevel,CARELEVEL)<>0 and
--          userentity.userName = USERNAME;
--END;
--
--create or replace PROCEDURE           USER_NEW_WARNING_PROCEDURE
--(USERNAME IN VARCHAR2, CARELEVEL IN VARCHAR2,P_CURSOR out ADMSERVERPKG.TRAPCursor) AS
--BEGIN
--  OPEN p_cursor FOR
--select trap.* 
--  from ADMSERVER.trap_new_view trap,ADMSERVER.SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area, USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person
--  where  trap.ipValue=config.ipValue and
--          address.id = switcher.address_id and
--          config.id = switcher.baseconfig_id and
--          area.id = address.areaid and
--          userarea.areas_id = area.id and
--          userentity.id = userarea.USERENTITY_ID and
--          INSTR(userentity.careLevel,CARELEVEL)<>0 and
--          userentity.userName = USERNAME;
--END;
--
--
--create or replace PROCEDURE           USERWARNING_PROCEDURE
--(USERNAME IN VARCHAR2, CARELEVEL IN VARCHAR2,P_CURSOR out ADMSERVERPKG.TRAPCursor) AS
--BEGIN
--OPEN P_CURSOR FOR
--select distinct trap.id trapid,trap.ipValue ipvalue, trap.warningType warntype,trap.currentStatus status,trap.descs,wt.warningLevel
--  from TrapWarningEntity trap,ADMSERVER.SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area, USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person,WarningType wt
--  where  trap.ipValue=config.ipValue and
--       address.id = switcher.address_id and
--       config.id = switcher.baseconfig_id and
--       area.id = address.areaid and
--       userarea.areas_id = area.id and
--       userentity.id = userarea.USERENTITY_ID and
--       INSTR(userentity.careLevel,CARELEVEL)<>0 and
--       userentity.userName = USERNAME;
--END;
INSERT INTO areaentity (ID, DESCS, NAME,SUPERAREA_ID) VALUES (1000, '123','234', null);
INSERT INTO personinfo (id,descs,email,fax,mobile,name,phone) 
values 
(1,'源拓光电1','yuantuo1@wintop.com','1312535353','1232132141','wintop','1312533434'),
(2,'源拓光电2','yuantuo2@wintop.com','1312535353','1232132141','wintop','1312533434');
INSERT INTO roleentity (id,descs,roleCode,roleName) 
VALUES 
(1,'拥有一切权限',1000,'超级管理员'),
(2,'拥有一般权限',1001,'管理员');
INSERT INTO userentity (id,careLevel,clientIp,currentTime,descs,password,userName,warningStyle,personID,RoleID) 
VALUES (1,1,'',20160922,'源拓光电1','111111','admin',1,1,1),(2,1,'',20160922,'源拓光电2','111111','user',1,1,1);
INSERT INTO warninglevel (id,desccs,warningLevel)
VALUES
(1,'紧急',1),
(2,'严重',2),
(3,'普通',3),
(4,'通知',4);
INSERT INTO warningcategory (id,desccs,warningCategory)
VALUES
(1,'设备告警',0),
(2,'板卡告警',1),
(3,'端口告警',2),
(4,'通信告警',3),
(5,'协议告警',4),
(6,'安全告警',5),
(7,'性能告警',6),
(8,'网管告警',7),
(9,'其它告警',8);
INSERT INTO topdiagram (id,descs,lastTime,NAME) VALUES (1,1,20161103,'admin');
