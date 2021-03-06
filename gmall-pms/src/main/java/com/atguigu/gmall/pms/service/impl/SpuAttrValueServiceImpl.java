package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import org.springframework.util.CollectionUtils;


@Service("spuAttrValueService")
public class SpuAttrValueServiceImpl extends ServiceImpl<SpuAttrValueMapper, SpuAttrValueEntity> implements SpuAttrValueService {

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private AttrMapper attrMapper;

    public void bigSave2PmsSpuAttrValue(SpuVo spu, Long spuId) {
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        List<SpuAttrValueEntity> collect = null;
        if(!CollectionUtils.isEmpty(baseAttrs)){
            SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
            collect = baseAttrs.stream().map(baseAttr -> {
                BeanUtils.copyProperties(baseAttr, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
        }
        spuAttrValueService.saveBatch(collect);
    }

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SpuAttrValueEntity> querySearchAttrValuesByCidAndSpuId(Long cid, Long spuId) {
        //根据cid查询出检索类型的规格参数
        List<AttrEntity> attrEntities = attrMapper.selectList(new QueryWrapper<AttrEntity>()
                .eq("category_id", cid)
                .eq("search_type", 1));
        if(CollectionUtils.isEmpty(attrEntities)){
            return null;
        }
        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
        //根据spuId和attrIds查询出基本类型的检索规格参数值
        List<SpuAttrValueEntity> spuAttrValueEntities = this.list(new QueryWrapper<SpuAttrValueEntity>()
                .eq("spu_id", spuId)
                .in("attr_id", attrIds));
        return spuAttrValueEntities;
    }
}