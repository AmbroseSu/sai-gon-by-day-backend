package com.ambrose.saigonbyday.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {
  private Long id;
  private String name;
  private String description;
  private Long startTime;
  private Long endTime;
  private Float price;
  private String category;
  private Boolean status;
  private String shortDescription;
  private Long userId;
  private Long destinationId;
  private List<Long> packageInDestinationIds;
}
