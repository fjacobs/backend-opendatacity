package com.dynacore.livemap.guidancesign.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class GuidanceDisplay {
    String parentId;
    @Id
    String Id;
    String OutputDescription;
    String Description;
    String Type;
    String Output;
}
