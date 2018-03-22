package com.mmall.dao;

import com.mmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /**
     *根据categoryd获取当前categoryid节点下的信息、平级不递归
     * @param parentId
     * @return
     */
    List<Category> selectCategoryChildrenByParentId(Integer parentId);
}