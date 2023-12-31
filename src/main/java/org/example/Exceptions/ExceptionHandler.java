package org.example.Exceptions;

import org.example.DAOs.CategoryDAOCriteria;

import java.util.Arrays;
import java.util.logging.Logger;

public class ExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAOCriteria.class.getName());
    private static final String MSG_EXCEPTION_DEF = "Error in class: ?class, method: ?method, Param: ?params, Msg: ?msg, Exception: ?exception";

    public static void handleSevereException(String className, Exception e, String method, String... params) {
        LOGGER.severe(MSG_EXCEPTION_DEF
                .replace("?class", className)
                .replace("?method", method)
                .replace("?params", Arrays.toString(params))
                .replace("?msg", e.getMessage())
                .replace("?exception", e.getClass().getName())
        );
    }

}
