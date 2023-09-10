package com.shop.search.controller;

import com.shop.search.service.MallSearchService;
import com.shop.search.vo.SearchParam;
import com.shop.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Controller
@Slf4j
public class SearchController {

    @Resource
    private MallSearchService mallSearchService;

    /**
     * 自动将页面提交过来的所有请求参数封装成我们指定的对象
     * @param param
     * @return
     */
    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.setQueryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = null;
        try {
            result = mallSearchService.search(param);
        } catch (IOException e) {
            log.error("检索出错: {}", e.getMessage());
        }
        model.addAttribute("result", result);

        return "list";
    }

}
