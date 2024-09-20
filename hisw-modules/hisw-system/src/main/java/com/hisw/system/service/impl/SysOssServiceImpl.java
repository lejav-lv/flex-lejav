package com.hisw.system.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.CacheNames;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StreamUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.file.FileUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.oss.core.OssClient;
import com.hisw.common.oss.entity.UploadResult;
import com.hisw.common.oss.enumd.AccessPolicyType;
import com.hisw.common.oss.factory.OssFactory;
import com.hisw.system.domain.SysOss;
import com.hisw.system.domain.bo.SysOssBo;
import com.hisw.system.domain.vo.SysOssVo;
import com.hisw.system.mapper.SysOssMapper;
import com.hisw.system.service.ISysOssService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.hisw.system.domain.table.SysOssTableDef.SYS_OSS;

/**
 * 文件上传 服务层实现
 *
 * @author lejav
 */
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl extends BaseServiceImpl<SysOssMapper, SysOss> implements ISysOssService {

    private final SysOssMapper ossMapper;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_OSS);
    }

    private QueryWrapper buildQueryWrapper(SysOssBo bo) {
        return super.buildBaseQueryWrapper()
            .where(SYS_OSS.FILE_NAME.like(bo.getFileName()))
            .and(SYS_OSS.ORIGINAL_NAME.like(bo.getOriginalName()))
            .and(SYS_OSS.FILE_SUFFIX.eq(bo.getFileSuffix()))
            .and(SYS_OSS.URL.eq(bo.getUrl()))
            .and(SYS_OSS.CREATE_TIME.between(bo.getParams().get("beginCreateTime"), bo.getParams().get("endCreateTime")))
            .and(SYS_OSS.SERVICE.eq(bo.getService()))
            .orderBy(SYS_OSS.OSS_ID.asc());
    }

    /**
     * 查询OSS对象存储列表
     *
     * @param bo        OSS对象存储分页查询对象
     * @return 结果
     */
    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssBo bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        Page<SysOssVo> result = ossMapper.paginateWithRelationsAs(PageQuery.build(), queryWrapper, SysOssVo.class);
        List<SysOssVo> filterResult = StreamUtils.toList(result.getRecords(), this::matchingUrl);
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     *
     * @param ossIds 一组文件在数据库中的唯一标识集合
     * @return 包含 SysOssVo 对象的列表
     */
    @Override
    public List<SysOssVo> listSysOssByIds(Collection<Long> ossIds) {
        List<SysOssVo> list = new ArrayList<>();
        for (Long id : ossIds) {
            SysOssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo));
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo);
                }
            }
        }
        return list;
    }

    /**
     * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
     *
     * @param ossId 文件在数据库中的唯一标识
     * @return SysOssVo 对象，包含文件信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public SysOssVo getById(Long ossId) {
        QueryWrapper queryWrapper = query().where(SYS_OSS.OSS_ID.eq(ossId));
        return ossMapper.selectOneWithRelationsByQueryAs(queryWrapper, SysOssVo.class);
    }

    /**
     * 文件下载方法，支持一次性下载完整文件
     *
     * @param ossId    OSS对象ID
     * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
     */
    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {
        SysOssVo sysOss = SpringUtils.getAopProxy(this).getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = OssFactory.instance(sysOss.getService());
        try (InputStream inputStream = storage.getObjectContent(sysOss.getUrl())) {
            int available = inputStream.available();
            IoUtil.copy(inputStream, response.getOutputStream(), available);
            response.setContentLength(available);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     * @throws ServiceException 如果上传过程中发生异常，则抛出 ServiceException 异常
     */
    @Override
    public SysOssVo upload(MultipartFile file) {
        String originalfileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult);
    }

    private SysOssVo buildResultEntity(String originalfileName, String suffix, String configKey, UploadResult uploadResult) {
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(configKey);
        this.save(oss);
        SysOssVo sysOssVo = MapstructUtils.convert(oss, SysOssVo.class);
        return this.matchingUrl(sysOssVo);
    }

    /**
     * 删除OSS对象存储
     *
     * @param ids     OSS对象ID串
     * @param isValid 判断是否需要校验
     * @return 结果
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        List<SysOss> list = this.listByIds(ids);
        for (SysOss sysOss : list) {
            OssClient storage = OssFactory.instance(sysOss.getService());
            storage.delete(sysOss.getUrl());
        }
        return this.removeByIds(ids);
    }

    /**
     * 桶类型为 private 的URL 修改为临时URL时长为120s
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssVo matchingUrl(SysOssVo oss) {
        OssClient storage = OssFactory.instance(oss.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }
}
