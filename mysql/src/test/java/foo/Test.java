package foo;

import com.jooreka.sql.SqlEntity;
import com.jooreka.sql.SqlSession;
import com.jooreka.sql.SqlTable;
import com.jooreka.sql.SqlConnectionSession;

import com.jooreka.sql.processor.Column;
import com.jooreka.sql.processor.Id;
import com.jooreka.sql.processor.Table;
import com.jooreka.sql.processor.Index;
import com.jooreka.sql.processor.EntityFactory;

import com.jooreka.jpa.mysql.MysqlSchemaGenerator;
import java.util.function.Function;

public abstract class Test implements SqlEntity {
  public static void main(String[] args)
    throws Exception
  {
  }

  @EntityFactory
  public interface Factory {
  }

  @Table(indexes = {@Index(columns = "shop_id")})
  public static abstract class ShopImageMap implements SqlEntity  {
    public static SqlTable<ShopImageMap> getTable(SqlSession session) {
      return Test$ShopImageMap$Impl.Table.getOrCreate(session);
    }

    @Id public abstract Long getId();
    @Column(columnDefinition = "LONG NOT NULL REFERENCES media(id)") public abstract Long getMediaId();
    @Column(columnDefinition = "LONG NOT NULL REFERENCES shop(id)") public abstract Long getShopId();

    public abstract ShopImageMap setMediaId(Long mediaId);
    public abstract ShopImageMap setShopId(Long shopId);
  }
  
}
