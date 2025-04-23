package org.hibernate.bugs.hhh19381;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Optional;

@Entity
@Table(name = "h2_other_history")
public class H2OtherHistory {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSomeId() {
        return someId;
    }

    public void setSomeId(Long someId) {
        this.someId = someId;
    }

    public H2Some getSome() {
        return some;
    }

    public void setSome(H2Some some) {
        this.some = some;
        setSomeId(
                Optional.ofNullable(this.some).map(H2Some::getId).orElse(null)
        );
    }

    public H2Other getOther() {
        return other;
    }

    public void setOther(H2Other other) {
        this.other = other;
        setSome(
                Optional.ofNullable(this.other).map(H2Other::getSome).orElse(null)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = false)
    @Column(name = "some_id", nullable = false, insertable = true, updatable = false)
    private Long someId;

    @OneToOne(optional = false)
    @JoinColumn(name = "some_id", nullable = false, insertable = false, updatable = false)
    private H2Some some;

    // -----------------------------------------------------------------------------------------------------------------
    @NotFound(action = NotFoundAction.IGNORE) // -> EAGER!!!
    @ManyToOne(optional = false,
            fetch = FetchType.LAZY // WON'T WORK!!!!
    )
    @JoinColumn(name = "some_id", referencedColumnName = "some_id", nullable = false, insertable = false,
            updatable = false)
    private H2Other other;
}
