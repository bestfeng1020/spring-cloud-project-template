package org.bestfeng.template.excel;

import java.io.OutputStream;
import java.util.function.Function;

public interface ImportConstructor {
    <T> void doImport(String fileUrl, Class<T> type, Function<T, Boolean> consumerFunction);
    <T> boolean validateHeader(String fileUrl, Class<T> type);
    <T> void  writeImportExcelTemplate(OutputStream outputStream, Class<T> type);
}
