package com.yoke.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yoke.seckill.pojo.Goods;
import com.yoke.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
public interface GoodsMapper extends BaseMapper<Goods> {

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
     * @param goodsId 商品ID
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
