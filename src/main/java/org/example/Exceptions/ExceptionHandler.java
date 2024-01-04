package org.example.Exceptions;

import org.example.DAOs.Category.CategoryDAOCriteria;

import java.util.Arrays;
import java.util.logging.Logger;

public class ExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAOCriteria.class.getName());
    private static final String MSG_EXCEPTION_DEF = "Error in class: ?class, method: ?method, Param: ?params, Msg: ?msg, Exception: ?exception";
    public static final String SEVERE = "severe";
    public static final String WARNING = "warning";
    public static final String INFO = "info";
    public static final String FINE = "fine";
    public static final String FINER = "finer";
    public static final String FINEST = "finest";


    public static void handleException(String className, Exception e, String method, String type, String... params) {
        if (type.equals(SEVERE)) handleSevereException(className, e, method, params);
        else if (type.equals(WARNING)) handleWarningException(className, e, method, params);
        else if (type.equals(INFO)) handleInfoException(className, e, method, params);
        else if (type.equals(FINE)) handleFineException(className, e, method, params);
        else if (type.equals(FINER)) handleFinerException(className, e, method, params);
        else if (type.equals(FINEST)) handleFinestException(className, e, method, params);
        else handleSevereException(className, e, method, params);
    }

    private static void handleFinestException(String className, Exception e, String method, String[] params) {
    }

    private static void handleFinerException(String className, Exception e, String method, String[] params) {
    }

    private static void handleFineException(String className, Exception e, String method, String[] params) {
    }

    private static void handleInfoException(String className, Exception e, String method, String[] params) {
    }

    private static void handleWarningException(String className, Exception e, String method, String[] params) {
        LOGGER.warning(MSG_EXCEPTION_DEF
               .replace("?class", className)
               .replace("?method", method)
               .replace("?params", Arrays.toString(params))
               .replace("?msg", e.getMessage())
               .replace("?exception", e.getClass().getName())
        );
    }

    private static void handleSevereException(String className, Exception e, String method, String... params) {
        LOGGER.severe(MSG_EXCEPTION_DEF
                .replace("?class", className)
                .replace("?method", method)
                .replace("?params", Arrays.toString(params))
                .replace("?msg", e.getMessage())
                .replace("?exception", e.getClass().getName())
        );
    }

}
