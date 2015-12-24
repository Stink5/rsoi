package stink5.oauth2.lab2.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stink5.oauth2.lab2.model.api.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model findByName(String name);

    @Query("select m from Brand b join b.models m where b.name = :brand and m.name = :model")
    Model findByBrandAndName(
        @Param("brand") String brandName,
        @Param("model") String modelName
    );

}
