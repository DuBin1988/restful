package com.aote.bank;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.aote.rs.BankTransService;



public abstract class ProtocolHandler {
	JSONObject request;
	JSONObject response;
	Session session;
	ApplicationContext appContext;
	
	/***
	 * ִ�з�����
	 */
	public abstract void execute();
	
	public abstract boolean isRequestMD5Correct() throws JSONException;
	
	/**
	 * ����MD5�ִ�
	 * @param data
	 * @return
	 */
	protected String getMD5(String data)
	{
		return DigestUtils.md5Hex(data);
	}

	/**
	 * ���Э��ͷ
	 * @param result
	 * @param cmdObj
	 */
	protected void fillHeader()
	{
		try 
		{
			fillField(response, request, BankTransService.TRANS_CODE);
			fillField(response, request, BankTransService.TRANS_SN);
			fillField(response, request, BankTransService.BANK_DATE);
			fillField(response, request, BankTransService.BANK_TIME);
			fillField(response, request, BankTransService.BANK_ID);
			fillField(response, request, BankTransService.TELLER_ID);
			fillField(response, request, BankTransService.CHANNEL_ID);
			fillField(response, request, BankTransService.DEVICE_ID);
		} 
		//�Ѿ�����Э�飬����
		catch (JSONException e) 
		{
		}
	}

	/**
	 * �����ֶ�
	 * @param result
	 * @param cmdObj
	 * @param key
	 * @throws JSONException
	 */
	private void fillField(JSONObject result, JSONObject cmdObj, String key) throws JSONException
	{
		result.put(key, cmdObj.optString(key));
	}
	
	public boolean isPacketSemanticallyCorrect()
	{
		if(request.isNull(BankTransService.TRANS_CODE))
			return false;
		if(request.isNull(BankTransService.TRANS_SN))
			return false;
		if(request.isNull(BankTransService.BANK_DATE))
			return false;
		if(request.isNull(BankTransService.BANK_TIME))
			return false;
		if(request.isNull(BankTransService.BANK_ID))
			return false;
		if(request.isNull(BankTransService.TELLER_ID))
			return false;
		if(request.isNull(BankTransService.CHANNEL_ID))
			return false;
		if(request.isNull(BankTransService.DEVICE_ID))
			return false;
		if(request.isNull(BankTransService.MAC))
			return false;
		return true;
	}
	
	/**
	 * �ж��ǲ����ظ�����
	 * @return
	 * @throws JSONException 
	 */
	public boolean isDuplicateRequest() throws Exception 
	{
		String MAC = request.getString(BankTransService.MAC);
		String sql = "from t_bank_trans where MAC = '" + MAC + 
		"' and TRANS_SN='" + request.getString(BankTransService.TRANS_SN) + 
		"' and TRANS_CODE='" + request.getString(BankTransService.TRANS_CODE) + "'";
		List list = session.createQuery(sql).list();
		if(list.size()>0)
			return true;
		else
			return false;
	}
	
	
	protected void calculateMaintenanceFee(String userid) throws Exception
	{
		IMaintenanceFee imf = (IMaintenanceFee)appContext.getBean("MaintenanceFee");
		Field f = imf.getClass().getDeclaredField("session");
		f.setAccessible(true);
		f.set(imf, this.session);
		JSONObject obj = imf.getMFInJSON(userid);
		response.put("MAINTENANCE_FEE_TILL", obj.getString("MAINTENANCE_FEE_TILL"));
		response.put("MAINTENANCE_FEE_COUNT", obj.getInt("MAINTENANCE_FEE_COUNT"));
		response.put("MAINTENANCE_FEE_ROWS", obj.getJSONArray("MAINTENANCE_FEE_ROWS"));
	}
	
	
	protected void setSuspendedIfMeterOverdue(String userId) throws Exception {
		//��黻���¼���õ���������
		//��ѯ�û�������Ϣ
		//��ѯ�û�������Ϣ
		Map<String, Object> user = (Map<String, Object>) session.createQuery("from t_userfiles where f_userid = '" + userId + "'" ).list().get(0);
		Date profileDate = (Date) user.get("f_dateofopening");

//		Object suspended = user.get("f_suspended");
		//�����ÿ�����һ�죬����12����ܹ���
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());	
		calendar.add(Calendar.YEAR, -10);
		//����������ں͵�ǰ�ȴ���10�꣬��������־��Ϊ1���������û���������-3
		//��������־Ϊ1�����ܹ���������-2�������û���Ϊ����ڲ��ܹ���
//		if(profileDate.before(calendar.getTime()))
//		{
//			if(suspended == null)
//			{
//				user.put("f_suspended", "1");
//				session.saveOrUpdate("t_userfiles", user);
//			}
//		}
		if(Util.getDate((Date)user.get("f_beginfee"), "yyyy-MM-dd").equals("2114-12-30"))
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			user.put("f_beginfee", sdf.parse("2114-12-31"));
		}
		session.saveOrUpdate("t_userfiles", user);
	}


	
	protected void fillSuspendedState(Date profileDate, Date f_beginfee, String userId) throws JSONException
	{
		List<Object[]> meter = (List<Object[]>) session.createQuery("select '1', max(f_cmchangemeterdate) from t_changmeter where f_userid = '" + userId + "'" ).list();
		if(meter.size()>0)
		{
			Date dt = (Date)meter.get(0)[1];
			if(dt != null)
				profileDate = dt;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String beginfee = sdf.format(f_beginfee);
		//�ж��Ƿ�SUSPENDED
		int state = getSuspended(profileDate, beginfee);
		//�޻���ʱ�����ơ���ĩ��ά��������
		if(state == 0)
		{
			response.put("SUSPENDED", 1);
			response.put("VISUAL_HINT", "");
		}
		//��ĩ12���
		else if(state == -1)
		{
			response.put("SUSPENDED", 0);
			response.put("VISUAL_HINT", BankTransService.encodeChinese("ÿ�����һ��12�����������"));
		}
		//���ڣ���ʾ����
		else if(state == -3)
		{
			response.put("SUSPENDED", 1);
			response.put("VISUAL_HINT", BankTransService.encodeChinese("ȼ����ʹ�ó���ʮ�꣬����ϵȼ����˾���������ܼ���������"));
		}
		//���ڣ����ܹ���
		else if(state == -2)
		{
			response.put("SUSPENDED", 0);
			response.put("VISUAL_HINT", BankTransService.encodeChinese("ȼ����ʹ�ó���ʮ�꣬���ܹ�����"));
		}
		//ά����δ��λ����ʾ��ά����
		else if(state == -4)
		{
			response.put("SUSPENDED", 1);
			response.put("VISUAL_HINT", BankTransService.encodeChinese("�뼰ʱ��ȼ����˾��ά���ѣ������´β��ܼ���������"));
		}
		//ά����δ��λ����ʾ��ά����
		else if(state == -5)
		{
			response.put("SUSPENDED", 0);
			response.put("VISUAL_HINT", BankTransService.encodeChinese("�뵽ȼ����˾��ά���ѣ����β��ܹ�����"));
		}		

	}
	
	protected int getSuspended(Date profileDate, String f_beginfee) {
		//�����ÿ�����һ�죬����12����ܹ���
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		//����12�㲻�ܹ���
		if(calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.HOUR_OF_DAY) >= 12)		
			return -1;
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		calendar.add(Calendar.YEAR, -10);
		//����������ں͵�ǰ�ȴ���10�꣬��������־��Ϊ1���������û���������-3
		//��������־Ϊ1�����ܹ���������-2�������û���Ϊ����ڲ��ܹ���
		if(profileDate.before(calendar.getTime()))
		{
			//�������𣬲��ܹ���
			//if(suspended != null)
				return -2;
//			else
//				return -3;
		}
		//���ж��Ƿ�ά����û������
		if(f_beginfee.equals("2114-12-30"))
			return -4;
		if(f_beginfee.equals("2114-12-31"))
			return -5;
		//���Թ���
		return 0;	
	}

	public void mirrorPacket() throws JSONException
	{
		BankTransService.emptyResult(response);
		this.fillHeader();
		this.fillBlankFields();
		response.put("ACK_TRANS_SN", request.getString(BankTransService.TRANS_SN));
		fillMD5();
	}

	public abstract void fillMD5()  throws JSONException;
	
	public abstract void fillBlankFields()  throws JSONException;
}

//// ִ��sql��ҳ��ѯ���������ʽ��������
//class HibernateSQLCall implements HibernateCallback {
//	String sql;
//	int page;
//	int rows;
//	//��ѯ���ת����������ת����Map�ȡ�
//	public ResultTransformer transformer = null;
//	
//	public HibernateSQLCall(String sql, int page, int rows) {
//		this.sql = sql;
//		this.page = page;
//		this.rows = rows;
//	}
//
//	public Object doInHibernate(Session session) {
//		Query q = session.createSQLQuery(sql);
//		//��ת����������ת����
//		if(transformer != null) {
//			q.setResultTransformer(transformer);
//		}
//		List result = q.setFirstResult(page * rows).setMaxResults(rows).list();
//		return result;
//	}
//}
//
// ִ�з�ҳ��ѯ
class HibernateCall implements HibernateCallback {
	String hql;
	int page;
	int rows;

	public HibernateCall(String hql, int page, int rows) {
		this.hql = hql;
		this.page = page;
		this.rows = rows;
	}

	public Object doInHibernate(Session session) {
		Query q = session.createQuery(hql);
		List result = q.setFirstResult(page * rows).setMaxResults(rows)
				.list();
		return result;
	}
}
