package main.persistence.repository;

import main.persistence.entity.Jwtoken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoJwtoken extends JpaRepository<Jwtoken, Integer> {

    public Jwtoken findByUserid(Integer userid);

}
