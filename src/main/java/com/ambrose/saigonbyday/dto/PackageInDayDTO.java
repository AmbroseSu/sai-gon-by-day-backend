package com.ambrose.saigonbyday.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageInDayDTO {
  private Long id;
  private Date date;
  private Long price;
  private String image;
  private Boolean status;
  private int numberAttendance;
  private List<Long> orderIds;
  private Long packageId;
}
