package com.notes.front.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;
import com.notes.front.domain.user.NotesUser;
import com.notes.front.mapper.user.NotesUserMapper;
import com.notes.front.service.user.INotesUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ldj
 * @since 2025-04-16
 */
@Service
public class NotesUserServiceImpl extends ServiceImpl<NotesUserMapper, NotesUser> implements INotesUserService {

    @Override
    public boolean save(NotesUser entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<NotesUser> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<NotesUser> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    public boolean removeById(NotesUser entity) {
        return super.removeById(entity);
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    public boolean remove(Wrapper<NotesUser> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Override
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        return super.removeByIds(list, useFill);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        return super.removeBatchByIds(list);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, boolean useFill) {
        return super.removeBatchByIds(list, useFill);
    }

    @Override
    public boolean updateById(NotesUser entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean update(Wrapper<NotesUser> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    public boolean update(NotesUser entity, Wrapper<NotesUser> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    public boolean updateBatchById(Collection<NotesUser> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    public NotesUser getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<NotesUser> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    public List<NotesUser> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    public NotesUser getOne(Wrapper<NotesUser> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public long count(Wrapper<NotesUser> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public List<NotesUser> list(Wrapper<NotesUser> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public List<NotesUser> list() {
        return super.list();
    }

    @Override
    public <E extends IPage<NotesUser>> E page(E page, Wrapper<NotesUser> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    @Override
    public <E extends IPage<NotesUser>> E page(E page) {
        return super.page(page);
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<NotesUser> queryWrapper) {
        return super.listMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        return super.listMaps();
    }

    @Override
    public List<Object> listObjs() {
        return super.listObjs();
    }

    @Override
    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return super.listObjs(mapper);
    }

    @Override
    public List<Object> listObjs(Wrapper<NotesUser> queryWrapper) {
        return super.listObjs(queryWrapper);
    }

    @Override
    public <V> List<V> listObjs(Wrapper<NotesUser> queryWrapper, Function<? super Object, V> mapper) {
        return super.listObjs(queryWrapper, mapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<NotesUser> queryWrapper) {
        return super.pageMaps(page, queryWrapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
        return super.pageMaps(page);
    }

    @Override
    public QueryChainWrapper<NotesUser> query() {
        return super.query();
    }

    @Override
    public LambdaQueryChainWrapper<NotesUser> lambdaQuery() {
        return super.lambdaQuery();
    }

    @Override
    public LambdaQueryChainWrapper<NotesUser> lambdaQuery(NotesUser entity) {
        return super.lambdaQuery(entity);
    }

    @Override
    public KtQueryChainWrapper<NotesUser> ktQuery() {
        return super.ktQuery();
    }

    @Override
    public KtUpdateChainWrapper<NotesUser> ktUpdate() {
        return super.ktUpdate();
    }

    @Override
    public UpdateChainWrapper<NotesUser> update() {
        return super.update();
    }

    @Override
    public LambdaUpdateChainWrapper<NotesUser> lambdaUpdate() {
        return super.lambdaUpdate();
    }

    @Override
    public boolean saveOrUpdate(NotesUser entity, Wrapper<NotesUser> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }
}
