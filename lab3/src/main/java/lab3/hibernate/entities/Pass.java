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
public class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pass_id")
    private int passId;

    @Column(name = "car_id", insertable = false, updatable = false)
    private int carId;

    @Column(name = "place_number")
    private int placeNumber;

    @Column(name = "has_charger")
    private boolean hasCharger;

    private int tariff;

    @ManyToOne
    @JoinColumn(name = "car_id", referencedColumnName = "car_id")
    private Car car;

    @OneToMany(mappedBy = "pass")
    private Collection<IOTime> times = new LinkedList<>();
}
