package zone.yby.lab.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisRecord {
    String key;
    String value;
    Integer ttl;

    @Override
    public String toString() {
        return "RedisRecord{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", ttl=" + ttl +
                '}';
    }
}
