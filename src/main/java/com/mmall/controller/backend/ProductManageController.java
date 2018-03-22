package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 后台管理商品controller
 *
 * @author
 * @create 2017-12-06 下午2:34
 **/

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    // 注入
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    // 注入上传文件的 service
    @Autowired
    private IFileService iFileService;

    /**
     * 保存或者更新商品
     *
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //     增加商品逻辑
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");

        }

    }


    /**
     * 上下架商品
     *
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session, Integer productId, Integer status) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            // 更新商品状态
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");
        }

    }

    /**
     * 获取商品详情
     *
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(HttpSession session, Integer productId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //  获取商品详情
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");
        }
    }

    /**
     * 后台商品列表动态分页
     *
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //   填充业务
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");
        }
    }


    /**
     * 后台商品搜索功能
     *
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //   填充业务
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");
        }
    }


    /**
     * 上传文件
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/upload.do")
    @ResponseBody
    public ServerResponse<Map> upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpSession session) {

        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录、请登录管理员");
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {

            // 判断上传的图片是否为空
            if (file.getSize() == 0 ) {
                return ServerResponse.createByErrorMessage("没有选择上传图片、图片为空、请重新选择图片上传");
            }

            //   填充上传文件 业务
            //         拿到路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            // 上传文件、获取 上传之后的文件名
            String targetFilename = iFileService.upload(file, path);
            // 拼接  文件访问的url绝对路径
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFilename;

            // 把 图片 文件名和 文件绝对路径 组装成map返回前端
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFilename);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);


        } else {
            // 不是管理员
            return ServerResponse.createByErrorMessage("不是管理员 无权限操作");
        }


    }


    /**
     * 富文本上传文件
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        // 判断管理员权限
        if (iUserService.chackAdminRole(user).isSuccess()) {
            // 判断上传的图片是否为空
            if (file.getSize() == 0 ) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传图片为空、请重新上传");
                return resultMap;
            }
            // 富文本上传对于返回值有自己的要求、我们使用的是simditor所以按照simditor的要求返回
            //   填充上传文件 业务
            //         拿到路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            // 上传文件、获取 上传之后的文件名
            String targetFilename = iFileService.upload(file, path);
            if (targetFilename.isEmpty()) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            // 拼接  文件访问的url绝对路径
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFilename;

            // 把 图片 文件名和 文件绝对路径 组装成map返回前端
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            // 修改response的header
            response.addHeader("Access-control-Allow-Headers", "X-File-Name");
            return resultMap;


        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }


    }


}
