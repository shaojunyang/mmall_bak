package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 查询商品列表
     * @return
     */
    List<Product> selectProductList();

    /**
     * 商品搜索功能
     * @param productName
     * @param productId
     * @return
     */
    List<Product>selectByNameAndProductId(@Param("productName") String productName, @Param("productId") Integer productId);

    /**
     * 前台 商品 搜索 和 搜索分类
     * @param productName
     * @param categoryIdList
     * @return
     */
    List<Product> selectByNameAndCategoryIds(@Param("productName") String productName, @Param("categoryIdList") List<Integer> categoryIdList);
}