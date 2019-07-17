package org.bestfeng.template.excel;

import lombok.*;
import org.hswebframework.web.validate.ValidateResults;

import java.util.List;

/**
 * excel导入错误原因详情
 * @author: bestfeng
 * @see:
 * @since: 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorInfo {

    private int indexColumn;

    private List<ValidateResults.Result> results;
}
