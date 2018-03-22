package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台分类管理 controller
 *
 * @author
 * @create 2017-12-06 上午10:24
 **/
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    // 注入service
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加 分类
     *
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            //     处理增加分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }

    }

    /**
     * 更新分类名称
     *
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "/set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            //      更新 分类名称
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


//     根据categoryd获取当前categoryid节点下的信息、平级不递归

    /**
     * //     根据categoryd获取当前categoryid节点下的信息、平级不递归
     *
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            //查询子节点的分类id
            //返回
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


    /**
     * //     获取当前categoryid、并且递归获取子节点的categoryid
     *
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            //查询当前节点的分类id 和递归子节点的id
            return iCategoryService.getCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }
}
