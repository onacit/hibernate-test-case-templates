package org.hibernate.bugs.hhh19381;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Optional;

@Entity
@Table(name = "child")
public class Child {

    @Override
    public String toString() {
        return "Child{" +
                "id=" + id +
                ", grandParentId=" + grandParentId +
                ", grandParent=" + grandParent +
                ", parent=" + parent +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrandParentId() {
        return grandParentId;
    }

    public void setGrandParentId(Long grandParentId) {
        this.grandParentId = grandParentId;
    }

    public GrandParent getGrandParent() {
        return grandParent;
    }

    public void setGrandParent(GrandParent grandParent) {
        this.grandParent = grandParent;
        setGrandParentId(
                Optional.ofNullable(this.grandParent)
                        .map(GrandParent::getId)
                        .orElse(null)
        );
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
        setGrandParent(
                Optional.ofNullable(this.parent).map(Parent::getGrandParent).orElse(null)
        );
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "grand_parent_id", nullable = false, insertable = true, updatable = false, unique = true)
    private Long grandParentId;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grand_parent_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false, unique = true)
    private GrandParent grandParent;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grand_parent_id", referencedColumnName = "grand_parent_id", nullable = false, insertable = false, updatable = false, unique = true)
    private Parent parent;
}
