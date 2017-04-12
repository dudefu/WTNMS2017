package com.jhw.adm.client.actions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图的Action
 * <p>
 * <strong>例子:</strong>
 * <p>
 * @ViewAction(name="query" icon="query.png")
 * <p>
 * public void query()
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ViewAction {
    /**
     * 通过ActionMap查找时使用的键值，如果不指定则使用该方法名
     */
    String name() default "";
    /**
     * Action显示文本在资源文件的键值，如果不存在则使用该字符串
     */
    String text() default "";
    /**
     * Action图标在资源文件的键值
     */
    String icon() default "";
    /**
     * Action角色代码
     */
    int role() default 0;
    /**
     * Action功能描述
     */
    String desc() default ""; 
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Parameter {
	String value() default "";
    }
}
//@Secured("ROLE_ADMIN")