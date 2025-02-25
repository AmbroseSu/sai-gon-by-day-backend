package com.ambrose.saigonbyday.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_serviceInPackage")
public class ServiceInPackage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "serviceeId")
  private Servicee servicee;

  @ManyToOne
  @JoinColumn(name = "packageInDestination")
  private PackageInDestination packageInDestination;
}
