package com.ambrose.saigonbyday.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeatadataDTO  {
  private boolean hasNextPage;
  private boolean hasPrevPage;
  private int limit;
  private int total;
  private int page;
}
