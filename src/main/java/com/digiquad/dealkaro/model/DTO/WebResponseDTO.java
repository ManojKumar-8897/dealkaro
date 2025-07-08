package com.digiquad.dealkaro.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONObject;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponseDTO<T> {
    private Boolean flag;
    private String message;
    public Integer status;
    public T response;
    private Long totalRecords;
    public Object otherInfo;
}

