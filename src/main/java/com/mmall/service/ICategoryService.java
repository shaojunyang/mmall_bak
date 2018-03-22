package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * 分类管理接口
 *
 * @author
 * @create 2017-12-06 上午10:44
 **/

public interface ICategoryService {
    /**
     * 添加分类
     *
     * @param categoryName
     * @param parentId
     * @return
     */
    ServerResponse addCategory(String categoryName, Integer parentId);

    /**
     * 更新分类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    /**
     * //     根据categoryd获取当前categoryid节点下的信息、平级不递归

     * @param parentId
     * @return
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);

    /**
     * 获取当前categoryid、并且递归获取子节点的categoryid
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);
}
