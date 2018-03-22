package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * 商品 管理 service
 *
 * @author
 * @create 2017-12-06 下午2:46
 **/

public interface IProductService {
    /**
     * 保存或者更新商品
     * @param product
     * @return
     */
    ServerResponse saveOrUpdateProduct(Product product);

    /**
     * 上下架商品
     * @param productId
     * @param status
     * @return
     */
    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    /**
     * 获取商品详情
     * @param productId
     * @return
     */
    ServerResponse <ProductDetailVo>manageProductDetail(Integer productId);


    /**
     *后台商品类别动态分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    /**
     * 后台商品搜索功能
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse <PageInfo>searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize);

    /**
     * 前台商品 的详情 展示
     * @param productId
     */
    ServerResponse <ProductDetailVo> getProductDetail(Integer productId);

    /**
     * 前台商品 搜索
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy);
}
