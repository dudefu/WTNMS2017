package com.jhw.adm.server.entity.util;

public class MessageNoConstants {
	private static int i = 0;
    
	public final static int TOPOSEARCH = ++i;// ���˷��֣����ڿͻ���Ҫ���Զ����˵���Ϣ����������
	public final static int TOPOSEARCHONEFINSH=++i;//����һ������ڵ㣬���ظ��ͻ�����Ϣ
	
	public final static int SYNCHDEVICE = ++i;// �ͻ���Ҫ��ͬ�����������в���
	public final static int SYNCHFEP = ++i; // �ͻ���Ҫ��ͬ��ǰ�û����������豸�Ĳ���
	public final static int SYNCHREP = ++i;// ǰ�û����ص���һ���豸��ͬ������
	/**
	 * ǰ�÷�����������豸�Ĳ���ͬ�������Ϣ
	 */
	public final static int SYNCHFINISH = ++i;
	public final static int SYNCHONEFINISH = ++i;// ���һ������������ͬ��ʱ���ɷ������˷��͸��ͻ��˵���Ϣ
	
	public final static int TOPONODE = ++i;// ���˽ڵ㣬����ǰ�÷��ط��ֵ����˽ڵ㣨������������Ϣ��
	public final static int TOPOFINISH = ++i;// ���˷������
//	public final static int TOPERROR = ++i;//���ͻ���Ҫ�����˵�ʱ�����ǰ�û�û�����߻���û�е�¼��ʱ�򣬷������˷��͸���Ϣ���ͻ���
//	public final static int SYNCHORERROR = ++i;//���ͻ���Ҫ�����ͬ��ʱ�����ǰ�û��������߻���û��������¼��������������Ϣ�ÿͻ���
	public final static int FEPOFFLINE = ++i;//ǰ�û������ߣ��������˷��͸��ͻ��˵���Ϣ
	// ==================================VLAN====================================================
	public final static int VLANGET = ++i;// �ͻ���Ҫ���ȡvlan������Ϣ
	public final static int VLANSET = ++i;// ���͸�ǰ��Ҫ������vlan����Ϣ
	public final static int VLANNEW = ++i;// �ͻ���Ҫ������һ��vlan
	public final static int VLANUPDATE = ++i;// �ͻ��˷��͸�ǰ�û��޸�vlan����Ϣ
	public final static int VLANDELETE = ++i;// �ͻ��˷��͸�ǰ�û�ɾ��vlan����Ϣ��
	public final static int VLANPORTUPDATE = ++i;// �޸Ķ˿ڵ�vlan��Ϣ
	// =================================RING======================================================
	public final static int RINGNEW = ++i;// �½�һ��JH_RING
	public final static int RINGDELETE = ++i;// ɾ��һ��JH_RING
	public final static int RINGSGET = ++i;// ��ȡJH_RING�б�
	public final static int RINGON = ++i;// ����RING
	public final static int RINGOFF = ++i;// �ر�RING
	// -----------------��·����----------------------------
	public final static int LINKBAKNEW = ++i;// �½���·����
	public final static int LINKBAKDELETE = ++i;;// ɾ����·����
	public final static int LINKBAKGET = ++i;// ��ȡ��·����
	public final static int PORTSRINGGET = ++i;// ����RING�˿���Ϣͳ��
	public final static int RINGCOUNT = ++i;// RINGͳ��
	// ==================================MAC================================================
	public final static int MACUNINEW = ++i;// ��������
	public final static int MACUNIGET = ++i;// �����б��ȡ
	public final static int MACUNIDEL = ++i;// ����ɾ��
	public final static int MACMUTINEW = ++i;// �ಥ����
	public final static int MACMUTIGET = ++i;// �ಥ�б��ȡ
	public final static int MACMUTIDEL = ++i;// �ಥɾ��
	// ==============================MIRROR=======================================================
	public final static int MIRRORNEW = ++i;// �˿ھ�������
	public final static int MIRRORUPDATE = ++i;// �˿ھ�������޸�
	public final static int MIRRORDEL = ++i;// �˿ھ���ɾ��
	public final static int MIRRORGET = ++i;// ��ȡ�˿ھ�������
	// ====================================�˿�����=================================================
	public final static int PORTSET = ++i;// ���ý������˿ڲ���
	public final static int PORTGET = ++i;// ��ȡ���ж˿ڲ���
	public final static int SERIALCOFIG =++i;//���ô��ڲ���

	// ==================================�豸������Ϣ=============================================
	public final static int BASEINFOGET = ++i;// ��ȡ�豸������Ϣ
	public final static int BASECONFIGGET = ++i;// ��ȡ�豸����������Ϣ
	public final static int BASECONFIGUPDATE = ++i;// �޸��豸����������Ϣ
	// =================================STP===================================================
	public final static int STPSYSCONFIGGET = ++i;// ��ȡstpϵͳ����
	public final static int STPSYSCONFIGSET = ++i;// ����stpϵͳ���ò���
	public final static int STPPORTCONFIGGET = ++i;// ��ȡstp�˿�����
	public final static int STPPORTCONFIGSET = ++i;// ����stp�˿����ò���
	public final static int STPPORTSTATUGET = ++i;// stp�˿�״̬��ȡ
	public final static int STPCOUNTGET = ++i;// stp�˿�ͳ������
	public final static int STPCOUNTCLEAR = ++i;// stp�˿�ͳ����������
	// ===================================SNTP====================================================
	public final static int SNTPGET = ++i;// sntp������ȡ
	public final static int SNTPSET = ++i;// sntp��������
	// ================================����������=================================================
	public final static int FEPHEARTBEAT = ++i;// ǰ�û����͵��������������������͵���Ϣ
	public final static int CLIENTHEARTBEAT = ++i;// �ͻ��˷��͵��������������������͵���Ϣ
	// ================================��Ϣ��ʱ=====================================================
	public final static int OUTTIMEEXCEPTION = ++i;// ǰ�û�����һ�����������ӳ�ʱ��Ϣ
	public final static int FEPSWITCHNETEXCEPTION = ++i;// ��ǰ�û�ֱ�������Ľ�����֮�����·�쳣
	// ==================================�澯=====================================================
	public final static int RTMESSAGE = ++i;// �·�ʵʱ�����Ϣ����Ϣ����
	public final static int REMONTHING = ++i;// ����rmon�¼�����Ϣ����
	public final static int TRAPMESSAGE = ++i;// trap�¼����ص���Ϣ����
	public final static int RMONCOUNT = ++i;// ����ʵʱ��ص�ʱ�򷢻ص�ʵʱ�������ݰ���Ϣ����
	// ===================================���ܼ��===================================================
	public final static int RTMONITORSTART = ++i;// ʵʱ��ؿ�ʼ����
	public final static int RTMONITORSTOP = ++i;// ʵʱ���ֹͣ����
	public final static int RTMONITORRES = ++i;// ʵʱ���ǰ�û�������Ϣ����
	// ===================================ʵʱping==================================================
	public final static int PINGSTART = ++i;// ping��ʼ
	public final static int PINGEND = ++i;// ping����
	public final static int PINGRES = ++i;// pingǰ�û�������Ϣ����
	// ===================================�˿ڸ澯����==============================================
	public final static int PWARNINGCONFIG = ++i;// �˿ڸ澯����
	public final static int PWARNINGCONFIGDELETE = ++i;// �˿ڸ澯���ò���ɾ��
	public final static int PWARNINGCONFIGUPDATE = ++i;// �˿ڸ澯���ò����޸�
	// =================================remon�¼�����===============================================
	public final static int REMONTHINGNEW = ++i;// �������¼�����
	public final static int REMONTHINGDEL = ++i;// ɾ���¼�����
	// =================================ʵʱ��ض˿�����===========================================
	public final static int REMONPORTNEW = ++i;// ������ʵʱ��ض˿�����
	public final static int REMONPORTDEL = ++i;// ɾ��ʵʱ��ض˿�����
	// ==============================�豸��������������Ϣ����=========================================
	public final static int PARMREP = ++i;// �豸��������������Ϣ����
	// ==============================�ز���������Ϣ����=========================================
	public final static int CARRIERTEST = ++i; // �ز�������//�ı���Ϣ
	public final static int CARRIERTESTREP = ++i;// �ز������Է���//"S","F"�������ı���
	public final static int CARRIERRESTART = ++i; // �ز�������//�ı���Ϣ
	public final static int CARRIERONLINE = ++i; // �ز�������//ǰ�û��������ͣ�û��res

	public final static int CARRIERWAVEBANDCONFIG = ++i; // �ز�����������//������Ϣ�������ز���ʵ��
	public final static int CARRIERWAVEBANDCONFIGREP = ++i;// �ز����������÷�����Ϣ

	public final static int CARRIERWAVEBANDQUERY = ++i;// ���β�ѯ��Ϣ����//�ı���Ϣ
	public final static int CARRIERWAVEBANDQUERYREP = ++i;// ���β�ѯ������Ϣ\

	public final static int CARRIERROUTEQUERY = ++i; // �ز���·�ɲ�ѯ
	public final static int CARRIERROUTEQUERYREP = ++i;// ·�ɲ�ѯ������Ϣ

	public final static int CARRIERROUTECONFIG = ++i; // �����ز���·������
	public final static int CARRIERROUTECONFIGS = ++i; // ����ͬ�������ز���·������
	public final static int CARRIERROUTECONFIGREP = ++i; // �����ز���·�����÷�����Ϣ
	public final static int CARRIERROUTEFINISH = ++i; // ����ͬ��·�ɽ���

	public final static int CARRIERROUTECLEAR = ++i; // �ز���·�����//�ı���Ϣ
	public final static int CARRIERROUTECLEARREP = ++i;// �ز���·����շ�����Ϣ

	public final static int CARRIERPORTQUERY = ++i; // �ز����˿ڲ�ѯ
	public final static int CARRIERPORTQUERYREP = ++i;// �ز����˿ڲ�ѯ������Ϣ����
	public final static int CARRIERPORTCONFIG = ++i; // �ز����˿�����
	public final static int CARRIERPORTCONFIGREP = ++i; // �ز����˿����÷�����Ϣ

	public final static int CARRIERSYSTEMUPGRADE = ++i; // �ز���ϵͳ����
	public final static int CARRIERSYSTEMUPGRADEREP = ++i; // �ز���ϵͳ����������Ϣ

	public final static int CARRIERMONITORREP = ++i; // ǰ�û���������ز���״̬���ͻ�������Ϣ����

	// ===================================GPRS=================================================================
	public final static int GPRSMESSAGE = ++i;// ����gprs�豸���豸�˷��͵��ͻ��˵���Ϣ
	public final static int SWITCHERUPGRATE = ++i;//������������Ϣ
	public final static int ONE_SWITCHER_UPGRADEREP=++i;//����������ɵķ�����Ϣ
	public final static int ALL_SWITCHER_UPGRADEREP = ++i;//�������������
	public final static int SWITCHER_UPGRADEING=++i;//��ǰ�������������������ܽ�����������
	public final static int WARNING=++i;//���������͸��ͻ��˵ĸ澯��Ϣ
	public final static int LLDPCONFIG=++i;//lldp����
	
	//=========================================================================================================
	public final static int REFRESHING=++i;//�������˷��֣���ʾ�ͻ����������û����ڽ������˷��֣����������û�ͬʱ�������˷��ֲ���
	public final static int SYNCHORIZING=++i;//���ڽ��в���ͬ������ʾ�ͻ��˲��������ͻ���ͬʱ���в���ͬ��
	
	public final static int SNMPHOSTADD=++i;//snmp��������
	public final static int SNMPHOSTDELETE = ++i;//ɾ��snmp������
	//==========================================�����===========================================================
	public final static int LIGHT_SIGNAL=++i;//���ͻ�ȡ���������źŵ���Ϣ
	public final static int LIGHT_SIGNAL_REP=++i;//ǰ�û����ؽ������źŵ�״̬����Ϣ����
	//==========================================ǰ�û�״̬
	public final static int FEP_STATUSTYPE=++i; //ǰ�û�״̬
	
	public final static int PING_TIMER=++i;//��ʱping
	//==========================================================================================================
	
    public final static int EPON_INFO=++i;//����epon��Ϣ

	public final static int ALL_VLAN=++i;//��������Vlan
	//==========================================================================================================
	
	public final static int ALL_RING=++i;//��������RING
	//==========================================================================================================
	
	public final static int IGMP_PORTSET=++i;//IGMP�˿�����
	public final static int IGMP_VLANSET=++i;//IGMPVlan����
	
	public final static int ALL_SNMPHOST=++i;//��������SNMPHOST
	//==========================================================================================================
	
	//====================================================QOS����
	
	public final static int QOS_SYSCONFIG=++i;//ϵͳ����
	public final static int QOS_PRIORITYCONFIG=++i;//QOS���ȼ�����
	public final static int TOS_PRIORITYCONFIG=++i;//TOS���ȼ�����
	public final static int DSCP_PRIORITYCONFIG=++i;//���ȼ�����
	public final static int QOS_LIMITPORT=++i;//�˿�����
	public final static int QOS_STORMCONTROL=++i;//�籩����
	
	
	//=============================================================
	public final static int LACPCONFIG=++i;//lacp����
	
	//=============================================================
	
	public final static int AGGREGATEPORT_ADD=++i;//�ۺ϶˿�����
	public final static int AGGREGATEPORT_DEL=++i;//�ۺ϶˿�ɾ��
	
	//=============================================================================================================
	public final static int OLTVLANADD=++i;//olt vlan ���
	public final static int OLTVLANDEL = ++i;//ɾ��olt�ϵ�vlan����
	public final static int DBACONFIG = ++i;//DBA����
	public final static int LLIDCONFIG=++i;//llid����
	public final static int OLTSTPCONFIG=++i;//OLT STPϵͳ����
	public final static int OLTMULTICASTCONFIG=++i;//OLT �鲥����
	

	//==========================================================================================================
	public final static int MONITORRING_TIMER=++i;//��ʱ���
	public final static int MONITORRING_DEL=++i;
	
	public final static int SWITCHRESTING=++i;//������������
	public final static int SWITCHRESTSUCCESS=++i;//�����������ɹ�
	public final static int SWITCHRECEIVE=++i;//���ܽ�������Ϣ
	public final static int SWITCHUPGRADEFAIL=++i;//����������ʧ��
	public final static int SWITCHRESTFAIL=++i;//����������ʧ��
	
	public final static int SWITCHUSERMANAGE=++i;//�������û�����
	
	
	public final static int SWITCH3STP=++i;//���㽻����STP����
	public final static int SWITCH3VLAN=++i;//���㽻����Vlan����
	public final static int SWITCH3VLANADD=++i;//���㽻����Vlan��������
	public final static int SWITCH3VLANUPDATE=++i;//���㽻����Vlan�޸�����
	public final static int SWITCH3VLANDEL=++i;//���㽻����Vlanɾ������
	public final static int SWITCH3STPPORT=++i;//���㽻����STP�˿�����
	public final static int SWITCH3SNMP=++i;//���㽻����SNMP����
	public final static int SWITCH3LLDP_P=++i;//���㽻����LLDPЭ������
	public final static int SWITCH3LLDPPORT=++i;//���㽻����LLDPЭ��˿�����
	public final static int SWITCH3VLANPORT=++i;//���㽻����Vlan�˿�����
	public final static int SWITCH3PORTFLOW=++i;//���㽻�����˿���������
	public final static int SWITCH3RINGADD=++i;//���㽻����Ring�������
	public final static int SWITCH3RINGUPDATE=++i;//���㽻����Ring��������
	public final static int SWITCH3RINGDEL=++i;//���㽻����Ringɾ������
	
	
	
	
	
	public final static int CARRIERMARKINGCONFIG=++i;//���ز��ͺ����ã�
    public final static int CARRIERMARKINGREP=++i;//�ز����ͺ����÷���
    
    public final static int SWITCHUSERUPDATE=++i;//
    public final static int SWITCHUSERADD=++i;//
    
    
    public final static int QOSPORTSPEEDREP=++i;//QOS�˿���������
    
    public final static int WARNINGLINK=++i;//�߸澯
    public final static int WARNINGNODE=++i;//�ڵ�澯
	
    
    public final static int CHECKLLDP=++i;//LLDP���
    
    public final static int SYSLOGHOSTSAVE=++i;//syslog��������
    
    public final static int SYSLOGHOSTDELETE=++i;//syslog����ɾ��
    
    /**
     * �����豸��������
     */
    public final static int SINGLESYNCHDEVICE=++i;
    
    /**
     * ����������LLDP����
     */
    public final static int SINGLESWITCHLLDP=++i;
    /**
     * ����������Vlan����
     */
    public final static int SINGLESWITCHVLAN=++i;
    /**
     * ����������Vlan�˿�����
     */
    public final static int SINGLESWITCHVLANPORT=++i;
    /**
     * ����������IGMP�˿�����
     */
    public final static int SINGLESWITCHIGMPPORT=++i;
    /**
     * ����������IGMPVlanID����
     */
    public final static int SINGLESWITCHIGMPVLANID=++i;
    
    /**
     * ����������IP����
     */
    public final static int SINGLESWITCHIP=++i;
    /**
     * ������������������
     */
    public final static int SINGLESWITCHUNICAST=++i;
    /**
     * �����������ಥ����
     */
    public final static int SINGLESWITCHMULTICAST=++i;
    /**
     * ����������QOS�˿���������
     */
    public final static int SINGLESWITCHQOSPORT=++i;
    /**
     * ����������QOS�籩��������
     */
    public final static int SINGLESWITCHQOSSTORM=++i;
    /**
     * ����������QOSϵͳ��������
     */
    public final static int SINGLESWITCHQOSSYS=++i;
    /**
     * ����������QOS802D1P���ȼ�����
     */
    public final static int SINGLESWITCHQOSPRIORITY802D1P=++i;
    /**
     * ����������QOSDSCP���ȼ�����
     */
    public final static int SINGLESWITCHQOSPRIORITYDSCP=++i;
    /**
     * ����������QOSTOS���ȼ�����
     */
    public final static int SINGLESWITCHQOSPRIORITYTOS=++i;
    /**
     * ����������RING����
     */
    public final static int SINGLESWITCHRING=++i;
   
    /**
     * ����������SNMPȺ������
     */
    public final static int SINGLESWITCHSNMPGROUP=++i;
    /**
     * ����������SNMP��������
     */
    public final static int SINGLESWITCHSNMPMASS=++i;
    /**
     * ����������SNMP��ͼ����
     */
    public final static int SINGLESWITCHSNMPVIEW=++i;
    /**
     * ����������SNMP��������
     */
    public final static int SINGLESWITCHSNMPHOST=++i;
    
    /**
     * ����������STP�˿�����
     */
    public final static int SINGLESWITCHSTPPORT=++i;
    /**
     * ����������STPϵͳ��������
     */
    public final static int SINGLESWITCHSTPSYS=++i;
    /**
     * �������ز�������
     */
    public final static int SINGLESYNCHONEFINISH=++i;
    /**
     * ������������ǰ�û�������Ϣ
     */
    public final static int SINGLESYNCHREP=++i;
    
    /**
     * ���������������˿���Ϣ����
     */
    public final static int SINGLESWITCHPORT=++i;
    /**
     * ��������������SNTP����
     */
    public final static int SINGLESWITCHSNTP=++i;
    
    
    
    /**
     * ����������������������
     */
    public final static int SINGLESWITCHSERIAL=++i;
    
    
    /**
     * ��������������SNMP�û�����
     */
    public final static int SINGLESWITCHSNMPUSER=++i;
    /**
     * �����������û�����
     */
    public final static int SINGLESWITCHUSERADM=++i;
    
   
    /**
     * ������������·��������
     */
    public final static int SINGLESWITCHLINKBACKUPS=++i;
    
    /**
     * ����������LACP����
     */
    public final static int SINGLESWITCHLACP=++i;
    
    /**
     * ����������mirror����
     */
    public final static int SINGLESWITCHMIRROR=++i;
    
    /**
     * ����������TRUNK�ۺ϶˿�����
     */
    public final static int SINGLESWITCHTRUNKPORT=++i;
    
    
    /**
     * �����������˿ڸ澯����
     */
    public final static int SINGLESWITCHPORTWARN=++i;
    
     
    /**
     * �����������¼�������
     */
    public final static int SINGLESWITCHEVENTGROUP=++i;
    
    
    /**
     * ɾ���������û�
     */
    public final static int SWITCHUSERDEL=++i;
    
    
    /**
     * ������������Ϣ
     */
    public final static int SINGLESWITCHINFO=++i;
    /**
     * ������PortRemon
     */
    public final static int SINGLESWITCHPORTREMON=++i;
    
    /**
     * ����syslog��������
     */
    public final static int SINGLESYSLOGHOST=++i;
    
    /**
     * ��������˿�����
     */
    public final static int SINGLESWITCHLAYER3PORT=++i;
    
    /**
     * ����OLT�˿�����
     */
    public final static int SINGLEOLTPORT=++i;
    
    /**
     * syslog�澯��Ϣ
     */
    public final static int SYSLOGMESSAGE=++i;
    
}
