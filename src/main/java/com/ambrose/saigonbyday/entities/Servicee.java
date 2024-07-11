package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
public class Servicee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  @Column(length = 20000)
  private String description;
  private Long startTime;
  private Long endTime;
  private Float price;
  private String category;
  private Boolean status;
  private String shortDescription;
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @OneToMany(mappedBy = "servicee", cascade = CascadeType.ALL)
  private List<ServiceInPackage> serviceInPackage;

  @ManyToOne
  @JoinColumn(name = "destinationId")
  private Destination destination;
}
