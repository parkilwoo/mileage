package com.example.mileage.common;

/**
 * MessageSource Value To static value
 */
public class MessageSourceToStr {
    public static String RESULT_SUCCESS_CODE          = Utils.getMessage("result.success.code");
    public static String RESULT_SUCCESS_MSG           = Utils.getMessage("result.success.msg");
    public static String RESULT_FAIL_CODE             = Utils.getMessage("result.fail.code");
    public static String RESULT_FAIL_MSG              = Utils.getMessage("result.fail.msg");

    public static String DUPLICATE_REVIEW_ID          = Utils.getMessage("exception.msg.duplicate-review-id");
    public static String ONLY_ONE_USER_PLACE          = Utils.getMessage("exception.msg.only-one-user-place");
    public static String MOD_ONLY_WRITER              = Utils.getMessage("exception.msg.mod-only-writer");
    public static String NOT_REGISTERED_REVIEW_ID     = Utils.getMessage("exception.msg.not-registered-review-id");
    public static String DELETE_ONLY_WRITER           = Utils.getMessage("exception.msg.delete-only-writer");

}
