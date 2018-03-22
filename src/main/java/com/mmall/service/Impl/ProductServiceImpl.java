package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.zxing.common.StringUtils;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品管理 service实现
 *
 * @author
 * @create 2017-12-06 下午2:47
 **/
@Service
public class ProductServiceImpl implements IProductService {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * * 保存或者更新商品
     *
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        // 判断 是否为空
        if (product != null) {
            //        判断子图是否是空的
            if (!Objects.equals(null,product.getSubImages())) {
                //如果 子图不是空的\\就取第一个子图赋值给主图
                String[] subImageArr = product.getSubImages().split(",");
                if (subImageArr.length > 0) {
                    //就取第一个子图赋值给主图
                    product.setMainImage(subImageArr[0]);
                }
            }
            //     如果是更新商品的话。如果更新 肯定带有id
            if (product.getId() != null) {
                int updateCount = productMapper.updateByPrimaryKey(product);
                if (updateCount > 0) {
                    return ServerResponse.createBySuccessMessage("更新商品成功");
                }
                return ServerResponse.createBySuccessMessage("更新商品失败");

            } else {
                //    就是 新增商品
                int insertCount = productMapper.insert(product);
                if (insertCount > 0) {
                    return ServerResponse.createBySuccessMessage("新增商品成功");
                }
                return ServerResponse.createBySuccessMessage("新增商品失败");
            }

        }
        return ServerResponse.createByErrorMessage("新增或者更新商品参数错误");
    }


    /**
     * 上下架商品
     *
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        //判断参数
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 创建对象
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        // 更新商品状态
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("修改商品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改商品销售状态失败");

    }


    /**
     * 获取商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        // 判断
        if (productId == null) {
            return ServerResponse.createByErrorMessage("商品参数错误");
        }
        //查询商品详情
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已经下架或者被删除");
        }
        // 返回 VO对象 // TODO: 2017/12/6
        // 前期 pojo-> vo(value Object)
        // 后期 pojo-> bo(business Object ) ->vo(view Object)

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 生成 ProductDetailVo 对象
     *
     * @param product
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        // 创建一个  ProductDetailVo 对象
        ProductDetailVo productDetailVo = new ProductDetailVo();
        // 设置值
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageost
        //parentCategoryId
        //createTime
        //updateTime
        // 给 图片 路径前缀赋值
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.mooc.com/"));

        // 赋值 父分类id
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            // 如果是空、这个 对象的 父分类id就是0、、  默认就是根节点
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());

        }

        // 赋值 更新和 创建时间
        // 把时间戳转换为字符串
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 使用PageHelper 管理  后台商品列表动态分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        // 1、start Page -  开始
        PageHelper.startPage(pageNum, pageSize);

        //2、填充自己的sql程序逻辑
        List<Product> productList = productMapper.selectProductList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //3、pageHelper-收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }


    /**
     * 生成  ProductListVo 对象
     *
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        // 赋值
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.mooc.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());


        return productListVo;
    }


    /**
     * 后台商品搜索功能
     *
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        // 1、start Page -  开始
        PageHelper.startPage(pageNum, pageSize);

        //2、填充自己的sql程序逻辑
        if (!Objects.equals(null,productName)) {
            //     使用模糊查询
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        // 调用dao 搜索
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //3、pageHelper-收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     * 前台详情页面 展示
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        // 判断
        if (productId == null) {
            return ServerResponse.createByErrorMessage("商品参数错误");
        }
        //查询商品详情
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已经下架或者被删除");
        }
        // 判断商品状态、是否下架
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已经下架或者被删除");
        }

        // 返回 VO对象 //
        // 前期 pojo-> vo(value Object)
        // 后期 pojo-> bo(business Object ) ->vo(view Object)

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);

    }


    /**
     * 前台 商品 根据 关键字和 分类 搜索搜索
     *
     * @param keyword    搜索关键字
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        // 效验 参数
        if (keyword.isEmpty() && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "搜索参数错误");
        }

        // 声明一个categoryId的 集合
        // 当传分类的时候、如果传的是高级分类、会调用递归算法把子孙的分类遍历出来放在集合中
        List<Integer> categoryIdList = new ArrayList();

        if (categoryId != null) {
            // 根据 分类id查询
            Category category = categoryMapper.selectByPrimaryKey(categoryId);

            // 如果查询分类为空 并且搜索关键字为空的话
            if (category == null && keyword.isEmpty()) {
                // 没有该分类、并且没有关键字、返回空的结果集
                PageHelper.startPage(pageNum, pageSize);
                List<ProductDetailVo> productDetailVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }

            // 当传分类的时候、如果传的是高级分类、会调用递归算法把子孙的分类遍历出来放在集合中
            categoryIdList = iCategoryService.getCategoryAndChildrenById(categoryId).getData();
        }
        //  判断搜索关键字是否是空的
        if (!keyword.isEmpty()) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //     排序处理
        PageHelper.startPage(pageNum, pageSize);
        // 如果排序参数是空的
        if (!orderBy.isEmpty()) {
            //  如果  orderBy参数 是 price_desc 或者 price_asc
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArr = orderBy.split("_");
                PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryIds(keyword.isEmpty() ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);
        //     构造vo对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //     分页
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}

