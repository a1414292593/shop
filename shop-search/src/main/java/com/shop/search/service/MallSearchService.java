package com.shop.search.service;

import com.shop.search.vo.SearchParam;
import com.shop.search.vo.SearchResult;

import java.io.IOException;

public interface MallSearchService {
    /**
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果，里面包含页面所需要的所有信息
     */
    SearchResult search(SearchParam param) throws IOException;
}
