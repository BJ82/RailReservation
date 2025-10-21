package com.rail.app.railreservation.enquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="RouteMapping")

@Getter
@Setter
@NoArgsConstructor
public class RouteMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int routeMappingID;

    private int parentRoute;

    @ElementCollection
    private List<Integer> childRoutes = new ArrayList<>();


}
