package com.musicApp.backend.map.TicketMaster.Sales;

import java.util.List;

public class Sales {

  private PublicSale publicSale;
  private List<PreSales> preSales;

  public List<PreSales> getPreSales() {
    return preSales;
  }

  public PublicSale getPublicSale() {
    return publicSale;
  }

}

