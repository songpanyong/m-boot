//package com.guohuai;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
//import com.guohuai.mmp.serialtask.SerialTaskService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@RunWith(SpringRunner.class)
//// @SpringApplicationConfiguration(classes=ApplicationBootstrap.class)
//@SpringBootTest(classes = ApplicationBootstrap.class)
//@Slf4j
//public class SerialTaskTest {
//	@Autowired
//	SerialTaskRequireNewService serialTaskRequireNewService;
//	@Autowired
//	SerialTaskService serialTaskService;
//
//	@Test
//	public void test() throws InterruptedException {
//		System.out.println("AAAAAAAAAAAAAAAAAAaaaa");
//		for (int a = 0; a < 5; a++) {
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					int i = 0;
//					while (true) {
//						if (i++ > 1000) {
//							break;
//						}
//						
////						boolean result = serialTaskRequireNewService.beginTask();
////						log.info("beginTask,{},{}",i,result);
//						serialTaskService.executeTask();
//					}
//
//				}
//			}).start();
//		}
//		try {
//			Thread.sleep(1000*60*10);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
