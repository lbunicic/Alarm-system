package hr.tel.fer.server;

/**
 * Created by lbunicic on 23/03/2017.
 */
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


//Interface is automatically implemented by Spring!

@RepositoryRestResource(collectionResourceRel = "window", path = "window")
public interface WindowRepository extends JpaRepository<Window, String>  {
	List<Window> findById(@Param("id") String id);
	@Query(value = "SELECT COUNT (id) FROM WINDOW WHERE id LIKE ?1", nativeQuery = true)
	Long countById(@Param("id") String id);
    @Query(value = "SELECT * FROM WINDOW WHERE NAME IS NULL",nativeQuery=true)
    List<Window> findMac();
    @Query(value = "SELECT * FROM WINDOW WHERE NAME IS NOT NULL AND ROOM_NAME IS NOT NULL",nativeQuery=true)
    List<Window> findNotNull();
    @Query(value = "SELECT * FROM WINDOW WHERE state IS TRUE",nativeQuery=true)
    List<Window> findOpened();
}
