package com.yoke.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoke.seckill.mapper.GoodsMapper;
import com.yoke.seckill.pojo.Goods;
import com.yoke.seckill.service.IGoodsService;
import com.yoke.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 功能描述: 获取商品列表
     *
     * @param:
     * @return:
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 功能描述: 获取商品详情
     *
     * @param: goodsId 商品ID
     * @return:
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
