package com.jhw.adm.client.actions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ��ͼ��Action
 * <p>
 * <strong>����:</strong>
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
     * ͨ��ActionMap����ʱʹ�õļ�ֵ�������ָ����ʹ�ø÷�����
     */
    String name() default "";
    /**
     * Action��ʾ�ı�����Դ�ļ��ļ�ֵ�������������ʹ�ø��ַ���
     */
    String text() default "";
    /**
     * Actionͼ������Դ�ļ��ļ�ֵ
     */
    String icon() default "";
    /**
     * Action��ɫ����
     */
    int role() default 0;
    /**
     * Action��������
     */
    String desc() default ""; 
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Parameter {
	String value() default "";
    }
}
//@Secured("ROLE_ADMIN")