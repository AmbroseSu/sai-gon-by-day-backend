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
@Table(name = "tbl_service")
public class Service {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  private Long startDate;
  private Long endTime;
  private Float price;
  private String category;
  private Boolean status;
  private String shortDescription;
  @ManyToOne
  @JoinColumn(name = "supplierId")
  private Supplier supplier;

  @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
  private List<ServiceInPackage> serviceInPackage;

  @ManyToOne
  @JoinColumn(name = "destinationId")
  private Destination destination;
}
