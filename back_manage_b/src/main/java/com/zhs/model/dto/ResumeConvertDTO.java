package com.zhs.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 简历的解析转换
 * @author: zhs
 * @date: 2020/8/9 8:41
 */
@Data
public class ResumeConvertDTO {

    /**
     * name : 林武
     * sex : 男
     * nation : 汉族
     * parties : 中国共产党
     * level :
     * company : 山西省委、山西省政府
     * position : 军民融合办主任、副书记、省长、党组书记
     * positionDate : 2020-01
     * birthday : 1962-2
     * age : 58
     * birthplace : 福建/闽侯
     * workStatus : 在职
     * politicsCompany :
     * politicsPosition :
     * photo :
     */

    private String name;
    private String sex;
    private String nation;
    private String parties;
    private String level;
    private String company;
    /**
     * 这里是当前职务，这里也是多个
     */
    private String birthplaceId;
    private String position;
    private String positionDate;
    private String birthday;
    private Integer age;
    private String birthplace;
    private String workStatus;
    /**
     * 政治身份职务变成多个
     */
    private String politics;
    private String politicsCompany;
    private String politicsPosition;
    private String photo;

}
