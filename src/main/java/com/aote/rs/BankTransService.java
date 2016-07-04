package com.aote.rs;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.bank.ProtocolHandlerFactory;
import com.aote.logic.LogicServer;
import com.aote.util.JsonTransfer;

@Path("bts")
@Singleton
@Component
@Transactional
public class BankTransService {
	static Logger log = Logger.getLogger(BankTransService.class);
	
	public static int IC_SALE_STATE_NOTIFIED = 0;
	public static int IC_SALE_STATE_CONFIRMED = 2;
	public static int IC_SALE_STATE_CANCELOFF = 1;
	
	@Autowired
	private LogicServer logicServer;
	
	public static String TRANS_CODE = "TRANS_CODE";
	public static String TRANS_SN = "TRANS_SN";
	public static String BANK_DATE = "BANK_DATE";
	public static String BANK_TIME = "BANK_TIME";
	public static String BANK_ID = "BANK_ID";
	public static String TELLER_ID = "TELLER_ID";
	public static String CHANNEL_ID = "CHANNEL_ID";
	public static String DEVICE_ID = "DEVICE_ID";
	public static String ACK_TRANS_SN = "ACK_TRANS_SN";
	public static String RESPONSE_CODE = "RESPONSE_CODE";
	public static String RESPONSE = "RESPONSE";
	public static String MAC = "MAC";
	
	public static String ERROR_NO_ERROR = "0000";
	public static String ERROR_WRONG_PACKET_FORMAT = "0001";
	public static String ERROR_WRITE_CARD_PENDING = "0002";
	public static String ERROR_MACHINE_METER_USER = "1001";
	public static String QUERY_MACHINE_METER_USER = "1002";
	public static String ERROR_MACHINE_CHARGE_NOTIFICATION = "1101";
	public static String ERROR_IC_METER_USER = "1201";
	public static String QUENRY_IC_METER_USER = "1202";
	public static String SALE_BEYOND_LIMIT = "1203";
	public static String ERROR_IC_CALC_FEE = "1301";
	public static String ERROR_IC_CHARGE_NOTIFICATION = "1404";
	public static String ERROR_IC_GAS_FEE_MISMATCH = "1401";
	public static String ERROR_IC_MAINTENANCE_FEE_MISMATCH = "1402";
	public static String ERROR_IC_OTHER_FEE_MISMATCH = "1403";
	public static String ERROR_USER_QUERY = "1501";
	public static String ERROR_IC_CANCEL_OFF = "1601";
	public static String ERROR_IC_HAS_CANCELLED_OFF = "1602";
	public static String ERROR_CHARGE_STATE = "1701";
	public static String ERROR_WRITE_CARD_CONFIRM = "1701";
	public static String ERROR_INTERNAL_ERROR = "8888";
	public static String ERROR_SERVICE_SUSPENDED = "9999";
	
	//	0000	交易成功。
	//	0001	请求报文格式错误。
	//	0002	卡表上次缴费未写卡，不能再次缴费。
	//	100x	机表用户查询错误。
	//	11xx	机表缴费通知错误
	//	12xx	卡表用户查询错误。
	//	13xx	卡表划价错误。
	//	14xx	卡表缴费通知错误。
	//	1401	二次划价气量不符。
	//	1402	二次划价维管费不符。
	//	1403	二次划价其他费用不符。
	//	15xx	用户信息模糊查询错误。
	//	16xx	卡表冲正错误。
	//	16xx	不能重复冲正。
	//	17xx	购气状态查询错误。
	//	18xx	写卡确认错误。
	//	9888	系统内部错误。
	//	9999	系统暂停服务。

	public static HashMap<String, String> MSG = new HashMap<String, String>(); 
	static
	{
		MSG.put(BankTransService.ERROR_NO_ERROR, "交易成功。");
		MSG.put(BankTransService.ERROR_WRONG_PACKET_FORMAT, "请求报文格式错误。");
		MSG.put(BankTransService.ERROR_WRITE_CARD_PENDING, "卡表上次缴费未写卡，不能再次缴费。");
		MSG.put(BankTransService.ERROR_MACHINE_METER_USER, "机表用户信息查询错误。");
		MSG.put(BankTransService.QUERY_MACHINE_METER_USER, "非民用用户请到营业厅交费。");
		MSG.put(BankTransService.ERROR_MACHINE_CHARGE_NOTIFICATION, "机表缴费通知错误。");
		MSG.put(BankTransService.ERROR_IC_METER_USER, "卡表用户查询错误。");
		MSG.put(BankTransService.QUENRY_IC_METER_USER, "非民用用户请到营业厅交费。");
		MSG.put(BankTransService.SALE_BEYOND_LIMIT, "一天之内不能多次购气。");
		MSG.put(BankTransService.ERROR_IC_CALC_FEE, "卡表划价错误。");
		MSG.put(BankTransService.ERROR_IC_CHARGE_NOTIFICATION, "卡表缴费通知错误。");
		MSG.put(BankTransService.ERROR_IC_GAS_FEE_MISMATCH, "二次划价气量不符。");
		MSG.put(BankTransService.ERROR_IC_MAINTENANCE_FEE_MISMATCH, "二次划价维管费不符。");
		MSG.put(BankTransService.ERROR_IC_OTHER_FEE_MISMATCH, "二次划价其他费用不符。");
		MSG.put(BankTransService.ERROR_USER_QUERY, "用户信息模糊查询错误。");
		MSG.put(BankTransService.ERROR_IC_CANCEL_OFF, "卡表冲正错误。");
		MSG.put(BankTransService.ERROR_IC_HAS_CANCELLED_OFF, "不能重复冲正。");
		MSG.put(BankTransService.ERROR_CHARGE_STATE, "购气状态查询错误。");
		MSG.put(BankTransService.ERROR_WRITE_CARD_CONFIRM, "写卡确认错误。");
		MSG.put(BankTransService.ERROR_INTERNAL_ERROR, "系统内部错误。");
		MSG.put(BankTransService.ERROR_SERVICE_SUSPENDED, "系统暂停服务。");
		Iterator<String> keys = MSG.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			MSG.put(key, BankTransService.encodeChinese(MSG.get(key)));
		}
	}
	
	@GET
	public String test()
	{
		return "Hello";
	}
	
	/**
	 * 协议处理服务
	 * @return
	 */
	@POST
	public String dispatcher(String json)
	{
		long interval = (new Date()).getTime();
		log.debug("接收的报文: " + json);
		JSONObject result = new JSONObject();
		try
		{
			JSONObject cmdObj = parseRequest(json);
			//报文异常
			if(cmdObj == null)
			{
				log.debug("错误：报文错误。");
				result.put(BankTransService.RESPONSE_CODE, BankTransService.ERROR_WRONG_PACKET_FORMAT);
				result.put(BankTransService.RESPONSE, json);
				return result.toString();
			}
			
			String logicName = ProtocolHandlerFactory.getInstance().getProtocol(cmdObj.getString(BankTransService.TRANS_CODE));			
			log.debug("处理器：" + logicName);
			
			// 执行业务逻辑
			JSONObject param = new JSONObject();
			param.put("data", cmdObj);
			Object ret = logicServer.run(logicName, param.toString());
			// 如果执行结果为Map，转换成JSON串
			if (ret instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>)ret;
				result = (JSONObject) new JsonTransfer().MapToJson(map);
			}
			
			// 计算MD5
			
			interval = (new Date()).getTime() - interval;
			log.debug("交易用时(ms): " + interval + "返回的报文: " + URLDecoder.decode(result.toString()));			
			return result.toString();
		}
		catch(Exception e)
		{
			//内部错误
			log.debug("内部错误: " + e.getMessage());
			internalError(result);
			return result.toString();
		}
	}

	/**
	 * 内部错误
	 * @param result
	 * @param code
	 */
	private void internalError(JSONObject result) 
	{
		try
		{
			emptyResult(result);
			result.put(BankTransService.RESPONSE_CODE, BankTransService.ERROR_INTERNAL_ERROR);
			result.put(BankTransService.RESPONSE, MSG.get(BankTransService.ERROR_INTERNAL_ERROR));
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * 清空所有字段
	 * @param result
	 * @throws JSONException
	 */
	public static void emptyResult(JSONObject result) throws JSONException
	{
		Iterator itr = result.keys();
		while(itr.hasNext())
			result.put(itr.next().toString(), JSONObject.NULL);
	}


	/**
	 * 解析报文
	 * @param json
	 * @return
	 */
	private JSONObject parseRequest(String json) 
	{	
		try
		{
			return new JSONObject(json);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static String encodeChinese(Object plainText)
	{
		if(plainText == null)
			plainText = "";
		return URLEncoder.encode(plainText.toString());
	}
	
	public static String formatDate(Date dt)
	{
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		return df.format(dt);
	}
	
	public static int toFen(Object value)
	{
		if(value == null)
			return 0;
		return (int)(Double.parseDouble(value.toString())*100);
	}

	public static Object toInt(Object value) 
	{
		if(value == null)
			return 0;
		return (int)(Double.parseDouble(value.toString()));
	}	
}
