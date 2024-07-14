package com.ambrose.saigonbyday.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_packageInDay")
public class PackageInDay {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Date date;
  private Float price;
  private String code;
  private Boolean status;
  private String image;
  private int numberAttendance;
  @OneToMany(mappedBy = "packageInDay")
  private List<OrderDetails> orderDetails;

  @ManyToOne
  @JoinColumn(name = "packageeId")
  private Package packagee;
}
