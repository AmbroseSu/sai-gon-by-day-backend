package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_packageInDestination")
public class PackageInDestination {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long startTime;
  private Long endTime;
  private String transportation;
  @ManyToOne
  @JoinColumn(name = "packageeId")
  private Package packagee;

  @OneToMany(mappedBy = "packageInDestination",cascade = CascadeType.ALL)
  private List<ServiceInPackage> serviceInPackage;

  @ManyToOne
  @JoinColumn(name = "destinationId")
  private Destination destination;
}
