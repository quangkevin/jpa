
import com.jooreka.sql.SqlEntity;
import com.jooreka.sql.SqlSession;
import com.jooreka.sql.SqlTable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Index;

public abstract class Test implements SqlEntity {
  public static void main(String[] args) {
  }

  @Table(indexes = {@Index(columnList = "shop_id")})
  public static abstract class ShopImageMap implements SqlEntity  {
    public static SqlTable<ShopImageMap> getTable(SqlSession session) {
      return Test$ShopImageMap$Impl.Table.getOrCreate(session);
    }

    @Column @Id public abstract Long getId();
    @Column(columnDefinition = "LONG NOT NULL REFERENCES media(id)") public abstract Long getMediaId();
    @Column(columnDefinition = "LONG NOT NULL REFERENCES shop(id)") public abstract Long getShopId();

    @Column public abstract ShopImageMap setMediaId(Long mediaId);
    @Column public abstract ShopImageMap setShopId(Long shopId);
  }
  
}
