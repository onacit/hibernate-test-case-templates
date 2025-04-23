package org.hibernate.bugs.hhh19381;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "h2_other")
public class H2Other {

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

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "some_id", nullable = false, insertable = true, updatable = false, unique = true)
    private Long someId;

    @OneToOne(optional = false)
    @JoinColumn(name = "some_id", nullable = false, insertable = false, updatable = false, unique = true)
    private H2Some some;
}
