package com.guohuai.mmp.investor.sonaccount.utils;
import org.apache.commons.lang.StringUtils;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import org.apache.commons.collections.MapUtils;
/**
 * 表单数据转换器
 * User: 表单数据型报文转换器
 * Date: 14-8-13
 * Time: 下午4:13
 * To change this template use File | Settings | File Templates.
 */
public class FormDateReportConvertor {

    /**
     * 将数据映射表拼接成表单数据POST样式的字符串  key1=value1&key2=value2
     * @param dataMap
     * @return
     */
    public static String postFormLinkReport(Map<String,String> dataMap){
        if(MapUtils.isEmpty(dataMap)) return "";

        StringBuilder reportBuilder = new StringBuilder();

        List<String> keyList = new ArrayList<String>(dataMap.keySet());
        Collections.sort(keyList);

        for(String key : keyList){
        	if(!StringUtils.isBlank(dataMap.get(key))){
        		 reportBuilder.append(key+"="+dataMap.get(key)+"&");
        	}
           
        }

        reportBuilder.deleteCharAt(reportBuilder.lastIndexOf("&"));

        return reportBuilder.toString();
    }

    /**
     * 将数据映射表拼接成表单数据POST样式的字符串  key1=value1&key2=value2
     * <p>并且对value进行URLEncoder编码
     * @param dataMap
     * @return
     */
    public static String postFormLinkReportWithURLEncode(Map<String,String> dataMap,String charset){
        if(MapUtils.isEmpty(dataMap)) return "";

        StringBuilder reportBuilder = new StringBuilder();

        List<String> keyList = new ArrayList<String>(dataMap.keySet());
        Collections.sort(keyList);

        for(String key : keyList){
            try{
                reportBuilder.append(key+"="+ URLEncoder.encode(dataMap.get(key),charset)+"&");
            }catch (Exception ex){
                //ignore to continue
                continue;
            }

        }

        reportBuilder.deleteCharAt(reportBuilder.lastIndexOf("&"));

        return reportBuilder.toString();
    }


    /**
     * 将数据映射表拼接成表单数据POST样式的字符串  key1="value1"&key2="value2"
     * @param dataMap
     * @return
     */
    public static String postBraceFormLinkReport(Map<String,String> dataMap){
        if(MapUtils.isEmpty(dataMap)) return "";

        StringBuilder reportBuilder = new StringBuilder();

        List<String> keyList = new ArrayList<String>(dataMap.keySet());
        Collections.sort(keyList);

        for(String key : keyList){
            reportBuilder.append(key+"=\""+dataMap.get(key)+"\"&");
        }

        reportBuilder.deleteCharAt(reportBuilder.lastIndexOf("&"));

        return reportBuilder.toString();
    }

    /**
     * 将数据映射表拼接成表单数据POST样式的字符串  key1="value1"&key2="value2"
     * <p>并且对value进行URLEncoder编码
     * @param dataMap
     * @return
     */
    public static String postBraceFormLinkReportWithURLEncode(Map<String,String> dataMap,String charset){
        if(MapUtils.isEmpty(dataMap)) return "";

        StringBuilder reportBuilder = new StringBuilder();

        List<String> keyList = new ArrayList<String>(dataMap.keySet());
        Collections.sort(keyList);

        for(String key : keyList){
            try{
                reportBuilder.append(key+"=\""+ URLEncoder.encode(dataMap.get(key),charset)+"\"&");
            }catch (Exception ex){
                //ignore to continue
                continue;
            }

        }

        reportBuilder.deleteCharAt(reportBuilder.lastIndexOf("&"));

        return reportBuilder.toString();
    }

    /**
     * 表单类型报文解析成数据映射表
     * @param reportContent
     * @param reportCharset --报文本身字符集
     * @param targetCharset --目标字符集
     * @return
     */
    public static Map<String,String> parseFormDataPatternReportWithDecode(String reportContent,String reportCharset,String targetCharset) {
        if(StringUtils.isBlank(reportContent)) return null;

        String[] domainArray = reportContent.split("&");

        Map<String,String> key_value_map = new HashMap<String, String>();
        for(String domain : domainArray){
            String[] kvArray = domain.split("=");

            if(kvArray.length == 2){
                try{
                    String decodeString = URLDecoder.decode(kvArray[1], reportCharset);
                    String lastInnerValue = new String(decodeString.getBytes(reportCharset), targetCharset);
                    key_value_map.put(kvArray[0], lastInnerValue);
                }catch (Exception ex){
                    // ignore
                }

            }
        }

        return key_value_map;
    }

	/**
	 * 表单类型报文解析成数据映射表
	 * 
	 * @param reportContent
	 * @return
	 */
	public static Map<String, String> parseFormDataPatternReport(String reportContent) {
		if (StringUtils.isBlank(reportContent))
			return null;

		String[] domainArray = reportContent.split("&");

		Map<String, String> key_value_map = new HashMap<String, String>();
		for (String domain : domainArray) {
			String[] kvArray = domain.split("=");

			if (kvArray.length == 2) {
				try {
					key_value_map.put(kvArray[0], kvArray[1]);
				} catch (Exception ex) {
					// ignore
				}

			}
		}

		return key_value_map;
	}
	
	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * 
	 * @param URL url地址
	 * @return url请求参数部分
	 */
	public static Map<String, String> urlRequest(String strUrlParam) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		if (strUrlParam == null) {
			return mapRequest;
		}
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");
			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}
}
