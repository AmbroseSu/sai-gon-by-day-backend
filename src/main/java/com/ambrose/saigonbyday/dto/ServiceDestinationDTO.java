package com.ambrose.saigonbyday.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDestinationDTO {
  private Long id;
  private Long startTime;
  private Long endTime;
  private String transportation;
}
