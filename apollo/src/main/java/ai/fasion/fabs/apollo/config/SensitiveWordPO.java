package ai.fasion.fabs.apollo.config;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


public class SensitiveWordPO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "关键字")
    private String word;

    @ApiModelProperty(value = "类别")
    private Integer category;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SensitiveWordPO{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", category=" + category +
                ", createdTime=" + createdTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
