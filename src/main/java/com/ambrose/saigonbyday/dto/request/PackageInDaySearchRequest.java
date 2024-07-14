package com.ambrose.saigonbyday.dto.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageInDaySearchRequest {
  private String packageName;
  private Float minPrice;
  private Float maxPrice;
  private Date date;
  private Integer minAttendance;
  private Integer maxAttendance;
  private String cityName;
  private int page = 0;
  private int limit = 10;
  private String sortDirection = "asc";
}
