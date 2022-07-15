package com.yoke.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.seckill.pojo.Goods;
import com.yoke.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 功能描述: 获取商品列表
     *
     * @param:
     * @return:
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 功能描述: 获取商品详情
     *
     * @param: goodsId 商品ID
     * @return:
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
