package com.guohuai.component.api.cms;


import com.guohuai.basic.component.ext.web.BaseResp;

import feign.Param;
import feign.RequestLine;

public interface CmsApi {
	/**
	 * 发送站内信
	 * @param userOid	用户oid
	 * @param mesTempCode	模板Code
	 * @param mesParam	模板参数，例如：["aaa","bbb","123"]
	 * @return
	 */
	@RequestLine("POST /cms/boot/mail/sendMail?userOid={userOid}&mesTempCode={mesTempCode}&mesParam={mesParam}")
	public BaseResp sendMail(@Param("userOid") String userOid, @Param("mesTempCode") String mesTempCode, @Param("mesParam") String mesParam);

	/**
	 * 发送推送
	 * @param userOid	用户oid
	 * @param mesTempCode	模板Code
	 * @param mesParam	模板参数，例如：["aaa","bbb","123"]
	 * @return
	 */
	@RequestLine("POST /cms/boot/push/sendPush?userOid={userOid}&mesTempCode={mesTempCode}&mesParam={mesParam}")
	public BaseResp sendPush(@Param("userOid") String userOid, @Param("mesTempCode") String mesTempCode, @Param("mesParam") String mesParam);

	/**
	 * 根据Code获取元素管理内容
	 * @param code 参考元素管理页面中的code
	 * @return
	 */
	@RequestLine("POST /cms/boot/element/find?code={code}")
	public ElementResp getElement(@Param("code") String code);
}
