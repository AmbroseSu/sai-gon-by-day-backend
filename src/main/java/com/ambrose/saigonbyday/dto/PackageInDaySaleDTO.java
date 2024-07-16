package com.ambrose.saigonbyday.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageInDaySaleDTO {
  private Long id;
  private Date date;
  private Float price;
  private Boolean status;
  private String image;
  private String code;
  private String city;
  private List<String> serviceNames;
  private int numberAttendance;
  private Long packageId;
  private String packageName;
  private String packageDescription;
  private String packageShortDescription;
  private Long packageStartTime;
  private Long packageEndTime;
  private List<String> galleryUrls;
}
