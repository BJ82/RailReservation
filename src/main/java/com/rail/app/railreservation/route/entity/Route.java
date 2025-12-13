package com.rail.app.railreservation.route.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Route")

@Getter
@Setter
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int routeID;

    @ElementCollection
    @CollectionTable(name = "stations" , joinColumns = @JoinColumn(name = "stn_id"))
    @Column(name = "stn_name")
    private List<String> stations = new ArrayList<>();

    //private String src;

    //private String destn;

}
