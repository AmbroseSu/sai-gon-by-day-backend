package com.ambrose.saigonbyday.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestinationDTO {
  private Long id;
  private String name;
  private String address;
  private String description;
  private String shortDescription;
  private Boolean status;
  private String activities;
  private Long cityId;
  private Long galleryId;
  private List<Long> serviceIds;
  private List<Long> packageIds;
}
