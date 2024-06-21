package com.ambrose.saigonbyday.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDTO {
  private Long id;
  private String name;
  private Boolean status;
  private List<Long> destinationIds;
}
