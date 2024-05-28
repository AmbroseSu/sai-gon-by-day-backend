package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.EnableMBeanExport;

@Data
@Entity
@Table(name = "tbl_supplier")
public class Supplier {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String address;
  private String phone;
  private String email;
  @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
  private List<Service> services;
}
