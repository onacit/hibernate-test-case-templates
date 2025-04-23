package org.hibernate.bugs.hhh19381;

import jakarta.persistence.*;

@Entity
@Table(name = "h2_some")
public class H2Some {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public H2Some getParent() {
        return parent;
    }

    public void setParent(H2Some parent) {
        this.parent = parent;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_id", nullable = true, insertable = true, updatable = true)
    private H2Some parent;
}
