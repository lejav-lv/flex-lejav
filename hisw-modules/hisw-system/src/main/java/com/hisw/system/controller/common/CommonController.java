package com.hisw.system.controller.common;

import cn.hutool.core.map.MapUtil;
import com.hisw.common.core.config.HiswBootConfig;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.file.FileUploadUtils;
import com.hisw.common.core.utils.file.FileUtils;
import com.hisw.system.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用请求处理
 *
 * @author lejav
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMETER = ",";

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public R<Map<String, String>> uploadFile(MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = HiswBootConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            Map<String, String> map = MapUtil.newHashMap(4);
            map.put("url", url);
            map.put("fileName", fileName);
            map.put("newFileName", FileUtils.getName(fileName));
            map.put("originalFilename", file.getOriginalFilename());
            return R.ok(map);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public R<Map<String, String>> uploadFiles(List<MultipartFile> files) {
        try {
            // 上传文件路径
            String filePath = HiswBootConfig.getUploadPath();
            List<String> urls = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            List<String> newFileNames = new ArrayList<>();
            List<String> originalFilenames = new ArrayList<>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            Map<String, String> map = MapUtil.newHashMap(4);
            map.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            map.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            map.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            map.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return R.ok(map);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

}
