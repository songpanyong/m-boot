package com.guohuai.mmp.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;

public class Test {
	public static void main(String[] args) throws ParseException, Exception {
		
		OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream("c:/tmp/seq.txt"));
		Calendar cal = Calendar.getInstance();
		for (int i = 1; i < 888; i++) {
			int year = cal.get(Calendar.YEAR);
			String month = addZero(cal.get(Calendar.MONTH) + 1);
			String day = addZero(cal.get(Calendar.DAY_OF_MONTH));
			String date = "" + year + month + day;
			
			String str = "INSERT INTO `sequence` (`name`, `current_value`, `increment`) VALUES ('" + date + "', 88" + date + "00000000, 1);";
			os.write(str);os.write(System.getProperty("line.separator"));
			cal.add(Calendar.DATE, 1);
		}
		
		os.flush();os.close();
		
	}

	private static String addZero(int i) {
		if (i < 10) {
			return "0" + i;
		} else {
			return "" + i;
		}
	}
}
