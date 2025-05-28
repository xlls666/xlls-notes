package com.notes.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.notes.common.core.page.PageDomain;
import com.notes.common.core.page.TableSupport;
import com.notes.common.utils.sql.SqlUtil;

import java.util.List;


/**
 * 分页工具类
 * 
 * @author ruoyi
 */
public class PageUtils extends PageHelper
{
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }

    public static <T,V> Page<V> parseVOPage(Page<T> source, List<V> records) {
        Page<V> newPage = new Page<V>();
        newPage.setCurrent(source.getCurrent());
        newPage.setSize(source.getSize());
        newPage.setTotal(source.getTotal());
        newPage.setRecords(records);
        return newPage;
    }

    public static <V> Page<V> page(List<V> records, long current, long size, long total) {
        Page<V> newPage = new Page<V>();
        newPage.setCurrent(current);
        newPage.setSize(size);
        newPage.setTotal(total);
        newPage.setRecords(records);
        return newPage;
    }
}
