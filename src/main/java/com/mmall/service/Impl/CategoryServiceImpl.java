package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 分类管理实现类
 *
 * @author
 * @create 2017-12-06 上午10:44
 **/
@Service
public class CategoryServiceImpl implements ICategoryService {

    // 日志
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    // 注入 分类管理 mapper
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     *
     * @param categoryName
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        // 校验参数
        if (parentId == null || categoryName.isEmpty()) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        // 创建对象
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//分类可用的

        // 添加分类
        int insertCount = categoryMapper.insert(category);
        if (insertCount > 0) {
            // 添加成功
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }


    /**
     * //     根据categoryd获取当前categoryid节点下的信息、平级不递归
     *
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId) {
        // 获取 子节点信息
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(parentId);
        if (categoryList.isEmpty()) {
            logger.info("未找到当前分类的子分类");
            //return ServerResponse.createBySuccessMessage("");
        }
        // 返回 查询结果
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 更新分类名称
     *
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        // 判断参数
        if (categoryId == null || categoryName.isEmpty()) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");

        }

        // 创建 对象
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        // 更新
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名称失败");

    }


    /**
     * 递归查询 本节点的id和 子孙的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse <List<Integer>>getCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);

        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归算法、算出子节点
     *
     * @param categoryId
     * @return
     */
    private Set<Category> findChildrenCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        //    查找子节点、递归算法一定要有退出的条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildrenCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
