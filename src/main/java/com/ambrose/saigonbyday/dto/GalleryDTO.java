package com.ambrose.saigonbyday.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalleryDTO {
  private Long id;
  private String imageNo1;
  private String imageNo2;
  private String imageNo3;
  private Boolean status;
}
