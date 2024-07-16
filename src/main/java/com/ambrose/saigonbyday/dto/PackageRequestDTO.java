package com.ambrose.saigonbyday.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageRequestDTO {
  private PackageDTO packageDTO;
  private List<ServiceDestinationDTO> serviceDestinationDTOS;
}
