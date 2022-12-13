package lab3.hibernate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "i_otime")
public class IOTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id", nullable = false, insertable = false, updatable = false)
    private int timeId;

    @Column(name = "pass_id", insertable = false, updatable = false)
    private int passId;

    @Column(name = "time_in")
    private Timestamp timeIn;

    @Column(name = "time_out")
    private Timestamp timeOut;

    @ManyToOne
    @JoinColumn(name = "pass_id", referencedColumnName = "pass_id")
    private Pass pass;
}
