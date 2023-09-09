package com.shop.common.constant;

public class ProductConstant {

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");

        private int code;

        private String message;

        AttrEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }


        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum StatusEnum {

        SPU_DOWN(2, "下架"),

        NEW_SPU(1, "上架"),
        SPU_UP(0, "新建");

        private int code;

        private String message;

        StatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }


        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
