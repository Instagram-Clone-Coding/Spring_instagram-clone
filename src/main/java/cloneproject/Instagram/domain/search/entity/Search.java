package cloneproject.Instagram.domain.search.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "searches")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    @Column(name = "search_count")
    private Long count;

    @Column(insertable = false, updatable = false)
    private String dtype;

    protected Search(){
        this.count = 0L;
    }

    public void upCount(){
        this.count++;
    }

    @Transient
    public void setDtype() {
        this.dtype = getClass().getAnnotation(DiscriminatorValue.class).value();
    }

}
