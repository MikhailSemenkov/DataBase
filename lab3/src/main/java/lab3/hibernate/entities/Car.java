package lab3.hibernate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @Column(name = "car_id")
    private int carId;

    private String model;

    @ManyToMany
    @JoinTable(name = "cardriver",
               joinColumns = {@JoinColumn(name = "car_id")},
               inverseJoinColumns = {@JoinColumn(name = "driver_id")})
    private Collection<Driver> drivers = new LinkedList<>();

    @OneToMany(mappedBy = "car")
    private Collection<Pass> passes = new LinkedList<>();
}
