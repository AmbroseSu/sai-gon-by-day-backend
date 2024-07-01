package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_package")
public class Package {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  @Column(length = 20000)
  private String description;
  private Boolean status;
  private String shortDescription;
  private Long startTime;
  private Long endTime;

  @OneToMany(mappedBy = "packagee", cascade = CascadeType.ALL)
  private List<PackageInDay> packageInDays;

  @OneToMany(mappedBy = "packagee", cascade = CascadeType.ALL)
  private List<PackageInDestination> packageInDestinations;
}
