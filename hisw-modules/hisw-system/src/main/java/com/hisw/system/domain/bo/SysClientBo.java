package com.hisw.system.domain.bo;

import com.hisw.system.domain.SysClient;
import com.hisw.common.core.validate.AddGroup;
import com.hisw.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.List;

/**
 * 授权管理业务对象 sys_client
 *
 * @author lejav
 */
@Data
@AutoMapper(target = SysClient.class, reverseConvertGenerate = false)
public class SysClientBo  implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端key
     */
    @NotBlank(message = "客户端key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String clientKey;

    /**
     * 客户端秘钥
     */
    @NotBlank(message = "客户端秘钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String clientSecret;

    /**
     * 授权类型
     */
    @NotNull(message = "授权类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private List<String> grantTypeList;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * token活跃超时时间
     */
    private Long activeTimeout;

    /**
     * token固定超时时间
     */
    private Long timeout;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /** 乐观锁 */
    private Integer version;

}
