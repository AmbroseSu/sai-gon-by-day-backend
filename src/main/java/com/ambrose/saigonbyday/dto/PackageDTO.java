package com.ambrose.saigonbyday.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO {
  private Long id;

  private String name;
  private String description;
  private Boolean status;
  private String shortDescription;
  private Long startTime;
  private Long endTime;

  private List<Long> packageInDayIds;

  private List<Long> destinationIds;
}
