package com.newemployee.common;

public enum IndexConfigTypeEnum {
    DEFAULT(0, "默认"),
    INDEX_SEARCH_HOTS(1, "首页搜索热点"),
    INDEX_SEARCH_DOWN_HOTS(2, "首页冷却的搜索热点"),
    INDEX_ITEMS_HOT(3, "首页商品热点"),
    INDEX_ITEMS_NEW(4, "首页新品"),
    INDEX_ITEMS_RECOMMOND(5, "首页商品推荐");

    private int type;

    private String msg;

    IndexConfigTypeEnum(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static IndexConfigTypeEnum getIndexConfigTypeEnumByType(int type) {
        for (IndexConfigTypeEnum indexConfigTypeEnum : IndexConfigTypeEnum.values()) {
            if (indexConfigTypeEnum.getType() == type) {
                return indexConfigTypeEnum;
            }
        }
        return DEFAULT;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
