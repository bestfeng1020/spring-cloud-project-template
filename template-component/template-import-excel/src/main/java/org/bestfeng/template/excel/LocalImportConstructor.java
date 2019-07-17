package org.bestfeng.template.excel;

import org.bestfeng.template.excel.annotations.ExcelHeader;
import io.swagger.annotations.ApiModelProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hswebframework.expands.office.excel.ExcelIO;
import org.hswebframework.expands.office.excel.api.poi.callback.AdvancedValue;
import org.hswebframework.expands.office.excel.config.CustomCellStyle;
import org.hswebframework.expands.office.excel.config.Header;
import org.hswebframework.expands.office.excel.wrapper.HashMapWrapper;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.bean.FastBeanCopier;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.apache.poi.ss.usermodel.DateUtil.isADateFormat;

@Component("importConstructor")
@Slf4j(topic = "business.excel.import.constructor")
public class LocalImportConstructor implements ImportConstructor {

    @Autowired(required = false)
    RequestBuilder requestBuilder;

    @PostConstruct
    public void init() {
        if (null == requestBuilder) {
            requestBuilder = new SimpleRequestBuilder();
        }
    }

    @Override
    public <T> void doImport(String fileUrl, Class<T> type, Function<T, Boolean> consumerFunction) {
        try {
            InputStream in = getExcelInputStream(fileUrl);
            readExcel(in, type, consumerFunction);
        } catch (Exception e) {
            log.error("excel文件读取失败:{}", e);
            throw new BusinessException("网络错误，请稍后重试。");
        }
    }

    @Override
    public <T> boolean validateHeader(String fileUrl, Class<T> type) {
        InputStream in = null;
        try {
            in = getExcelInputStream(fileUrl);
        } catch (Exception e) {
            log.error("excel文件读取失败:{}", e);
            throw new BusinessException("网络错误，请稍后重试。");
        }
        try {
            //上传的表头title
            List<Object> upHeaders = getUploadExcelHeader(in);
            //模板表头
            List<Header> tagHeaders = createHeaders(type);
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            tagHeaders.forEach(tagHeader -> {
                if (upHeaders.contains(tagHeader.getTitle())) {
                    upHeaders.remove(tagHeader.getTitle());
                } else {
                    atomicBoolean.set(false);
                }
            });
            upHeaders.forEach(e -> {
                if (!"".equals(e.toString().trim())) {
                    atomicBoolean.set(false);
                }
            });
            return atomicBoolean.get();
        } catch (Exception e) {
            log.error("表头解析失败:{}", e);
            throw new BusinessException("表头解析失败,请上传正确的excel文件");
        }
    }

    @Override
    public <T> void writeImportExcelTemplate(OutputStream outputStream, Class<T> type) {
        try {
            ExcelIO.write(outputStream, createHeaders(type), createExplain(type));
        } catch (Exception e) {
            log.error("写出excel数据失败:{}", e);
            throw new BusinessException("写出excel数据失败");
        }
    }

    protected <T> void readExcel(InputStream in, Class<T> type, Function<T, Boolean> consumerFunction) throws Exception {
        //重新包装表头映射
        HashMapWrapper wrapper = new HashMapWrapper() {
            @Override
            @SneakyThrows
            public boolean wrapperDone(Map<String, Object> instance) {
                T value = type.newInstance();
                if (!consumerFunction.apply(FastBeanCopier.copy(instance, value))) {
                    shutdown();
                }
                return false;
            }
        };
        wrapper.setHeaderNameMapper(createMapper(type));
        ExcelIO.read(in, wrapper);
    }

    protected InputStream getExcelInputStream(String fileUrl) throws Exception {
        //创建一个临时文件
        File file = File.createTempFile(IDGenerator.MD5.generate(), ".xlsx");
        //下载上传的文件
        requestBuilder
                .http(fileUrl)
                .download()
                .write(file);
        InputStream in = new FileInputStream(file);
        return in;
    }

    /**
     * 获取上传excel的表头集合
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    protected List<Object> getUploadExcelHeader(InputStream inputStream) throws Exception {
        List<Object> headers = new LinkedList<>();
        Workbook wbs = WorkbookFactory.create(inputStream);
        //获取第一个sheet
        Sheet sheet = wbs.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        if (rowNum <= 0) return headers;
        Row row = sheet.getRow(0);
        if (row == null) return headers;
        int colNum = row.getPhysicalNumberOfCells();
        for (int j = 0; j < colNum; j++) {
            Cell cell = row.getCell(j);
            headers.add(cell2Object(cell));
        }
        return headers;
    }

    /**
     * 将单元格数据转为java对象
     *
     * @param cell 单元格数据
     * @return 对应的java对象
     */
    protected Object cell2Object(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                if (isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                String stringValue = String.valueOf(cell.getNumericCellValue());
                BigDecimal value = new BigDecimal(stringValue);
                if (stringValue.endsWith(".0") || stringValue.endsWith(".00")) {
                    return value.intValue();
                }
                return value;
            case STRING:
                return cell.getRichStringCellValue().getString();
            case FORMULA:
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellTypeEnum()) {
                    case BOOLEAN:
                        return cell.getBooleanCellValue();
                    case NUMERIC:
                        if (isCellDateFormatted(cell)) {
                            return cell.getDateCellValue();
                        }
                        value = new BigDecimal(cell.getNumericCellValue());
                        if (String.valueOf(value).endsWith(".0") || String.valueOf(value).endsWith(".00"))
                            return value.intValue();
                        return value;
                    case BLANK:
                        return "";
                    default:
                        return cellValue.getStringValue();
                }
            case BLANK:
                return "";
            default:
                return cell.getStringCellValue();
        }
    }

    public static boolean isCellDateFormatted(Cell cell) {
        if (cell == null) return false;
        boolean bDate = false;

        double d = cell.getNumericCellValue();
        if (DateUtil.isValidExcelDate(d)) {
            CellStyle style = cell.getCellStyle();
            if (style == null) return false;
            int i = style.getDataFormat();
            if (i == 58 || i == 31) return true;
            String f = style.getDataFormatString();
            f = f.replaceAll("[\"|\']", "").replaceAll("[年|月|日|时|分|秒|毫秒|微秒]", "");
            bDate = isADateFormat(i, f);
        }
        return bDate;
    }


    private <T> Map<String, String> createMapper(Class<T> type) {
        Map<String, String> mapper = new HashMap<>();
        ReflectionUtils.doWithFields(type, field -> {
            ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
            ExcelHeader header = field.getAnnotation(ExcelHeader.class);
            String name = property != null ? property.value() : field.getName();
            if (header == null || header.enableExport()) {
                mapper.put(name, field.getName());
            }
        });
        return mapper;
    }

    private <T> List<Header> createHeaders(Class<T> type) {
        List<Header> headers = new LinkedList<>();
        ReflectionUtils.doWithFields(type, field -> {
            ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
            ExcelHeader header = field.getAnnotation(ExcelHeader.class);
            String name = property != null ? property.value() : field.getName();
            if (header == null || header.enableExport()) {
                headers.add(new Header(name, field.getName()));
            }
        });
        return headers;
    }

    private <T> List<Object> createExplain(Class<T> type) {
        //获取实体类的字段，建立与表头说明值的映射
        List<Object> datas = new LinkedList<>();
        Map<String, Object> data = new HashMap<>();
        ReflectionUtils.doWithFields(type, field -> {
            ExcelHeader header = field.getAnnotation(ExcelHeader.class);
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            Length length = field.getAnnotation(Length.class);
            StringBuilder sb = new StringBuilder();
            if (header == null || header.enableExport()) {
                if (notBlank != null) {
                    sb.append("必填字段");
                }
                if (length != null) {
                    sb.append("、" + length.message());
                }
                AdvancedValue value = new AdvancedValue();
                value.setValue(sb.toString());
                //excel下拉选项设置
                //value.setOptions(Arrays.asList("1","2","3"));
                CustomCellStyle style = new CustomCellStyle();
                style.setFontColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                value.setStyle(style);
                data.put(field.getName(), value);
            }

        });
        datas.add(data);
        return datas;
    }
}
