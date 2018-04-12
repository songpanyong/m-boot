package com.guohuai.mmp.platform.payment;

public class PayParam {
	
	
	public static enum Type {
		DEPOSIT("01"), WITHDRAW("02");
		String value;
		private Type(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}
	


	public static enum PayMethod {
		MOBILE("mobile"), PC("pc");
		String value;
		private PayMethod(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	public static enum PayType {
		INVEST("01"), REDEEM("02");
		String value;
		private PayType(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}

	public static enum OrderType {
		DEPOSIT("01"), WITHDRAW("02");
		String value;
		private OrderType(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 *	投资人账户:T1、发行人账户:T2、平台账户:T3 
	 */
	public enum UserType {
		INVESTOR("T1"), SPV("T2"), PLATFORM("T3");
		private String value;
		private UserType(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			
			return this.value;
		}
	}
	
	
	public static enum SystemSource {
		MIMOSA("mimosa");
		String value;
		private SystemSource(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	public static enum ReturnCode {
		RC0000("0000"), RC9999("9999");
		String value;
		private ReturnCode(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}
	

}
