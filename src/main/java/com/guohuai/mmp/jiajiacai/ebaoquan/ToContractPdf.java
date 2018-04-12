package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ToContractPdf {

	public static final String FONT = "./font/MSYH.TTF";
	
	public boolean htmlToPdf(String htmlFilePath, String pdfFilePath) {
		boolean rst = false;
//		FileUtil.mkdirs(pdfFilePath);
		
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
	        fontImp.register(FONT);
	        
	        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(htmlFilePath), null, Charset.forName("UTF-8"), fontImp);
	        rst = true;
	        log.info("转换成功 html={} pdf={}", htmlFilePath, pdfFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
		
		return rst;
	}

}
