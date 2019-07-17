package org.bestfeng.template;

import org.hswebframework.web.service.file.simple.LocalFileService;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bestfeng
 * @since 1.0
 */
public class FixInputStreamCloseLocalFileService extends LocalFileService {

    @Override
    public String saveStaticFile(InputStream fileStream, String fileName) throws IOException {
        try {
            return super.saveStaticFile(fileStream, fileName);
        } finally {
            try {
                fileStream.close();
            } catch (Exception ignore) {

            }
        }
    }
}
