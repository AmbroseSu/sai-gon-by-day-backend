package com.ambrose.saigonbyday.dto.response;

import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.entities.Servicee;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageServiceResponse {
  private PackageDTO packageDTO;
  private List<Long> serviceId;
}
