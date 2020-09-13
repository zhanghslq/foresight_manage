package com.zhs.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.zhs.entity.ConcatRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 联系记录表
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ConcatRecordVO对象", description="联系记录")
public class ConcatRecordVO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "联络人(主动联系别人)")
    private Long operatorId;

    @ApiModelProperty("联系人姓名（主动操作的人）")
    private String operatorName;

    @ApiModelProperty(value = "被联系人编号")
    private Long concatPersonId;

    @ApiModelProperty(value = "被联系人姓名")
    private Long concatPersonName;

    @ApiModelProperty(value = "单位(关联组织）")
    private Long companyId;

    @ApiModelProperty(value = "被联系人单位")
    private String companyName;

    @ApiModelProperty(value = "职务")
    private String job;

    @ApiModelProperty(value = "联系类型")
    private String concatType;

    @ApiModelProperty(value = "联系次数")
    private Integer concatCount;


    public ConcatRecordVO(ConcatRecord concatRecord){
        this.id = concatRecord.getId();
        this.operatorId = concatRecord.getOperatorId();
        this.concatPersonId = concatRecord.getConcatPersonId();
        this.companyId = concatRecord.getCompanyId();
        this.job=concatRecord.getJob();
        this.concatType=concatRecord.getConcatType();
        this.concatCount = concatRecord.getConcatCount();
    }

}
