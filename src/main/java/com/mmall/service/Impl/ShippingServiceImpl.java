package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 收货地址
 *
 * @author yangshaojun
 * @create 2017-12-07 下午4:00
 **/
@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;


    /**
     * 增加收货地址z
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<Map> add(Integer userId, Shipping shipping) {
        // 赋值
        shipping.setUserId(userId);
        //增加地址
        int insertCount = shippingMapper.insert(shipping);
        if (insertCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");

    }

    /**
     * 根据 用户id和 收货地址id 删除收货地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<String> del(Integer userId, Integer shippingId) {
        // 删除地址
        int rowCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");

    }

    /**
     * //根据 收货地址id 和 用户id  查询 用户 收货地址
     *查看 某个地址详情
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        //根据 收货地址id 和 用户id  查询 用户 收货地址
        Shipping shipping = shippingMapper.selectByShippingUserId(userId, shippingId);
        if (null == shipping) {
            return ServerResponse.createByErrorMessage("无法查询到该地址");

        }
        return ServerResponse.createBySuccess(shipping);
    }

    /**
     * 根据 用户id和 收货地址id 更新收货地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        //增加地址
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");


    }


    /**
     * 查询收货地址列表  并分页
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        // 分页
        PageHelper.startPage(pageNum,pageSize);

        // sql  根据 用户查询所有的收货地址
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);

        // 构造 pageInfo
        PageInfo pageInfo=new PageInfo(shippingList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}
