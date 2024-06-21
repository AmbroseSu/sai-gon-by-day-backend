package com.ambrose.saigonbyday.repository;


import com.ambrose.saigonbyday.entities.Gallery;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, String> {

  Gallery findById(Long id);

//  @Query("SELECT d FROM Destination d WHERE d.gallery.id = :galleryId")
//  Destination findDestinationByGalleryId(@Param("galleryId") Long galleryId);

  Gallery findByStatusIsTrueAndId(Long id);

  List<Gallery> findAllBy(Pageable pageable);

  List<Gallery> findAllByStatusIsTrue(Pageable pageable);
  Long countAllByStatusIsTrue();

}
