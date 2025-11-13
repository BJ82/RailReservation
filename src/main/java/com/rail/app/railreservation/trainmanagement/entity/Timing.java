package com.rail.app.railreservation.trainmanagement.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Timing {

    private String station;

    private String arrvTime;

    private String deptTime;
}
