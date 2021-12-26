package main.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.persistence.IDs.IDmeetUp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(IDmeetUp.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="meetup")
public class MeetUp {

    @Id
    private Integer id;
    @Id
    private Integer iduser;


}