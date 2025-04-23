package org.hibernate.bugs.hhh19381;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "grand_parent")
public class GrandParent {

    @Override
    public String toString() {
        return "GrandParent{" +
                "id=" + id +
                ", grandGrandParentId=" + grandGrandParentId +
                ", grandGrandParent=" + grandGrandParent +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrandGrandParentId() {
        return grandGrandParentId;
    }

    public void setGrandGrandParentId(Long grandGrandParentId) {
        this.grandGrandParentId = grandGrandParentId;
    }

    public GrandParent getGrandGrandParent() {
        return grandGrandParent;
    }

    public void setGrandGrandParent(GrandParent grandGrandParent) {
        this.grandGrandParent = grandGrandParent;
        setGrandGrandParentId(
                Optional.ofNullable(this.grandGrandParent).map(GrandParent::getId).orElse(null)
        );
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @JoinColumn(name = "parent_id", nullable = true, insertable = false, updatable = false)
    private Long grandGrandParentId;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true, insertable = false, updatable = false)
    private GrandParent grandGrandParent;
}
