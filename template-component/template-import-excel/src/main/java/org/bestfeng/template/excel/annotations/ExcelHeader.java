package org.bestfeng.template.excel.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 字段是否可导入、导出、表头排序
 * @author: bestfeng
 * @see:
 * @since: 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ExcelHeader {

    /**
     * 是否可导入
     * @return
     */
    boolean enableImport() default true;

    /**
     * 是否可导出
     * @return
     */
    boolean enableExport() default true;

    /**
     * 表头排序
     * @return
     */
    int sortNumber() default 0;

}
