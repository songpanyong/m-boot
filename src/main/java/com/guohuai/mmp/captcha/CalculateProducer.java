package com.guohuai.mmp.captcha;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.google.code.kaptcha.text.TextProducer;

@Component
public class CalculateProducer implements TextProducer{
	
	public static String[] logicalsChinese = {
			"加", "减" , "乘"
	};
	
	@Override
	public String getText() {
		return null;
	}

	public String[] getCalculate() {
		Random rand = new Random();
		int number1 = rand.nextInt(10);
		
		int number2 = rand.nextInt(10);
		
		String logicalChinese = logicalsChinese[rand.nextInt(logicalsChinese.length)];
		
		String calculate = "";
		
		switch (logicalChinese) {
		case "加":
			calculate = String.valueOf(number1 + number2);
			break ;

		case "减":
			while (number1 - number2 < 0) {
				number1 = rand.nextInt(10);
				number2 = rand.nextInt(10);
			}
			calculate = String.valueOf(number1 - number2);
			break;
			
		case "乘":
			calculate = String.valueOf(number1 * number2);
			break;
			
		default:
			calculate = String.valueOf(number1 + number2);
			break;
		}
		
		return new String[] {calculate , number1 + logicalChinese + number2 + " = ?"};
	}
	
}
