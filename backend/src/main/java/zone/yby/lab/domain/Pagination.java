package zone.yby.lab.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pagination {
    Integer page;
    Integer size;
}
