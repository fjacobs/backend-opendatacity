package com.dynacore.livemap.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/* Use determine future requests for Feature history streaming replay.
  - The Feature count can determine the request(n) size for every pub_date.
  - The OffsetDateTime between multiple pub_dates to calculate the fast forward or rewind speed
    and the time interval between every request(n).  (for correct rewind/ffw, since gap between pub dates can vary)
*/
@NoArgsConstructor
@Data
public class PubDateSizeResponse {
  Integer count;
  OffsetDateTime pubDate;
}
