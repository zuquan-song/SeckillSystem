package org.seckill.exception;

import lombok.Getter;
import org.seckill.result.CodeMsg;

/**
 * @author Zuquan Song
 *
 * @description GlobalException
 */
@Getter
public class GlobalException extends RuntimeException {

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
