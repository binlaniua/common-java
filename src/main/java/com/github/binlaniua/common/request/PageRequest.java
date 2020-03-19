package com.github.binlaniua.common.request;

import cn.tkk.common.jpa.query.value.SortItem;
import com.github.binlaniua.common.jpa.query.value.SortItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Tkk
 */
@Data
public class PageRequest {

    private int size;

    private int page;

    @ApiModelProperty(hidden = true)
    private SortItem[] sorts;

    @ApiModelProperty(hidden = true)
    public int getStart() {
        return this.getPageNum() * this.getPageSize();
    }

    @ApiModelProperty(hidden = true)
    public int getEnd() {
        return (this.getPageNum() + 1) * this.getPageSize();
    }

    @ApiModelProperty(hidden = true)
    public int getPageNum() {
        return this.page < 0 ? 0 : this.page;
    }

    @ApiModelProperty(hidden = true)
    public int getPageSize() {
        return this.size <= 0 || this.size > 100 ? 10 : this.size;
    }

    @ApiModelProperty(hidden = true)
    public org.springframework.data.domain.PageRequest getPage() {
        return org.springframework.data.domain.PageRequest.of(this.getPageNum(), this.getPageSize());
    }
}
