package com.guohuai.mmp.investor.sonaccount.produceMessage;



import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.productLabel.ProductLabelDao;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

@Service
public class ProductMessageService {
	
	@Autowired
	private ProductDao productDao;
	
	public ProductMessageRep queryByProductOid(String productOid) {
		ProductMessageRep rep = new ProductMessageRep();
		Product product = this.productDao.findByOid(productOid);
		if(product!=null){
			rep.setName(product.getName());//产品名称
			rep.setFullName(product.getFullName());//产品全名
			rep.setType(product.getType().getOid());//产品类型
			if(rep.getType().equals("PRODUCTTYPE_01")){
				rep.setTypeDesc("定期产品");
			}else{
				rep.setTypeDesc("活期产品");
			}
			//预期年化收益率----将其转换为百分数
			BigDecimal expAror = product.getExpAror();
			if (product.getExpAror() != null && product.getExpAror().compareTo(new BigDecimal("0")) > 0) {
			String expArorStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(expAror)) + "%";
			rep.setExpAror(expArorStr);
			}
			rep.setRaiseStartDate(product.getRaiseStartDate());//募集开始日
			rep.setRaiseEndDate(product.getRaiseEndDate());//募集结束日
			rep.setRaisedTotalNumber(product.getRaisedTotalNumber());//募集总份额
			rep.setPurchaseConfirmDays(product.getPurchaseConfirmDays());//认购确认日
			rep.setRaisePeriodDays(product.getRaisePeriodDays());//募集期
			rep.setDurationPeriodDays(product.getDurationPeriodDays());//存续期
			rep.setInvestMin(product.getInvestMin());//单笔投资最低份额
			rep.setInvestAdditional(product.getInvestAdditional());//单笔投资递增份额
			rep.setNetUnitShare(product.getNetUnitShare());//单位份额净值
			rep.setInstruction(product.getInstruction());//产品说明
			rep.setInvestComment(product.getInvestComment());//投资标的
			rep.setRiskLevel(product.getRiskLevel());//风险等级
			rep.setCreateTime(product.getCreateTime());//产品的创建时间
			rep.setUpdateTime(product.getUpdateTime());//产品的更新时间
			rep.setIncomeDealType(product.getIncomeDealType());
			rep.setAdditionalRredeem(product.getAdditionalRredeem());
					
			return rep;	
		}else{
			throw new AMPException("该产品不存在");
		}
		
		
		
	}

	
}
