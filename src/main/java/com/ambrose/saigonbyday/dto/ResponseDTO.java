package com.ambrose.saigonbyday.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDTO {
  private Object content;
  private String message;
  private List<String> details;
  private int statusCode;
  private MeatadataDTO meatadataDTO;
}
