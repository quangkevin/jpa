
import com.jooreka.sql.SqlEntity;
import com.jooreka.sql.SqlSession;
import com.jooreka.sql.SqlTable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

public abstract class Test implements SqlEntity {
  public static void main(String[] args) {
  }

  private static class Foo {
  }

  @Table
  public interface Shop extends SqlEntity {
    public static SqlTable<Shop> getTable(SqlSession session) {
      return Test$Shop$Impl.Table.getOrCreate(session);
    }

    @Column @Id public abstract Long getId();
    @Column public abstract String getTitle();
    @Column public abstract Long getMainImageId();
    @Column public abstract Double getDeliveryFee();
    @Column public abstract Double getTaxPercentage();
    @Column public abstract Double getServiceFeePercentage();
    @Column public abstract Double getReviewScore();
    @Column public abstract Integer getNumberOfReviews();
    @Column public abstract Boolean getIsClubMembership();


    @Column public abstract Shop setTitle(String title);
    @Column public abstract Shop setMainImageId(Long mainImageId);
    @Column public abstract Shop setDeliveryFee(Double deliveryFee);
    @Column public abstract Shop setTaxPercentage(Double taxPercentage);
    @Column public abstract Shop setServiceFeePercentage(Double serviceFeePercentage);
    @Column public abstract Shop setReviewScore(Double reviewScore);
    @Column public abstract Shop setNumberOfReviews(Integer numberOfReviews);
    @Column public abstract Shop setIsClubMembership(Boolean isClubMembership);
  }
}
