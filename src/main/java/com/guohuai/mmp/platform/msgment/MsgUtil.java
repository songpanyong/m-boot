package com.guohuai.mmp.platform.msgment;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.api.cms.CmsApi;
import com.guohuai.component.api.cms.ElementResp;
import com.guohuai.component.util.DateUtil;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class MsgUtil {
	
	@Autowired
	private CmsApi cmsApi;
	
	
	public String getHotLine() {
		ElementResp rep = new ElementResp();
		try {
			rep =  this.cmsApi.getElement("hotline");
		} catch (Exception e) {
			log.error("hotline exception", e);
			return "hotline exception";
		}
		 
		if (null == rep || null == rep.getData()) {
			return "hotline exception";
		}
		if (0 != rep.getErrorCode()) {
			return "hotline excetpion";
		}
		return rep.getData().getContent();
	}
	
	public String assembleMsgParams(Object... obj) {
		
		StringBuilder sb = new StringBuilder("[");
		for (Object tmp : obj) {
			if (tmp instanceof String) {
				sb.append("\"").append((String)tmp).append("\"").append(",");
			} else if (tmp instanceof BigDecimal) {
				String formatStr = String.format("%.2f", ((BigDecimal)tmp).floatValue());
				sb.append("\"").append((formatStr).toString()).append("\"").append(",");;
			} else if (tmp instanceof java.sql.Timestamp) {
				sb.append("\"").append(DateUtil.format((java.sql.Timestamp)tmp, DateUtil.fullDatePattern)).append("\"").append(",");;
			}
				
		}
		if (sb.length() >= 2) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static List<String> disAssembleMsgParams(String params) {
		// Removed the "["
		List<String> result = new ArrayList<String>();
		if (params.length() < 5) {
			return null;
		}
		String origins = params.substring(2, params.length() - 2);
		String[] splits = origins.split(",");
		for (int i = 0; i < splits.length; i++) {
			String s = splits[i];
			if (s.length() < 5) {
				continue;
			}
			//Removed the "/""
			result.add(s.substring(2, s.length() - 2));
		}
		return result;
	}

	public <T> String assembleMsgParam(T req) {
		Field[] fields = req.getClass().getDeclaredFields();
		StringBuilder sb = new StringBuilder("[");
		
		for (Field tmp : fields) {
			try {
				Object obj = req.getClass().getMethod("get" + tmp.getName().substring(0, 1).toUpperCase() + tmp.getName().substring(1), new Class[] {}).invoke(req, new Object[] {});
				if (obj instanceof BigDecimal) {
					sb.append(((BigDecimal)obj).toString()).append(",");
				} else if (obj instanceof String) {
					sb.append(obj).append(",");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		if (sb.length() >= 2) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	
	
	public static void main(String[] args) {
		MsgUtil util = new MsgUtil();
//		RechargeSuccessMsgReq req = new RechargeSuccessMsgReq();
//		req.setOrderAmount(new BigDecimal(3333));
//		req.setPhone("18521095619");
		
//		BuySuccessMsgReq req = new BuySuccessMsgReq();
//		req.setProductName("FUCK");
		
		AbortiveMsgReq req = new AbortiveMsgReq();
		req.setHotLine("400-88-888");
		req.setProductName("产品名称");
		System.out.println(util.assembleMsgParam(req));
	}
}
