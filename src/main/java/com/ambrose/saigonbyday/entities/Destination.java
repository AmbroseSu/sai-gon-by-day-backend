package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_destination")
public class Destination {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String address;
  private String description;
  private String shortDescription;
  private Boolean status;
  private String activities;

  @ManyToOne
  @JoinColumn(name = "cityId")
  private City city;

  @OneToOne
  @JoinColumn(name = "galleryId")
  private Gallery gallery;

  @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
  private List<PackageInDestination> packageInDestinations;

  @OneToMany(mappedBy = "destination",cascade = CascadeType.ALL)
  private List<Servicee> servicees;
}
