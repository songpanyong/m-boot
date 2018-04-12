package com.guohuai.mmp.platform.accment;
import org.springframework.context.annotation.Condition;   
import org.springframework.context.annotation.ConditionContext;   
import org.springframework.core.type.AnnotatedTypeMetadata;   
   
public class AccmentIsolationCondition implements Condition{  
   
  @Override   
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {  
    return context.getEnvironment().getProperty("common.settlement.value").contains("Isolation");  
  }  
}  