package org.bestfeng.template.excel.sql;

import lombok.SneakyThrows;
import org.hswebframework.web.commons.bean.Bean;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public class SqlHelper {

    public static <T extends Bean> String getInsertSql(T entity, String tableName) {
        StringBuilder sb = new StringBuilder();
        StringBuilder zw = new StringBuilder();
        sb.append("insert into " + tableName + "(id,");
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            field.setAccessible(true);
            if (!StringUtils.isEmpty(field.get(entity))) {
                sb.append(upperCharToUnderLine(field.getName()));
                zw.append("?");
                sb.append(",");
                zw.append(",");
            }
        });
        zw.append("?");
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values(");
        sb.append(zw + ")");
        return sb.toString();
    }

    @SneakyThrows
    public static <T> List<Object> getInsertValues(T entity) {
        List<Object> list = new ArrayList<>();
        list.add(IDGenerator.SNOW_FLAKE_STRING.generate());
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (!StringUtils.isEmpty(field.get(entity))) {
                list.add(value);
            }
        });
        return list;
    }

    /**
     * 大写字母转下划线
     *
     * @param param
     * @return
     */
    public static String upperCharToUnderLine(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
